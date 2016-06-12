package com.travelzen.rosetta.eterm.parser.rt.subparser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.rosetta.eterm.common.pojo.rt.PassengerInfo;
import com.travelzen.rosetta.eterm.common.pojo.rt.PassengerInfo.TicketInfo;

/**
 * 票号信息解析
 * @author yiming.yan
 */
public enum SsrTkneParser {

	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SsrTkneParser.class);
	
	public static List<TicketInfo> parse(String text) {
		List<TicketInfo> ticketInfoList = new ArrayList<TicketInfo>();
		Pattern pattern = Pattern.compile("(INF)?(\\d+)/(\\d)/P(\\d{1,2})");
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			TicketInfo ticketInfo = new TicketInfo();
			ticketInfo.setPsgNo(Integer.parseInt(matcher.group(4)));
			ticketInfo.setSegmentNo(Integer.parseInt(matcher.group(3)));
			ticketInfo.setTktNo(matcher.group(2));
			ticketInfoList.add(ticketInfo);
		}
		return ticketInfoList;
	}
	
	public static PassengerInfo parse(String text, PassengerInfo passengerInfo) {
		if (null == passengerInfo)
			return null;
		Set<TicketInfo> ticketInfos = new HashSet<TicketInfo>();
		Pattern pattern = Pattern.compile("(INF)?(\\d+)/(\\d)/P(\\d{1,2})");
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			TicketInfo ticketInfo = new TicketInfo();
			ticketInfo.setPsgNo(Integer.parseInt(matcher.group(4)));
			ticketInfo.setSegmentNo(Integer.parseInt(matcher.group(3)));
			ticketInfo.setTktNo(matcher.group(2));
			if (passengerInfo.getPassengers().size() >= ticketInfo.getPsgNo()) {
				if (matcher.group(1) != null) {
					passengerInfo.getPassengers().get(ticketInfo.getPsgNo()-1).getInfTktNos().add(ticketInfo);
				} else {
					passengerInfo.getPassengers().get(ticketInfo.getPsgNo()-1).getTktNos().add(ticketInfo);
				}
			} else {
				LOGGER.error("PNR解析：票号信息解析失败！乘客序号不匹配！解析文本：{}", matcher.group());
			}
			ticketInfos.add(ticketInfo);
		}
		if (0 != ticketInfos.size())
			passengerInfo.setTicketInfos(ticketInfos);
		return passengerInfo;
	}
	
	public static void main(String[] args) {
		String text0 = "";
		String text1 = " 7.SSR TKNE FM HK1 PVGCTU 9407 R16JAN 7816750102156/1/P1";
		String text2 = "17.SSR TKNE CA HK1 SHACTU 4516 H19JAN 9996750105105/1/P3\n"
				+ "18.SSR TKNE CA HK1 SHACTU 4516 H19JAN 9996750105104/1/P2\n"
				+ "19.SSR TKNE CA HK1 SHACTU 4516 H19JAN 9996750105103/1/P1\n"
				+ "20.SSR TKNE CA HK1 CTUSHA 4501 G20JAN 9996750105103/2/P1\n"
				+ "21.SSR TKNE CA HK1 CTUSHA 4501 G20JAN 9996750105104/2/P2\n"
				+ "22.SSR TKNE CA HK1 CTUSHA 4501 G20JAN 9996750105105/2/P3";
		String text3 = " 8.SSR TKNE SK HK1 CDGCPH 566 E23FEB INF1178911853656/1/P1  \n"
				+ " 9.SSR TKNE SK HK1 CDGCPH 566 E23FEB 1178911853655/1/P1 \n"
				+ "10.SSR TKNE SK HK1 CPHPEK 995 E04MAR 1178911853655/2/P1              \n"          
				+ "11.SSR TKNE SK HK1 CPHPEK 995 E04MAR INF1178911853656/2/P1   ";
		try {
			System.out.println(parse(text0));
			System.out.println(parse(text1));
			System.out.println(parse(text2));
			System.out.println(parse(text3));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
