package com.travelzen.farerule.mongo.front.simpecker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.travelzen.farerule.TravelDate;
import com.travelzen.farerule.condition.OriginCondition;
import com.travelzen.farerule.condition.TravelDateSubItem;
import com.travelzen.farerule.mongo.front.simpecker.tool.ConditionTransducer;
import com.travelzen.farerule.mongo.front.simpecker.tool.DateTransducer;
import com.travelzen.farerule.rule.TravelDateItem;

public class SimPecker14 extends SimPeckerBase {
	
	private TravelDate travelDate;
	private List<TravelDateItem> travelDateItemList;
	
	public SimPecker14() {
		travelDate = new TravelDate();
		travelDateItemList = new ArrayList<TravelDateItem>();
	}

	public TravelDate getTravelDate() {
		return travelDate;
	}
	
	public void process(String ruleText) {
		if (ruleText.contains("无限制")) {
			return;
		}
		
		String[] segments = ruleText.split("。");
		Pattern pattern_segment = Pattern.compile("第(\\d)段航程");
		Pattern pattern_origin = Pattern.compile("(?:\n|^)([\\u4e00-\\u9fa5]+始发|去程|回程)");
		Pattern pattern_complete = Pattern.compile("行程必须于(\\d{4}\\.\\d{2}\\.\\d{2})前结束");
		for (String segment:segments) {
			TravelDateItem travelDateItem = new TravelDateItem();
			
			List<TravelDateSubItem> travelDateSubItemList = parseTravelDateSubItemList(segment);
			if (travelDateSubItemList.size() != 0)
				travelDateItem.setTravelDateSubItemList(travelDateSubItemList);
			else
				continue;
			
			Matcher matcher_complete = pattern_complete.matcher(segment);
			if (matcher_complete.find())
				travelDateItem.setCompleteDate(DateTransducer.parseDateSim(matcher_complete.group(2)));
			
			Matcher matcher_segment = pattern_segment.matcher(segment);
			if (matcher_segment.find())
				travelDateItem.setSegmentNum(Integer.parseInt(matcher_segment.group(1)));
			
			OriginCondition originCondition = new OriginCondition();
			Matcher matcher_origin = pattern_origin.matcher(segment);
			if (matcher_origin.find()) {
				originCondition = ConditionTransducer.parseOriginSim(matcher_origin.group(1));
			}
			travelDateItem.setOriginCondition(originCondition);
			
			travelDateItemList.add(travelDateItem);
		}
		
		travelDate.setTravelDateItemList(travelDateItemList);
	}

	private List<TravelDateSubItem> parseTravelDateSubItemList(String segment) {
		List<TravelDateSubItem> travelDateSubItemList = new ArrayList<TravelDateSubItem>();
		Matcher matcher = Pattern.compile(
				"\\d{4}\\.\\d{2}\\.\\d{2}或之(?:前|后)|"
				+ "\\d{4}\\.\\d{2}\\.\\d{2}到\\d{4}\\.\\d{2}\\.\\d{2}之间").matcher(segment);
		while (matcher.find()) {
			TravelDateSubItem travelDateSubItem = ConditionTransducer.parseTravelDateSim(matcher.group());
			travelDateSubItemList.add(travelDateSubItem);
		}
		return travelDateSubItemList;
	}

}
