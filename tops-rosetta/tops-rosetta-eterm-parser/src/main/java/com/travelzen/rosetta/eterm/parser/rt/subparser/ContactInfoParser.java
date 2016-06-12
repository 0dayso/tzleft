package com.travelzen.rosetta.eterm.parser.rt.subparser;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ContactInfoParser {

	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContactInfoParser.class);
	
	public static String parse(String text) {
		String contact = null;
		if (text.matches(" *"))
			return contact;
		Matcher matcher = Pattern.compile(
				"^ *\\d+\\.([\\w -]+[\\d])(?:/| *$|-?[A-Z])").matcher(text);
		if (matcher.find()) {
			contact = matcher.group(1);
		} else {
			if (!(text.trim().startsWith("T", 2) || text.trim().startsWith("REM", 2)))
				LOGGER.error("PNR解析：联系方式信息解析失败！解析文本：{}", text);
		}
		return contact;
	}
	
	public static Set<String> parseOsi(String text, Set<String> cttSet) {
		if (cttSet == null)
			cttSet = new HashSet<String>();
		// 现在只接受CTCT开头的联系方式
		Pattern pattern = Pattern.compile("CTCT\\s*(\\d[\\d -]+\\d)");
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			if (!cttSet.contains(matcher.group(1)))
				cttSet.add(matcher.group(1));
		}
		if (cttSet.size() == 0)
			LOGGER.error("PNR解析：OSI CT联系方式信息解析失败！解析文本：{}", text);
		return cttSet;
	}
	
	public static Set<String> parseRmk(String text, Set<String> cttSet) {
		if (cttSet == null)
			cttSet = new HashSet<String>();
		Pattern pattern = Pattern.compile("RMK MP ?([\\d -]+[\\d])");
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			if (!cttSet.contains(matcher.group(1)))
				cttSet.add(matcher.group(1));
		}
		if (cttSet.size() == 0)
			LOGGER.error("PNR解析：RMK MP联系方式信息解析失败！解析文本：{}", text);
		return cttSet;
	}
	
	public static void main(String[] args) {
		String text0 = " ";
		String text1 = " 9.057-156888688  ";
		String text2 = " 5.6227681 005   ";
		String text3 = " 6.13801933791        ";
		String text4 = " 6.051250135900SHU  ";
		String text5 = " 5.SHA62327889T ";
		String text6 = " 4.021-51069999X454360/P1  ";
		System.out.println(parse(text0));
		System.out.println(parse(text1));
		System.out.println(parse(text2));
		System.out.println(parse(text3));
		System.out.println(parse(text4));
		System.out.println(parse(text5));
		System.out.println(parse(text6));
		
		String text00 = "";
		String text01 = " 8.OSI FM CTCT 13636341467  \n"
				+ " 9.OSI FM CTCT057-156888688  \n";
		String text02 = "23.OSI CA CTCT6227681 005   \n"
				+ "24.OSI CA CTCM13801933791        ";
		String text03 = "12.OSI CTC                                    -\n"
				+ "13.OSI CZ CT51133636";
		System.out.println(parseOsi(text00, new HashSet<String>()));
		System.out.println(parseOsi(text01, new HashSet<String>()));
		System.out.println(parseOsi(text02, new HashSet<String>()));
		System.out.println(parseOsi(text03, new HashSet<String>()));
		
		String text10 = "";
		String text11 = "11.RMK MP 13501978593   ";
		System.out.println(parseRmk(text10, new HashSet<String>()));
		System.out.println(parseRmk(text11, new HashSet<String>()));
	}
}
