package com.travelzen.etermface.service;

import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.common.ufis.util.UfisException;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.common.PNRStatusParser;
import com.travelzen.etermface.service.constant.UfisStatus;
import com.travelzen.etermface.service.entity.PnrOpResult;
import com.travelzen.etermface.service.enums.PNRStatus;
import com.travelzen.framework.core.common.ReturnClass;
import com.travelzen.framework.core.common.ReturnCode;

/**
 * @author hongqiang.mao
 * @date 2013-7-8 上午11:09:05
 * @description
 */
public class CancelPNRParser {
	
    /**
     * 判断是不是已出票状态
     */
    private static final String RR_STATUS_REGEX = ".*[A-Z]{2}[0-9]{2}[A-Z]{3}.{2}[A-Z]{6} RR[0-9]{1}.*";

    private static final String TNKE_REGEX = ".*SSR TKNE.*";

    private static final String OSI_1E_REGEX = ".*OSI 1E.*TN/.*";

    private static final String OFFICE_REGEX = "[A-Za-z]{3}\\d{3}";

    private Logger logger = LoggerFactory.getLogger(CancelPNRParser.class);

    /**
     * 取消结果转化为XML
     *
     * @param pPnr
     * @return
     */
    public String cancelPNR(String pPnr, boolean forceCancel, String office, boolean force) throws SessionExpireException, UfisException {
    	if (UfisStatus.active && UfisStatus.cancelPnr) {
    		PnrOpResult lvPnrOpResult = cancelPnrByUfis(pPnr, forceCancel, office, force);
            if (null == lvPnrOpResult
                    || (ReturnCode.ERROR.getErrorCode().equalsIgnoreCase(lvPnrOpResult.getReturnCode()) && lvPnrOpResult
                    .getMessage().contains("失败"))) {
                lvPnrOpResult = cancelPnrByUfis(pPnr, forceCancel, office, force);
            }
            return lvPnrOpResult.toXML();
    	} else {
    		PnrOpResult lvPnrOpResult = cancelPNRResult(pPnr, forceCancel, office, force);
            if (null == lvPnrOpResult
                    || (ReturnCode.ERROR.getErrorCode().equalsIgnoreCase(lvPnrOpResult.getReturnCode()) && lvPnrOpResult
                    .getMessage().contains("失败"))) {
                lvPnrOpResult = cancelPNRResult(pPnr, forceCancel, office, force);
            }
            return lvPnrOpResult.toXML();
    	}
    }

    /**
     * 取消PNR
     *
     * @param pPnr
     * @return
     */
    public PnrOpResult cancelPNRResult(String pPnr, boolean forceCancel, String office, boolean force) throws SessionExpireException {
        EtermWebClient lvEtermWebClient = new EtermWebClient();
        PnrOpResult lvPnrOpResult = new PnrOpResult();
        try {
            lvPnrOpResult.setIssue(false);

            if (StringUtils.isBlank(pPnr)) {
                lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                lvPnrOpResult.setMessage("请输入有效的PNR");
                return lvPnrOpResult;
            }

            boolean nextStep = true;
            
            boolean ownOffice = false;
            if (null != office)
            	ownOffice = true;

            try {
            	if (null != office)
            		lvEtermWebClient.connect(office);
            	else
            		lvEtermWebClient.connect();
            } catch (Exception e) {
                logger.error("成都新港连接异常：" + e.getMessage(), e);
                lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                lvPnrOpResult.setMessage(e.getMessage());
                nextStep = false;
            }

            if (nextStep) {
                ReturnClass<String> lvReturnClass = lvEtermWebClient.getRT(pPnr, false);
                String lvRTResult = lvReturnClass.getObject();
                logger.info("RT结果\n" + lvReturnClass.getObject());
                if (null == office && StringUtils.isNotBlank(lvRTResult)) {
                    lvRTResult = lvRTResult.trim();
                    if (lvRTResult.length() > 6) {
                        office = lvRTResult.substring(lvRTResult.length() - 6);
                        if (!office.matches(OFFICE_REGEX)) {
                            office = null;
                        }
                    }
                }


                Pair<PNRStatus, String> lvPNRStatusPair = PNRStatusParser.checkPNRStatus(lvRTResult);

                if (PNRStatus.NORNAL != lvPNRStatusPair.getValue0() && PNRStatus.ELECTRONIC_TICKET != lvPNRStatusPair.getValue0()) {
                    if (PNRStatus.ENTIRELY_CANCELLED == lvPNRStatusPair.getValue0()) {
                        lvPnrOpResult.setReturnCode(ReturnCode.SUCCESS.getErrorCode());
                    } else {
                        lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                    }
                    lvPnrOpResult.setMessage(lvPNRStatusPair.getValue1());
                    lvPnrOpResult.setIssue(false);
                    nextStep = false;
                } else {
                    if (PNRStatus.ELECTRONIC_TICKET == lvPNRStatusPair.getValue0()) {
                        lvPnrOpResult.setIssue(true);
                        lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                        lvPnrOpResult.setMessage("PNR(" + pPnr + ")已出票!!!");
                        if (!forceCancel) {
                            nextStep = false;
                        }
                    } else {
                        String status = getStatus(lvRTResult);
                        logger.info("PNR " + pPnr + "STATUS --> " + status);
                        if ("RR".equals(status)) {
                            lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                            lvPnrOpResult.setMessage("PNR(" + pPnr + ")已出票!!!");
                            lvPnrOpResult.setIssue(true);
                            if (!forceCancel) {
                                nextStep = false;
                            }
                        }
                    }
                }
            }


            if (nextStep) {
                String lvSuccessStr = "PNR CANCELLED " + pPnr.trim();
                String lvRtResult = lvEtermWebClient.cancelPNR(office, ownOffice, force);
                logger.info("删除PNR(" + pPnr + ")返回信息:" + lvRtResult);
                if (null != lvRtResult && lvSuccessStr.equalsIgnoreCase(lvRtResult.trim())) {
                    lvPnrOpResult.setReturnCode(ReturnCode.SUCCESS.getErrorCode());
                    lvPnrOpResult.setMessage("取消PNR(" + pPnr + ")成功");
                } else {
                    lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                    lvPnrOpResult.setMessage("取消PNR(" + pPnr + ")失败");
                }
            }

            logger.info("Cancel PNR(" + pPnr + ")	" + lvPnrOpResult.toString());
        } finally {
        	if (null != lvEtermWebClient)
        		lvEtermWebClient.close();
        }
        return lvPnrOpResult;
    }
    
    /**
     * 取消PNR
     *
     * @param pnr
     * @return
     */
    public PnrOpResult cancelPnrByUfis(String pnr, boolean forceCancel, String office, boolean force) {
        EtermUfisClient client = null;
        PnrOpResult lvPnrOpResult = new PnrOpResult();
        try {
            lvPnrOpResult.setIssue(false);

            if (StringUtils.isBlank(pnr)) {
                lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                lvPnrOpResult.setMessage("请输入有效的PNR");
                return lvPnrOpResult;
            }

            boolean nextStep = true;
            
            boolean ownOffice = false;
            if (null != office)
            	ownOffice = true;

            if (null != office)
        		client = new EtermUfisClient(office);
        	else
        		client = new EtermUfisClient();

            if (nextStep) {
                String lvRTResult = client.execRt(pnr, true);
                logger.info("RT结果\n" + lvRTResult);
                if (null == office && StringUtils.isNotBlank(lvRTResult)) {
                    lvRTResult = lvRTResult.trim();
                    if (lvRTResult.length() > 6) {
                        office = lvRTResult.substring(lvRTResult.length() - 6);
                        if (!office.matches(OFFICE_REGEX)) {
                            office = null;
                        }
                    }
                }


                Pair<PNRStatus, String> lvPNRStatusPair = PNRStatusParser.checkPNRStatus(lvRTResult);

                if (PNRStatus.NORNAL != lvPNRStatusPair.getValue0() && PNRStatus.ELECTRONIC_TICKET != lvPNRStatusPair.getValue0()) {
                    if (PNRStatus.ENTIRELY_CANCELLED == lvPNRStatusPair.getValue0()) {
                        lvPnrOpResult.setReturnCode(ReturnCode.SUCCESS.getErrorCode());
                    } else {
                        lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                    }
                    lvPnrOpResult.setMessage(lvPNRStatusPair.getValue1());
                    lvPnrOpResult.setIssue(false);
                    nextStep = false;
                } else {
                    if (PNRStatus.ELECTRONIC_TICKET == lvPNRStatusPair.getValue0()) {
                        lvPnrOpResult.setIssue(true);
                        lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                        lvPnrOpResult.setMessage("PNR(" + pnr + ")已出票!!!");
                        if (!forceCancel) {
                            nextStep = false;
                        }
                    } else {
                        String status = getStatus(lvRTResult);
                        logger.info("PNR " + pnr + "STATUS --> " + status);
                        if ("RR".equals(status)) {
                            lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                            lvPnrOpResult.setMessage("PNR(" + pnr + ")已出票!!!");
                            lvPnrOpResult.setIssue(true);
                            if (!forceCancel) {
                                nextStep = false;
                            }
                        }
                    }
                }
            }


            if (nextStep) {
                String lvSuccessStr = "PNR CANCELLED " + pnr.trim();
                String lvRtResult = client.cancelPnr(office, ownOffice);
                logger.info("删除PNR(" + pnr + ")返回信息:" + lvRtResult);
                if (null != lvRtResult && lvSuccessStr.equalsIgnoreCase(lvRtResult.trim())) {
                    lvPnrOpResult.setReturnCode(ReturnCode.SUCCESS.getErrorCode());
                    lvPnrOpResult.setMessage("取消PNR(" + pnr + ")成功");
                } else {
                    lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                    lvPnrOpResult.setMessage("取消PNR(" + pnr + ")失败");
                }
            }

            logger.info("Cancel PNR(" + pnr + ")	" + lvPnrOpResult.toString());
        } catch (UfisException e) {
        	logger.error("Ufis异常：{}", e.getMessage(), e);
        	lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
            lvPnrOpResult.setMessage("Ufis异常：" + e.getMessage());
		} finally {
			if (null != client)
				client.close();
        }
        return lvPnrOpResult;
    }

    private static String getStatus(String rs) {
        if (null == rs) {
            return null;
        }

        String status = null;

        rs = rs.replaceAll("\r", "\n");

        String[] strs = rs.split("\n+");
        for (String str : strs) {
            if (str.matches(RR_STATUS_REGEX) || str.matches(TNKE_REGEX) || str.matches(OSI_1E_REGEX)) {
                status = "RR";
                break;
            }
        }

        return status;
    }
}
