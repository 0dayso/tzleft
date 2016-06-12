/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */
 
package com.travelzen.farerule.jpecker.nest;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.fare.RuleInfo;
import com.travelzen.fare.RuleInfoQuery;
import com.travelzen.fare.dandelion.ServiceProviderFactory;
import com.travelzen.fare.dandelion.service.FareRuleDBService;
import com.travelzen.fare.dandelion.service.ServiceProvider;
import com.travelzen.farerule.mongo.OriginalRule;
import com.travelzen.farerule.mongo.morphia.OriginalRuleMorphia;

public class JpeckerNest {
	
	private static final Logger log = LoggerFactory.getLogger(JpeckerNest.class);
	
	private static OriginalRuleMorphia originalRuleMorphia = OriginalRuleMorphia.Instance;
	
	List<String> hashIdList = new ArrayList<String>();
    
    public void nest(long time) {
    	List<RuleInfo> ruleInfoList = fetchRuleInfoList(time);
    	iterateRuleInfoList(ruleInfoList);
    }
    
    private void iterateRuleInfoList(List<RuleInfo> ruleInfoList) {
    	try {
    		BufferedWriter writer = new BufferedWriter(new FileWriter("data/RuleInfoDict.txt"));
    		if (ruleInfoList.size() != 0) {
    			int count = 0;
                for (RuleInfo ruleInfo:ruleInfoList) {         	
                	// Get original RuleInfo Id
					String originalRuleInfoId = ruleInfo.getOriginalRuleInfoId();
					if(hashIdList.contains(originalRuleInfoId))
						continue;
					else
						hashIdList.add(originalRuleInfoId);
					// Get airline
					RuleInfoQuery ruleInfoQuery = ruleInfo.getRuleInfoQuery();
					String airline = ruleInfoQuery.getFilingAirline();
					// write info line 
					String line = originalRuleInfoId + "   " + airline + "\n";
					writer.write(line);
					
					// Get original Rule Text
					String originalRuleText = fetchOriginalRuleText(originalRuleInfoId);
					// Get Rule Text 16
					String rule16 = get16(originalRuleText);
					// save result text
//					savePenaltiesByAirline(airline, rule16);
//					saveSamplesByAirline(airline, originalRuleInfoId, originalRuleText);
					if (count++%100 == 0)
						System.out.print("\n");
					System.out.print(".");
                }
            }
			writer.flush();
			writer.close();
		} catch (Exception e) {
            log.error(e.getMessage(), e);
        }
	}
	
	private String get16(String ruleDoc) {		
		String startAnchor = "\n16\\.[A-Z]";
		String endAnchor = "(?:\\*+ *END *\\*+)"; // '|$' produce massive sums of 'null'
		
		Pattern pattern = Pattern.compile(startAnchor + "[\\w\\W]+?" + endAnchor);
		Matcher matcher = pattern.matcher(ruleDoc);
		
		String ruleText = "";
		if (matcher.find()) {
			ruleText = matcher.group(0).substring(matcher.group(0).indexOf("\n", 1) + 1);
		}
		return ruleText;
	}
	
	private void makeDir(File file) {
		if (!file.getParentFile().exists()) {
			makeDir(file.getParentFile());
		}
		file.mkdir();
	}
	
	private void savePenaltiesByAirline(String airline, String rule16) {
		try {
			File file = new File("data/PenaltiesByAirline/"+airline+".txt");
			if (!file.getParentFile().exists()) {
				makeDir(file.getParentFile());
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedReader reader = new BufferedReader(
				new FileReader("data/PenaltiesByAirline/"+airline+".txt"));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = reader.readLine()) != null) {
				sb.append(line+"\n");
			}
			reader.close();
			BufferedWriter writer = new BufferedWriter(
				new FileWriter("data/PenaltiesByAirline/"+airline+".txt"));
			sb.append("16.PENALTIES\n"+rule16+"\n\n\n");
			writer.write(sb.toString());
			writer.flush();
            writer.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	private void saveSamplesByAirline(String airline, String originalRuleInfoId, String originalRuleText) {
		try {
			File dir = new File("data/SamplesByAirline/"+airline);
			if (!dir.getParentFile().exists()) {
				makeDir(dir);
			}
			if (!dir.exists()) {
				dir.mkdir();
			}			
			BufferedWriter writer = new BufferedWriter(
				new FileWriter("data/SamplesByAirline/"+airline+"/"+originalRuleInfoId+".txt"));
			writer.write(originalRuleText);
			writer.flush();
            writer.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	private List<RuleInfo> fetchRuleInfoList(long updateTime) {
    	List<RuleInfo> ruleInfoList = new ArrayList<RuleInfo>();
        try {
        	ServiceProvider provider = ServiceProviderFactory.get();
			FareRuleDBService fareRuleDBService = provider.getFareRuleDBService();
			ruleInfoList = fareRuleDBService.searchRuleInfoByUpdateTime(updateTime);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
        return ruleInfoList;
    }
	
	private String fetchOriginalRuleText(String originalRuleInfoId) {
        OriginalRule originalRule = originalRuleMorphia.findById(originalRuleInfoId);
        if (originalRule == null)
        	return null;
        return originalRule.getText();
    }
}
