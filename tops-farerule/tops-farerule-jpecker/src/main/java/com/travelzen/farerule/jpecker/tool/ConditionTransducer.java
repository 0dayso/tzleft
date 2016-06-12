/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.jpecker.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.travelzen.farerule.condition.SalesDateSubItem;
import com.travelzen.farerule.condition.TravelDateSubItem;
import com.travelzen.farerule.condition.OriginCondition;
import com.travelzen.farerule.condition.OriginTypeEnum;
import com.travelzen.farerule.jpecker.consts.DateConst;
import com.travelzen.farerule.jpecker.tool.DateTransducer;
import com.travelzen.farerule.mongo.Location;
import com.travelzen.farerule.mongo.morphia.LocationMorphia;

public class ConditionTransducer {
	
	private static LocationMorphia morphia = LocationMorphia.Instance;
	
	public static OriginCondition parseOrigin(String str) {
		if (str == null)
			return null;
		OriginCondition originCondition = new OriginCondition();
		if (str.matches("OUTBOUND"))
			originCondition.setOriginType(OriginTypeEnum.OUTBOUND);
		else if (str.matches("INBOUND"))
			originCondition.setOriginType(OriginTypeEnum.INBOUND);
		else {
			originCondition.setOriginType(OriginTypeEnum.ORIGIN);
			Matcher matcher = Pattern.compile("ORIGINATING +([A-Z0-9,\\(\\) ]*)").matcher(str);
			if (matcher.find()) {
				String loc = matcher.group(1);
				originCondition.setLocation(loc);
				if (morphia.find(loc) == null)
					morphia.save(new Location().setEnLoc(loc));
			} else
				originCondition.setLocation(str);
		}
		return originCondition;
	}
	
	public static SalesDateSubItem parseSalesDate(String str) {
		if (str == null)
			return null;
		SalesDateSubItem salesDateSubItem = new SalesDateSubItem();
		Pattern pattern1 = Pattern.compile("AFTER " + DateConst.DATE);
		Matcher matcher1 = pattern1.matcher(str);
		if (matcher1.find()) {
			long date = DateTransducer.parseDate(matcher1.group(1));
			salesDateSubItem.setAfterDate(date);
		}
		Pattern pattern2 = Pattern.compile("BEFORE " + DateConst.DATE);
		Matcher matcher2 = pattern2.matcher(str);
		if (matcher2.find()) {
			long date = DateTransducer.parseDate(matcher2.group(1));
			salesDateSubItem.setBeforeDate(date);
		}
		return salesDateSubItem;
	}
	
	public static TravelDateSubItem parseTravelDate(String str) {
		if (str == null)
			return null;
		TravelDateSubItem travelDateSubItem = new TravelDateSubItem();
		Pattern pattern1 = Pattern.compile("AFTER " + DateConst.DATE);
		Matcher matcher1 = pattern1.matcher(str);
		if (matcher1.find()) {
			long date = DateTransducer.parseDate(matcher1.group(1));
			travelDateSubItem.setAfterDate(date);
		}
		Pattern pattern2 = Pattern.compile("BEFORE " + DateConst.DATE);
		Matcher matcher2 = pattern2.matcher(str);
		if (matcher2.find()) {
			long date = DateTransducer.parseDate(matcher2.group(1));
			travelDateSubItem.setBeforeDate(date);
		}
		return travelDateSubItem;
	}

}
