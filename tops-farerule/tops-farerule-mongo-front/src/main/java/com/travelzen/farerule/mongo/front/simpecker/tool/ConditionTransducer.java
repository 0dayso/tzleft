/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.mongo.front.simpecker.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.travelzen.farerule.condition.SalesDateSubItem;
import com.travelzen.farerule.condition.TravelDateSubItem;
import com.travelzen.farerule.condition.OriginCondition;
import com.travelzen.farerule.condition.OriginTypeEnum;

public class ConditionTransducer {
	
	public static OriginCondition parseOriginSim(String str) {
		if (str == null)
			return null;
		OriginCondition originCondition = new OriginCondition();
		if (str.matches("去程"))
			originCondition.setOriginType(OriginTypeEnum.OUTBOUND);
		else if (str.matches("回程"))
			originCondition.setOriginType(OriginTypeEnum.INBOUND);
		else {
			originCondition.setOriginType(OriginTypeEnum.ORIGIN);
			originCondition.setLocation(str.substring(0, str.length()-2));
		}
		return originCondition;
	}
	
	public static SalesDateSubItem parseSalesDateSim(String str) {
		if (str == null)
			return null;
		SalesDateSubItem salesDateSubItem = new SalesDateSubItem();
		Matcher matcher1 = Pattern.compile("(\\d{4}\\.\\d{2}\\.\\d{2})或之后").matcher(str);
		Matcher matcher2 = Pattern.compile("(\\d{4}\\.\\d{2}\\.\\d{2})或之前").matcher(str);
		Matcher matcher3 = Pattern.compile("(\\d{4}\\.\\d{2}\\.\\d{2})到(\\d{4}\\.\\d{2}\\.\\d{2})之间").matcher(str);
		if (matcher1.find()) {
			long date = DateTransducer.parseDateSim(matcher1.group(1));
			salesDateSubItem.setAfterDate(date);
		} else if (matcher2.find()) {
			long date = DateTransducer.parseDateSim(matcher2.group(1));
			salesDateSubItem.setBeforeDate(date);
		} else if (matcher3.find()) {
			long date1 = DateTransducer.parseDateSim(matcher3.group(1));
			salesDateSubItem.setAfterDate(date1);
			long date2 = DateTransducer.parseDateSim(matcher3.group(2));
			salesDateSubItem.setBeforeDate(date2);
		}
		return salesDateSubItem;
	}
	
	public static TravelDateSubItem parseTravelDateSim(String str) {
		if (str == null)
			return null;
		TravelDateSubItem travelDateSubItem = new TravelDateSubItem();
		Matcher matcher1 = Pattern.compile("(\\d{4}\\.\\d{2}\\.\\d{2})或之后").matcher(str);
		Matcher matcher2 = Pattern.compile("(\\d{4}\\.\\d{2}\\.\\d{2})或之前").matcher(str);
		Matcher matcher3 = Pattern.compile("(\\d{4}\\.\\d{2}\\.\\d{2})到(\\d{4}\\.\\d{2}\\.\\d{2})之间").matcher(str);
		if (matcher1.find()) {
			long date = DateTransducer.parseDateSim(matcher1.group(1));
			travelDateSubItem.setAfterDate(date);
		} else if (matcher2.find()) {
			long date = DateTransducer.parseDateSim(matcher2.group(1));
			travelDateSubItem.setBeforeDate(date);
		} else if (matcher3.find()) {
			long date1 = DateTransducer.parseDateSim(matcher3.group(1));
			travelDateSubItem.setAfterDate(date1);
			long date2 = DateTransducer.parseDateSim(matcher3.group(2));
			travelDateSubItem.setBeforeDate(date2);
		}
		return travelDateSubItem;
	}
}
