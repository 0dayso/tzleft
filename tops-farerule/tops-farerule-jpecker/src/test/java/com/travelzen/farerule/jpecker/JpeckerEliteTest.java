/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.jpecker;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.fare.RuleInfo;
import com.travelzen.fare.dandelion.ServiceProviderFactory;
import com.travelzen.fare.dandelion.service.FareRuleDBService;
import com.travelzen.fare.dandelion.service.ServiceProvider;
import com.travelzen.farerule.TzRule;
import com.travelzen.farerule.jpecker.server.DisplayRule;
import com.travelzen.farerule.mongo.morphia.RuleInfoMorphia;
import com.travelzen.farerule.translator.RuleTranslator;

@RunWith(JUnit4.class)
public class JpeckerEliteTest {
	
	public JpeckerEliteTest() {
		LogBase.logBack();
	}

	private static final Logger log = LoggerFactory.getLogger(JpeckerEliteTest.class);
	
	@Test
	public void testSelectedOnes() {
		RuleInfoMorphia ruleInfoMorphia = RuleInfoMorphia.Instance;
		RuleInfo ruleInfo = ruleInfoMorphia.findByRuleInfoId("e5287147cd2cdf8f48b90dbf88d5f9a6");
		System.out.println(ruleInfo);
		List<RuleInfo> ruleInfoList = new ArrayList<RuleInfo>();
		ruleInfoList.add(ruleInfo);
		JpeckerElite jr = new JpeckerElite();
		jr.processToTzRule(ruleInfoList);
		TzRule tzRule = jr.getTzRule();
		System.out.println(tzRule);
		DisplayRule displayRule = RuleTranslator.translate(tzRule);
		System.out.println(displayRule.getMinStay());
		System.out.println(displayRule.getMaxStay());
		System.out.println(displayRule.getTravelDate());
		System.out.println(displayRule.getPenalties());
	}

	@Ignore
	@Test
	public void testSomeOnes() {
		System.out.println("Start fetch rules...");
		List<RuleInfo> ruleInfoList = fetchRuleInfoList(0);
		System.out.println(ruleInfoList.size());
		System.out.println("Get particular rules...");
		List<RuleInfo> neededRuleInfoList = touchRuleInfoList(ruleInfoList);
		System.out.println(neededRuleInfoList.size());
		System.out.println("Jpecker start...");
		JpeckerElite jr = new JpeckerElite();
		jr.processToTzRule(neededRuleInfoList);
		TzRule tzRule = jr.getTzRule();
		System.out.println(tzRule.toString());
		DisplayRule displayRule = RuleTranslator.translate(tzRule);
		System.out.println(displayRule.getMinStay());
		System.out.println(displayRule.getMaxStay());
		System.out.println(displayRule.getTravelDate());
		System.out.println(displayRule.getPenalties());
	}

	private List<RuleInfo> touchRuleInfoList(List<RuleInfo> ruleInfoList) {
		List<RuleInfo> neededRuleInfoList = new ArrayList<RuleInfo>();
		try {
			if (ruleInfoList.size() > 3) {
				for (int i = 10; i < 49; i++) {
					RuleInfo ruleInfo = ruleInfoList.get(i);
					neededRuleInfoList.add(ruleInfo);
				}
//				neededRuleInfoList.add(ruleInfoList.get(11));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return neededRuleInfoList;
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
}