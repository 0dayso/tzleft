package com.travelzen.rosetta.eterm.parser.rt.subparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.rosetta.eterm.common.pojo.rt.FlightInfo;
import com.travelzen.rosetta.eterm.common.pojo.rt.FlightInfo.Flight;
import com.travelzen.rosetta.eterm.common.pojo.rt.FlightInfo.FlightType;
import com.travelzen.rosetta.eterm.parser.util.RtDateTimeUtil;

/**
 * 航班信息解析
 * @author yiming.yan
 */
public enum FlightInfoParser {

	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FlightInfoParser.class);
	
	public static FlightInfo parse(String text, boolean isDomestic) {
		FlightInfo flightInfo = new FlightInfo();
		List<Flight> flightList = new ArrayList<Flight>();
		List<String> carrierList = new ArrayList<String>();
		Pattern pattern = Pattern.compile(
				"(?<carrier>\\*?[A-Z0-9]{2})(?<flightNo>[A-Z0-9]+) +(?<cabin>[A-Z0-9]+) +"
				+ "(?<weekday>[A-Z]{2})(?<date>\\d{2}[A-Z]{3}(?:\\d{2})?) *"
				+ "(?<deptAirport>[A-Z]{3})(?<arrAirport>[A-Z]{3}) ?"
				+ "(?<status>[A-Z]{2})(?<passengerNum>\\d{1,3}) +"
				+ "(?:(?<deptTime>\\d{4}) (?<arrTime>\\d{4}))?(?<nights>[\\+\\-]\\d)? +"
				+ "(?:E|[A-Z]+)(?: "
				+ "(?<deptTerminal>[A-Z0-9 -]{2})(?<arrTerminal>[A-Z0-9 -]{2}) ?"
				+ "(?<subCabin>[A-Z][0-9])?)?"
				+ ".*" //for the following OP-XX123 
				+ "|"
				+ "(?<carrier1>\\*?[A-Z0-9]{2})?(?<open>OPEN) +(?<cabin1>[A-Z0-9]+)? +"
				+ "(?:(?<weekday1>[A-Z]{2})(?<date1>\\d{2}[A-Z]{3}(?:\\d{2})?))? *"
				+ "(?<deptAirport1>[A-Z]{3})(?<arrAirport1>[A-Z]{3})"
				+ "|"
				+ "(?<arnk>ARNK) +(?<deptAirport2>[A-Z]{3})(?<arrAirport2>[A-Z]{3})");
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			Flight flight = new Flight();
			if (matcher.group("open") != null) {
				flight.setType(FlightType.valueOf(matcher.group("open")));
				if (matcher.group("carrier1") != null) {
					flight.setCarrier(matcher.group("carrier1"));
					if (!carrierList.contains(flight.getCarrier()))
						carrierList.add(flight.getCarrier());
				}
				if (matcher.group("cabin1") != null)
					flight.setCabin(matcher.group("cabin1"));
				if (matcher.group("weekday1") != null)
					flight.setWeekday(matcher.group("weekday1"));
				if (matcher.group("date1") != null)
					flight.setDeptDate(matcher.group("date1"));
				flight.setDeptAirport(matcher.group("deptAirport1"));
				flight.setArrAirport(matcher.group("arrAirport1"));
				flightList.add(flight);
				continue;
			}
			if (matcher.group("arnk") != null) {
				flight.setType(FlightType.valueOf(matcher.group("arnk")));
				flight.setDeptAirport(matcher.group("deptAirport2"));
				flight.setArrAirport(matcher.group("arrAirport2"));
				flightList.add(flight);
				continue;
			}
			if (matcher.group("carrier").length() == 3) {
				flight.setShared(true);
				Matcher matcher_op = Pattern.compile(
						"OP-(?<opCarrier>\\*?[A-Z0-9]{2})(?<opFlightNo>[A-Z0-9]+)").matcher(matcher.group(0));
				if (matcher_op.find()) {
					flight.setOpCarrier(matcher_op.group("opCarrier"));
					flight.setOpFlightNo(matcher_op.group("opFlightNo"));
				}
				flight.setCarrier(matcher.group("carrier").substring(1));
			} else {
				flight.setCarrier(matcher.group("carrier"));
			}
			if (!carrierList.contains(flight.getCarrier()))
				carrierList.add(flight.getCarrier());
			flight.setFlightNo(matcher.group("flightNo"));
			flight.setCabin(matcher.group("cabin"));
			flight.setWeekday(matcher.group("weekday"));
			int nights = 0;
			if (null != matcher.group("nights"))
				nights = Integer.parseInt(matcher.group("nights"));
			Pair<String, String> dates = RtDateTimeUtil.parseDates(
					matcher.group("date"), nights, matcher.group("deptTime"));
			flight.setDeptDate(dates.getValue0());
			flight.setArrDate(dates.getValue1());
			flight.setDeptAirport(matcher.group("deptAirport"));
			flight.setArrAirport(matcher.group("arrAirport"));
			flight.setStatus(matcher.group("status"));
			flight.setPassengerNum(matcher.group("passengerNum"));
			if (null != matcher.group("deptTime")) {
				String deptTime = matcher.group("deptTime").substring(0,2) 
						+ ":" + matcher.group("deptTime").substring(2);
				flight.setDeptTime(deptTime);
			}
			if (null != matcher.group("arrTime")) {
				String arrTime = matcher.group("arrTime").substring(0,2) 
						+ ":" + matcher.group("arrTime").substring(2);
				flight.setArrTime(arrTime);
			}
			if (matcher.group("nights") != null)
				flight.setNights(matcher.group("nights").substring(1));
			if (matcher.group("deptTerminal") != null && !matcher.group("deptTerminal").equals("  ") && 
					!matcher.group("deptTerminal").equals("--"))
				flight.setDeptTerminal(matcher.group("deptTerminal"));
			if (matcher.group("arrTerminal") != null && !matcher.group("arrTerminal").equals("  ") && 
					!matcher.group("arrTerminal").equals("--"))
				flight.setArrTerminal(matcher.group("arrTerminal"));
			if (matcher.group("subCabin") != null)
				flight.setSubCabin(matcher.group("subCabin"));
			flightList.add(flight);
		}
		if (flightList.size() == 0) {
			LOGGER.error("PNR解析：航班信息解析失败！解析文本：{}", text);
		}
		flightInfo.setFlights(flightList);
		flightInfo.setCarriers(carrierList);
		return flightInfo;
	}
	
	
	public static void main(String[] args) {
		String text0 = "-----------";
		String text1 = " 2.  FM9407 R   FR16JAN15PVGCTU RR1   1700 2025          E T1I ";
		String text2 = " 2.  CA4502 G   SA31JAN  SHACTU RR11  1950 2310+1       E 2 I  ";
		String text3 = " 2.  CA1924 Q   TU17FEB  INCXIY HK1   1735 1845          E --T2 ";
		String text4 = " 2.  CZ6981 D   SA17JAN15URCSHA HK2   1050 1515          SEAME --I1 S ";
		String text5 = " 4.  CA4516 H   MO19JAN  SHACTU UN3   1145 1455          E MT \n"
					+ " 5.  CA4501 G   TU20JAN  CTUSHA TK3   1615 1850          SEAME T1T2 \n";
		String text6 = " 3.  FM815  S   SU08FEB  SHAHND HK2   0905 1230          E T1I  \n"
					+ " 4.  CA158  L   TH12FEB  NRTPVG HK2   0855 1130          E T1T2 \n";
		String text7 = " 2. *MU8985 K   TH22JAN  HKGSHA HK1   1425 1650          E --T1 OP-HX238\n";
		String text8 = " 3.  MU729  V   FR13MAR  PVGKIX HK2   1825 2130+1        E T1-- \n"
					+ " 4.    ARNK              KIXNRT \n"
					+ " 5.  MU524  R   SU22MAR  NRTPVG HK2   1350 1610          E 2 T1 ";
		String text9 = " 4.    OPEN              KIXNRT \n"
					+ " 5.  MU524  R   SU22MAR  NRTPVG HK2   1350 1610          E 2 T1 ";
		String text10 = " 2.  CZ3379 L   TU17MAR15CSXCAN HX1   1920 2035          E  \n"
					+ " 3.  CZ3379 W   TU17MAR15CSXCAN KK1                      E  ";
		// subcabin
		String text11 = " 2.  CA1389 V   FR27MAR  TSNHGH NO1   1010 1210-1          E      V1  ";
		// 隔天到无+号?待确认...
		String text12 = " 5.  TV9824 I   FR05JUN  LXACTU HK4   2215 0020 1        E --T2    ";
		// OPEN && ARNK
		String text13 = " 2.  MUOPEN Y            HKGPVG\n"
					  + " 3.  MUOPEN Y   FR05JUN  HKGPVG\n"
					  + " 4.    ARNK              KIXNRT ";
		// KK状态航班与机场间无空格
		String text14 = " 3.  CZ3379 W   TU17MAR15CSXCANKK1                      E ";
		// 航班号带字母
		String text15 = " 3.  MU729Q  V   FR13MAR  PVGKIX HK2   1825 2130+1        E ";
		
		System.out.println(parse(text0, true).toString());
		System.out.println(parse(text1, true).toString());
		System.out.println(parse(text2, true).toString());
		System.out.println(parse(text3, true).toString());
		System.out.println(parse(text4, false).toString());
		System.out.println(parse(text5, false).toString());
		System.out.println(parse(text6, false).toString());
		System.out.println(parse(text7, true).toString());
		System.out.println(parse(text8, false).toString());
		System.out.println(parse(text9, false).toString());
		System.out.println(parse(text10, true).toString());
		System.out.println(parse(text11, true).toString());
		System.out.println(parse(text12, true).toString());
		System.out.println(parse(text13, false).toString());
		System.out.println(parse(text14, false).toString());
		System.out.println(parse(text15, false).toString());
	}
}
