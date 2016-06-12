package com.travelzen.farerule.mongo.front.simpecker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.travelzen.farerule.MaxStay;
import com.travelzen.farerule.condition.RuleCondition;
import com.travelzen.farerule.jpecker.struct.RuleTextSegment;
import com.travelzen.farerule.mongo.front.simpecker.tool.ConditionTransducer;
import com.travelzen.farerule.mongo.front.simpecker.tool.DateTransducer;
import com.travelzen.farerule.rule.MaxStayItem;
import com.travelzen.farerule.rule.TimeTypeEnum;

public class SimPecker7 extends SimPeckerBase {

	private MaxStay maxStay;
	private List<MaxStayItem> maxStayItemList;

	public SimPecker7() {
		maxStay = new MaxStay();
		maxStayItemList = new ArrayList<MaxStayItem>();
	}
	
	public MaxStay getMaxStay() {
		return maxStay;
	}
	
	public void process(String ruleText) {
		if (ruleText.contains("无限制")) {
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
			maxStayItem.setRuleCondition(ruleCondition);
			
			maxStayItemList.add(maxStayItem);
		}
		
		maxStay.setMaxStayItemList(maxStayItemList);
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
	
	private long parseDate(String ruleText) {
		long longDate = 0;
		Pattern pattern = Pattern.compile("停留至(\\d{4}\\.\\d{2}\\.\\d{2})");
		Matcher matcher = pattern.matcher(ruleText);
		if (matcher.find()) {
			longDate = DateTransducer.parseDateSim(matcher.group(1));
		}
		return longDate;
	}
}

