/**
 *
 */
package com.travelzen.etermface.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.common.PNRDateFormat;
import com.travelzen.etermface.service.constant.UfisStatus;
import com.travelzen.etermface.service.entity.FlightParam;
import com.travelzen.etermface.service.entity.QteBySegmentsParam;
import com.travelzen.etermface.service.entity.SegPrice;
import com.travelzen.etermface.service.entity.SegPriceSyntaxTree;
import com.travelzen.etermface.service.entity.qte.ExchangeRate;
import com.travelzen.etermface.service.entity.qte.QteBySegmentsResponse;
import com.travelzen.etermface.service.entity.qte.SegmentLimit;
import com.travelzen.framework.core.util.TZUtil;

/**
 * @author guohuaxue
 */
public class QteBySegmentsParser {
	
    private final static String NO_FARE = "NO FARES/RBD/CARRIER";
    private final static String NO_FARE_CHINESE = "无适用的运价";
    private final static String NO_INTERLINE_AGREEMENT = "NO INTERLINE AGREEMENT";
    private final static String FARE_START = "ATTN PRICED";
    private String requestKey = "";
    private final static String EORROR = "ERROR_";
    private static Logger logger = LoggerFactory.getLogger(QteBySegmentsParser.class);
    private Map<String, String> qteResultMap = new HashMap<String, String>();
    private Map<String, String> qteResultAirlineMap = new HashMap<String, String>();

    /**
     * @return
     * @throws SessionExpireException
     */
    public String getQteResultBySegementsParam(String qteBySegmentsParamString) {
    	logger.info("getQteResultBySegementsParam请求：{}", qteBySegmentsParamString);
        QteBySegmentsParam qteBySegmentsParam = QteBySegmentsParam.convertFromXML(qteBySegmentsParamString);
        requestKey = qteBySegmentsParam.getQteBySegmentsParamKey();
        try {
            // 正确的返回结果应该包含的信息
            List<String> needContains = new ArrayList<String>();
            Map<String, List<String>> passengerCommands = convertToBookCommand(qteBySegmentsParam, needContains);
            if (passengerCommands == null || passengerCommands.isEmpty()) {
                logger.error(requestKey + "没有获得可执行的命令：" + QteBySegmentsParam.convertToXML(qteBySegmentsParam));
                return "";
            } else {
                for (String passenger : passengerCommands.keySet()) {
                    List<String> commands = passengerCommands.get(passenger);
                    String qteResult = null;
                    int airlineIndex = 0;
                    for (String command : commands) {
                        qteResult = qteBySegments(command, needContains);
                        if (qteResult.equals(NO_FARE)) {
                            airlineIndex++;
                            continue;
                        } else {
                            break;
                        }
                    }
                    if(airlineIndex>= qteBySegmentsParam.getTicketingAirlineCompany().size()){
                    	airlineIndex--;
                    }
                    if (StringUtils.isNotBlank(qteResult)) {
                        qteResult = qteResult.replaceAll("\n", "\r");
                    } else {
                        qteResult = EORROR + "获取结果为空;";
                    }
                    qteResultMap.put(passenger, qteResult);
                    qteResultAirlineMap.put(passenger, qteBySegmentsParam.getTicketingAirlineCompany().get(airlineIndex));
                }
            }
        } catch (Exception e) {
            logger.error(requestKey + ";预料之外的错误：{}", TZUtil.stringifyException(e));
        }
        return parserQteResultMap(qteBySegmentsParam);
    }

    public String getQteResultByQteTxt(String qteResult) {
        if (StringUtils.isBlank(qteResult)) {
            return "";
        } else {
            qteResult = qteResult.replaceAll("\n", "\r");
        }
        QteBySegmentsResponse qteBySegmentsResponse = parserQteResult(qteResult);
        System.out.println(qteBySegmentsResponse);
        System.out.println();
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        return xstream.toXML(qteBySegmentsResponse);
    }

    /**
     * 解析报价结果
     * 
     * @param qteResultMap
     * @return
     */
    private String parserQteResultMap(QteBySegmentsParam qteBySegmentsParam) {
    	logger.info("parserQteResultMap解析报价结果请求：{}", qteBySegmentsParam);
        List<QteBySegmentsResponse> qteBySegmentsResponses = new ArrayList<>();
        if (qteResultMap == null || qteResultMap.size() == 0) {
            for (String passenger : qteBySegmentsParam.getPassengers()) {
                QteBySegmentsResponse qteBySegmentsResponse = new QteBySegmentsResponse();
                qteBySegmentsResponse.setErrorInfo("qteResultMap 为空");
                qteBySegmentsResponse.setPassengerType(passenger);
                qteBySegmentsResponses.add(qteBySegmentsResponse);
            }
        } else {
            for (String key : qteResultMap.keySet()) {
                QteBySegmentsResponse qteBySegmentsResponse = parserQteResult(qteResultMap.get(key));
                qteBySegmentsResponse.setPassengerType(key);
                if (key.equalsIgnoreCase("INF")) {
                    qteBySegmentsResponse.setBaseFare(0);
                    qteBySegmentsResponse.setTotalTax(0);
                    qteBySegmentsResponse.setTotalFare(0);
                }
                qteBySegmentsResponse.setTicketingAirlineCompany(qteResultAirlineMap.get(key));
                qteBySegmentsResponses.add(qteBySegmentsResponse);
            }
        }
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        xstream.processAnnotations(QteBySegmentsResponse.class);
        xstream.processAnnotations(SegmentLimit.class);
        xstream.processAnnotations(SegPriceSyntaxTree.class);
        xstream.processAnnotations(ExchangeRate.class);
        xstream.alias("qteBySegmentsResponses", List.class);
        xstream.alias("qteBySegmentsResponse", QteBySegmentsResponse.class);
        xstream.alias("exchangeRate", ExchangeRate.class);
        xstream.alias("segPriceSyntaxTree", SegPriceSyntaxTree.class);
        xstream.alias("segmentLimit", SegmentLimit.class);
        xstream.alias("segPrice", SegPrice.class);
        String result = xstream.toXML(qteBySegmentsResponses);
        logger.info("QTE解析结果：{}", result);
        return result;
    }

    /**
     * 处理单条解析结果
     * 
     * @param qteResult
     * @return
     */
    private QteBySegmentsResponse parserQteResult(String qteResult) {
    	logger.info("QTE解析单条请求：{}", qteResult);
        QteBySegmentsResponse qteBySegmentsResponse = new QteBySegmentsResponse();
        if (qteResult.startsWith(EORROR) || qteResult.startsWith(NO_FARE)) {
            qteBySegmentsResponse.setErrorInfo(qteResult);
            logger.info("QTE解析单条结果：{}", qteBySegmentsResponse);
            return qteBySegmentsResponse;
        }
        try {
            String[] strs = qteResult.split("\r");
            // 记录qteResult处理到第几行索引
            int index = 2;
            // 价格信息第一条应该是总价信息
            {
                String lastFareInfo = strs[0].trim();
                String[] strings = lastFareInfo.split("[ \\+]");
                if (strings.length <= 3) {
                    throw new Exception("最后一个价格寻找错误");
                }
                for (int i = strings.length - 1; i > 0; i--) {
                    Pattern p = Pattern.compile("[0-9]+.[0-9]+");
                    String str = strings[i].trim();
                    Matcher m = p.matcher(str);
                    if (m.matches()) {
                        qteBySegmentsResponse.setTotalFare(Integer.valueOf(str));
                        break;
                    }
                }
            }
            // Segement处理
            List<SegmentLimit> segmentLimits = new ArrayList<>();
            qteBySegmentsResponse.setSegmentLimits(segmentLimits);
            String formCity = strs[1].trim(); // qteResult 第一行为最开始的出发城市
            for (int i = index; i < strs.length; i++) {
                String str = strs[i];
                str = str.trim() + " ";
                if (str.startsWith("FARE")) {
                    index = i;
                    break;
                }
                // 处理数据样例： PUS YFFCK NVB NVA27JUL 2PC
                SegmentLimit segmentLimit = new SegmentLimit();
                segmentLimit.setFromCity(formCity);
                segmentLimits.add(segmentLimit);
                int processIndex = str.indexOf(" ");
                String toCity = str.substring(0, processIndex).trim();
                if (toCity.length() > 3) {
                    toCity = toCity.substring(toCity.length() - 3);
                }
                formCity = toCity;
                segmentLimit.setToCity(toCity);
                str = str.substring(processIndex + 1);

                // 没有nvb，认为是路面交通或者其他
                if (!str.contains("NVB")) {
                    segmentLimit.setMark(str.trim());
                    continue;
                }
                processIndex = str.indexOf("NVB");
                String fareBasis = str.substring(0, processIndex).trim();
                if (fareBasis.contains(" ")) {
                    int blankIndex = fareBasis.indexOf(" ");
                    String passengerdiscout = fareBasis.substring(blankIndex);
                    segmentLimit.setPassengerdiscout(passengerdiscout);
                    fareBasis = fareBasis.substring(0, blankIndex).trim();
                }
                segmentLimit.setFareBasis(fareBasis);
                str = str.substring(processIndex);

                processIndex = str.indexOf("NVA");
                String nvb = str.substring(3, processIndex).trim();
                segmentLimit.setNvb(nvb);
                str = str.substring(processIndex);

                processIndex = str.indexOf(" ");
                if (processIndex < 0) {
                    String nva = str.substring(3).trim();
                    segmentLimit.setNva(nva);
                } else {
                    String nva = str.substring(3, processIndex);
                    segmentLimit.setNva(nva);
                    str = str.substring(processIndex).trim();
                    segmentLimit.setLuggageLimit(str);
                }
            }

            // 票面价处理
            String baseFareInfo = strs[index].trim();
            index = index + 1;
            if (baseFareInfo.endsWith("FARE*")) {
                baseFareInfo = baseFareInfo.substring(0, baseFareInfo.length() - 1);
                int needIndex = baseFareInfo.lastIndexOf("*");
                if (needIndex <= 0) {
                    throw new Exception("qte fare处理失败");
                } else {
                    qteBySegmentsResponse.setItOrBt(baseFareInfo.substring(needIndex)+"*");
                    baseFareInfo = baseFareInfo.substring(0, needIndex).trim();
                }
            }
            String baseFareString = baseFareInfo.substring(baseFareInfo.lastIndexOf(" ") + 1);
            int baseFare = Integer.valueOf(baseFareString);// 验证确实或缺到的是数值数据
            qteBySegmentsResponse.setBaseFare(baseFare);

            // 稅收信息
            String taxDetail = strs[index].trim();
            index = index + 1;
            qteBySegmentsResponse.setTaxDetail(taxDetail);

            // 总价信息
            String totalFareInfo = strs[index].trim();
            int totalFare = 0;
            if (totalFareInfo.contains(" ") && totalFareInfo.contains("TOTAL")) {
                index = index + 1;
                String totalFareString = totalFareInfo.substring(totalFareInfo.lastIndexOf(" ") + 1);
                totalFare = Integer.valueOf(totalFareString);// 验证确实或缺到的是数值数据
                qteBySegmentsResponse.setTotalFare(totalFare);
            } else {
                totalFare = qteBySegmentsResponse.getTotalFare();
            }

            int totalTax = totalFare - baseFare;
            qteBySegmentsResponse.setTotalTax(totalTax);

            // nucinfo信息处理
            StringBuffer sb = new StringBuffer();
            for (int i = index; i < strs.length; i++) {
                String str = strs[i].trim();
                if (str.startsWith("XT ") || str.startsWith("ENDOS") || str.startsWith("RATE") || str.startsWith("*AUTO ")) {
                    index = i;
                    break;
                }
                // 一般Q值解析问题都处在这里：NucInfo字符串解析错误
                if (needBlank(sb.toString(), str))
                	sb.append(" ");
                if (sb.toString().endsWith("Q") && str.matches("[A-Z]{6}\\d+.+"))
                	sb.append(" ");
                if (str.matches("Q( [A-Z]{6})?\\d+.+"))
                	sb.append(" ");
                sb.append(str);
            }
            qteBySegmentsResponse.setNucInfo(sb.toString().trim());

            for (int i = index; i < strs.length; i++) {
                String str = strs[i].trim();
                if (str.startsWith("*AUTO ")) {
                    index = i;
                    break;
                }
                if (str.startsWith("XT ")) {
                    qteBySegmentsResponse.setTaxDetail(qteBySegmentsResponse.getTaxDetail() + "\n" + str);
                    continue;
                }
                if (str.startsWith("ENDOS")) {
                    if (StringUtils.isNotBlank(qteBySegmentsResponse.getEndos())) {
                        qteBySegmentsResponse.setEndos(qteBySegmentsResponse.getEndos() + "\n" + str);
                    } else {
                        qteBySegmentsResponse.setEndos(str);
                    }
                    continue;
                }
                if (str.startsWith("RATE")) {
                    Matcher matcher = Pattern.compile("1[A-Z]{3}=\\d+\\.\\d+[A-Z]{3}").matcher(str);
                    if (matcher.find()) {
                    	str = matcher.group();
                    }
                    String[] exchangeRateInfo = str.split("=");
                    ExchangeRate exchangeRate = new ExchangeRate();
                    exchangeRate.setPreCountryCode(exchangeRateInfo[0].substring(exchangeRateInfo[0].length() - 3));
                    str = exchangeRateInfo[1];
                    exchangeRate.setSuffixCountryCode(str.substring(str.length() - 3));
                    str = str.substring(0, str.length() - 3);
                    double rate = Double.valueOf(str);
                    exchangeRate.setRate(rate);
                    qteBySegmentsResponse.setExchangeRate(exchangeRate);
                    continue;
                }
            }

            // 包含 NET价格处理
            for (int i = index; i < strs.length; i++) {
                String str = strs[i].trim();
                if (str.startsWith("TOUR T ")) {
                    continue;
                }
                if (str.startsWith("NET CNY ")) {
                    qteBySegmentsResponse.setItOrBt("NET");
                    baseFareString = str.substring(str.lastIndexOf(" ") + 1);
                    baseFare = Integer.valueOf(baseFareString);// 验证确实或缺到的是数值数据
                    qteBySegmentsResponse.setBaseFare(baseFare);
                }

                if (str.startsWith("NET TOTAL CNY ")) {
                    String totalFareString = str.substring(str.lastIndexOf(" ") + 1);
                    totalFare = Integer.valueOf(totalFareString);// 验证确实或缺到的是数值数据
                    qteBySegmentsResponse.setTotalFare(totalFare);
                }
                if (str.startsWith("COMMISSION")) {
                    String commissionString = str.substring(11).trim();
                    commissionString = commissionString.substring(0, commissionString.indexOf(" "));
                    qteBySegmentsResponse.setCommission(commissionString);
                }
            }

            double rate = 1;
            if (qteBySegmentsResponse.getExchangeRate() != null && qteBySegmentsResponse.getExchangeRate().getRate() != 0) {
                rate = qteBySegmentsResponse.getExchangeRate().getRate();
            }
            int qValue = getQTotaolValueFromNuc(qteBySegmentsResponse.getNucInfo(), rate);
            qteBySegmentsResponse.setqValue(qValue);
            if (qValue != 0) {
                baseFare = baseFare - qValue;
                qteBySegmentsResponse.setBaseFare(baseFare);
            }
        } catch (Exception e) {
            logger.error("获取的结果跟预想中的不同：" + qteResult, e);
            qteBySegmentsResponse.setErrorInfo("获取的结果跟预想中的不同：" + qteResult + ";    " + e.getMessage());
            logger.info("QTE解析单条结果：{}", qteBySegmentsResponse);
            return qteBySegmentsResponse;
        }
        logger.info("QTE解析单条结果：{}", qteBySegmentsResponse);
        return qteBySegmentsResponse;
    }

    public static boolean needBlank(String front, String str) {
        boolean frontEndWithDouble = false;
        boolean strStartWithDouble = false;
        if (front.endsWith(".") || str.startsWith(".")) {
            return false;
        }
        for (int i = front.length() - 1; i > 0; i--) {
            char ch = front.charAt(i);
            if (Character.isDigit(ch)) {
                continue;
            } else if (ch == '.' && Character.isDigit(front.charAt(i - 1))) {
                frontEndWithDouble = true;
                break;
            } else {
                frontEndWithDouble = false;
                break;
            }
        }
        for (int i = 0; i < str.length() - 2; i++) {
            char ch = str.charAt(i);
            if (Character.isDigit(ch)) {
                continue;
            } else if (ch == '.' && Character.isDigit(str.charAt(i + 1))) {
                strStartWithDouble = true;
                break;
            } else {
                strStartWithDouble = false;
                break;
            }
        }

        if (frontEndWithDouble && strStartWithDouble) {
            return true;
        }
        return false;
    }

    private boolean auditCommandRs(String rs, List<String> needContains) {

        if (StringUtils.isBlank(rs)) {
            return false;
        }
        for (String str : needContains) {
            if (!rs.contains(str)) {
                return false;
            }
        }
        return true;
    }

    private String qteBySegments(String command, List<String> needContains) {
    	StringBuffer sb = new StringBuffer();
    	if (UfisStatus.active && UfisStatus.qteBySegments) {
    		EtermUfisClient client = null;
            try {
            	client = new EtermUfisClient(45);
                /**
                 * 撤销上次操作(还原)
                 */
                client.resume();

                String rtStr = client.execCmd(command, true);
                logger.info(requestKey + ";command-->" + command.toString() + "\nretrun-->" + rtStr);
                // 验证 rsStr
                boolean auditRs = auditCommandRs(rtStr, needContains);
                if (auditRs) {
                    sb.append(rtStr);
                } else {
                    return EORROR + "获取结果不是想要的结果：" + rtStr;
                }
                rtStr = rtStr.trim();

                // 当包含无价格信息时终止
                if (rtStr.contains(NO_FARE) || rtStr.contains(NO_FARE_CHINESE) || rtStr.contains(NO_INTERLINE_AGREEMENT)) {
                    return NO_FARE;
                }

                // 如果有+号，则翻页
                if (rtStr.endsWith("+")) {
                    rtStr = client.execCmd("pn");
                    logger.info(requestKey + ";command-->pn" + "\nretrun-->" + rtStr);
                    sb.append("\r");
                    sb.append(rtStr);
                }
                // 如果有多个价格则选择最后一个价格；通过FARE_SELECT_END寻找最后一个价格的信息
                String lastFare = selectLastFare(sb.toString());
                if (StringUtils.isBlank(lastFare)) {
                    logger.error(requestKey + "因为最后一个可用价格索引没有找到，认为查询失败。");
                    return EORROR + "因为最后一个可用价格索引没有找到，认为查询失败.";
                }
                String fareIndex = lastFare.substring(0, 2);
                if (fareIndex.equals("01")) {
                    String rs = sb.toString();
                    int index = rs.indexOf(FARE_START);
                    if (index == -1) {
                        throw new Exception("FARE_START是错误的");
                    }
                    rs = rs.substring(index);
                    index = rs.indexOf("\r");
                    rs = rs.substring(index + 1);
                    sb.delete(0, sb.length());
                    sb.append(lastFare);
                    sb.append("\r");
                    sb.append(rs);
                } else {
                    rtStr = client.execCmd("XS FSQ" + fareIndex);
                    logger.info(requestKey + ";command-->XS FSQ" + fareIndex + "\nretrun-->" + rtStr);
                    sb.delete(0, sb.length());
                    sb.append(lastFare);
                    sb.append("\r");
                    sb.append(rtStr);
                    // 如果有+号，则翻页
                    if (rtStr.endsWith("+")) {
                        rtStr = client.execCmd("pn");
                        logger.info(requestKey + ";command-->pn" + "\nretrun-->" + rtStr);
                        sb.append("\r");
                        sb.append(rtStr);
                    }
                }
            } catch (Exception e) {
                sb.delete(0, sb.length());
                logger.error(requestKey + ";qteBySegments err:{}", TZUtil.stringifyException(e));
                return EORROR + e.getMessage();
            } finally {
                client.close();
            }
    	} else {
    		EtermWebClient etermWebClient = new EtermWebClient();
            try {
                etermWebClient.connect(45000);
                /**
                 * 撤销上次操作(还原)
                 */
                etermWebClient.resume();

                String rtStr = etermWebClient.executeCmdWithRetry(command, false).getObject();
                logger.info(requestKey + ";command-->" + command.toString() + "\nretrun-->" + rtStr);
                // 验证 rsStr
                boolean auditRs = auditCommandRs(rtStr, needContains);
                if (auditRs) {
                    sb.append(rtStr);
                } else {
                    return EORROR + "获取结果不是想要的结果：" + rtStr;
                }
                rtStr = rtStr.trim();

                // 当包含无价格信息时终止
                if (rtStr.contains(NO_FARE) || rtStr.contains(NO_FARE_CHINESE) || rtStr.contains(NO_INTERLINE_AGREEMENT)) {
                    return NO_FARE;
                }

                // 如果有+号，则翻页
                if (rtStr.endsWith("+")) {
                    rtStr = etermWebClient.rawExecuteCmd("pn").getObject();
                    logger.info(requestKey + ";command-->pn" + "\nretrun-->" + rtStr);
                    sb.append("\r");
                    sb.append(rtStr);
                }
                // 如果有多个价格则选择最后一个价格；通过FARE_SELECT_END寻找最后一个价格的信息
                String lastFare = selectLastFare(sb.toString());
                if (StringUtils.isBlank(lastFare)) {
                    logger.error(requestKey + "因为最后一个可用价格索引没有找到，认为查询失败。");
                    return EORROR + "因为最后一个可用价格索引没有找到，认为查询失败.";
                }
                String fareIndex = lastFare.substring(0, 2);
                if (fareIndex.equals("01")) {
                    String rs = sb.toString();
                    int index = rs.indexOf(FARE_START);
                    if (index == -1) {
                        throw new Exception("FARE_START是错误的");
                    }
                    rs = rs.substring(index);
                    index = rs.indexOf("\r");
                    rs = rs.substring(index + 1);
                    sb.delete(0, sb.length());
                    sb.append(lastFare);
                    sb.append("\r");
                    sb.append(rs);
                } else {
                    rtStr = etermWebClient.rawExecuteCmd("XS FSQ" + fareIndex).getObject();
                    logger.info(requestKey + ";command-->XS FSQ" + fareIndex + "\nretrun-->" + rtStr);
                    sb.delete(0, sb.length());
                    sb.append(lastFare);
                    sb.append("\r");
                    sb.append(rtStr);
                    // 如果有+号，则翻页
                    if (rtStr.endsWith("+")) {
                        rtStr = etermWebClient.rawExecuteCmd("pn").getObject();
                        logger.info(requestKey + ";command-->pn" + "\nretrun-->" + rtStr);
                        sb.append("\r");
                        sb.append(rtStr);
                    }
                }
            } catch (Exception e) {
                sb.delete(0, sb.length());
                logger.error(requestKey + ";qteBySegments err:{}", TZUtil.stringifyException(e));
                return EORROR + e.getMessage();
            } catch (SessionExpireException e) {
                return EORROR + "配置获取问题;";
            } finally {
                etermWebClient.close();
            }
    	}
        logger.info("查询结果：" + sb.toString());
        return sb.toString();
    }

    /**
     * // 如果有多个价格则选择最后一个价格；通过FARE_SELECT_END寻找最后一个价格
     * 
     * @param rs
     * @return
     */
    private String selectLastFare(String rs) {
        int index = rs.indexOf(FARE_START);
        if (index == -1 || index < 3) {
            logger.error("没有找到FARE_START");
            return "";
        }
        rs = rs.substring(0, index - 2).trim();
        String[] str = rs.split("\r");
        String result = "";
        for (int i = str.length - 1; i >= 0; i--) {
            rs = str[i].trim();
            if (rs.length() < 2) {
                continue;
            }
            if (Character.isDigit(rs.charAt(0)) && Character.isDigit(rs.charAt(1))) {
                result = rs;
                break;
            }
        }
        if (StringUtils.isBlank(result)) {
            logger.error("没有找到最后一个价格信息");
            return "";
        }
        String fareIndex = result.substring(0, 2);
        logger.info("最后一个价格索引序号是：" + fareIndex);
        try {
            index = Integer.valueOf(fareIndex);
        } catch (Exception e) {
            logger.info("最后一个价格索引序号寻找失败：" + fareIndex, e.getMessage());
            return "";
        }
        return result;
    }

    /**
     * 将输入的参数列表转化成对应的SS命令
     * 
     * @return
     */

    private Map<String, List<String>> convertToBookCommand(QteBySegmentsParam qteBySegmentsParam, List<String> needContains) {
        if (null == qteBySegmentsParam) {
            return null;
        }
        if (qteBySegmentsParam.getQteParams() == null || qteBySegmentsParam.getQteParams().size() == 0) {
            return null;
        }

        StringBuffer resultBuffer = new StringBuffer();
        for (int i = 0; i < qteBySegmentsParam.getQteParams().size(); i++) {
            FlightParam flightParam = qteBySegmentsParam.getQteParams().get(i);
            if (StringUtils.isBlank(flightParam.getAirCompany())) {
                return null;
            }

            if (StringUtils.isBlank(flightParam.getSmallCabin())) {
                return null;
            }
            if (StringUtils.isBlank(flightParam.getFromAirPort())) {
                return null;
            }

            if (StringUtils.isBlank(flightParam.getToAirPort())) {
                return null;
            }
            if (flightParam.isOpen()) {
                resultBuffer.append("O ");
                resultBuffer.append(flightParam.getAirCompany());
                resultBuffer.append(" ");
                resultBuffer.append(flightParam.getSmallCabin());
                resultBuffer.append(" ");
                resultBuffer.append(flightParam.getFromAirPort());
                resultBuffer.append(" ");
                resultBuffer.append(flightParam.getToAirPort());
                resultBuffer.append("0");
                String sOrX = "S";
                if (i != qteBySegmentsParam.getQteParams().size() - 1) {
                    FlightParam nextFlightParam = qteBySegmentsParam.getQteParams().get(i + 1);
                    if (nextFlightParam.isOpen()) {
                        sOrX = "X";
                    }
                }
                resultBuffer.append(sOrX);
            } else {
                if (StringUtils.isBlank(flightParam.getFlightNum())) {
                    return null;
                }
                String depatureDateString = PNRDateFormat.dayMonthFormat(flightParam.getDepatureDate());
                if (StringUtils.isBlank(depatureDateString)) {
                    return null;
                }

                String arriveDateString = PNRDateFormat.dayMonthFormat(flightParam.getArriveDate());
                if (StringUtils.isBlank(arriveDateString)) {
                    return null;
                }
                String valueBetweenTime = " ";
                String sOrX = "S";
                if (i != qteBySegmentsParam.getQteParams().size() - 1) {
                    FlightParam nextFlightParam = qteBySegmentsParam.getQteParams().get(i + 1);
                    if (!nextFlightParam.isOpen()) {
                        try {
                            Date arriveDate = DateUtils.parseDate(flightParam.getArriveDate() + flightParam.getArriveTime(), "yyyy-MM-ddHH:mm");
                            Date depatureDate = DateUtils.parseDate(nextFlightParam.getDepatureDate() + nextFlightParam.getDepatureTime(),
                                    "yyyy-MM-ddHH:mm");
                            long timeInterval = depatureDate.getTime() - arriveDate.getTime();
                            if (timeInterval <= 86400000) {
                                sOrX = "X";
                            }
                        } catch (ParseException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                    // if (nextFlightParam.isOpen()) {
                    // sOrX = "S";
                    // } else {
                    // try {
                    // Date arriveDate =
                    // DateUtils.parseDate(flightParam.getArriveDate() +
                    // flightParam.getArriveTime(), "yyyy-MM-ddHH:mm");
                    // Date depatureDate =
                    // DateUtils.parseDate(nextFlightParam.getDepatureDate() +
                    // nextFlightParam.getDepatureTime(),
                    // "yyyy-MM-ddHH:mm");
                    // long timeInterval = depatureDate.getTime() -
                    // arriveDate.getTime();
                    // if (timeInterval <= 86400000) {
                    // sOrX = "X";
                    // } else {
                    // sOrX = "S";
                    // }
                    // } catch (ParseException e) {
                    // logger.error(e.getMessage(), e);
                    // }
                    // }

                }
                if (flightParam.getDepatureDate().equals(flightParam.getArriveDate())) {
                    valueBetweenTime = " ";
                } else {
                    try {
                        Date depatureDate = DateUtils.parseDate(flightParam.getDepatureDate(), "yyyy-MM-dd");
                        Date arriveDate = DateUtils.parseDate(flightParam.getArriveDate(), "yyyy-MM-dd");
                        long timeInterval = arriveDate.getTime() - depatureDate.getTime();
                        if (timeInterval < 0) {
                            valueBetweenTime = "<";
                        } else if (timeInterval == 86400000) {
                            valueBetweenTime = ">";
                        } else {
                            valueBetweenTime = "+";
                        }
                    } catch (ParseException e) {
                        return null;
                    }

                }

                String depatureTime = PNRDateFormat.timeFormat(flightParam.getDepatureTime());
                if (StringUtils.isBlank(depatureTime)) {
                    return null;
                }
                String arriveTime = PNRDateFormat.timeFormat(flightParam.getArriveTime());
                if (StringUtils.isBlank(arriveTime)) {
                    return null;
                }
                // 如果机型为空
                if (StringUtils.isBlank(flightParam.getPlaneType())) {
                    flightParam.setPlaneType("");
                }
                needContains.add(flightParam.getAirCompany());
                needContains.add(flightParam.getFlightNum() + flightParam.getSmallCabin() + depatureDateString);
                needContains.add(flightParam.getFromAirPort() + depatureTime);
                needContains.add(arriveTime + flightParam.getToAirPort());

                resultBuffer.append("S ");
                resultBuffer.append(flightParam.getAirCompany());
                resultBuffer.append(" ");
                resultBuffer.append(flightParam.getFlightNum());
                resultBuffer.append(flightParam.getSmallCabin());
                resultBuffer.append(depatureDateString);
                resultBuffer.append(" ");
                resultBuffer.append(flightParam.getFromAirPort());
                resultBuffer.append(depatureTime);
                resultBuffer.append(valueBetweenTime);
                resultBuffer.append(arriveTime);
                resultBuffer.append(flightParam.getToAirPort());
                resultBuffer.append("0");
                resultBuffer.append(sOrX);
                resultBuffer.append("  ");
                resultBuffer.append(flightParam.getPlaneType());
                if (StringUtils.isNotBlank(flightParam.getCodeShareAirCompany())) {
                    resultBuffer.append(" #O");
                    resultBuffer.append(flightParam.getCodeShareAirCompany());
                }
            }
            resultBuffer.append("\r");
        }

        if (qteBySegmentsParam.getPassengers() == null || qteBySegmentsParam.getPassengers().size() == 0) {
            List<String> passengers = new ArrayList<String>();
            passengers.add("ADT");
            passengers.add("CHD");
            qteBySegmentsParam.setPassengers(passengers);
        }
        Map<String, List<String>> commandMap = new HashMap<String, List<String>>();
        for (String passenger : qteBySegmentsParam.getPassengers()) {
            String prefix = "XS FSI";
            if ("CHD".equals(passenger)) {
                prefix = prefix + "CH";
            } else if ("INF".equals(passenger)) {
                prefix = prefix + "IN";
            }
            List<String> commands = new ArrayList<String>();
            for (String airCompany : qteBySegmentsParam.getTicketingAirlineCompany()) {
                airCompany = airCompanyProcess(airCompany, qteBySegmentsParam.getQteParams().get(0).getFromAirPort());
                commands.add(prefix + "/" + airCompany + "\r" + resultBuffer);
            }
            commandMap.put(passenger, commands);
        }
        return commandMap;
    }

    private static String airCompanyProcess(String airCompany, String fromAirPort) {
        if ("TG".equals(airCompany) && (fromAirPort.equalsIgnoreCase("SHA") || fromAirPort.equals("PVG"))) {
            return airCompany + "/nego";
        } else {
            return airCompany;
        }
    }

    public static int getQTotaolValueFromNuc(String nucInfo, double rate) {
    	logger.info("开始Q值解析, nucInfo: {}", nucInfo);
        if (StringUtils.isBlank(nucInfo)) {
            return 0;
        }
        // nucInfo = nucInfo.replaceAll("\n", " ");
        // System.out.println(nucInfo);
        Pattern specialQFormat = Pattern.compile(" (Q *([A-Z]{6})([0-9]+\\.[0-9]+))+");
        Pattern standardQFormat = Pattern.compile("(Q([0-9]+\\.[0-9]+))+");
        Pattern doubleValue = Pattern.compile("[0-9]+\\.[0-9]+");
        nucInfo = replaceAllSpecialQValue(nucInfo, specialQFormat, doubleValue);
        String[] strs = nucInfo.split(" ");
        List<Double> qList = new ArrayList<Double>();
        for (int i = 0; i < strs.length; i++) {
            String str = strs[i].trim();
            if (!str.startsWith("Q")) {
                continue;
            }
            str = deleteNonNumericCharInEnd(str);
            Matcher m = standardQFormat.matcher(str);
            if (m.matches()) {
                Matcher mValue = doubleValue.matcher(str);
                while (mValue.find()) {
                    String qValue = mValue.group();
                    qList.add(Double.valueOf(qValue));
                }
            }
        }
        logger.info("提取出的Q值列表: {}", qList);
        double roe = 1;
        if (nucInfo.contains("ROE")) {
            String roeString = nucInfo.substring(nucInfo.lastIndexOf("ROE") + 3);
            roeString = roeString.replaceAll(" ", "");
            try {
                roe = Double.valueOf(roeString);
            } catch (Exception e) {
                roe = 1;
            }
        }
        int totalQValue = 0;
        for (double d : qList) {
            d = d * roe * rate;
            d = Math.ceil((d / 10)) * 10;
            totalQValue = totalQValue + (int)d;
        }
        logger.info("最终返回的税款: {}", totalQValue);
        return totalQValue;
    }

    // 当前需要特别处理的格式： 去掉Q和Q值之间的非数字字符(Q SHATYO57.07 )
    private static String replaceAllSpecialQValue(String nucInfo, Pattern specialQFormat, Pattern doubleValue) {
        Map<String, String> replacePairs = new HashMap<String, String>();
        Matcher m = specialQFormat.matcher(nucInfo);
        while (m.find()) {
            String specialQ = m.group();
            Matcher mValue = doubleValue.matcher(specialQ);
            String qValueRs = "";
            while (mValue.find()) {
                String qValue = mValue.group();
                qValueRs = qValueRs + " Q" + qValue;
            }
            replacePairs.put(specialQ, qValueRs);
        }
        for (String key : replacePairs.keySet()) {
            nucInfo = nucInfo.replaceAll(key, replacePairs.get(key));
        }
        return nucInfo;
    }

    private static String deleteNonNumericCharInEnd(String str) {
        int i = 0;
        // 找到Q值结尾
        while (i < str.length()) {
            if (isNum(str.charAt(i)) || str.charAt(i) == 'Q' || str.charAt(i) == '.') {
                i++;
                continue;
            } else {
                break;
            }
        }
        if (i < str.length() && str.charAt(i - 1) == 'Q') {
            i--;
        }
        int endQ = i;
        return str.substring(0, endQ);
    }

    private static boolean isNum(char n) {
        return ('0' <= n && n <= '9');
    }
    
    public static void main(String[] args) {
		String s = "02 MFETZVAR+*          15419 CNY                    INCL TAX\n"
				+ "BJS     \n"
				+ "XSEA MFETZVAR                          NVB01NOV15 NVA01NOV15 2PC  \n"
				+ " ATL MFETZVAR                          NVB01NOV15 NVA01NOV15 2PC  \n"
				+ "XSEA XLETZSUL                          NVB06NOV15 NVA06NOV15 2PC  \n"
				+ " BJS XLETZSUL                          NVB06NOV15 NVA06NOV15 2PC  \n"
				+ "FARE  CNY   12660     \n"
				+ "TAX   CNY      90CN CNY      72AY CNY    2597XT   \n"
				+ "TOTAL CNY   15419     \n"
				+ "01NOV15BJS DL X/SEA DL ATL M1713.82DL X/SEA DL BJS M273.20NU  \n"
				+ "C1987.02END ROE6.368800   \n"
				+ "XT CNY 226US CNY 32XA CNY 45XY CNY 35YC CNY 2230YR    \n"
				+ "XT CNY 29XFSEA4.5     \n"
				+ "ENDOS 01,02 *REF/CHANGE PENALTIES APPLY   \n"
				+ "ENDOS 03,04 *NONREF/PENALTY APPLIES   \n"
				+ "*AUTO BAGGAGE INFORMATION AVAILABLE - SEE FSB     \n"
				+ "TKT/TL31OCT15*1328    \n"
				+ "RFSONLN/1E /EFEP_19/FCC=W/ \n";
		new QteBySegmentsParser().getQteResultByQteTxt(s);
		String s2 = "02 MFETZVAR+*          15419 CNY                    INCL TAX\n"
				+ "BJS     \n"
				+ "XSEA MFETZVAR                          NVB01NOV15 NVA01NOV15 2PC  \n"
				+ " ATL MFETZVAR                          NVB01NOV15 NVA01NOV15 2PC  \n"
				+ "XSEA XLETZSUL                          NVB06NOV15 NVA06NOV15 2PC  \n"
				+ " BJS XLETZSUL                          NVB06NOV15 NVA06NOV15 2PC  \n"
				+ "FARE  CNY   12660     \n"
				+ "TAX   CNY      90CN CNY      72AY CNY    2597XT   \n"
				+ "TOTAL CNY   15419     \n"
				+ "20DEC15SHA AM X/TIJ AM MTY Q SHAMTY179.00M/BT //MEX AM SHA Q \n"
				+ " MTYSHA179.00M/BT END ROE6.368800\n"
				+ "XT CNY 226US CNY 32XA CNY 45XY CNY 35YC CNY 2230YR    \n"
				+ "XT CNY 29XFSEA4.5     \n"
				+ "ENDOS 01,02 *REF/CHANGE PENALTIES APPLY   \n"
				+ "ENDOS 03,04 *NONREF/PENALTY APPLIES   \n"
				+ "*AUTO BAGGAGE INFORMATION AVAILABLE - SEE FSB     \n"
				+ "TKT/TL31OCT15*1328    \n"
				+ "RFSONLN/1E /EFEP_19/FCC=W/ \n";
		new QteBySegmentsParser().getQteResultByQteTxt(s2);
		String s3 = "02 MFETZVAR+*          15419 CNY                    INCL TAX\n"
				+ "BJS     \n"
				+ "XSEA MFETZVAR                          NVB01NOV15 NVA01NOV15 2PC  \n"
				+ " ATL MFETZVAR                          NVB01NOV15 NVA01NOV15 2PC  \n"
				+ "XSEA XLETZSUL                          NVB06NOV15 NVA06NOV15 2PC  \n"
				+ " BJS XLETZSUL                          NVB06NOV15 NVA06NOV15 2PC  \n"
				+ "FARE  CNY   12660     \n"
				+ "TAX   CNY      90CN CNY      72AY CNY    2597XT   \n"
				+ "TOTAL CNY   15419     \n"
				+ "31JAN16SHA AM X/TIJ AM MTY Q SHAMTY179.00 787.43/-MEX AM SHA  \n"
				+ " Q179.00 447.49NUC1592.92END ROE6.368800 \n"
				+ "XT CNY 226US CNY 32XA CNY 45XY CNY 35YC CNY 2230YR    \n"
				+ "XT CNY 29XFSEA4.5     \n"
				+ "ENDOS 01,02 *REF/CHANGE PENALTIES APPLY   \n"
				+ "ENDOS 03,04 *NONREF/PENALTY APPLIES   \n"
				+ "*AUTO BAGGAGE INFORMATION AVAILABLE - SEE FSB     \n"
				+ "TKT/TL31OCT15*1328    \n"
				+ "RFSONLN/1E /EFEP_19/FCC=W/ \n";
		new QteBySegmentsParser().getQteResultByQteTxt(s3);
		String s4 = "01 NSRWCH+YOW           5647 CNY                    INCL TAX                    \n"
				+ "*SYSTEM DEFAULT-CHECK OPERATING CARRIER                                         \n"
				+ "*INTERLINE AGREEMENT PRICING APPLIED                                            \n"
				+ "*TKT STOCK RESTR                                                                \n"
				+ "*ATTN PRICED ON 08JAN16*1531                                                    \n"
				+ " SHA                                                                            \n"
				+ " HKG NSRWCH                            NVB03FEB16 NVA03FEB16 20K                \n"
				+ "XSEL YOW                               NVB        NVA03FEB17 1PC                \n"
				+ " TYO YOW                               NVB        NVA03FEB17 1PC                \n"
				+ "FARE  CNY    5360                                                               \n"
				+ "TAX   CNY      90CN CNY     102HK CNY      95XT                                 \n"
				+ "TOTAL CNY    5647                                                               \n"
				+ "03FEB16SHA MU HKG186.96OZ X/SEL Q5.80OZ TYO Q HKGTYO24.90M61                    \n"
				+ "6.75NUC834.41END ROE6.418450                                                    \n"
				+ "XT CNY 55BP CNY 40YQ                                                            \n"
				+ "ENDOS 01 *Q/NON-END/RER.                                                        \n"
				+ "ENDOS 01 *RFD/CHG CNY200                                                        \n"
				+ "*AUTO BAGGAGE INFORMATION AVAILABLE - SEE FSB                                   \n"
				+ "RFSONLN/1E /EFEP_36/FCC=T/";
		new QteBySegmentsParser().getQteResultByQteTxt(s4);
		String s5 = "01 A0LNCD               5954 CNY                    INCL TAX  \n"
				+ "*SYSTEM DEFAULT-CHECK OPERATING CARRIER   \n"
				+ "*CA CHECKIN TAX MAY APPLY. SEE FXT/CA/--  \n"
				+ "*CHECK RULE FOR PAYMENT/TICKETING CONDITIONS  \n"
				+ "*TKT STOCK RESTR  \n"
				+ "*仅限电子票   \n"
				+ "*ATTN PRICED ON 25FEB16*1149  \n"
				+ " SHA  \n"
				+ " YVR A0LNCD                            NVB29APR16 NVA29APR16 2PC  \n"
				+ " YXE A0LNCD                            NVB02MAY16 NVA02MAY16 2PC  \n"
				+ "XYVR A0LNCD                            NVB16MAY16 NVA16MAY16 2PC  \n"
				+ " SHA A0LNCD                            NVB16MAY16 NVA16MAY16 2PC  \n"
				+ "FARE  CNY    3350     \n"
				+ "TAX   CNY      90CN CNY     124CA CNY    2390XT   \n"
				+ "TOTAL CNY    5954     \n"
				+ "29APR16SHA AC YVR AC YXE Q SHAYXE11.11 210.33AC X/YVR AC SHA  \n"
				+ " Q YXESHA11.11 210.33 1S77.90NUC520.78END ROE6.418450     \n"
				+ "XT CNY 190SQ CNY 10XG CNY 2190YQ                                               \n"
				+ "ENDOS *REFUNDABLE/CXLFEE/CHGFEE                                                \n"
				+ "*AUTO BAGGAGE INFORMATION AVAILABLE - SEE FSB   \n"
				+ "*COMMISSION VALIDATED - DATA SOURCE TRAVELSKY   \n"
				+ "TKT/TL26FEB16*1149  \n"
				+ "COMMISSION  3.00 PERCENT OF GROSS   \n"
				+ "RFSONLN/1E /EFEP_37/FCC=W/";
		new QteBySegmentsParser().getQteResultByQteTxt(s5);
	}

}
