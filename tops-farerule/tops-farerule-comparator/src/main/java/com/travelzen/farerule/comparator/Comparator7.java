/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.comparator;

import java.util.*;

import com.travelzen.farerule.MaxStay;
import com.travelzen.farerule.cpecker.pecker.Cpecker7;
import com.travelzen.farerule.jpecker.pecker.Jpecker7;
import com.travelzen.farerule.rule.MaxStayItem;

public class Comparator7 {
	
	public boolean versus(String airCompany, String rawRule, String ibeRule) {
		boolean flag = true;		
		MaxStay myMaxStay = parseRawRule(airCompany, rawRule);
		MaxStay ibeMaxStay = parseIbeRule(ibeRule);
		
		List<MaxStayItem> myMaxStayItemList = myMaxStay.getMaxStayItemList();
		List<MaxStayItem> ibeMaxStayItemList = ibeMaxStay.getMaxStayItemList();
		
		if (myMaxStayItemList == null && ibeMaxStayItemList == null)
			return true;
		// Conflicting 
		if (myMaxStayItemList == null || ibeMaxStayItemList == null ||
				(myMaxStayItemList.size() != ibeMaxStayItemList.size())) {
			return false;
		}
		
		for (int i=0; i<myMaxStayItemList.size(); i++) {
			if (flag == false)
				break;
			MaxStayItem myMaxStayItem = myMaxStayItemList.get(i);
			MaxStayItem ibeMaxStayItem = ibeMaxStayItemList.get(i);
			if (myMaxStayItem.getStayTimeType() != ibeMaxStayItem.getStayTimeType()
					|| myMaxStayItem.getStayTimeNum() != ibeMaxStayItem.getStayTimeNum()) {
				flag = false;
			}
		}
		return flag;
	}
	
	private MaxStay parseRawRule(String airCompany, String rawRule) {
		MaxStay maxStay = Jpecker7.parse(rawRule);
		return maxStay;
	}
	
	private MaxStay parseIbeRule(String ibeRule) {
		MaxStay maxStay = new MaxStay();
		Cpecker7 cpecker = new Cpecker7();
		cpecker.parse(ibeRule);
		maxStay = cpecker.getMaxStay();
		return maxStay;
	}
}
