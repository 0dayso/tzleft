/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.cpecker.tool;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import com.travelzen.farerule.rule.WeekDayEnum;
import com.travelzen.framework.core.time.DateTimeUtil;

public class DateTransducer {
	
	public static long parseDateCn(String dateStr) {
		//string = "2014年5月19日"
		long longDate = 0;
		if (dateStr == null)
			return longDate;
		dateStr = dateStr.replaceAll(" +", "");		
		try{
			Pattern pattern1 = Pattern.compile("(\\d+)年(\\d+)月(\\d+)日");
			Pattern pattern2 = Pattern.compile("(\\d+)月(\\d+)日");
			Matcher matcher1 = pattern1.matcher(dateStr);
			Matcher matcher2 = pattern2.matcher(dateStr);
			DateTime dateTime = new DateTime();
			if (matcher1.find()) {
				String dateString = matcher1.group(1)+to2Digits(matcher1.group(2))+to2Digits(matcher1.group(3));
				dateTime = DateTimeUtil.getDate(dateString, "yyyyMMdd", Locale.ENGLISH);
				longDate = DateTimeUtil.getDateTimestamp(dateTime, 2);
			} else if (matcher2.find()) {
				String dateString = to2Digits(matcher2.group(1))+to2Digits(matcher2.group(2));
				dateTime = DateTimeUtil.getDate(dateString, "MMdd", Locale.ENGLISH);
				longDate = DateTimeUtil.getDateTimestamp(dateTime, 2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return longDate;
	}
	
	private static String to2Digits(String str) {
		if (str.length() == 1)
			return "0" + str;
		return str;
	}
	
	public static WeekDayEnum parseWeekCnToEnum(String Weekday) {
		HashMap<String, WeekDayEnum> weekmap = new HashMap<String, WeekDayEnum>();
		weekmap.put("一", WeekDayEnum.MON);
		weekmap.put("二", WeekDayEnum.TUE);
		weekmap.put("三", WeekDayEnum.WED);
		weekmap.put("四", WeekDayEnum.THU);
		weekmap.put("五", WeekDayEnum.FRI);
		weekmap.put("六", WeekDayEnum.SAT);
		weekmap.put("日", WeekDayEnum.SUN);
		weekmap.put("天", WeekDayEnum.SUN);
		if (weekmap.containsKey(Weekday))
			return weekmap.get(Weekday);
		return null;
	}

}
