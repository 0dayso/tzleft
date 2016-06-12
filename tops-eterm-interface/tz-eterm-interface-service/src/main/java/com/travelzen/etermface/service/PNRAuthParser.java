package com.travelzen.etermface.service;

import java.text.SimpleDateFormat;
import java.util.Date;

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
 * @date 2013-6-20 下午1:26:05
 * @description
 */
public class PNRAuthParser {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm SSS");

    private Logger logger = LoggerFactory.getLogger(PNRAuthParser.class);

    private static final String AUTH_SUCCESS_PREFIX = "RMK TJ AUTH ";

    public String pnrAuthorizeWithRetry(String pnr, String ownerOffice, String grantorOffice) {
        // 尝试次数
        int retryTime = 3;

        String result = null;
        PnrOpResult pnrOpResult = null;
        for (int time = 0; time < retryTime; time++) {
            long begin = System.currentTimeMillis();
            try {
                pnrOpResult = pnrAuthorize(pnr, ownerOffice, grantorOffice);
                if (null != pnrOpResult && ReturnCode.SUCCESS.getErrorCode().equals(pnrOpResult.getReturnCode())) {
                    break;
                }
            } catch (SessionExpireException e) {
                logger.error(e.getMessage(), e);
            } finally {
                long end = System.currentTimeMillis();
                long last = end - begin;
                logger.info("第" + time + "次授权" + pnr + "，开始时间:" + sdf.format(new Date(begin)) + ",结束时间:" + sdf.format(new Date(end)) + ",耗时" + (last / 1000) + "秒" + (last % 1000) + "毫秒");
            }
        }


        if (null != pnrOpResult) {
            result = pnrOpResult.toXML();
        }

        return result;
    }

    public PnrOpResult pnrAuthorize(String pnr, String ownerOffice, String grantorOffice) throws SessionExpireException {
        if (UfisStatus.active && UfisStatus.auth) {
        	EtermUfisClient client = null;
            PnrOpResult lvPnrOpResult = new PnrOpResult();
            try {
                logger.info("pnrAuthorize method : pnr = " + pnr + ",ownerOffice =" + ownerOffice + ",grantorOffice ="
                        + grantorOffice);


                if (null != ownerOffice) {
                    ownerOffice = ownerOffice.trim().toUpperCase();
                }

                if (null != grantorOffice) {
                    grantorOffice = grantorOffice.trim().toUpperCase();
                }

                if (grantorOffice.equalsIgnoreCase(ownerOffice)) {
                    lvPnrOpResult.setReturnCode(ReturnCode.SUCCESS.getErrorCode());
                    lvPnrOpResult.setMessage("该PNR是OFFICE" + grantorOffice + "预订的，无需授权");
                    return lvPnrOpResult;
                }


                String authSuccessStr = AUTH_SUCCESS_PREFIX + grantorOffice.trim();
                boolean nextStep = true;

                client = new EtermUfisClient(ownerOffice);

                if (nextStep) {
                    /**
                     * 先对pnr进行rt操作，对rt结果进行分析
                     */
                    String lvRTResult = client.execRt(pnr, true);
                    if (null != lvRTResult) {
                        lvRTResult = lvRTResult.trim();
                    }

                    Pair<PNRStatus, String> lvPNRStatusPair = PNRStatusParser.checkPNRStatus(lvRTResult);
                    if (lvPNRStatusPair.getValue0() != PNRStatus.NORNAL) {
                        nextStep = false;
                        lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                        lvPnrOpResult.setMessage("pnr(" + pnr + ")" + lvPNRStatusPair.getValue1());
                    } else if (lvRTResult.contains(authSuccessStr)) {
                        lvPnrOpResult.setReturnCode(ReturnCode.SUCCESS.getErrorCode());
                        lvPnrOpResult.setMessage("已经授权，请不要重复授权");
                        nextStep = false;
                    } else if (lvRTResult.endsWith(grantorOffice)) {
                        lvPnrOpResult.setReturnCode(ReturnCode.SUCCESS.getErrorCode());
                        lvPnrOpResult.setMessage("该PNR是OFFICE" + grantorOffice + "预订的，无需授权");
                        nextStep = false;
                    }
                }

                if (nextStep) {
                    /**
                     * 授权
                     */
                    client.execCmd("RMK TJ AUTH " + grantorOffice);
                    client.heal();

                    /**
                     * 授权结果
                     */
                    String lvAuthResult = client.execRt(pnr, true);
                    if (lvAuthResult.contains(authSuccessStr)) {
                        lvPnrOpResult.setReturnCode(ReturnCode.SUCCESS.getErrorCode());
                        lvPnrOpResult.setMessage("授权成功");
                    } else {
                        lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                        lvPnrOpResult.setMessage(lvAuthResult);
                    }

                }


                String result = lvPnrOpResult.toXML();

                logger.info("pnrAuthorize method : pnr = " + pnr + ",ownerOffice =" + ownerOffice + ",grantorOffice ="
                        + grantorOffice + "\nresult=" + result);
            } catch (UfisException e) {
            	logger.error("Ufis异常：{}" + e.getMessage());
            	lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                lvPnrOpResult.setMessage(("Ufis异常：" + e.getMessage()));
			} finally {
				if (null != client)
					client.close();
            }
            return lvPnrOpResult;
        } else {
        	EtermWebClient lvEtermWebClient = new EtermWebClient();
            PnrOpResult lvPnrOpResult = new PnrOpResult();
            try {
                logger.info("pnrAuthorize method : pnr = " + pnr + ",ownerOffice =" + ownerOffice + ",grantorOffice ="
                        + grantorOffice);


                if (null != ownerOffice) {
                    ownerOffice = ownerOffice.trim().toUpperCase();
                }

                if (null != grantorOffice) {
                    grantorOffice = grantorOffice.trim().toUpperCase();
                }

                if (grantorOffice.equalsIgnoreCase(ownerOffice)) {
                    lvPnrOpResult.setReturnCode(ReturnCode.SUCCESS.getErrorCode());
                    lvPnrOpResult.setMessage("该PNR是OFFICE" + grantorOffice + "预订的，无需授权");
                    return lvPnrOpResult;
                }


                String authSuccessStr = AUTH_SUCCESS_PREFIX + grantorOffice.trim();
                boolean nextStep = true;

                try {
                    lvEtermWebClient.connect(ownerOffice);
                } catch (Exception e) {
                    e.printStackTrace();
                    lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                    lvPnrOpResult.setMessage(e.getMessage());
                    nextStep = false;
                }

                if (nextStep) {
                    /**
                     * 先对pnr进行rt操作，对rt结果进行分析
                     */
                    ReturnClass<String> lvReturnClass = lvEtermWebClient.getRT(pnr, false);
                    String lvRTResult = lvReturnClass.getObject();
                    if (null != lvRTResult) {
                        lvRTResult = lvRTResult.trim();
                    }

                    Pair<PNRStatus, String> lvPNRStatusPair = PNRStatusParser.checkPNRStatus(lvRTResult);
                    if (lvPNRStatusPair.getValue0() != PNRStatus.NORNAL) {
                        nextStep = false;
                        lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                        lvPnrOpResult.setMessage("pnr(" + pnr + ")" + lvPNRStatusPair.getValue1());
                    } else if (lvRTResult.contains(authSuccessStr)) {
                        lvPnrOpResult.setReturnCode(ReturnCode.SUCCESS.getErrorCode());
                        lvPnrOpResult.setMessage("已经授权，请不要重复授权");
                        nextStep = false;
                    } else if (lvRTResult.endsWith(grantorOffice)) {
                        lvPnrOpResult.setReturnCode(ReturnCode.SUCCESS.getErrorCode());
                        lvPnrOpResult.setMessage("该PNR是OFFICE" + grantorOffice + "预订的，无需授权");
                        nextStep = false;
                    }
                }

                if (nextStep) {
                    /**
                     * 授权
                     */
                    lvEtermWebClient.getRMK_AUTH(grantorOffice);
                    lvEtermWebClient.heal();

                    /**
                     * 授权结果
                     */
                    String lvAuthResult = lvEtermWebClient.getRT(pnr, false).getObject();
                    if (lvAuthResult.contains(authSuccessStr)) {
                        lvPnrOpResult.setReturnCode(ReturnCode.SUCCESS.getErrorCode());
                        lvPnrOpResult.setMessage("授权成功");
                    } else {
                        lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                        lvPnrOpResult.setMessage(lvAuthResult);
                    }

                }


                String result = lvPnrOpResult.toXML();

                logger.info("pnrAuthorize method : pnr = " + pnr + ",ownerOffice =" + ownerOffice + ",grantorOffice ="
                        + grantorOffice + "\nresult=" + result);
            } finally {
                lvEtermWebClient.close();
            }
            return lvPnrOpResult;
        }
    }

    /**
     * 取消pnr授权
     *
     * @param pnr
     * @param ownerOffice
     * @param grantorOffice
     * @return
     * @throws SessionExpireException
     */
    public String cancelPnrAuthorize(String pnr, String ownerOffice, String grantorOffice) throws SessionExpireException {
    	if (UfisStatus.active && UfisStatus.cancelAuth) {
    		PnrOpResult lvPnrOpResult = new PnrOpResult();
    		EtermUfisClient client = null;
            try {
                String authSuccessStr = AUTH_SUCCESS_PREFIX + grantorOffice.trim();
                int lineNum = -1;
                boolean nextStep = true;

                if (null != ownerOffice) {
                    ownerOffice = ownerOffice.trim().toUpperCase();
                }

                if (null != grantorOffice) {
                    grantorOffice = grantorOffice.trim().toUpperCase();
                }

                client = new EtermUfisClient(ownerOffice);

                if (nextStep) {
                	String lvRTResult = client.execRt(pnr, true);
                    String[] lvLinesRTResult = lvRTResult.replaceAll("\r", "\n").split("\n");

                    Pair<PNRStatus, String> lvPNRStatusPair = PNRStatusParser.checkPNRStatus(lvRTResult);
                    if (lvPNRStatusPair.getValue0() != PNRStatus.NORNAL) {
                        nextStep = false;
                        lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                        lvPnrOpResult.setMessage("pnr(" + pnr + ")" + lvPNRStatusPair.getValue1());
                    } else {
                        for (String tmLine : lvLinesRTResult) {
                            if (tmLine.contains(authSuccessStr)) {
                                String lineNumStr = tmLine.split(".RMK")[0].trim();
                                try {
                                    lineNum = Integer.parseInt(lineNumStr);
                                    break;
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        /**
                         * 没找到授权
                         */
                        if (lineNum == -1) {
                            lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                            lvPnrOpResult.setMessage("请确认pnr(" + pnr + ")已授权给" + grantorOffice);
                            nextStep = false;
                        }
                    }
                }

                if (nextStep) {
                    /**
                     * 取消授权
                     */
                	client.execCmd("XE " + lineNum);
                    client.heal();

                    /**
                     * 取消授权结果
                     */
                    String lvAuthResult = client.execRt(pnr, true);
                    if (!lvAuthResult.contains(authSuccessStr)) {
                        lvPnrOpResult.setReturnCode(ReturnCode.SUCCESS.getErrorCode());
                        lvPnrOpResult.setMessage("取消授权成功");
                    } else {
                        lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                        lvPnrOpResult.setMessage(lvAuthResult);
                    }
                }
            } catch (UfisException e) {
            	logger.error("Ufis异常：{}" + e.getMessage());
            	lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                lvPnrOpResult.setMessage("Ufis异常：" + e.getMessage());
			} finally {
                client.close();
            }
            return lvPnrOpResult.toXML();
    	} else {
    		PnrOpResult lvPnrOpResult = new PnrOpResult();
            EtermWebClient lvEtermWebClient = new EtermWebClient();
            try {
                String authSuccessStr = AUTH_SUCCESS_PREFIX + grantorOffice.trim();
                int lineNum = -1;
                boolean nextStep = true;

                if (null != ownerOffice) {
                    ownerOffice = ownerOffice.trim().toUpperCase();
                }

                if (null != grantorOffice) {
                    grantorOffice = grantorOffice.trim().toUpperCase();
                }

                try {
                    lvEtermWebClient.connect(ownerOffice);
                } catch (Exception e) {
                    e.printStackTrace();
                    lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                    lvPnrOpResult.setMessage(e.getMessage());
                    nextStep = false;
                }

                if (nextStep) {
                    ReturnClass<String> lvReturnClass = lvEtermWebClient.getRT(pnr, false);
                    String lvRTResult = lvReturnClass.getObject();
                    String[] lvLinesRTResult = lvRTResult.replaceAll("\r", "\n").split("\n");

                    Pair<PNRStatus, String> lvPNRStatusPair = PNRStatusParser.checkPNRStatus(lvRTResult);
                    if (lvPNRStatusPair.getValue0() != PNRStatus.NORNAL) {
                        nextStep = false;
                        lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                        lvPnrOpResult.setMessage("pnr(" + pnr + ")" + lvPNRStatusPair.getValue1());
                    } else {
                        for (String tmLine : lvLinesRTResult) {
                            if (tmLine.contains(authSuccessStr)) {
                                String lineNumStr = tmLine.split(".RMK")[0].trim();
                                try {
                                    lineNum = Integer.parseInt(lineNumStr);
                                    break;
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        /**
                         * 没找到授权
                         */
                        if (lineNum == -1) {
                            lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                            lvPnrOpResult.setMessage("请确认pnr(" + pnr + ")已授权给" + grantorOffice);
                            nextStep = false;
                        }
                    }
                }

                if (nextStep) {
                    /**
                     * 取消授权
                     */
                    lvEtermWebClient.getXE(lineNum);
                    lvEtermWebClient.heal();

                    /**
                     * 取消授权结果
                     */
                    String lvAuthResult = lvEtermWebClient.getRT(pnr, false).getObject();
                    if (!lvAuthResult.contains(authSuccessStr)) {
                        lvPnrOpResult.setReturnCode(ReturnCode.SUCCESS.getErrorCode());
                        lvPnrOpResult.setMessage("取消授权成功");
                    } else {
                        lvPnrOpResult.setReturnCode(ReturnCode.ERROR.getErrorCode());
                        lvPnrOpResult.setMessage(lvAuthResult);
                    }
                }
            } finally {
                lvEtermWebClient.close();
            }
            return lvPnrOpResult.toXML();
    	}
    }
}