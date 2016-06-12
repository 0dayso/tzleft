/**
 * Copyright 2013 Travelzen Inc. All Rights Reserved.
 * Author: m18621298620@163.com (guohua xue)
 */

package com.travelzen.etermface.service.abe_imitator.fare.pojo;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.travelzen.etermface.service.common.PNRDateFormat;
import com.travelzen.framework.core.exception.BizException;

@XStreamAlias("FareSearchRequest")
public class FareSearchRequest implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -744115673480287945L;
    /**
	 * 
	 */
    // From 始发城市或机场三字代码 如：北京 PEK
    private String from;
    // Arrive 目的城市或机场三字代码 如：上海 SHA
    private String arrive;
    // Date 航班日期 YYYYMMDD YYYY-MM-DD YYYY/MM/DD
    private String date;
    // 航空公司代码
    private String carrier;

    // 根据属性生成原始查询命令
    public String publicFareSearchCmd() throws Exception {
        String cmd = "";

        StringBuffer sb = new StringBuffer();
        sb.append("FD ");

        if (StringUtils.isBlank(from) || StringUtils.isBlank(arrive) || from.length() != 3 || arrive.length() != 3) {
            throw BizException.instance("from or arrive are not IATA code!");
        } else {
            sb.append(from);
            sb.append(arrive);
        }

        if (StringUtils.isNotBlank(date)) {
            sb.append("/");
            String dateCmd = PNRDateFormat.dayMonthYearFormat(date);
            if (StringUtils.isBlank(dateCmd)) {
                throw new Exception("date请求参数格式不对，格式应该是：yyyy-MM-dd;当前值为："+date);
            }
            sb.append(dateCmd);
        }

        if (StringUtils.isNotBlank(carrier)) {
            sb.append("/");
            sb.append(carrier);
        }

        cmd = sb.toString();
        sb.delete(0, sb.length());
        sb = null;
        return cmd;
    }

    public String bargainFareSearchCmd() {
        String cmd = "";

        StringBuffer sb = new StringBuffer();
        sb.append("NFD ");

        if (StringUtils.isBlank(from) || StringUtils.isBlank(arrive) || from.length() != 3 || arrive.length() != 3) {
            throw BizException.instance("from or arrive are not IATA code!");
        } else {
            sb.append(from);
            sb.append(arrive);
        }

        if (StringUtils.isNotBlank(date)) {
            sb.append("/");
            sb.append(PNRDateFormat.dayMonthYearFormat(date));
        }

        if (StringUtils.isNotBlank(carrier)) {
            sb.append("/");
            sb.append(carrier);
        }

        cmd = sb.toString();
        sb.delete(0, sb.length());
        sb = null;
        return cmd;
    }

    public String bargainFareSearchResultStart() {
        String start = "";

        StringBuffer sb = new StringBuffer();
        sb.append("NFD");

        if (StringUtils.isBlank(from) || StringUtils.isBlank(arrive) || from.length() != 3 || arrive.length() != 3) {
            throw BizException.instance("from or arrive are not IATA code!");
        } else {
            sb.append(from);
            sb.append(arrive);
        }
        start = sb.toString();
        sb.delete(0, sb.length());
        sb = null;
        return start;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getArrive() {
        return arrive;
    }

    public void setArrive(String arrive) {
        this.arrive = arrive;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        if (date.contains("/")) {
            date = date.replaceAll("/", "-");
        }
        this.date = date;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    /**
     * 将xml转化为对象
     * 
     * @param xml
     * @return
     */
    public static FareSearchRequest fromXML(String xml) {
        if (null == xml || !xml.startsWith("<")) {
            return null;
        }

        XStream xstream = new XStream();
        xstream.processAnnotations(FareSearchRequest.class);
        return (FareSearchRequest) xstream.fromXML(xml);
    }

	@Override
	public String toString() {
		return "FareSearchRequest [from=" + from + ", arrive=" + arrive
				+ ", date=" + date + ", carrier=" + carrier + "]";
	}
	
}