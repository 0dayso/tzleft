/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.jpecker.pecker.pecker16;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Jpecker16_AY extends Jpecker16__Base {

	public void process(String ruleText) {
		
		Matcher matcher1 = Pattern.compile(
				"^\\s*(?:NOTE -\nANY TIME|ANY TIME\nNOTE -)\n(CHANGES[\\w\\W]+)"
				+ "\n(?:NOTE -\nANY TIME|ANY TIME\nNOTE -)\n(CANCELLATIONS[\\w\\W]+)").matcher(ruleText);
		Matcher matcher2 = Pattern.compile(
				"^\\s*(?:NOTE -\nANY TIME|ANY TIME\nNOTE -)\n(?:CHANGES|CHARGE [A-Z]{3} \\d+ FOR (?:REISSUE|REVALIDATION))").matcher(ruleText);
		if (matcher1.find()) {
			ruleText = "CHANGES\n" + matcher1.group(1) + "\nCANCELLATIONS\n" + matcher1.group(2);
			Jpecker16__General general = new Jpecker16__General();
			general.process("AY", ruleText);
			penalties = general.getPenalties();
		} else if (matcher2.find()) {
			ruleText = "CHANGES\n" + ruleText;
			Jpecker16__General general = new Jpecker16__General();
			general.process("AY", ruleText);
			penalties = general.getPenalties();
		} else {
			Jpecker16__General general = new Jpecker16__General();
			general.process("AY", ruleText);
			penalties = general.getPenalties();
		}
	}
}
