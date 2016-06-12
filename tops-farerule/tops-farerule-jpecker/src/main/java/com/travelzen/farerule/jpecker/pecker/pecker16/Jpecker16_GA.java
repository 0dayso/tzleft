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
import com.travelzen.farerule.jpecker.consts.ConditionConst;
import com.travelzen.farerule.jpecker.consts.PenaltyConst;
import com.travelzen.farerule.jpecker.tool.ConditionTransducer;
import com.travelzen.farerule.jpecker.tool.PenaltyTransducer;
import com.travelzen.farerule.rule.PenaltiesItem;
import com.travelzen.farerule.rule.PenaltyCancelItem;
import com.travelzen.farerule.rule.PenaltyCancelTypeEnum;
import com.travelzen.farerule.rule.PenaltyChangeItem;
import com.travelzen.farerule.rule.PenaltyChangeTypeEnum;
import com.travelzen.farerule.rule.PenaltyCondition;
import com.travelzen.farerule.rule.PenaltyConditionTypeEnum;

public class Jpecker16_GA extends Jpecker16__Base {
	
	private Jpecker16__Impl impl = new Jpecker16__Impl("GA");

	public void process(String ruleText) {
		PenaltiesItem penaltiesItem = new PenaltiesItem();
		
		Matcher matcher0 = Pattern.compile(
				"\nFOR DOI (ON/BEFORE " + ConditionConst.date
				+ ")([\\w\\W]+)"
				+ "\nFOR DOI (ON/AFTER " + ConditionConst.date
				+ ")([\\w\\W]+)$").matcher(ruleText);
		Matcher matcher1 = Pattern.compile(
				"\n(?:[A-Z][ \\.]*)?" + PenaltyConst.cancel + "([\\w\\W]+?)"
				+ "\nREBOOKING(?: (?:AND REROUTING|AFTER TICKETING)[ -]*)?\n([\\w\\W]+)$").matcher(ruleText);
		Matcher matcher2 = Pattern.compile(
				"^([\\w\\W]+\n)FOR ISSUED (ON[ /]AFTER " + ConditionConst.date
				+ ")(\n([\\w\\W]+))$").matcher(ruleText);
		if (matcher0.find()) {
			penaltiesItem = processGAGeneral(matcher0.group(2));
			penaltiesItem.setRuleCondition(
					new RuleCondition().setSalesDateCondition(
							ConditionTransducer.parseSalesDate(matcher0.group(1))));
			penaltiesItemList.add(penaltiesItem);
			penaltiesItem = processGAsp(matcher0.group(4));
			penaltiesItem.setRuleCondition(
					new RuleCondition().setSalesDateCondition(
							ConditionTransducer.parseSalesDate(matcher0.group(3))));
			penaltiesItemList.add(penaltiesItem);
		} else if (matcher1.find()) {
			penaltiesItem = processGA(ruleText, matcher1.group(1), matcher1.group(2), "", "", "");
			String tmpReissue = Jpecker16__Util.parseReissue("GA", ruleText);
			if (!tmpReissue.equals("")) {
				PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
				penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.REISSUE);
				penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(tmpReissue));
				penaltiesItem.getPenaltyChangeItemList().add(penaltyChangeItem);
			}
			penaltiesItemList.add(penaltiesItem);
			return;
		} else if (matcher2.find()) {
			penaltiesItem = impl.parseOnePenaltiesItem(matcher2.group(1));
			penaltiesItemList.add(penaltiesItem);
			penaltiesItem = impl.parseOnePenaltiesItem(matcher2.group(3));
			penaltiesItem.setRuleCondition(
					new RuleCondition().setSalesDateCondition(
							ConditionTransducer.parseSalesDate(matcher2.group(2))));
			penaltiesItemList.add(penaltiesItem);
		} else {
			penaltiesItem = processGAGeneral(ruleText);
			penaltiesItemList.add(penaltiesItem);
		}
		
		penalties.setPenaltiesItemList(penaltiesItemList);
	}
	
	private PenaltiesItem processGAGeneral(String ruleText) {
		Matcher matcher_cancel = Pattern.compile(
				"\n(?:[A-Z][ \\.]+)?" + PenaltyConst.cancel + "([\\w\\W]+?)"
				+ "(?=\n[A-Z][ \\.]+[A-Z]|$)").matcher(ruleText);
		Matcher matcher_change = Pattern.compile(
				"\n[A-Z][ \\.]+" + PenaltyConst.change + "([\\w\\W]+?)"
				+ "(?=\n[A-Z][ \\.]+[A-Z]|\n-REROUTING\n|$)").matcher(ruleText);
		Matcher matcher_noshow = Pattern.compile(
				"\n[A-Z][ \\.]+" + PenaltyConst.noshow + "([\\w\\W]+?)"
				+ "(?=\n[A-Z][ \\.]+[A-Z]|$)").matcher(ruleText);
		Matcher matcher_reissue = Pattern.compile(
				"\n[A-Z][ \\.]+RE[ -]?ISSUED?([\\w\\W]+?)"
				+ "(?=\n[A-Z][ \\.]+[A-Z]|$)").matcher(ruleText);
		Matcher matcher_reissue2 = Pattern.compile(
				"CHARGE [A-Z]{3} ?\\d+ FOR REISSUE").matcher(ruleText);
//		Matcher matcher_reroute = Pattern.compile(
//				"\n(?:[A-Z][ \\.]+|-)REROUTING([\\w\\W]+?)"
//				+ "(?=\n[A-Z][ \\.]+[A-Z]|\n-NAME AMANDEMENT\n|$)").matcher(ruleText);
		
		String cancel = "", change = "", noshow = "", reissue = "", reroute = "";
		if (matcher_cancel.find())
			cancel = matcher_cancel.group(1);
		if (matcher_change.find())
			change = matcher_change.group(1);
		if (matcher_noshow.find())
			noshow = matcher_noshow.group(1);
		if (matcher_reissue.find())
			reissue = matcher_reissue.group(1);
		else if (matcher_reissue2.find())
			reissue = matcher_reissue2.group();
//		if (matcher_reroute.find())
//			reroute = matcher_reroute.group(1);
				
		PenaltiesItem penaltiesItem = processGA(ruleText, cancel, change, noshow, reissue, reroute);
		return penaltiesItem;
	}
	
	private PenaltiesItem processGA(String ruleText, String cancel, String change, String noshow, String reissue, String reroute) {
		List<PenaltyCancelItem> penaltyCancelItemList = impl.parseCancelNormal(cancel, ruleText);
		List<PenaltyChangeItem> penaltyChangeItemList = impl.parseChangeNormal(change, ruleText);
		if (!noshow.equals("")) {
			String tmpNoshow = Jpecker16__Util.parseInside(noshow);
			if (!tmpNoshow.equals("")) {
				PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
				penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.NOSHOW);
				penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(tmpNoshow));
				penaltyCancelItemList.add(penaltyCancelItem);
				if (!noshow.startsWith("/CANCEL")) {
					PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
					penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.NOSHOW);
					penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(tmpNoshow));
					penaltyChangeItemList.add(penaltyChangeItem);
				}
			}
		} else {
			String tmpNoshow1 = Jpecker16__Util.parseNoshow("GA", cancel);
			String tmpNoshow2 = Jpecker16__Util.parseNoshow("GA", change);
			if (!tmpNoshow1.equals("")) {
				PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
				penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.NOSHOW);
				penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(tmpNoshow1));
				penaltyCancelItemList.add(penaltyCancelItem);
			}
			if (!tmpNoshow2.equals("")) {
				PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
				penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.NOSHOW);
				penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(tmpNoshow2));
				penaltyChangeItemList.add(penaltyChangeItem);
			}
		}
		if (!reissue.equals("")) {
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.REISSUE);
			penaltyChangeItem.setPenaltyContent(
					PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parsePenalties(reissue)));
			penaltyChangeItemList.add(penaltyChangeItem);
		}
/*		if (!reroute.equals("")) {
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.REROUTE);
			penaltyChangeItem.setPenaltyContent(
					PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parsePenalties(reroute)));
			penaltyChangeItemList.add(penaltyChangeItem);
		}
*/		
		PenaltiesItem penaltiesItem = new PenaltiesItem();
		penaltiesItem.setPenaltyCancelItemList(penaltyCancelItemList);
		penaltiesItem.setPenaltyChangeItemList(penaltyChangeItemList);
		return penaltiesItem;
	}
	
	private PenaltiesItem processGAsp(String text) {
		List<PenaltyCancelItem> penaltyCancelItemList = new ArrayList<PenaltyCancelItem>();
		List<PenaltyChangeItem> penaltyChangeItemList = new ArrayList<PenaltyChangeItem>();
		
		Matcher matcher_ifused = Pattern.compile(
				PenaltyConst.UNUSED
				+ PenaltyConst.CONTENT1 + PenaltyConst.USED
				+ PenaltyConst.CONTENT2 + "(?=TOTALLY UNUSED|$)").matcher(text);
		Pattern pattern_bdad = Pattern.compile(
				"BEFORE[ \n]?FLIGHT[ \n]?"
				+ "([\\w\\W]+?)"
				+ "AFTER[ \n]?FLIGHT[ \n]?"
				+ "([\\w\\W]+?)$");
		
		if (matcher_ifused.find()) {
			Matcher matcher_unused = pattern_bdad.matcher((matcher_ifused.group("content1")));
			if (matcher_unused.find()) {
				PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
				penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
				penaltyChangeItem.setUsed(false);
				penaltyChangeItem.setPenaltyCondition(
						new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.BEFORE_DEPT))
					.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parsePenalties(matcher_unused.group(1))));
				penaltyChangeItemList.add(penaltyChangeItem);
				penaltyChangeItem = new PenaltyChangeItem();
				penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
				penaltyChangeItem.setUsed(false);
				penaltyChangeItem.setPenaltyCondition(
						new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.AFTER_DEPT))
					.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parsePenalties(matcher_unused.group(2))));
				penaltyChangeItemList.add(penaltyChangeItem);
			}
			Matcher matcher_used = pattern_bdad.matcher((matcher_ifused.group("content2")));
			if (matcher_used.find()) {
				PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
				penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
				penaltyChangeItem.setUsed(true);
				penaltyChangeItem.setPenaltyCondition(
						new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.BEFORE_DEPT))
					.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parsePenalties(matcher_used.group(1))));
				penaltyChangeItemList.add(penaltyChangeItem);
				penaltyChangeItem = new PenaltyChangeItem();
				penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
				penaltyChangeItem.setUsed(true);
				penaltyChangeItem.setPenaltyCondition(
						new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.AFTER_DEPT))
					.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parsePenalties(matcher_used.group(2))));
				penaltyChangeItemList.add(penaltyChangeItem);
			}
		}
		if (matcher_ifused.find()) {
			Matcher matcher_unused = pattern_bdad.matcher((matcher_ifused.group("content1")));
			if (matcher_unused.find()) {
				PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
				penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
				penaltyCancelItem.setUsed(false);
				penaltyCancelItem.setPenaltyCondition(
						new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.BEFORE_DEPT))
					.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parsePenalties(matcher_unused.group(1))));
				penaltyCancelItemList.add(penaltyCancelItem);
				penaltyCancelItem = new PenaltyCancelItem();
				penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
				penaltyCancelItem.setUsed(false);
				penaltyCancelItem.setPenaltyCondition(
						new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.AFTER_DEPT))
					.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parsePenalties(matcher_unused.group(2))));
				penaltyCancelItemList.add(penaltyCancelItem);
			}
			Matcher matcher_used = pattern_bdad.matcher((matcher_ifused.group("content2")));
			if (matcher_used.find()) {
				PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
				penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
				penaltyCancelItem.setUsed(true);
				penaltyCancelItem.setPenaltyCondition(
						new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.BEFORE_DEPT))
					.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parsePenalties(matcher_used.group(1))));
				penaltyCancelItemList.add(penaltyCancelItem);
				penaltyCancelItem = new PenaltyCancelItem();
				penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
				penaltyCancelItem.setUsed(true);
				penaltyCancelItem.setPenaltyCondition(
						new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.AFTER_DEPT))
					.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.parsePenalties(matcher_used.group(2))));
				penaltyCancelItemList.add(penaltyCancelItem);
			}
		}
		
		PenaltiesItem penaltiesItem = new PenaltiesItem();
		penaltiesItem.setPenaltyCancelItemList(penaltyCancelItemList);
		penaltiesItem.setPenaltyChangeItemList(penaltyChangeItemList);
		return penaltiesItem;
	}

}