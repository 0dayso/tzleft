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
import com.travelzen.farerule.jpecker.tool.ConditionTransducer;
import com.travelzen.farerule.jpecker.tool.PenaltyTransducer;
import com.travelzen.farerule.rule.PenaltiesItem;
import com.travelzen.farerule.rule.PenaltyCancelItem;
import com.travelzen.farerule.rule.PenaltyCancelTypeEnum;
import com.travelzen.farerule.rule.PenaltyChangeItem;
import com.travelzen.farerule.rule.PenaltyChangeTypeEnum;

public class Jpecker16_CA extends Jpecker16__Base {

	public void process(String ruleText) {
		PenaltiesItem penaltiesItem = new PenaltiesItem();
		List<PenaltyCancelItem> penaltyCancelItemList = new ArrayList<PenaltyCancelItem>();
		List<PenaltyChangeItem> penaltyChangeItemList = new ArrayList<PenaltyChangeItem>();
		
		String outText = "", inText = "";
		Matcher matcher1 = Pattern.compile(
				"^\\s*NOTE -\n"
				+ "REFUND NOT PERMITTED\n"
				+ "REBOOKING NOT PERMITTED\n"
				+ "REROUTING NOT PERMITTED").matcher(ruleText);
		Matcher matcher2 = Pattern.compile(
				"\n {4}OUTBOUND[ -]*\n([\\w\\W]+?)"
				+ "\n {4}INBOUND[ -]*\n([\\w\\W]+?)"
				+ "\n {4}CHANGES[ -]*\n([\\w\\W]+?)"
				+ "\n {4}CANCELLATIONS[ -]*\n([\\w\\W]+)$").matcher(ruleText);
		Matcher matcher3 = Pattern.compile(
				"\n {4}OUTBOUND[ -]*\n([\\w\\W]+?)"
				+ "\n {4}INBOUND[ -]*\n([\\w\\W]+?)"
				+ "\n {4}CANCELLATIONS[ -]*\n([\\w\\W]+)$").matcher(ruleText);		
		if(ruleText.length() < 120 && matcher1.find()) {
			PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
			penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent("-1"));
			penaltyCancelItemList.add(penaltyCancelItem);
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
			penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent("-1"));
			penaltyChangeItemList.add(penaltyChangeItem);
			penaltiesItem.setPenaltyCancelItemList(penaltyCancelItemList);
			penaltiesItem.setPenaltyChangeItemList(penaltyChangeItemList);
			penaltiesItemList.add(penaltiesItem);
			return;
		} else if(matcher2.find()) {
			outText = matcher2.group(1) + "\n" + matcher2.group(3) + "\n" + matcher2.group(4);
			inText = matcher2.group(2) + "\n" + matcher2.group(3) + "\n" + matcher2.group(4);
		} else if(matcher3.find()) {
			outText = matcher3.group(1) + "\n" + matcher3.group(3);
			inText = matcher3.group(2) + "\n" + matcher3.group(3);
		} else {
			Jpecker16__General general = new Jpecker16__General();
			general.process("CA", ruleText);
			penalties = general.getPenalties();
			return;
		}
		
		Jpecker16__Impl impl = new Jpecker16__Impl("CA");
		PenaltiesItem outPenaltiesItem = impl.parseOnePenaltiesItem(outText);
		outPenaltiesItem.setRuleCondition(
				new RuleCondition().setOriginCondition(ConditionTransducer.parseOrigin("OUTBOUND")));
		penaltiesItemList.add(outPenaltiesItem);
		PenaltiesItem inPenaltiesItem = impl.parseOnePenaltiesItem(inText);
		inPenaltiesItem.setRuleCondition(
				new RuleCondition().setOriginCondition(ConditionTransducer.parseOrigin("INBOUND")));
		penaltiesItemList.add(inPenaltiesItem);
		
		penalties.setPenaltiesItemList(penaltiesItemList);
	}
}
