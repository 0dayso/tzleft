package com.travelzen.controller.cpbs;

import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14/11/6
 * Time:下午5:17
 * <p/>
 * Description:
 */
public class DateFormatCollections {

    public static final SimpleDateFormat YYYY_MM_DD_HH_MM_SS_SSS_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

    public static final SimpleDateFormat YYYY_MM_DD_HH_MM_SS_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public final static SimpleDateFormat YYYY_MM_DD_HH_MM_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public final static SimpleDateFormat YYYY_MM_DD_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public final static SimpleDateFormat HH_MM_SS_SSS_FORMAT = new SimpleDateFormat("HH:mm:ss SSS");

}
