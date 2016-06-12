/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.jpecker.pecker.pecker16;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Jpecker16_AM extends Jpecker16__Base {

	public void process(String ruleText) {
		
		Matcher matcher1 = Pattern.compile(
				"\nIF INFANT 0[\\w\\W]+?"
				+ "\nOTHERWISE\n([\\w\\W]+)$").matcher(ruleText);
		Matcher matcher2 = Pattern.compile(
				"^([\\w\\W]+?)"
				+ "\nIF INFANT 0").matcher(ruleText);
		if (matcher1.find()) {
			Jpecker16__General general = new Jpecker16__General();
			general.process("AM", matcher1.group(1));
			penalties = general.getPenalties();
		} else if (matcher2.find()) {
			Jpecker16__General general = new Jpecker16__General();
			general.process("AM", matcher2.group(1));
			penalties = general.getPenalties();
		} else {
			Jpecker16__General general = new Jpecker16__General();
			general.process("AM", ruleText);
			penalties = general.getPenalties();
		}
	}
}