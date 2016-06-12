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

import com.travelzen.farerule.MinStay;
import com.travelzen.farerule.condition.RuleCondition;
import com.travelzen.farerule.jpecker.consts.DateConst;
import com.travelzen.farerule.jpecker.struct.RuleTextBlock;
import com.travelzen.farerule.jpecker.struct.RuleTextSegment;
import com.travelzen.farerule.jpecker.tool.ConditionTransducer;
import com.travelzen.farerule.jpecker.tool.DateTransducer;
import com.travelzen.farerule.rule.MinStayItem;
import com.travelzen.farerule.rule.TimeTypeEnum;
import com.travelzen.farerule.rule.WeekDayEnum;

public class Jpecker6 extends JpeckerBase {
	
	private static final Logger logger = LoggerFactory.getLogger(Jpecker6.class);
	
	public static MinStay parse(String ruleText) {
		return parse(ruleText, "");
	}
	
	public static MinStay parse(String ruleText, String airCompany) {
		Matcher matcher_no = Pattern.compile(
				"NO MINIMUM STAY (?:REQUIREMENTS|APPLIES)").matcher(ruleText);
		if (matcher_no.find()) {
			return null;
		}
		
		List<MinStayItem> minStayItemList = new ArrayList<MinStayItem>();
		
		List<RuleTextBlock> ruleTextBlockList = splitOrigins(ruleText);
		List<RuleTextSegment> ruleTextSegmentList = splitDates(ruleTextBlockList);
		
		for (RuleTextSegment ruleTextSegment:ruleTextSegmentList) {
			MinStayItem minStayItem = new MinStayItem();
			String text = ruleTextSegment.getText();
			if (parseDays(text) != 0) {
				minStayItem.setStayTimeType(TimeTypeEnum.DAY);
				minStayItem.setStayTimeNum(parseDays(text));
				if (parseWeekday(text) != null)
					minStayItem.setWeekday(parseWeekday(text));
			} else if (parseMonths(text) != 0) {
				minStayItem.setStayTimeType(TimeTypeEnum.MONTH);
				minStayItem.setStayTimeNum(parseMonths(text));
				if (parseWeekday(text) != null)
					minStayItem.setWeekday(parseWeekday(text));
			} else if (parseWeekday(text) != null) {
				minStayItem.setWeekday(parseWeekday(text));
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
			minStayItem.setRuleCondition(ruleCondition);
			
			minStayItemList.add(minStayItem);
		}
		
		MinStay minStay = new MinStay();
		minStay.setMinStayItemList(minStayItemList);
		return minStay;
	}	
	
	private static int parseDays(String ruleText) {
		int dayNum = 0;
		Pattern pattern = Pattern.compile("THAN\\s*(\\d{1,3})\\s*DAY");
		Matcher matcher = pattern.matcher(ruleText);
		if (matcher.find()) {
			dayNum = Integer.parseInt(matcher.group(1));
		}
		return dayNum;
	}
	
	private static int parseMonths(String ruleText) {
		int monthNum = 0;
		Pattern pattern = Pattern.compile("THAN\\s*(\\d{1,2})\\s*MONTH");
		Matcher matcher = pattern.matcher(ruleText);
		if (matcher.find()) {
			monthNum = Integer.parseInt(matcher.group(1));
		}
		return monthNum;
	}
	
	private static WeekDayEnum parseWeekday(String ruleText) {
		Pattern pattern = Pattern.compile("THE\\s*FIRST\\s*" + DateConst.WEEKDAY);
		Matcher matcher = pattern.matcher(ruleText);
		if (matcher.find()) {
			WeekDayEnum weekday = DateTransducer.parseWeekEnToEnum(matcher.group(1));
			return weekday;
		}
		return null;
	}

}
