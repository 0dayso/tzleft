/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.jpecker.pecker.pecker16;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.travelzen.farerule.jpecker.tool.PenaltyTransducer;
import com.travelzen.farerule.rule.PenaltiesItem;
import com.travelzen.farerule.rule.PenaltyCancelItem;
import com.travelzen.farerule.rule.PenaltyCancelTypeEnum;
import com.travelzen.farerule.rule.PenaltyChangeItem;
import com.travelzen.farerule.rule.PenaltyChangeTypeEnum;
import com.travelzen.farerule.rule.PenaltyCondition;
import com.travelzen.farerule.rule.PenaltyConditionTypeEnum;

public class Jpecker16_ET extends Jpecker16__Base {

	public void process(String ruleText) {
		PenaltiesItem penaltiesItem = new PenaltiesItem();
		List<PenaltyCancelItem> penaltyCancelItemList = new ArrayList<PenaltyCancelItem>();
		List<PenaltyChangeItem> penaltyChangeItemList = new ArrayList<PenaltyChangeItem>();
		
		Matcher matcher1 = Pattern.compile(
				"\n *CHANGES BEFORE DEPARTURE\n([0-9A-Z \n-]+?)\\.").matcher(ruleText);
		Matcher matcher2 = Pattern.compile(
				"\n *CHANGES AFTER DEPARTURE\n([0-9A-Z \n-]+?)\\.").matcher(ruleText);
		if(matcher1.find()) {
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
			penaltyChangeItem.setPenaltyCondition(
					new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.BEFORE_DEPT));
			penaltyChangeItem.setPenaltyContent(
					PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parsePenalties(matcher1.group(1))));
			penaltyChangeItemList.add(penaltyChangeItem);
		}
		if(matcher2.find()) {
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
			penaltyChangeItem.setPenaltyCondition(
					new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.AFTER_DEPT));
			penaltyChangeItem.setPenaltyContent(
					PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parsePenalties(matcher2.group(1))));
			penaltyChangeItemList.add(penaltyChangeItem);
		}
		
		Matcher matcher3 = Pattern.compile(
				"\n *REFUND\n([0-9A-Z \n-\\.]+?)(?:-{3}|$)").matcher(ruleText);
		if(matcher3.find()) {
			Matcher matcher4 = Pattern.compile(
					"BEFORE DEPARTURE([0-9A-Z \n-\\.]+?)(?=AFTER DEPARTURE|$)").matcher(matcher3.group(1));
			Matcher matcher5 = Pattern.compile(
					"AFTER DEPARTURE([0-9A-Z \n-\\.]+?)$").matcher(matcher3.group(1));
			if(matcher4.find()) {
				PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
				penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
				penaltyCancelItem.setPenaltyCondition(
						new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.BEFORE_DEPT));
				penaltyCancelItem.setPenaltyContent(
						PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parsePenalties(matcher4.group(1))));
				penaltyCancelItemList.add(penaltyCancelItem);
			}
			if(matcher5.find()) {
				PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
				penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
				penaltyCancelItem.setPenaltyCondition(
						new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.AFTER_DEPT));
				penaltyCancelItem.setPenaltyContent(
						PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parsePenalties(matcher5.group(1))));
				penaltyCancelItemList.add(penaltyCancelItem);
			}
		}
		
		penaltiesItem.setPenaltyCancelItemList(penaltyCancelItemList);
		penaltiesItem.setPenaltyChangeItemList(penaltyChangeItemList);
		penaltiesItemList.add(penaltiesItem);
		penalties.setPenaltiesItemList(penaltiesItemList);
	}
}
