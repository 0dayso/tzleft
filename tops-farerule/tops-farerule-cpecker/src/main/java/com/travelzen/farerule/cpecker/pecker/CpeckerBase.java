/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */
 
package com.travelzen.farerule.cpecker.pecker;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.farerule.jpecker.struct.RuleTextBlock;
import com.travelzen.farerule.jpecker.struct.RuleTextSegment;

public class CpeckerBase {
	
	protected static final Logger log = LoggerFactory.getLogger(CpeckerBase.class);
	
	protected String airCompany;
	
	protected List<RuleTextBlock> ruleTextBlockList = new ArrayList<RuleTextBlock>();
	protected List<RuleTextSegment> ruleTextSegmentList = new ArrayList<RuleTextSegment>();
	
	protected void splitOrigins(String text) {
		text = "\n" + excludeGeneral(text);
		Matcher matcher1 = Pattern.compile("[\n。] *([0-9\\u4e00-\\u9fa5]{1,9}?)始发([\\w\\W]+?)(?=[\n。] *[0-9\\u4e00-\\u9fa5]{1,9}?始发|$)").matcher(text);
		Matcher matcher2 = Pattern.compile("\n *(去程)([\\w\\W]+?)。(?:[\\w\\W]*?\n)? *(回程)([\\w\\W]+?)$").matcher(text);
		RuleTextBlock ruleTextBlock;
		List<String> tmpOriginList = new ArrayList<String>();
		List<String> tmpTextList = new ArrayList<String>();
		if (matcher1.find()) {
			tmpOriginList.add(matcher1.group(1));
			tmpTextList.add(matcher1.group(2));
		} else if (matcher2.find()) {
			ruleTextBlock = new RuleTextBlock();
			ruleTextBlock.setOrigin(matcher2.group(1));
			ruleTextBlock.setText(matcher2.group(2));
			ruleTextBlockList.add(ruleTextBlock);
			ruleTextBlock = new RuleTextBlock();
			ruleTextBlock.setOrigin(matcher2.group(3));
			ruleTextBlock.setText(matcher2.group(4));
			ruleTextBlockList.add(ruleTextBlock);
			return;
		} else {
			ruleTextBlock = new RuleTextBlock();
			ruleTextBlock.setOrigin("");
			ruleTextBlock.setText(text);
			ruleTextBlockList.add(ruleTextBlock);
			return;
		}
		while (matcher1.find()) {
			if (!tmpOriginList.contains(matcher1.group(1))) {
				tmpOriginList.add(matcher1.group(1));
				tmpTextList.add(matcher1.group(2));
			} else {
				int index = tmpOriginList.indexOf(matcher1.group(1));
				tmpTextList.set(index, tmpTextList.get(index)+"\n"+matcher1.group(2));
			}
		}
		for (int i=0; i<tmpOriginList.size(); i++) {
			ruleTextBlock = new RuleTextBlock();
			ruleTextBlock.setOrigin(tmpOriginList.get(i));
			ruleTextBlock.setText(tmpTextList.get(i));
			ruleTextBlockList.add(ruleTextBlock);
		}
	}
	
	protected void splitDates(List<RuleTextBlock> ruleTextBlockList) {
		Pattern pattern1 = Pattern.compile(
				"(\\d{2,4}年\\d{1,2}月\\d{1,2}日(?:/|或)(?:之)?(?:前|后)"
				+ "(?:和\\d{2,4}年\\d{1,2}月\\d{1,2}日(?:/|或)(?:之)?前)?)"
				+ "(?:出|买)票"
				+ "(?:/在?\\d{2,4}年\\d{1,2}月\\d{1,2}日(?:/|或)(?:之)?(?:前|后)"
				+ "(?:和\\d{2,4}年\\d{1,2}月\\d{1,2}日(?:/|或)(?:之)?前)?"
				+ "旅行)?"
				+ "([\\w\\W]+?)"
				+ "(?=\\d{2,4}年\\d{1,2}月\\d{1,2}日(?:/|或)|$)");
		Pattern pattern2 = Pattern.compile(
				"(\\d{2,4}年\\d{1,2}月\\d{1,2}日(?:/|或)(?:之)?(?:前|后)"
				+ "(?:和\\d{2,4}年\\d{1,2}月\\d{1,2}日(?:/|或)(?:之)?前)?)"
				+ "旅行"
				+ "([\\w\\W]+?)"
				+ "(?=\\d{2,4}年\\d{1,2}月\\d{1,2}日(?:/|或)|$)");
		RuleTextSegment ruleTextSegment;
		for (RuleTextBlock ruleTextBlock:ruleTextBlockList) {
			List<RuleTextSegment> tmpRuleTextSegmentList = new ArrayList<RuleTextSegment>();
			String origin = ruleTextBlock.getOrigin();
			Matcher matcher1 = pattern1.matcher(ruleTextBlock.getText());
			Matcher matcher2 = pattern2.matcher(ruleTextBlock.getText());
			while (matcher1.find()) {
				ruleTextSegment = new RuleTextSegment().setOrigin(origin);
				ruleTextSegment.setSalesDate(matcher1.group(1));
				ruleTextSegment.setTravelDate("");
				ruleTextSegment.setText(matcher1.group(2));
				tmpRuleTextSegmentList.add(ruleTextSegment);
			}
			if (tmpRuleTextSegmentList.size() == 0) {
				while (matcher2.find()) {
					ruleTextSegment = new RuleTextSegment().setOrigin(origin);
					ruleTextSegment.setSalesDate("");
					ruleTextSegment.setTravelDate(matcher2.group(1));
					ruleTextSegment.setText(matcher2.group(2));
					tmpRuleTextSegmentList.add(ruleTextSegment);
				}
			}
			if (tmpRuleTextSegmentList.size() == 0) {
				ruleTextSegment = new RuleTextSegment().setOrigin(origin);
				ruleTextSegment.setSalesDate("");
				ruleTextSegment.setTravelDate("");
				ruleTextSegment.setText(ruleTextBlock.getText());
				tmpRuleTextSegmentList.add(ruleTextSegment);
			}
			ruleTextSegmentList.addAll(tmpRuleTextSegmentList);
		}
	}
	
	private String excludeGeneral(String text) {
		Matcher matcher = Pattern.compile("\n本条款如下([\\w\\W]+?)总则如下[\\w\\W]+?$").matcher(text);
		if (matcher.find())
			return matcher.group(1);
		return text;
	}
}
