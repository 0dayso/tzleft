/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.cpecker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.travelzen.farerule.Penalties;
import com.travelzen.farerule.cpecker.pecker.Cpecker16;
import com.travelzen.farerule.translator.RuleTranslator16;

@RunWith(JUnit4.class)
public class ReadCpecker16Test {
	
	String airCompany = "CX";

	@Test
	public void testNormal() {
		LogBase.logBack();
		
		StringBuilder sb = new StringBuilder();
		String line = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader("data/test-c16.txt"));
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Pattern pattern = Pattern.compile(
				"C1~~~~~([\\w\\W]+?)"
				+ "C2~~~~~([\\w\\W]+?)"
				+ "C3~~~~~([\\w\\W]+?)"
				+ "CE~~~~~");
		Matcher matcher = pattern.matcher(sb.toString());
		while (matcher.find()) {
			Cpecker16 cp16 = new Cpecker16(airCompany);
			try {
//				System.out.println(matcher.group(1));
				cp16.parse(matcher.group(1), matcher.group(2), matcher.group(3));
				Penalties penalties = cp16.getPenalties();
				System.out.println(penalties.getPenaltiesItemList());
				System.out.println(RuleTranslator16.translate(penalties));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
