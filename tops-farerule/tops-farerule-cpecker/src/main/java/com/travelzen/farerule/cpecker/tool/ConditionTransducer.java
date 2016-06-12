/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.cpecker.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.travelzen.farerule.condition.SalesDateSubItem;
import com.travelzen.farerule.condition.TravelDateSubItem;
import com.travelzen.farerule.condition.OriginCondition;
import com.travelzen.farerule.condition.OriginTypeEnum;

public class ConditionTransducer {
	
	public static OriginCondition parseOriginCn(String str) {
		if (str == null)
			return null;
		OriginCondition originCondition = new OriginCondition();
		if (str.matches("去程"))
			originCondition.setOriginType(OriginTypeEnum.OUTBOUND);
		else if (str.matches("回程"))
			originCondition.setOriginType(OriginTypeEnum.INBOUND);
		else {
			originCondition.setOriginType(OriginTypeEnum.ORIGIN);
			originCondition.setLocation(str);
		}
		return originCondition;
	}
	
	public static SalesDateSubItem parseSalesDateCn(String str) {
		if (str == null)
			return null;
		SalesDateSubItem salesDateSubItem = new SalesDateSubItem();
		Pattern pattern1 = Pattern.compile("((\\d+)年(\\d+)月(\\d+)日)(?:/|或)(?:之)?后");
		Matcher matcher1 = pattern1.matcher(str);
		if (matcher1.find()) {
			long date = DateTransducer.parseDateCn(matcher1.group(1));
			salesDateSubItem.setAfterDate(date);
		}
		Pattern pattern2 = Pattern.compile("((\\d+)年(\\d+)月(\\d+)日)(?:/|或)(?:之)?前");
		Matcher matcher2 = pattern2.matcher(str);
		if (matcher2.find()) {
			long date = DateTransducer.parseDateCn(matcher2.group(1));
			salesDateSubItem.setBeforeDate(date);
		}
		return salesDateSubItem;
	}
	
	public static TravelDateSubItem parseTravelDateCn(String str) {
		if (str == null)
			return null;
		TravelDateSubItem travelDateSubItem = new TravelDateSubItem();
		Pattern pattern1 = Pattern.compile("((\\d+)年(\\d+)月(\\d+)日)(?:/|或)(?:之)?后");
		Matcher matcher1 = pattern1.matcher(str);
		if (matcher1.find()) {
			long date = DateTransducer.parseDateCn(matcher1.group(1));
			travelDateSubItem.setAfterDate(date);
		}
		Pattern pattern2 = Pattern.compile("((\\d+)年(\\d+)月(\\d+)日)(?:/|或)(?:之)?前");
		Matcher matcher2 = pattern2.matcher(str);
		if (matcher2.find()) {
			long date = DateTransducer.parseDateCn(matcher2.group(1));
			travelDateSubItem.setBeforeDate(date);
		}
		return travelDateSubItem;
	}
}
