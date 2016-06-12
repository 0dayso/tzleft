/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.comparator.pool;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComPool16 {

	private static final Logger log = LoggerFactory.getLogger(ComPool16.class);

	public static StringBuilder originErrorBuilder = new StringBuilder();
	public static StringBuilder cancelBuilder = new StringBuilder();
	public static StringBuilder changeBuilder = new StringBuilder();
	public static StringBuilder noshowBuilder = new StringBuilder();
	public static StringBuilder penaltyBuilder = new StringBuilder();
	
	private String originErrorLoc = "data/errors/origin-errors.txt";
	private String versusCancelLoc = "data/errors/cancel-errors.txt";
	private String versusChangeLoc = "data/errors/change-errors.txt";
	private String versusNoshowLoc = "data/errors/noshow-errors.txt";
	private String penaltyLoc = "data/errors/versus16-errors.txt";
	
	public static Map<String, Integer> originErrorMap = new HashMap<String, Integer>();
	public static Map<String, Integer> errorMap1 = new HashMap<String, Integer>();
	public static Map<String, Integer> errorMap2 = new HashMap<String, Integer>();
	public static Map<String, Integer> errorMap3 = new HashMap<String, Integer>();
	public static Map<String, Integer> errorMap = new HashMap<String, Integer>();
	
	public static List<String> originList = new ArrayList<String>();
	
	// Save the text.
	public void save() {
		saveText("originError");
		saveText("cancel");
		saveText("change");
		saveText("noshow");
		saveText("all");
		
		originErrorMap = bubbleSort(originErrorMap);
		errorMap1 = bubbleSort(errorMap1);
		errorMap2 = bubbleSort(errorMap2);
		errorMap3 = bubbleSort(errorMap3);
		errorMap = bubbleSort(errorMap);
		saveMap(originErrorMap, "origin-error-sort");
		saveMap(errorMap1, "cancel-error-sort");
		saveMap(errorMap2, "change-error-sort");
		saveMap(errorMap3, "noshow-error-sort");
		saveMap(errorMap, "penalties-error-sort");
		
		log.info(originList.toString());
	}
	
	private void saveText(String tag) {
		try {
			String loc = "";
			switch (tag) {
				case "originError":
					loc = originErrorLoc;
					break;
				case "cancel":
					loc = versusCancelLoc;
					break;
				case "change":
					loc = versusChangeLoc;
					break;
				case "noshow":
					loc = versusNoshowLoc;
					break;
				case "all":
					loc = penaltyLoc;
					break;
			}
			File file = new File(loc);
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(loc));
			switch (tag) {
				case "originError":
					writer.write(originErrorBuilder.toString());
					break;
				case "cancel":
					writer.write(cancelBuilder.toString());
					break;
				case "change":
					writer.write(changeBuilder.toString());
					break;
				case "noshow":
					writer.write(noshowBuilder.toString());
					break;
				case "all":
					writer.write(penaltyBuilder.toString());
					break;
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	private Map<String, Integer> bubbleSort(Map<String, Integer> map) {
		Object[] keyArray = map.keySet().toArray();
		for (int i=0; i<keyArray.length; i++) {
			for (int j=0; j<keyArray.length-i-1; j++) {
				String code1 = keyArray[j].toString();
				String code2 = keyArray[j+1].toString();
				if (map.get(code1) < map.get(code2)) {
					keyArray[j] = code2;
					keyArray[j+1] = code1;
				}
			}
		}
		
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (int i=0; i<keyArray.length; i++) {
			String airline = keyArray[i].toString();
			sortedMap.put(airline, map.get(airline));
		}
		return sortedMap;
	}
	
	private void saveMap(Map<String, Integer> map, String fileName) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("data/sorted-errors/"+fileName+".txt"));
			StringBuilder sb = new StringBuilder();
			for (Entry<String, Integer> entry:map.entrySet()) {
				sb.append(entry.getKey() + " : " + entry.getValue() + "\n");
			}
			writer.write(sb.toString());
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
