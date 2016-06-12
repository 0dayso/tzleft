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

public class Jpecker16_MF extends Jpecker16__Base {

	public void process(String ruleText) {
		PenaltiesItem penaltiesItem = new PenaltiesItem();
		List<PenaltyCancelItem> penaltyCancelItemList = new ArrayList<PenaltyCancelItem>();
		List<PenaltyChangeItem> penaltyChangeItemList = new ArrayList<PenaltyChangeItem>();
		
		ruleText = ruleText.replaceAll("/\n?[A-Z]{3} ?\\d+\\b", "");
		
		Matcher matcher1 = Pattern.compile(
				"\n-+FOR CHANGE-+\n([\\w\\W]+)"
				+ "\n-+FOR NOSHOW-+\n([\\w\\W]+)"
				+ "\n-+FOR REFUND-+\n([\\w\\W]+)"
				+ "------").matcher(ruleText);
		Matcher matcher2 = Pattern.compile(
				"^\\s*(OUTBOUND -\n"
				+ "[\\w\\W]+?"
				+ "/+\n-+CHANGES-+\n)"
				+ "(CANCELLATIONS\n"
				+ "[\\w\\W]+?)"
				+ "\nNOTE -\n-+CANCELLATIONS-").matcher(ruleText);
		Matcher matcher3_1 = Pattern.compile(
				"(\nCHANGES\n"
				+ "[\\w\\W]+?)"
				+ "\nNOTE -\n-+CHANGES-").matcher(ruleText);
		Matcher matcher3_2 = Pattern.compile(
				"(\nCANCELLATIONS\n"
				+ "[\\w\\W]+?)"
				+ "\nNOTE -\n-+CANCELLATIONS-").matcher(ruleText);
		if (matcher1.find()) {
			Jpecker16__Impl impl = new Jpecker16__Impl("MF");
			penaltyCancelItemList = impl.parseCancelNormal(matcher1.group(3), ruleText);
			String tmpChange = Jpecker16__Util.parseFirst(matcher1.group(1));
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
			penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(tmpChange));
			penaltyChangeItemList.add(penaltyChangeItem);
			String tmpNoshow = Jpecker16__Util.parseFirst(matcher1.group(2));
			PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.NOSHOW);
			penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(tmpNoshow));
			penaltyCancelItemList.add(penaltyCancelItem);
			penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.NOSHOW);
			penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(tmpNoshow));
			penaltyChangeItemList.add(penaltyChangeItem);
		} else if (matcher2.find()) {
			String text = "\n" + matcher2.group(1) 
					+ "\nOUTBOUND -\n" + matcher2.group(2)
					+ "\n\nINBOUND -\n" + matcher2.group(2);
			Jpecker16__General general = new Jpecker16__General();
			general.process("MF", text);
			penalties = general.getPenalties();
			return;
		} else if (matcher3_1.find() && matcher3_2.find()) {
			String text = matcher3_1.group(1) + "\n" + matcher3_2.group(1);
			Jpecker16__General general = new Jpecker16__General();
			general.process("MF", text);
			penalties = general.getPenalties();
			return;
		} else {
			Jpecker16__General general = new Jpecker16__General();
			general.process("MF", ruleText);
			penalties = general.getPenalties();
			return;
		}
				
		penaltiesItem.setPenaltyCancelItemList(penaltyCancelItemList);
		penaltiesItem.setPenaltyChangeItemList(penaltyChangeItemList);
		penaltiesItemList.add(penaltiesItem);
		penalties.setPenaltiesItemList(penaltiesItemList);
	}
}
