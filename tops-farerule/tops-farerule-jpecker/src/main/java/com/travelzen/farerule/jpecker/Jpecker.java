/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.jpecker;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.slf4j.Logger;

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
import com.travelzen.farerule.jpecker.server.DisplayRule;
import com.travelzen.farerule.mongo.OriginalRule;
import com.travelzen.farerule.mongo.morphia.OriginalRuleMorphia;
import com.travelzen.farerule.mongo.morphia.TzRuleMorphia;
import com.travelzen.farerule.rule.MaxStayItem;
import com.travelzen.farerule.rule.MinStayItem;
import com.travelzen.farerule.rule.PenaltiesItem;
import com.travelzen.farerule.rule.TravelDateItem;
import com.travelzen.farerule.translator.RuleTranslator;
import com.travelzen.framework.logger.core.ri.RequestIdentityLogger;

public class Jpecker {
	
	private static final Logger logger = RequestIdentityLogger.getLogger(Jpecker.class);
	
	private static OriginalRuleMorphia originalRuleMorphia;
	private static TzRuleMorphia tzRuleMorphia;
	
	static {
		originalRuleMorphia = OriginalRuleMorphia.Instance;
		tzRuleMorphia = TzRuleMorphia.Instance;
	}

	private TzRule finalTzRule;
	private DisplayRule displayRule;
	
	public Jpecker() {
		finalTzRule = null;
		displayRule = null;
	}
	
	public TzRule getTzRule() {
		return finalTzRule;
	}
	
	public DisplayRule getDisplayRule() {
		displayRule = RuleTranslator.translate(finalTzRule);
		return displayRule;
	}
	
	public void peck(List<String> idList) {
		logger.info("Jpecker解析开始");
		try {
			if (idList == null || idList.size() == 0)
				logger.warn("请求运价规则解析的idList为空！");
			else if (idList.size() == 1)
				finalTzRule = peckOneway(idList.get(0));
			else
				finalTzRule = peckMulti(idList);
		} catch (Exception e) {
			logger.error("Jpecker解析异常", e);
		}
		logger.info("Jpecker解析结束");
	}
	
	private TzRule peckOneway(String id) {
		logger.info("请求单程运价规则：{}", id);
		TzRule tzRule = tzRuleMorphia.findByJpeckerId(id);
		if (tzRule != null) {
			logger.info("从运价规则数据库中取出解析结果：{}", id);
			return tzRule;
		}
		OriginalRule originalRule = originalRuleMorphia.findById(id);
		if (originalRule == null) {
			logger.warn("异常！获取原始文本失败：{}", id);
			return null;
		}
		tzRule = peckText(originalRule);
		logger.info("Jpecker单程运价规则解析结果：{}", tzRule);
		return tzRule;
	}

	private TzRule peckMulti(List<String> idList) {
		logger.info("请求多程运价规则：{}", idList);
		TzRule tzRule = new TzRule();
		String ids = "";
		List<MinStayItem> minStayItemList = new ArrayList<MinStayItem>();
		List<MaxStayItem> maxStayItemList = new ArrayList<MaxStayItem>();
		List<TravelDateItem> travelDateItemList = new ArrayList<TravelDateItem>();
		List<PenaltiesItem> penaltiesItemList = new ArrayList<PenaltiesItem>();
		for (int i=0; i<idList.size(); i++) {
			String id = idList.get(i);
			if (ids.equals(""))
				ids = id;
			else
				ids = ids + "," + id;
			TzRule oneTzRule = tzRuleMorphia.findByJpeckerId(id);
			if (oneTzRule != null) {
				logger.info("从运价规则数据库中取出解析结果：{}, {}", id, oneTzRule);
			} else {
				OriginalRule originalRule = originalRuleMorphia.findById(id);
				if (originalRule == null) {
					logger.warn("异常！获取原始文本失败：{}", id);
					continue;
				}
				oneTzRule = peckText(originalRule);
				logger.info("解析运价规则得到解析结果：{}, {}", id, oneTzRule);
			}
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
			if (oneTzRule.getPenalties() != null && oneTzRule.getPenalties().getPenaltiesItemList() != null)
				penaltiesItemList.addAll(PenaltiesMerger.merge(oneTzRule.getPenalties().getPenaltiesItemList()));
		}
		tzRule.setTzRuleInfo(new TzRuleInfo().setRuleSource(RuleSourceEnum.JPECKER).setJpeckerRuleId(ids))
			.setMinStay(new MinStay().setMinStayItemList(StayMerger.minMerge(minStayItemList)))
			.setMaxStay(new MaxStay().setMaxStayItemList(StayMerger.maxMerge(maxStayItemList)))
			.setTravelDate(new TravelDate().setTravelDateItemList(travelDateItemList))
			.setPenalties(new Penalties().setPenaltiesItemList(PenaltiesMerger.merge(penaltiesItemList)));
		logger.info("Jpecker多程运价规则解析结果：{}", tzRule);
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
		tzRuleMorphia.save(oneTzRule);
		return oneTzRule;
	}
}
