/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */
 
package com.travelzen.farerule.jpecker.pecker;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.farerule.TravelDate;
import com.travelzen.farerule.condition.OriginCondition;
import com.travelzen.farerule.condition.TravelDateSubItem;
import com.travelzen.farerule.jpecker.consts.DateConst;
import com.travelzen.farerule.jpecker.struct.RuleTextBlock;
import com.travelzen.farerule.jpecker.tool.ConditionTransducer;
import com.travelzen.farerule.jpecker.tool.DateTransducer;
import com.travelzen.farerule.rule.TravelDateItem;

public class Jpecker14 extends JpeckerBase {
	
	private static final Logger logger = LoggerFactory.getLogger(Jpecker14.class);

	public static TravelDate parse(String ruleText) {
		if (ruleText.contains("NO TRAVEL DATE RESTRICTIONS")) {
			return null;
		}
		
		List<TravelDateItem> travelDateItemList = new ArrayList<TravelDateItem>();
		
		List<RuleTextBlock> ruleTextBlockList = splitOrigins(ruleText);
		
		for (RuleTextBlock ruleTextBlock:ruleTextBlockList) {
			TravelDateItem travelDateItem = new TravelDateItem();
			List<TravelDateSubItem> travelDateSubItemList = new ArrayList<TravelDateSubItem>();
			String[] segments = ruleTextBlock.getText().split("\\sOR\\s");
			for (String segment:segments) {
				TravelDateSubItem travelDateSubItem = new TravelDateSubItem();
				travelDateSubItem.setAfterDate(parseAfterDate(segment));
				travelDateSubItem.setBeforeDate(parseBeforeDate(segment));
				if (travelDateSubItem.getAfterDate() != 0 || travelDateSubItem.getBeforeDate() != 0) {
					travelDateSubItemList.add(travelDateSubItem);
				}
			}
			if (travelDateSubItemList.size() != 0)
				travelDateItem.setTravelDateSubItemList(travelDateSubItemList);
			travelDateItem.setCompleteDate(parseCompleteDate(ruleText));
			if (travelDateItem.getTravelDateSubItemList() != null || travelDateItem.getCompleteDate() != 0) {
				if (ruleTextBlock.getOrigin() != null) {
					OriginCondition originCondition = ConditionTransducer.parseOrigin(ruleTextBlock.getOrigin());
					travelDateItem.setOriginCondition(originCondition);
				}
				travelDateItemList.add(travelDateItem);
			}
		}
		
		TravelDate travelDate = new TravelDate();
		travelDate.setTravelDateItemList(travelDateItemList);
		return travelDate;
	}
	
	private static long parseAfterDate(String text) {
		long longDate = 0;
		Pattern pattern = Pattern.compile("ON\\s*/\\s*AFTER\\s*"+DateConst.DATE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			longDate = DateTransducer.parseDate(matcher.group(1));
		}
		return longDate;
	}
	
	private static long parseBeforeDate(String text) {
		long longDate = 0;
		Pattern pattern = Pattern.compile("ON\\s*/\\s*BEFORE\\s*"+DateConst.DATE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			longDate = DateTransducer.parseDate(matcher.group(1));
		}
		return longDate;
	}
	
	private static long parseCompleteDate(String text) {
		long longDate = 0;
		Pattern pattern = Pattern.compile("ON\\s*"+DateConst.DATE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			longDate = DateTransducer.parseDate(matcher.group(1));
		}
		return longDate;
	}
}
