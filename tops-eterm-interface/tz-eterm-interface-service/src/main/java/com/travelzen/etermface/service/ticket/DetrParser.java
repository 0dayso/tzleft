package com.travelzen.etermface.service.ticket;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.common.ufis.util.UfisException;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.EtermUfisClient;
import com.travelzen.etermface.service.EtermWebClient;
import com.travelzen.etermface.service.constant.PnrParserConstant;
import com.travelzen.etermface.service.constant.UfisStatus;
import com.travelzen.etermface.service.entity.DetrResult;
import com.travelzen.etermface.service.entity.ParseConfBean;
import com.travelzen.etermface.service.entity.config.FullDetrConfig;
import com.travelzen.framework.core.common.ReturnClass;
import com.travelzen.framework.core.common.ReturnCode;

public class DetrParser {

    private static Logger logger = LoggerFactory.getLogger(DetrParser.class);

    private String officeId = null;

    public DetrParser() {
    }

    public DetrParser(ParseConfBean confBean) {
        officeId = confBean.getOfficeId();
    }

    private String getRawResultStr(String tktNumber) {
    	String cmd = "detr: TN/" + tktNumber;
    	String ret = null;
    	if (UfisStatus.active && UfisStatus.detr) {
    		EtermUfisClient client = null;
        	try {
        		client = new EtermUfisClient(officeId);
				ret = client.execCmd(cmd, true);
			} catch (UfisException e) {
				logger.error(e.getMessage(), e);
			} finally {
				if (null != client)
        			client.close();
			}
    	} else {
    		EtermWebClient client = new EtermWebClient(officeId);
    		client.connect();
    		try {
				ret = client.executeCmdWithRetry(cmd, false).getObject();
			} catch (SessionExpireException e) {
				logger.error(e.getMessage(), e);
			} finally {
				client.close();
			}
    	}
        logger.info("detr:{} \n{}", tktNumber, ret);
        return ret;
    }

    private ReturnClass<DetrResult> rawParse(String detrStr) {
        ReturnClass<DetrResult> retDetrResult = new ReturnClass<>();
        DetrResult fullDetrResult = new DetrResult();
        String[] lines = StringUtils.split(detrStr, "[\r|\n]+");
        PROC:
        for (int i = 0; i < lines.length; i++) {

            final String CONJ_TKT = "CONJ TKT:";
            if (StringUtils.contains(lines[i], CONJ_TKT)) {
                int idx = lines[i].indexOf(CONJ_TKT);
                fullDetrResult.conjTkt = StringUtils.trim(lines[i].substring(idx + CONJ_TKT.length()));
                continue PROC;
            }

            final String ER = "E/R:";
            if (StringUtils.contains(lines[i], ER)) {
                int idx = lines[i].indexOf(ER);
                fullDetrResult.er = StringUtils.trim(lines[i].substring(idx + ER.length()));
                continue PROC;
            }

            final String TOUR_CODE = "TOUR CODE:";
            if (StringUtils.contains(lines[i], TOUR_CODE)) {
                int idx = lines[i].indexOf(TOUR_CODE);

                fullDetrResult.tourCode = StringUtils.trim(lines[i].substring(idx + TOUR_CODE.length()));
                continue PROC;
            }

            final String PASSENGER = "PASSENGER:";
            if (StringUtils.contains(lines[i], PASSENGER)) {
                int idx = lines[i].indexOf(PASSENGER);

                fullDetrResult.name = StringUtils.trim(lines[i].substring(idx + PASSENGER.length()));
                continue PROC;
            }

            final String FARE_CALCULATION = "FC:";
            if (StringUtils.contains(lines[i], FARE_CALCULATION)) {
                int idx = lines[i].indexOf(FARE_CALCULATION);

                if (StringUtils.isBlank(fullDetrResult.fc)) {
                    fullDetrResult.fc = "";
                }

                fullDetrResult.fc += StringUtils.trim(lines[i].substring(idx + FARE_CALCULATION.length()));
                if (i + 1 < lines.length && !StringUtils.contains(lines[i + 1], "FARE:")) {
                    lines[i + 1] = "FC:" + lines[i + 1];
                }

                continue PROC;
            }

            final String FORM_OF_PAYMENT = "FOP:";
            if (StringUtils.contains(lines[i], FORM_OF_PAYMENT)) {
                int idx = lines[i].indexOf(FORM_OF_PAYMENT);

                fullDetrResult.fop = StringUtils.trim(lines[i].substring(idx + FORM_OF_PAYMENT.length()));
                // /continue PROC;
            }

            final String TAX = "TAX:";
            if (StringUtils.startsWith(lines[i], TAX)) {
                int idx = lines[i].indexOf(TAX);

                parseTaxString(lines[i].substring(idx + TAX.length()), fullDetrResult.taxs);
                continue PROC;
            }

            final String FARE = "FARE:";
            if (StringUtils.contains(lines[i], FARE)) {
                int idx = lines[i].indexOf(FARE);

                String value = StringUtils.trim(lines[i].substring(idx + FARE.length()));
                fullDetrResult.fare = value;
                if (StringUtils.contains(value, FORM_OF_PAYMENT)) {
                    fullDetrResult.fare = StringUtils.trim(value.substring(0, value.indexOf(FORM_OF_PAYMENT)));
                }
                continue PROC;
            }

            final String TOTAL = "TOTAL:";
            if (StringUtils.contains(lines[i], TOTAL)) {
                int idx = lines[i].indexOf(TOTAL);
                String value = StringUtils.trim(lines[i].substring(idx + TOTAL.length()));
                fullDetrResult.total = value;

                if (StringUtils.contains(value, "TKTN:")) {
                    fullDetrResult.total = value.substring(0, value.indexOf("TKTN:")).trim();
                }
            }

            // O FM:1LUM VOID VOID VOID
            // RL:
            // O TO:2BSD MU 5966 Q 11DEC 2130 OK QA 20K OPEN FOR USE
            // RL:NL9MNV /
            // TO: KMG
            // 在一改签票中发现一个航段可能不止一行
            final String O_FM = "O FM:";
            if (StringUtils.startsWith(lines[i], O_FM)) {
                // from行
                int firstFromLineIdx = i;
                // 下一行 开始to
                int lastToLineIdx = i + 1;
                Pattern pattern = Pattern.compile(".*TO:.+TO:\\s+(\\D{3})$");
                FIND_LAST_TO:
                while (true) {
                    if (lastToLineIdx >= lines.length - 1) {
                        break FIND_LAST_TO;
                    }
                    if (StringUtils.startsWith(lines[lastToLineIdx], "  TO:")) {
                        break FIND_LAST_TO;
                    }
                    //判断最后一段航班位置
                    Matcher matcher = pattern.matcher(lines[lastToLineIdx].trim());
                    if (matcher.find()) {
                        break FIND_LAST_TO;
                    }
                    lastToLineIdx++;
                }

                String fromLine = lines[i];
                int idx = lines[i].indexOf(O_FM);
                String segline = fromLine.substring(idx + O_FM.length());
                for (int count = i; count < lastToLineIdx; count++) {
                    if (StringUtils.contains(lines[count], "TO:")) {
                        break;
                    }
                    if (StringUtils.contains(lines[count + 1], "TO:")) {
                        break;
                    }
                    segline += lines[count+1];
                }

                DetrResult.FlightSeg seg = new DetrResult.FlightSeg();
                parseFlightSeg(segline, seg, fullDetrResult);


                int dx = firstFromLineIdx;
                while (dx <= lastToLineIdx) {
                    int curLineIdx = dx++;
                    final String TO = "TO:";
                    int index = lines[curLineIdx].indexOf(TO);
                    if (index != -1) {
                        String segString = lines[curLineIdx].substring(index + TO.length());
                        for (int count = curLineIdx + 1; count < lastToLineIdx; count++) {
                            if (StringUtils.contains(lines[count], "TO:")) {
                                break;
                            }
                            segString += lines[count];
                        }

                        seg = new DetrResult.FlightSeg();
                        parseFlightSeg(segString, seg, fullDetrResult);
                    }
                    Matcher matcher = pattern.matcher(lines[curLineIdx].trim());
                    if (matcher.find()) {
                        seg = new DetrResult.FlightSeg();
                        seg.from = matcher.group(1);
                        fullDetrResult.lsFlightSeg.add(seg);
                    }
                }

                // move to the next seg
                i = lastToLineIdx;
            }


            final String X_FM = "X FM:";
            if (StringUtils.startsWith(lines[i], X_FM)) {
                // from行
                int firstFromLineIdx = i;
                // 下一行 开始to
                int lastToLineIdx = i + 1;
                Pattern pattern = Pattern.compile(".*TO:.+TO:\\s+(\\D{3})$");
                FIND_LAST_TO:
                while (true) {
                    if (lastToLineIdx >= lines.length - 1) {
                        break FIND_LAST_TO;
                    }
                    if (StringUtils.startsWith(lines[lastToLineIdx], "  TO:")) {
                        break FIND_LAST_TO;
                    }
                    //判断最后一段航班位置
                    Matcher matcher = pattern.matcher(lines[lastToLineIdx].trim());
                    if (matcher.find()) {
                        break FIND_LAST_TO;
                    }
                    lastToLineIdx++;
                }

                String fromLine = lines[i];
                int idx = lines[i].indexOf(X_FM);
                String segline = fromLine.substring(idx + X_FM.length());
                for (int count = i; count < lastToLineIdx; count++) {
                    if (StringUtils.contains(lines[count], "TO:")) {
                        break;
                    }
                    if (StringUtils.contains(lines[count + 1], "TO:")) {
                        break;
                    }
                    segline += lines[count+1];
                }

                DetrResult.FlightSeg seg = new DetrResult.FlightSeg();
                parseFlightSeg(segline, seg, fullDetrResult);


                int dx = firstFromLineIdx;
                while (dx <= lastToLineIdx) {
                    int curLineIdx = dx++;
                    final String TO = "TO:";
                    int index = lines[curLineIdx].indexOf(TO);
                    if (index != -1) {
                        String segString = lines[curLineIdx].substring(index + TO.length());
                        for (int count = curLineIdx + 1; count < lastToLineIdx; count++) {
                            if (StringUtils.contains(lines[count], "TO:")) {
                                break;
                            }
                            segString += lines[count];
                        }

                        seg = new DetrResult.FlightSeg();
                        parseFlightSeg(segString, seg, fullDetrResult);
                    }
                    Matcher matcher = pattern.matcher(lines[curLineIdx].trim());
                    if (matcher.find()) {
                        seg = new DetrResult.FlightSeg();
                        seg.from = matcher.group(1);
                        fullDetrResult.lsFlightSeg.add(seg);
                    }
                }

                // move to the next seg
                i = lastToLineIdx;
            }

        }
        retDetrResult.setStatus(ReturnCode.SUCCESS);
        retDetrResult.setObject(fullDetrResult);

        return retDetrResult;
    }

    public ReturnClass<DetrResult> parse(FullDetrConfig param) {
        ReturnClass<DetrResult> retDetrResult = new ReturnClass<>();
        String tktNumber = param.tktNumber;
        String ret = getRawResultStr(tktNumber);
        if (null != ret) {
            if (ret.contains("没有权限") || ret.contains("AUTHORITY")) {
                retDetrResult.setStatus(ReturnCode.E_ORDER_AUTHORIZATION_ERROR);
                return retDetrResult;
            } else {
                return rawParse(ret);
            }
        } else {
            retDetrResult.setStatus(ReturnCode.ERROR, null);
            return retDetrResult;
        }

    }

    /**
     * 解析税费字符串
     *
     * @param taxStr
     * @param taxs
     */
    private static void parseTaxString(String taxStr, List<String> taxs) {
        if (StringUtils.isBlank(taxStr)) {
            return;
        }

        // 判断获取税费值
        if (taxStr.length() < 23) {
            return;
        }

        String taxValue = taxStr.substring(0, 23).trim();
        taxs.add(taxValue);
    }

    /**
     * 解析航段字符串
     *
     * @param flightSegStr
     * @param flightSeg
     */
    private static void parseFlightSeg(String flightSegStr, DetrResult.FlightSeg flightSeg, DetrResult detrResult) {
        if (StringUtils.isBlank(flightSegStr)) {
            return;
        }

        int length = flightSegStr.length();

        detrResult.lsFlightSeg.add(flightSeg);

        // 判断获取始发地/目的地
        if (length < 4) {
            return;
        }
        flightSeg.from = flightSegStr.substring(1, 4).trim();

        // 判断获取航班号前两位(公司)
        if (length < 7) {
            return;
        }
        flightSeg.carrier = flightSegStr.substring(5, 7).trim();

        // 判断获取航班号后三/四位(编号)
        if (length < 15) {
            return;
        }
        flightSeg.flightNumber = flightSegStr.substring(11, 15).trim();

        if ("VOID".equals(flightSeg.carrier) && "VOID".equals(flightSeg.flightNumber)) {
            flightSeg.segType = PnrParserConstant.CONST_SEGTYPE_ARNK;
        }

        // 判断获取舱位等级
        if (length < 18) {
            return;
        }
        flightSeg.classCode = flightSegStr.substring(17, 18).trim();

        // 判断获取起飞日期(ddmm)
        if (length < 24) {
            return;
        }
        flightSeg.date = flightSegStr.substring(19, 24).trim();

        // 判断获取起飞时间(hhmm)
        if (length < 29) {
            return;
        }
        flightSeg.time = flightSegStr.substring(25, 29).trim();

        // 判断获取客票状态
        if (length < 32) {
            return;
        }
        flightSeg.status = flightSegStr.substring(30, 32).trim();

        // 判断获取fareBasis(未知)
        if (length < 39) {
            return;
        }
        flightSeg.fareBasis = flightSegStr.substring(33, 39).trim();

        // 判断获取有效时间
        if (length < 57) {
            return;
        }
        flightSeg.period = flightSegStr.substring(44, 57).trim();

        // 判断获取行李
        if (length < 61) {
            return;
        }
        flightSeg.luggageWeight = flightSegStr.substring(58, 61).trim();

        String postStr = flightSegStr.substring(61).trim();

        //此处获取机票状态信息，由于此处不好使用正则表达式，因此使用条件判断
        //此处将前面的status状态修改成了更细的状态。
        if(postStr.startsWith("OPEN FOR USE")){
            flightSeg.status="OPEN FOR USE";
        }else if(postStr.startsWith("VOID")){
            flightSeg.status="VOID";
        }else if(postStr.startsWith("REFUNDED")){
            flightSeg.status="REFUNDED";
        }else if(postStr.startsWith("CHECKED IN")){
            flightSeg.status="CHECKED IN";
        }else if(postStr.startsWith("USED/FLOWN")){
            flightSeg.status="USED/FLOWN";
        }else if(postStr.startsWith("LIFT/BOARDED")){
            flightSeg.status="LIFT/BOARDED";
        }else if(postStr.startsWith("SUSPENDED")){
            flightSeg.status="SUSPENDED";
        }else if(postStr.startsWith("EXCHANGED")){
            flightSeg.status="EXCHANGED";
        }
        // 由于存在一段航段信息占用两行的情况，因此后端信息分开处理
        if (StringUtils.isBlank(postStr) || !StringUtils.contains(flightSegStr, "RL:")) {
            return;
        }

        int index = postStr.indexOf("RL:");

        // 判断获取起飞航站楼
        if (index - 5 < 0) {
            return;
        }
        flightSeg.fromT = postStr.substring(index - 5, index - 3).trim();
        // 判断获取到达航站楼
        flightSeg.toT = postStr.substring(index - 3, index - 1).trim();

        // 判断获取航空公司记录编号
        String combinePnr = postStr.substring(index + 3).trim();
        if (StringUtils.isBlank(combinePnr)) {
            return;
        }

        int sepIndex = combinePnr.indexOf("/");

        //PNR与航司大编码判断逻辑，如果"/"后面的字符串含有1E就是PNR，否则为航司大编码；相应的"/"前面的部分为航司大编码或PNR;
        String airLinePnr = null;
        String pnr = null;

        if (sepIndex >= 6) {
            String tmPnrStr = combinePnr.substring(0, sepIndex).trim();
            if (tmPnrStr.length() >= 6) {
                airLinePnr = tmPnrStr.substring(0, 6);
            }
        }

        // 判断获取订座编号
        if (sepIndex + 6 <= combinePnr.length()) {
            String tmPnrStr = combinePnr.substring(sepIndex + 1).trim();
            if (tmPnrStr.length() == 8 && tmPnrStr.endsWith("1E")) {
                pnr = tmPnrStr.substring(0, 6);
            } else if (tmPnrStr.length() >= 6) {
                pnr = airLinePnr;
                airLinePnr = tmPnrStr.substring(0, 6);
            }
        }

        //一个为空值，则赋值给另一个
        if (StringUtils.isBlank(pnr)) {
            pnr = airLinePnr;
        } else if (StringUtils.isBlank(airLinePnr)) {
            airLinePnr = pnr;
        }

        detrResult.airLinePnr = airLinePnr;
        detrResult.pnr = pnr;

    }

}
