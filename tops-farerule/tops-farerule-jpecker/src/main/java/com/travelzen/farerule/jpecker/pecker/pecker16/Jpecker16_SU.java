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

public class Jpecker16_SU extends Jpecker16__Base {

	public void process(String ruleText) {
		PenaltiesItem penaltiesItem = new PenaltiesItem();
		List<PenaltyCancelItem> penaltyCancelItemList = new ArrayList<PenaltyCancelItem>();
		List<PenaltyChangeItem> penaltyChangeItemList = new ArrayList<PenaltyChangeItem>();
		
		Matcher matcher = Pattern.compile(
				"^\\s*NOTE -\n"
				+ "AT ANY TIME - IN CASE OF REFUND - PAPER TICKET\\s"
				+ "SURCHARGE IS NOT REFUNDABLE WHEN APPLIED").matcher(ruleText);
		if (matcher.find()) {
			PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
			penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent("-1"));
			penaltyCancelItemList.add(penaltyCancelItem);
		} else {
			Jpecker16__General general = new Jpecker16__General();
			general.process("SU", ruleText);
			penalties = general.getPenalties();
			return;
		}
		
		penaltiesItem.setPenaltyCancelItemList(penaltyCancelItemList);
		penaltiesItem.setPenaltyChangeItemList(penaltyChangeItemList);
		penaltiesItemList.add(penaltiesItem);
		penalties.setPenaltiesItemList(penaltiesItemList);
	}
}
