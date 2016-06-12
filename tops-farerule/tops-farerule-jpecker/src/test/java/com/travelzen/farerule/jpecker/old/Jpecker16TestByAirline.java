/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.jpecker.old;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;

import com.travelzen.farerule.Penalties;
import com.travelzen.farerule.jpecker.pecker.*;

@RunWith(JUnit4.class)
public class Jpecker16TestByAirline {

	private static final Logger log = LoggerFactory.getLogger(Jpecker16TestByAirline.class);
	
	@Test
	public void testByAirline() {
		String airCompany = "CX";
		String path = "data/PenaltiesByAirline/"+airCompany+".txt";
//		File file = new File("data/test16.txt");
//		System.out.println(file.getName().substring(0, file.getName().length()-4));
//		System.out.println(file.getPath());
//		System.out.println(file.getAbsolutePath());
		doTest(airCompany, path);
	}
	
	@Test
	public void testAllAirline() {
		File f = new File("data/PenaltiesByAirline");
        File[] files = f.listFiles();
        for (File file:files) {
        	String airCompany = file.getName().substring(0, file.getName().length()-4);
        	doTest(airCompany, file.getPath());
        }
	}
	
	private void doTest(String airCompany, String path) {
		StringBuilder sb = new StringBuilder();
		String line = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String start = "16\\.PENALTIES\n";
		String end = "(?=16\\.PENALTIES\n)";
		Pattern pattern = Pattern.compile(start + "([\\w\\W]+?)" + end);
		Matcher matcher = pattern.matcher(sb.toString());
		while (matcher.find()) {
			Jpecker16 jp16 = new Jpecker16(airCompany);
			try {
				log.info(matcher.group(1));
				jp16.parse(matcher.group(1));
				Penalties penalties = jp16.getPenalties();
				log.info(penalties.getPenaltiesItemList().toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

