/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.jpecker.pecker.pecker16;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Jpecker16_EK extends Jpecker16__Base {

	public void process(String ruleText) {
		Matcher matcher = Pattern.compile(
				"^([\\w\\W]*)"
				+ "\n +NOTE -\n").matcher(ruleText);
		Jpecker16__General general = new Jpecker16__General();
		if(matcher.find())
			general.process("EK", matcher.group(1));
		else
			general.process("EK", ruleText);
		penalties = general.getPenalties();
	}
}
