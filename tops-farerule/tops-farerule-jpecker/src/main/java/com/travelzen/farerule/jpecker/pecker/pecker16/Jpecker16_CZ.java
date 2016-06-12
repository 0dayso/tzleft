/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.jpecker.pecker.pecker16;

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

public class Jpecker16_CZ extends Jpecker16__Base {

	public void process(String ruleText) {
		Matcher matcher1 = Pattern.compile(
				"\nOUTBOUND -\n([\\w\\W]+?)"
				+ "\n(?:AND - )?INBOUND -\n([\\w\\W]+?)"
				+ "(\nCANCELLATIONS\n[\\w\\W]+?)"
				+ "\nNOTE -\n").matcher(ruleText);
		Matcher matcher2 = Pattern.compile(
				"\n(CHANGES\n[\\w\\W]+?)"
				+ "\nAND - (CANCELLATIONS\n[\\w\\W]+?)"
				+ "\nAND - CHANGES/CANCELLATIONS\n([\\w\\W]+?)"
				+ "\nAND -\nNOTE -\n").matcher(ruleText);
		if (matcher1.find()) {
			String outText = matcher1.group(1) + "\n" + matcher1.group(3);
			String inText = matcher1.group(2) + "\n" + matcher1.group(3);
			Jpecker16__Impl impl = new Jpecker16__Impl("CZ");
			PenaltiesItem outPenaltiesItem = impl.parseOnePenaltiesItem(outText);
			outPenaltiesItem.setRuleCondition(
					new RuleCondition().setOriginCondition(ConditionTransducer.parseOrigin("OUTBOUND")));
			penaltiesItemList.add(outPenaltiesItem);
			PenaltiesItem inPenaltiesItem = impl.parseOnePenaltiesItem(inText);
			inPenaltiesItem.setRuleCondition(
					new RuleCondition().setOriginCondition(ConditionTransducer.parseOrigin("INBOUND")));
			penaltiesItemList.add(inPenaltiesItem);
		} else if (matcher2.find()) {
			String text = matcher2.group(1) + "\n" + matcher2.group(2);
			Jpecker16__Impl impl = new Jpecker16__Impl("CZ");
			PenaltiesItem penaltiesItem = impl.parseOnePenaltiesItem(text);
/*			Matcher matcher_reroute = Pattern.compile(
					"TICKET IS REROUTABLE").matcher(matcher2.group(1));
			if(matcher_reroute.find()) {
				PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
				penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.REROUTE);
				penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent("0"));
				penaltiesItem.getPenaltyChangeItemList().add(penaltyChangeItem);
			}
*/			String noshow = Jpecker16__Util.parseNoshow("CZ", matcher2.group(3));
			PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.NOSHOW);
			penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(noshow));
			penaltiesItem.getPenaltyCancelItemList().add(penaltyCancelItem);
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.NOSHOW);
			penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(noshow));
			penaltiesItem.getPenaltyChangeItemList().add(penaltyChangeItem);
			penaltiesItemList.add(penaltiesItem);
		} else {
			Jpecker16__General general = new Jpecker16__General();
			general.process("CZ", ruleText);
			penalties = general.getPenalties();
			return;
		}
		
		penalties.setPenaltiesItemList(penaltiesItemList);
	}
}
