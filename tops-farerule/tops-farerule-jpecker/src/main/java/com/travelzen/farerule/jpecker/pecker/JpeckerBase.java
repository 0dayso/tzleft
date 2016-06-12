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

import com.travelzen.farerule.jpecker.consts.ConditionConst;
import com.travelzen.farerule.jpecker.struct.RuleTextBlock;
import com.travelzen.farerule.jpecker.struct.RuleTextSegment;

public class JpeckerBase {
	
	protected static final Logger log = LoggerFactory.getLogger(Jpecker16.class);
	
	protected String airCompany;
	
	protected static List<RuleTextBlock> splitOrigins(String text) {
		List<RuleTextBlock> ruleTextBlockList = new ArrayList<RuleTextBlock>();
		text = "\n" + text;
		Matcher matcher = Pattern.compile(ConditionConst.ORIGINS).matcher(text);
		RuleTextBlock ruleTextBlock;
		if (matcher.find()) {
			ruleTextBlock = new RuleTextBlock();
			ruleTextBlock.setOrigin(matcher.group(1));
			ruleTextBlock.setText(matcher.group(2));
			ruleTextBlockList.add(ruleTextBlock);
		} else {
			ruleTextBlock = new RuleTextBlock();
			ruleTextBlock.setText(text);
			ruleTextBlockList.add(ruleTextBlock);
			return ruleTextBlockList;
		}
		while (matcher.find()) {
			ruleTextBlock = new RuleTextBlock();
			ruleTextBlock.setOrigin(matcher.group(1));
			ruleTextBlock.setText(matcher.group(2));
			ruleTextBlockList.add(ruleTextBlock);
		}
		return ruleTextBlockList;
	}
	
	protected static List<RuleTextSegment> splitDates(List<RuleTextBlock> ruleTextBlockList) {
		List<RuleTextSegment> ruleTextSegmentList = new ArrayList<RuleTextSegment>();
		Pattern pattern1 = Pattern.compile(ConditionConst.SALESRES);
		Pattern pattern2 = Pattern.compile(ConditionConst.TRAVELRES);
		for (RuleTextBlock ruleTextBlock:ruleTextBlockList) {
			RuleTextSegment ruleTextSegment = new RuleTextSegment().setOrigin(ruleTextBlock.getOrigin());
			Matcher matcher1 = pattern1.matcher(ruleTextBlock.getText());
			Matcher matcher2 = pattern2.matcher(ruleTextBlock.getText());
			if (matcher1.find()) {
				ruleTextSegment.setSalesDate(matcher1.group(1));
				if (matcher1.group(2) != null)
					ruleTextSegment.setTravelDate(matcher1.group(2));
				ruleTextSegment.setText(matcher1.group(3));
				ruleTextSegmentList.add(ruleTextSegment);
				while (matcher1.find()) {
					ruleTextSegment = new RuleTextSegment().setOrigin(ruleTextBlock.getOrigin());
					ruleTextSegment.setSalesDate(matcher1.group(1));
					if (matcher1.group(2) != null)
						ruleTextSegment.setTravelDate(matcher1.group(2));
					ruleTextSegment.setText(matcher1.group(3));
					ruleTextSegmentList.add(ruleTextSegment);
				}
			} else if (matcher2.find()) {
				ruleTextSegment.setTravelDate(matcher2.group(1));
				ruleTextSegment.setText(matcher2.group(2));
				ruleTextSegmentList.add(ruleTextSegment);
				while (matcher2.find()) {
					ruleTextSegment = new RuleTextSegment().setOrigin(ruleTextBlock.getOrigin());
					ruleTextSegment.setTravelDate(matcher2.group(1));
					ruleTextSegment.setText(matcher2.group(2));
					ruleTextSegmentList.add(ruleTextSegment);
				}
			} else {
				ruleTextSegment.setText(ruleTextBlock.getText());
				ruleTextSegmentList.add(ruleTextSegment);
			}
		}
		return ruleTextSegmentList;
	}

}
