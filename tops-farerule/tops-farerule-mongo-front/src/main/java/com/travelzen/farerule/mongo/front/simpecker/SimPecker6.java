package com.travelzen.farerule.mongo.front.simpecker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.travelzen.farerule.MinStay;
import com.travelzen.farerule.condition.RuleCondition;
import com.travelzen.farerule.jpecker.struct.RuleTextSegment;
import com.travelzen.farerule.mongo.front.simpecker.tool.ConditionTransducer;
import com.travelzen.farerule.mongo.front.simpecker.tool.DateTransducer;
import com.travelzen.farerule.rule.MinStayItem;
import com.travelzen.farerule.rule.TimeTypeEnum;
import com.travelzen.farerule.rule.WeekDayEnum;

public class SimPecker6 extends SimPeckerBase {
	
	private MinStay minStay;
	private List<MinStayItem> minStayItemList;
	
	public SimPecker6() {
		minStay = new MinStay();
		minStayItemList = new ArrayList<MinStayItem>();
	}
	
	public MinStay getMinStay() {
		return minStay;
	}
	
	public void process(String ruleText) {
		if (ruleText.contains("无限制")) {
			return;
		}
		
		splitOrigins(ruleText);
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
						ConditionTransducer.parseOriginSim(ruleTextSegment.getOrigin()));
			}
			if (!ruleTextSegment.getSalesDate().equals("")) {
				ruleCondition.setSalesDateCondition(
						ConditionTransducer.parseSalesDateSim(ruleTextSegment.getSalesDate()));
			}
			if (!ruleTextSegment.getTravelDate().equals("")) {
				ruleCondition.setTravelDateCondition(
						ConditionTransducer.parseTravelDateSim(ruleTextSegment.getTravelDate()));
			}
			minStayItem.setRuleCondition(ruleCondition);
			
			minStayItemList.add(minStayItem);
		}
		
		minStay.setMinStayItemList(minStayItemList);
	}
	
	private int parseDays(String ruleText) {
		int dayNum = 0;
		Pattern pattern = Pattern.compile("(\\d{1,3})天");
		Matcher matcher = pattern.matcher(ruleText);
		if (matcher.find()) {
			dayNum = Integer.parseInt(matcher.group(1));
		}
		return dayNum;
	}
	
	private int parseMonths(String ruleText) {
		int monthNum = 0;
		Pattern pattern = Pattern.compile("(\\d{1,2})个月");
		Matcher matcher = pattern.matcher(ruleText);
		if (matcher.find()) {
			monthNum = Integer.parseInt(matcher.group(1));
		}
		return monthNum;
	}
	
	private WeekDayEnum parseWeekday(String ruleText) {
		Pattern pattern = Pattern.compile("出发后的第一个周([\\u4e00-\\u9fa5])");
		Matcher matcher = pattern.matcher(ruleText);
		if (matcher.find()) {
			WeekDayEnum weekday = DateTransducer.parseWeekCnToEnum(matcher.group(1));
			return weekday;
		}
		return null;
	}

}
