package com.travelzen.etermface.service.util;

import java.util.Locale;

import org.javatuples.Pair;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * joda_time　工具类
 * <p>
 * @author yiming.yan
 * @Date Dec 16, 2015
 */
public enum DateTimeUtil {
	
	;
	
	private static DateTimeFormatter dateTimeFormatter1 = DateTimeFormat.forPattern("ddMMMyy").withLocale(Locale.ENGLISH).withZone(DateTimeZone.forID("+08:00"));
	private static DateTimeFormatter dateTimeFormatter2 = DateTimeFormat.forPattern("ddMMM").withLocale(Locale.ENGLISH).withZone(DateTimeZone.forID("+08:00"));
	private static DateTimeFormatter dateTimeFormatter3 = DateTimeFormat.forPattern("ddMMMyyHHmm").withLocale(Locale.ENGLISH).withZone(DateTimeZone.forID("+08:00"));
	private static DateTimeFormatter dateTimeFormatter4 = DateTimeFormat.forPattern("ddMMMHHmm").withLocale(Locale.ENGLISH).withZone(DateTimeZone.forID("+08:00"));
	
	public static String parseDate(String dateStr) throws IllegalFieldValueException {
		DateTime dateTime = null;
		if (dateStr.length() == 7) {
			dateTime = dateTimeFormatter1.parseDateTime(dateStr);
		} else if (dateStr.length() == 5) {
			dateTime = dateTimeFormatter2.parseDateTime(dateStr);
			dateTime = dateTimeWithYear(dateTime);
		}
		if (null == dateTime)
			return null;
		return dateTime.toString("yyyy-MM-dd", Locale.ENGLISH);
	}
	
	public static Pair<String, String> parseDates(String dateStr, int nights, String deptTime) throws IllegalFieldValueException {
		DateTime dateTime = null;
		if (dateStr.length() == 7) {
			dateTime = dateTimeFormatter1.parseDateTime(dateStr);
		} else if (dateStr.length() == 5) {
			dateTime = dateTimeFormatter2.parseDateTime(dateStr);
			DateTime fullDatetime = dateTimeFormatter4.parseDateTime(dateStr + deptTime);
			fullDatetime = dateTimeWithYear(fullDatetime);
			dateTime = dateTime.withYear(fullDatetime.getYear());
		}
		if (null == dateTime)
			return null;
		String date0 = dateTime.toString("yyyy-MM-dd", Locale.ENGLISH);
		if (0 != nights)
			dateTime = dateTime.plusDays(nights);
		String date1 = dateTime.toString("yyyy-MM-dd", Locale.ENGLISH);
		return Pair.with(date0, date1);
	}
	
	public static String parseDateTime(String dateStr) throws IllegalFieldValueException {
		String date = null;
		DateTime dateTime = null;
		if (dateStr.length() == 11) {
			dateTime = dateTimeFormatter3.parseDateTime(dateStr);
			date = dateTime.toString("yyyy-MM-dd HH:mm", Locale.ENGLISH);
		} else if (dateStr.length() == 9) {
			dateTime = dateTimeFormatter4.parseDateTime(dateStr);
			dateTime = dateTimeWithYear(dateTime);
			date = dateTime.toString("yyyy-MM-dd HH:mm", Locale.ENGLISH);
		} else if (dateStr.length() == 7) {
			dateTime = dateTimeFormatter1.parseDateTime(dateStr);
			date = dateTime.toString("yyyy-MM-dd", Locale.ENGLISH);
		} else if (dateStr.length() == 5) {
			dateTime = dateTimeFormatter2.parseDateTime(dateStr);
			dateTime = dateTimeWithYear(dateTime);
			date = dateTime.toString("yyyy-MM-dd", Locale.ENGLISH);
		}
		return date;
	}
	
	private static DateTime dateTimeWithYear(DateTime dateTime) throws IllegalFieldValueException {
		dateTime = dateTime.withYear(DateTime.now().getYear());
		if (dateTime.isAfterNow())
			return dateTime;
		dateTime = dateTime.withYear(DateTime.now().getYear() + 1);
		return dateTime;
	}
	
	/**
	 * to format: HH:mm
	 */
	public static String getTime(String time) {
		if (time.length() < 4)
			return null;
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
		
		System.out.println(parseDate("13OCT"));
		System.out.println(parseDate("13FEB"));
		
		System.out.println(parseDates("27MAY", 1, "0900"));
		System.out.println(parseDates("27MAY", 2, "1900"));
		
		System.out.println(parseDateTime("13OCT151330"));
		System.out.println(parseDateTime("13FEB1430"));
		
		System.out.println(parseDate("04MAY86"));
	}

}
