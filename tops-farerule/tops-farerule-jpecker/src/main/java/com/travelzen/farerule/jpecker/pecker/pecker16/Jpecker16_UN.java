/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.jpecker.pecker.pecker16;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Jpecker16_UN extends Jpecker16__Base {

	public void process(String ruleText) {
		Pattern pattern = Pattern.compile(
				"^([\\w\\W]+?)"
				+ "\nFOR SALE (?:IN|OUTSIDE) RUSSIAN FEDERATION"
				+ "[\\w\\W]+?(?:--+(\n[\\w\\W]+))?$");
		Matcher matcher = pattern.matcher(ruleText);
		while (matcher.find()) {
			if (matcher.group(2) == null)
				ruleText = matcher.group(1);
			else
				ruleText = matcher.group(1) + matcher.group(2);
			matcher = pattern.matcher(ruleText);
		}
		
		Jpecker16__General general = new Jpecker16__General();
		general.process("UN", ruleText);
		penalties = general.getPenalties();
	}
}
