/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.comparator;

import java.util.*;
import java.text.NumberFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.farerule.MaxStay;
import com.travelzen.farerule.MinStay;
import com.travelzen.farerule.comparator.pool.ComPool16;
import com.travelzen.farerule.cpecker.pecker.Cpecker6;
import com.travelzen.farerule.cpecker.pecker.Cpecker7;
import com.travelzen.farerule.jpecker.pecker.Jpecker6;
import com.travelzen.farerule.jpecker.pecker.Jpecker7;
import com.travelzen.farerule.mongo.OriginalRule;
import com.travelzen.fare.RuleInfo;

public class Comparator {

	private static final Logger log = LoggerFactory.getLogger(Comparator.class);

	List<String> hashIdList = new ArrayList<String>();
    
    int sum, sum6, sum7, sum16, num6, num7, num16, num16_1, num16_2, null6, null7, null16, ori_error16;
    
    public void goVersus(long time, String theAirCompany) {
    	List<RuleInfo> ruleInfoList = ComparatorUtil.fetchRuleInfoList(time);
    	iterateRuleInfoList(theAirCompany, ruleInfoList);
    	// Save results
    	ComPool16 results = new ComPool16();
    	results.save();
    	
    	NumberFormat nt = NumberFormat.getPercentInstance();
		nt.setMinimumFractionDigits(2);
		System.out.print("Rule 6: " + num6 + "/" + sum6 + "=" + nt.format(num6 / (double) sum6));
		System.out.print("Rule 7: " + num7 + "/" + sum7 + "=" + nt.format(num7 / (double) sum7));
		System.out.print("Rule 16: " + num16 + "/" + sum16 + "=" + nt.format(num16 / (double) sum16));
		System.out.print("Rule 16-1: " + num16_1 + "/" + sum16 + "=" + nt.format(num16_1 / (double) sum16));
		System.out.print("Rule 16-2: " + num16_2 + "/" + sum16 + "=" + nt.format(num16_2 / (double) sum16));
		System.out.print("Rule 6-null: " + null6 + "/" + sum + "=" + nt.format(null6 / (double) sum));
		System.out.print("Rule 7-null: " + null7 + "/" + sum + "=" + nt.format(null7 / (double) sum));
		System.out.print("Rule 16-null: " + null16 + "/" + sum + "=" + nt.format(null16 / (double) sum));
		System.out.print("Rule 16-ori-err: " + ori_error16 + "/" + sum + "=" + nt.format(ori_error16 / (double) sum));
    }
    
    private void iterateRuleInfoList(String theAirCompany, List<RuleInfo> ruleInfoList) {
    	try {
    		if (ruleInfoList.size() != 0) {
    			int count = 0;
				for (RuleInfo ruleInfo:ruleInfoList) {
                	// Get original RuleInfo Id
					String originalRuleInfoId = ruleInfo.getOriginalRuleInfoId();
					if(hashIdList.contains(originalRuleInfoId))
						continue;
					else
						hashIdList.add(originalRuleInfoId);
					
					// Get airCompany
					String airCompany = ruleInfo.getRuleInfoQuery().getFilingAirline();
					// only XX airCompany
//					if(!airCompany.equals("GA"))
//						continue;
					// exclude XX/YY/ZZ airCompany
//					List<String> banList = new ArrayList<String>(Arrays.asList(
//							"CA"));
//					if(banList.contains(airCompany))
//						continue;
					
					if (count++%100 == 0)
						System.out.print("\n");
					System.out.print(".");
										
					// Get original Rule Text
					OriginalRule originalRule = ComparatorUtil.fetchOriginalRuleById(originalRuleInfoId);
					if (originalRule == null) {
						System.out.println("No this original rule!");
						continue;
					}
					String originalRuleText = originalRule.getText();
					// Get structured Rules
					Map<String,String> structuredRules = ruleInfo.getStructuredRules();
					// Run versus
//					System.out.println(originalRuleInfoId);
					versus(airCompany, originalRuleText, structuredRules);
                }
            }
		} catch (Exception e) {
            log.error(e.getMessage(), e);
        }
	}
	
	public void versus(String airCompany, String ruleDoc, Map<String,String> structuredRules) {
		sum++;
		
		if (ruleDoc == null) {
			log.debug("Rule Text is null!");
			null16++;null6++;null7++;
			return;
		}
		
		Map<Integer,String> rawRuleMap = ComparatorUtil.splitRuleDoc(ruleDoc);
		
		Comparator6 versus6 = new Comparator6();
		Comparator7 versus7 = new Comparator7();
		Comparator16 versus16 = new Comparator16();
		
		// Rule 6
		if (structuredRules.get("MinStay") == null) {
			null6++;
		} else {
			sum6++;
			boolean bool6 = versus6.versus(airCompany, rawRuleMap.get(6), structuredRules.get("MinStay"));
			if (bool6 == true)
				num6++;
			else {
				MinStay minStay1 = Jpecker6.parse(rawRuleMap.get(6));
				Cpecker6 cpecker6 = new Cpecker6();
				cpecker6.parse(structuredRules.get("MinStay"));
				MinStay minStay2 = cpecker6.getMinStay();
				log.debug("\n"+airCompany+"\n6:\n"+rawRuleMap.get(6)+"\n"+minStay1+"\n"+
						structuredRules.get("MinStay")+"\n"+minStay2);
			}
		}
		
		// Rule 7
		if (structuredRules.get("MaxStay") == null) {
			null7++;
		} else {
			sum7++;
			boolean bool7 = versus7.versus(airCompany, rawRuleMap.get(7), structuredRules.get("MaxStay"));				
			if (bool7 == true)
				num7++;
			else {
				MaxStay maxStay1 = Jpecker7.parse(rawRuleMap.get(7));
				Cpecker7 cpecker7 = new Cpecker7();
				cpecker7.parse(structuredRules.get("MaxStay"));
				MaxStay maxStay2 = cpecker7.getMaxStay();
				log.debug("\n"+airCompany+"\n7:\n"+rawRuleMap.get(7)+"\n"+maxStay1+"\n"+
						structuredRules.get("MaxStay")+"\n"+maxStay2);
			}
		}
		
		// Rule 16
		if (structuredRules.get("Penalties-Cancel/Refund") == null
				|| structuredRules.get("Penalties-Change") == null
				|| structuredRules.get("Penalties-Noshow") == null) {
			null16++;
		} else {
			sum16++;
			// List of three booleans, cancel >> change >> noshow
			List<Boolean> bool16 = versus16.versus(airCompany, 
				rawRuleMap.get(16), structuredRules.get("Penalties-Cancel/Refund"),
				structuredRules.get("Penalties-Change"), structuredRules.get("Penalties-Noshow"));
			if (bool16.size() == 0) {
//				log.debug("Error origin?");
				ori_error16++;
				sum16--;
				if(!ComPool16.originErrorMap.containsKey(airCompany))
					ComPool16.originErrorMap.put(airCompany, 1);
				else
					ComPool16.originErrorMap.put(airCompany, ComPool16.originErrorMap.get(airCompany)+1);
				return;
			}		
			if (bool16.get(0) == true) {
				num16_1++;
			} else {
				if(!ComPool16.errorMap1.containsKey(airCompany))
					ComPool16.errorMap1.put(airCompany, 1);
				else
					ComPool16.errorMap1.put(airCompany, ComPool16.errorMap1.get(airCompany)+1);
			}
			if (bool16.get(1) == true) {
				num16_2++;
			} else {
				if(!ComPool16.errorMap2.containsKey(airCompany))
					ComPool16.errorMap2.put(airCompany, 1);
				else
					ComPool16.errorMap2.put(airCompany, ComPool16.errorMap2.get(airCompany)+1);
			}
			if (bool16.get(0) == true && bool16.get(1) == true) {
				num16++;
			} else {
				if(!ComPool16.errorMap.containsKey(airCompany))
					ComPool16.errorMap.put(airCompany, 1);
				else
					ComPool16.errorMap.put(airCompany, ComPool16.errorMap.get(airCompany)+1);
			}
		}
	}
	
}
