package com.travelzen.rosetta.eterm.parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javatuples.Pair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.rosetta.eterm.common.pojo.EtermSkResponse;
import com.travelzen.rosetta.eterm.common.pojo.EtermSkResponse.Flight;
import com.travelzen.rosetta.eterm.common.pojo.EtermSkResponse.Flight.FlightSegment;
import com.travelzen.rosetta.eterm.common.pojo.enums.EtermCmdSource;
import com.travelzen.rosetta.eterm.parser.util.DateTimeUtil;
import com.travelzen.rosetta.eterm.parser.util.RegexUtil;

/**
 * Eterm SK　指令解析类
 * <p>
 * @author yiming.yan
 * @Date Nov 17, 2015
 */
public enum EtermSkParser {
	
	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EtermSkParser.class);
	
	private static final Pattern FIRST_LINE_PATTERN = Pattern.compile(
			"^(?<deptDate>\\d{2}[A-Z]{3}(?:\\d{2})?)\\(?:[A-Z]{3}\\)/"
			+ "(?<destDate>\\d{2}[A-Z]{3}(?:\\d{2})?)\\(?:[A-Z]{3}\\) ?"
			+ "(?<deptAirport>[A-Z]{3})(?<destAirport>[A-Z]{3})"
			+ "(?: VIA (?<airCompany>[A-Z0-9]{2}))?"
			+ "(?<isDirect> DIRECT ONLY)?");
	
	/**
	 * 解析Eterm SK文本
	 * <p>
	 * @param text Eterm SK文本
	 * @param showMore SK/SK:H
	 * @param etermCmdSource Eterm SK文本来源
	 * @return EtermSkResponse
	 */
	public static EtermSkResponse parse(String text, boolean showMore, EtermCmdSource etermCmdSource) {
		LOGGER.info("开始解析Eterm SK 文本：{}", text);
		long startTime = new Date().getTime();
		if (null == text || text.equals("")) {
			LOGGER.info("Eterm SK　文本为空：{}", text);
			return new EtermSkResponse(false, "Eterm SK 文本为空！");
		}
		if (etermCmdSource == EtermCmdSource.UFIS && text.startsWith("UFIS-ERROR")) {
			LOGGER.info("Ufis Error: " + text);
			return new EtermSkResponse(false, text);
		} else if (text.contains("组帐号错误或没有可用PID")) {
			LOGGER.info("Eterm Error: " + text);
			return new EtermSkResponse(false, text);
		}
		EtermSkResponse etermSkResponse = new EtermSkResponse();
		String fromDate = null, toDate = null, deptAirport = null, destAirport = null, airCompany = null;
		boolean isDirect = false;
		Pair<String, List<String>> pureText = preprocess(text);
		LOGGER.info("预处理后Eterm SK 文本：{}", pureText);
		if (pureText.getValue0() == null) {
			LOGGER.info("Eterm SK　文本首行无效：{}", pureText.getValue0());
			return new EtermSkResponse(false, "Eterm SK　解析异常！");
		}
		Matcher firstLineMatcher = FIRST_LINE_PATTERN.matcher(pureText.getValue0().trim());
		if (firstLineMatcher.find()) {
			DateTime deptDateTime = null;
			if (firstLineMatcher.group("deptDate").length() == 7) {
				deptDateTime = DateTimeUtil.parseToDateTime_ddMMMyy(firstLineMatcher.group("deptDate"));
			} else if (firstLineMatcher.group("deptDate").length() == 5) {
				deptDateTime = DateTimeUtil.parseToDateTime_ddMMM(firstLineMatcher.group("deptDate"));
				deptDateTime = deptDateTime.withYear(DateTime.now().getYear());
			}
			if (deptDateTime == null) {
				LOGGER.info("Eterm SK　文本首行开始日期解析失败：{}", pureText.getValue0());
				return new EtermSkResponse(false, "Eterm SK　解析异常！");
			}
			fromDate = DateTimeUtil.parseFromDateTime_yyyy_MM_dd(deptDateTime);
			etermSkResponse.setFromDate(fromDate);
			DateTime destDateTime = null;
			if (firstLineMatcher.group("destDate").length() == 7) {
				destDateTime = DateTimeUtil.parseToDateTime_ddMMMyy(firstLineMatcher.group("destDate"));
			} else if (firstLineMatcher.group("destDate").length() == 5) {
				destDateTime = DateTimeUtil.parseToDateTime_ddMMM(firstLineMatcher.group("destDate"));
				destDateTime = destDateTime.withYear(DateTime.now().getYear());
			}
			if (destDateTime == null) {
				LOGGER.info("Eterm SK　文本首行结束日期解析失败：{}", pureText.getValue0());
				return new EtermSkResponse(false, "Eterm SK　解析异常！");
			}
			toDate = DateTimeUtil.parseFromDateTime_yyyy_MM_dd(destDateTime);
			etermSkResponse.setToDate(toDate);
			deptAirport = firstLineMatcher.group("deptAirport");
			destAirport = firstLineMatcher.group("destAirport");
			if (firstLineMatcher.group("airCompany") != null)
				airCompany = firstLineMatcher.group("airCompany");
			if (firstLineMatcher.group("isDirect") != null)
				isDirect = true;
		} else {
			LOGGER.info("Eterm SK　文本首行解析失败：{}", pureText.getValue0());
			return new EtermSkResponse(false, "Eterm SK　解析异常！");
		}
		List<Flight> flights = new ArrayList<Flight>();
		for (String line:pureText.getValue1()) {
			Flight flight = parseFlight(line, showMore, deptAirport, destAirport, airCompany, isDirect);
			if (null != flight)
				flights.add(flight);
		}
		if (0 == flights.size()) {
			LOGGER.info("Eterm SK　文本未解析出有效结果：{}", pureText.getValue1());
			return new EtermSkResponse(false, "Eterm SK　解析异常！");
		}
		etermSkResponse.setFlights(flights);
		etermSkResponse.setSuccess(true);
		long endTime = new Date().getTime();
		LOGGER.info("PNR解析用时：{}ms", endTime-startTime);
		LOGGER.info("Eterm SK　文本解析结果：{}", etermSkResponse);
		return etermSkResponse;
	}
	
	private static Pair<String, List<String>> preprocess(String text) {
		String[] lines = text.split("[\n\r]+");
		String firstLine = lines[0].contains("Qp!")?null:lines[0];
		List<String> items = new ArrayList<String>();
		int index = -1;
		for (String line:lines) {
			if (RegexUtil.startsWith(line, "\\s*\\*{2}|b|\\?|[\\u4e00-\\u9fa5]") || line.contains("Qp!")) {
				continue;
			}
			if (RegexUtil.startsWith(line, " ?\\d{2}[A-Z]")) {
				if (null == firstLine)
					firstLine = line;
				else if (!line.trim().equals(firstLine.trim()))
					break;
				continue;
			}
			if (RegexUtil.startsWith(line, "\\d[ \\-\\+]")) {
				index++;
				items.add(line);
			} else if (items.size() > 0 && index == (items.size() - 1)) {
				items.set(index, items.get(index) + "\n" + line);
			} else {
				LOGGER.error("未处理文本：{}", line);
			}
		}
		return Pair.with(firstLine, items);
	}

	private static Flight parseFlight(String text, boolean showMore, String deptAirport, 
			String destAirport, String airCompany, boolean isDirect) {
		Flight flight = new Flight();
		List<FlightSegment> flightSegments = new ArrayList<FlightSegment>();
		String[] lines = text.trim().split("\n");
		if (lines.length == 0) {
			LOGGER.error("Eterm SK Flight　文本为空：{}", text);
			return null;
		}
		String transitAirport = null;
		String preDuration = null;
		String preDestTime = null;
		String preDestDate = null;
		for (String line:lines) {
			FlightSegment flightSegment = null;
			if (showMore)
				flightSegment = parseFlightSegmentForH(line, transitAirport, preDuration, preDestTime, preDestDate);
			else
				flightSegment = parseFlightSegment(line, transitAirport);
			if (null != flightSegment) {
				flightSegments.add(flightSegment);
				transitAirport = flightSegment.getDestAirport();
			}
		}
		if (0 == flightSegments.size()) {
			LOGGER.info("Eterm SK　Flight　文本未解析出有效结果：{}", text);
			return null;
		} else if (1 == flightSegments.size()) {
			flight.setTransit(false);
		} else {
			flight.setTransit(true);
		}
		flight.setFromDate(flightSegments.get(0).getFromDate());
		flight.setToDate(flightSegments.get(0).getToDate());
		flight.setDeptAirport(deptAirport);
		flight.setDestAirport(destAirport);
		if (isDirect && airCompany == null)
			airCompany = flightSegments.get(0).getAirCompany();
		if (airCompany != null)
			flight.setAirCompany(airCompany);
		flight.setFlightSegments(flightSegments);
		return flight;
	}
	
	private static final Pattern SIMPLE_PATTERN = Pattern.compile(
			"^[\\d ][ \\-\\+] (?<isShared>[ \\*])"
			+ "(?<airCompany>[A-Z0-9]{2})(?<flightNum>[0-9A-Z]+) +"
			+ "(?<deptAirport>[A-Z]{3})?(?<destAirport>[A-Z]{3}) "
			+ "(?<deptTime>\\d{4})(?<deptNights>[\\+ ]\\d)? +"
			+ "(?<destTime>\\d{4})(?<destNights>[\\+ ]\\d)? +"
			+ "(?<aircraftType>[A-Z0-9]+) "
			+ "(?<stopover>\\d)(?<reserveSeat>.)(?<meal>[A-Z])?"
			+ " +E +"
			+ "(?<valid>X?\\d+)?(?: +(?<fromDate>\\d{2}[A-Z]{3})(?<toDate>\\d{2}[A-Z]{3}))?");
	
	private static FlightSegment parseFlightSegment(String line, String transitAirport) {
		FlightSegment flightSegment = new FlightSegment();
		Matcher matcher = SIMPLE_PATTERN.matcher(line);
		if (matcher.find()) {
			if (matcher.group("isShared").equals("*"))
				flightSegment.setShared(true);
			else
				flightSegment.setShared(false);
			flightSegment.setAirCompany(matcher.group("airCompany"));
			flightSegment.setFlightNum(matcher.group("flightNum"));
			if (matcher.group("deptAirport") != null)
				flightSegment.setDeptAirport(matcher.group("deptAirport"));
			else if (transitAirport != null)
				flightSegment.setDeptAirport(transitAirport);
			flightSegment.setDestAirport(matcher.group("destAirport"));
			flightSegment.setDeptTime(
					DateTimeUtil.getTime(matcher.group("deptTime")));
			flightSegment.setDestTime(
					DateTimeUtil.getTime(matcher.group("destTime")));
			flightSegment.setAircraftType(matcher.group("aircraftType"));
			if (matcher.group("stopover") != null) {
				if (matcher.group("stopover").equals("0"))
					flightSegment.setStopover(false);
				else
					flightSegment.setStopover(true);
			}
			if (matcher.group("reserveSeat") != null) {
				if (matcher.group("reserveSeat").equals("^"))
					flightSegment.setReserveSeat(true);
				else
					flightSegment.setReserveSeat(false);
			}
			if (matcher.group("valid") != null)
				flightSegment.setValidDays(matcher.group("valid"));
			if (matcher.group("fromDate") != null)
				flightSegment.setFromDate(matcher.group("fromDate"));
			if (matcher.group("toDate") != null)
				flightSegment.setToDate(matcher.group("toDate"));
		}
		return flightSegment;
	}
	
	private static final Pattern PREFIX_PATTERN = Pattern.compile(
			"^[\\d ][ \\-\\+] (?<isShared>[ \\*])"
			+ "(?<airCompany>[A-Z0-9]{2})(?<flightNum>[0-9A-Z]+) +[^ ]* "
			+ "(?<cabinInfo>(?:[A-Z0-9]{2} )+) +"
			+ "(?<deptAirport>[A-Z]{3})?(?<destAirport>[A-Z]{3}) "
			+ "(?<deptTime>\\d{4})(?<deptNights>[\\+ ]\\d)? +"
			+ "(?<destTime>\\d{4})(?<destNights>[\\+ ]\\d)? +"
			+ "(?<aircraftType>[A-Z0-9]+) "
			+ "(?<stopover>\\d)(?<reserveSeat>.)?(?<meal>[A-Z])?");
	private static final Pattern SUFFIX_PATTERN = Pattern.compile(
			"(?:(?<opAirCompany>[A-Z0-9]{2})(?<opFlightNum>[0-9A-Z]+) +)?"
			+ "(?<cabinInfo>(?:[A-Z0-9]{2} )*) {3,}"
			+ "(?<deptTerminal>[A-Z0-9\\-]{1,2})? *(?<destTerminal>[A-Z0-9\\-]{1,2})? *"
			+ "(?<duration>\\d+:\\d+)?$");
	
	private static FlightSegment parseFlightSegmentForH(String line, String transitAirport, 
			String preDuration, String preDestTime, String preDestDate) {
		FlightSegment flightSegment = new FlightSegment();
//		String[] segments = line.split(">", 2);
//		if (segments.length < 2) {
//			LOGGER.error("\">\"分割错误：{}", line);
//			return null;
//		}
//		String prefix = segments[0];
//		Matcher prefixMatcher = PREFIX_PATTERN.matcher(prefix);
//		if (prefixMatcher.find()) {
//			if (prefixMatcher.group("isShared").equals("*"))
//				flightSegment.setShared(true);
//			else
//				flightSegment.setShared(false);
//			flightSegment.setAirCompany(prefixMatcher.group("airCompany"));
//			flightSegment.setFlightNum(prefixMatcher.group("flightNum"));
//			flightSegment.setCabinInfo(
//					parseCabinInfo(prefixMatcher.group("cabinInfo"), new HashMap<String, String>()));
//			if (prefixMatcher.group("deptAirport") != null)
//				flightSegment.setDeptAirport(prefixMatcher.group("deptAirport"));
//			else if (transitAirport != null)
//				flightSegment.setDeptAirport(transitAirport);
//			flightSegment.setDestAirport(prefixMatcher.group("destAirport"));
//			flightSegment.setDeptTime(
//					DateTimeUtil.getTime(prefixMatcher.group("deptTime")));
//			flightSegment.setDestTime(
//					DateTimeUtil.getTime(prefixMatcher.group("destTime")));
//			if (prefixMatcher.group("deptNights") != null && originDeptDate != null) {
//				String deptDate = getNewDate(originDeptDate, Integer.parseInt(prefixMatcher.group("deptNights")));
//				flightSegment.setDeptDate(deptDate);
//			} else {
//				flightSegment.setDeptDate(originDeptDate);
//			}
//			if (prefixMatcher.group("destNights") != null && originDeptDate != null) {
//				String destDate = getNewDate(originDeptDate, Integer.parseInt(prefixMatcher.group("destNights")));
//				flightSegment.setDestDate(destDate);
//			} else {
//				flightSegment.setDestDate(originDeptDate);
//			}
//			flightSegment.setAircraftType(prefixMatcher.group("aircraftType"));
//			if (prefixMatcher.group("stopover") != null) {
//				if (prefixMatcher.group("stopover").equals("0"))
//					flightSegment.setStopover(false);
//				else
//					flightSegment.setStopover(true);
//			}
//			if (prefixMatcher.group("reserveSeat") != null) {
//				if (prefixMatcher.group("reserveSeat").equals("^"))
//					flightSegment.setReserveSeat(true);
//				else
//					flightSegment.setReserveSeat(false);
//			}
//			if (prefixMatcher.group("meal") != null)
//				flightSegment.setMeal(prefixMatcher.group("meal"));
//		} else {
//			LOGGER.error("前半行解析失败：{}", prefix);
//			return null;
//		}
//		String suffix = segments[1];
//		if (" ".equals(suffix.charAt(suffix.length()-1) + ""))
//			suffix = suffix.substring(0, segments[1].lastIndexOf(" "));
//		Matcher suffixMatcher = SUFFIX_PATTERN.matcher(suffix);
//		if (suffixMatcher.find()) {
//			if (suffixMatcher.group("opAirCompany") != null)
//				flightSegment.setOpAirCompany(suffixMatcher.group("opAirCompany"));
//			if (suffixMatcher.group("opFlightNum") != null)
//				flightSegment.setOpFlightNum(suffixMatcher.group("opFlightNum"));
//			if (suffixMatcher.group("cabinInfo") != null) {
//				flightSegment.setCabinInfo(
//						parseCabinInfo(suffixMatcher.group("cabinInfo"), flightSegment.getCabinInfo()));
//			}
//			if (null != suffixMatcher.group("deptTerminal") && null != suffixMatcher.group("destTerminal")) {
//				flightSegment.setDeptTerminal(suffixMatcher.group("deptTerminal"));
//				flightSegment.setDestTerminal(suffixMatcher.group("destTerminal"));
//			}
//			if (preDuration != null && preDestTime != null && preDestDate != null) {
//				DateTime preDestDateTime = DateTimeUtil.parseToDateTime_yyyy_MM_dd(preDestDate);
//				DateTime deptDateTime = DateTimeUtil.parseToDateTime_yyyy_MM_dd(flightSegment.getDeptDate());
//				Period period = new Period(preDestDateTime, deptDateTime, PeriodType.days());
//				flightSegment.setDuration(getDuration(
//						preDuration, preDestTime, flightSegment.getDeptTime(), period.getDays(), suffixMatcher.group("duration")));
//			} else
//				flightSegment.setDuration(suffixMatcher.group("duration"));
//		} else if (suffix.trim().matches(" *(?:[A-Z0-9]{2} )*[A-Z0-9]{2}")) {
//			flightSegment.setCabinInfo(
//					parseCabinInfo(suffix, flightSegment.getCabinInfo()));
//		} else if (suffix.matches("")) {
//			LOGGER.debug("后半行信息为空：{}", suffix);
//		} else {
//			LOGGER.error("后半行解析失败：{}", suffix);
//			return null;
//		}
		return flightSegment;
	}

	private static Map<String, String> parseCabinInfo(String text, Map<String, String> cabinInfo) {
		String[] cabins = text.trim().split(" ");
		for (String cabin:cabins) {
			if (cabin.length() == 2) {
				cabinInfo.put(cabin.charAt(0)+"", cabin.charAt(1)+"");
			}
		}
		return cabinInfo;
	}
	
	private static String getNewDate(String deptDate, int nights) {
		DateTime dateTime = DateTimeUtil.parseToDateTime_yyyy_MM_dd(deptDate);
		dateTime = dateTime.plusDays(nights);
		return DateTimeUtil.parseFromDateTime_yyyy_MM_dd(dateTime);
	}
	
	private static String getDuration(String preDuration, String preDestTime, String deptTime, 
			int periodDays, String totalDuration) {
		String transitDuration = durationMinus(deptTime, preDestTime);
		String tempDuration = durationMinus(totalDuration, preDuration);
		if (periodDays != 0) {
			String period = periodDays*24 + ":00";
			tempDuration = durationMinus(tempDuration, period);
		}
		return durationMinus(tempDuration, transitDuration);
	}

	private static String durationMinus(String longer, String shorter) {
		int minusMinutes = getMinutes(longer) - getMinutes(shorter);
		String result = minusMinutes/60 + ":" + minusMinutes%60;
		return result;
	}
	
	private static int getMinutes(String duration) {
		String[] tokens = duration.split(":");
		return Integer.parseInt(tokens[0])*60 + Integer.parseInt(tokens[1]);
	}

}
