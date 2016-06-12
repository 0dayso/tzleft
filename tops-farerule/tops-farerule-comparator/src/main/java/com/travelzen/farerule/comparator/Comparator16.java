/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.comparator;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.farerule.Penalties;
import com.travelzen.farerule.comparator.rod.ComRod16;
import com.travelzen.farerule.cpecker.pecker.Cpecker16;
import com.travelzen.farerule.jpecker.pecker.Jpecker16;
import com.travelzen.farerule.rule.PenaltiesItem;
import com.travelzen.farerule.rule.PenaltyCancelItem;
import com.travelzen.farerule.rule.PenaltyCancelTypeEnum;
import com.travelzen.farerule.rule.PenaltyChangeItem;
import com.travelzen.farerule.rule.PenaltyChangeTypeEnum;
import com.travelzen.farerule.rule.PenaltyContent;
import com.travelzen.farerule.rule.PenaltyFee;
import com.travelzen.farerule.rule.PenaltyTypeEnum;

public class Comparator16 {

	private static final Logger log = LoggerFactory.getLogger(Comparator16.class);
	
	public List<Boolean> versus(String airCompany, String rawRule, String ibeRule1, String ibeRule2, String ibeRule3) {
		List<Boolean> boolList = new ArrayList<Boolean>();
		
		if (rawRule == null) {
			log.debug("Original Rule Text is null!");
			return boolList;
		}
		
		if (ibeRule1 == null && ibeRule2 == null && ibeRule3 == null) {
			log.debug("ibeplus Text is null!");
			return boolList;
		}
		
		Penalties myPenalties = parseRawRule(airCompany, rawRule);
		Penalties ibePenalties = parseIbeRule(airCompany, ibeRule1, ibeRule2, ibeRule3);
//		System.out.println(airCompany);
//		System.out.println(rawRule);
//		System.out.println(myPenalties);
//		System.out.println(ibePenalties);
		
		if (myPenalties == null || ibePenalties == null) {
			if (myPenalties == null && ibePenalties == null)
				return Arrays.asList(true, true);
			else
				return Arrays.asList(false, false);
		}
		if (myPenalties.getPenaltiesItemList() == null && ibePenalties.getPenaltiesItemList() == null)
			return Arrays.asList(true, true);
		if (myPenalties.getPenaltiesItemList() == null || ibePenalties.getPenaltiesItemList() == null ||
				myPenalties.getPenaltiesItemList().size() != ibePenalties.getPenaltiesItemList().size()) {
			log.debug("Conflicting sizes of two Penalties!");	
			ComRod16.saveSizeErrorText(airCompany, rawRule, ibeRule1, ibeRule2, ibeRule3, myPenalties, ibePenalties);
			return boolList;
		}

		PenaltiesItem myPenaltiesItem = null, ibePenaltiesItem = null;
		
		boolean flag1 = true, flag2 = true;
		for (int i=0; i<myPenalties.getPenaltiesItemList().size(); i++) {
			myPenaltiesItem = myPenalties.getPenaltiesItemList().get(i);
			ibePenaltiesItem = ibePenalties.getPenaltiesItemList().get(i);
			
			flag1 = compareCancel(flag1, myPenaltiesItem.getPenaltyCancelItemList(), ibePenaltiesItem.getPenaltyCancelItemList());
			flag2 = compareChange(flag2, myPenaltiesItem.getPenaltyChangeItemList(), ibePenaltiesItem.getPenaltyChangeItemList());
		}
		
		if (flag1 == false || flag2 == false) {
			ComRod16.saveResults(airCompany, rawRule, ibeRule1, ibeRule2, ibeRule3, myPenalties, ibePenalties, "all");
		}
		if (flag1 == false) {
			ComRod16.saveResults(airCompany, rawRule, ibeRule1, ibeRule2, ibeRule3, myPenalties, ibePenalties, "cancel");
		}
		if (flag2 == false) {
			ComRod16.saveResults(airCompany, rawRule, ibeRule1, ibeRule2, ibeRule3, myPenalties, ibePenalties, "change");
		}
		
		boolList.add(flag1);
		boolList.add(flag2);
		return boolList;
	}
	
	// Compare cancel
	private boolean compareCancel(boolean flag, 
			List<PenaltyCancelItem> myPenaltyCancelItemList, 
			List<PenaltyCancelItem> ibePenaltyCancelItemList) {
		if (flag == false)
			return flag;
//		if (myPenaltyCancelItemList.size() != ibePenaltyCancelItemList.size())
//			return false;
		int size = myPenaltyCancelItemList.size()<ibePenaltyCancelItemList.size()?myPenaltyCancelItemList.size():ibePenaltyCancelItemList.size();
		for (int i=0;i<size;i++) {
			PenaltyCancelItem myPenaltyCancelItem = myPenaltyCancelItemList.get(i);
			PenaltyCancelItem ibePenaltyCancelItem = ibePenaltyCancelItemList.get(i);
			if (myPenaltyCancelItem.getPenaltyCancelType() != PenaltyCancelTypeEnum.CANCEL && myPenaltyCancelItem.getPenaltyCancelType() != PenaltyCancelTypeEnum.NOSHOW)
				continue;
			if (myPenaltyCancelItem.getPenaltyCancelType() != ibePenaltyCancelItem.getPenaltyCancelType())
				return false;
			if ((myPenaltyCancelItem.getPenaltyCondition() == null && ibePenaltyCancelItem.getPenaltyCondition() == null) ||
					(myPenaltyCancelItem.getPenaltyCondition() != null && ibePenaltyCancelItem.getPenaltyCondition() != null && 
					myPenaltyCancelItem.getPenaltyCondition().getPenaltyConditionType() == ibePenaltyCancelItem.getPenaltyCondition().getPenaltyConditionType())) {
				if (isEqual(myPenaltyCancelItem.getPenaltyContent(), ibePenaltyCancelItem.getPenaltyContent()) == false)
					return false;
			} else {
				return false;
			}
		}
		return flag;
	}
	
	// Compare change
	private boolean compareChange(boolean flag,
			List<PenaltyChangeItem> myPenaltyChangeItemList,
			List<PenaltyChangeItem> ibePenaltyChangeItemList) {
		if (flag == false)
			return flag;
//		if (myPenaltyChangeItemList.size() != ibePenaltyChangeItemList.size())
//			return false;
		int size = myPenaltyChangeItemList.size()<ibePenaltyChangeItemList.size()?myPenaltyChangeItemList.size():ibePenaltyChangeItemList.size();
		for (int i=0;i<size;i++) {
			PenaltyChangeItem myPenaltyChangeItem = myPenaltyChangeItemList.get(i);
			PenaltyChangeItem ibePenaltyChangeItem = ibePenaltyChangeItemList.get(i);
			if (myPenaltyChangeItem.getPenaltyChangeType() != PenaltyChangeTypeEnum.CHANGE && myPenaltyChangeItem.getPenaltyChangeType() != PenaltyChangeTypeEnum.NOSHOW)
				continue;
			if (myPenaltyChangeItem.getPenaltyChangeType() != ibePenaltyChangeItem.getPenaltyChangeType())
				return false;
			if ((myPenaltyChangeItem.getPenaltyCondition() == null && ibePenaltyChangeItem.getPenaltyCondition() == null) ||
					(myPenaltyChangeItem.getPenaltyCondition() != null && ibePenaltyChangeItem.getPenaltyCondition() != null && 
					myPenaltyChangeItem.getPenaltyCondition().getPenaltyConditionType() == ibePenaltyChangeItem.getPenaltyCondition().getPenaltyConditionType())) {
				if (isEqual(myPenaltyChangeItem.getPenaltyContent(), ibePenaltyChangeItem.getPenaltyContent()) == false)
					return false;
			} else {
				return false;
			}
		}
		return flag;
	}
	
	private boolean isEqual(PenaltyContent myPenaltyContent, PenaltyContent ibePenaltyContent) {
		boolean flag = true;
		if (myPenaltyContent.getPenaltyType() != ibePenaltyContent.getPenaltyType())
			return false;
		if (myPenaltyContent.getPenaltyType() == PenaltyTypeEnum.FEE) {
			PenaltyFee myPenaltyFee = myPenaltyContent.getPenaltyFeeList().get(0);
			PenaltyFee ibePenaltyFee = ibePenaltyContent.getPenaltyFeeList().get(0);
			if (myPenaltyFee.getAmount() != ibePenaltyFee.getAmount())
				return false;
		}
		if (myPenaltyContent.getPenaltyType() == PenaltyTypeEnum.RATIO) {
			if (myPenaltyContent.getPenaltyRatio().getRatio() != ibePenaltyContent.getPenaltyRatio().getRatio())
				return false;
		}
		return flag;
	}
	
	private Penalties parseRawRule(String airCompany, String rawRule) {
		Penalties myPenalties = new Penalties();
		Jpecker16 jpecker16 = new Jpecker16(airCompany);
		jpecker16.parse(rawRule);
		myPenalties = jpecker16.getPenalties();
		return myPenalties;
	}
	
	private Penalties parseIbeRule(String airCompany, String ibeRule1, String ibeRule2, String ibeRule3) {
		Penalties ibePenalties = new Penalties();
		Cpecker16 cpecker16 = new Cpecker16(airCompany);
		cpecker16.parse(ibeRule1, ibeRule2, ibeRule3);
		ibePenalties = cpecker16.getPenalties();
		return ibePenalties;
	}
}
