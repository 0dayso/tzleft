package com.travelzen.farerule.mongo.front.simpecker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.travelzen.farerule.jpecker.struct.RuleTextBlock;
import com.travelzen.farerule.jpecker.struct.RuleTextSegment;

public class SimPeckerBase {

	protected List<RuleTextBlock> ruleTextBlockList = new ArrayList<RuleTextBlock>();
	protected List<RuleTextSegment> ruleTextSegmentList = new ArrayList<RuleTextSegment>();
	
	protected void splitOrigins(String text) {
		text = "\n" + text;
		Matcher matcher1 = Pattern.compile("\n([0-9\\u4e00-\\u9fa5]+?始发)([\\w\\W]+?)(?=\n[0-9\\u4e00-\\u9fa5]+?始发|$)").matcher(text);
		Matcher matcher2 = Pattern.compile("\n(去程)([\\w\\W]+?)\n(回程)([\\w\\W]+?)$").matcher(text);
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
				"(\\d{4}\\.\\d{2}\\.\\d{2}或之(?:前|后)|"
				+ "\\d{4}\\.\\d{2}\\.\\d{2}到\\d{4}\\.\\d{2}\\.\\d{2}之间)"
				+ "出票"
				+"(?:\n(\\d{4}\\.\\d{2}\\.\\d{2}或之(?:前|后)|"
				+ "\\d{4}\\.\\d{2}\\.\\d{2}到\\d{4}\\.\\d{2}\\.\\d{2}之间)"
				+ "出发)?"
				+ "([\\w\\W]+?)"
				+ "(?=\\d{4}\\.\\d{2}\\.\\d{2}|$)");
		Pattern pattern2 = Pattern.compile(
				"(\\d{4}\\.\\d{2}\\.\\d{2}或之(?:前|后)|"
				+ "\\d{4}\\.\\d{2}\\.\\d{2}到\\d{4}\\.\\d{2}\\.\\d{2}之间)"
				+ "出发"
				+ "([\\w\\W]+?)"
				+ "(?=\\d{4}\\.\\d{2}\\.\\d{2}|$)");
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

}
