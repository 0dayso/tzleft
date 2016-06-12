package com.travelzen.rosetta.eterm.parser.rt.subparser;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 授权office信息解析
 * @author yiming.yan
 */
public enum AuthOfficeParser {
	
	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthOfficeParser.class);
	
	public static Set<String> parse(String text) {
		Set<String> authSet = new HashSet<String>();
		Pattern pattern = Pattern.compile("\\d+\\.RMK TJ(?: AUTH)? ([A-Z0-9]{6})");
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			authSet.add(matcher.group(1));
		}
//		if (authSet.size() == 0)
//			LOGGER.error("PNR解析：授权office信息解析失败！解析文本：{}", text);
		return authSet;
	}
	
	public static void main(String[] args) {
		String text0 = "";
		String text1 = "13.RMK TJ SHA255 \n"
				+ "14.RMK CA/NHMEWV \n"
				+ "15.RMK TJ AUTH SHA255   \n";
		String text2 = "11.RMK TJ SHA255               \n"                                                
				+ "12.RMK 1A/YNQQEF\n"
				+ "13.RMK TJ AUTH BJS914/T \n"
				+ "14.RMK TJ AUTH SHA255/BJS914";
		
		System.out.println(parse(text0));
		System.out.println(parse(text1));
		System.out.println(parse(text2));
	}
	
}
