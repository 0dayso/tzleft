package com.travelzen.etermface.service.abe_imitator.fare;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.travelzen.etermface.service.abe_imitator.fare.bargains.BargainFareSearchParser;
import com.travelzen.etermface.service.EtermWebClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.etermface.service.abe_imitator.fare.pojo.CabinInfo;
import com.travelzen.etermface.service.abe_imitator.fare.pojo.FareSearchRequest;
import com.travelzen.etermface.service.abe_imitator.fare.pojo.FareSearchResponse;
import com.travelzen.etermface.service.abe_imitator.fare.publics.PublicFareSearchParser;
import com.travelzen.etermface.common.config.cdxg.CdxgConstant;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.fare.NfdRuleOneParser;
import com.travelzen.framework.core.common.ReturnClass;

public class FareSearch {
    private static Logger logger = LoggerFactory.getLogger(FareSearch.class);
    private EtermWebClient etermWebClient;

    public FareSearch() {
        etermWebClient = new EtermWebClient();
    }

    public FareSearchResponse publicFareSearch(FareSearchRequest req) throws SessionExpireException {
        FareSearchResponse fareSearchResponse = new FareSearchResponse();
        int i = 0;
        String notes = null;
        while (i < 3) {
            i++;
            try {
                notes = publicFareSearchEtermRs(req, fareSearchResponse);
                break;
            } catch (SessionExpireException e1) {
                continue;
            }
        }
        if (StringUtils.isBlank(notes)) {
            return fareSearchResponse;
        }
        try {
            String rs = notes.toString();
            rs = rs.replaceAll("\r\n", "\r");
            fareSearchResponse = PublicFareSearchParser.parseString(notes.toString());
            fareSearchResponse.setSuccess(true);
        } catch (Exception e) {
            fareSearchResponse.setSuccess(false);
            fareSearchResponse.setErrorInfo("publicFare Parser error : \n " + e.getMessage() + "\n" + notes.toString());
            logger.error("error:{" + e.getMessage() + "}");
            logger.error("publicFare Parser error:{" + notes.toString() + "}");
        }
        return fareSearchResponse;
    }

    private String publicFareSearchEtermRs(FareSearchRequest req, FareSearchResponse fareSearchResponse)
            throws SessionExpireException {
        StringBuffer notes = new StringBuffer();
        try {
            int n = 1;
            // 获取资源 并利用资源查询生成结果
            etermWebClient.connect(CdxgConstant.MAX_LOCK_KEEP_MILLSEC);
            String errorMsg = "";
            ReturnClass<String> cmdRs = etermWebClient.executeCmdWithRetry(req.publicFareSearchCmd(), false);
            String result = "";
            if (!cmdRs.isSuccess()) {
                errorMsg = cmdRs.getStatus().toString();
                logger.error(errorMsg);
                fareSearchResponse.setSuccess(false);
                fareSearchResponse.setErrorInfo(errorMsg);
                return null;
            } else {
                result = cmdRs.getObject();
                if (null == result) {
                    logger.error("结果为空");
                    fareSearchResponse.setSuccess(false);
                    fareSearchResponse.setErrorInfo("结果为空");
                    return null;
                }
                if (result.contains("指定航段本日没有适用运价")) {
                    logger.error("指定航段本日没有适用运价");
                    fareSearchResponse.setSuccess(false);
                    fareSearchResponse.setErrorInfo("指定航段本日没有适用运价");
                    return null;
                }
                if (result.contains("航空公司")) {
                    logger.error("指定航段本日没有适用运价");
                    fareSearchResponse.setSuccess(false);
                    fareSearchResponse.setErrorInfo("指定航段本日没有适用运价");
                    return null;
                }
            }
            if (StringUtils.isBlank(result)) {
                fareSearchResponse.setSuccess(false);
                fareSearchResponse.setErrorInfo("raw result is blank!");
                return null;
            }
            result = result.trim();
            notes.append(result);
            notes.append("\r");

            char pageNumChar = result.charAt(result.length() - 1);
            char pageNumCharBefore = result.charAt(result.length() - 2);
            if (Character.isDigit(pageNumChar)) {
                String str = "";
                if (Character.isDigit(pageNumCharBefore)) {
                    str = str + pageNumCharBefore + pageNumChar;
                } else {
                    str = str + pageNumChar;
                }
                int pageNum = Integer.parseInt(String.valueOf(str));
                while (n < pageNum) {
                    n++;
                    cmdRs = etermWebClient.executePn();
                    if (!cmdRs.isSuccess()) {
                        errorMsg = cmdRs.getStatus().toString();
                        break;
                    } else {
                        result = cmdRs.getObject();
                        if (null == result) {
                            logger.error("unknow error");
                            break;
                        }
                    }
                    result = result.trim();
                    notes.append(result);
                    notes.append("\r");
                }
                logger.info("publicFareSearchRS:{" + notes.toString() + "}");
            }
        } catch (Exception e) {
            logger.error("error:{" + e.getMessage() + "}");
            logger.error("publicFareSearchEtermRs error:{" + notes.toString() + "}");
            fareSearchResponse.setSuccess(false);
            fareSearchResponse.setErrorInfo("publicFareSearchEtermRs  error : \n " + e.getMessage() + "\n"
                    + notes.toString());
            return null;
        } finally {
            etermWebClient.close();
        }
        return notes.toString();
    }

    public FareSearchResponse bargainFareSearchWithNfnInternal(FareSearchRequest req) throws SessionExpireException {
        FareSearchResponse fareSearchResponse = new FareSearchResponse();
        String notes = null;
        try {
            etermWebClient.connect(CdxgConstant.MAX_LOCK_KEEP_MILLSEC);
            long startTime = System.currentTimeMillis();
            String taskName = req.bargainFareSearchCmd();

            notes = bargainFareSearchEtermWithClient(etermWebClient, req, fareSearchResponse);
            if (StringUtils.isBlank(notes)) {
                return fareSearchResponse;
            }
            logger.info(loggerInfo(taskName, startTime, req.bargainFareSearchCmd(), notes));
            try {
                fareSearchResponse = bargainFareSearchParser(notes);
            } catch (Exception e) {
                fareSearchResponse.setSuccess(false);
                fareSearchResponse.setErrorInfo("bargainFareSearch Parser error :" + e.getMessage() + "\n"
                        + notes.toString());
                logger.error("error:{" + e.getMessage() + "}", e);
                logger.error("bargainFareSearch Parser error:{" + notes.toString() + "}");

            }
            if (fareSearchResponse.getErrorInfo() != null) {
                return fareSearchResponse;
            }

            List<CabinInfo> lsCabin = fareSearchResponse.getCabinInfos();
            // 保存已经和运价规则匹配好的仓位价格，匹配出现异常的不保存
            List<CabinInfo> lsCabinRs = new ArrayList<CabinInfo>();
            fareSearchResponse.setCabinInfos(lsCabinRs);
            for (CabinInfo cb : lsCabin) {
                etermWebClient.extendSessionExpireMillsec(2000, "NFN");
                String idx = cb.getIndex();
                String cmd = "NFN:" + idx + "//01";
                ReturnClass<String> rs = etermWebClient.executeCmdWithRetry(cmd, false);
                logger.info(loggerInfo(taskName, startTime, cmd, rs.getObject()));
                int i = 0;
                while (!(rs.isSuccess() && rs.getObject().contains(cmd))) {
                    i++;
                    Thread.sleep(i * 1000);
                    if (i > 2) {
                        logger.error("2次尝试后，仍然获得错误结果:" + loggerInfo(taskName, startTime, cmd, rs.getObject()));
                        break;
                    }
                    rs = etermWebClient.executeCmdWithRetry(cmd, false);
                    logger.info("第i次查询：" + loggerInfo(taskName, startTime, cmd, rs.getObject()));
                }
                if (rs.isSuccess() && rs.getObject().contains(cmd)) {
                    CabinInfo tmpcb = NfdRuleOneParser.parse(rs.getObject());
                    cb.setSuitableTimeRange(tmpcb.getSuitableTimeRange());
                    cb.setSuitableWeekdays(tmpcb.getSuitableWeekdays());
                    cb.setPrebookEarliestDay(tmpcb.getPrebookEarliestDay());
                    cb.setPrebookLatestDay(tmpcb.getPrebookLatestDay());
                    cb.setSuitableFltNumber(tmpcb.getSuitableFltNumber());
                    cb.setOutwardExcludeFltNumber(tmpcb.getOutwardExcludeFltNumber());
                    cb.setEarliestIssuteTktDate(tmpcb.getEarliestIssuteTktDate());
                    cb.setLatestIssuteTktDate(tmpcb.getLatestIssuteTktDate());
                    lsCabinRs.add(cb);
                }
                logger.info(loggerInfo(taskName, startTime, "", cb.toString()));
            }
            logger.info("task end, expenses:" + (System.currentTimeMillis() - startTime) + ", result :{"
                    + fareSearchResponse.toXML().replaceAll("\n", "") + "}");

        } catch (Exception e) {
            logger.error("bargainFareSearchWithNfnInternal error : "
                    + req.bargainFareSearchCmd() + "error:{" + e.getMessage() + "}", e);
            fareSearchResponse.setSuccess(false);
            fareSearchResponse.setErrorInfo("bargainFareSearchWithNfnInternal error : " + e.getMessage());
            return null;
        } finally {
            if (etermWebClient != null) {
                etermWebClient.close();
            }
        }
        return fareSearchResponse;
    }

    public FareSearchResponse bargainFareSearchWithNfn(FareSearchRequest req) {
        FareSearchResponse fareSearchResponse = new FareSearchResponse();
        int i = 0;
        while (i < 2) {
            i++;
            try {
                fareSearchResponse = bargainFareSearchWithNfnInternal(req);
                break;
            } catch (SessionExpireException e) {
                logger.info(i + ", SessionExpireException: " + e.getMessage(), e);
                continue;
            }
        }
        return fareSearchResponse;
    }

    public FareSearchResponse bargainFareSearch(FareSearchRequest req) {
        FareSearchResponse fareSearchResponse = new FareSearchResponse();
        int i = 0;
        String notes = null;
        while (i < 3) {
            i++;
            try {
                notes = bargainFareSearchEtermRs(req, fareSearchResponse);
                break;
            } catch (SessionExpireException e1) {
                if (i == 3) {
                    logger.error("查询NFD信息尝试失败三次：" + req.bargainFareSearchCmd());
                }
                continue;
            }
        }
        if (StringUtils.isBlank(notes)) {
            return fareSearchResponse;
        }
        try {
            fareSearchResponse = bargainFareSearchParser(notes);
        } catch (Exception e) {
            fareSearchResponse.setSuccess(false);
            fareSearchResponse.setErrorInfo("bargainFareSearch Parser error :" + e.getMessage() + "\n"
                    + notes.toString());
            logger.error("error:{" + e.getMessage() + "}", e);
            logger.error("bargainFareSearch Parser error:{" + notes.toString() + "}");

        }
        return fareSearchResponse;
    }

    public FareSearchResponse bargainFareSearchParser(String notes) throws Exception {
        if (notes.startsWith(">")) {
            notes = notes.replaceFirst(">", "(");
            notes = notes.replaceAll("\\u001e", "");
        }
        InputStream in = new ByteArrayInputStream(notes.getBytes("UTF-8"));
        BargainFareSearchParser parser = new BargainFareSearchParser(in);
        FareSearchResponse fareSearchResponse = parser.analysis();
        fareSearchResponse.setSuccess(true);
        parser = null;
        return fareSearchResponse;
    }

    public String bargainFareSearchEtermWithClient(EtermWebClient etermWebClient,
                                                   FareSearchRequest req, FareSearchResponse fareSearchResponse) throws SessionExpireException {
        StringBuffer notes = new StringBuffer();
        String result = "";
        try {
            int n = 1;
            // 获取资源 并利用资源查询生成结果
            String errorMsg = "";
            ReturnClass<String> cmdRs = etermWebClient.executeCmdWithRetry(req.bargainFareSearchCmd(), false);
            if (!cmdRs.isSuccess()) {
                errorMsg = cmdRs.getStatus().toString();
                logger.error(errorMsg);
                fareSearchResponse.setSuccess(false);
                fareSearchResponse.setErrorInfo(errorMsg);
                return null;
            } else {
                result = cmdRs.getObject();
                if (null == result) {
                    logger.error("结果为空");
                    fareSearchResponse.setSuccess(false);
                    fareSearchResponse.setErrorInfo("结果为空");
                    return null;
                } else if (result.contains("指定航段本日没有适用运价")) {
                    logger.error(req.bargainFareSearchCmd() + "指定航段本日没有适用运价");
                    fareSearchResponse.setSuccess(false);
                    fareSearchResponse.setErrorInfo("指定航段本日没有适用运价");
                    return null;
                } else if (result.contains("航空公司")) {
                    logger.error(req.bargainFareSearchCmd() + "指定航段本日没有适用运价");
                    fareSearchResponse.setSuccess(false);
                    fareSearchResponse.setErrorInfo("指定航段本日没有适用运价");
                    return null;
                } else if (result.contains("指令超时")) {
                    logger.error(req.bargainFareSearchCmd() + "指令超时");
                    fareSearchResponse.setSuccess(false);
                    fareSearchResponse.setErrorInfo("指令超时");
                    return null;
                } else if (!result.contains(req.bargainFareSearchResultStart())) {
                    logger.error(req.bargainFareSearchCmd() + "返回结果和查询不匹配,查询结果：" + result.replaceAll("\r", "\n"));
                    fareSearchResponse.setSuccess(false);
                    fareSearchResponse.setErrorInfo("返回结果和查询不匹配");
                    return null;
                }
            }
            if (StringUtils.isBlank(result)) {
                fareSearchResponse.setSuccess(false);
                fareSearchResponse.setErrorInfo("raw result is blank!");
                return null;
            }

            result = result.trim();
            result = result.replaceAll("\r\n", "\r");
            result = result.replaceAll("\n", "\r");
            int index = result.lastIndexOf("\r");
            String pageString = result.substring(index);
            result = result.substring(0, index);
            result = result.trim();
            String[] strs = result.split("\r");
            int startEnd = 0;
            notes.append(result);
            notes.append("\r");
            char pageNumChar = pageString.charAt(pageString.length() - 1);
            char pageNumCharBefore = pageString.charAt(pageString.length() - 2);
            if (Character.isDigit(pageNumChar)) {
                String str = "";
                if (Character.isDigit(pageNumCharBefore)) {
                    str = str + pageNumCharBefore + pageNumChar;
                } else {
                    str = str + pageNumChar;
                }
                int pageNum = Integer.parseInt(String.valueOf(str));
                while (n < pageNum) {
                    n++;
                    cmdRs = etermWebClient.executePn();
                    if (!cmdRs.isSuccess()) {
                        errorMsg = cmdRs.getStatus().toString();
                        break;
                    } else {
                        result = cmdRs.getObject();
                        if (StringUtils.isBlank(result)) {
                            logger.error("unknow error");
                            break;
                        }
                    }
                    result = result.trim();
                    result = result.replaceAll("\r\n", "\r");
                    result = result.replaceAll("\n", "\r");
                    index = result.lastIndexOf("\r");
                    pageString = result.substring(index);
                    result = result.substring(0, index);
                    if (startEnd == 0) {
                        for (String string : strs) {
                            int find = result.indexOf(string);
                            if (find != -1) {
                                startEnd++;
                                continue;
                            } else {
                                break;
                            }
                        }
                    }
                    for (int i = 0; i < startEnd; i++) {
                        result = result.substring(result.indexOf("\r") + 1);
                    }
                    result = result.trim();
                    notes.append(result);
                    notes.append("\r");
                }
                // logger.info("bargainFareSearchRs :{" + notes.toString() +
                // "}");
            }
        } catch (Exception e) {
            logger.error("error:{" + e.getMessage() + ";" + result + "}", e);
            logger.error("bargainFareSearchEtermRs error:{" + notes.toString() + "}");
            fareSearchResponse.setSuccess(false);
            fareSearchResponse.setErrorInfo("bargainFareSearchEtermRs  error:" + e.getMessage() + "\n"
                    + notes.toString());
        }

        return notes.toString();
    }

    private String bargainFareSearchEtermRs(FareSearchRequest req, FareSearchResponse fareSearchResponse)
            throws SessionExpireException {

        try {
            etermWebClient.connect();
            return bargainFareSearchEtermWithClient(etermWebClient, req, fareSearchResponse);
        } catch (SessionExpireException e) {
            throw e;
        } finally {
            etermWebClient.close();
        }

    }

    public static String loggerInfo(String taskName, long taskStartTime, String cmd, String rs) {
        if (StringUtils.isBlank(rs)) {
            rs = "";
        } else {
            rs = rs.replaceAll("\r", "\n");
        }

        String expenses = String.valueOf(System.currentTimeMillis() - taskStartTime);
        String loggerInfo = String.format(
                "taskName:%s, task expenses:%s, cmd:%s; black screen results:%s", taskName,
                expenses, cmd, rs);
        return loggerInfo;
    }
}
