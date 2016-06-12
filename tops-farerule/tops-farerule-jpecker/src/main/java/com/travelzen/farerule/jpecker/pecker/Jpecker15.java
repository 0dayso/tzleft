package com.travelzen.farerule.jpecker.pecker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.farerule.SalesDate;
import com.travelzen.farerule.condition.OriginCondition;
import com.travelzen.farerule.condition.SalesDateSubItem;
import com.travelzen.farerule.jpecker.consts.DateConst;
import com.travelzen.farerule.jpecker.struct.RuleTextBlock;
import com.travelzen.farerule.jpecker.tool.ConditionTransducer;
import com.travelzen.farerule.jpecker.tool.DateTransducer;
import com.travelzen.farerule.rule.SalesDateItem;

public class Jpecker15 extends JpeckerBase {
	
	private static final Logger logger = LoggerFactory.getLogger(Jpecker15.class);
	
	public static SalesDate parse(String ruleText) {
		if (ruleText.contains("NO RESTRICTIONS ON SALES")) {
			return null;
		}
		
		List<SalesDateItem> salesDateItemList = new ArrayList<SalesDateItem>();
		
		List<RuleTextBlock> ruleTextBlockList = splitOrigins(ruleText);
		
		for (RuleTextBlock ruleTextBlock:ruleTextBlockList) {
			SalesDateItem salesDateItem = new SalesDateItem();
			List<SalesDateSubItem> salesDateSubItemList = new ArrayList<SalesDateSubItem>();
			String[] segments = ruleTextBlock.getText().split("\\sOR\\s");
			for (String segment:segments) {
				SalesDateSubItem salesDateSubItem = new SalesDateSubItem();
				salesDateSubItem.setAfterDate(parseAfterDate(segment));
				salesDateSubItem.setBeforeDate(parseBeforeDate(segment));
				if (salesDateSubItem.getAfterDate() != 0 || salesDateSubItem.getBeforeDate() != 0) {
					salesDateSubItemList.add(salesDateSubItem);
				}
			}
			
			if (salesDateSubItemList.size() != 0) {
				if (ruleTextBlock.getOrigin() != null) {
					OriginCondition originCondition = ConditionTransducer.parseOrigin(ruleTextBlock.getOrigin());
					salesDateItem.setOriginCondition(originCondition);
				}
				salesDateItem.setSalesDateSubItemList(salesDateSubItemList);
				salesDateItemList.add(salesDateItem);
			}
		}
		
		SalesDate salesDate = new SalesDate();
		salesDate.setSalesDateItemList(salesDateItemList);
		return salesDate;
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

}
