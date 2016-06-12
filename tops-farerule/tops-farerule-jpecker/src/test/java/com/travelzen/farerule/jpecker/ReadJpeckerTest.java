/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.jpecker;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;

import com.travelzen.farerule.MaxStay;
import com.travelzen.farerule.MinStay;
import com.travelzen.farerule.Penalties;
import com.travelzen.farerule.RuleSourceEnum;
import com.travelzen.farerule.TravelDate;
import com.travelzen.farerule.TzRule;
import com.travelzen.farerule.TzRuleInfo;
import com.travelzen.farerule.jpecker.merger.PenaltiesMerger;
import com.travelzen.farerule.jpecker.merger.StayMerger;
import com.travelzen.farerule.jpecker.pecker.Jpecker14;
import com.travelzen.farerule.jpecker.pecker.Jpecker16;
import com.travelzen.farerule.jpecker.pecker.Jpecker6;
import com.travelzen.farerule.jpecker.pecker.Jpecker7;
import com.travelzen.farerule.mongo.OriginalRule;
import com.travelzen.farerule.rule.MaxStayItem;
import com.travelzen.farerule.rule.MinStayItem;
import com.travelzen.farerule.rule.PenaltiesItem;
import com.travelzen.farerule.rule.TravelDateItem;

@RunWith(JUnit4.class)
public class ReadJpeckerTest {

	@Test
	public void testOneFile() {
		StringBuilder sb = new StringBuilder();
		String line = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader("data/sample.txt"));
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		OriginalRule originalRule = new OriginalRule();
		originalRule.setAirCompany("FM");
		originalRule.setText(sb.toString());
		TzRule oneTzRule = peckText(originalRule);
		
		List<TzRule> oneTzRules = new ArrayList<>();
		oneTzRules.add(oneTzRule);
		peckMulti(oneTzRules);
	}
	
	private TzRule peckMulti(List<TzRule> oneTzRules) {
		TzRule tzRule = new TzRule();
		String ids = "";
		List<MinStayItem> minStayItemList = new ArrayList<MinStayItem>();
		List<MaxStayItem> maxStayItemList = new ArrayList<MaxStayItem>();
		List<TravelDateItem> travelDateItemList = new ArrayList<TravelDateItem>();
		List<PenaltiesItem> penaltiesItemList = new ArrayList<PenaltiesItem>();
		int i = 0;
		for (TzRule oneTzRule:oneTzRules) {
			if (oneTzRule.getMinStay() != null && oneTzRule.getMinStay().getMinStayItemList() != null)
				minStayItemList.addAll(oneTzRule.getMinStay().getMinStayItemList());
			if (oneTzRule.getMaxStay() != null && oneTzRule.getMaxStay().getMaxStayItemList() != null)
				maxStayItemList.addAll(oneTzRule.getMaxStay().getMaxStayItemList());
			if (oneTzRule.getTravelDate() != null && oneTzRule.getTravelDate().getTravelDateItemList() != null) {
				for (TravelDateItem travelDateItem:oneTzRule.getTravelDate().getTravelDateItemList()) {
					TravelDateItem tmpTravelDateItem = new TravelDateItem();
					tmpTravelDateItem.setSegmentNum(i+1)
						.setOriginCondition(travelDateItem.getOriginCondition())
						.setTravelDateSubItemList(travelDateItem.getTravelDateSubItemList())
						.setCompleteDate(travelDateItem.getCompleteDate());
					travelDateItemList.add(tmpTravelDateItem);
				}
			}
			if (oneTzRule.getPenalties() != null)
				penaltiesItemList.addAll(PenaltiesMerger.merge(oneTzRule.getPenalties().getPenaltiesItemList()));
			i++;
		}
		tzRule.setTzRuleInfo(new TzRuleInfo().setRuleSource(RuleSourceEnum.JPECKER).setJpeckerRuleId(ids))
			.setMinStay(new MinStay().setMinStayItemList(StayMerger.minMerge(minStayItemList)))
			.setMaxStay(new MaxStay().setMaxStayItemList(StayMerger.maxMerge(maxStayItemList)))
			.setTravelDate(new TravelDate().setTravelDateItemList(travelDateItemList))
			.setPenalties(new Penalties().setPenaltiesItemList(PenaltiesMerger.merge(penaltiesItemList)));
		return tzRule;
	}
	
	private TzRule peckText(OriginalRule originalRule) {
		String startAnchor = "\n(\\d{2})\\.[A-Z]";
		String endAnchor = "(?=\\*+ END \\*)"; // '|$' produce massive sums of 'null'
		Pattern pattern = Pattern.compile(startAnchor + "[A-Z -/]+(?<text>\n[\\w\\W]+?)" + endAnchor);
		Matcher matcher = pattern.matcher(originalRule.getText());
		Map<Integer, String> textMap = new HashMap<Integer, String>();
		while (matcher.find()) {
			List<String> list = new ArrayList<String>(Arrays.asList
				("06","07","14","16"));
			if (!list.contains(matcher.group(1)))
				continue;
			int ruleIndex = Integer.parseInt(matcher.group(1));
			// 乱码: '<<  '
			String ruleText = matcher.group("text").replaceAll("<<  ", "");
			textMap.put(ruleIndex, ruleText);
		}
		TzRule oneTzRule = new TzRule();
		oneTzRule.setMinStay(Jpecker6.parse(textMap.get(6)));
		oneTzRule.setMaxStay(Jpecker7.parse(textMap.get(7)));
		oneTzRule.setTravelDate(Jpecker14.parse(textMap.get(14)));
		Jpecker16 jp16 = new Jpecker16(originalRule.getAirCompany());
		jp16.parse(textMap.get(16));
		oneTzRule.setPenalties(jp16.getPenalties());
		oneTzRule.setTzRuleInfo(new TzRuleInfo()
				.setRuleSource(RuleSourceEnum.JPECKER)
				.setJpeckerRuleId(originalRule.getId()));
		// Save TzRule to mongodb
//		tzRuleMorphia.save(oneTzRule);
		return oneTzRule;
	}
}
