/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */
 
package com.travelzen.farerule.cpecker.pecker;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.travelzen.farerule.MinStay;
import com.travelzen.farerule.condition.RuleCondition;
import com.travelzen.farerule.cpecker.tool.ConditionTransducer;
import com.travelzen.farerule.cpecker.tool.DateTransducer;
import com.travelzen.farerule.jpecker.struct.RuleTextBlock;
import com.travelzen.farerule.jpecker.struct.RuleTextSegment;
import com.travelzen.farerule.rule.MinStayItem;
import com.travelzen.farerule.rule.TimeTypeEnum;
import com.travelzen.farerule.rule.WeekDayEnum;

public class Cpecker6 extends CpeckerBase {

	private MinStay minStay;
	private List<MinStayItem> minStayItemList;
	
	public Cpecker6() {
		this.airCompany = "";
		minStay = new MinStay();
		minStayItemList = new ArrayList<MinStayItem>();
	}
	
	public Cpecker6(String airCompany) {
		this.airCompany = airCompany;
		minStay = new MinStay();
		minStayItemList = new ArrayList<MinStayItem>();
	}
	
	public MinStay getMinStay() {
		return minStay;
	}
	
	public void parse(String ruleText) {
		if (ruleText == null || ruleText.contains("无限制")) {
			return;
		}
		
		splitOrigins(ruleText);
		
		// For special DL airline
		boolean isSpecialDL = false;
		if (airCompany.equals("DL")) {
			if (ruleText.contains("日/后和")) {
				isSpecialDL = true;
				splitDates4DL(ruleTextBlockList);
			}
		}
		if (isSpecialDL == false)
			splitDates(ruleTextBlockList);
		
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
			minStayItem.setRuleCondition(ruleCondition);
			
			minStayItemList.add(minStayItem);
		}
		
		minStay.setMinStayItemList(minStayItemList);
	}
	
	private int parseDays(String ruleText) {
		int dayNum = 0;
		Pattern pattern = Pattern.compile("(\\d{1,3}) ?[\\w\\W]{0,2}天");
		if (ruleText.contains("中转"))	
			pattern = Pattern.compile("中转：(\\d{1,3}) ?天");
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
	
	private WeekDayEnum parseWeekday(String ruleText) {
		Pattern pattern = Pattern.compile("必须经过一个星期([\\u4e00-\\u9fa5])");
		Matcher matcher = pattern.matcher(ruleText);
		if (matcher.find()) {
			WeekDayEnum weekday = DateTransducer.parseWeekCnToEnum(matcher.group(1));
			return weekday;
		}
		return null;
	}
	
	private void splitDates4DL(List<RuleTextBlock> ruleTextBlockList) {
		Pattern pattern1 = Pattern.compile(
				"(\\d{2,4}年\\d{1,2}月\\d{1,2}日(?:/|或)(?:之)?(?:前|后))旅行"
				+ "([\\w\\W]+?)"
				+ "(?=在\\d{2,4}年|$)");
		Pattern pattern2 = Pattern.compile(
				"(\\d{2,4}年\\d{1,2}月\\d{1,2}日(?:/|或)(?:之)?后和\\d{2,4}年\\d{1,2}月\\d{1,2}日(?:/|或)(?:之)?前)旅行"
				+ "([\\w\\W]+?)"
				+ "(?=在\\d{2,4}年|$)");
		RuleTextSegment ruleTextSegment;
		for (RuleTextBlock ruleTextBlock:ruleTextBlockList) {
			String origin = ruleTextBlock.getOrigin();
			Matcher matcher1 = pattern1.matcher(ruleTextBlock.getText());
			Matcher matcher2 = pattern2.matcher(ruleTextBlock.getText());
			if (matcher1.find()) {
				ruleTextSegment = new RuleTextSegment().setOrigin(origin);
				ruleTextSegment.setTravelDate(matcher1.group(1));
				ruleTextSegment.setText(matcher1.group(2));
				ruleTextSegmentList.add(ruleTextSegment);
			}
			while (matcher2.find()) {
				ruleTextSegment = new RuleTextSegment().setOrigin(origin);
				ruleTextSegment.setTravelDate(matcher2.group(1));
				ruleTextSegment.setText(matcher2.group(2));
				ruleTextSegmentList.add(ruleTextSegment);
			}
			if (matcher1.find()) {
				ruleTextSegment = new RuleTextSegment().setOrigin(origin);
				ruleTextSegment.setTravelDate(matcher1.group(1));
				ruleTextSegment.setText(matcher1.group(2));
				ruleTextSegmentList.add(ruleTextSegment);
			}
		}
	}

}
