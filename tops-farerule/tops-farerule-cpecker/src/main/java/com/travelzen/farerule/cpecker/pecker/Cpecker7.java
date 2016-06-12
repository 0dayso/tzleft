/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */
 
package com.travelzen.farerule.cpecker.pecker;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.travelzen.farerule.MaxStay;
import com.travelzen.farerule.condition.RuleCondition;
import com.travelzen.farerule.jpecker.struct.RuleTextSegment;
import com.travelzen.farerule.cpecker.tool.ConditionTransducer;
import com.travelzen.farerule.cpecker.tool.DateTransducer;
import com.travelzen.farerule.rule.MaxStayItem;
import com.travelzen.farerule.rule.TimeTypeEnum;

public class Cpecker7 extends CpeckerBase {
	
	private MaxStay maxStay;
	private List<MaxStayItem> maxStayItemList;

	public Cpecker7() {
		this.airCompany = "";
		maxStay = new MaxStay();
		maxStayItemList = new ArrayList<MaxStayItem>();
	}
	
	public Cpecker7(String airCompany) {
		this.airCompany = airCompany;
		maxStay = new MaxStay();
		maxStayItemList = new ArrayList<MaxStayItem>();
	}
	
	public MaxStay getMaxStay() {
		return maxStay;
	}
	
	public void parse(String ruleText) {
		if (ruleText == null || ruleText.contains("无限制")) {
			return;
		}
		
		splitOrigins(ruleText);
		splitDates(ruleTextBlockList);
		
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
			if (!ruleTextSegment.getOrigin().equals("")) {
				ruleCondition.setOriginCondition(
						ConditionTransducer.parseOriginCn(ruleTextSegment.getOrigin()));
			}
			if (!ruleTextSegment.getSalesDate().equals("")) {
				ruleCondition.setSalesDateCondition(
						ConditionTransducer.parseSalesDateCn(ruleTextSegment.getSalesDate()));
			}
			if (!ruleTextSegment.getTravelDate().equals("")) {
				ruleCondition.setTravelDateCondition(
						ConditionTransducer.parseTravelDateCn(ruleTextSegment.getTravelDate()));
			}
			maxStayItem.setRuleCondition(ruleCondition);
			
			maxStayItemList.add(maxStayItem);
		}
		
		maxStay.setMaxStayItemList(maxStayItemList);
	}
	
	private int parseDays(String ruleText) {
		int dayNum = 0;
		Pattern pattern = Pattern.compile("(\\d{1,3}) ?天");
		Matcher matcher = pattern.matcher(ruleText);
		if (matcher.find()) {
			dayNum = Integer.parseInt(matcher.group(1));
		}
		return dayNum;
	}
	
	private int parseMonths(String ruleText) {
		int monthNum = 0;
		Pattern pattern = Pattern.compile("(\\d{1,2}) ?(?:个)?月");
		Matcher matcher = pattern.matcher(ruleText);
		if (matcher.find()) {
			monthNum = Integer.parseInt(matcher.group(1));
		}
		return monthNum;
	}
	
	private long parseDate(String ruleText) {
		long longDate = 0;
		Pattern pattern = Pattern.compile("(\\d{4}年\\d{1,2}月\\d{1,2}日)");
		Matcher matcher = pattern.matcher(ruleText);
		if (matcher.find()) {
			longDate = DateTransducer.parseDateCn(matcher.group(1));
		}
		return longDate;
	}
}
