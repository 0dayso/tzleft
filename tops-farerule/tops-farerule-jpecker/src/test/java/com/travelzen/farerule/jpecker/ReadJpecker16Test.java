/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.jpecker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.travelzen.farerule.Penalties;
import com.travelzen.farerule.jpecker.pecker.Jpecker16;
import com.travelzen.farerule.translator.RuleTranslator16;

@RunWith(JUnit4.class)
public class ReadJpecker16Test {
	
	String airCompany = "FM";

	@Test
	public void testNormal() {
		LogBase.logBack();
		
		StringBuilder sb = new StringBuilder();
		String line = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader("data/test16.txt"));
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String start = "S~~~~~";
		String end = "E~~~~~";
		Pattern pattern = Pattern.compile(start + "([\\w\\W]+?)" + end);
		Matcher matcher = pattern.matcher(sb.toString());
		while (matcher.find()) {
			Jpecker16 jp16 = new Jpecker16(airCompany);
			try {
//				System.out.println(matcher.group(1));
				// XXX 以前没乱码，现在有了
				jp16.parse(matcher.group(1).replaceAll("<<  ", ""));
				Penalties penalties = jp16.getPenalties();
				// test PenaltiesMerger
//				if (penalties != null) {
//					List<PenaltiesItem> penaltiesItemList = penalties.getPenaltiesItemList();
//					penaltiesItemList.addAll(penalties.getPenaltiesItemList());
//				}
//				penaltiesItemList = PenaltiesMerger.merge(penaltiesItemList);
//				penalties.setPenaltiesItemList(penaltiesItemList);
				System.out.println(penalties);
				System.out.println(RuleTranslator16.translate(penalties));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
