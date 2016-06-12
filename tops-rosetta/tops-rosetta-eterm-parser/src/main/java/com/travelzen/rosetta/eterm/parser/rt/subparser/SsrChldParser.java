package com.travelzen.rosetta.eterm.parser.rt.subparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.rosetta.eterm.common.pojo.enums.PassengerType;
import com.travelzen.rosetta.eterm.common.pojo.rt.PassengerInfo;
import com.travelzen.rosetta.eterm.common.pojo.rt.PassengerInfo.Passenger;
import com.travelzen.rosetta.eterm.parser.util.RtDateTimeUtil;

/**
 * SSR 儿童信息解析
 * @author yiming.yan
 */
public enum SsrChldParser {
	
	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SsrChldParser.class);
	
	public static PassengerInfo parse(String text, PassengerInfo passengerInfo) {
		Matcher matcher = Pattern.compile(""
				+ " (?<birth>\\d{2}[A-Z]{3}\\d{2})/P(?<psgNo>\\d+)").matcher(text);
		while (matcher.find()) {
			int psgNo = Integer.parseInt(matcher.group("psgNo"));
			Pair<Boolean, Integer> ifExist = ifExist(psgNo, passengerInfo.getPassengers());
			if (ifExist.getValue0()) {
				passengerInfo.getPassengers().get(ifExist.getValue1()).setPsgType(PassengerType.CHD);
				passengerInfo.getPassengers().get(ifExist.getValue1()).setBirthday(RtDateTimeUtil.parseDate(matcher.group("birth")));;
			}
		}
		return passengerInfo;
	}
	
	private static Pair<Boolean, Integer> ifExist(int psgNo, List<Passenger> passengers) {
		for (int i = 0; i < passengers.size(); i++) {
			Passenger passenger = passengers.get(i);
			if (psgNo == passenger.getPsgNo())
				return Pair.with(true, i);
		}
		return Pair.with(false, 0);
	}
	
	public static void main(String[] args) {
		String text0 = "";
		String text1 = "27.SSR CHLD MU HK1 26FEB08/P2  \n";
		
		List<Passenger> passengers = new ArrayList<Passenger>();
		PassengerInfo passengerInfo = new PassengerInfo();
		passengerInfo.setPassengers(passengers);
		System.out.println(parse(text0, passengerInfo));
		passengerInfo.getPassengers().clear();
		System.out.println(parse(text1, passengerInfo));
	}
	
}
