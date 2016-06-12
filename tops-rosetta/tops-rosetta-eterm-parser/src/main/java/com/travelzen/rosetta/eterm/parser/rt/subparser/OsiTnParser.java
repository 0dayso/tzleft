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
 * OSI票号信息解析
 * @author yiming.yan
 */
public enum OsiTnParser {

	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OsiTnParser.class);
	
	public static List<String> parse(String text) {
		List<String> l = new ArrayList<String>();
		Matcher matcher_single = Pattern.compile(
				"OSI .+? TN/(\\d{3}-?\\d+)").matcher(text);
		Matcher matcher_group = Pattern.compile(
				"OSI .+? TN/(\\d{7,})-(\\d{7,})").matcher(text);
		if (matcher_group.find()) {
			l.add(matcher_group.group(1));
			l.add(matcher_group.group(2));
		} else if (matcher_single.find()) {
			l.add(matcher_single.group(1).replaceAll("-", ""));
		}
		return l;
	}
	
	public static PassengerInfo parse(String text, PassengerInfo passengerInfo) {
		if (null == passengerInfo)
			return null;
		// 此处验证，若总票号数可除尽乘客数，就不解析OSI票号
		if (null != passengerInfo.getTicketInfos() && 
				(passengerInfo.getTicketInfos().size() % passengerInfo.getPassengers().size() == 0))
			return passengerInfo;
		Set<TicketInfo> ticketInfos = new HashSet<TicketInfo>();
		Matcher matcher_single = Pattern.compile(
				"OSI .+? TN/(\\d{3}-?\\d+)").matcher(text);
		Matcher matcher_group = Pattern.compile(
				"OSI .+? TN/(\\d{7,})-(\\d{7,})").matcher(text);
		if (matcher_group.find()) {
			long startIdx = Long.parseLong(matcher_group.group(1));
			long endIdx = Long.parseLong(matcher_group.group(2));
			if (passengerInfo.getPassengers().size() != (endIdx-startIdx+1)) {
				LOGGER.error("PNR解析：OSI票号信息与乘客人数不一致！解析文本：{}", text);
				return passengerInfo;
			}
			List<TicketInfo> tktNos = new ArrayList<TicketInfo>();
			TicketInfo tkt = new TicketInfo();
			int psgNo = 1;
			for (long idx=startIdx; idx<=endIdx; idx++) {
				tkt.setPsgNo(psgNo);
				tkt.setSegmentNo(1);
				tkt.setTktNo(String.valueOf(idx));
				tktNos.add(tkt);
				ticketInfos.add(tkt);
				passengerInfo.getPassengers().get(psgNo-1).setTktNos(tktNos);
				psgNo++;
				tkt = new TicketInfo();
				tktNos = new ArrayList<TicketInfo>();
			}
		} else if (matcher_single.find()) {
			if (passengerInfo.getPassengers().size() != 1) {
				LOGGER.error("PNR解析：OSI票号信息与乘客人数不一致！解析文本：{}", text);
				return passengerInfo;
			}
			List<TicketInfo> tktNos = new ArrayList<TicketInfo>();
			TicketInfo tkt = new TicketInfo();
			tkt.setPsgNo(1);
			tkt.setSegmentNo(1);
			tkt.setTktNo(matcher_single.group(1));
			tktNos.add(tkt);
			ticketInfos.add(tkt);
			passengerInfo.getPassengers().get(0).setTktNos(tktNos);
		}
		if (0 != ticketInfos.size())
			passengerInfo.setTicketInfos(ticketInfos);
		return passengerInfo;
	}
	
	public static void main(String[] args) {
		String s1 = "14.OSI 1E MUET TN/7812133427202  ";
		String s2 = "13.OSI 1E MUET TN/781-2133427202    ";
		String s3 = "15.OSI 1E TN/876-1234554321   ";
		String s4 = "31.OSI 1E MUET TN/7812189770471-7812189770480   ";
		String s5 = "31.OSI 1E CAET TN/9992119347860-9992119347861   ";
		
		System.out.println(parse(s1));
		System.out.println(parse(s2));
		System.out.println(parse(s3));
		System.out.println(parse(s4));
		System.out.println(parse(s5));
	}
	
}
