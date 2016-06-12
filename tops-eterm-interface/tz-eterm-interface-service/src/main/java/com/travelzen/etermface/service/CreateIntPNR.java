package com.travelzen.etermface.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jodd.util.StringUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.common.ufis.util.UfisException;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.common.PNRDateFormat;
import com.travelzen.etermface.service.common.WriteFile;
import com.travelzen.etermface.service.constant.UfisStatus;
import com.travelzen.etermface.service.createpnr.CreatePnrReturnClass;
import com.travelzen.etermface.service.createpnr.CreatePnrReturnCode;
import com.travelzen.etermface.service.entity.FlightInfo;
import com.travelzen.etermface.service.entity.FlightSegments;
import com.travelzen.etermface.service.entity.FlightSegments.Flight;
import com.travelzen.etermface.service.entity.IntPnrCreateRequest;
import com.travelzen.etermface.service.entity.PassengerInfo;
import com.travelzen.framework.core.common.ReturnClass;

public class CreateIntPNR {
	
    private Logger logger = LoggerFactory.getLogger(CreateIntPNR.class);

    private final static String TIME_REGEX = "\\d{2}:\\d{2}";

    private final static String BOOKING_TIME_REGEX = "\\d{4}";

    private final static String DateRegex = "\\d{4}-\\d{2}-\\d{2}";

    private final static String LEGAL_PNR = "[A-Z0-9]{6}";
    // KA 905 M TH31OCT PEKHKG DK2 0800 1145
    private final static String SegmentRegex = "\\*?[A-Za-z0-9]{2}\\s+[A-Za-z0-9]{2,5}\\s+[A-Za-z0-9]{1,2}\\s+[A-Za-z0-9]{7}\\s+[A-Za-z]{6}\\s+[A-Za-z]{2}[0-9]{1,2}\\s+[0-9]{4}\\s+[0-9]{4}";

    private static final Map<String, String> SPECIAL_PASSENGER_TYPES = new HashMap<String, String>();
    /**
     * 日期格式
     */
    private final static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    /**
     * 生成pnr成功标记
     */
    private final static String EOT_SUCCESS = "-EOT SUCCESSFUL";

    /**
     * 一小时
     */
    private final static long ONE_HOUR = 60 * 60 * 1000l;

    /**
     * 出票时限,解析生成的PNR用
     */
    private final static String TICKET_LIMIT = "出票时限";

    /**
     * 一行最大的字符数
     */
    private final static int LINE_SIZE = 80;

    /**
     * 行与行之间的连接串
     */
    private final static String CONNECTION_STRING = "-";

    /**
     * 需要加儿童称谓的航司(MSTR,MISS)
     */
    // private final static Set<String> AIRLINES = new HashSet<String>();

    static {
        // AIRLINES.add("AC");
        // AIRLINES.add("SQ");
        // AIRLINES.add("PR");
        // AIRLINES.add("NH");

        SPECIAL_PASSENGER_TYPES.put("STU", "STU");
        SPECIAL_PASSENGER_TYPES.put("SC", "SEA");
        SPECIAL_PASSENGER_TYPES.put("DL", "LBR");
        SPECIAL_PASSENGER_TYPES.put("EM", "EMI");
        SPECIAL_PASSENGER_TYPES.put("ZZ", null);
        SPECIAL_PASSENGER_TYPES.put("TS", null);
        SPECIAL_PASSENGER_TYPES.put("OTHER", null);
    }
    
    public String createIntPNR(String xml) {

        IntPnrCreateRequest request = null;
        try {
            request = IntPnrCreateRequest.convertToObject(xml);
        } catch (Exception e) {
            logger.error("createIntPNR error:" + e.getMessage(), e);
        }

        CreatePnrReturnClass<String> returnClass = null;
        if (null == request) {
            CreatePnrReturnCode returnCode = null;
            returnClass = new CreatePnrReturnClass<String>();
            returnCode = CreatePnrReturnCode.SUBMIT_DATE_ERR;
            returnClass.setReturnCode(returnCode);
            returnClass.setStatus(returnCode);
        } else {
            returnClass = createIntPNR(request);
        }
        String rtXml = CreatePnrReturnClass.convertToXml(returnClass);
        logger.info("createIntPNR request:\n" + xml);
        logger.info("createIntPNR response:\n" + rtXml);
        return rtXml;
    }

    public CreatePnrReturnClass<String> createIntPNR(IntPnrCreateRequest request) {
        CreatePnrReturnClass<String> returnClass = null;

        CreatePnrReturnCode returnCode = null;

        // 验证航班信息
        returnCode = checkFlightInfo(request.getFlightInfoList());
        if (null != returnCode) {
            returnClass = new CreatePnrReturnClass<String>();
            returnClass.setReturnCode(returnCode);
            return returnClass;
        }

        // 验证乘客信息
        returnCode = checkPassengerInfo(request.getPassengerList());
        if (null != returnCode) {
            returnClass = new CreatePnrReturnClass<String>();
            returnClass.setReturnCode(returnCode);
            return returnClass;
        }

        // 验证联系方式
        returnCode = checkContacts(request);
        if (null != returnCode) {
            returnClass = new CreatePnrReturnClass<String>();
            returnClass.setReturnCode(returnCode);
            return returnClass;
        }

        resetPassengerOrder(request);
        addInformation(request);

        List<String> commands = new ArrayList<String>();

        List<String> subCommands = null;

        // 生成SS命令
        subCommands = toSSCommands(request);
        commands.addAll(subCommands);

        // 生成设置乘机人姓名命令
        subCommands = toNameCommand(request);
        commands.addAll(subCommands);

        // 生成Docs命令
        subCommands = toSSRDocsCommands(request);
        commands.addAll(subCommands);

        // 生成Xn命令
        subCommands = toXnCommands(request);
        commands.addAll(subCommands);

        // 生成SSR INFT命令
        subCommands = toSSRInftCommands(request);
        commands.addAll(subCommands);
        
        // 生成SSR CTCM命令
        subCommands = toSsrCtcmCommands(request);
        commands.addAll(subCommands);

        // 生成OSI CTCT命令
        subCommands = toOsiCTCTCommands(request);
        commands.addAll(subCommands);
        
        // 生成RMK 授权航司命令
        subCommands = toRmkAuthCommands(request);
        if (subCommands != null)
        	commands.addAll(subCommands);

        if (null != request.getTktl()) {
            commands.add(request.getTktl());
        }

        StringBuffer oneCommandBuffer = new StringBuffer();
        boolean first = true;
        for (String command : commands) {
            if (first) {
                first = false;
            } else {
                oneCommandBuffer.append("\r");
            }
            oneCommandBuffer.append(command);
        }

        oneCommandBuffer.append("\r@I");
        
        String rtnText = null;
        
        if (UfisStatus.active && UfisStatus.createIntPnr) {
        	EtermUfisClient client = null;
        	try {
        		if (request.getOfficeNo() != null && request.getOfficeNo().length() == 6)
            		client = new EtermUfisClient(request.getOfficeNo());
            	else
            		client = new EtermUfisClient();
				rtnText = client.execCmd(oneCommandBuffer.toString(), true);
			} catch (UfisException e) {
				logger.error(e.getMessage(), e);
				if (e.getMessage().contains("Ufis-Client无法获取有效的分组"))
					return new CreatePnrReturnClass<String>(CreatePnrReturnCode.INVALID_OFFICE_ERR);
				return new CreatePnrReturnClass<String>(CreatePnrReturnCode.UFIS_ERR);
			} finally {
				if (null != client)
					client.close();
			}
        } else {
        	EtermWebClient client = new EtermWebClient();
            try {
                if (request.getOfficeNo() != null && request.getOfficeNo().length() == 6) {
                    client.connect(request.getOfficeNo());
                } else {
                    client.connect();
                }
            } catch (Exception e) {
                e.printStackTrace();
                client.close();
                returnClass = new CreatePnrReturnClass<String>();
                if (e.getMessage().contains("不存在该Office的配置"))
                	returnClass.setReturnCode(CreatePnrReturnCode.INVALID_OFFICE_ERR);
        		else
        			returnClass.setReturnCode(CreatePnrReturnCode.CONNECT_ETERM_ERR);
                return returnClass;
            }
            /**
             * 撤销上次操作(还原)
             */
            try {
                client.resume();
            } catch (SessionExpireException e) {
                client.close();
                return new CreatePnrReturnClass<String>(CreatePnrReturnCode.SESSION_EXPIRE_ERR);
            }

            logger.info("create international pnr Command:\n" + oneCommandBuffer.toString());
            ReturnClass<String> rtStr = null;
            try {
                rtStr = client.executeCmdWithRetry(oneCommandBuffer.toString(), true);
                rtnText = rtStr.getObject();
            } catch (SessionExpireException e) {
                return new CreatePnrReturnClass<String>(CreatePnrReturnCode.SESSION_EXPIRE_ERR);
            } finally {
                client.close();
                logger.info("create international pnr return:\n" + rtStr.getObject());
            }
        }
        
        if (null == rtnText) {
        	return new CreatePnrReturnClass<String>(CreatePnrReturnCode.COMMAND_NO_RETURN_ERR);
        }
        
        if (rtnText.contains("UNABLE TO SELL.PLEASE CHECK THE AVAILABILITY")) {
        	return new CreatePnrReturnClass<String>(CreatePnrReturnCode.NO_SEAT_ERR);
        }
        
        if (rtnText.trim().startsWith("MARRIED SEGMENT EXIST")) {
        	return new CreatePnrReturnClass<String>(CreatePnrReturnCode.MARRIED_SEGMENT_ERR);
        }

        returnClass = getPNRFromSSReturn(rtnText);
        returnClass.setObject(rtnText);
        if (null != returnClass.getPnr() && !"FORMAT".equalsIgnoreCase(returnClass.getPnr())) {
            returnClass.setReturnCode(CreatePnrReturnCode.SUCCESS);
            /**
             * 写log
             */
            WriteFile.write(returnClass.getPnr());
        } else {
            returnClass.setReturnCode(CreatePnrReturnCode.UNKNOWN_ERR);
        }

        return returnClass;
    }

	/**
     * 验证联系方式
     *
     * @param request
     * @return
     */
    public CreatePnrReturnCode checkContacts(IntPnrCreateRequest request) {
        if (StringUtil.isBlank(request.getTelephone())) {
            return CreatePnrReturnCode.CONTACTS_ERR;
        }
        return null;
    }

    /**
     * 验证航班信息
     *
     * @param flightInfoList
     * @return
     */
    private CreatePnrReturnCode checkFlightInfo(List<FlightInfo> flightInfoList) {

        if (null == flightInfoList || 0 == flightInfoList.size()) {
            return CreatePnrReturnCode.NO_FLIGHT_INFO_ERR;
        }

        for (FlightInfo flightInfo : flightInfoList) {
            if (StringUtil.isBlank(flightInfo.getFromAirPort())) {
                return CreatePnrReturnCode.DEPART_AIRPORT_ERR;
            }

            if (StringUtil.isBlank(flightInfo.getToAirPort())) {
                return CreatePnrReturnCode.ARRIVE_AIRPORT_ERR;
            }

            if (StringUtil.isBlank(flightInfo.getCabinCode())) {
                return CreatePnrReturnCode.CLASS_CODE_ERR;
            }

            if (StringUtil.isBlank(flightInfo.getFlightNo())) {
                return CreatePnrReturnCode.FLIGHT_NUMER_ERR;
            }

            if (StringUtil.isBlank(flightInfo.getCarrier())) {
                return CreatePnrReturnCode.CARRIER__ERR;
            }

            if (StringUtil.isBlank(flightInfo.getFromDate()) || !flightInfo.getFromDate().matches(DateRegex)) {
                return CreatePnrReturnCode.FLIGHT_DATE_ERR;
            }
        }

        return null;
    }

    /**
     * 验证乘客信息
     *
     * @param passengerList
     * @return
     */
    private CreatePnrReturnCode checkPassengerInfo(List<PassengerInfo> passengerList) {
        if (null == passengerList || 0 == passengerList.size()) {
            return CreatePnrReturnCode.NO_PASSENGER_ERR;
        }
        // 成人人数
        int adtCount = 0;
        // 儿童人数
        int chdCount = 0;
        // 婴儿人数
        int infCount = 0;

        for (PassengerInfo passenger : passengerList) {

            if (passenger.getPassengerType().equals("ADT") || SPECIAL_PASSENGER_TYPES.containsKey(passenger.getPassengerType())) {
                adtCount++;
            } else if (passenger.getPassengerType().equals("CHD")) {
                chdCount++;
            } else if (passenger.getPassengerType().equals("INF")) {
                infCount++;
            } else {
                return CreatePnrReturnCode.PASSENGER_TYPE_ERR;
            }

            if (StringUtil.isBlank(passenger.getSurName()) || !passenger.getSurName().contains("/")) {
                return CreatePnrReturnCode.PASSENGER_NAME_ERR;
            }

            if (StringUtil.isBlank(passenger.getBirthDay()) || !passenger.getBirthDay().matches(DateRegex)) {
                return CreatePnrReturnCode.PASSENGER_BIRTHDAY_ERR;
            }

            if (StringUtil.isBlank(passenger.getPassengerType())) {
                return CreatePnrReturnCode.PASSENGER_TYPE_ERR;
            }

            if (!passenger.getPassengerType().equals("INF")) {
                if (StringUtil.isBlank(passenger.getCerNo())) {
                    return CreatePnrReturnCode.PASSENGER_IDENTITICATION_ERR;
                }

                if (StringUtil.isBlank(passenger.getCerValidity()) || !passenger.getCerValidity().matches(DateRegex)) {
                    return CreatePnrReturnCode.PASSENGER_IDENTITICATION_VALIDTIME_ERR;
                }

                if (StringUtil.isBlank(passenger.getCerCountry())) {
                    return CreatePnrReturnCode.PASSENGER_CE_COUNTRY_ERR;
                }

                if (StringUtil.isBlank(passenger.getNationality())) {
                    return CreatePnrReturnCode.PASSENGER_COUNTRY_ERR;
                }
            }
        }

        if (adtCount < infCount) {
            return CreatePnrReturnCode.PASSENGER_INF_COUNT_ERR;
        }

        return null;
    }

    /**
     * 重置乘客序列(成人在前，儿童中间，婴儿最后)，并设置乘客index及婴儿跟随的成人index
     *
     * @param request
     */
    private void resetPassengerOrder(IntPnrCreateRequest request) {
        List<PassengerInfo> allPassengers = new ArrayList<PassengerInfo>();
        List<PassengerInfo> adultPassengers = new ArrayList<PassengerInfo>();
        List<PassengerInfo> childPassengers = new ArrayList<PassengerInfo>();
        List<PassengerInfo> infPassengers = new ArrayList<PassengerInfo>();

        for (PassengerInfo passenger : request.getPassengerList()) {
            if (passenger.getPassengerType().equals("ADT") || SPECIAL_PASSENGER_TYPES.containsKey(passenger.getPassengerType())) {
                adultPassengers.add(passenger);
            } else if (passenger.getPassengerType().equals("CHD")) {
                childPassengers.add(passenger);
            } else {
                infPassengers.add(passenger);
            }
        }

        allPassengers.addAll(adultPassengers);
        allPassengers.addAll(childPassengers);
        allPassengers.addAll(infPassengers);

        int idIndex = 1;
        int followAdtIndex = 1;

        for (PassengerInfo passenger : allPassengers) {
            if (passenger.getPassengerType().equals("INF")) {
                passenger.setFollowAdtIndex(followAdtIndex++);
            } else {
                passenger.setIdIndex(idIndex++);
            }
        }

        request.setPassengerList(allPassengers);
    }

    /**
     * 补充完善信息(订座数，出票office号，行动代码，出票时限等)
     *
     * @param request
     */
    private void addInformation(IntPnrCreateRequest request) {

        if (StringUtil.isBlank(request.getOfficeNo())) {
            request.setOfficeNo("SHA255");
        }

        if (StringUtil.isBlank(request.getActionCode())) {
            request.setActionCode("NN");
        }

        if (StringUtil.isBlank(request.getTktl())) {
            request.setTktl(getTktl(request));
        }

        int seatNum = 0;
        for (PassengerInfo passenger : request.getPassengerList()) {
            if (passenger.getPassengerType().equals("ADT") || passenger.getPassengerType().equals("CHD") || SPECIAL_PASSENGER_TYPES.containsKey(passenger.getPassengerType())) {
                seatNum++;
            }
        }

        request.setSeatNum(seatNum);

        for (FlightInfo flightInfo : request.getFlightInfoList()) {
            if (flightInfo.getFlightNo().startsWith(flightInfo.getCarrier())) {
                flightInfo.setFlightNo(flightInfo.getFlightNo().substring(2));
            }

            if (flightInfo.getFlightNo().length() == 1) {
                flightInfo.setFlightNo("00" + flightInfo.getFlightNo());
            } else if (flightInfo.getFlightNo().length() == 2) {
                flightInfo.setFlightNo("0" + flightInfo.getFlightNo());
            }

            flightInfo.setFlightNo(flightInfo.getCarrier() + flightInfo.getFlightNo());
        }
    }

    /**
     * 转化为SSCommand命令 例如（SS：CA1301/Y/20OCT/PEKCAN/NN1） SS:指令 CA:航空公司代码 1301:航班号
     * Y:舱位 20OCT:日期 PEK:出发城市 CAN:到达城市 NN:行动代码 1:订座数
     *
     * @param request
     * @return
     */
    private List<String> toSSCommands(IntPnrCreateRequest request) {
        List<String> ssCommands = new ArrayList<String>();
        List<FlightInfo> flightInfoList = request.getFlightInfoList();

        for (FlightInfo flightInfo : flightInfoList) {
            String date = PNRDateFormat.dayMonthFormat(flightInfo.getFromDate());

            StringBuffer buffer = new StringBuffer();
            buffer.append("SS:");
            buffer.append(flightInfo.getFlightNo());
            buffer.append("/");
            if (flightInfo.getCabinCode().length() > 1)
            	buffer.append(flightInfo.getCabinCode().substring(0, 1));
            else
            	buffer.append(flightInfo.getCabinCode());
            buffer.append("/");
            buffer.append(date);
            buffer.append("/");
            buffer.append(flightInfo.getFromAirPort());
            buffer.append(flightInfo.getToAirPort());
            buffer.append("/");
            buffer.append(request.getActionCode());
            buffer.append(request.getSeatNum());

            // String fromTime = toBookingTime(flightInfo.getFromTime());
            // String toTime = toBookingTime(flightInfo.getToTime());
            //
            // if (null != fromTime || null != toTime) {
            // buffer.append("/");
            // buffer.append(fromTime);
            // buffer.append(" ");
            // buffer.append(toTime);
            // }

            ssCommands.add(buffer.toString());
        }

        return ssCommands;
    }

    /**
     * 乘客姓名 例如(NM 1zhang/san 1li/si MSTR)
     *
     * @param request
     * @return
     */
    private List<String> toNameCommand(IntPnrCreateRequest request) {
        StringBuffer nameCommand = new StringBuffer("NM");
        List<PassengerInfo> passengerList = request.getPassengerList();

        // 是否需要添加称谓
        // boolean needCall = false;
        // for (FlightInfo flightInfo : request.getFlightInfoList()) {
        // if (AIRLINES.contains(flightInfo.getCarrier())) {
        // needCall = true;
        // break;
        // }
        // }

        for (PassengerInfo passenger : passengerList) {
            if (passenger.getPassengerType().equals("INF")) {
                continue;
            }
            nameCommand.append("1");
            nameCommand.append(passenger.getSurNameWithAppellation());
            // nameCommand.append(" ");
            //
            // if
            // (SPECIAL_PASSENGER_TYPES.containsKey(passenger.getPassengerType()))
            // {
            // String call =
            // SPECIAL_PASSENGER_TYPES.get(passenger.getPassengerType());
            // if (StringUtils.isNotBlank(call)) {
            // nameCommand.append(call);
            // }
            // } else if (passenger.getPassengerType().equals("CHD")) {
            // if (needCall) {
            // if (passenger.getGender().equals("M")) {
            // nameCommand.append("MSTR");
            // } else {
            // nameCommand.append("MISS");
            // }
            // } else {
            // nameCommand.append("CHD");
            // }
            // }
        }

        String originCommand = nameCommand.toString();
        return splitToMutilLine(originCommand);
    }

    /**
     * 给每一个乘客每一个航空公司生成SSR DOCS信息
     *
     * @param request
     * @return
     */
    private List<String> toSSRDocsCommands(IntPnrCreateRequest request) {

        List<String> ssrDocsCommands = new ArrayList<String>();

        Set<String> airlines = new HashSet<String>();
        // 收集所有的航空公司信息
        for (FlightInfo flightInfo : request.getFlightInfoList()) {
            airlines.add(flightInfo.getCarrier());
        }

        for (PassengerInfo passenger : request.getPassengerList()) {
            if (passenger.getPassengerType().equals("INF")) {
                continue;
            }
            List<String> commands = toSSRDocsCommands(passenger, airlines);
            ssrDocsCommands.addAll(commands);
        }

        return ssrDocsCommands;
    }

    /**
     * 一个乘客每一个航空公司生成SSR DOCS信息 例如(SSR DOCS MU HK1
     * P/CN/G38981666/CN/13AUG66/M/26NOV19/TANG/XI WEN/P1) SSR DOCS 航空公司代码
     * Action-Code 1
     * 证件类型/发证国家/证件号码/国籍/出生日期/性别/证件有效期限/SURNAME/FIRST-NAME/MID-NAME/持有人标识H/P1
     *
     * @param passenger
     * @param airlines
     * @return
     */
    private List<String> toSSRDocsCommands(PassengerInfo passenger, Set<String> airlines) {
        List<String> ssrDocsCommands = new ArrayList<String>();

        for (String airline : airlines) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("SSR DOCS ");
            buffer.append(airline);
            buffer.append(" HK1 P/");
            buffer.append(passenger.getCerCountry());
            buffer.append("/");
            buffer.append(passenger.getCerNo());
            buffer.append("/");
            buffer.append(passenger.getNationality());
            buffer.append("/");
            buffer.append(PNRDateFormat.dayMonthYearFormat(passenger.getBirthDay()));
            buffer.append("/");
            buffer.append(passenger.getGender());
            buffer.append("/");
            buffer.append(PNRDateFormat.dayMonthYearFormat(passenger.getCerValidity()));
            buffer.append("/");
            buffer.append(passenger.getSurName());
            buffer.append("/P");
            buffer.append(passenger.getIdIndex());
            if (buffer.length() > 80)
            	ssrDocsCommands.add(buffer.substring(0, 80) + "-" + buffer.substring(80));
            else
            	ssrDocsCommands.add(buffer.toString());
        }

        return ssrDocsCommands;
    }

    /**
     * 生成XN命令 例如XN:IN/li/li INF(MAR10)/P1 XN:IN/婴儿名 INF(出生月年)/PN *PN表示跟随第几个成人乘客
     *
     * @param request
     * @return
     */
    private List<String> toXnCommands(IntPnrCreateRequest request) {
        List<String> xnCommands = new ArrayList<String>();

        for (PassengerInfo passenger : request.getPassengerList()) {
            if (passenger.getPassengerType().equals("INF")) {
                StringBuffer buffer = new StringBuffer();
                buffer.append("XN:IN/");
                buffer.append(passenger.getSurName());
                buffer.append(" INF (");
                buffer.append(PNRDateFormat.monthYearFormat(passenger.getBirthDay()));
                buffer.append(")/P");
                buffer.append(passenger.getFollowAdtIndex());
                xnCommands.add(buffer.toString());
            }
        }

        return xnCommands;
    }

    /**
     * 生成 SSR INTF命令 SSR INFT CZ NN1/CANKUL 365 V 20JUL LI/LIINF 10MAR10/P1 SSR
     * INFT 航空公司二字码 NN1 城市对 航班号 舱位 出发日期 姓/名INF 出生年月日/Pn
     *
     * @param request
     * @return
     */
    private List<String> toSSRInftCommands(IntPnrCreateRequest request) {
        List<String> ssrInftCommands = new ArrayList<String>();

        List<PassengerInfo> passengerList = request.getPassengerList();

        for (PassengerInfo passenger : passengerList) {
            if (passenger.getPassengerType().equals("INF")) {

                String birthDay = PNRDateFormat.dayMonthYearFormat(passenger.getBirthDay());
                for (FlightInfo flightInfo : request.getFlightInfoList()) {

                    String flightNo = null;
                    if (flightInfo.getFlightNo().startsWith(flightInfo.getCarrier())) {
                        flightNo = flightInfo.getFlightNo().substring(flightInfo.getCarrier().length());
                    } else {
                        flightNo = flightInfo.getFlightNo();
                    }

                    StringBuffer buffer = new StringBuffer("SSR INFT ");
                    buffer.append(flightInfo.getCarrier());
                    buffer.append(" NN1/");
                    buffer.append(flightInfo.getFromAirPort());
                    buffer.append(flightInfo.getToAirPort());
                    buffer.append(" ");
                    buffer.append(flightNo);
                    buffer.append(" ");
                    if (flightInfo.getCabinCode().length() > 1)
                    	buffer.append(flightInfo.getCabinCode().substring(0, 1));
                    else
                    	buffer.append(flightInfo.getCabinCode());
                    buffer.append(" ");
                    buffer.append(PNRDateFormat.dayMonthFormat(flightInfo.getFromDate()));
                    buffer.append(" ");
                    buffer.append(passenger.getSurName());
                    buffer.append(" ");
                    buffer.append(birthDay);
                    buffer.append("/P");
                    buffer.append(passenger.getFollowAdtIndex());

                    ssrInftCommands.add(buffer.toString());
                }
            }
        }

        return ssrInftCommands;
    }
    
    /**
     * SSR CTCM 航空公司 HK1 联系电话/P1
     *
     * @param request
     * @return
     */
    private List<String> toSsrCtcmCommands(IntPnrCreateRequest request) {
        List<String> ssrCTCMCommands = new ArrayList<String>();

        Set<String> airlines = new HashSet<String>();
        for (FlightInfo flightInfo : request.getFlightInfoList()) {
            airlines.add(flightInfo.getCarrier());
        }

        for (String airline : airlines) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("SSR CTCM ");
            buffer.append(airline);
            buffer.append(" HK1 ");
            buffer.append(request.getTelephone());
            buffer.append("/P1");
            ssrCTCMCommands.add(buffer.toString());
        }

        return ssrCTCMCommands;
    }

    /**
     * OSI CTCT OSI 航空公司 CTCT 联系电话
     *
     * @param request
     * @return
     */
    private List<String> toOsiCTCTCommands(IntPnrCreateRequest request) {
        List<String> osiCTCTCommands = new ArrayList<String>();

        Set<String> airlines = new HashSet<String>();
        for (FlightInfo flightInfo : request.getFlightInfoList()) {
            airlines.add(flightInfo.getCarrier());
        }

        for (String airline : airlines) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("OSI ");
            buffer.append(airline);
            buffer.append(" CTCT ");
            buffer.append(request.getTelephone());
            osiCTCTCommands.add(buffer.toString());
        }

        return osiCTCTCommands;
    }
    
    private List<String> toRmkAuthCommands(IntPnrCreateRequest request) {
    	String authOffice = request.getAuthOfficeNo();
    	if (authOffice == null)
			return null;
    	List<String> rmkAuthCommands = new ArrayList<String>();
		rmkAuthCommands.add("RMK TJ AUTH " + authOffice);
		return rmkAuthCommands;
	}

    /**
     * 获取出票时限 TKTL2000/24JUL/SHA255
     *
     * @return
     */
    private String getTktl(IntPnrCreateRequest request) {

        long miniFromTime = -1l;
        for (FlightInfo flightInfo : request.getFlightInfoList()) {
            try {

                String fromTime = toNormalTime(flightInfo.getFromTime());
                if (null == fromTime) {
                    continue;
                }
                Date date = DATE_TIME_FORMAT.parse(flightInfo.getFromDate() + " " + fromTime);
                long time = date.getTime();
                if (-1 == miniFromTime || time > miniFromTime) {
                    miniFromTime = time;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        long currentTime = System.currentTimeMillis();
        if (-1 == miniFromTime) {
            miniFromTime = currentTime + 6 * ONE_HOUR;
        } else {
            if (miniFromTime - 48 * ONE_HOUR >= currentTime) {// 大于等于48小时情况
                miniFromTime = miniFromTime - 24 * ONE_HOUR;
            } else if (miniFromTime - 24 * ONE_HOUR >= currentTime) {// 大于等于24小时情况
                miniFromTime = currentTime + 23 * ONE_HOUR;
            } else {
                miniFromTime = miniFromTime - ONE_HOUR;// 小于24小时情况
            }
        }

        String date = null;
        try {
            date = DATE_TIME_FORMAT.format(new Date(miniFromTime));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null == date) {
            return null;
        }

        String dayMonth = PNRDateFormat.dayMonthFormat(date.substring(0, 10));

        if (null == dayMonth) {
            return null;
        }

        String tktl = "TKTL" + date.substring(11, 13) + date.substring(14, 16) + "/" + dayMonth + "/SHA255";
        return tktl;
    }

    /**
     * @param ssRtstr
     * @return
     */
    private static CreatePnrReturnClass<String> getPNRFromSSReturn(String ssRtstr) {
        FlightSegments flightSegments = new FlightSegments();
        flightSegments.flightList = new ArrayList<Flight>();

        String[] strs = ssRtstr.replaceAll("\r", "\n").split("\n");
        String pnr = null;
        for (String str : strs) {
            str = str.trim();
            if (str.matches(SegmentRegex)) {
                String[] subStrs = str.split("\\s+");
                Flight flight = new Flight();
                flight.Carrier = subStrs[0];
                flight.Flight = subStrs[0] + subStrs[1];
                flight.BookingClass = subStrs[2];
                flight.DepartureDate = subStrs[3];
                flight.BoardPoint = subStrs[4].substring(0, 3);
                flight.OffPoint = subStrs[4].substring(3, 6);
                flight.ActionCode = subStrs[5];
                flightSegments.flightList.add(flight);
                continue;
            }

            if (str.length() == 6) {
                pnr = str;
            } else {
                int index = str.indexOf(EOT_SUCCESS);
                if (index != -1) {
                    pnr = str.substring(0, index).trim();
                    if (pnr.length() != 6) {
                        pnr = null;
                    }
                } else if (str.contains(TICKET_LIMIT)) {
                    String[] ss = str.split("\\s+");
                    if (null != ss && ss.length > 0) {
                        pnr = ss[0];
                    }
                }
            }

            if (validate(pnr)) {
                break;
            } else {
                pnr = null;
            }
        }

        CreatePnrReturnClass<String> rs = new CreatePnrReturnClass<String>();
        rs.setPnr(pnr);
        rs.setFlightSegments(flightSegments);
        return rs;
    }

    /**
     * PNR合法性校验(字母或数字)
     *
     * @param pnr
     * @return
     */
    private static boolean validate(String pnr) {
        if (StringUtils.isBlank(pnr)) {
            return false;
        }

        if (pnr.matches(LEGAL_PNR)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 转换预订时间(起飞时间和到达时间)
     *
     * @param time
     * @return
     */
    private static String toBookingTime(String time) {
        if (null == time) {
            return null;
        }

        if (time.matches(BOOKING_TIME_REGEX)) {
            return time;
        }
        if (time.matches(TIME_REGEX)) {
            String[] strs = time.split(":");
            return strs[0] + strs[1];
        } else {
            return null;
        }
    }

    private static String toNormalTime(String time) {
        if (null == time) {
            return null;
        }

        if (time.matches(TIME_REGEX)) {
            return time;
        }

        if (time.matches(BOOKING_TIME_REGEX)) {
            return time.substring(0, 2) + ":" + time.substring(2, 4);
        }

        return null;
    }

    private static List<String> splitToMutilLine(String originStr) {
        List<String> lines = new ArrayList<String>();
        if (StringUtils.isBlank(originStr)) {
            return lines;
        }
        int start = 0;
        int end = LINE_SIZE;
        boolean firstLine = true;
        int length = originStr.length();
        while (start < length) {
            String line = null;
            if (firstLine) {
                firstLine = false;
                line = "";
            } else {
                line = CONNECTION_STRING;
            }
            end = (end < length ? end : length);
            line = line + originStr.substring(start, end);
            lines.add(line);
            start = end;
            end = end + LINE_SIZE - CONNECTION_STRING.length();
        }
        return lines;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        String s = "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
        List<String> lines = splitToMutilLine(s);
        for (int index = 0; index < lines.size(); index++) {
            System.out.println(lines.get(index));
        }
        String str = "  MU8763  Y WE16OCT  PVGNRT DK1   0900 1300                                     \n" + "HMNH1H -  航空公司使用自动出票时限, 请检查PNR                                 \n"
                + "*** 预订酒店指令HC, 详情  HC:HELP   ***";
        System.out.println(getPNRFromSSReturn(str));

        String time1 = "2302";

        System.out.println(toNormalTime(time1));

        time1 = "21:03";
        System.out.println(toBookingTime(time1));

        long time = System.currentTimeMillis();
        String date = null;
        try {
            date = DATE_TIME_FORMAT.format(new Date(time));
            System.out.println(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String dayMonth = PNRDateFormat.dayMonthFormat(date.substring(0, 10));
        String tktl = "TKTL" + date.substring(11, 13) + date.substring(14, 16) + "/" + dayMonth + "/SHA255";
        System.out.println(tktl);
        System.out.println(date.substring(0, 10));
        System.out.println(PNRDateFormat.dayMonthFormat(date.substring(0, 10)));
    }
}