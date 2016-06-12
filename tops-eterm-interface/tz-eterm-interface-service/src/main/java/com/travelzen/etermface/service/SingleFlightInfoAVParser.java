package com.travelzen.etermface.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.common.ufis.util.UfisException;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.common.PNRDateFormat;
import com.travelzen.etermface.service.constant.UfisStatus;
import com.travelzen.etermface.service.entity.FlightInfo;
import com.travelzen.etermface.service.entity.SingleFlightInfoRet;
import com.travelzen.framework.core.common.ReturnClass;

/**
 * @author hongqiang.mao
 * @date 2014-01-16 上午15:20:05
 * @description
 */
public class SingleFlightInfoAVParser{

    /**
     * DEP TIME ARR TIME WEEK FLY GROUND TERM TYPE MEAL DISTANCE 航班信息正则表达式 PEK
     * 0150 CDG 0540 THU 10:50 T2/2E 77W
     */
    private static final String FLIGHT_INFO_REGEX = "([A-Z]{3} *\\d{4}(\\+\\d{1}){0,1} *){2}[A-Z]{3}.*";

    private static final String META_REGEX = "DEP.*";

    private static Logger logger = LoggerFactory.getLogger(SingleFlightInfoAVParser.class);

    /**
     * 取消结果转化为XML
     *
     * @param flightNo
     * @param date
     * @return
     */
    public String getSingleFlightInfo(String flightNo, String date) throws SessionExpireException {
        SingleFlightInfoRet result = getSingleFlightInfoResult(flightNo, date);
        if (StringUtils.isNotBlank(result.getMessage()) && result.getMessage().startsWith("EXCEPTION")) {
            result = getSingleFlightInfoResult(flightNo, date);
        }
        return result.toXML();
    }

    /**
     * 取消PNR
     *
     * @param flightNo
     * @param date
     * @return
     */
    public SingleFlightInfoRet getSingleFlightInfoResult(String flightNo, String date) throws SessionExpireException {
        SingleFlightInfoRet result = new SingleFlightInfoRet();

        if (StringUtils.isBlank(flightNo)) {
            result.setSuccess(false);
            result.setMessage("请输入有效的航班号");
            return result;
        }

        String cmd = "AV:" + flightNo;
        String dayMonth = PNRDateFormat.dayMonthFormat(date);
        if (StringUtils.isNotBlank(dayMonth)) {
            cmd = cmd + "/" + dayMonth;
        }

        String avText = null;
        if (UfisStatus.active && UfisStatus.avFlight) {
        	EtermUfisClient client = null;
        	try {
        		client = new EtermUfisClient();
				avText = client.execCmd(cmd, false);
			} catch (UfisException e) {
				logger.error("Eterm av flight 指令 Ufis异常：" + e.getMessage(), e);
				result.setSuccess(false);
                result.setMessage("Ufis Exception:" + e.getMessage());
			} finally {
                client.close();
            }
        } else {
        	EtermWebClient lvEtermWebClient = new EtermWebClient();
            try {
                lvEtermWebClient.connect();
                ReturnClass<String> returnClass = lvEtermWebClient.executeCmdWithRetry(cmd, true);
                avText = returnClass.getObject();
            } catch (Exception e) {
                logger.info(e.getMessage());
                result.setSuccess(false);
                result.setMessage("EXCEPTION:" + e.getMessage());
            } finally {
                lvEtermWebClient.close();
            }
        }
        
        logger.info("CMD: " + cmd + "\nReturn --> " + avText);
        if (avText != null) {
        	List<FlightInfo> flightInfos = getFlightInfos(avText, flightNo, date);
            result.setSuccess(true);
            result.setFlightInfos(flightInfos);
            result.setAvResultStr(avText);
        }
        return result;
    }

    private static List<FlightInfo> getFlightInfos(String avStr, String flightNo, String date) {
        if (null == avStr) {
            return null;
        }

        List<FlightInfo> flightInfos = new ArrayList<FlightInfo>();
        avStr = avStr.replaceAll("\r", "\n");

        String[] strs = avStr.split("\n+");
        List<Integer> startIndexList = new ArrayList<Integer>();

        int lastIndex = 0;
        for (String str : strs) {
            if (str.matches(META_REGEX)) {
                lastIndex = 0;
                String[] ss = str.split(" +");
                for (String s : ss) {
                    int startIndex = str.indexOf(s, lastIndex);
                    startIndexList.add(startIndex);
                    lastIndex = startIndex;
                }
                break;
            }
        }

        for (String str : strs) {
            if (str.matches(FLIGHT_INFO_REGEX)) {
                int addDays = 0;
                int index = 0;
                FlightInfo flightInfo = new FlightInfo();
                flightInfos.add(flightInfo);

                String fromDate = date;

                String fromAirport = getSubStr(index++, startIndexList, str);

                String fromTime = getSubStr(index++, startIndexList, str);
                if (fromTime.contains("+")) {
                    int plusIndex = fromTime.indexOf("+");
                    try {
                        addDays = Integer.valueOf(fromTime.substring(plusIndex + 1));
                    } catch (Exception e) {
                    }
                    if (StringUtils.isNotBlank(date)) {
                        fromDate = PNRDateFormat.addDays(date, addDays);
                    }
                    fromTime = fromTime.substring(0, fromTime.length() - 2);
                }

                String arriAirport = getSubStr(index++, startIndexList, str);

                String arriTime = getSubStr(index++, startIndexList, str);
                String toDate = date;
                if (arriTime.contains("+")) {
                    int plusIndex = arriTime.indexOf("+");
                    try {
                        addDays = Integer.valueOf(arriTime.substring(plusIndex + 1));
                    } catch (Exception e) {
                    }

                    if (StringUtils.isNotBlank(date)) {
                        toDate = PNRDateFormat.addDays(date, addDays);
                    }
                    arriTime = arriTime.substring(0, arriTime.length() - 2);
                } else if (arriTime.compareTo(fromTime) < 0) {
                    addDays = 1;
                    toDate = PNRDateFormat.addDays(date, addDays);
                }
                String day = getSubStr(index++, startIndexList, str);
                String duration = getSubStr(index++, startIndexList, str);
                index++;
                String terms = getSubStr(index++, startIndexList, str);
                String plane = getSubStr(index++, startIndexList, str);
                String meal = getSubStr(index++, startIndexList, str);
                String distance = getSubStr(index++, startIndexList, str);
                int intDistance = 0;
                try {
                    if (StringUtils.isNotBlank(distance)) {
                        intDistance = Integer.valueOf(distance);
                    }
                } catch (NumberFormatException e) {
                    logger.error(e.getMessage());
                }

                String realFlightNo = getSubStr(index++, startIndexList, str);
                String fromTerminal = null;
                String arriTerminal = null;
                if (null != terms) {
                    if (terms.startsWith("/")) {
                        arriTerminal = terms.substring(1);
                    } else if (terms.endsWith("/")) {
                        fromTerminal = terms.substring(0, terms.length() - 1);
                    } else {
                        String[] termStrs = terms.split("/");
                        if (termStrs.length == 2) {
                            fromTerminal = termStrs[0];
                            arriTerminal = termStrs[1];
                        }
                    }
                }

                flightInfo.setFromAirPort(fromAirport);
                flightInfo.setToAirPort(arriAirport);
                flightInfo.setFromDate(fromDate);
                flightInfo.setFromTime(fromTime);
                flightInfo.setToDate(toDate);
                flightInfo.setToTime(arriTime);
                flightInfo.setDuration(duration);
                flightInfo.setPlaneModel(plane);

                flightInfo.setFromTerminal(fromTerminal);
                flightInfo.setToTerminal(arriTerminal);
                flightInfo.setDistance(intDistance);
                flightInfo.setMeal(meal);

                if (StringUtils.isNotBlank(flightNo)) {
                    flightInfo.setFlightNo(flightNo);
                    if (flightNo.length() > 2) {
                        flightInfo.setCarrier(flightNo.substring(0, 2));
                    }
                }

                if (StringUtils.isNotBlank(realFlightNo) && realFlightNo.length() > 2) {
                    flightInfo.setCodeShare(true);
                    flightInfo.setRealFlightNo(realFlightNo);
                    flightInfo.setRealCarrier(realFlightNo.substring(0, 2));
                } else {
                    flightInfo.setCodeShare(false);
                }
            }
        }

        return flightInfos;
    }

    private static String getSubStr(int index, List<Integer> startIndexList, String str) {
        if (null == startIndexList || index >= startIndexList.size()) {
            return null;
        }

        int beginIndex = startIndexList.get(index);

        int str_length = str.length();
        if (null == str || str_length < beginIndex) {
            return null;
        }

        int endIndex = str_length;
        if (index < startIndexList.size() - 1) {
            endIndex = startIndexList.get(index + 1);
        }

        if (endIndex > str_length) {
            endIndex = str_length;
        }

        return str.substring(beginIndex, endIndex).trim();
    }

    private static List<FlightInfo> getFlightInfos1(String avStr, String flightNo, String date) {
        if (null == avStr) {
            return null;
        }

        List<FlightInfo> flightInfos = new ArrayList<FlightInfo>();
        avStr = avStr.replaceAll("\r", "\n");

        String[] strs = avStr.split("\n+");
        int addDays = 0;
        for (String str : strs) {
            if (str.matches(FLIGHT_INFO_REGEX)) {
                FlightInfo flightInfo = new FlightInfo();
                flightInfos.add(flightInfo);
                String fromDate = date;
                String fromAirport = str.substring(0, 4).trim();
                String fromTime = str.substring(4, 11).trim();
                if (fromTime.contains("+")) {
                    int index = fromTime.indexOf("+");
                    try {
                        addDays = Integer.valueOf(fromTime.substring(index + 1));
                    } catch (Exception e) {
                    }
                    if (StringUtils.isNotBlank(date)) {
                        fromDate = PNRDateFormat.addDays(date, addDays);
                    }
                    fromTime = fromTime.substring(0, fromTime.length() - 2);
                }
                String arriAirport = str.substring(11, 15).trim();
                String arriTime = str.substring(15, 22).trim();
                String toDate = date;
                if (arriTime.contains("+")) {
                    int index = arriTime.indexOf("+");
                    try {
                        addDays = Integer.valueOf(arriTime.substring(index + 1));
                    } catch (Exception e) {
                    }

                    if (StringUtils.isNotBlank(date)) {
                        toDate = PNRDateFormat.addDays(date, addDays);
                    }
                    arriTime = arriTime.substring(0, arriTime.length() - 2);
                }
                String day = str.substring(22, 27).trim();
                String duration = null;
                String terms = null;
                String plane = null;
                String meal = null;
                String realFlightNo = null;
                String distance = null;
                int intDistance = 0;
                if (str.length() >= 33) {
                    duration = str.substring(27, 33).trim();
                }
                if (str.length() >= 46) {
                    terms = str.substring(40, 46).trim();
                }
                if (str.length() >= 51) {
                    plane = str.substring(46, 51).trim();
                }
                if (str.length() >= 57) {
                    meal = str.substring(51, 57).trim();
                }
                if (str.length() >= 65) {
                    distance = str.substring(57, 65).trim();
                    try {
                        intDistance = Integer.valueOf(distance);
                    } catch (NumberFormatException e) {
                        logger.error(e.getMessage());
                    }
                }

                if (str.length() >= 72) {
                    realFlightNo = str.substring(65, 72).trim();
                }

                String fromTerminal = null;
                String arriTerminal = null;
                if (null != terms) {
                    if (terms.startsWith("/")) {
                        arriTerminal = terms.substring(1);
                    } else if (terms.endsWith("/")) {
                        fromTerminal = terms.substring(0, terms.length() - 1);
                    } else {
                        String[] termStrs = terms.split("/");
                        if (termStrs.length == 2) {
                            fromTerminal = termStrs[0];
                            arriTerminal = termStrs[1];
                        }
                    }
                }

                flightInfo.setFromAirPort(fromAirport);
                flightInfo.setToAirPort(arriAirport);
                flightInfo.setFromDate(fromDate);
                flightInfo.setFromTime(fromTime);
                flightInfo.setToDate(toDate);
                flightInfo.setToTime(arriTime);
                flightInfo.setDuration(duration);
                flightInfo.setPlaneModel(plane);

                flightInfo.setFromTerminal(fromTerminal);
                flightInfo.setToTerminal(arriTerminal);
                flightInfo.setDistance(intDistance);
                flightInfo.setMeal(meal);

                if (StringUtils.isNotBlank(flightNo)) {
                    flightInfo.setFlightNo(flightNo);
                    if (flightNo.length() > 2) {
                        flightInfo.setCarrier(flightNo.substring(0, 2));
                    }
                }

                if (StringUtils.isNotBlank(realFlightNo) && realFlightNo.length() > 2) {
                    flightInfo.setCodeShare(true);
                    flightInfo.setRealFlightNo(realFlightNo);
                    flightInfo.setRealCarrier(realFlightNo.substring(0, 2));
                } else {
                    flightInfo.setCodeShare(false);
                }
            }
        }
        return flightInfos;
    }
}
