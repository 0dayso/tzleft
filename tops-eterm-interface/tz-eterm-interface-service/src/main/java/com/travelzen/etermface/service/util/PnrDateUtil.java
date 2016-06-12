package com.travelzen.etermface.service.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.travelzen.etermface.service.common.PNRDateFormat;
import com.travelzen.etermface.service.nlp.TimeLimitTokenRepo;
import com.travelzen.etermface.service.nlp.TimeLimitTokenRole;
import com.travelzen.framework.core.exception.BizException;

public class PnrDateUtil {

	static public Pattern MON_3CODE_PATTERN;
	static public String MON_3CODE_PSTR;
	static public String MON_3CODE_PSTR_WITHPAIR;
	static {

		List<String> pstr = Lists.newArrayList();
		for (String mon : PNRDateFormat.MONTHS_MAP.values()) {
			pstr.add("(" + mon + ")");
		}
		MON_3CODE_PSTR = StringUtils.join(pstr, "|");

		MON_3CODE_PSTR_WITHPAIR = "(" + MON_3CODE_PSTR + ")";

		MON_3CODE_PATTERN = Pattern.compile(MON_3CODE_PSTR, Pattern.CASE_INSENSITIVE);

	}

	// SHA03MAY13/2135
	public static Pattern TIME_LIMIT_PATTERN = Pattern.compile("(\\w{3}\\d{2}" + PnrDateUtil.MON_3CODE_PSTR_WITHPAIR + "\\d{2}/\\d{4})");

	// 02AUG13
	public static Pattern TIME_LIMIT_PATTERN_2 = Pattern.compile("(\\d{2}" + PnrDateUtil.MON_3CODE_PSTR_WITHPAIR + "\\d{2})");

	// 08MAY 1100
	public static Pattern TIME_LIMIT_PATTERN_3 = Pattern.compile("(\\d{2}" + PnrDateUtil.MON_3CODE_PSTR_WITHPAIR + ")\\s*(\\d{4})");

	// 05AUG GMT 2359
	public static Pattern TIME_LIMIT_PATTERN_4 = Pattern.compile("(\\d{2}" + PnrDateUtil.MON_3CODE_PSTR_WITHPAIR + ")\\s*GMT\\s*(\\d{4})");

	private final static String defaultDateFormat = "yyyy-MM-dd";
	private final static String defaultTimeFormat = "yyyy-MM-dd HH:mm:ss";

	public static String formatDate(Date date, String pattern) {
		if (date == null)
			return "";
		pattern = (pattern == null) ? defaultDateFormat : pattern;
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	public static Date parseDate(String dateStr, String format) {
		if (StringUtils.isEmpty(dateStr))
			return null;
		Date date = null;
		try {
			DateFormat df = new SimpleDateFormat(format);
			String dt = dateStr;
			if ((!dt.equals("")) && (dt.length() < format.length())) {
				dt += format.substring(dt.length()).replaceAll("[YyMmDdHhSs]", "0");
			}
			date = (Date) df.parse(dt);
		} catch (Exception e) {
		}
		return date;
	}

	public static String formatDateTime(Date date) {
		return (formatDate(date, defaultTimeFormat));
	}

	public static String formatDateTime(String dateStr, String pattern) {
		return formatDate(parseDate(dateStr), pattern);
	}

	public static String formatDateTime() {
		return (formatDate(now(), defaultTimeFormat));
	}

	public static String formatDate(Date date) {
		return (formatDate(date, defaultDateFormat));
	}

	public static Date parseDate(String dateStr) {
		return (parseDate(dateStr, defaultDateFormat));
	}

	public static String formatDate() {
		return (formatDate(now(), defaultDateFormat));
	}

	public static String formatTime(Date date) {
		return (formatDate(date, "HH:mm:ss"));
	}

	public static String formatTime() {
		return (formatDate(now(), "HH:mm:ss"));
	}

	public static String formatTime2() {
		return (formatDate(now(), "HHmmss"));
	}

	/**
	 * 格式后hh:mm
	 * 
	 * @param timeStr
	 *            ibe返回的时间字符串 hhmm
	 * @return
	 */
	public static String formatIbeTime(String timeStr) {
		if(StringUtils.isBlank(timeStr)){
			return timeStr;
		}
		return new StringBuffer(timeStr.substring(0, 2)).append(":").append(timeStr.substring(2, 4)).toString();
	}

	/**
	 * 取得当前日期
	 * 
	 * @return
	 */
	public static Date now() {
		return new Date();
	}

	/**
	 * 提前N天的日期
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date beforeDate(Date date, int days) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, -days);
		return c.getTime();
	}

	/**
	 * N天后的日期
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date afterDate(Date date, int days) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, days);
		return c.getTime();
	}

	public static int MonthStr2Number(String str) {
		if (str.equals("JAN")) {
			return 1;
		} else if (str.equals("FEB")) {
			return 2;
		} else if (str.equals("MAR")) {
			return 3;
		} else if (str.equals("APR")) {
			return 4;
		} else if (str.equals("MAY")) {
			return 5;
		} else if (str.equals("JUN")) {
			return 6;
		} else if (str.equals("JUL")) {
			return 7;
		} else if (str.equals("AUG")) {
			return 8;
		} else if (str.equals("SEP")) {
			return 9;
		} else if (str.equals("OCT")) {
			return 10;
		} else if (str.equals("NOV")) {
			return 11;
		} else if (str.equals("DEC")) {
			return 12;
		}

		throw BizException.instance("Error Month Str");
	}

	/**
	 * 12NOV12转换成yyyy-MM-dd这种类型
	 * 
	 * @param ibeDateStr
	 * @return
	 */
	public static String ibeDateStrToDateStr(String ibeDateStr) {
		if (StringUtils.isEmpty(ibeDateStr))
			return "";
		StringBuffer dateStr = new StringBuffer();
		// 年
		dateStr.append("20").append(ibeDateStr.substring(5, 7)).append("-");
		// 月
		String year = ibeDateStr.substring(2, 5);
		if (year.equals("JAN")) {
			dateStr.append("01-");
		} else if (year.equals("FEB")) {
			dateStr.append("02-");
		} else if (year.equals("MAR")) {
			dateStr.append("03-");
		} else if (year.equals("APR")) {
			dateStr.append("04-");
		} else if (year.equals("MAY")) {
			dateStr.append("05-");
		} else if (year.equals("JUN")) {
			dateStr.append("06-");
		} else if (year.equals("JUL")) {
			dateStr.append("07-");
		} else if (year.equals("AUG")) {
			dateStr.append("08-");
		} else if (year.equals("SEP")) {
			dateStr.append("09-");
		} else if (year.equals("OCT")) {
			dateStr.append("10-");
		} else if (year.equals("NOV")) {
			dateStr.append("11-");
		} else if (year.equals("DEC")) {
			dateStr.append("12-");
		}
		// 日
		dateStr.append(ibeDateStr.substring(0, 2));
		// return
		return dateStr.toString();
	}

	/**
	 * yyyy-MM-dd转换成12NOV12这种类型
	 * 
	 * @param dateStr
	 * @return
	 */
	public static String dateStrToIbeDateStr(String dateStr) {
		if(StringUtils.isBlank(dateStr)){
			return dateStr;
		}
		StringBuffer ibeDateStr = new StringBuffer();
		// 日
		ibeDateStr.append(dateStr.substring(8, 10));
		// 月
		String month = dateStr.substring(5, 7);
		if (month.equals("01")) {
			ibeDateStr.append("JAN");
		} else if (month.equals("02")) {
			ibeDateStr.append("FEB");
		} else if (month.equals("03")) {
			ibeDateStr.append("MAR");
		} else if (month.equals("04")) {
			ibeDateStr.append("APR");
		} else if (month.equals("05")) {
			ibeDateStr.append("MAY");
		} else if (month.equals("06")) {
			ibeDateStr.append("JUN");
		} else if (month.equals("07")) {
			ibeDateStr.append("JUL");
		} else if (month.equals("08")) {
			ibeDateStr.append("AUG");
		} else if (month.equals("09")) {
			ibeDateStr.append("SEP");
		} else if (month.equals("10")) {
			ibeDateStr.append("OCT");
		} else if (month.equals("11")) {
			ibeDateStr.append("NOV");
		} else if (month.equals("12")) {
			ibeDateStr.append("DEC");
		}
		// 年
		ibeDateStr.append(dateStr.substring(2, 4));
		// return
		return ibeDateStr.toString();
	}
	
	/**
	 * yyyyMMdd转换成12NOV这种类型
	 * 
	 * @param dateStr
	 * @return
	 */
	public static String dateStr8ToIbeMonthDateStr(String dateStr) {
		
		if(StringUtils.isBlank(dateStr)){
			return dateStr;
		}
		
		StringBuffer ibeDateStr = new StringBuffer();
		// 日
		ibeDateStr.append(dateStr.substring(6, 8));
		// 月
		String month = dateStr.substring(4, 6);
		if (month.equals("01")) {
			ibeDateStr.append("JAN");
		} else if (month.equals("02")) {
			ibeDateStr.append("FEB");
		} else if (month.equals("03")) {
			ibeDateStr.append("MAR");
		} else if (month.equals("04")) {
			ibeDateStr.append("APR");
		} else if (month.equals("05")) {
			ibeDateStr.append("MAY");
		} else if (month.equals("06")) {
			ibeDateStr.append("JUN");
		} else if (month.equals("07")) {
			ibeDateStr.append("JUL");
		} else if (month.equals("08")) {
			ibeDateStr.append("AUG");
		} else if (month.equals("09")) {
			ibeDateStr.append("SEP");
		} else if (month.equals("10")) {
			ibeDateStr.append("OCT");
		} else if (month.equals("11")) {
			ibeDateStr.append("NOV");
		} else if (month.equals("12")) {
			ibeDateStr.append("DEC");
		}
		// return
		return ibeDateStr.toString();
	}
	
	

	/**
	 * yyyy-MM-dd转换成12NOV这种类型
	 * 
	 * @param dateStr
	 * @return
	 */
	public static String dateStrToIbeMonthDateStr(String dateStr) {
		if(StringUtils.isBlank(dateStr)){
			return dateStr;
		}
		
		StringBuffer ibeDateStr = new StringBuffer();
		// 日
		ibeDateStr.append(dateStr.substring(8, 10));
		// 月
		String month = dateStr.substring(5, 7);
		if (month.equals("01")) {
			ibeDateStr.append("JAN");
		} else if (month.equals("02")) {
			ibeDateStr.append("FEB");
		} else if (month.equals("03")) {
			ibeDateStr.append("MAR");
		} else if (month.equals("04")) {
			ibeDateStr.append("APR");
		} else if (month.equals("05")) {
			ibeDateStr.append("MAY");
		} else if (month.equals("06")) {
			ibeDateStr.append("JUN");
		} else if (month.equals("07")) {
			ibeDateStr.append("JUL");
		} else if (month.equals("08")) {
			ibeDateStr.append("AUG");
		} else if (month.equals("09")) {
			ibeDateStr.append("SEP");
		} else if (month.equals("10")) {
			ibeDateStr.append("OCT");
		} else if (month.equals("11")) {
			ibeDateStr.append("NOV");
		} else if (month.equals("12")) {
			ibeDateStr.append("DEC");
		}
		// return
		return ibeDateStr.toString();
	}

	/**
	 * n天后的日期 格式 yyyy-MM-dd
	 * 
	 * @param ibeDateStr
	 *            ibe返回的日期字符串
	 * @param day
	 *            天数
	 * @return
	 */
	public static String afterDate(String ibeDateStr, int day) {
		return formatDate(afterDate(parseDate(ibeDateStrToDateStr(ibeDateStr)), day));
	}

	// SHA03MAY13/2135 ==> 2012-12-12 14:34
	public static String getTimeLimitFromADTK(String str) {

		int month = MonthStr2Number(str.substring(5, 8));

		MutableDateTime time = new MutableDateTime();

		time.setMonthOfYear(month);
		time.setDayOfMonth(NumberUtils.toInt(str.substring(3, 5)));

		time.setHourOfDay(NumberUtils.toInt(str.substring(11, 13)));
		time.setMinuteOfHour(NumberUtils.toInt(str.substring(13, 15)));

		return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").print(time);

	}

	private static void setLastMinOfDay(MutableDateTime time) {
		time.setHourOfDay(23);
		time.setMinuteOfHour(59);
	}

	// "02AUG13"; ==> 2013-08-02 23:59
	public static String getTimeLimitFromADTK2(String str) {

		int month = MonthStr2Number(str.substring(2, 5));

		MutableDateTime time = new MutableDateTime();

		time.setMonthOfYear(month);
		time.setDayOfMonth(NumberUtils.toInt(str.substring(0, 2)));

		setLastMinOfDay(time);

		return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").print(time);

	}

	public static boolean isTimeLimitTimeStr(String timeStr) {

		if (timeStr.length() == 5) {

			int hour = NumberUtils.toInt(timeStr.substring(0, 2));
			int min = NumberUtils.toInt(timeStr.substring(3, 4));

			return (hour >= 0 && hour <= 24 && min >= 0 && min <= 60);
		}

		if (timeStr.length() == 4) {

			if (!StringUtils.isNumeric(timeStr)) {
				return false;
			}

			int hour = NumberUtils.toInt(timeStr.substring(0, 2));
			int min = NumberUtils.toInt(timeStr.substring(2, 4));

			return (hour >= 0 && hour <= 24 && min >= 0 && min <= 60);
		}

		return false;

	}

	public static boolean isOTHS_TimeLimitStr(String str) {

		String[] tks = str.split("\\s+");
		Set<String> tkset = Sets.newHashSet(tks);

		if (tkset.contains("TERMINAL")) {
			if (tkset.contains("DEPART") || tkset.contains("ARRIVE")) {
				return false;
			}
		}

		return true;
	}

	public static DateTime getTimeLimitFromTokenRepo(TimeLimitTokenRepo repo) {

		Optional<String> monStr = repo.getOneTokenImageWithRole(TimeLimitTokenRole.MON);
		Preconditions.checkState(monStr.isPresent(), "mon must exist");

		int month = MonthStr2Number(monStr.get());

		MutableDateTime time = new MutableDateTime();

		time.setMonthOfYear(month);

		Optional<String> dayStr = repo.getOneTokenImageWithRole(TimeLimitTokenRole.DAY);
		Preconditions.checkState(dayStr.isPresent(), "day must exist");

		int day = NumberUtils.toInt(dayStr.get());
		time.setDayOfMonth(day);

		Optional<String> hourMinStr = repo.getOneTokenImageWithRole(TimeLimitTokenRole.HOUR_MIN);
		if (hourMinStr.isPresent()) {

			String hourMin = hourMinStr.get();

			int hour, min;
			if (hourMin.length() == 4) {
				hour = NumberUtils.toInt(hourMin.substring(0, 2));
				min = NumberUtils.toInt(hourMin.substring(2, 4));
			} else if (hourMin.length() == 5) {
				hour = NumberUtils.toInt(hourMin.substring(0, 2));
				min = NumberUtils.toInt(hourMin.substring(3, 5));
			} else {
				throw new IllegalStateException("invalid time format");
			}

			Preconditions.checkArgument(hour <= 24 && hour >= 0 && min <= 60 && min >= 0, "invalid hour-min format");

			time.setHourOfDay(hour);
			time.setMinuteOfHour(min);

		} else {
			setLastMinOfDay(time);
		}

		//  we should judge if the  date is after current day in this year ,    for example  14JAN  should be recoginzed as 2012-01-14
		// instead of 2013-01-14
		Optional<String> yearStr = repo.getOneTokenImageWithRole(TimeLimitTokenRole.YEAR);
		if (yearStr.isPresent()) {
			int year = NumberUtils.toInt(yearStr.get());
			int yearOfCentury = new DateTime().getYearOfCentury() ;
			if (year > new DateTime().getYearOfCentury()) {
				
				int addYearCnt =  year - yearOfCentury ;
				
				MutableDateTime  reallyYear = new MutableDateTime( );
				reallyYear.addYears(addYearCnt);
				time.setYear(reallyYear.getYear());
			}
		}else{
			//date is after current day in this year 
			if(time.getDayOfYear() < new DateTime( ).getDayOfYear()){
				time.addYears(1);
			}
		}

		return time.toDateTime();
		//

	}

	public static String getTimeLimitStr(DateTime time) {
		return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").print(time);
	}

	// pattern3: "08MAY 1100"; ==> 2013-05-08 23:59
	// pattern4: "05AUG GMT 2359"; ==> 2013-05-08 23:59

	// dateStr => 05AUG
	// timeStr => 2359
	public static String getTimeLimitFromADTK3_4(String dateStr, String timeStr) {

		int month = MonthStr2Number(dateStr.substring(5, 8));

		MutableDateTime time = new MutableDateTime();
		time.setMonthOfYear(month);
		time.setDayOfMonth(NumberUtils.toInt(dateStr.substring(0, 2)));

		if (!isTimeLimitTimeStr(timeStr)) {
			setLastMinOfDay(time);
		} else {
			int hour = NumberUtils.toInt(timeStr.substring(0, 2));
			int min = NumberUtils.toInt(timeStr.substring(2, 4));
			time.setHourOfDay(hour);
			time.setMinuteOfHour(min);
		}

		return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").print(time);

	}

	public static void main(String[] args) {
		System.out.print(getTimeLimitFromADTK("SHA03MAY13/2135"));

		// System.out.println(beforeDate(now(), 1));
		// 12NOV12

		// System.out.println(ibeDateStrToDateStr("12APR12"));
		// System.out.println(dateStrToIbeDateStr("2012-10-12"));
		// System.out.println(afterDate("31DEC12", 1));
		// System.out.println(dateStrToIbeMonthDateStr("2012-12-14"));
		// System.err.println(getDateAdd("2012-11-12", 1));
		System.out.println(defaultTimeFormat);
	}

}
