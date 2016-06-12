/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.comparator;

import java.util.*;

import com.travelzen.farerule.MinStay;
import com.travelzen.farerule.cpecker.pecker.Cpecker6;
import com.travelzen.farerule.jpecker.pecker.Jpecker6;
import com.travelzen.farerule.rule.MinStayItem;

public class Comparator6 {
	
	public boolean versus(String airCompany, String rawRule, String ibeRule) {
		boolean flag = true;		
		MinStay myMinStay = parseRawRule(airCompany, rawRule);
		MinStay ibeMinStay = parseIbeRule(ibeRule);
		
		List<MinStayItem> myMinStayItemList = myMinStay.getMinStayItemList();
		List<MinStayItem> ibeMinStayItemList = ibeMinStay.getMinStayItemList();
		
		if (myMinStayItemList == null && ibeMinStayItemList == null)
			return true;
		// Conflicting 
		if (myMinStayItemList == null || ibeMinStayItemList == null ||
				(myMinStayItemList.size() != ibeMinStayItemList.size())) {
			return false;
		}
		
		for (int i=0; i<myMinStayItemList.size(); i++) {
			if (flag == false)
				break;
			MinStayItem myMinStayItem = myMinStayItemList.get(i);
			MinStayItem ibeMinStayItem = ibeMinStayItemList.get(i);
			if (myMinStayItem.getStayTimeType() != ibeMinStayItem.getStayTimeType()
					|| myMinStayItem.getStayTimeNum() != ibeMinStayItem.getStayTimeNum()) {
				flag = false;
			}
		}
		return flag;
	}
	
	private MinStay parseRawRule(String airCompany, String rawRule) {
		MinStay minStay = Jpecker6.parse(rawRule);
		return minStay;
	}
	
	private MinStay parseIbeRule(String ibeRule) {
		MinStay minStay = new MinStay();
		Cpecker6 cpecker = new Cpecker6();
		cpecker.parse(ibeRule);
		minStay = cpecker.getMinStay();
		return minStay;
	}
}
