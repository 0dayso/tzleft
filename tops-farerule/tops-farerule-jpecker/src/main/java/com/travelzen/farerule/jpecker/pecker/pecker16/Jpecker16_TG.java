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
import com.travelzen.farerule.rule.PenaltyContent;
import com.travelzen.farerule.rule.PenaltyTypeEnum;

public class Jpecker16_TG extends Jpecker16__Base {
	
	public void process(String ruleText) {
		PenaltiesItem penaltiesItem = new PenaltiesItem();
		List<PenaltyCancelItem> penaltyCancelItemList = new ArrayList<PenaltyCancelItem>();
		List<PenaltyChangeItem> penaltyChangeItemList = new ArrayList<PenaltyChangeItem>();
		
		Matcher matcher1 = Pattern.compile(
				"^\\s*NOTE - GENERAL RULE DOES NOT APPLY").matcher(ruleText);
		Matcher matcher2 = Pattern.compile(
				"^\\s*NOTE - RULE 31TG IN IPRG100 APPLIES").matcher(ruleText);
		if(matcher1.find()) {
			String text = Jpecker16__Util.rmNoise(ruleText);
			Matcher matcher3_1 = Pattern.compile(
					"TOTALLY UNUSED TICKET([\\w\\W]+?)PARTIALLY USED TICKET([\\w\\W]+)(?:\\.|$)").matcher(text);
			Matcher matcher3_2 = Pattern.compile(
					"\nCANCELLATIONS/REFUNDS[ -]*\n([\\w\\W]+?)PARTIALLY REFUNDS([\\w\\W]+)(?:\\.|$)").matcher(text);
			Matcher matcher4 = Pattern.compile(
					"RESERVATION CHANGES AFTER TICKET ISSUED\n"
					+ "REISSUE FOR FLIGHT DATE CHANGE ([\\w\\W]+?)\\.").matcher(text);
//			Matcher matcher5 = Pattern.compile(
//					"REROUTING[ -]*" + PenaltyConst.PENALTIES).matcher(text);
			String cancel_unused = "", cancel_used = "";
			if(matcher3_1.find()) {
				cancel_unused = Jpecker16__Util.parseInside(matcher3_1.group(1));
				cancel_used = Jpecker16__Util.parseInside(matcher3_1.group(2));
			} else if(matcher3_2.find()) {
				cancel_unused = Jpecker16__Util.parseInside(matcher3_2.group(1));
				cancel_used = Jpecker16__Util.parseInside(matcher3_2.group(2));
			}
			if (!cancel_unused.equals("")) {
				PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
				penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
				penaltyCancelItem.setUsed(false);
				penaltyCancelItem.setPenaltyContent(
						PenaltyTransducer.parsePenaltyContent(cancel_unused));
				penaltyCancelItemList.add(penaltyCancelItem);
			}
			if (!cancel_used.equals("")) {
				PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
				penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
				penaltyCancelItem.setUsed(true);
				penaltyCancelItem.setPenaltyContent(
						PenaltyTransducer.parsePenaltyContent(cancel_used));
				penaltyCancelItemList.add(penaltyCancelItem);
			}
			if(matcher4.find()) {
				String change = Jpecker16__Util.parseInside(matcher4.group(1));
				PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
				penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
				penaltyChangeItem.setPenaltyContent(
						PenaltyTransducer.parsePenaltyContent(change));
				penaltyChangeItemList.add(penaltyChangeItem);
				penaltyChangeItem = new PenaltyChangeItem();
				penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.REISSUE);
				penaltyChangeItem.setPenaltyContent(
						PenaltyTransducer.parsePenaltyContent(change));
				penaltyChangeItemList.add(penaltyChangeItem);
			}
/*			if(matcher5.find()) {
				String reroute = Jpecker16__Util.parseInside(matcher5.group("penalty"));
				PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
				penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.REROUTE);
				penaltyChangeItem.setPenaltyContent(
						PenaltyTransducer.parsePenaltyContent(reroute));
				penaltyChangeItemList.add(penaltyChangeItem);
			}
*/
			if(penaltyCancelItemList.size() == 0 && penaltyChangeItemList.size() == 0) {
				Jpecker16__Impl impl = new Jpecker16__Impl("TG");
				penaltiesItem = impl.parseOnePenaltiesItem(text);
				for (PenaltyChangeItem penaltyChangeItem:penaltiesItem.getPenaltyChangeItemList()) {
					if (penaltyChangeItem.getPenaltyChangeType() == PenaltyChangeTypeEnum.NOSHOW
							&& penaltyChangeItem.getPenaltyContent().getPenaltyType() == PenaltyTypeEnum.NOT_PERMIT) {
						PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
						penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.NOSHOW);
						penaltyCancelItem.setPenaltyContent(new PenaltyContent().setPenaltyType(PenaltyTypeEnum.NOT_PERMIT));
						penaltiesItem.getPenaltyCancelItemList().add(penaltyCancelItem);
						break;
					}
				}
				penaltiesItemList.add(penaltiesItem);
				penalties.setPenaltiesItemList(penaltiesItemList);
				return;
			}	
		} else if(matcher2.find()) {
			Matcher matcher = Pattern.compile(
					"\n *TRAFFIC DOCUMENT ISSUED OUTSIDE THAILAND\n([\\w\\W]+?)(?:-{3}|$)").matcher(ruleText);
			if(matcher.find()) {
				Jpecker16__General general = new Jpecker16__General();
				general.process("TG", matcher.group(1));
				penalties = general.getPenalties();
			} else {
				Jpecker16__General general = new Jpecker16__General();
				general.process("TG", ruleText);
				penalties = general.getPenalties();
			}
			return;
		} else {
			Jpecker16__General general = new Jpecker16__General();
			general.process("TG", ruleText);
			penalties = general.getPenalties();
			return;
		}
		
		penaltiesItem.setPenaltyCancelItemList(penaltyCancelItemList);
		penaltiesItem.setPenaltyChangeItemList(penaltyChangeItemList);
		penaltiesItemList.add(penaltiesItem);
		penalties.setPenaltiesItemList(penaltiesItemList);
	}
}
