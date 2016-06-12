/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.jpecker.pecker.pecker16;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.travelzen.farerule.condition.RuleCondition;
import com.travelzen.farerule.jpecker.consts.PenaltyConst;
import com.travelzen.farerule.jpecker.struct.RuleTextBlock;
import com.travelzen.farerule.jpecker.tool.ConditionTransducer;
import com.travelzen.farerule.jpecker.tool.PenaltyTransducer;
import com.travelzen.farerule.rule.PenaltiesItem;
import com.travelzen.farerule.rule.PenaltyCancelItem;
import com.travelzen.farerule.rule.PenaltyCancelTypeEnum;
import com.travelzen.farerule.rule.PenaltyChangeItem;
import com.travelzen.farerule.rule.PenaltyChangeTypeEnum;

public class Jpecker16_3U extends Jpecker16__Base {
	
	public void process(String ruleText) {
		List<RuleTextBlock> ruleTextBlockList = splitOrigins(ruleText);
		
		PenaltiesItem penaltiesItem;
		for(RuleTextBlock ruleTextBlock:ruleTextBlockList) {
			penaltiesItem = parseText(ruleTextBlock.getOrigin(), ruleTextBlock.getText());
			penaltiesItemList.add(penaltiesItem);
		}
		penalties.setPenaltiesItemList(penaltiesItemList);
	}
	
	private PenaltiesItem parseText(String origin, String text) {
		PenaltiesItem penaltiesItem = new PenaltiesItem();
		List<PenaltyCancelItem> penaltyCancelItemList = new ArrayList<PenaltyCancelItem>();
		List<PenaltyChangeItem> penaltyChangeItemList = new ArrayList<PenaltyChangeItem>();
		
		Matcher matcher1 = Pattern.compile(
				PenaltyConst.PENALTIES 
				+ "\\s(?:PER\\sSECTOR\\s)?(?:FOR\\s|SUBJECT\\sTO\\s)REFUND").matcher(text);
		if (matcher1.find()) {
			PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
			penaltyCancelItem.setPenaltyContent(
					PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parsePenalties(matcher1.group("penalty"))));
			penaltyCancelItemList.add(penaltyCancelItem);
		}
		Matcher matcher2 = Pattern.compile(
				PenaltyConst.PENALTIES 
				+ "\\s(?:PER\\sSECTOR\\s)?(?:FOR|(?:SUBJECT\\s)?TO)\\sDATE\\sCHANGE").matcher(text);
		if (matcher2.find()) {
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
			penaltyChangeItem.setPenaltyContent(
					PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parsePenalties(matcher2.group("penalty"))));
			penaltyChangeItemList.add(penaltyChangeItem);
		}
		Matcher matcher3 = Pattern.compile(
				PenaltyConst.PENALTIES 
				+ "\\s(?:EACH\\sTIME\\s)?(?:PER\\sSECTOR\\s)?(?:FOR\\s|SUBJECT\\sTO\\s)NO[ -]?SHOW").matcher(text);
		if (matcher3.find()) {
			PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.NOSHOW);
			penaltyCancelItem.setPenaltyContent(
					PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parsePenalties(matcher3.group("penalty"))));
			penaltyCancelItemList.add(penaltyCancelItem);
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.NOSHOW);
			penaltyChangeItem.setPenaltyContent(
					PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parsePenalties(matcher3.group("penalty"))));
			penaltyChangeItemList.add(penaltyChangeItem);
		}
		
		penaltiesItem.setRuleCondition(new RuleCondition().setOriginCondition(ConditionTransducer.parseOrigin(origin)));
		penaltiesItem.setPenaltyCancelItemList(penaltyCancelItemList);
		penaltiesItem.setPenaltyChangeItemList(penaltyChangeItemList);
		return penaltiesItem;
	}
}
