package com.travelzen.rosetta.eterm.parser.util;

import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * joda_time　工具类
 * <p>
 * @author yiming.yan
 * @Date Nov 19, 2015
 */
public enum DateTimeUtil {
	
	;
	
	private static final DateTimeFormatter DATE_TIME_FORMATTER_ddMMM = 
			DateTimeFormat.forPattern("ddMMM").withLocale(Locale.ENGLISH).withZone(DateTimeZone.forID("+08:00"));
	private static final DateTimeFormatter DATE_TIME_FORMATTER_ddMMMyy = 
			DateTimeFormat.forPattern("ddMMMyy").withLocale(Locale.ENGLISH).withZone(DateTimeZone.forID("+08:00"));
	private static final DateTimeFormatter DATE_TIME_FORMATTER_yyyy_MM_dd = 
			DateTimeFormat.forPattern("yyyy-MM-dd").withLocale(Locale.ENGLISH).withZone(DateTimeZone.forID("+08:00"));
	
	public static DateTime parseToDateTime_ddMMMyy(String dateStr) {
		DateTime dateTime = DATE_TIME_FORMATTER_ddMMMyy.parseDateTime(dateStr);
		return dateTime;
	}
	
	public static DateTime parseToDateTime_ddMMM(String dateStr) {
		DateTime dateTime = DATE_TIME_FORMATTER_ddMMM.parseDateTime(dateStr);
		return dateTime;
	}
	
	public static DateTime parseToDateTime_yyyy_MM_dd(String dateStr) {
		DateTime dateTime = DATE_TIME_FORMATTER_yyyy_MM_dd.parseDateTime(dateStr);
		return dateTime;
	}
	
	/**
	 * to format: yyyy-MM-dd
	 */
	public static String parseFromDateTime_yyyy_MM_dd(DateTime dateTime) {
		return dateTime.toString(DATE_TIME_FORMATTER_yyyy_MM_dd);
	}
	
	/**
	 * to format: HH:mm
	 */
	public static String getTime(String time) {
		return time.substring(0, 2) + ":" + time.substring(2,4);
	}
	
	public static void main(String[] args) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("ddMMM").withLocale(Locale.ENGLISH);
		DateTime dateTime = dateTimeFormatter.parseDateTime("13OCT");
		System.out.println(dateTime);
		System.out.println(dateTime.isAfterNow());
		System.out.println(dateTime.withYear(2015));
		System.out.println(DateTime.now());
		System.out.println(DateTime.now().getYear());
		System.out.println(DateTime.now().toString("ddMMM", Locale.ENGLISH));
	}

}
