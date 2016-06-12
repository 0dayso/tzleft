package com.travelzen.etermface.service.fare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.travelzen.etermface.service.common.PNRDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.common.pojo.fare.NfdFareRequest;
import com.travelzen.etermface.common.pojo.fare.NfdFareResponse;
import com.travelzen.etermface.common.pojo.fare.NfdFareResponseNew;
import com.travelzen.etermface.service.EtermWebClient;
import com.travelzen.framework.core.common.ReturnClass;
import com.travelzen.framework.core.json.JsonUtil;

/**
 * 获取NFD报价接口
 *
 * @author yiming.yan
 */
public class NfdFareService {

    private static Logger logger = LoggerFactory.getLogger(NfdFareService.class);

    public NfdFareResponseNew getNfd(NfdFareRequest nfdRequest) throws SessionExpireException {
        String dateStr = null;
        try {
            dateStr = PNRDateFormat.dayMonthFormat(nfdRequest.getDeptDate());
        } catch (Exception e) {
            logger.info("日期{},错误原因{}", nfdRequest.getDeptDate(), e);
        }
        if (null == dateStr) {
            return null;
        }

        NfdFareResponseNew nfdResponse = new NfdFareResponseNew();
        EtermWebClient etermWebClient = new EtermWebClient();
        etermWebClient.connect(30000);
        String nfdCmd = "NFD:" + nfdRequest.getDeptAirport() + nfdRequest.getArrAirport()
                + "/" + dateStr + "/" + nfdRequest.getCarrier();
        if (nfdRequest.getCabin() != null)
            nfdCmd += "/#" + nfdRequest.getCabin();

        // NFD翻页
        StringBuilder nfdText = new StringBuilder();
        String nfdPageText = null;
        try {
            int n = 1;
            String errorMsg = "";
            ReturnClass<String> cmdRs = etermWebClient.executeCmdWithRetry(nfdCmd, false);
            if (!cmdRs.isSuccess()) {
                errorMsg = cmdRs.getStatus().toString();
                logger.error(errorMsg);
                nfdResponse.setSuccess(false);
                nfdResponse.setErrorInfo(errorMsg);
                return null;
            } else {
                nfdPageText = cmdRs.getObject();
                if (StringUtils.isBlank(nfdPageText)) {
                    logger.error("结果为空");
                    nfdResponse.setSuccess(false);
                    nfdResponse.setErrorInfo("结果为空");
                    return null;
                } else if (nfdPageText.contains("指定航段本日没有适用运价")) {
                    logger.error(nfdCmd + "指定航段本日没有适用运价");
                    nfdResponse.setSuccess(false);
                    nfdResponse.setErrorInfo("指定航段本日没有适用运价");
                    return null;
                } else if (nfdPageText.contains("航空公司")) {
                    logger.error(nfdCmd + "指定航段本日没有适用运价");
                    nfdResponse.setSuccess(false);
                    nfdResponse.setErrorInfo("指定航段本日没有适用运价");
                    return null;
                } else if (nfdPageText.contains("指令超时")) {
                    logger.error(nfdCmd + "指令超时");
                    nfdResponse.setSuccess(false);
                    nfdResponse.setErrorInfo("指令超时");
                    return null;
                }
            }

            nfdPageText = nfdPageText.trim();
            nfdPageText = nfdPageText.replaceAll("\n", "\r");
            int index = nfdPageText.lastIndexOf("\r");
            String pageString = nfdPageText.substring(index);
            nfdPageText = nfdPageText.substring(0, index);
            nfdPageText = nfdPageText.trim();
            String[] strs = nfdPageText.split("\r");
            int startEnd = 0;
            nfdText.append(nfdPageText);
            nfdText.append("\r");
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
                    etermWebClient.extendSessionExpireMillsec(5000, "PN");
                    cmdRs = etermWebClient.executePn();
                    if (!cmdRs.isSuccess()) {
                        errorMsg = cmdRs.getStatus().toString();
                        break;
                    } else {
                        nfdPageText = cmdRs.getObject();
                        if (StringUtils.isBlank(nfdPageText)) {
                            logger.error("unknow error");
                            break;
                        }
                    }
                    nfdPageText = nfdPageText.trim();
                    nfdPageText = nfdPageText.replaceAll("\n", "\r");
                    index = nfdPageText.lastIndexOf("\r");
                    pageString = nfdPageText.substring(index);
                    nfdPageText = nfdPageText.substring(0, index);
                    if (startEnd == 0) {
                        for (String string : strs) {
                            int find = nfdPageText.indexOf(string);
                            if (find != -1) {
                                startEnd++;
                                continue;
                            } else {
                                break;
                            }
                        }
                    }
                    for (int i = 0; i < startEnd; i++) {
                        nfdPageText = nfdPageText.substring(nfdPageText.indexOf("\r") + 1);
                    }
                    nfdPageText = nfdPageText.trim();
                    nfdText.append(nfdPageText);
                    nfdText.append("\r");
                }
            }

            // NFD文本转为NfdFareResponse,　并加入NFN
            nfdResponse = nfdText2Response(nfdText.toString(), etermWebClient, nfdRequest);
            nfdResponse.setSuccess(true);
            nfdResponse.setDeptAirport(nfdRequest.getDeptAirport());
            nfdResponse.setArrAirport(nfdRequest.getArrAirport());
            nfdResponse.setDeptDate(nfdRequest.getDeptDate());
            nfdResponse.setCarrier(nfdRequest.getCarrier());
            nfdResponse.setCabin(nfdRequest.getCabin());
        } catch (Exception e) {
            logger.error("bargainFareSearchWithNfnInternal error : "
                    + nfdCmd + "error:{" + e.getMessage() + "}", e);
            nfdResponse.setSuccess(false);
            nfdResponse.setErrorInfo("bargainFareSearchWithNfnInternal error: " + e.getMessage());
        } finally {
            if (etermWebClient != null) {
                etermWebClient.close();
            }
        }
        return nfdResponse;
    }

    private NfdFareResponseNew nfdText2Response(String nfdText, EtermWebClient etermWebClient, NfdFareRequest nfdRequest) 
    		throws SessionExpireException, JsonMappingException, JsonGenerationException, IOException {
    	NfdFareResponseNew nfdResponse = new NfdFareResponseNew();
        List<NfdFareResponse.NfdInfo> nfdInfos = new ArrayList<>();
        Matcher matcher = Pattern.compile("\r(?<line>(?<no>\\d+)  [A-Z]{2} .+)").matcher(nfdText);
        while (matcher.find()) {
            String index = matcher.group("no");
            String str = matcher.group("line");
            NfdFareResponse.NfdInfo nfdInfo = new NfdFareResponse.NfdInfo();
            nfdInfo.setNfdStr(str);
            if (nfdRequest.isNeedNfn01()) {
                String cmd = "NFN:" + index + "//01";
                String nfnResult = getNfn(cmd, etermWebClient);
                if (null != nfnResult)
                    nfdInfo.setNfn01Str(nfnResult);
            }
            if (nfdRequest.isNeedNfn02()) {
                String cmd = "NFN:" + index + "//02";
                String nfnResult = getNfn(cmd, etermWebClient);
                if (null != nfnResult)
                    nfdInfo.setNfn02Str(nfnResult);
            }
            if (nfdRequest.isNeedNfn04()) {
                String cmd = "NFN:" + index + "//04";
                String nfnResult = getNfn(cmd, etermWebClient);
                if (null != nfnResult)
                    nfdInfo.setNfn04Str(nfnResult);
            }
            if (nfdRequest.isNeedNfn05()) {
                String cmd = "NFN:" + index + "//05";
                String nfnResult = getNfn(cmd, etermWebClient);
                if (null != nfnResult)
                    nfdInfo.setNfn05Str(nfnResult);
            }
            if (nfdRequest.isNeedNfn06()) {
                String cmd = "NFN:" + index + "//06";
                String nfnResult = getNfn(cmd, etermWebClient);
                if (null != nfnResult)
                    nfdInfo.setNfn06Str(nfnResult);
            }
            if (nfdRequest.isNeedNfn08()) {
                String cmd = "NFN:" + index + "//08";
                String nfnResult = getNfn(cmd, etermWebClient);
                if (null != nfnResult)
                    nfdInfo.setNfn08Str(nfnResult);
            }
            if (nfdRequest.isNeedNfn09()) {
                String cmd = "NFN:" + index + "//09";
                String nfnResult = getNfn(cmd, etermWebClient);
                if (null != nfnResult)
                    nfdInfo.setNfn09Str(nfnResult);
            }
            if (nfdRequest.isNeedNfn11()) {
                String cmd = "NFN:" + index + "//11";
                String nfnResult = getNfn(cmd, etermWebClient);
                if (null != nfnResult)
                    nfdInfo.setNfn11Str(nfnResult);
            }
            nfdInfos.add(nfdInfo);
        }
        String nfdInfosJson = JsonUtil.toJson(nfdInfos, false);
        nfdResponse.setNfdInfos(nfdInfosJson);
        return nfdResponse;
    }

    private String getNfn(String cmd, EtermWebClient etermWebClient) throws SessionExpireException {
        etermWebClient.extendSessionExpireMillsec(5000, "NFN");

        // NFN翻页
        StringBuilder nfnText = new StringBuilder();
        String nfnPageText = null;
        try {
            int n = 1;
            String errorMsg = "";
            ReturnClass<String> nfnCmdRs = etermWebClient.executeCmdWithRetry(cmd, false);
            if (!nfnCmdRs.isSuccess()) {
                errorMsg = nfnCmdRs.getStatus().toString();
                logger.error(errorMsg);
                return null;
            } else {
                nfnPageText = nfnCmdRs.getObject();
                if (StringUtils.isBlank(nfnPageText)) {
                    logger.error("结果为空");
                    return null;
                }
            }

            nfnPageText = nfnPageText.trim();
            nfnPageText = nfnPageText.replaceAll("\n", "\r");
            int index = nfnPageText.lastIndexOf("\r");
            String pageString = nfnPageText.substring(index);
            nfnPageText = nfnPageText.substring(0, index);
            nfnPageText = nfnPageText.trim();
            String[] strs = nfnPageText.split("\r");
            int startEnd = 0;
            nfnText.append(nfnPageText);
            nfnText.append("\r");
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
                    etermWebClient.extendSessionExpireMillsec(5000, "PN");
                    nfnCmdRs = etermWebClient.executePn();
                    if (!nfnCmdRs.isSuccess()) {
                        errorMsg = nfnCmdRs.getStatus().toString();
                        break;
                    } else {
                        nfnPageText = nfnCmdRs.getObject();
                        if (StringUtils.isBlank(nfnPageText)) {
                            logger.error("unknow error");
                            break;
                        }
                    }
                    nfnPageText = nfnPageText.trim();
                    nfnPageText = nfnPageText.replaceAll("\n", "\r");
                    index = nfnPageText.lastIndexOf("\r");
                    pageString = nfnPageText.substring(index);
                    nfnPageText = nfnPageText.substring(0, index);
                    if (startEnd == 0) {
                        for (String string : strs) {
                            int find = nfnPageText.indexOf(string);
                            if (find != -1) {
                                startEnd++;
                                continue;
                            } else {
                                break;
                            }
                        }
                    }
                    for (int i = 0; i < startEnd; i++) {
                        nfnPageText = nfnPageText.substring(nfnPageText.indexOf("\r") + 1);
                    }
                    nfnPageText = nfnPageText.trim();
                    nfnText.append(nfnPageText);
                    nfnText.append("\r");
                }
            }
        } catch (Exception e) {
            logger.error("NFN search error : "
                    + cmd + "error:{" + e.getMessage() + "}", e);
            return null;
        }
        return nfnText.toString();
    }

    public static void main(String[] args) {
        NfdFareRequest nfdRequest = new NfdFareRequest();
        nfdRequest.setDeptAirport("SHA");
        nfdRequest.setArrAirport("PEK");
        nfdRequest.setDeptDate("2015-06-25");
        nfdRequest.setCarrier("CA");
        nfdRequest.setCabin(null);
        NfdFareService search = new NfdFareService();
        try {
            System.out.println(search.getNfd(nfdRequest));
        } catch (SessionExpireException e) {
            e.printStackTrace();
        }
    }

}
