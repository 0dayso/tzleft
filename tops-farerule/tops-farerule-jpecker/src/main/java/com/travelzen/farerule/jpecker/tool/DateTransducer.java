/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.jpecker.tool;

import java.util.*;

import org.joda.time.DateTime;

import com.travelzen.farerule.rule.WeekDayEnum;
import com.travelzen.framework.core.time.DateTimeUtil;

public class DateTransducer {
	
	public static long parseDate(String dateStr) {
		//string = "06 JAN"/"06 JAN14"/"06 JAN2014"
		long longDate = 0;
		if (dateStr == null)
			return longDate;
		dateStr = dateStr.replaceAll(" +", "");		
		try{
			DateTime dateTime = new DateTime();
			if (dateStr.length() == 5) {
				dateTime = DateTimeUtil.getDate(dateStr, "ddMMM", Locale.ENGLISH);
				longDate = DateTimeUtil.getDateTimestamp(dateTime, 2);
			} else if (dateStr.length() == 7) {
				dateTime = DateTimeUtil.getDate(dateStr, "ddMMMyy", Locale.ENGLISH);
				longDate = DateTimeUtil.getDateTimestamp(dateTime, 2);
			} else if (dateStr.length() == 9) {
				dateTime = DateTimeUtil.getDate(dateStr, "ddMMMyyyy", Locale.ENGLISH);
				longDate = DateTimeUtil.getDateTimestamp(dateTime, 2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return longDate;
	}
	
	public static WeekDayEnum parseWeekEnToEnum(String Weekday) {
		HashMap<String, WeekDayEnum> weekmap = new HashMap<String, WeekDayEnum>();
		weekmap.put("MON", WeekDayEnum.MON);
		weekmap.put("TUE", WeekDayEnum.TUE);
		weekmap.put("WED", WeekDayEnum.WED);
		weekmap.put("THU", WeekDayEnum.THU);
		weekmap.put("FRI", WeekDayEnum.FRI);
		weekmap.put("SAT", WeekDayEnum.SAT);
		weekmap.put("SUN", WeekDayEnum.SUN);
		if (weekmap.containsKey(Weekday))
			return weekmap.get(Weekday);
		return null;
	}

}
