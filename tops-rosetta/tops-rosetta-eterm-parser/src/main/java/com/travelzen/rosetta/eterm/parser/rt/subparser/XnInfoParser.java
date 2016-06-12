package com.travelzen.rosetta.eterm.parser.rt.subparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.rosetta.eterm.common.pojo.enums.PassengerType;
import com.travelzen.rosetta.eterm.common.pojo.rt.PassengerInfo;
import com.travelzen.rosetta.eterm.common.pojo.rt.PassengerInfo.Passenger;

/**
 * XN 婴儿信息解析
 * @author yiming.yan
 */
public enum XnInfoParser {
	
	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XnInfoParser.class);
	
	public static PassengerInfo parse(String text, PassengerInfo passengerInfo, boolean isDomestic) {
		Pattern pattern = null;
		boolean hasInf = false;
		if (isDomestic)
			pattern = Pattern.compile(
					"XN/IN/(?<name>[\\u4e00-\\u9fa5 A-Z/]+?) ?(?:INF)? ?\\([A-Z]{3}\\d{2}\\)/P(?<foPsgNo>\\d+)");
		else
			pattern = Pattern.compile(
					"XN/IN/(?<name>[A-Z/ ]+?) ?(?:INF)? ?\\([A-Z]{3}\\d{2}\\)/P(?<foPsgNo>\\d+)");
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			hasInf = true;
			String name = matcher.group("name");
			int foPsgNo = Integer.parseInt(matcher.group("foPsgNo"));
			int infIdx = getTheInfIdx(passengerInfo.getPassengers(), foPsgNo);
			if (0 != infIdx) {
				passengerInfo.getPassengers().get(infIdx).setName(name);
			} else {
				Passenger infant = new Passenger(PassengerType.INF);
				infant.setName(name);
				infant.setFoPsgNo(foPsgNo);
				passengerInfo.getPassengers().add(infant);
			}
		}
		if (!hasInf) {
			LOGGER.error("PNR解析：XN 婴儿信息解析失败！解析文本：{}", text);
		}
		return passengerInfo;
	}
	
	private static int getTheInfIdx(List<Passenger> passengers, int foPsgNo) {
		for (int i = 0; i < passengers.size(); i++) {
			Passenger passenger = passengers.get(i);
			if (passenger.getFoPsgNo() == foPsgNo) {
				return i;
			}
		}
		return 0;
	}
	
	public static void main(String[] args) {
		String text0 = "";
		String text1 = "27.XN/IN/DUAN/YUXUANINF(OCT13)/P1  ";
		String text2 = "32.XN/IN/WU/DEMI ZIINF(JUL13)/P1";
		String text3 = "21.XN/IN/TANG/WEN INF(MAR13)/P1 ";
		String text4 = "49.XN/IN/JI/MINGXI(FEB14)/P2     ";
		String text5 = "33.XN/IN/WEI/MURUI INF (DEC14)/P1";
		
		List<Passenger> passengers = new ArrayList<Passenger>();
		PassengerInfo passengerInfo = new PassengerInfo();
		passengerInfo.setPassengers(passengers);
		System.out.println(parse(text0, passengerInfo, false));
		passengerInfo.getPassengers().clear();
		System.out.println(parse(text1, passengerInfo, true));
		passengerInfo.getPassengers().clear();
		System.out.println(parse(text2, passengerInfo, false));
		passengerInfo.getPassengers().clear();
		System.out.println(parse(text3, passengerInfo, false));
		passengerInfo.getPassengers().clear();
		System.out.println(parse(text4, passengerInfo, false));
		passengerInfo.getPassengers().clear();
		System.out.println(parse(text5, passengerInfo, false));
	}
	
}
