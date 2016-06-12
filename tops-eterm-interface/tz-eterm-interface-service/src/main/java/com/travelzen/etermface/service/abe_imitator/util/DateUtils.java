/**
 * Copyright 2013 Travelzen Inc. All Rights Reserved.
 * Author: m18621298620@163.com (guohua xue)
 */
package com.travelzen.etermface.service.abe_imitator.util;

import java.text.ParseException;
import java.util.Locale;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.travelzen.framework.core.time.DateTimeUtil;

public class DateUtils {

    public static Map<String, String> month;

    // YYYYMMDD 格式转化
    public static String ddMmmDate(String dateStr) {

        try {
            DateTime date = DateTimeUtil.getDate(dateStr, "yyyyMMdd");
            String rs = DateTimeUtil.formatDate(date,Locale.ENGLISH, "ddMMM" );
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // YYYYMMDD 格式转化
    public static String ddMmmYyDate(String dateStr) {

        try {
            DateTime date = DateTimeUtil.getDate(dateStr, "yyyyMMdd");
            String rs = DateTimeUtil.formatDate(date, "ddMMMyy");
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // YYYYMMDD 格式转化
    public static String dateByDdMmm(String dateStr) {

        try {

            DateTime currentDate = DateTime.now();
            dateStr = dateStr + currentDate.getYear();
            DateTime date = DateTimeUtil.getDate(dateStr, "ddMMMyyyy");
            if (date.isBefore(currentDate)) {
                date = date.plusYears(1);
            }
            String rs = DateTimeUtil.formatDate(date, "yyyy-MM-dd");
            ;
            return rs;
        } catch (Exception e) {
            //
            System.out.println(dateStr);
        }
        return null;
    }

    // YYYYMMDD 格式转化
    public static String mmmYyDate(String dateStr) {

        try {
            DateTime birth = DateTimeUtil.getDate(dateStr, "yyyyMMdd");
            String date = DateTimeUtil.formatDate(birth, "MMMyy");
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws ParseException {
        String s = "01DEC2008";
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("ddMMMyyyy");

        DateTimeFormatter dateTimeFormatter1 = DateTimeFormat.forPattern("yyyyMMdd");
        DateTime when = dateTimeFormatter.parseDateTime(s);
        System.out.println(when);
        if (true)
            return;
        String year = "2001";
        year = String.valueOf(Integer.valueOf(year) + 1);
        System.out.println(year);
        String arriveTime = "201312+2";
        String test = "** Plx";
        test = test.replaceAll("\\*", "").trim();
        System.out.println(test);
        char f = test.charAt(0);
        if (Character.isLowerCase(f) || Character.isUpperCase(f)) {
            System.out.println(true);
        }
        if (arriveTime.contains("+")) {
            System.out.println(dateTimeFormatter1.print(when));
            int index = arriveTime.indexOf("+");
            String arrivalInterval = arriveTime.substring(index + 1);
            arriveTime = arriveTime.substring(0, index);
            System.out.println(Integer.valueOf(arrivalInterval));
            System.out.println("--------------------");
        }

        String str = "20120505";
        System.out.println(DateUtils.ddMmmDate(str));
        // System.out.println(DateUtils.cmDate(str));

        str = "20120505";
        System.out.println(DateUtils.ddMmmDate(str));
        System.out.println(DateUtils.ddMmmYyDate(str));
        System.out.println(DateUtils.mmmYyDate(str));
        System.out.println("ABC" + dateByDdMmm("10jul"));
        // TODO Auto-generated catch block

    }

}
