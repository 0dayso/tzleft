/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */
 
package com.travelzen.farerule.cpecker.pecker;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.travelzen.farerule.Penalties;
import com.travelzen.farerule.condition.RuleCondition;
import com.travelzen.farerule.jpecker.struct.PenaltyNoshowPack;
import com.travelzen.farerule.jpecker.struct.RuleTextSegment;
import com.travelzen.farerule.cpecker.tool.ConditionTransducer;
import com.travelzen.farerule.rule.PenaltiesItem;
import com.travelzen.farerule.rule.PenaltyCancelItem;
import com.travelzen.farerule.rule.PenaltyChangeItem;

public class Cpecker16 extends CpeckerBase {

	private Penalties penalties;
	private List<PenaltiesItem> penaltiesItemList;
	
	public Cpecker16() {
		this.airCompany = "";
		penalties = new Penalties();
		penaltiesItemList = new ArrayList<PenaltiesItem>();
	}
	
	public Cpecker16(String airCompany) {
		this.airCompany = airCompany;
		penalties = new Penalties();
		penaltiesItemList = new ArrayList<PenaltiesItem>();
	}
	
	public Penalties getPenalties() {
		return penalties;
	}
	
	public void parse(String text1, String text2, String text3) {
		if (airCompany.equals("TG")) {
			Pattern pattern = Pattern.compile("泰国境外段-([\\w\\W]+)$");
			Matcher matcher1 = pattern.matcher(text1);
			Matcher matcher2 = pattern.matcher(text2);
			Matcher matcher3 = pattern.matcher(text3);
			if (matcher1.find())
				text1 = matcher1.group(1);
			if (matcher2.find())
				text2 = matcher2.group(1);
			if (matcher3.find())
				text3 = matcher3.group(1);
		}
		if (airCompany.equals("VX")) {
			Pattern pattern = Pattern.compile("02岁以下无座婴儿允许。([\\w\\W]+)$");
			Matcher matcher1 = pattern.matcher(text1);
			Matcher matcher2 = pattern.matcher(text2);
			Matcher matcher3 = pattern.matcher(text3);
			if (matcher1.find())
				text1 = matcher1.group(1);
			if (matcher2.find())
				text2 = matcher2.group(1);
			if (matcher3.find())
				text3 = matcher3.group(1);
		}
		
		List<RuleTextSegment> ruleTextSegmentList1 = new ArrayList<RuleTextSegment>();
		List<RuleTextSegment> ruleTextSegmentList2 = new ArrayList<RuleTextSegment>();
		List<RuleTextSegment> ruleTextSegmentList3 = new ArrayList<RuleTextSegment>();
		
		splitOrigins(text1);
		splitDates(ruleTextBlockList);
		ruleTextSegmentList1.addAll(ruleTextSegmentList);
		ruleTextBlockList.clear();
		ruleTextSegmentList.clear();
		splitOrigins(text2);
		splitDates(ruleTextBlockList);
		ruleTextSegmentList2.addAll(ruleTextSegmentList);
		ruleTextBlockList.clear();
		ruleTextSegmentList.clear();
		splitOrigins(text3);
		splitDates(ruleTextBlockList);
		ruleTextSegmentList3.addAll(ruleTextSegmentList);
		
		mergeAndParse(ruleTextSegmentList1, ruleTextSegmentList2, ruleTextSegmentList3);
		penalties.setPenaltiesItemList(penaltiesItemList);
	}
	
	public void mergeAndParse(List<RuleTextSegment> ruleTextSegmentList1, 
			List<RuleTextSegment> ruleTextSegmentList2, List<RuleTextSegment> ruleTextSegmentList3) {
		List<RuleTextSegment> baseSegmentList = 
				ruleTextSegmentList1.size()>ruleTextSegmentList2.size()?ruleTextSegmentList1:ruleTextSegmentList2;
		for (RuleTextSegment baseSegment:baseSegmentList) {
			String origin = baseSegment.getOrigin();
			String salesDate = baseSegment.getSalesDate();
			String travelDate = baseSegment.getTravelDate();
			// Noshow start
			PenaltyNoshowPack penaltyNoshowPack = new PenaltyNoshowPack();
			boolean isFound_noshow = false;
			PenaltyCancelItem penaltyCancelItem_noshow = null;
			PenaltyChangeItem penaltyChangeItem_noshow = null;
			for (RuleTextSegment ruleTextSegment3:ruleTextSegmentList3) {
				if (ruleTextSegment3.getOrigin().equals(origin) && 
						ruleTextSegment3.getSalesDate().equals(salesDate) && 
						ruleTextSegment3.getTravelDate().equals(travelDate)) {
					penaltyNoshowPack = Cpecker16Util.parsePenaltyNoshow(ruleTextSegment3.getText());
					penaltyCancelItem_noshow = penaltyNoshowPack.getCancelNoshow();
					penaltyChangeItem_noshow = penaltyNoshowPack.getChangeNoshow();
					isFound_noshow = true;
					break;
				}
			}
			if (isFound_noshow == false) {
				for (RuleTextSegment ruleTextSegment3:ruleTextSegmentList3) {
					if (ruleTextSegment3.getOrigin().equals(origin) && 
							ruleTextSegment3.getSalesDate().equals("") && 
							ruleTextSegment3.getTravelDate().equals("")) {
						penaltyNoshowPack = Cpecker16Util.parsePenaltyNoshow(ruleTextSegment3.getText());
						penaltyCancelItem_noshow = penaltyNoshowPack.getCancelNoshow();
						penaltyChangeItem_noshow = penaltyNoshowPack.getChangeNoshow();
						isFound_noshow = true;
						break;
					}
				}
			}
			if (isFound_noshow == false) {
				for (RuleTextSegment ruleTextSegment3:ruleTextSegmentList3) {
					if (ruleTextSegment3.getOrigin().equals("") && 
							ruleTextSegment3.getSalesDate().equals("") && 
							ruleTextSegment3.getTravelDate().equals("")) {
						penaltyNoshowPack = Cpecker16Util.parsePenaltyNoshow(ruleTextSegment3.getText());
						penaltyCancelItem_noshow = penaltyNoshowPack.getCancelNoshow();
						penaltyChangeItem_noshow = penaltyNoshowPack.getChangeNoshow();
						isFound_noshow = true;
						break;
					}
				}
			}
			// Cancel start
			List<PenaltyCancelItem> penaltyCancelItemList = new ArrayList<PenaltyCancelItem>();
			boolean isFound_cancel = false;
			for (RuleTextSegment ruleTextSegment1:ruleTextSegmentList1) {
				if (ruleTextSegment1.getOrigin().equals(origin) && 
						ruleTextSegment1.getSalesDate().equals(salesDate) && 
						ruleTextSegment1.getTravelDate().equals(travelDate)) {
					penaltyCancelItemList.addAll(Cpecker16Util.parsePenaltyCancel(ruleTextSegment1.getText(), penaltyCancelItem_noshow));
					isFound_cancel = true;
					break;
				}
			}
			if (isFound_cancel == false) {
				for (RuleTextSegment ruleTextSegment1:ruleTextSegmentList1) {
					if (ruleTextSegment1.getOrigin().equals(origin) && 
							ruleTextSegment1.getSalesDate().equals("") && 
							ruleTextSegment1.getTravelDate().equals("")) {
						penaltyCancelItemList.addAll(Cpecker16Util.parsePenaltyCancel(ruleTextSegment1.getText(), penaltyCancelItem_noshow));
						isFound_cancel = true;
						break;
					}
				}
			}
			if (isFound_cancel == false) {
				for (RuleTextSegment ruleTextSegment1:ruleTextSegmentList1) {
					if (ruleTextSegment1.getOrigin().equals("") && 
							ruleTextSegment1.getSalesDate().equals("") && 
							ruleTextSegment1.getTravelDate().equals("")) {
						penaltyCancelItemList.addAll(Cpecker16Util.parsePenaltyCancel(ruleTextSegment1.getText(), penaltyCancelItem_noshow));
						isFound_cancel = true;
						break;
					}
				}
			}
			// Change start
			List<PenaltyChangeItem> penaltyChangeItemList = new ArrayList<PenaltyChangeItem>();
			boolean isFound_change = false;
			for (RuleTextSegment ruleTextSegment2:ruleTextSegmentList2) {
				if (ruleTextSegment2.getOrigin().equals(origin) && 
						ruleTextSegment2.getSalesDate().equals(salesDate) && 
						ruleTextSegment2.getTravelDate().equals(travelDate)) {
					penaltyChangeItemList.addAll(Cpecker16Util.parsePenaltyChange(ruleTextSegment2.getText(), penaltyChangeItem_noshow));
					isFound_change = true;
					break;
				}
			}
			if (isFound_change == false) {
				for (RuleTextSegment ruleTextSegment2:ruleTextSegmentList2) {
					if (ruleTextSegment2.getOrigin().equals(origin) && 
							ruleTextSegment2.getSalesDate().equals("") && 
							ruleTextSegment2.getTravelDate().equals("")) {
						penaltyChangeItemList.addAll(Cpecker16Util.parsePenaltyChange(ruleTextSegment2.getText(), penaltyChangeItem_noshow));
						isFound_change = true;
						break;
					}
				}
			}
			if (isFound_change == false) {
				for (RuleTextSegment ruleTextSegment2:ruleTextSegmentList2) {
					if (ruleTextSegment2.getOrigin().equals("") && 
							ruleTextSegment2.getSalesDate().equals("") && 
							ruleTextSegment2.getTravelDate().equals("")) {
						penaltyChangeItemList.addAll(Cpecker16Util.parsePenaltyChange(ruleTextSegment2.getText(), penaltyChangeItem_noshow));
						isFound_change = true;
						break;
					}
				}
			}
			
			RuleCondition ruleCondition = new RuleCondition();
			if (!origin.equals(""))
				ruleCondition.setOriginCondition(ConditionTransducer.parseOriginCn(origin));
			if (!salesDate.equals(""))
				ruleCondition.setSalesDateCondition(ConditionTransducer.parseSalesDateCn(salesDate));
			if (!travelDate.equals(""))
				ruleCondition.setTravelDateCondition(ConditionTransducer.parseTravelDateCn(travelDate));
			PenaltiesItem penaltiesItem = new PenaltiesItem();
			penaltiesItem.setRuleCondition(ruleCondition);
			penaltiesItem.setPenaltyCancelItemList(penaltyCancelItemList);
			penaltiesItem.setPenaltyChangeItemList(penaltyChangeItemList);
			penaltiesItemList.add(penaltiesItem);
		}
	}
}
