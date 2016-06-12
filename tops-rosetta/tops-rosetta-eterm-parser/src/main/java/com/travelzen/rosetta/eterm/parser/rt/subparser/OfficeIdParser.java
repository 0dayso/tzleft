package com.travelzen.rosetta.eterm.parser.rt.subparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PNR OfficeId解析
 * @author yiming.yan
 */
public enum OfficeIdParser {

	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OfficeIdParser.class);
	
	public static String parse(String text) {
		String officeId = null;
		Matcher matcher = Pattern.compile("\\.([0-9A-Z]{6})").matcher(text);
		if (matcher.find())
			officeId = matcher.group(1);
		else 
			LOGGER.error("PNR解析：PNR OfficeId解析失败！解析文本：{}", text);
		return officeId;
	}
	
	public static void main(String[] args) {
		String text0 = "";
		String text1 = "26.SHA255";
		String text2 = "437.SHA255";
		
		System.out.println(parse(text0));
		System.out.println(parse(text1));
		System.out.println(parse(text2));
	}
	
}
