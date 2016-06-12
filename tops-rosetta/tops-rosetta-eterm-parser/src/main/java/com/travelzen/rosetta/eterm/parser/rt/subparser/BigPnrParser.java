package com.travelzen.rosetta.eterm.parser.rt.subparser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.rosetta.eterm.common.pojo.rt.BigPnr;

/**
 * PNR大编码解析
 * @author yiming.yan
 */
public enum BigPnrParser {

	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BigPnrParser.class);
	
	// 多个票价等级-》多个大编码
	public static Set<BigPnr> parse(String text) {
		Set<BigPnr> bigPnrs = new HashSet<BigPnr>();
		Pattern pattern = Pattern.compile("\\d+\\.RMK ([0-9A-Z]{2})/([0-9A-Z]{5,6}) *(?=\n|$)"); // MH大编码是五位的
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			BigPnr bigPnr = new BigPnr();
			bigPnr.setCarrier(matcher.group(1));
			bigPnr.setBigPnr(matcher.group(2));
			bigPnrs.add(bigPnr);
		}
		if (bigPnrs.size() == 0) 
			LOGGER.error("PNR解析：PNR大编码解析失败！解析文本：{}", text);
		return bigPnrs;
	}
	
	public static Set<BigPnr> parse(String text, List<String> carriers) {
		Set<BigPnr> bigPnrs = new HashSet<BigPnr>();
		Pattern pattern = Pattern.compile("\\d+\\.RMK ([0-9A-Z]{2})/([0-9A-Z]{5,6}) *(?=\n|$)"); // MH大编码是五位的
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			BigPnr bigPnr = new BigPnr();
			if (carriers.contains(matcher.group(1)))
				bigPnr.setCarrier(matcher.group(1));
			bigPnr.setBigPnr(matcher.group(2));
			bigPnrs.add(bigPnr);
		}
		if (bigPnrs.size() == 0) 
			LOGGER.error("PNR解析：PNR大编码解析失败！解析文本：{}", text);
		return bigPnrs;
	}
	
	public static void main(String[] args) {
		String text0 = "21.RMK 1A/REJECTED-MESSAGE IGNORED  ";
		String text1 = "26.RMK CA/NGTTQ2";
		String text2 = "13.RMK PR/NW0HBL";
		String text3 = "23.RMK 1A/X7A2LY";
		String text4 = "21.OSI YY CTCT749   \n"
				+ "22.RMK TJ SHA255         \n"                                                      
				+ "23.RMK CA/NFWWT7                     \n"                                          
				+ "24.RMK PG/EBWPOX\n"
				+ "25.RMK TJ AUTH SHA255  ";
		String text5 = "25.RMK MH/L2LE7 ";
		String text6 = "12.RMK AK HDQJQ Q8DRQI ";
		
		System.out.println(parse(text0));
		System.out.println(parse(text1));
		System.out.println(parse(text2));
		System.out.println(parse(text3, Arrays.asList("MU")));
		System.out.println(parse(text4, Arrays.asList("CA", "PG")));
		System.out.println(parse(text5));
		System.out.println(parse(text6));
	}
	
}
