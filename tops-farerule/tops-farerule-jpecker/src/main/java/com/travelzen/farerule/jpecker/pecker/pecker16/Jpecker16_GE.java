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

public class Jpecker16_GE extends Jpecker16__Base {

	public void process(String ruleText) {
		PenaltiesItem penaltiesItem = new PenaltiesItem();
		List<PenaltyCancelItem> penaltyCancelItemList = new ArrayList<PenaltyCancelItem>();
		List<PenaltyChangeItem> penaltyChangeItemList = new ArrayList<PenaltyChangeItem>();
		
		Matcher matcher = Pattern.compile(
				"\nREFUND AND CHANGE--\n"
				+ "FOR TOTALLY UNUSED TICKETS-\n"
				+ "([0-9A-Z ]+?)\\.\n"
				+ "([0-9A-Z ]+?)\\.\n"
				+ "FOR PARTIALLY USED TICKETS-\n").matcher(ruleText);
		if(matcher.find()) {
/*			Matcher matcher1 = Pattern.compile(
					PenaltyConst.PENALTIES + " FOR REFUND").matcher(matcher.group("content"));
			Matcher matcher2 = Pattern.compile(
					"CHANGE" + PenaltyConst.PENALTIES).matcher(matcher.group("content"));
*/
			PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
			penaltyCancelItem.setUsed(false);
			penaltyCancelItem.setPenaltyContent(
					PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parsePenalties(matcher.group(1))));
			penaltyCancelItemList.add(penaltyCancelItem);
			
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
			penaltyChangeItem.setUsed(false);
			penaltyChangeItem.setPenaltyContent(
					PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parsePenalties(matcher.group(2))));
			penaltyChangeItemList.add(penaltyChangeItem);
		}
		
		penaltiesItem.setPenaltyCancelItemList(penaltyCancelItemList);
		penaltiesItem.setPenaltyChangeItemList(penaltyChangeItemList);
		penaltiesItemList.add(penaltiesItem);
		penalties.setPenaltiesItemList(penaltiesItemList);
	}
}
