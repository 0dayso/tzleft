package com.travelzen.farerule.jpecker.pecker.pecker16;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.travelzen.farerule.condition.RuleCondition;
import com.travelzen.farerule.condition.SalesDateSubItem;
import com.travelzen.farerule.condition.TravelDateSubItem;
import com.travelzen.farerule.jpecker.struct.RuleTextSegment;
import com.travelzen.farerule.jpecker.tool.ConditionTransducer;
import com.travelzen.farerule.jpecker.tool.PenaltyTransducer;
import com.travelzen.farerule.rule.PenaltiesItem;
import com.travelzen.farerule.rule.PenaltyCancelItem;
import com.travelzen.farerule.rule.PenaltyCancelTypeEnum;
import com.travelzen.farerule.rule.PenaltyChangeItem;
import com.travelzen.farerule.rule.PenaltyChangeTypeEnum;
import com.travelzen.farerule.rule.PenaltyCondition;
import com.travelzen.farerule.rule.PenaltyConditionTypeEnum;

public class Jpecker16_UO extends Jpecker16__Base {
	
	public void process(String ruleText) {
		Jpecker16__Pre pre = new Jpecker16__Pre();
		pre.process(ruleText);
		List<RuleTextSegment> ruleTextSegmentList = ruleTextSegmentList = pre.getReadyList();
		if (ruleTextSegmentList.size() != 0) {
			Matcher matcher = Pattern.compile(
					"^\\s*\nNOTE -\n").matcher(ruleTextSegmentList.get(0).getText());
			if (matcher.find()) {
				parseSpecialUO(ruleTextSegmentList);
				return;
			}
		}
		
		Jpecker16__Impl impl = new Jpecker16__Impl("UO");
		impl.process(ruleTextSegmentList);
		penalties = impl.getPenalties();		
	}

	private void parseSpecialUO(List<RuleTextSegment> ruleTextSegmentList) {
		for (RuleTextSegment ruleTextSegment:ruleTextSegmentList) {
			String text = ruleTextSegment.getText();
			PenaltiesItem penaltiesItem = parsePenaltiesItem(text);
			if (penaltiesItem.getPenaltyCancelItemList().size() == 0
					&& penaltiesItem.getPenaltyChangeItemList().size() == 0)
				continue;
			RuleCondition ruleCondition = new RuleCondition();
			ruleCondition.setOriginCondition(ConditionTransducer.parseOrigin(ruleTextSegment.getOrigin()));
			SalesDateSubItem salesDateCondition = ConditionTransducer.parseSalesDate(ruleTextSegment.getSalesDate());
			TravelDateSubItem travelDateCondition = ConditionTransducer.parseTravelDate(ruleTextSegment.getTravelDate());
			ruleCondition.setSalesDateCondition(salesDateCondition);
			ruleCondition.setTravelDateCondition(travelDateCondition);
			penaltiesItem.setRuleCondition(ruleCondition);
			penaltiesItemList.add(penaltiesItem);
		}
		penalties.setPenaltiesItemList(penaltiesItemList);
	}

	private PenaltiesItem parsePenaltiesItem(String text) {
		PenaltiesItem penaltiesItem = new PenaltiesItem();
		List<PenaltyCancelItem> penaltyCancelItemList = new ArrayList<PenaltyCancelItem>();
		List<PenaltyChangeItem> penaltyChangeItemList = new ArrayList<PenaltyChangeItem>();
		Matcher matcher_refund = Pattern.compile(
				"FARES\\sARE\\sNONREFUNDABLE").matcher(text);
		Matcher matcher_noshow = Pattern.compile(
				"NO-SHOW\\sOR\\sFAILURE\\sTO\\sCHECK\\sIN/BOARD\\sON\\sTIME\\sWILL\\sRESULT\\s"
				+ "IN\\sFORFEITURE\\sOF\\sTHE\\sFARE").matcher(text);
		Matcher matcher_change = Pattern.compile(
				"DATE/TIME\\sCHANGES\\sPERMITTED\\sUP\\sTO\\s(\\d)\\sDAYS\\sBEFORE\\sDEPARTURE\\s"
				+ "FOR\\sA\\sFEE\\sOF\\s(?<penalty>[A-Z]{3}\\s\\d+)").matcher(text);
//		Matcher matcher_reroute = Pattern.compile(
//				"REROUTE\\sIS\\sNOT\\sPERMITTED").matcher(text);
		if (matcher_refund.find()) {
			PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
			penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent("-1"));
			penaltyCancelItemList.add(penaltyCancelItem);
		}
		if (matcher_change.find()) {
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
			PenaltyCondition penaltyCondition = new PenaltyCondition();
			penaltyCondition.setPenaltyConditionType(PenaltyConditionTypeEnum.BEFORE_DEPT_TIME);
			penaltyCondition.setBeforeDeptHour(24*Integer.parseInt(matcher_change.group(1)));
			penaltyChangeItem.setPenaltyCondition(penaltyCondition);
			penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(matcher_change.group("penalty")));
			penaltyChangeItemList.add(penaltyChangeItem);
		}
/*		if (matcher_reroute.find()) {
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.REROUTE);
			penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent("-1"));
			penaltyChangeItemList.add(penaltyChangeItem);
		}
*/		if (matcher_noshow.find()) {
			PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.NOSHOW);
			penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent("-1"));
			penaltyCancelItemList.add(penaltyCancelItem);
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.NOSHOW);
			penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent("-1"));
			penaltyChangeItemList.add(penaltyChangeItem);
		}
		
		penaltiesItem.setPenaltyCancelItemList(penaltyCancelItemList);
		penaltiesItem.setPenaltyChangeItemList(penaltyChangeItemList);
		return penaltiesItem;
	}
}
