/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */
 
package com.travelzen.farerule.jpecker.pecker.pecker16;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.travelzen.farerule.jpecker.consts.ConditionConst;
import com.travelzen.farerule.jpecker.struct.RuleTextSegment;

public class Jpecker16__Pre {
	
	private List<RuleTextSegment> ruleTextSegmentList = new ArrayList<RuleTextSegment>();
	
	private List<String> originList = new ArrayList<String>();
	private List<String> salesResList = new ArrayList<String>();
	private List<String> travelResList = new ArrayList<String>();
	List<Map<String, String>> tmpTextInfoList = new ArrayList<Map<String, String>>();
	private List<Map<String, String>> textInfoList = new ArrayList<Map<String, String>>();
	
	public List<RuleTextSegment> getReadyList() {
		return ruleTextSegmentList;
	}
	
	public void process(String ruleText) {
		// Process multiple origins and multiple date ranges
		splitOrigins(ruleText);
		// Merge same origin & date range
		mergeTextInfoList();
		// To RuleTextSegment List
		RuleTextSegment ruleTextSegment;
		for (Map<String, String> textInfo:textInfoList) {
			ruleTextSegment = new RuleTextSegment();
			if (!textInfo.get("origin").equals(""))
				ruleTextSegment.setOrigin(textInfo.get("origin"));
			if (!textInfo.get("salesRes").equals(""))
				ruleTextSegment.setSalesDate(textInfo.get("salesRes"));
			if (!textInfo.get("travelRes").equals(""))
				ruleTextSegment.setTravelDate(textInfo.get("travelRes"));
			ruleTextSegment.setText(textInfo.get("text"));
			ruleTextSegmentList.add(ruleTextSegment);
		}
	}
	
	private void splitOrigins(String text) {
		String share = "";
		Matcher matcher_share = Pattern.compile(ConditionConst.ORIGINSHARE).matcher(text);
		Matcher matcher = Pattern.compile(ConditionConst.ORIGINS).matcher(text);
		if (matcher_share.find()) {
			share = matcher_share.group(1) + "\n";
		}
		if (matcher.find()) {
			splitDates(matcher.group(1), share + matcher.group(2));
		} else {
			splitDates("", text);
			return;
		}
		while (matcher.find()) {
			splitDates(matcher.group(1), share + matcher.group(2));
		}
	}
	
	private void splitDates(String origin, String text) {
		String share = "", salesRes = "", travelRes = "", ruleText = "";
		Matcher matcher0 = Pattern.compile(ConditionConst.DATESHARE).matcher(text);
		Matcher matcher1 = Pattern.compile(ConditionConst.SALESRES).matcher(text);
		Matcher matcher2 = Pattern.compile(ConditionConst.TRAVELRES).matcher(text);
		if (matcher0.find()) {
			share = matcher0.group(1) + "\n";
		}
		if (matcher1.find()) {
			salesRes = matcher1.group(1);
			travelRes = ""; // matcher1.group(2);
			ruleText = share + matcher1.group(3);
			saveToList(origin, salesRes, travelRes, ruleText);
		} else if (matcher2.find()) {
			salesRes = "";
			travelRes = matcher2.group(1);
			ruleText = share + matcher2.group(2);
			saveToList(origin, salesRes, travelRes, ruleText);
		} else {
			salesRes = "";
			travelRes = "";
			ruleText = text;
			saveToList(origin, salesRes, travelRes, ruleText);
			return;
		}
		while (matcher1.find()) {
			salesRes = matcher1.group(1);
			travelRes = ""; // matcher1.group(2);
			ruleText = share + matcher1.group(3);
			saveToList(origin, salesRes, travelRes, ruleText);
		}
		while (matcher2.find()) {
			salesRes = "";
			travelRes = matcher2.group(1);
			ruleText = share + matcher2.group(2);
			saveToList(origin, salesRes, travelRes, ruleText);
		}
	}
	
	private void saveToList(String origin, String salesRes, String travelRes, String text) {
		if (!originList.contains(origin))
			originList.add(origin);
		if (!salesRes.equals("") && !salesResList.contains(salesRes))
			salesResList.add(salesRes);
		if (!travelRes.equals("") && !travelResList.contains(travelRes))
			travelResList.add(travelRes);
		HashMap<String, String> textInfoMap = new HashMap<String, String>();
		textInfoMap.put("origin", origin);
		textInfoMap.put("salesRes", salesRes);
		textInfoMap.put("travelRes", travelRes);
		textInfoMap.put("text", text);
		tmpTextInfoList.add(textInfoMap);
	}
	
	private void mergeTextInfoList() {
		for (String origin:originList) {	
			if (salesResList.size() == 0 && travelResList.size() == 0) {
				StringBuilder mergedText = new StringBuilder();
				for (Map<String, String> textInfo:tmpTextInfoList) {
					if (textInfo.get("origin").equals(origin) && textInfo.get("salesRes").equals("")
							&& textInfo.get("travelRes").equals("")) {
						mergedText.append(textInfo.get("text")).append("\n");
					}
				}
				HashMap<String, String> textInfoMap = new HashMap<String, String>();
				textInfoMap.put("origin", origin);
				textInfoMap.put("salesRes", "");
				textInfoMap.put("travelRes", "");
				textInfoMap.put("text", mergedText.toString());
				textInfoList.add(textInfoMap);
			} else if (salesResList.size() > 0 && travelResList.size() == 0) {
				boolean flag = false;
				for (String salesRes:salesResList) {
					StringBuilder mergedText = new StringBuilder();
					boolean flag_in = false;
					for (Map<String, String> textInfo:tmpTextInfoList) {
						if (textInfo.get("origin").equals(origin) && textInfo.get("salesRes").equals(salesRes)) {
							mergedText.append(textInfo.get("text")).append("\n");
							flag_in = true;
						}
					}
					if (flag_in == true) {
						HashMap<String, String> textInfoMap = new HashMap<String, String>();
						textInfoMap.put("origin", origin);
						textInfoMap.put("salesRes", salesRes);
						textInfoMap.put("travelRes", "");
						textInfoMap.put("text", mergedText.toString());
						textInfoList.add(textInfoMap);
						flag = true;
					}
				}
				if (flag == false) {
					StringBuilder mergedText = new StringBuilder();
					boolean flag_in = false;
					for (Map<String, String> textInfo:tmpTextInfoList) {
						if (textInfo.get("origin").equals(origin) && textInfo.get("salesRes").equals("")) {
							mergedText.append(textInfo.get("text")).append("\n");
							flag_in = true;
						}
					}
					if (flag_in == true) {
						HashMap<String, String> textInfoMap = new HashMap<String, String>();
						textInfoMap.put("origin", origin);
						textInfoMap.put("salesRes", "");
						textInfoMap.put("travelRes", "");
						textInfoMap.put("text", mergedText.toString());
						textInfoList.add(textInfoMap);
					}
				}
			} else if (salesResList.size() == 0 && travelResList.size() > 0) {
				boolean flag = false;
				for (String travelRes:travelResList) {
					StringBuilder mergedText = new StringBuilder();
					boolean flag_in = false;
					for (Map<String, String> textInfo:tmpTextInfoList) {
						if (textInfo.get("origin").equals(origin) && textInfo.get("travelRes").equals(travelRes)) {
							mergedText.append(textInfo.get("text")).append("\n");
							flag_in = true;
						}
					}
					if (flag_in == true) {
						HashMap<String, String> textInfoMap = new HashMap<String, String>();
						textInfoMap.put("origin", origin);
						textInfoMap.put("salesRes", "");
						textInfoMap.put("travelRes", travelRes);
						textInfoMap.put("text", mergedText.toString());
						textInfoList.add(textInfoMap);
						flag = true;
					}
				}
				if (flag == false) {
					StringBuilder mergedText = new StringBuilder();
					boolean flag_in = false;
					for (Map<String, String> textInfo:tmpTextInfoList) {
						if (textInfo.get("origin").equals(origin) && textInfo.get("travelRes").equals("")) {
							mergedText.append(textInfo.get("text")).append("\n");
							flag_in = true;
						}
					}
					if (flag_in == true) {
						HashMap<String, String> textInfoMap = new HashMap<String, String>();
						textInfoMap.put("origin", origin);
						textInfoMap.put("salesRes", "");
						textInfoMap.put("travelRes", "");
						textInfoMap.put("text", mergedText.toString());
						textInfoList.add(textInfoMap);
					}
				}
			}
		}
	}
}
