package com.travelzen.rosetta.eterm.parser.rt.subparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.rosetta.eterm.common.pojo.enums.PassengerIdType;
import com.travelzen.rosetta.eterm.common.pojo.rt.PassengerInfo;
import com.travelzen.rosetta.eterm.common.pojo.rt.PassengerInfo.PassengerId;

/**
 * 国内证件信息解析
 * @author yiming.yan
 */
public enum SsrFoidParser {

	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SsrFoidParser.class);
	
	public static List<PassengerId> parse(String text) {
		List<PassengerId> foidList = new ArrayList<PassengerId>();
		Pattern pattern = Pattern.compile("(?:NI|PP)([0-9A-Z]+)/P(\\d{1,2})");
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			PassengerId passengerId = new PassengerId();
			passengerId.setId(matcher.group(1));
			passengerId.setPsgNo(Integer.parseInt(matcher.group(2)));
			if (matcher.group(1).length() == 18) {
				if (Character.isDigit(matcher.group(1).charAt(16))) {
					if (Integer.parseInt(matcher.group(1).substring(16, 17))%2 == 1)
						passengerId.setSex("M");
					else
						passengerId.setSex("F");
				}
			}
			foidList.add(passengerId);
			
		}
		if (foidList.size() == 0)
			LOGGER.error("PNR解析：国内证件信息解析失败！解析文本：{}", text);
		return foidList;
	}
	
	public static PassengerInfo parse(String text, PassengerInfo passengerInfo) {
		if (null == passengerInfo)
			return null;
		Pattern pattern = Pattern.compile("(?<type>NI|PP)(?<id>[0-9A-Z]+)/P(?<psgNo>\\d{1,2})");
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			int psgNo = Integer.parseInt(matcher.group("psgNo"));
			if (passengerInfo.getPassengers().size() >= psgNo) {
				if (matcher.group("type").equals("NI"))
					passengerInfo.getPassengers().get(psgNo-1).setIdType(PassengerIdType.NI);
				else if (matcher.group("type").equals("PP"))
					passengerInfo.getPassengers().get(psgNo-1).setIdType(PassengerIdType.PP);
				else
					passengerInfo.getPassengers().get(psgNo-1).setIdType(PassengerIdType.OTHER);
				passengerInfo.getPassengers().get(psgNo-1).setId(matcher.group("id"));
				if (matcher.group("id").length() == 18) {
					if (Character.isDigit(matcher.group("id").charAt(16))) {
						if (Integer.parseInt(matcher.group("id").substring(16, 17))%2 == 1)
							passengerInfo.getPassengers().get(psgNo-1).setSex("M");
						else
							passengerInfo.getPassengers().get(psgNo-1).setSex("F");
					}
				}
			} else {
				LOGGER.error("PNR解析：国内证件信息解析失败！乘客序号不匹配！解析文本：{}", matcher.group());
			}
		}
		return passengerInfo;
	}
	
	public static void main(String[] args) {
		String text0 = " 5.SSR FOID FM HK1 PPG30688/P1 ";
		String text1 = " 5.SSR FOID FM HK1 NIG30688074/P1   ";
		String text2 = "8.SSR FOID HU HK1 NI110108193804165480/P1 \n"
				+ "9.SSR FOID HU HK1 NI1101081936110354LX/P2 ";
		try {
			System.out.println(parse(text0));
			System.out.println(parse(text1));
			System.out.println(parse(text2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
