package com.travelzen.etermface.service.pnr;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.common.ufis.util.UfisException;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.travelzen.etermface.common.config.cdxg.exception.EtermException;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.EtermUfisClient;
import com.travelzen.etermface.service.EtermWebClient;
import com.travelzen.etermface.service.PNRParser;
import com.travelzen.etermface.service.constant.PnrParserConstant;
import com.travelzen.etermface.service.entity.EtermSessionInfo;
import com.travelzen.etermface.service.entity.ParseConfBean;
import com.travelzen.etermface.service.entity.PnrOperateResult;
import com.travelzen.etermface.service.entity.PnrRet.PatItem;
import com.travelzen.etermface.service.entity.PnrRet.PatResult;
import com.travelzen.etermface.service.entity.config.PnrOperateConfig;
import com.travelzen.framework.core.common.ReturnClass;
import com.travelzen.framework.core.common.ReturnCode;
import com.travelzen.framework.core.exception.BizException;
import com.travelzen.framework.core.time.DateTimeUtil;

public class PnrSfcSetterByUfis {
	
    private static Logger logger = LoggerFactory.getLogger(PnrSfcSetterByUfis.class);
    
    private EtermUfisClient client;
    
    private String officeId = null;

    public PnrSfcSetterByUfis(ParseConfBean confBean) {
        officeId = confBean.getOfficeId();
    }

    private void setPnrPriceInternal(List<PatItem> pats, PnrOperateConfig param) throws UfisException {
        logger.info("set price:{}", param.toString());

        String ret = null;

        logger.debug("before RT" + param.pnr);
        ret = client.execCmd("RT " + param.pnr, false);
        logger.debug("after RT" + param.pnr);

        EtermSessionInfo esi = new EtermSessionInfo();
        esi.setPnr(param.pnr);

        if (null == ret) {
            esi.addStatusLine(ret);
            throw new EtermException(ReturnCode.ERROR, "error in RT", EtermSessionInfo.class, esi);
        } else {
            logger.debug("RT SUCCESS:" + ret);
        }

        Map<String, PatItem> psgTypePatItemMap = Maps.newHashMap();
        for (PatItem pat : pats) {
            psgTypePatItemMap.put(pat.PsgType, pat);
        }

        boolean foundFare = false;
        if (StringUtils.isNotBlank(param.adtFareBasis)) {
            PatItem pat = psgTypePatItemMap.get(PnrParserConstant.PASSENGER_TYPE_ADT);
            for (PatResult pr : pat.PatResults) {
                if ("TEXEMPTYQ".equals(pr.yq)) {
                    logger.info("燃油附加费为空");
                } else {
                    double yq = getDoubleValue(pr.yq);
                    if (yq <= 0) {
                        throw new EtermException(ReturnCode.ERROR, "没有燃油费", EtermSessionInfo.class, esi);
                    }
                }
                
                double fare = getDoubleValue(pr.fare);
                if (StringUtils.equalsIgnoreCase(pr.fareBasis, param.adtFareBasis) && fare == param.adtFare) {
                    client.execCmd("PAT:A");
                    ret = client.execCmd("SFC:" + pr.sfc);

                    logger.info("ret:{}", ret);

                    foundFare = true;
                    break;
                }
            }
            // QUNAR传错误运价基础，只匹配价格
            if (!foundFare) {
            	for (PatResult pr : pat.PatResults) {
                    if ("TEXEMPTYQ".equals(pr.yq)) {
                        logger.info("燃油附加费为空");
                    } else {
                        double yq = getDoubleValue(pr.yq);
                        if (yq <= 0) {
                            throw new EtermException(ReturnCode.ERROR, "没有燃油费", EtermSessionInfo.class, esi);
                        }
                    }
                    
                    double fare = getDoubleValue(pr.fare);
                    if (fare == param.adtFare) {
                        client.execCmd("PAT:A");
                        ret = client.execCmd("SFC:" + pr.sfc);

                        logger.info("ret:{}", ret);

                        foundFare = true;
                        break;
                    }
                }
            }
        } else if (StringUtils.isNotBlank(param.chdFareBasis)) {
            PatItem pat = psgTypePatItemMap.get(PnrParserConstant.PASSENGER_TYPE_CHD);
            for (PatResult pr : pat.PatResults) {
                if ("TEXEMPTYQ".equals(pr.yq)) {
                    logger.info("燃油附加费为空");
                } else {
                    double yq = getDoubleValue(pr.yq);
                    if (yq <= 0) {
                        throw new EtermException(ReturnCode.ERROR, "没有燃油费", EtermSessionInfo.class, esi);
                    }
                }

                double fare = getDoubleValue(pr.fare);
                if (StringUtils.equalsIgnoreCase(pr.fareBasis, param.chdFareBasis) && fare == param.chdFare) {
                    client.execCmd("PAT:A*CH");
                    ret = client.execCmd("SFC:" + pr.sfc);

                    logger.info("ret:{}", ret);

                    foundFare = true;
                    break;
                }
            }
        } else {
            throw new EtermException(ReturnCode.ERROR, "must set one fareBasis err:", EtermSessionInfo.class, esi);
        }

        if (!foundFare) {
            throw new EtermException(ReturnCode.ERROR, "can't find  target fareBasis err", EtermSessionInfo.class, esi);
        }

        if (null == ret) {
            esi.addStatusLine(ret);
            throw new EtermException(ReturnCode.ERROR, "ret is null", EtermSessionInfo.class, esi);
        }

        // client.extendSessionExpireMillsec(CdxgConstant.BASE_LOCK_KEEP_MILLSEC,
        // "ready to seal");

        ret = client.execCmd("@I ", true);
        logger.info("ret:{}", ret);

        DateTimeUtil.SleepSec(1);

        ret = client.execCmd("@I ", true);
        logger.info("ret:{}", ret);

        logger.info("ret:{}", ret);

        if (null == ret) {
            esi.addStatusLine(ret);
            throw new EtermException(ReturnCode.ERROR, "seal error", EtermSessionInfo.class, esi);
        }

        DateTimeUtil.SleepSec(1);

        OK:
        for (int i = 0; i < 2; i++) {
            ret = client.execRt(param.pnr, true);
            if (StringUtils.isNotBlank(ret)) {
                break OK;
            }
            DateTimeUtil.SleepSec(1);
        }

        logger.info("RT:\n" + ret);

        if (null == ret) {
            esi.addStatusLine(ret);
            throw new EtermException(ReturnCode.ERROR, "error in RT", EtermSessionInfo.class, esi);
        }

        String content = ret;

        if (!content.contains("AUTOMATIC FARE") && !content.contains("FN/A")) {
            throw new EtermException(ReturnCode.ERROR, "check  AUTOMATIC FARE failed", EtermSessionInfo.class, esi);
        }
    }

    private void setPnrPrice(PnrOperateConfig param) throws UfisException {
        ParseConfBean conf = new ParseConfBean();

        conf.needFare = true;
        conf.isDomestic = true;

        if (StringUtils.isNotBlank(param.officeId)) {
            conf.setOfficeId((param.officeId));
        }

        PNRParser parser = new PNRParser(conf);

        parser.parsePnrForTops(conf, param.pnr);
        
        //　在做价格前，判断PNR中是否已有价格，若已有价格则报错
        String pnr = parser.pnrRet.PNR;
        String rtRet = null;
        try {
            client = new EtermUfisClient(officeId);
            rtRet = client.execRt(pnr, true);
        } catch (UfisException e) {
            Throwables.propagate(e);
        } finally {
            client.close();
        }
        if (null == rtRet) {
        	logger.error("rt failed");
        	EtermSessionInfo esi = new EtermSessionInfo();
            esi.setPnr(param.pnr);
        	throw new EtermException(ReturnCode.ERROR, "rt failed", EtermSessionInfo.class, esi);
        } else {
        	String rtText = rtRet;
        	if (rtText.contains(".FC/A") || rtText.contains(".FN/A") || rtText.contains(".FP/")) {
        		logger.error("PNR已有价格");
                EtermSessionInfo esi = new EtermSessionInfo();
                esi.setPnr(param.pnr);
            	throw new EtermException(ReturnCode.ERROR, "PNR已有价格", EtermSessionInfo.class, esi);
        	}
        }
        //

        List<PatItem> pats = parser.pnrRet.PriceNodes.PatItem;

        if (pats == null || pats.size() == 0) {
            EtermSessionInfo esi = new EtermSessionInfo();
            esi.setPnr(param.pnr);
            throw new EtermException(ReturnCode.ERROR, "no pat price", EtermSessionInfo.class, esi);
        }

        try {
            client = new EtermUfisClient(officeId);
            setPnrPriceInternal(pats, param);
        } catch (UfisException e) {
            Throwables.propagate(e);
        } finally {
            client.close();
        }

    }

    public ReturnClass<PnrOperateResult> operate(PnrOperateConfig param) throws UfisException {
        PnrOperateResult ret = new PnrOperateResult();
        ReturnClass<PnrOperateResult> finalRet = new ReturnClass<>();

        try {
            setPnrPrice(param);

            ret.status = ReturnCode.SUCCESS.toString();

            finalRet.setStatus(ReturnCode.SUCCESS);
            finalRet.setObject(ret);

        } catch (BizException e) {

            ret.status = e.getRetCode().toString();
            ret.error = e.getRetMsg();
            ret.errorDetail = e.getMessage();

            finalRet.setStatus(ReturnCode.ERROR);
            finalRet.setObject(ret);
        } catch (EtermException e) {

            ret.etermSessionInfo = e.getObject(EtermSessionInfo.class);
            ret.error = e.getMessage();
            ret.errorDetail = e.getMessage();

            finalRet.setStatus(ReturnCode.ERROR);
            finalRet.setObject(ret);
        }

        return finalRet;
    }

    ReturnClass<PnrOperateResult> operate(EtermWebClient client, PnrOperateConfig param) throws SessionExpireException {
        throw new BizException(ReturnCode.ERROR, "method not allowed ");
    }


    /**
     * 获取整形值
     *
     * @param str
     * @return
     */
    private double getDoubleValue(String str) {
        double doubleValue = 0;
        if (StringUtils.isBlank(str)) {
            return doubleValue;
        }

        if (str.startsWith("CNY")) {
            str = str.replace("CNY", "");
        }

        try {
            doubleValue = Double.valueOf(str.trim());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return doubleValue;
    }

}
