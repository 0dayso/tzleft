package com.travelzen.fare2go;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * @author hongqiang.mao
 * @date 2013-5-4 下午5:25:54
 * @description
 */
public class CommonTool {

	private final static SimpleDateFormat SDF = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm");
	
	private static final String DATE_REGEX = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";

	/**
	 * 两小时
	 */
	private static final long TwoHours = 2*60*60*1000;
	
	/**
	 * 验证日期合法性
	 *
	 * @param pDate
	 * @return
	 */
	public static boolean validateDate(String pDate) {
		if (null == pDate) {
			return false;
		}
		if (pDate.matches(DATE_REGEX)) {
			return true;
		}
		return false;
	}

	/**
	 * 判定一个字符串是否为空字符串
	 *
	 * @param pStr
	 * @return
	 */
	public static boolean emptyString(String pStr) {
		if (null == pStr || "".equals(pStr.trim())) {
			return true;
		}
		return false;
	}

	/**
	 * 判定一个数组是否为空数组
	 *
	 * @param pList
	 * @return
	 */
	public static boolean emptyList(List<?> pList) {
		if (null == pList || 0 == pList.size()) {
			return true;
		}
		return false;
	}

	/**
	 * 判断字符串是否为数字
	 *
	 * @param pStr
	 * @return
	 */
	public static boolean isDigital(String pStr) {
		if (CommonTool.emptyString(pStr)) {
			return false;
		}
		if (pStr.matches("\\d+")) {
			return true;
		}
		return false;
	}

	/**
	 * 比较两个日期的大小
	 *
	 * @param pDate0
	 * @param pDate1
	 * @return 1：前面 >后面   -1：前面 <后面   0：前面 ==后面
	 * @throws Exception
	 */
	public static int compareDate(String pDate0, String pDate1) throws CriteriaConvertException {
		if (emptyString(pDate0)) {
			throw new CriteriaConvertException("pDate0 is null or empty");
		}

		if (emptyString(pDate1)) {
			throw new CriteriaConvertException("pDate1 is null or empty");
		}

		String[] lvStrs0 = pDate0.split("[-/]");
		String[] lvStrs1 = pDate1.split("[-/]");
		if (null == lvStrs0 || 3 != lvStrs0.length) {
			throw new CriteriaConvertException("pDate0's format error, the correct format likes 2013-05-04");
		}
		if (null == lvStrs1 || 3 != lvStrs1.length) {
			throw new CriteriaConvertException("pDate1's format error, the correct format likes 2013-05-04");
		}
		int lvYear0 = Integer.valueOf(lvStrs0[0]);
		int lvMonth0 = Integer.valueOf(lvStrs0[1]);
		int lvDay0 = Integer.valueOf(lvStrs0[2]);

		int lvYear1 = Integer.valueOf(lvStrs1[0]);
		int lvMonth1 = Integer.valueOf(lvStrs1[1]);
		int lvDay1 = Integer.valueOf(lvStrs1[2]);


		if (lvYear0 > lvYear1) {
			return 1;
		} else if (lvYear0 < lvYear1) {
			return -1;
		}

		if (lvMonth0 > lvMonth1) {
			return 1;
		} else if (lvMonth0 < lvMonth1) {
			return -1;
		}

		if (lvDay0 > lvDay1) {
			return 1;
		} else if (lvDay0 < lvDay1) {
			return -1;
		}

		return 0;
	}

	public static void main(String[] args) {
		String date = "2013-02-28";
		System.out.println(date + validateDate(date));
		date = "2008-02-29";
		System.out.println(date + validateDate(date));
		date = "2009-03-31";
		System.out.println(date + validateDate(date));
		date = "2009-04-30";
		System.out.println(date + validateDate(date));
		date = "2009-04-31";
		System.out.println(date + validateDate(date));

		date = "2009/04/30";
		System.out.println(date + validateDate(date));

		date = "2009.04.30";
		System.out.println(date + validateDate(date));

		try {
			System.out.println(compareDate("2012-1-1", "2011-12-1"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String lvStr = "<SR R=\"´íÌìÌØ¼Û\" C=\"950   2013-05-27 2013-06-27\"/>";
		System.out.println(convertToUTF8(lvStr, "UTF-8", "GBK"));

		lvStr = "<PNRStatus><ErrorIndex> 13</ErrorIndex><IsMakeOK>0</IsMakeOK><PNR> </PNR><Status>µÇÂ½Ê§°Ü</Status></PNRStatus>";
		System.out.println(convertToUTF8(lvStr, "UTF-8", "GBK"));

		String time = "2200";
		System.out.println(formatTimeToNormal(time));

		time = "22:00";
		System.out.println(formatTimeToFarego(time));
	}


	/**
	 * 将字符串转化为UTF-8
	 *
	 * @param pStr
	 * @return
	 */
	public static String convertToUTF8(String pStr, String sourceCharSet, String targetCharSet) {
		if (null == pStr) {
			return null;
		}
		String lvResult = null;
		try {
			byte[] lvSourceStr = pStr.getBytes(sourceCharSet);
			lvResult = new String(lvSourceStr, targetCharSet);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return pStr;
		}
		return lvResult;
	}

	/**
	 * @param time
	 * @return
	 */
	public static String formatTimeToNormal(String time) {
		if (null == time || time.length() != 4) {
			return time;
		}
		StringBuffer lvBuffer = new StringBuffer();
		lvBuffer.append(time.substring(0, 2));
		lvBuffer.append(":");
		lvBuffer.append(time.substring(2, 4));
		return lvBuffer.toString();
	}

	/**
	 * @param time
	 * @return
	 */
	public static String formatTimeToFarego(String time) {
		if (null == time || time.length() != 5) {
			return time;
		}
		StringBuffer lvBuffer = new StringBuffer();
		lvBuffer.append(time.substring(0, 2));
		lvBuffer.append(time.substring(3, 5));
		return lvBuffer.toString();
	}

	/**
	 * 返回10000000~20000000内的随机数
	 *
	 * @return
	 */
	public static long getRandom() {
		long l = 10000000;
		return Math.round(Math.random() * l + l);
	}
	
	
	/**
	 * 两小时内
	 * @param pDate
	 * @param pTime
	 * @return
	 */
	public static boolean inTwoHours(String pDate,String pTime){
		Date lvTime = null;
		try {
			lvTime = SDF.parse(pDate+" "+pTime);
		} catch (ParseException e) {
			e.printStackTrace();
			return true;
		}
		
		if(lvTime.getTime() - System.currentTimeMillis() < TwoHours ){
			return true;
		}else{
			return false;
		}
	}
}