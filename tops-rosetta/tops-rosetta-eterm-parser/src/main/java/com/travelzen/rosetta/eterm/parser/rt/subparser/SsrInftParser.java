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
 * SSR 婴儿信息解析
 * @author yiming.yan
 */
public enum SsrInftParser {
	
	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SsrInftParser.class);
	
	public static PassengerInfo parse(String text, PassengerInfo passengerInfo) {
		int psgNum = passengerInfo.getPassengers().size();
		Matcher matcher = Pattern.compile(""
				+ "[A-Z]\\d{2}[A-Z]{3} (?<name>[A-Z /]+) (?<birth>\\d{2}[A-Z]{3}\\d{2})/P(?<foPsgNo>\\d+)").matcher(text);
		while (matcher.find()) {
			int foPsgNo = Integer.parseInt(matcher.group("foPsgNo"));
			Pair<Boolean, Integer> ifExist = ifExist(foPsgNo, passengerInfo.getPassengers());
			if (ifExist.getValue0()) {
				passengerInfo.getPassengers().get(ifExist.getValue1()).setName(matcher.group("name"));
				continue;
			}
			Passenger infant = new Passenger(PassengerType.INF);
			infant.setName(matcher.group("name"));
			infant.setBirthday(RtDateTimeUtil.parseDate(matcher.group("birth")));
			infant.setFoPsgNo(foPsgNo);
			passengerInfo.getPassengers().add(infant);
		}
		if (passengerInfo.getPassengers().size() == psgNum) {
			LOGGER.error("PNR解析：SSR婴儿解析未增加新乘客！解析文本：{}", text);
		}
		return passengerInfo;
	}
	
	private static Pair<Boolean, Integer> ifExist(int foPsgNo, List<Passenger> passengers) {
		for (int i = 0; i < passengers.size(); i++) {
			Passenger passenger = passengers.get(i);
			if (foPsgNo == passenger.getFoPsgNo())
				return Pair.with(true, i);
		}
		return Pair.with(false, 0);
	}
	
	public static void main(String[] args) {
		String text0 = "";
		String text1 = " 8.SSR INFT CA  KK1 PEKCTU 4194 Y29JUN XIAOFEN/XIAO 22DEC14/P1\n";
		String text2 = " 8.SSR INFT CA  KK1 HFEPEK 931 Y02OCT YI/SU 12JAN15/P1           \n";
		String text3 = "24.SSR INFT MU HK1 PVGJZH 2227 B22JUN LU/CHIENHSIN 19JUN14/P1\n"
				+ "25.SSR INFT MU HK1 JZHXIY 2368 B25JUN LU/CHIENHSIN 19JUN14/P1\n"
				+ "26.SSR INFT MU HK1 XIYPVG 2167 N29JUN LU/CHIENHSIN 19JUN14/P1";
		
		List<Passenger> passengers = new ArrayList<Passenger>();
		PassengerInfo passengerInfo = new PassengerInfo();
		passengerInfo.setPassengers(passengers);
		System.out.println(parse(text0, passengerInfo));
		passengerInfo.getPassengers().clear();
		System.out.println(parse(text1, passengerInfo));
		passengerInfo.getPassengers().clear();
		System.out.println(parse(text2, passengerInfo));
		passengerInfo.getPassengers().clear();
		System.out.println(parse(text3, passengerInfo));
	}
	
}
