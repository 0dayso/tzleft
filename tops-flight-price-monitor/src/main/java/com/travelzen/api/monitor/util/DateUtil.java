package com.travelzen.api.monitor.util;

import java.util.Locale;

import org.joda.time.DateTime;

public class DateUtil {
	
	public static String getAvailDate(String pattern) {
		DateTime dateTime = DateTime.now().plusDays(15);
		return dateTime.toString(pattern, Locale.ENGLISH).toUpperCase();
	}
	
	public static void main(String[] args) {
		System.out.println(getAvailDate("yyyy-MM-dd"));
	}

}
