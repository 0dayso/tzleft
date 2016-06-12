/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.translator.tool;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.travelzen.farerule.rule.WeekDayEnum;
import com.travelzen.farerule.translator.consts.DateConst;

public class DateTransducer {
	
	public static String longDateToString(long longDate) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		Date date = new Date(longDate*1000);
		return format.format(date);
	}
	
	public static String weekEnumToCn(WeekDayEnum weekDay) {
		if (DateConst.weekMap.containsKey(weekDay))
			return DateConst.weekMap.get(weekDay);
		return null;
	}

}
