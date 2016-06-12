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

public class Jpecker16_SC extends Jpecker16__Base {

	public void process(String ruleText) {
		PenaltiesItem penaltiesItem = new PenaltiesItem();
		List<PenaltyCancelItem> penaltyCancelItemList = new ArrayList<PenaltyCancelItem>();
		List<PenaltyChangeItem> penaltyChangeItemList = new ArrayList<PenaltyChangeItem>();
		
		Matcher matcher1 = Pattern.compile(
				"\nREFUND-\n"
				+ "([\\w\\W]+?)"
				+ "(?=\nCHANGES-\n|$)").matcher(ruleText);
		Matcher matcher2 = Pattern.compile(
				"\nCHANGES-\n"
				+ "([\\w\\W]+?)"
				+ "(?=\nREFUND-\n|$)").matcher(ruleText);
		if(matcher1.find() && matcher2.find()) {
			Matcher matcher3 = Pattern.compile(
					"TOTAL(?:Y|LY)? UNUSED\n([\\w\\W]+?)\\d\\.PARTIAL USED([\\w\\W]+)$").matcher(matcher1.group(1));
			Matcher matcher4 = Pattern.compile(
					"VOLUNTARY CHANGE FLIGHT OR DATE([\\w\\W]+?)(?:\\.|$)").matcher(matcher2.group(1));
			if(matcher3.find()) {
				PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
				penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
				penaltyCancelItem.setUsed(false);
				penaltyCancelItem.setPenaltyContent(
						PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parseInside(matcher3.group(1))));
				penaltyCancelItemList.add(penaltyCancelItem);
				penaltyCancelItem = new PenaltyCancelItem();
				penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
				penaltyCancelItem.setUsed(true);
				penaltyCancelItem.setPenaltyContent(
						PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parseInside(matcher3.group(2))));
				penaltyCancelItemList.add(penaltyCancelItem);
			}
			if(matcher4.find()) {
				PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
				penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
				penaltyChangeItem.setPenaltyContent(
						PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parseInside(matcher4.group(1))));
				penaltyChangeItemList.add(penaltyChangeItem);
			}		
		} else {
			Jpecker16__General general = new Jpecker16__General();
			general.process("SC", ruleText);
			penalties = general.getPenalties();
			return;
		}
		
		penaltiesItem.setPenaltyCancelItemList(penaltyCancelItemList);
		penaltiesItem.setPenaltyChangeItemList(penaltyChangeItemList);
		penaltiesItemList.add(penaltiesItem);
		penalties.setPenaltiesItemList(penaltiesItemList);
	}
}
