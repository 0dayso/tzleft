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

public class Jpecker16_OZ extends Jpecker16__Base {

	public void process(String ruleText) {
		PenaltiesItem penaltiesItem = new PenaltiesItem();
		List<PenaltyCancelItem> penaltyCancelItemList = new ArrayList<PenaltyCancelItem>();
		List<PenaltyChangeItem> penaltyChangeItemList = new ArrayList<PenaltyChangeItem>();
		
		Matcher matcher0 = Pattern.compile(
				"^\\s*NOTE -\nNO PENALTY APPLIES").matcher(ruleText);
		Matcher matcher1 = Pattern.compile(
				"\nNO PENALTY APPLIES\n"
				+ "(?:1\\.)?ISSUED IN CHINA - REFUND SERVICE FEE OF\\s"
				+ "([A-Z]{3} \\d+)").matcher(ruleText);
		Matcher matcher2 = Pattern.compile(
				"\nOUTBOUND -\n([\\w\\W]+?)"
				+ "\nINBOUND -\n([\\w\\W]+?)"
				+ "(\nCANCELLATIONS\n[\\w\\W]+)$").matcher(ruleText);
		if (matcher1.find()) {
			PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
			penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(matcher1.group(1)));
			penaltyCancelItemList.add(penaltyCancelItem);
		} else if (matcher0.find()) {
			penalties = null;
			return;
		} else if (matcher2.find()) {
			String outText = matcher2.group(1) + "\n" + matcher2.group(3);
			String inText = matcher2.group(2) + "\n" + matcher2.group(3);
			Jpecker16__Impl impl = new Jpecker16__Impl("OZ");
			PenaltiesItem outPenaltiesItem = impl.parseOnePenaltiesItem(outText);
			outPenaltiesItem.setRuleCondition(
					new RuleCondition().setOriginCondition(ConditionTransducer.parseOrigin("OUTBOUND")));
			PenaltiesItem inPenaltiesItem = impl.parseOnePenaltiesItem(inText);
			inPenaltiesItem.setRuleCondition(
					new RuleCondition().setOriginCondition(ConditionTransducer.parseOrigin("INBOUND")));
			penaltiesItemList.add(outPenaltiesItem);
			penaltiesItemList.add(inPenaltiesItem);
			penalties.setPenaltiesItemList(penaltiesItemList);
			return;
		} else {
			Jpecker16__General general = new Jpecker16__General();
			general.process("OZ", ruleText);
			penalties = general.getPenalties();
			return;
		}
		
		penaltiesItem.setPenaltyCancelItemList(penaltyCancelItemList);
		penaltiesItem.setPenaltyChangeItemList(penaltyChangeItemList);
		penaltiesItemList.add(penaltiesItem);
		penalties.setPenaltiesItemList(penaltiesItemList);
	}
}
