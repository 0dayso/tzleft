/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */
 
package com.travelzen.farerule.jpecker.consts;

public class DateConst {

	public final static String weekday = 
		"(?:MON|TUE|WED|THU|FRI|SAT|SUN|MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY)";
	public final static String month = "(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)";
	
	public final static String WEEKDAY = "(\\b" + weekday + "\\b)";
	public final static String WEEKDAYS = "(\\b" + weekday + "(?:\\/" + weekday + "){1,6}\\b)";
	public final static String TIME = "(\\d{3,4}\\s*(?:AM|PM)|NOON|MIDNIGHT)";
	public final static String DATE = "(\\d{2}\\s*" + month + "(?:\\s*\\d{2,4})?)";
}
