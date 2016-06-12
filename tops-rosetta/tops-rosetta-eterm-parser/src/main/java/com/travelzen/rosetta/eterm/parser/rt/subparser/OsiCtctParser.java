package com.travelzen.rosetta.eterm.parser.rt.subparser;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OSI联系方式信息解析
 * @author yiming.yan
 */
public enum OsiCtctParser {
	
	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OsiCtctParser.class);
	
	private static Pattern pattern_domestic = Pattern.compile("CTCT ?([\\d -]+[\\d])");
	private static Pattern pattern_international = Pattern.compile("CTC[A-Z]? ?.*?([\\d -]+[\\d])");
	
	public static Set<String> parse(String text, boolean isDomestic) {
		Set<String> ctctSet = new HashSet<String>();
		Pattern pattern = null;
		if (isDomestic)
			pattern = pattern_domestic;
		else
			pattern = pattern_international;
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			ctctSet.add(matcher.group(1));
		}
		if (ctctSet.size() == 0)
			LOGGER.error("PNR解析：OSI联系方式信息解析失败！解析文本：{}", text);
		return ctctSet;
	}
	
	public static void main(String[] args) {
		String text0 = "";
		String text1 = " 8.OSI FM CTCT 13636341467   \n"
				+ " 9.OSI FM CTCT057-156888688  \n";
		String text2 = "23.OSI CA CTCT6227681 005   \n"
				+ "24.OSI CA CTCM13801933791        ";
		String text3 = "16.OSI KL CTCM 86-13651774376 \n"
				+ "17.OSI KL CTC SHA/13651774376/P1";
		
		System.out.println(parse(text0, true));
		System.out.println(parse(text1, true));
		System.out.println(parse(text2, true));
		System.out.println(parse(text3, false));
	}
	
}
