package com.travelzen.rosetta.eterm.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.rosetta.eterm.common.pojo.EtermRtktResponse;
import com.travelzen.rosetta.eterm.common.pojo.EtermRtktResponse.Flight;
import com.travelzen.rosetta.eterm.common.pojo.EtermRtktResponse.PsgType;
import com.travelzen.rosetta.eterm.common.pojo.EtermRtktResponse.Flight.Stopover;
import com.travelzen.rosetta.eterm.parser.util.RegexUtil;

/**
 * Eterm RTKT　指令解析类
 * <p>
 * @author yiming.yan
 * @Date Mar 2, 2016
 */
public class EtermRtktParser {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EtermRtktParser.class);
	
	public static EtermRtktResponse parse(String text, boolean isDomestic) {
		LOGGER.info("Eterm RTKT　解析请求　是否国内：{}，文本：{}", isDomestic, text);
		if (text.trim().length() < 80) {
			return new EtermRtktResponse(false, "Eterm RTKT 失败: " + text.trim());
		}
		RtktText rtktText = splitRtktText(text, isDomestic);
		if (null == rtktText) {
			LOGGER.error("Eterm RTKT　文本异常！");
			return new EtermRtktResponse(false, "Eterm RTKT　文本异常！");
		}
		LOGGER.info("Eterm RTKT　分割文本：{}", rtktText);
		EtermRtktResponse response = new EtermRtktResponse();
		try {
			if (rtktText.psg.contains(" CHD "))
				response.setPsgType(PsgType.CHD);
			response = parseFare(response, rtktText.fare, isDomestic);
			if (!isDomestic)
				response = parseNucStr(response, rtktText.nucStr);
			response = parseFlights(response, rtktText.flights, isDomestic);
			if (null != response.getFlights())
				response.setSuccess(true);
			else {
				LOGGER.error("Eterm RTKT　解析错误！");
				response.setSuccess(false);
				response.setErrorMsg("Eterm RTKT　解析错误！");
			}
		} catch (Exception e) {
			LOGGER.error("Eterm RTKT　解析异常：" + e.getMessage(), e);
			response.setSuccess(false);
			response.setErrorMsg("Eterm RTKT　解析异常！");
		}
		LOGGER.info("Eterm RTKT　解析结果：{}", response);
		return response;
	}
	
	private static final Pattern PATTERN_NUC = Pattern.compile("(?<nuc>.+?END)");
	
	private static RtktText splitRtktText(String text, boolean isDomestic) {
		String[] lines = text.trim().split("[\r\n]+");
		if (lines.length < 15)
			return null;
		RtktText rtktText = new RtktText();
		int psgIndex = getPsgIndex(lines, isDomestic);
		if (0 == psgIndex)
			return null;
		rtktText.psg = lines[psgIndex];
		int fareIndex = getFareIndex(lines, isDomestic);
		// 连续票号只有第一张票有价格
		if (0 != fareIndex)
			rtktText.fare = lines[fareIndex];
		StringBuilder flightsBuilder = new StringBuilder();
		int flightsIndex = getFlightsIndex(lines, isDomestic);
		if (0 == flightsIndex)
			return null;
		for (int i = flightsIndex; i < flightsIndex + 5; i++) {
			flightsBuilder.append(lines[i]).append("\n");
		}
		rtktText.flights = flightsBuilder.toString();
		if (!isDomestic) {
			StringBuilder nucBuilder = new StringBuilder();
			for (int i = 10; i < 15; i++) {
				if (lines[i].trim().length() == 0)
					continue;
				if (lines[i].trim().length() < 12)
					break;
				if (i > 10 && !lines[i].substring(0, 1).equals(" ")) {
					if (RegexUtil.startsWith(lines[i], "CNY? "))
						nucBuilder.append(lines[i].substring(12));
					else
						nucBuilder.append(" ").append(lines[i]);
				} else {
					String line = lines[i].substring(15);
					line = RegexUtil.trimSuffix(line);
					nucBuilder.append(line);
				}
			}
			if (RegexUtil.startsWith(nucBuilder.toString(), "[0-9]{2}[A-Z]{3}[0-9]{2}")) {
				Matcher matcher = PATTERN_NUC.matcher(nucBuilder.toString());
				if (matcher.find())
					rtktText.nucStr = matcher.group("nuc");
				else {
					LOGGER.error("Eterm RTKT Nuc行解析失败!");
					return null;
				}
			} else {
				LOGGER.error("Eterm RTKT Nuc行解析失败!");
				return null;
			}
		}
		return rtktText;
	}
	
	private static int getPsgIndex(String[] lines, boolean isDomestic) {
		for (int i = 2; i < 7; i++) {
			if (RegexUtil.endsWith(lines[i].trim(), "DEV-\\d+"))
				return i;
		}
		return 0;
	}

	private static int getFareIndex(String[] lines, boolean isDomestic) {
		if (isDomestic) {
			for (int i = lines.length - 1; i > 15; i--) {
				String[] tokens = lines[i].trim().split(" +");
				if (tokens.length == 3 && RegexUtil.startsWith(tokens[0], "[A-Z]{3}") 
						&& tokens[1].length() < 5 && tokens[1].matches("\\d+") && tokens[2].matches("\\d+\\.\\d+"))
					return i;
				if (tokens.length == 2 && RegexUtil.startsWith(tokens[0], "[A-Z]{3}") 
						&& tokens[1].length() < 5 && tokens[1].matches("\\d+"))
					return i;
				if (tokens.length == 4 && tokens[0].matches("[A-Z]{3}") 
						&& tokens[2].length() < 5 && tokens[2].matches("\\d+") && tokens[3].matches("\\d+\\.\\d+"))
					return i;
			}
		} else {
			for (int i = lines.length - 1; i > 4; i--) {
				String[] tokens = lines[i].trim().split(" +");
				if (tokens.length == 4 && RegexUtil.startsWith(tokens[1], "[A-Z]{3}") 
						&& tokens[2].length() < 5 && tokens[2].matches("\\d+") && tokens[3].matches("\\d+\\.\\d+"))
					return i;
				if (tokens.length == 3 && RegexUtil.startsWith(tokens[1], "[A-Z]{3}") 
						&& tokens[2].length() < 5 && tokens[2].matches("\\d+"))
					return i;
			}
		}
		return 0;
	}

	private static int getFlightsIndex(String[] lines, boolean isDomestic) {
		String regex = null;
		if (isDomestic)
			regex = "   [ X] (?:[\\u4e00-\\u9fa5]+ +[A-Z]{3} |[A-Z]{3}\\?? {9,11}[A-Z]{3}  [A-Z0-9]{2}[ \\*] *\\d)";
		else
			regex = "   [ X] [A-Z][A-Z ]{13}[A-Z]{3}(?:[A-Z0-9]{2}[ \\*] *\\d| +VOID)";
		for (int i = 4; i < lines.length; i++) {
			if (RegexUtil.startsWith(lines[i], regex))
				return i;
		}
		return 0;
	}

	private static EtermRtktResponse parseFare(EtermRtktResponse response, String fareText, boolean isDomestic) {
		if (null == fareText)
			return response;
		fareText = fareText.replaceAll("([A-Z]{3}) +(\\d+\\.\\d+)", "$1$2");
		String[] tokens = fareText.trim().split(" +");
		int i = 1;
		if (isDomestic)
			i = 0;
		if (tokens[i].length() > 3)
			response.setFare(Double.parseDouble(tokens[i].substring(3)));
		response.setCommission(Double.parseDouble(tokens[i+1]));
		if (tokens.length == i+3)
			response.setTax(Double.parseDouble(tokens[i+2]));
		return response;
	}

	private static EtermRtktResponse parseNucStr(EtermRtktResponse response, String nucStrText) {
		response.setNucStr(nucStrText);
		return response;
	}
	
	private static final Pattern PATTERN_FLIGHT_D = Pattern.compile(
			"(?<airport>[A-Z]{3})  (?<airCompany>[A-Z0-9]{2})(?<share>[ \\*]) *(?<flightNo>[0-9A-Z]+) "
			+ "(?<cabin>[A-Z]) (?<deptDate>[0-9]{2}[A-Z]{3}) (?<deptTime>\\d{4})  ");
	private static final Pattern PATTERN_FLIGHT_I = Pattern.compile(
			"(?<airport>[A-Z]{3})(?<airCompany>[A-Z0-9]{2})(?<share>[ \\*]) *(?<flightNo>[0-9A-Z]+) "
			+ "(?<cabin>[A-Z]) (?<deptDate>[0-9]{2}[A-Z]{3}) (?<deptTime>\\d{4})  ");

	private static EtermRtktResponse parseFlights(EtermRtktResponse response, String flightsText, boolean isDomestic) {
		Pattern pattern = null;
		if (isDomestic)
			pattern = PATTERN_FLIGHT_D;
		else
			pattern = PATTERN_FLIGHT_I;
		List<String> lines = new ArrayList<String>();
		for (String line:flightsText.split("\n")) {
			if (line.trim().length() == 0)
				continue;
			lines.add(line);
		}
		List<Flight> flights = new ArrayList<Flight>();
		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line.startsWith("     VOID"))
				break;
			Flight flight = new Flight();
			String preLine = lines.get(i-1);
			if (preLine.trim().endsWith("VOID"))
				continue;
			if (!isDomestic)
				preLine = preLine.substring(19);
			Matcher matcher_dept = pattern.matcher(preLine);
			if (matcher_dept.find()) {
				flight.setDeptAirport(matcher_dept.group("airport"));
				flight.setAirCompany(matcher_dept.group("airCompany"));
				if (matcher_dept.group("share").equals("*"))
					flight.setShared(true);
				flight.setFlightNo(matcher_dept.group("flightNo"));
				flight.setCabin(matcher_dept.group("cabin"));
				flight.setDeptDate(matcher_dept.group("deptDate"));
				flight.setDeptTime(matcher_dept.group("deptTime"));
				if (isDomestic) {
					Matcher matcher_arra = Pattern.compile(" (?<arra>[A-Z0-9]{3}) ").matcher(line);
					if (matcher_arra.find())
						flight.setArrAirport(matcher_arra.group("arra"));
				} else
					flight.setArrAirport(line.substring(19, 22));
				flights.add(flight);
			} else {
				LOGGER.info("航班解析失败：{}", lines.get(i-1));
				return response;
			}
		}
		if (flights.size() != 0) {
			flights = handleStopover(flights);
			response.setFlights(flights);
		}
		return response;
	}

	private static List<Flight> handleStopover(List<Flight> flights) {
		List<Stopover> stopovers = new ArrayList<Stopover>();
		for (int i = 1; i < flights.size(); ) {
			Flight preFlight = flights.get(i-1);
			Flight flight = flights.get(i);
			if (flight.getAirCompany().equals(preFlight.getAirCompany()) && flight.getFlightNo().equals(preFlight.getFlightNo())) {
				flights.get(i-1).setArrAirport(flight.getArrAirport());
				Stopover stopover = new Stopover();
				stopover.setAirport(flight.getArrAirport());
				stopover.setAirCompany(flight.getAirCompany());
				stopover.setShared(flight.isShared());
				stopover.setFlightNo(flight.getFlightNo());
				stopover.setCabin(flight.getCabin());
				stopover.setDeptDate(flight.getDeptDate());
				stopover.setDeptTime(flight.getDeptTime());
				stopovers.add(stopover);
				flights.remove(i);
				continue;
			}
			if (0 != stopovers.size()) {
				flights.get(i-1).setStopovers(stopovers);
				stopovers = new ArrayList<Stopover>();
			}
			i++;
		}
		if (0 != stopovers.size()) {
			flights.get(flights.size()-1).setStopovers(stopovers);
		}
		return flights;
	}

	private static class RtktText {
		String psg;
		String flights;
		String nucStr;
		String fare;
		
		@Override
		public String toString() {
			return "RtktText [psg=" + psg + ", flights=" + flights
					+ ", nucStr=" + nucStr + ", fare=" + fare + "]";
		}
	}

}
