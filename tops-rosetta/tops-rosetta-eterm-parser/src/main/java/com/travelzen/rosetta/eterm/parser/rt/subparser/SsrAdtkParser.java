package com.travelzen.rosetta.eterm.parser.rt.subparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.rosetta.eterm.parser.util.RtDateTimeUtil;

/**
 * 出票时间限制解析
 * @author yiming.yan
 */
public enum SsrAdtkParser {

	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SsrAdtkParser.class);
	
	private static String MONTH = "(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)";
	
	public static String parse(String text, boolean isDomestic) {
		String adtk = null;
		Pattern pattern1 = Pattern.compile(
				"BY (?:[A-Z]{3})?"
				+ "(?<time>\\d{4})[/ ]"
				+ "(?<date>\\d{2}" + MONTH + ")"
				+ "(?:(?:20)?(?<year>\\d{2}))?");
		Pattern pattern2 = Pattern.compile(
				"BY (?:[A-Z]{3})?"
				+ "(?<date>\\d{2}" + MONTH + ")"
				+ "(?:(?:20)?(?<year>\\d{2}))?"
				+ "[/ ](?<time>\\d{4})");
		Pattern pattern3 = Pattern.compile(
				" (?<time>\\d{4})[/ ]"
				+ "(?<date>\\d{2}" + MONTH + ")"
				+ "(?:(?:20)?(?<year>\\d{2}))?");
		Pattern pattern4 = Pattern.compile(
				"(?<date>\\d{2}" + MONTH + ")"
				+ "(?:(?:20)?(?<year>\\d{2}))?"
				+ "[/ ](?<time>\\d{4})");
		Matcher matcher1 = pattern1.matcher(text);
		Matcher matcher2 = pattern2.matcher(text);
		Matcher matcher3 = pattern3.matcher(text);
		Matcher matcher4 = pattern4.matcher(text);
		if (matcher1.find()) {
			String dateStr = matcher1.group("date");
			if (matcher1.group("year") != null)
				dateStr += matcher1.group("year");
			dateStr += matcher1.group("time");
			adtk = RtDateTimeUtil.parseDateTime(dateStr);
		} else if (matcher2.find()) {
			String dateStr = matcher2.group("date");
			if (matcher2.group("year") != null)
				dateStr += matcher2.group("year");
			dateStr += matcher2.group("time");
			adtk = RtDateTimeUtil.parseDateTime(dateStr);
		} else if (matcher3.find()) {
			String dateStr = matcher3.group("date");
			if (matcher3.group("year") != null)
				dateStr += matcher3.group("year");
			dateStr += matcher3.group("time");
			adtk = RtDateTimeUtil.parseDateTime(dateStr);
		} else if (matcher4.find()) {
			String dateStr = matcher4.group("date");
			if (matcher4.group("year") != null)
				dateStr += matcher4.group("year");
			dateStr += matcher4.group("time");
			adtk = RtDateTimeUtil.parseDateTime(dateStr);
		} else if (!isDomestic) {
			Matcher matcher_zone = Pattern.compile(
					"(?<date>\\d{2}" + MONTH + ")"
					+ "(?:(?:20)?(?<year>\\d{2}))?"
					+ "(?: (?<zone>GMT))?(?: (?<time>\\d{4}))?").matcher(text);
			Matcher matcher_im = Pattern.compile(
					"TICKET IMMEDIATELY").matcher(text);
			if (matcher_zone.find()) {
				String dateStr = matcher_zone.group("date");
				if (matcher_zone.group("year") != null)
					dateStr += matcher_zone.group("year");
				if (matcher_zone.group("time") != null)
					dateStr += matcher_zone.group("time");
				adtk = RtDateTimeUtil.parseDateTime(dateStr);
				// Time zone
				if (matcher_zone.group("zone") != null)
					adtk += " " + matcher_zone.group("zone");
			} else if (matcher_im.find()) {
				adtk = "IMMEDIATELY";
			}
		} else
			LOGGER.error("PNR解析：出票时间限制解析失败！解析文本：{}", text);
		return adtk;
	}
	
	public static void main(String[] args) {
		String text0 = "10.SSR ADTK 1E BY SHA10FEB15/1026 OR CXL 3U8844 L10FEB  ";
		String text1 = " 6.SSR ADTK 1E BY SHA16JAN15/0227 OR CXL FM9407 R16JAN  \n";
		String text2 = "16.SSR ADTK 1E BY SHA16JAN15/0034 OR CXL CA ALL SEGS\n";
		String text3 = " 9.SSR ADTK 1E ADV TKT NBR TO CX/KA BY 15JAN GMT 2359 OR SUBJECT TO CANCEL     ";
		String text4 = "13.SSR ADTK 1E HK1 . UA - PLEASE TICKET BY 19JAN2015/0459Z  ";
		String text5 = "10.SSR ADTK 1E JL S-CLS BY XX 18JAN XX USING SSR OR WILL BE XLD           ";
		String text6 = " 9.SSR ADTK 1E PLS ADV TKT BY 20JAN15 1500CN OR WL BE CXLD  ";
		String text7 = " 8.SSR ADTK 1E TO OZ BY 15JAN 2100 OTHERWISE WILL BE XLD";
		String text8 = " 6.SSR ADTK 1E BOOK AND BUY PLEASE TICKET IMMEDIATELY   ";
		String text9 = "  9.SSR ADTK 1E PLZ ADVC PAX EMAIL ADDRESS N MOBILE PHONE NO FOR SKJ CHG INFO\n"
				+ "10.SSR ADTK 1E PLZ ADTK B4 1200/13MAR FR PVGKHHCI0582H04JUL OR AUTO CANC EL \n"
				+ "DZEND";
		String text10 = "12.SSR ADTK 1E IO-0890/09SEP15 BY 24AUG/1801Z OR CNL";
		
		System.out.println(parse(text0, true));
		System.out.println(parse(text1, true));
		System.out.println(parse(text2, true));
		System.out.println(parse(text3, false));
		System.out.println(parse(text4, false));
		System.out.println(parse(text5, false));
		System.out.println(parse(text6, false));
		System.out.println(parse(text7, false));
		System.out.println(parse(text8, false));
		System.out.println(parse(text9, false));
		System.out.println(parse(text10, false));
	}
	
}
