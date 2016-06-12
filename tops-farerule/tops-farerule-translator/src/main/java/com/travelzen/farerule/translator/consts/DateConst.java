package com.travelzen.farerule.translator.consts;

import java.util.HashMap;
import java.util.Map;

import com.travelzen.farerule.rule.TimeTypeEnum;
import com.travelzen.farerule.rule.WeekDayEnum;

public class DateConst {

	public final static Map<TimeTypeEnum, String> stayTimeTypeMap = new HashMap<TimeTypeEnum, String>() {
		private static final long serialVersionUID = 2116127722023101597L;
	{
		put(TimeTypeEnum.DAY, "天");
		put(TimeTypeEnum.MONTH, "个月");
		put(TimeTypeEnum.YEAR, "年");
	}};
	
	public final static Map<WeekDayEnum, String> weekMap = new HashMap<WeekDayEnum, String>() {
		private static final long serialVersionUID = 7919075926197931809L;
	{
		put(WeekDayEnum.MON, "周一");
		put(WeekDayEnum.TUE, "周二");
		put(WeekDayEnum.WED, "周三");
		put(WeekDayEnum.THU, "周四");
		put(WeekDayEnum.FRI, "周五");
		put(WeekDayEnum.SAT, "周六");
		put(WeekDayEnum.SUN, "周日");
		put(WeekDayEnum.EVE, "每天");
	}};
}
