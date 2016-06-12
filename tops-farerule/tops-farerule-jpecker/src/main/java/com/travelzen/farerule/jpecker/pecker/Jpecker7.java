/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */
 
package com.travelzen.farerule.jpecker.pecker;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.farerule.MaxStay;
import com.travelzen.farerule.rule.MaxStayItem;
import com.travelzen.farerule.rule.TimeTypeEnum;
import com.travelzen.farerule.condition.RuleCondition;
import com.travelzen.farerule.jpecker.consts.DateConst;
import com.travelzen.farerule.jpecker.struct.RuleTextBlock;
import com.travelzen.farerule.jpecker.struct.RuleTextSegment;
import com.travelzen.farerule.jpecker.tool.ConditionTransducer;
import com.travelzen.farerule.jpecker.tool.DateTransducer;

public class Jpecker7 extends JpeckerBase {
	
	private static final Logger logger = LoggerFactory.getLogger(Jpecker7.class);
	
	public static MaxStay parse(String ruleText) {
		return parse(ruleText, "");
	}
	
	public static MaxStay parse(String ruleText, String airCompany) {
		if (ruleText.contains("NO MAXIMUM STAY REQUIREMENTS")) {
			return null;
		}
		
		List<MaxStayItem> maxStayItemList = new ArrayList<MaxStayItem>();
		
		// for Japanese airlines
		if (airCompany.equals("JL") || airCompany.equals("NH")) {
			maxStayItemList.addAll(parseJP(ruleText));
		}
		if (maxStayItemList.size() != 0) {
			MaxStay maxStay = new MaxStay();
			maxStay.setMaxStayItemList(maxStayItemList);
			return maxStay;
		}
		
		List<RuleTextBlock> ruleTextBlockList = splitOrigins(ruleText);
		List<RuleTextSegment> ruleTextSegmentList = splitDates(ruleTextBlockList);
		
		for (RuleTextSegment ruleTextSegment:ruleTextSegmentList) {
			MaxStayItem maxStayItem = new MaxStayItem();
			String text = ruleTextSegment.getText();
			if (parseDays(text) != 0) {
				maxStayItem.setStayTimeType(TimeTypeEnum.DAY);
				maxStayItem.setStayTimeNum(parseDays(text));
				if (parseDate(text) != 0)
					maxStayItem.setDate(parseDate(text));
			} else if (parseMonths(text) != 0) {
				maxStayItem.setStayTimeType(TimeTypeEnum.MONTH);
				maxStayItem.setStayTimeNum(parseMonths(text));
				if (parseDate(text) != 0)
					maxStayItem.setDate(parseDate(text));
			} else if (parseDate(text) != 0) {
				maxStayItem.setDate(parseDate(text));
			} else {
				continue;
			}
			
			RuleCondition ruleCondition = new RuleCondition();
			if (ruleTextSegment.getOrigin() != null) {
				ruleCondition.setOriginCondition(
						ConditionTransducer.parseOrigin(ruleTextSegment.getOrigin()));
			}
			if (ruleTextSegment.getSalesDate() != null) {
				ruleCondition.setSalesDateCondition(
						ConditionTransducer.parseSalesDate(ruleTextSegment.getSalesDate()));
			}
			if (ruleTextSegment.getTravelDate() != null) {
				ruleCondition.setTravelDateCondition(
						ConditionTransducer.parseTravelDate(ruleTextSegment.getTravelDate()));
			}
			maxStayItem.setRuleCondition(ruleCondition);
			
			maxStayItemList.add(maxStayItem);
		}
		
		MaxStay maxStay = new MaxStay();
		maxStay.setMaxStayItemList(maxStayItemList);
		return maxStay;
	}
	
	private static int parseDays(String ruleText) {
		int dayNum = 0;
		Pattern pattern = Pattern.compile("THAN\\s*(?:MIDNIGHT\\s*)?(\\d{1,3})\\s*DAY");
		Matcher matcher = pattern.matcher(ruleText);
		if (matcher.find()) {
			dayNum = Integer.parseInt(matcher.group(1));
		}
		return dayNum;
	}
	
	private static int parseMonths(String ruleText) {
		int monthNum = 0;
		Pattern pattern = Pattern.compile("THAN\\s*(?:MIDNIGHT\\s*)?(\\d{1,2})\\s*MONTH");
		Matcher matcher = pattern.matcher(ruleText);
		if (matcher.find()) {
			monthNum = Integer.parseInt(matcher.group(1));
		}
		return monthNum;
	}
	
	private static long parseDate(String ruleText) {
		long longDate = 0;
		Pattern pattern = Pattern.compile(DateConst.DATE + "\\s*WHICHEVER");
		Matcher matcher = pattern.matcher(ruleText);
		if (matcher.find()) {
			longDate = DateTransducer.parseDate(matcher.group(1));
		}
		return longDate;
	}
	
	private static List<MaxStayItem> parseJP(String ruleText) {
		List<MaxStayItem> maxStayItemList = new ArrayList<MaxStayItem>();
		MaxStayItem maxStayItem = new MaxStayItem();
		Matcher matcher = Pattern.compile("VALID\\sFOR\\s(\\d+)\\sDAYS").matcher(ruleText);
		if (matcher.find()) {
			maxStayItem.setStayTimeType(TimeTypeEnum.DAY);
			maxStayItem.setStayTimeNum(Integer.parseInt(matcher.group(1)));
			maxStayItemList.add(maxStayItem);			
		}
		return maxStayItemList;
	}
}
