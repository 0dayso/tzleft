/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */
 
package com.travelzen.farerule.jpecker.pecker.pecker16;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.travelzen.farerule.Penalties;
import com.travelzen.farerule.condition.RuleCondition;
import com.travelzen.farerule.condition.SalesDateSubItem;
import com.travelzen.farerule.condition.TravelDateSubItem;
import com.travelzen.farerule.jpecker.consts.PenaltyConst;
import com.travelzen.farerule.jpecker.struct.PenaltyNoshowPack;
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
import com.travelzen.farerule.rule.PenaltyTypeEnum;

public class Jpecker16__Impl {
	
	private String airCompany;
	private Penalties penalties;
	private List<PenaltiesItem> penaltiesItemList;
	
	public Jpecker16__Impl() {
		airCompany = "";
		penalties = new Penalties();
		penaltiesItemList = new ArrayList<PenaltiesItem>();
	}
	
	public Jpecker16__Impl(String airCompany) {
		this.airCompany = airCompany;
		penalties = new Penalties();
		penaltiesItemList = new ArrayList<PenaltiesItem>();
	}
	
	public Penalties getPenalties() {
		return penalties;
	}
	
	public void process(List<RuleTextSegment> ruleTextSegmentList) {
		for (RuleTextSegment ruleTextSegment:ruleTextSegmentList) {
			String text = ruleTextSegment.getText();
			// rule out Noisy text
			text = Jpecker16__Util.rmNoise(text);
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
	
	public PenaltiesItem parseOnePenaltiesItem(String text) {
		PenaltiesItem penaltiesItem = parsePenaltiesItem(text);
		return penaltiesItem;
	}
	
	private PenaltiesItem parsePenaltiesItem(String ruleText) {
		PenaltiesItem penaltiesItem = new PenaltiesItem();
		
		// CNY 420/USD 70 --> CNY 420 which may result in mistakes
//		ruleText = ruleText.replaceAll("/[A-Z]{3} ?\\d+\\b", "");
		
		StringBuilder cancelText = new StringBuilder(), 
				changeText = new StringBuilder(), 
				noshowText = new StringBuilder(),
				bigCancelText = new StringBuilder(), 
				bigChangeText = new StringBuilder();
		// get Cancel text
		Matcher matcher_cancel = Pattern.compile("((?<!/|FOR)\n|^)[ -]*" + PenaltyConst.CANCEL + "[ -]*(?:ANY TIME)?\n" 
			+ PenaltyConst.CONTENT + PenaltyConst.ENDTAG).matcher(ruleText);
		while (matcher_cancel.find()) {
			cancelText.append(matcher_cancel.group("content")).append("\n#####\n");
		}				
		// get Change text
		Matcher matcher_change = Pattern.compile("((?<!/|FOR)\n|^)[ -]*" + PenaltyConst.CHANGE + "[ -]*(?:ANY TIME)?\n" 
			+ PenaltyConst.CONTENT + PenaltyConst.ENDTAG).matcher(ruleText);
		while (matcher_change.find()) {
			changeText.append(matcher_change.group("content")).append("\n#####\n");
		}
		// get Noshow text
		Matcher matcher_noshow = Pattern.compile("((?<!/|FOR)\n|^)[ -]*" + PenaltyConst.NOSHOW + "(?: FEE)?[ -]*\n" 
			+ PenaltyConst.CONTENT + PenaltyConst.ENDTAG).matcher(ruleText);
		while (matcher_noshow.find()) {
			noshowText.append(matcher_noshow.group("content")).append("\n#####\n");
		}
		// get AND text
		Matcher matcher_and = Pattern.compile("((?<!/)\n|^)[ -]*" + PenaltyConst.BOTH + "[ -]*\n" 
			+ PenaltyConst.CONTENT + PenaltyConst.ENDTAG).matcher(ruleText);
		while (matcher_and.find()) {
			cancelText.append(matcher_and.group("content")).append("\n#####\n");
			changeText.append(matcher_and.group("content")).append("\n#####\n");
		}
		// get Big Cancel text for Noshow parser
		Matcher matcher_cancel_big = Pattern.compile("((?<!/|FOR)\n|^)[ -]*" + PenaltyConst.CANCEL + "[ -]*(?:ANY TIME)?\n" 
				+ PenaltyConst.CONTENT + PenaltyConst.ENDTAG_BIG).matcher(ruleText);
		while (matcher_cancel_big.find()) {
			bigCancelText.append(matcher_cancel_big.group("content")).append("\n#####\n");
		}				
		// get Big Change text for Noshow parser
		Matcher matcher_change_big = Pattern.compile("((?<!/|FOR)\n|^)[ -]*" + PenaltyConst.CHANGE + "[ -]*(?:ANY TIME)?\n" 
			+ PenaltyConst.CONTENT + PenaltyConst.ENDTAG_BIG).matcher(ruleText);
		while (matcher_change_big.find()) {
			bigChangeText.append(matcher_change_big.group("content")).append("\n#####\n");
		}
		
		PenaltyNoshowPack noshowPack = parseNoshowPack(
				bigCancelText.toString(), bigChangeText.toString(), noshowText.toString(), ruleText);
		PenaltyCancelItem penaltyCancelItem_noshow = noshowPack.getCancelNoshow();
		PenaltyChangeItem penaltyChangeItem_noshow = noshowPack.getChangeNoshow();
		
		List<PenaltyCancelItem> penaltyCancelItemList = parsePenaltyCancelItemList(
				cancelText.toString(), ruleText, penaltyCancelItem_noshow);
		List<PenaltyChangeItem> penaltyChangeItemList = parsePenaltyChangeItemList(
				changeText.toString(), ruleText, penaltyChangeItem_noshow);
		
		penaltiesItem.setPenaltyCancelItemList(penaltyCancelItemList);
		penaltiesItem.setPenaltyChangeItemList(penaltyChangeItemList);
		return penaltiesItem;
	}
	
	// parse PenaltyCancel
	private List<PenaltyCancelItem> parsePenaltyCancelItemList(
			String cancelText, String ruleText, PenaltyCancelItem penaltyCancelItem_noshow) {
		List<PenaltyCancelItem> penaltyCancelItemList = new ArrayList<PenaltyCancelItem>();
		PenaltyCancelItem penaltyCancelItem;
		if (cancelText.length() == 0) {
			String refund = Jpecker16__Util.sortTwoPenalties(
					Jpecker16__Util.parseRefund(airCompany, ruleText), Jpecker16__Util.parseAnd(ruleText));
			if (!refund.equals("")) {
				penaltyCancelItem = new PenaltyCancelItem();
				penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
				penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(refund));
				penaltyCancelItemList.add(penaltyCancelItem);
			}
		} else {
			String bd = "", ad = "";
			// For MU airline
			if (airCompany.equals("MU")) {
				Matcher matcher_mu_bd = Pattern.compile(PenaltyConst.PENALTIES 
						+ "(?:\\sOR)?(?:\\sOR\\sEQUIVALENT)?(?:\\sFOR\\sREFUND)?\\sBEF(?:ORE)?\\s(?:DEPARTURE|DEP(?:T)?)").matcher(cancelText);
				Matcher matcher_mu_ad = Pattern.compile("(?:NOT PERMIT|" + PenaltyConst.PENALTIES + ")" 
						+ "\\sAFTER\\s(?:DEPARTURE|DEP(?:T)?)").matcher(cancelText);
				if (matcher_mu_bd.find() && matcher_mu_ad.find()) {
					bd = Jpecker16__Util.parsePenalties(matcher_mu_bd.group(0));
					if (matcher_mu_ad.group(0).contains("NOT PERMIT"))
						ad = "-1";
					else
						ad = Jpecker16__Util.parsePenalties(matcher_mu_ad.group(0));
				}
			}
			// For MH airline
			else if (airCompany.equals("MH")) {
				Matcher matcher_mh = Pattern.compile(
						"REFUND - PAY (\\d+) ?PCT OF THE FARE").matcher(ruleText);
				if (matcher_mh.find()) {
					bd = ad = matcher_mh.group(1)+"%";
				}
			}
			
			if (bd.equals("") && ad.equals("")) {
				penaltyCancelItemList = parseCancelNormal(cancelText, ruleText);
			} else {
				if (bd.equals(ad)) {
					penaltyCancelItem = new PenaltyCancelItem();
					penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
					penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(bd));
					penaltyCancelItemList.add(penaltyCancelItem);
				} else {
					if (!bd.equals("")) {
						penaltyCancelItem = new PenaltyCancelItem();
						penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
						PenaltyCondition penaltyCondition = new PenaltyCondition();
						penaltyCondition.setPenaltyConditionType(PenaltyConditionTypeEnum.BEFORE_DEPT);
						penaltyCancelItem.setPenaltyCondition(penaltyCondition);
						penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(bd));
						penaltyCancelItemList.add(penaltyCancelItem);
					}
					if (!ad.equals("")) {
						penaltyCancelItem = new PenaltyCancelItem();
						penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
						PenaltyCondition penaltyCondition = new PenaltyCondition();
						penaltyCondition.setPenaltyConditionType(PenaltyConditionTypeEnum.AFTER_DEPT);
						penaltyCancelItem.setPenaltyCondition(penaltyCondition);
						penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(ad));
						penaltyCancelItemList.add(penaltyCancelItem);
					}
				}
			}
		}
		
		if (penaltyCancelItem_noshow != null)
			// No noshow penalty when cancel is not permitted
			if (!(penaltyCancelItemList.size() == 1 && 
					penaltyCancelItemList.get(0).getPenaltyContent().getPenaltyType() == PenaltyTypeEnum.NOT_PERMIT))
				penaltyCancelItemList.add(penaltyCancelItem_noshow);
		
		return penaltyCancelItemList;
	}
	
	// parse PenaltyChange
	private List<PenaltyChangeItem> parsePenaltyChangeItemList(
			String changeText, String ruleText, PenaltyChangeItem penaltyChangeItem_noshow) {
		List<PenaltyChangeItem> penaltyChangeItemList = new ArrayList<PenaltyChangeItem>();
		PenaltyChangeItem penaltyChangeItem;
		if (changeText.length() == 0) {
			String revalidation = Jpecker16__Util.sortTwoPenalties(
					Jpecker16__Util.parseRevalidation(airCompany, ruleText), Jpecker16__Util.parseAnd(ruleText));
			if (!revalidation.equals("")) {
				penaltyChangeItem = new PenaltyChangeItem();
				penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
				penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(revalidation));
				penaltyChangeItemList.add(penaltyChangeItem);
			}
		} else {
			String bd = "", ad = "";
			// For 9W airline
			if (airCompany.equals("9W")) {
				Matcher matcher_9w = Pattern.compile(
						"ANY TIME\nCHARGE " + PenaltyConst.PENALTY1).matcher(changeText);
				if (matcher_9w.find()) {
					bd = ad = matcher_9w.group(1);
				}
			}
			// For HX airline
			else if (airCompany.equals("HX")) {
				Matcher matcher_hx1 = Pattern.compile(
						"PERMITTED AND SERVICE FEE " + PenaltyConst.PENALTY1).matcher(changeText);
				Matcher matcher_hx2 = Pattern.compile(
						"^\\s*[A-Z]\\.PERMITTED\\.\n[A-Z]\\.").matcher(changeText);
				if (matcher_hx1.find()) {
					bd = ad = matcher_hx1.group(1);
				} else if (matcher_hx2.find()) {
					bd = ad = "0";
				}
			}
			
			if (bd.equals("") && ad.equals("")) {
				penaltyChangeItemList = parseChangeNormal(changeText.toString(), ruleText);
			} else {
				if (bd.equals(ad)) {
					penaltyChangeItem = new PenaltyChangeItem();
					penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
					penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(bd));
					penaltyChangeItemList.add(penaltyChangeItem);
				} else {
					if (!bd.equals("")) {
						penaltyChangeItem = new PenaltyChangeItem();
						penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
						PenaltyCondition penaltyCondition = new PenaltyCondition();
						penaltyCondition.setPenaltyConditionType(PenaltyConditionTypeEnum.BEFORE_DEPT);
						penaltyChangeItem.setPenaltyCondition(penaltyCondition);
						penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(bd));
						penaltyChangeItemList.add(penaltyChangeItem);
					}
					if (!ad.equals("")) {
						penaltyChangeItem = new PenaltyChangeItem();
						penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
						PenaltyCondition penaltyCondition = new PenaltyCondition();
						penaltyCondition.setPenaltyConditionType(PenaltyConditionTypeEnum.AFTER_DEPT);
						penaltyChangeItem.setPenaltyCondition(penaltyCondition);
						penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(ad));
						penaltyChangeItemList.add(penaltyChangeItem);
					}
				}
			}
		}
		
		if (penaltyChangeItem_noshow != null) {
			// No noshow penalty when change is not permitted
			if (!(penaltyChangeItemList.size() == 1 && 
					penaltyChangeItemList.get(0).getPenaltyContent().getPenaltyType() == PenaltyTypeEnum.NOT_PERMIT))
				penaltyChangeItemList.add(penaltyChangeItem_noshow);
		}
		
		String reissue = Jpecker16__Util.parseReissue(airCompany, ruleText);
		if (!reissue.equals("")) {
			penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.REISSUE);
			penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(reissue));
			penaltyChangeItemList.add(penaltyChangeItem);
		}
/*		String reroute = Jpecker16__Util.parseReroute(airCompany, ruleText);
		if (!reroute.equals("")) {
			penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.REROUTE);
			penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(reroute));
			penaltyChangeItemList.add(penaltyChangeItem);
		}
		String endorse = Jpecker16__Util.parseEndorse(airCompany, ruleText);
		if (!endorse.equals("")) {
			penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.ENDORSE);
			penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(endorse));
			penaltyChangeItemList.add(penaltyChangeItem);
		}
*/		
		return penaltyChangeItemList;
	}
	
	private PenaltyNoshowPack parseNoshowPack(String cancelText, String changeText, String noshowText, String ruleText) {
		// parse PenaltyNoshow
		List<String> noshowCancelList = new ArrayList<String>();
		List<String> noshowChangeList = new ArrayList<String>();
		
		// For CX airline
		if (airCompany.equals("CX") || airCompany.equals("KA")) {
			Matcher matcher_cx_noshow_cancel = Pattern.compile(
					PenaltyConst.cancel + "\\sFEE\\s" + PenaltyConst.PENALTIES_N
					+ "\\sPLUS\\sNO[-\\s]SHOW\\sFEE\\s" + PenaltyConst.PENALTIES_SIM).matcher(ruleText);
			Matcher matcher_cx_noshow_change = Pattern.compile(
					PenaltyConst.change + "\\sFEE\\s" + PenaltyConst.PENALTIES_N
					+ "\\sPLUS\\sNO[-\\s]SHOW\\sFEE\\s" + PenaltyConst.PENALTIES_SIM).matcher(ruleText);
			if (matcher_cx_noshow_cancel.find()) {
				String tmpNoshowCancel = Jpecker16__Util.parsePenalties(matcher_cx_noshow_cancel.group("penalty"));
				noshowCancelList.add(tmpNoshowCancel);
			}
			if (matcher_cx_noshow_change.find()) {
				String tmpNoshowChange = Jpecker16__Util.parsePenalties(matcher_cx_noshow_change.group("penalty"));
				noshowChangeList.add(tmpNoshowChange);
			}
		}
		// For MH airline
		else if (airCompany.equals("MH")) {
			Matcher matcher_mh_noshow1 = Pattern.compile(
					"NO SHOW - PAY (\\d+) ?PCT OF THE FARE").matcher(ruleText);
			Matcher matcher_mh_noshow2 = Pattern.compile(
					"IN CASE OF NO-SHOW\nCONSIDERED AS USED").matcher(ruleText);
			Matcher matcher_mh_noshow3 = Pattern.compile(
					"\nCHARGE ([A-Z]{3} \\d+) FOR NO-SHOW\\.").matcher(ruleText);
			if (matcher_mh_noshow1.find()) {
				noshowCancelList.add(matcher_mh_noshow1.group(1)+"%");
				noshowChangeList.add(matcher_mh_noshow1.group(1)+"%");
			} else if (matcher_mh_noshow2.find()) {
				noshowCancelList.add("-1");
				noshowChangeList.add("-1");
			} else if (matcher_mh_noshow3.find()) {
				noshowCancelList.add(matcher_mh_noshow3.group(1));
				noshowChangeList.add(matcher_mh_noshow3.group(1));
			}
		}
		// UO airline
		else if (airCompany.equals("UO")) {
			Matcher matcher_uo = Pattern.compile(
					"NO SHOW OR FAILURE TO CHECK IN/BOARD ON TIME WILL\n"
					+ "RESULT IN FORFEITURE OF THE FARE").matcher(ruleText);
			if (matcher_uo.find()) {
				noshowCancelList.add("-1");
				noshowChangeList.add("-1");
			}
		}
		// IB airline
		else if (airCompany.equals("IB")) {
			Matcher matcher_ib_cancel = Pattern.compile(
					"IN CASE OF NO[ -]?SHOW\\.?([\\w\\W]+?)\\.").matcher(cancelText);
			Matcher matcher_ib_change = Pattern.compile(
					"IN CASE OF NO[ -]?SHOW\\.?([\\w\\W]+?)\\.").matcher(changeText);
			if (matcher_ib_cancel.find()) {
				noshowCancelList.add(Jpecker16__Util.parsePenalties(matcher_ib_cancel.group(1)));
			}
			if (matcher_ib_change.find()) {
				noshowChangeList.add(Jpecker16__Util.parsePenalties(matcher_ib_change.group(1)));
			}
		}
		// 9W airline
		else if (airCompany.equals("9W")) {
			Matcher matcher_9w_change = Pattern.compile(
					"CHANGES NOT PERMITTED IN CASE OF NO").matcher(ruleText);
			if (matcher_9w_change.find()) {
				noshowChangeList.add("-1");
			}
		}
		
		String tmpCancelNoshow, tmpChangeNoshow;
		if (!(tmpCancelNoshow = Jpecker16__Util.parseNoshow(airCompany, cancelText)).equals(""))
			noshowCancelList.add(tmpCancelNoshow);
		if (!(tmpChangeNoshow = Jpecker16__Util.parseNoshow(airCompany, changeText)).equals(""))
			noshowChangeList.add(tmpChangeNoshow);
		if (noshowText.length() != 0) {
			// Undecided here ?!
			String tmpNoshowText = noshowText.toString();
			if (tmpNoshowText.contains("---"))
				tmpNoshowText = tmpNoshowText.substring(0,tmpNoshowText.indexOf("---"));
			if (!(tmpCancelNoshow = Jpecker16__Util.parsePenalties(tmpNoshowText)).equals(""))
				noshowCancelList.add(Jpecker16__Util.parsePenalties(tmpNoshowText));
			if (!(tmpChangeNoshow = Jpecker16__Util.parsePenalties(tmpNoshowText)).equals(""))
				noshowChangeList.add(Jpecker16__Util.parsePenalties(tmpNoshowText));
		}
		
		PenaltyNoshowPack noshowPack = new PenaltyNoshowPack();
		if (noshowCancelList.size() != 0) {
			PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.NOSHOW);
			penaltyCancelItem.setPenaltyContent(
					PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.sortPenalties(noshowCancelList)));
			noshowPack.setCancelNoshow(penaltyCancelItem);
		}
		if (noshowChangeList.size() != 0) {
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.NOSHOW);
			penaltyChangeItem.setPenaltyContent(
					PenaltyTransducer.parsePenaltyContent(Jpecker16__Util.sortPenalties(noshowChangeList)));
			noshowPack.setChangeNoshow(penaltyChangeItem);
		}
		
		return noshowPack;
	}
	
	public List<PenaltyCancelItem> parseCancelNormal(String cancelText, String ruleText) {
		List<PenaltyCancelItem> penaltyCancelItemList = new ArrayList<PenaltyCancelItem>();
		List<String> textList = parseIfUsed(cancelText);
		if (textList.size() == 1) {
			String text = textList.get(0);
			Map<String,String> textMap = parseBdAd(text);
			penaltyCancelItemList = parseCancelNormalDetail(textMap, ruleText, null);
		} else if (textList.size() == 2) {
			String unused = textList.get(0);
			Map<String,String> unusedTextMap = parseBdAd(unused);
			penaltyCancelItemList.addAll(parseCancelNormalDetail(unusedTextMap, ruleText, false));
			String used = textList.get(1);
			Map<String,String> usedTextMap = parseBdAd(used);
			penaltyCancelItemList.addAll(parseCancelNormalDetail(usedTextMap, ruleText, true));
		}
		return penaltyCancelItemList;
	}
	
	private List<PenaltyCancelItem> parseCancelNormalDetail(Map<String,String> resultMap, String ruleText, Boolean ifUsed) {
		List<PenaltyCancelItem> penaltyCancelItemList = new ArrayList<PenaltyCancelItem>();
		
		String bd = "", ad = "";	
		String bd_text_purged = resultMap.get("bd_purged");
		String ad_text_purged = resultMap.get("ad_purged");
		
		Pattern pattern1 = Pattern.compile(
				PenaltyConst.PENALTIES + "\\s*(?:FOR|IN\\s*CASE\\s*OF)\\s*(?:[\\w\\W]{1,15}/)*\\s*" + PenaltyConst.cancel);
		Pattern pattern2 = Pattern.compile(
				"(?:\n|^)\\s*" + PenaltyConst.cancel + "\\s" + PenaltyConst.PENALTY4 + "\\.?(?:\n|$)");
		Pattern pattern3 = Pattern.compile(
				PenaltyConst.PENALTIES + "(?!\\s*(?:FOR|IN\\s*CASE\\s*OF)\\s*(?:NO[\\s-]*SHOW|LOST\\s*TICKET|CHANGE(?:S)?|REBOOKING(?:S)?|REVALIDATION(?:S)?|REISSUE|REROUT(?:E|ING)))");
		
		Matcher matcher_bd1 = pattern1.matcher(bd_text_purged);
		Matcher matcher_bd2 = pattern2.matcher(bd_text_purged);
		Matcher matcher_bd3 = pattern3.matcher(bd_text_purged);

		List<String> tmpBdList = new ArrayList<String>();
		while (matcher_bd1.find())
			tmpBdList.add(Jpecker16__Util.parsePenalties(matcher_bd1.group("penalty")));
		if (matcher_bd2.find())
			tmpBdList.add("0");
		if(tmpBdList.size() == 0) {
			while (matcher_bd3.find())
				tmpBdList.add(Jpecker16__Util.parsePenalties(matcher_bd3.group("penalty")));
		}
		bd = Jpecker16__Util.sortPenalties(tmpBdList);
		
		Matcher matcher_ad1 = pattern1.matcher(ad_text_purged);
		Matcher matcher_ad2 = pattern2.matcher(ad_text_purged);
		Matcher matcher_ad3 = pattern3.matcher(ad_text_purged);

		List<String> tmpAdList = new ArrayList<String>();
		while (matcher_ad1.find())
			tmpAdList.add(Jpecker16__Util.parsePenalties(matcher_ad1.group("penalty")));
		if (matcher_ad2.find())
			tmpAdList.add("0");
		if(tmpAdList.size() == 0) {
			while (matcher_ad3.find())
				tmpAdList.add(Jpecker16__Util.parsePenalties(matcher_ad3.group("penalty")));
		}
		ad = Jpecker16__Util.sortPenalties(tmpAdList);
		
		if (bd.equals("0"))
			bd = Jpecker16__Util.sortTwoPenalties(bd, Jpecker16__Util.parseRefund(airCompany, resultMap.get("bd")));
		if (ad.equals("0"))
			ad = Jpecker16__Util.sortTwoPenalties(ad, Jpecker16__Util.parseRefund(airCompany, resultMap.get("ad")));
		String tmpRefund = Jpecker16__Util.parseRefund(airCompany, ruleText);
		if (bd == "" && ad == "" && tmpRefund != "") {
			bd = ad = tmpRefund;
		}
		
		if (bd.equals("") && ad.equals(""))
			return penaltyCancelItemList;
		PenaltyCancelItem penaltyCancelItem;
		if (bd.equals(ad)) {
			penaltyCancelItem = new PenaltyCancelItem();
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
			if (ifUsed != null)
				penaltyCancelItem.setUsed(ifUsed);
			penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(bd));
			penaltyCancelItemList.add(penaltyCancelItem);
		} else {
			if (!bd.equals("")) {
				penaltyCancelItem = new PenaltyCancelItem();
				penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
				if (ifUsed != null)
					penaltyCancelItem.setUsed(ifUsed);
				PenaltyCondition penaltyCondition = new PenaltyCondition();
				penaltyCondition.setPenaltyConditionType(PenaltyConditionTypeEnum.BEFORE_DEPT);
				penaltyCancelItem.setPenaltyCondition(penaltyCondition);
				penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(bd));
				penaltyCancelItemList.add(penaltyCancelItem);
			}
			if (!ad.equals("")) {
				penaltyCancelItem = new PenaltyCancelItem();
				penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
				if (ifUsed != null)
					penaltyCancelItem.setUsed(ifUsed);
				PenaltyCondition penaltyCondition = new PenaltyCondition();
				penaltyCondition.setPenaltyConditionType(PenaltyConditionTypeEnum.AFTER_DEPT);
				penaltyCancelItem.setPenaltyCondition(penaltyCondition);
				penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(ad));
				penaltyCancelItemList.add(penaltyCancelItem);
			}
		}
		
		return penaltyCancelItemList;
	}
	
	public List<PenaltyChangeItem> parseChangeNormal(String changeText, String ruleText) {
		List<PenaltyChangeItem> penaltyChangeItemList = new ArrayList<PenaltyChangeItem>();
		List<String> textList = parseIfUsed(changeText);
		if (textList.size() == 1) {
			String text = textList.get(0);
			Map<String,String> textMap = parseBdAd(text);
			penaltyChangeItemList = parseChangeNormalDetail(textMap, ruleText, null);
		} else if (textList.size() == 2) {
			String unused = textList.get(0);
			Map<String,String> unusedTextMap = parseBdAd(unused);
			penaltyChangeItemList.addAll(parseChangeNormalDetail(unusedTextMap, ruleText, false));
			String used = textList.get(1);
			Map<String,String> usedTextMap = parseBdAd(used);
			penaltyChangeItemList.addAll(parseChangeNormalDetail(usedTextMap, ruleText, true));
		}
		return penaltyChangeItemList;
	}
	
	private List<PenaltyChangeItem> parseChangeNormalDetail(Map<String,String> resultMap, String ruleText, Boolean ifUsed) {
		List<PenaltyChangeItem> penaltyChangeItemList = new ArrayList<PenaltyChangeItem>();
		
		String bd = "", ad = "";
		
		String bd_text_purged = resultMap.get("bd_purged");
		String ad_text_purged = resultMap.get("ad_purged");
		
		Pattern pattern1 = Pattern.compile(PenaltyConst.PENALTIES + "\\s*(?:FOR|IN\\s*CASE\\s*OF)\\s*(?:[\\w\\W]{1,15}/)*\\s*" + PenaltyConst.change);
		Pattern pattern2 = Pattern.compile("^(?: *ANY TIME\\s)?(?: *" + PenaltyConst.change + ")?\\s*(?:CHARGE *)?" + PenaltyConst.PENALTIES 
				+ "(?!\\s(?:FOR|IN\\sCASE\\sOF)\\s(?:REISSUE|REROUT(?:E|ING)|NO[\\s-]*SHOW|LOST\\sTICKET|CANCEL|CANCELLATION(?:S)?|REFUND(?:S)?))");
		Pattern pattern3 = Pattern.compile(PenaltyConst.PENALTIES 
				+ "(?!\\s(?:FOR|IN\\sCASE\\sOF)\\s(?:REISSUE|REROUT(?:E|ING)|NO[\\s-]*SHOW|LOST\\sTICKET|CANCEL|CANCELLATION(?:S)?|REFUND(?:S)?))");
		
		Matcher matcher_bd1 = pattern1.matcher(bd_text_purged);
		Matcher matcher_bd2 = pattern2.matcher(bd_text_purged);
		Matcher matcher_bd3 = pattern3.matcher(bd_text_purged);
		
		List<String> tmpBdList = new ArrayList<String>();
		while (matcher_bd1.find())
			tmpBdList.add(Jpecker16__Util.parsePenalties(matcher_bd1.group("penalty")));
		if (matcher_bd2.find())
			tmpBdList.add(Jpecker16__Util.parsePenalties(matcher_bd2.group("penalty")));
		if (tmpBdList.size() == 0) {
			while (matcher_bd3.find())
				tmpBdList.add(Jpecker16__Util.parsePenalties(matcher_bd3.group("penalty")));
		}
		bd = Jpecker16__Util.sortPenalties(tmpBdList);
		
		Matcher matcher_ad1 = pattern1.matcher(ad_text_purged);
		Matcher matcher_ad2 = pattern2.matcher(ad_text_purged);
		Matcher matcher_ad3 = pattern3.matcher(ad_text_purged);
		
		List<String> tmpAdList = new ArrayList<String>();
		while (matcher_ad1.find())
			tmpAdList.add(Jpecker16__Util.parsePenalties(matcher_ad1.group("penalty")));
		if (matcher_ad2.find())
			tmpAdList.add(Jpecker16__Util.parsePenalties(matcher_ad2.group("penalty")));
		if (tmpAdList.size() == 0) {
			while (matcher_ad3.find())
				tmpAdList.add(Jpecker16__Util.parsePenalties(matcher_ad3.group("penalty")));
		}
		ad = Jpecker16__Util.sortPenalties(tmpAdList);
		
		if (bd.equals("0"))
			bd = Jpecker16__Util.sortTwoPenalties(bd, Jpecker16__Util.parseRevalidation(airCompany, resultMap.get("bd")));
		if (ad.equals("0"))
			ad = Jpecker16__Util.sortTwoPenalties(ad, Jpecker16__Util.parseRevalidation(airCompany, resultMap.get("ad")));
		String tmpRevalid = Jpecker16__Util.parseRevalidation(airCompany, ruleText);
		if (bd == "" && ad == "" && tmpRevalid != "") {
			bd = ad = tmpRevalid;
		}
		
		if (bd.equals("") && ad.equals(""))
			return penaltyChangeItemList;
		PenaltyChangeItem penaltyChangeItem;
		if (bd.equals(ad)) {
			penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
			if (ifUsed != null)
				penaltyChangeItem.setUsed(ifUsed);
			penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(bd));
			penaltyChangeItemList.add(penaltyChangeItem);
		} else {
			if (!bd.equals("")) {
				penaltyChangeItem = new PenaltyChangeItem();
				penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
				if (ifUsed != null)
					penaltyChangeItem.setUsed(ifUsed);
				PenaltyCondition penaltyCondition = new PenaltyCondition();
				penaltyCondition.setPenaltyConditionType(PenaltyConditionTypeEnum.BEFORE_DEPT);
				penaltyChangeItem.setPenaltyCondition(penaltyCondition);
				penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(bd));
				penaltyChangeItemList.add(penaltyChangeItem);
			}
			if (!ad.equals("")) {
				penaltyChangeItem = new PenaltyChangeItem();
				penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
				if (ifUsed != null)
					penaltyChangeItem.setUsed(ifUsed);
				PenaltyCondition penaltyCondition = new PenaltyCondition();
				penaltyCondition.setPenaltyConditionType(PenaltyConditionTypeEnum.AFTER_DEPT);
				penaltyChangeItem.setPenaltyCondition(penaltyCondition);
				penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(ad));
				penaltyChangeItemList.add(penaltyChangeItem);
			}
		}
		
		return penaltyChangeItemList;
	}
	
	public List<String> parseIfUsed(String text) {
		List<String> textList = new ArrayList<String>();
		if (airCompany.equals("AA") || airCompany.equals("US")) {
			textList.add(text);
			return textList;
		}
		
		String unused = null, used = null;
		Matcher matcher_bd = Pattern.compile(PenaltyConst.PENALTIES + "\\sFOR WHOLLY UNUSED").matcher(text);
		Matcher matcher_bd_mf = Pattern.compile(PenaltyConst.PENALTIES 
				+ " FOR WHOLLY UNUSED TICKET\\.\n--REFUND FEE FOR PER TICKET\\s[A-Z]{3} ?\\d+").matcher(text);
		Matcher matcher_ad = Pattern.compile(PenaltyConst.PENALTIES + "\\sFOR PARTIALLY USED").matcher(text);
		if (airCompany.equals("MF")) {
			if (matcher_bd_mf.find() && matcher_ad.find()) {
				unused = matcher_bd_mf.group();
				used = matcher_ad.group();
				textList.add(unused);
				textList.add(used);
				return textList;
			}
		}
		if (matcher_bd.find() && matcher_ad.find()) {
			unused = matcher_bd.group();
			used = matcher_ad.group();
			textList.add(unused);
			textList.add(used);
			return textList;
		}
		
		Matcher matcher_or = Pattern.compile(
				PenaltyConst.ORUSED
				+ PenaltyConst.CONTENT + "$").matcher(text);
		Matcher matcher_xifused = Pattern.compile("^" 
				+ PenaltyConst.CONTENT + PenaltyConst.UNUSED
				+ PenaltyConst.CONTENT1 + PenaltyConst.USED
				+ PenaltyConst.CONTENT2 + "$").matcher(text);
		Matcher matcher_xifused_r = Pattern.compile("^" 
				+ PenaltyConst.CONTENT + PenaltyConst.USED
				+ PenaltyConst.CONTENT1 + PenaltyConst.UNUSED
				+ PenaltyConst.CONTENT2 + "(?:---|$)").matcher(text);
		if (matcher_or.find()) {
			textList.add(text);
			return textList;
		} else if (matcher_xifused.find()) {
			unused = matcher_xifused.group("content") + "\n" + matcher_xifused.group("content1");
			used = matcher_xifused.group("content") + "\n" + matcher_xifused.group("content2");
		} else if (matcher_xifused_r.find()) {
			unused = matcher_xifused_r.group("content") + "\n" + matcher_xifused_r.group("content2");
			used = matcher_xifused_r.group("content") + "\n" + matcher_xifused_r.group("content1");
		} else {
			textList.add(text);
			return textList;
		}
		
		textList.add(unused);
		textList.add(used);
		return textList;
	}
	
	private Map<String,String> parseBdAd(String text) {
		Map<String,String> resultMap = new HashMap<String,String>();
		String bd_text = "", ad_text = "", bd_text_purged = "", ad_text_purged = "";
		
		if (airCompany.equals("GA")) {
			Matcher matcher_ga = Pattern.compile("\n[A-Z]\\.\\d\\. BEFORE TRAVEL COMMENCED\n" 
					+ PenaltyConst.CONTENT1 + "\n[A-Z]\\.\\d\\. AFTER TRAVEL COMMENCED\n" 
					+ PenaltyConst.CONTENT2 + "$").matcher(text);
			if (matcher_ga.find()) {
				resultMap.put("bd", matcher_ga.group("content1"));
				resultMap.put("ad", matcher_ga.group("content2"));
				resultMap.put("bd_purged", matcher_ga.group("content1"));
				resultMap.put("ad_purged", matcher_ga.group("content2"));
				return resultMap;
			}
		}
		
		Matcher matcher_bd = Pattern.compile(PenaltyConst.PENALTIES + "\\s?:BEFORE\\s(?:DEPARTURE|DEPT)").matcher(text);
		Matcher matcher_bd_mu = Pattern.compile(PenaltyConst.PENALTIES 
				+ "(?:\\sOR)?(?:\\sOR\\sEQUIVALENT)?(?:\\sFOR\\sREFUND)?\\sBEF(?:ORE)?\\s(?:DEPARTURE|DEP(?:T)?)").matcher(text);
		Matcher matcher_ad = Pattern.compile(PenaltyConst.PENALTIES + "\\sAFTER\\s(?:DEPARTURE|DEP(?:T)?)").matcher(text);
		if (airCompany.equals("MU")) {
			if (matcher_bd_mu.find() && matcher_ad.find()) {
				resultMap.put("bd", matcher_bd_mu.group());
				resultMap.put("ad", matcher_ad.group());
				resultMap.put("bd_purged", matcher_bd_mu.group());
				resultMap.put("ad_purged", matcher_ad.group());
				return resultMap;
			}
		}
		if (matcher_bd.find() && matcher_ad.find()) {
			resultMap.put("bd", matcher_bd.group());
			resultMap.put("ad", matcher_ad.group());
			resultMap.put("bd_purged", matcher_bd.group());
			resultMap.put("ad_purged", matcher_ad.group());
			return resultMap;
		}
		
		Matcher matcher_all = Pattern.compile("^\\s*ANY TIME *\n"
			+ PenaltyConst.CONTENT + "\n *" + PenaltyConst.BD + "[ -]*\n" 
			+ PenaltyConst.CONTENT1 + "\n *" + PenaltyConst.AD + "[ -]*\n" 
			+ PenaltyConst.CONTENT2 + "$").matcher(text);
		Matcher matcher_any = Pattern.compile("^\\s*ANY TIME *\n"
			+ PenaltyConst.CONTENT + "$").matcher(text);
		Matcher matcher_bdad = Pattern.compile("^ *" + PenaltyConst.BD + "[ -]*\n" 
			+ PenaltyConst.CONTENT1 + "\n *" + PenaltyConst.AD + "[ -]*\n" 
			+ PenaltyConst.CONTENT2 + "$").matcher(text);
		Matcher matcher_xbdad = Pattern.compile("^" 
			+ PenaltyConst.CONTENT + "\n *" + PenaltyConst.BD + "[ -]*\n" 
			+ PenaltyConst.CONTENT1 + "\n *" + PenaltyConst.AD + "[ -]*\n" 
			+ PenaltyConst.CONTENT2 + "$").matcher(text);
		Matcher matcher_xbd = Pattern.compile("^" 
				+ PenaltyConst.CONTENT + "\n *" + PenaltyConst.BD + "[ -]*\n" 
				+ PenaltyConst.CONTENT1 + "$").matcher(text);
		Matcher matcher_onlybd = Pattern.compile("(?:^|\n *)"+ PenaltyConst.BD + "[ -]*\n" 
			+ PenaltyConst.CONTENT1 + "$").matcher(text);
		
		Matcher matcher_all_r = Pattern.compile("^\\s*ANY TIME *\n"
			+ PenaltyConst.CONTENT + "\n *" + PenaltyConst.AD + "[ -]*\n" 
			+ PenaltyConst.CONTENT1 + "\n *" + PenaltyConst.BD + "[ -]*\n" 
			+ PenaltyConst.CONTENT2 + "$").matcher(text);
		Matcher matcher_any_r = Pattern.compile("^\\s*ANY TIME *\n"
			+ PenaltyConst.CONTENT + "$").matcher(text);
		Matcher matcher_bdad_r = Pattern.compile("^ *" + PenaltyConst.AD + "[ -]*\n" 
			+ PenaltyConst.CONTENT1 + "\n *" + PenaltyConst.BD + "[ -]*\n" 
			+ PenaltyConst.CONTENT2 + "$").matcher(text);
		Matcher matcher_xbdad_r = Pattern.compile("^" 
			+ PenaltyConst.CONTENT + "\n *" + PenaltyConst.AD + "[ -]*\n" 
			+ PenaltyConst.CONTENT1 + "\n *" + PenaltyConst.BD + "[ -]*\n" 
			+ PenaltyConst.CONTENT2 + "$").matcher(text);
		Matcher matcher_xad = Pattern.compile("^" 
				+ PenaltyConst.CONTENT + "\n *" + PenaltyConst.AD + "[ -]*\n" 
				+ PenaltyConst.CONTENT1 + "$").matcher(text);
		Matcher matcher_onlyad = Pattern.compile("(?:^|\n *)"+ PenaltyConst.AD + "[ -]*\n" 
			+ PenaltyConst.CONTENT1 + "$").matcher(text);
		
		if (matcher_all.find()) {
			bd_text = matcher_all.group("content") + "\n" + matcher_all.group("content1");
			ad_text = matcher_all.group("content") + "\n" + matcher_all.group("content2");
			bd_text_purged = Jpecker16__Util.rmNote(matcher_all.group("content")) + "\n" + Jpecker16__Util.rmNote(matcher_all.group("content1"));
			ad_text_purged = Jpecker16__Util.rmNote(matcher_all.group("content")) + "\n" + Jpecker16__Util.rmNote(matcher_all.group("content2"));
		} else if (matcher_all_r.find()) {
			bd_text = matcher_all_r.group("content") + "\n" + matcher_all_r.group("content2");
			ad_text = matcher_all_r.group("content") + "\n" + matcher_all_r.group("content1");
			bd_text_purged = Jpecker16__Util.rmNote(matcher_all_r.group("content")) + "\n" + Jpecker16__Util.rmNote(matcher_all_r.group("content2"));
			ad_text_purged = Jpecker16__Util.rmNote(matcher_all_r.group("content")) + "\n" + Jpecker16__Util.rmNote(matcher_all_r.group("content1"));
		} else if (matcher_any.find()) {
			bd_text = ad_text = text;
			bd_text_purged = ad_text_purged = Jpecker16__Util.rmNote(text);
		} else if (matcher_any_r.find()) {
			bd_text = ad_text = text;
			bd_text_purged = ad_text_purged = Jpecker16__Util.rmNote(text);
		} else if (matcher_bdad.find()) {
			bd_text = matcher_bdad.group("content1");
			ad_text = matcher_bdad.group("content2");
			bd_text_purged = Jpecker16__Util.rmNote(matcher_bdad.group("content1"));
			ad_text_purged = Jpecker16__Util.rmNote(matcher_bdad.group("content2"));
		} else if (matcher_bdad_r.find()) {
			bd_text = matcher_bdad_r.group("content2");
			ad_text = matcher_bdad_r.group("content1");
			bd_text_purged = Jpecker16__Util.rmNote(matcher_bdad_r.group("content2"));
			ad_text_purged = Jpecker16__Util.rmNote(matcher_bdad_r.group("content1"));
		} else if (matcher_xbdad.find()) {
			bd_text = matcher_xbdad.group("content") + "\n" + matcher_xbdad.group("content1");
			ad_text = matcher_xbdad.group("content") + "\n" + matcher_xbdad.group("content2");
			bd_text_purged = Jpecker16__Util.rmNote(matcher_xbdad.group("content")) + "\n" + Jpecker16__Util.rmNote(matcher_xbdad.group("content1"));
			ad_text_purged = Jpecker16__Util.rmNote(matcher_xbdad.group("content")) + "\n" + Jpecker16__Util.rmNote(matcher_xbdad.group("content2"));
		} else if (matcher_xbdad_r.find()) {
			bd_text = matcher_xbdad_r.group("content") + "\n" + matcher_xbdad_r.group("content2");
			ad_text = matcher_xbdad_r.group("content") + "\n" + matcher_xbdad_r.group("content1");
			bd_text_purged = Jpecker16__Util.rmNote(matcher_xbdad_r.group("content")) + "\n" + Jpecker16__Util.rmNote(matcher_xbdad_r.group("content2"));
			ad_text_purged = Jpecker16__Util.rmNote(matcher_xbdad_r.group("content")) + "\n" + Jpecker16__Util.rmNote(matcher_xbdad_r.group("content1"));
		} else if (matcher_xbd.find()) {
			bd_text = matcher_xbd.group("content") + "\n" + matcher_xbd.group("content1");
			ad_text = matcher_xbd.group("content");
			bd_text_purged = Jpecker16__Util.rmNote(matcher_xbd.group("content")) + "\n" + Jpecker16__Util.rmNote(matcher_xbd.group("content1"));
			ad_text_purged = Jpecker16__Util.rmNote(matcher_xbd.group("content"));
		} else if (matcher_xad.find()) {
			bd_text = matcher_xad.group("content");
			ad_text = matcher_xad.group("content") + "\n" + matcher_xad.group("content1");
			bd_text_purged = Jpecker16__Util.rmNote(matcher_xad.group("content"));
			ad_text_purged = Jpecker16__Util.rmNote(matcher_xad.group("content")) + "\n" + Jpecker16__Util.rmNote(matcher_xad.group("content1"));
		} else if (matcher_onlybd.find()) {
			bd_text = matcher_onlybd.group("content1");
			bd_text_purged = Jpecker16__Util.rmNote(matcher_onlybd.group("content1"));
		} else if (matcher_onlyad.find()) {
			ad_text = matcher_onlyad.group("content1");
			ad_text_purged = Jpecker16__Util.rmNote(matcher_onlyad.group("content1"));
		} else {
			bd_text = ad_text = text;
			bd_text_purged = ad_text_purged = Jpecker16__Util.rmNote(text);
		}
		
//		log.debug("Text:\n"+text);
//		log.debug("BD Text:\n"+bd_text);
//		log.debug("Purged BD Text:\n"+bd_text_purged);
//		log.debug("AD Text:\n"+ad_text);
//		log.debug("Purged AD Text:\n"+ad_text_purged);
		
		resultMap.put("bd", bd_text);
		resultMap.put("ad", ad_text);
		resultMap.put("bd_purged", bd_text_purged);
		resultMap.put("ad_purged", ad_text_purged);
		return resultMap;
	}
}
