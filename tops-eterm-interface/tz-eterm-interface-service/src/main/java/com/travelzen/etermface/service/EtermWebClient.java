package com.travelzen.etermface.service;

import it.unimi.dsi.lang.MutableString;

import java.io.Closeable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.travelzen.etermface.common.config.cdxg.user.impl.RPCServiceSingleton;
import com.travelzen.etermface.common.config.cdxg.CdxgConstant;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.constant.PnrParserConstant;
import com.travelzen.etermface.service.entity.ParseConfBean;
import com.travelzen.framework.core.common.ReturnClass;
import com.travelzen.framework.core.common.ReturnCode;
import com.travelzen.framework.core.exception.BizException;
import com.travelzen.framework.core.util.TZUtil;

public class EtermWebClient implements Closeable {
	
    private static Logger logger = LoggerFactory.getLogger(EtermWebClient.class);


    private EtermWebClientConfig config = new EtermWebClientConfig();
    private RPCServiceSingleton RPCServiceSingleton;


    public EtermWebClient() {
        super();
        RPCServiceSingleton = RPCServiceSingleton.getInstance();
    }
    
    public EtermWebClient(String officeId) {
        super();
        config.setOfficeId(officeId);
        RPCServiceSingleton = RPCServiceSingleton.getInstance();
    }

    public EtermWebClient(ParseConfBean confBean) {
        super();
        config.setOfficeId(confBean.getOfficeId());
        RPCServiceSingleton = RPCServiceSingleton.getInstance();
    }

    /**
     * 延长超时时间
     *
     * @param millsec
     * @param reason
     */
    public void extendSessionExpireMillsec(int millsec, String reason) throws SessionExpireException {
        RPCServiceSingleton.extendSessionExpireMillsec(millsec, reason);
    }

    public void connect(String officeId, int sessionExpireMillsec) {
        RPCServiceSingleton.openSession(officeId, sessionExpireMillsec);
    }

    public void connect(String officeId) {
        connect(officeId, CdxgConstant.BASE_LOCK_KEEP_MILLSEC);
    }

    public void connect(int sessionExpireMillsec) {
        connect(config.getOfficeId(), sessionExpireMillsec);
    }

    public void connect() {
        connect(config.getOfficeId());
    }

    /**
     * @param pnr
     * @param onepage
     * @return
     * @throws SessionExpireException
     */
    public ReturnClass<String> getRT(String pnr, boolean onepage) throws SessionExpireException {
        ReturnClass<String> ret = executeCmdWithRetry("RT " + pnr, PnrParserConstant.DEFAULT_RETRY_CNT, onepage);

        if (!ret.isSuccess()) {
            return ret;
        } else {
            String rt = ret.getObject();

            if (StringUtils.isEmpty(rt)) {
                ret.setStatus(ReturnCode.ERROR);
                return ret;
            }
            logger.debug("rt:{}", rt);
            ret.setObject(rt);

            return ret;
        }
    }

    /**
     * 执行指令，最多执行默认的次数
     *
     * @param cmd     指令
     * @param onepage 是否只取第一页数据
     * @return
     * @throws SessionExpireException
     */
    public ReturnClass<String> executeCmdWithRetry(String cmd, boolean onepage) throws SessionExpireException {
        return executeCmdWithRetry(cmd, PnrParserConstant.DEFAULT_RETRY_CNT, onepage);
    }

    /**
     * @param cmd
     * @param retryCnt
     * @param onepage
     * @return
     * @throws SessionExpireException
     */
    public ReturnClass<String> executeCmdWithRetry(String cmd, int retryCnt, boolean onepage) throws SessionExpireException {
        ReturnClass<String> retCls = new ReturnClass<String>();
        RETRY:
        for (int i = 0; i < retryCnt; i++) {
            retCls = executeCmd(cmd, onepage);

            if (retCls.isSuccess()) {
                return retCls;
            } else {
                if (retCls.getObjects().length == 2) {
                    if (retCls.getObjects()[1] instanceof Boolean) {
                        Boolean needRetry = (Boolean) retCls.getObjects()[1];
                        if (!needRetry) {
                            break RETRY;
                        }
                    }
                }
            }
        }
        return retCls;
    }

    /**
     * @param cmd                         命令
     * @param retryCnt                    重试次数
     * @param extendSessionMillsecOnRetry 重试时延长的时间
     * @param onepage                     是否执行一页
     * @return
     * @throws SessionExpireException
     */
    public ReturnClass<String> executeCmdWithRetry(String cmd, int retryCnt, int extendSessionMillsecOnRetry, boolean onepage)
            throws SessionExpireException {
        ReturnClass<String> retCls = new ReturnClass<String>();
        for (int i = 0; i < retryCnt; i++) {
            retCls = executeCmd(cmd, onepage);
            if (StringUtils.equalsIgnoreCase(StringUtils.trim(retCls.getObject()), "NO PNR")) {
                continue;
            }
            if (retCls.isSuccess()) {
                return retCls;
            }
            this.extendSessionExpireMillsec(extendSessionMillsecOnRetry, "retry");
        }

        retCls.setStatus(ReturnCode.E_TIMEOUT);
        return retCls;
    }

    static Pattern twoDigitPoint = Pattern.compile("(\\d+)\\.");
    static Pattern DigitPointPattern = Pattern.compile("(\\d+)\\.");

    // 23.XN/IN/高歆媛 INF(SEP12)/P1 -24.SHA255
    private static int found2digitPointPattern(String str) {
        int ORIGIN_HYPHEN_OFFSET = 81;
        String line = StringUtils.substring(str, 0, ORIGIN_HYPHEN_OFFSET);
        Matcher m = twoDigitPoint.matcher(line);
        if (m.find() && m.find()) {
            return m.start();
        }
        return -1;
    }

    // use " 2digitPointPattern and a hyphen before 2nd pattern" as the tag
    public ReturnClass<String> executePn() throws SessionExpireException {
        ReturnClass<String> retCls = new ReturnClass<String>();

        String resultStr;
        try {
            try {
                resultStr = RPCServiceSingleton.getRawResult("pn");
            } catch (BizException e) {
                String errMsg = TZUtil.stringifyException(e);
                logger.error("getRawResult err:{}", errMsg);
                if (e.getRetCode() == ReturnCode.E_SESSION_EXPIRED) {
                    throw e;
                }
                retCls.setStatus(ReturnCode.ERROR);
                retCls.setObjects(new Object[]{errMsg});
                return retCls;
            }

            MutableString res = new MutableString(resultStr);

            if (resultStr.charAt(81) == '-') {
                res.setCharAt(81, '\n');
                resultStr = res.toString();
            } else {
                int second2digitPointOffset = found2digitPointPattern(resultStr);
                if (second2digitPointOffset > 0) {
                    if (resultStr.charAt(second2digitPointOffset - 1) == '-') {
                        res.setCharAt(second2digitPointOffset - 1, '\n');
                        resultStr = res.toString();
                    }
                }
            }

            // after strip, length of this line is 79, instead of 80
            resultStr = StringUtils.stripStart(resultStr, null);
            // logger.info(resultStr);

            retCls.setObject(resultStr);
            retCls.setStatus(ReturnCode.SUCCESS);
        } catch (Exception e) {
            retCls.setStatus(ReturnCode.ERROR);
        }
        return retCls;
    }

    // RTN向前翻页
    private ReturnClass<String> executePb() throws SessionExpireException {
        ReturnClass<String> retCls = new ReturnClass<String>();

        String resultStr;
        try {
            try {
                resultStr = RPCServiceSingleton.getRawResult("pb");
            } catch (BizException e) {
                String errMsg = TZUtil.stringifyException(e);
                logger.error("getRawResult err:{}", errMsg);
                if (e.getRetCode() == ReturnCode.E_SESSION_EXPIRED) {
                    throw e;
                }
                retCls.setStatus(ReturnCode.ERROR);
                retCls.setObjects(new Object[]{errMsg});
                return retCls;
            }

            MutableString res = new MutableString(resultStr);

            if (resultStr.charAt(81) == '-') {
                res.setCharAt(81, '\n');
                resultStr = res.toString();
            } else {
                int second2digitPointOffset = found2digitPointPattern(resultStr);
                if (second2digitPointOffset > 0) {
                    if (resultStr.charAt(second2digitPointOffset - 1) == '-') {
                        res.setCharAt(second2digitPointOffset - 1, '\n');
                        resultStr = res.toString();
                    }
                }
            }

            // after strip, length of this line is 79, instead of 80
            resultStr = StringUtils.stripStart(resultStr, null);
            // logger.info(resultStr);

            retCls.setObject(resultStr);
            retCls.setStatus(ReturnCode.SUCCESS);
        } catch (Exception e) {
            retCls.setStatus(ReturnCode.ERROR);
        }
        return retCls;
    }

    /**
     * if the PNR 6code is line-break, this function can't handle this scene
     *
     * @param cmd
     * @param res
     * @return
     */
    private Pair<Boolean, Integer> cutRtPnr(String cmd, MutableString res) {
        boolean hasCut = false;

        String upcase = cmd.toUpperCase().trim();
        int psgNum = 0;
        if (upcase.startsWith("RT") && !upcase.startsWith("RTN")) {
            String pnr = cmd.substring(2).trim();
            int ix = res.indexOf(pnr);
            if (ix >= 0) {
                res.setCharAt(ix + pnr.length(), '\n');
                hasCut = true;

                Matcher m = DigitPointPattern.matcher(res.substring(0, ix));

                while (m.find()) {
                    psgNum++;
                }
            }
        }
        return Pair.with(hasCut, psgNum);
    }

    public ReturnClass<String> rawExecuteCmd(String cmd) throws SessionExpireException {
        return rawExecuteCmd(cmd, true);
    }

    /**
     * @param cmd
     * @param onepage
     * @return
     * @throws SessionExpireException
     */
    public ReturnClass<String> rawExecuteCmd(String cmd, boolean onepage) throws SessionExpireException {
        ReturnClass<String> retCls = new ReturnClass<String>();

        logger.info("执行原始cmd:{}", cmd);

        String resultStr = "";
        try {
            try {
                resultStr = RPCServiceSingleton.getRawResult(cmd);
            } catch (BizException e) {
                logger.error("getRawResult err:{}", TZUtil.stringifyException(e));
                Boolean needRetry = true;
                if (e.getRetCode() == ReturnCode.E_DATA_VALIDATION_ERROR) {
                    needRetry = false;
                }
                retCls.setStatus(e.getRetCode(), new Object[]{e.getMessage(), needRetry});
                return retCls;
            }

            resultStr = resultStr.trim();

            if (resultStr.endsWith("+") && !onepage) {
                // remove the '+' at endofLine
                resultStr = resultStr.substring(0, resultStr.length() - 1) + "\n";
                String tmpResultStr = "";
                StringBuffer restStr = new StringBuffer();
                GET_REST:
                while (tmpResultStr != null) {
                    ReturnClass<String> ret = executePn();
                    if (ret.isSuccess()) {
                        tmpResultStr = ret.getObject();
                        // tmpResultStr = tmpResultStr.trim();
                        // not use trim, to keep the white space in the line
                        // begin
                        tmpResultStr = StringUtils.stripEnd(tmpResultStr, null);

                        boolean shouldBreak = true;
                        if (tmpResultStr.endsWith("+")) {
                            shouldBreak = false;
                            tmpResultStr = tmpResultStr.substring(0, tmpResultStr.length() - 1) + "\n";
                        }

                        restStr.append(tmpResultStr);

                        if (shouldBreak) {
                            break GET_REST;
                        }
                    } else {
                        logger.error(ret.getMessage());
                        break GET_REST;
                    }
                }
                resultStr += restStr.toString();
            }

            retCls.setObject(resultStr);
            retCls.setStatus(ReturnCode.SUCCESS);
            return retCls;
        } catch (BizException e) {
            logger.info("err:{}", e.getMessage());
            retCls.setStatus(ReturnCode.ERROR);
            retCls.setObjects(new Object[]{e.getRetMsg()});
            return retCls;
        } catch (Exception e) {
            // 不往上抛, 包括 expire异常
            String errMsg = TZUtil.stringifyException(e);
            logger.info("err:{}", errMsg);
            retCls.setStatus(ReturnCode.ERROR);
            retCls.setObjects(new Object[]{errMsg});
            return retCls;
        }
    }

    /**
     * note :
     * <p/>
     * use " 2digitPointPattern and a hyphen before 2nd pattern" as the tag
     *
     * @param cmd
     * @param onepage
     * @return
     * @throws SessionExpireException
     */
    public ReturnClass<String> executeCmd(String cmd, boolean onepage) throws SessionExpireException {
        ReturnClass<String> retCls = new ReturnClass<String>();

        logger.info("执行原始cmd:{}", cmd);

        String resultStr = "";
        try {
            try {
                resultStr = RPCServiceSingleton.getRawResult(cmd);
                MutableString res = new MutableString(resultStr);

                // Important !!
                //				resultStr.length() >= 80 && resultStr.charAt(81) == '-'
                //						scene1 :
                //				   1.焦铭皓CHD JS2V3V
                //				   2.  CZ6799 Y   FR17JAN  PVGKMG HK1   1420 1745          E T2--

                //				scene2 :(not accruate sample)
                // RT HE6BL8
                //					  MARRIED SEGMENT EXIST IN THE PNR                                               - 1.GUO/SHAOYA 2.LI/FENGFANG 3.SUN/MINGMING 4.XIAO/JIANPING 5.ZHANG/LE HE6LHV

                final int LINE_WIDTH = 80;

                if (resultStr.length() >= LINE_WIDTH && resultStr.endsWith("-")) {
                    Pair<Boolean, Integer> hasCut_psgNum = cutRtPnr(cmd, res);
                    //put a accurate   psgCnt recoginize algorithm here and use the psgCnt to judge the begin of   fltSet
                    //when  fltsegIdx is small than 80, than we don't need to put a "\n" on the 81th pos

                    if (hasCut_psgNum.getValue0()) {
                        Pattern fltsegIdx = Pattern.compile((hasCut_psgNum.getValue1() + 1) + ".", Pattern.LITERAL);
                        Matcher mfltsegIdx = fltsegIdx.matcher(res.toString());
                        if (mfltsegIdx.find()) {
                            if (mfltsegIdx.start() > LINE_WIDTH) {
                                res.setCharAt(81, '\n');
                            }
                        }
                    }

                    resultStr = res.toString();
                    logger.info("line end - when RT cmd:{} ", cmd);

                } else {
                    if (resultStr.length() >= 80) {
                        resultStr = res.toString();
                    }

                    //					   1.叶敏 JVNDG8
                    //					   2.  CZ3538 W   TU07JAN  SHACAN HK1   1445 1725          E T2--
                    //					   3.CAN/T CAN/T020-83995999/GUANGZHOU CHANGRONG AIR SERVICES LIMITED/WANGJUAN
                    //					       ABCDEFG
                    //					   4.TL/1245/07JAN/CAN525
                    //					   5.FC/A/SHA B-07JAN14 A-07JAN14 CZ CAN 1100.00WDK86 CNY1100.00END **DK0208130    6.SSR FOID CZ HK1 NI310113198608142606/P1
                    //					   7.SSR CKIN CZ HK1 VICODK0208130
                    //					   8.SSR ADTK 1E BY CAN05JAN14/1717 OR CXL CZ BOOKING
                    //					   9.OSI CZ CTCT13917515297
                    //					  10.RMK OT/A/0/90349/3-1CZ422702N1
                    //					  11.RMK CA/NV4K44                                                               +
                    int second2digitPointOffset = found2digitPointPattern(resultStr);

                    if (second2digitPointOffset > 0 && resultStr.charAt(second2digitPointOffset - 1) == '-') {
                        res.setCharAt(second2digitPointOffset - 1, '\n');
                        resultStr = res.toString();
                        logger.info("line end - when RT cmd:{} ", cmd);
                    }
                }

                // resultStr = resultStr.trim();
                // not use trim, to keep the white space in the line begin
                resultStr = StringUtils.stripEnd(resultStr, null);

            } catch (BizException e) {
                logger.error("getRawResult err:{}", TZUtil.stringifyException(e));

                Boolean needRetry = true;
                if (e.getRetCode() == ReturnCode.E_DATA_VALIDATION_ERROR) {
                    needRetry = false;
                }
                retCls.setStatus(e.getRetCode(), new Object[]{e.getMessage(), needRetry});
                return retCls;
            }

            if (resultStr.endsWith("+") && !onepage) {
                // remove the '+' at endofLine
                resultStr = resultStr.substring(0, resultStr.length() - 1) + "\n";

                String tmpResultStr = "";

                StringBuffer restStr = new StringBuffer();
                GET_REST:
                while (tmpResultStr != null) {
                    ReturnClass<String> ret = executePn();
                    if (ret.isSuccess()) {

                        tmpResultStr = ret.getObject();
                        // tmpResultStr = tmpResultStr.trim();
                        // not use trim, to keep the white space in the line
                        // begin
                        tmpResultStr = StringUtils.stripEnd(tmpResultStr, null);

                        boolean shouldBreak = true;
                        if (tmpResultStr.endsWith("+")) {
                            shouldBreak = false;
                            tmpResultStr = tmpResultStr.substring(0, tmpResultStr.length() - 1) + "\n";
                        }

                        restStr.append(tmpResultStr);

                        if (shouldBreak) {
                            break GET_REST;
                        }
                    } else {
                        logger.error(ret.getMessage());
                        break GET_REST;
                    }
                }
                resultStr += restStr.toString();
            }

            // RTN向前翻页
            if (resultStr.split("\\r")[0].contains("    -") && !onepage) {
                String tmpResultStr = "";

                StringBuffer restStr = new StringBuffer();
                // PB一次，然后PN
                ReturnClass<String> ret0 = executePb();
                if (ret0.isSuccess()) {

                    tmpResultStr = ret0.getObject();
                    // tmpResultStr = tmpResultStr.trim();
                    // not use trim, to keep the white space in the line
                    // begin
                    tmpResultStr = StringUtils.stripEnd(tmpResultStr, null);

                    if (tmpResultStr.endsWith("+")) {
                        tmpResultStr = tmpResultStr.substring(0, tmpResultStr.length() - 1) + "\n";
                    }

                    restStr.append(tmpResultStr);
                } else {
                    logger.error(ret0.getMessage());
                }
                GET_REST:
                while (tmpResultStr != null) {
                    ReturnClass<String> ret = executePn();
                    if (ret.isSuccess()) {

                        tmpResultStr = ret.getObject();
                        // tmpResultStr = tmpResultStr.trim();
                        // not use trim, to keep the white space in the line
                        // begin
                        tmpResultStr = StringUtils.stripEnd(tmpResultStr, null);

                        boolean shouldBreak = true;
                        if (tmpResultStr.endsWith("+")) {
                            shouldBreak = false;
                            tmpResultStr = tmpResultStr.substring(0, tmpResultStr.length() - 1) + "\n";
                        }

                        restStr.append(tmpResultStr);

                        if (shouldBreak) {
                            break GET_REST;
                        }
                    } else {
                        logger.error(ret.getMessage());
                        break GET_REST;
                    }
                }
                resultStr = restStr.toString();
            }

            retCls.setObject(resultStr);
            retCls.setStatus(ReturnCode.SUCCESS);
            return retCls;
        } catch (BizException e) {
            if (e.getRetCode() == ReturnCode.E_SESSION_EXPIRED) {
                throw e;
            } else {
                // 除了E_SESSION_EXPIRED之外， 都不往上抛
                logger.info("err:{}", e.getMessage());
                retCls.setStatus(ReturnCode.ERROR);
                retCls.setObjects(new Object[]{e.getRetMsg()});
            }
            return retCls;
        } catch (Exception e) {
            // 不往上抛, 包括 expire异常
            String errMsg = TZUtil.stringifyException(e);
            logger.info("err:{}", errMsg);
            retCls.setStatus(ReturnCode.ERROR);
            retCls.setObjects(new Object[]{errMsg});
            return retCls;
        }
    }

    /**
     * 直接调用成都新港封装好的接口
     *
     * @param cmd
     * @param cmdType
     * @return
     * @throws SessionExpireException
     */
    public ReturnClass<String> executeWrapCmd(String cmd, String cmdType) throws SessionExpireException {
        ReturnClass<String> retCls = new ReturnClass<String>();

        logger.info("执行包装好的cmd:{}", cmd);

        String resultStr = "";
        try {
            resultStr = RPCServiceSingleton.getWrapResult(cmd, cmdType);
            retCls.setObject(resultStr);
            retCls.setStatus(ReturnCode.SUCCESS);
            return retCls;
        } catch (BizException e) {
            if (e.getRetCode() == ReturnCode.E_SESSION_EXPIRED) {
                throw e;
            } else {
                // 除了E_SESSION_EXPIRED之外， 都不往上抛
                logger.info("err:{}", e.getMessage());
                retCls.setStatus(ReturnCode.ERROR);
                retCls.setObjects(new Object[]{e.getRetMsg()});
            }
            return retCls;
        } catch (Exception e) {
            // 不往上抛, 包括 expire异常
            String errMsg = TZUtil.stringifyException(e);
            logger.info("err:{}", errMsg);
            retCls.setStatus(ReturnCode.ERROR);
            retCls.setObjects(new Object[]{errMsg});
            return retCls;
        }
    }

    public String getXS_FSQ(int i) throws SessionExpireException {
        return executeCmdWithRetry("XS FSQ" + i, false).getObject();
    }

    public String getDFSQ(String code) throws SessionExpireException {
        return executeCmdWithRetry("DFSQ:" + code, false).getObject();
    }

    public String getRTN() throws SessionExpireException {
        return executeCmdWithRetry("RTN", false).getObject();
    }

    public String getRTN(boolean onepage) throws SessionExpireException {
        return executeCmdWithRetry("RTN", onepage).getObject();
    }

    final static String SESSION_LOCEKD = "SESSION CURRENTLY LOCKED";
    final static String MANUAL_PRICING = "UNABLE TO PROCESS - MANUAL PRICING REQUIRED";
    final static String NO_INTERLINE_AGREEMENT = "NO INTERLINE AGREEMENT FOR";

    public static boolean isNoFairQte(String qte) {
        return qte.contains(MANUAL_PRICING) || qte.contains(NO_INTERLINE_AGREEMENT);
    }

    static Set<String> slowCarrierSet = Sets.newCopyOnWriteArraySet();

    static {
        slowCarrierSet.addAll(Arrays.asList("DL CX EY EK QR LH".split("\\s+")));
    }

    /**
     * for slowCarrier,  every qte will consume DECAY_MILLSEC,    take  max_session_lock_sec as 60 and decay_sec as 10,   max 4 times decay is allowed
     * for normal carrier,   max 2 time decay is allowed,   normally, the number is 1.
     *
     * @param carrier
     * @param type
     * @return
     * @throws SessionExpireException
     */
    public String getQteWithResetSessionBeginTimestamp(List<String> carrier, String type) throws SessionExpireException {
        String ret = getQTE(carrier, type);
        return ret;
    }

    private ReturnClass<String> internalQte(String carrier, String type) throws SessionExpireException {
        this.extendSessionExpireMillsec(PnrParserConstant.QTE_NEED_MILLSEC, "getQTE");

        try {
            return executeCmdWithRetry("QTE:" + type + "/" + carrier, false);
        } catch (BizException be) {
            logger.error("internalQte err:{}", be);
            return new ReturnClass<>();
        }
    }

    public String getQTE(List<String> carrier, String type) throws SessionExpireException {
        String qte = "";

        int carrierIdx = 0;
        ReturnClass<String> qresult;

        NXT_CR:
        for (int carrierLoop = 0; carrierLoop < carrier.size(); carrierLoop++) {
            SESSION_LOCK:
            for (int sessionLockCnt = 0; sessionLockCnt < 3; sessionLockCnt++) {
                if (StringUtils.isEmpty(qte)) {
                    //1st try
                    qresult = internalQte(carrier.get(carrierIdx), type);
                    if (qresult.isSuccess()) {
                        qte = qresult.getObject();
                    } else {
                        continue NXT_CR;
                    }
                } else if (StringUtils.contains(qte, SESSION_LOCEKD)) {
                    qresult = internalQte(carrier.get(carrierIdx), "");
                    if (qresult.isSuccess()) {
                        qte = qresult.getObject();
                    } else {
                        continue NXT_CR;
                    }
                    continue SESSION_LOCK;
                } else if (StringUtils.contains(qte, NO_INTERLINE_AGREEMENT) || StringUtils.contains(qte, MANUAL_PRICING)) {
                    carrierIdx++;
                    if (carrierIdx >= carrier.size()) {
                        break NXT_CR;
                    }
                    qresult = internalQte(carrier.get(carrierIdx), "");
                    if (qresult.isSuccess()) {
                        qte = qresult.getObject();
                    } else {
                        continue NXT_CR;
                    }
                } else {
                    break NXT_CR;
                }
            }
        }

        return qte;
    }

    /**
     * 获取国内PNR报价
     *
     * @param code pat:后紧跟的指令
     * @return
     * @throws SessionExpireException
     */
    public ReturnClass<String> getPAT(String code) throws SessionExpireException {
        this.extendSessionExpireMillsec(PnrParserConstant.PAT_NEED_MILLSEC, "getPAT");
        ReturnClass<String> rtClass = executeCmdWithRetry("PAT:" + code, false);

        if (rtClass.isSuccess()) {
            String patStr = rtClass.getObject();
            logger.info("cmd{PAT:" + code + "}\nreturn-->\n" + patStr);
            StringUtils.replace(patStr, "\r", "\n");
            rtClass.setObject(patStr);
            return rtClass;
        } else {
            return rtClass;
        }
    }

    /**
     * 取消pnr 过程：1.先RT PNR号；2.调用此命令进行取消操作
     *
     * @return
     */
    public String cancelPNR() throws SessionExpireException {
        String rtStr = executeCmdWithRetry("XEPNR\\", false).getObject();
        return StringUtils.replace(rtStr, "\r", "\n");
    }

    /**
     * 取消pnr 过程：1.先RT PNR号；2.调用此命令进行取消操作
     *
     * @throws SessionExpireException
     */
    public String cancelPNR(String office, boolean ownOffice, boolean force) throws SessionExpireException {
        String cancelCommand = "XEPNR\\";
        if (!ownOffice && StringUtils.isNotBlank(office) && !"SHA255".equals(office)) {
            cancelCommand = "XEPNR\\" + office;
            if (force)
            	cancelCommand += "@SHA255";
        }
        String rtStr = executeCmdWithRetry(cancelCommand, false).getObject();
        return StringUtils.replace(rtStr, "\r", "\n");
    }

    /**
     * 封口
     *
     * @throws SessionExpireException
     */
    public String heal() throws SessionExpireException {
        return executeCmdWithRetry("@I", false).getObject();
    }

    /**
     * pnr授权
     *
     * @param targetOffice
     * @throws SessionExpireException
     */
    public String getRMK_AUTH(String targetOffice) throws SessionExpireException {
        return executeCmdWithRetry("RMK TJ AUTH " + targetOffice, false).getObject();
    }

    /**
     * 还原操作
     */
    public void resume() throws SessionExpireException {
        this.extendSessionExpireMillsec(PnrParserConstant.RESUME_MILLSEC, "resume");

        logger.info("还原操作");
        ReturnClass<String> rtClass = null;
        for (int index = 0; index < 3; index++) {
            rtClass = executeCmdWithRetry("IG", false);
            if (null != rtClass && null != rtClass.getObject() && "NO PNR".equalsIgnoreCase(rtClass.getObject().trim())) {
                break;
            }
        }
    }

    /**
     * 取消RT出来的PNR中的指定行
     *
     * @param lineNum
     * @return
     * @throws SessionExpireException
     */
    public String getXE(int lineNum) throws SessionExpireException {
        return executeCmdWithRetry("xe " + lineNum, false).getObject();
    }

    /**
     * 取消RT出来的PNR中的指定行（多行）
     *
     * @param lineNum
     * @return
     * @throws SessionExpireException
     */
    public String getXEs(List<Integer> lineNums) throws SessionExpireException {
    	StringBuilder lineNumsStr = new StringBuilder();
    	for (Integer lineNum:lineNums) {
    		lineNumsStr.append(lineNum).append("/");
    	}
    	lineNumsStr.deleteCharAt(lineNumsStr.length()-1);
        return executeCmdWithRetry("XE " + lineNumsStr.toString(), false).getObject();
    }

    /**
     * 获取城市间距离
     *
     * @param cities
     * @throws SessionExpireException
     */
    public String getXS_FSM(String cities) throws SessionExpireException {
        return executeCmdWithRetry("XS FSM " + cities, false).getObject();
    }

    public void close() {
        RPCServiceSingleton.closeSession();
    }

    /**
     * RTR指令，该指令目前主要用来获取大编码，该指令依赖于RT指令
     *
     * @throws SessionExpireException
     */
    public void getRTR() throws SessionExpireException {
        executeCmdWithRetry("RTR", true);
    }

    /**
     * PF指令，该指令的作用是获取第一页的内容
     *
     * @return
     * @throws SessionExpireException
     */
    public String getPF() throws SessionExpireException {
        return executeCmdWithRetry("PF", true).getObject().trim().replaceAll("\r", "\n");
    }
}
