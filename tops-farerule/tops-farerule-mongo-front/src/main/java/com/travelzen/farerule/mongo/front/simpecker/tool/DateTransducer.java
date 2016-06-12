/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.mongo.front.simpecker.tool;

import java.util.*;

import org.joda.time.DateTime;

import com.travelzen.farerule.rule.WeekDayEnum;
import com.travelzen.framework.core.time.DateTimeUtil;

public class DateTransducer {
	
	public static long parseDateSim(String dateStr) {
		//string = "2014.05.19"
		long longDate = 0;
		if (dateStr == null)
			return longDate;
		try{
			DateTime dateTime = new DateTime();
			if (dateStr.matches("\\d{4}\\.\\d{2}\\.\\d{2}")) {
				dateTime = DateTimeUtil.getDate(dateStr, "yyyy.MM.dd", Locale.ENGLISH);
				longDate = DateTimeUtil.getDateTimestamp(dateTime, 2);
			} else if (dateStr.matches("\\d{2}\\.\\d{2}")) {
				dateTime = DateTimeUtil.getDate(dateStr, "MM.dd", Locale.ENGLISH);
				longDate = DateTimeUtil.getDateTimestamp(dateTime, 2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return longDate;
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
		if (weekmap.containsKey(Weekday))
			return weekmap.get(Weekday);
		return null;
	}

}
