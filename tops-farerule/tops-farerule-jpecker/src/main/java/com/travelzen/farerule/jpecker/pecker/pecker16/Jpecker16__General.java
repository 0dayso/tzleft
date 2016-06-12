/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.jpecker.pecker.pecker16;

import java.util.List;

import com.travelzen.farerule.jpecker.struct.RuleTextSegment;

public class Jpecker16__General extends Jpecker16__Base {
	
	public void process(String airCompany, String ruleText) {
		// Preprocess, get textInfoList
		Jpecker16__Pre pre = new Jpecker16__Pre();
		pre.process(ruleText);
		List<RuleTextSegment> ruleTextSegmentList = pre.getReadyList();
		
		// Parse cancel & change & noshow
		Jpecker16__Impl impl = new Jpecker16__Impl(airCompany);
		impl.process(ruleTextSegmentList);
		penalties = impl.getPenalties();
	}
}
