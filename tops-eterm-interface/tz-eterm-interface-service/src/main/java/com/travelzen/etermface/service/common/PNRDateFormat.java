/**
 * 
 */
package com.travelzen.etermface.service.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * @author hongqiangmao
 * 
 */
public class PNRDateFormat {
	public  static Map<String, String> MONTHS_MAP = new HashMap<String, String>();
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	private static final long ONE_DAY = 24*60*60*1000l;
	
	static {
		MONTHS_MAP.put("01", "JAN");
		MONTHS_MAP.put("02", "FEB");
		MONTHS_MAP.put("03", "MAR");
		MONTHS_MAP.put("04", "APR");
		MONTHS_MAP.put("05", "MAY");
		MONTHS_MAP.put("06", "JUN");
		MONTHS_MAP.put("07", "JUL");
		MONTHS_MAP.put("08", "AUG");
		MONTHS_MAP.put("09", "SEP");
		MONTHS_MAP.put("10", "OCT");
		MONTHS_MAP.put("11", "NOV");
		MONTHS_MAP.put("12", "DEC");
	}

	public static String dayMonthFormat(String pDate) {
		if (null == pDate) {
			return null;
		}
		String lvRegex = "\\d{4}-\\d{2}-\\d{2}";
		if (!pDate.matches(lvRegex)) {
			return null;
		}
		String[] lvStrs = pDate.split("-");
		String month = MONTHS_MAP.get(lvStrs[1]);
		if(null == month){
			return null;
		}
		return lvStrs[2]+month;
	}
	
	public static String monthYearFormat(String date){
		if (null == date) {
			return null;
		}
		String regex = "\\d{4}-\\d{2}-\\d{2}";
		if (!date.matches(regex)) {
			return null;
		}
		
		String[] strs = date.split("-");
		String month = MONTHS_MAP.get(strs[1]);
		if(null == month){
			return null;
		}
		String year = strs[0].substring(2);
		return month+year;
	}
	
	public static String dayMonthYearFormat(String date){
		if (null == date) {
			return null;
		}
		String regex = "\\d{4}-\\d{2}-\\d{2}";
		if (!date.matches(regex)) {
			return null;
		}
		
		String[] strs = date.split("-");
		String month = MONTHS_MAP.get(strs[1]);
		if(null == month){
			return null;
		}
		String year = strs[0].substring(2);
		return strs[2]+month+year;
	}
	
	
	public static String timeFormat(String pTime) {
		
		if(StringUtils.isBlank(pTime)) {
			return "";
		}
		
		String lvRegex = "(\\d{2}):(\\d{2})";
		Pattern timePattern = Pattern.compile(lvRegex);
		Matcher timeMatcher = timePattern.matcher(pTime);
		
		if(!timeMatcher.find()) {
			return "";
		}
		
		return timeMatcher.group(1)+timeMatcher.group(2);
	}
	
	public static String addDays(String date,int n){
		if(null == date || 0 == n ){
			return date;
		}
		
		String regex = "\\d{4}-\\d{2}-\\d{2}";
		if (!date.matches(regex)) {
			return date;
		}
		
		String day = null;
		try {
			//加一天
			Date d = sdf.parse(date);
			long timeMillis = d.getTime();
			timeMillis += ONE_DAY*n;
			d = new Date(timeMillis);
			day = sdf.format(d);
		} catch (ParseException e) {
		}
		return day;
	}
	
	public static void main(String[] args){
		System.out.println(dayMonthFormat("2011-09-09"));
		System.out.println(timeFormat("12:50"));
		System.out.println(dayMonthYearFormat("2011-09-09"));
		System.out.println(monthYearFormat("2011-09-09"));
		System.out.println(addDays("2011-09-09",-7));
	}
}
