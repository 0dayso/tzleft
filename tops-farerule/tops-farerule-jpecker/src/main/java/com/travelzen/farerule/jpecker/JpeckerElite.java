package com.travelzen.farerule.jpecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.fare.RuleInfo;
import com.travelzen.farerule.jpecker.merger.PenaltiesMerger;
import com.travelzen.farerule.jpecker.merger.StayMerger;
import com.travelzen.farerule.jpecker.pecker.Jpecker14;
import com.travelzen.farerule.jpecker.pecker.Jpecker16;
import com.travelzen.farerule.jpecker.pecker.Jpecker6;
import com.travelzen.farerule.jpecker.pecker.Jpecker7;
import com.travelzen.farerule.mongo.OriginalRule;
import com.travelzen.farerule.mongo.morphia.OriginalRuleMorphia;
import com.travelzen.farerule.mongo.morphia.TzRuleMorphia;
import com.travelzen.farerule.rule.MaxStayItem;
import com.travelzen.farerule.rule.MinStayItem;
import com.travelzen.farerule.rule.PenaltiesItem;
import com.travelzen.farerule.rule.TravelDateItem;
import com.travelzen.farerule.MaxStay;
import com.travelzen.farerule.MinStay;
import com.travelzen.farerule.Penalties;
import com.travelzen.farerule.RuleSourceEnum;
import com.travelzen.farerule.TravelDate;
import com.travelzen.farerule.TzRule;
import com.travelzen.farerule.TzRuleInfo;

public class JpeckerElite {
	
	private static final Logger logger = LoggerFactory.getLogger(JpeckerElite.class);
	
	private static OriginalRuleMorphia originalRuleMorphia;
	private static TzRuleMorphia tzRuleMorphia;
	
	static {
		originalRuleMorphia = OriginalRuleMorphia.Instance;
		tzRuleMorphia = TzRuleMorphia.Instance;
	}
	
	private TzRule tzRule;
	
	public JpeckerElite() {
		tzRule = new TzRule();
	}
	
	public TzRule getTzRule() {
		return tzRule;
	}
	
	public void processToTzRule(List<RuleInfo> ruleInfoList) {
		logger.info("Jpecker解析开始");
		if (ruleInfoList == null || ruleInfoList.size() == 0) {
			logger.info("请求航线为空，ruleInfoList为空！");
			tzRule = null;
			logger.info("Jpecker解析结束");
			return;
		}
		MinStay minStay = new MinStay();
		MaxStay maxStay = new MaxStay();
		TravelDate travelDate = new TravelDate();
		Penalties penalties = new Penalties();
		TzRuleInfo tzRuleInfo = new TzRuleInfo().setRuleSource(RuleSourceEnum.JPECKER);
		if (ruleInfoList.size() == 1) {
			logger.info("jpecker获得请求ruleInfoList.size() == 1");
			RuleInfo ruleInfo = ruleInfoList.get(0);
			tzRuleInfo.setJpeckerRuleId(ruleInfo.getOriginalRuleInfoId());
			String airCompany = "";
			if (ruleInfo.getRuleInfoQuery() != null)
				airCompany = ruleInfo.getRuleInfoQuery().getFilingAirline();
			else
				logger.info("异常：ruleInfo.getRuleInfoQuery()为空！");
			String originalRuleInfoId = ruleInfo.getOriginalRuleInfoId();
			// if already in database
			if (tzRuleMorphia.findByJpeckerId(originalRuleInfoId) != null) {
				tzRule = tzRuleMorphia.findByJpeckerId(originalRuleInfoId);
				logger.info("从运价规则数据库中取出解析结果：" + originalRuleInfoId);
				logger.info("Jpecker解析结束");
				return;
			}
			String originalRuleText = fetchOriginalRuleText(originalRuleInfoId);
			if (originalRuleText == null) {
				logger.info("异常：获取原始文本失败！ " + originalRuleInfoId);
				logger.info("Jpecker解析结束");
				return;
			}
			Map<Integer,String> rawRuleMap = splitRuleDoc(originalRuleText);
			// Process rule 6
			minStay = Jpecker6.parse(rawRuleMap.get(6));
			// Process rule 7
			maxStay = Jpecker7.parse(rawRuleMap.get(7));
			// Process rule 14
			travelDate = Jpecker14.parse(rawRuleMap.get(14));
			// Process rule 16
			Jpecker16 jpecker16 = new Jpecker16(airCompany);
			jpecker16.parse(rawRuleMap.get(16));
			penalties = jpecker16.getPenalties();
			tzRule.setTzRuleInfo(tzRuleInfo);
			tzRule.setMinStay(minStay);
			tzRule.setMaxStay(maxStay);
			tzRule.setTravelDate(travelDate);
			tzRule.setPenalties(jpecker16.getPenalties());
			// Save TzRule to mongodb
			tzRuleMorphia.save(tzRule);
		} else {
			logger.info("jpecker获得请求ruleInfoList.size() > 1");
			String ids = "";
			List<MinStayItem> minStayItemList = new ArrayList<MinStayItem>();
			List<MaxStayItem> maxStayItemList = new ArrayList<MaxStayItem>();
			List<TravelDateItem> travelDateItemList = new ArrayList<TravelDateItem>();
			List<PenaltiesItem> penaltiesItemList = new ArrayList<PenaltiesItem>();
			int segmentNum = 1;
			for (RuleInfo ruleInfo:ruleInfoList) {
				if (ids.equals(""))
					ids = ruleInfo.getOriginalRuleInfoId();
				else
					ids = ids + "," + ruleInfo.getOriginalRuleInfoId();
				String airCompany = "";
				if (ruleInfo.getRuleInfoQuery() != null)
					airCompany = ruleInfo.getRuleInfoQuery().getFilingAirline();
				else
					logger.info("异常：ruleInfo.getRuleInfoQuery()为空");
				String originalRuleInfoId = ruleInfo.getOriginalRuleInfoId();
				// if already in database
				TzRule existingTzRule = null;
				boolean isRuleExisted = false;
				if (tzRuleMorphia.findByJpeckerId(originalRuleInfoId) != null) {
					existingTzRule = tzRuleMorphia.findByJpeckerId(originalRuleInfoId);
					isRuleExisted = true;
					logger.info("从运价规则数据库中取出解析结果：" + originalRuleInfoId);
				}
				String originalRuleText = null;
				Map<Integer,String> rawRuleMap = null;
				if (!isRuleExisted) {
					originalRuleText = fetchOriginalRuleText(originalRuleInfoId);
					if (originalRuleText == null) {
						logger.info("异常：获取原始文本失败！ " + originalRuleInfoId);
						continue;
					}
					rawRuleMap = splitRuleDoc(originalRuleText);
				}
				MinStay tmpMinStay = null;
				MaxStay tmpMaxStay = null;
				TravelDate tmpTravelDate = null;
				Penalties tmpPenalties = null;
				// Process 6
				if (isRuleExisted) {
					tmpMinStay = existingTzRule.getMinStay();
				} else {
					tmpMinStay = Jpecker6.parse(rawRuleMap.get(6));
				}
				if (tmpMinStay.getMinStayItemList() != null)
					minStayItemList.addAll(tmpMinStay.getMinStayItemList());
				// Process 7
				if (isRuleExisted) {
					tmpMaxStay = existingTzRule.getMaxStay();
				} else {
					tmpMaxStay = Jpecker7.parse(rawRuleMap.get(7));
				}
				if (tmpMaxStay.getMaxStayItemList() != null)
					maxStayItemList.addAll(tmpMaxStay.getMaxStayItemList());
				// Process 14
				if (isRuleExisted) {
					tmpTravelDate = existingTzRule.getTravelDate();
				} else {
					tmpTravelDate = Jpecker14.parse(rawRuleMap.get(14));
				}
				if (tmpTravelDate.getTravelDateItemList() != null && tmpTravelDate.getTravelDateItemList().size() != 0) {
					for (TravelDateItem travelDateItem:tmpTravelDate.getTravelDateItemList()) {
						TravelDateItem tmpTravelDateItem = new TravelDateItem();
						tmpTravelDateItem.setSegmentNum(segmentNum)
							.setOriginCondition(travelDateItem.getOriginCondition())
							.setTravelDateSubItemList(travelDateItem.getTravelDateSubItemList())
							.setCompleteDate(travelDateItem.getCompleteDate());
						travelDateItemList.add(tmpTravelDateItem);
					}
				}
				segmentNum++;
				// Process rule 16
				if (isRuleExisted) {
					tmpPenalties = existingTzRule.getPenalties();
				} else {
					Jpecker16 jpecker16 = new Jpecker16(airCompany);
					jpecker16.parse(rawRuleMap.get(16));
					tmpPenalties = jpecker16.getPenalties();
				}
				if (tmpPenalties != null)
					penaltiesItemList.addAll(PenaltiesMerger.merge(tmpPenalties.getPenaltiesItemList()));
				if (!isRuleExisted) {
					// Save TzRule to mongodb
					TzRule tmpTzRule = new TzRule();
					tmpTzRule.setTzRuleInfo(
							new TzRuleInfo().setRuleSource(RuleSourceEnum.JPECKER).setJpeckerRuleId(ruleInfo.getOriginalRuleInfoId()));
					tmpTzRule.setMinStay(tmpMinStay);
					tmpTzRule.setMaxStay(tmpMaxStay);
					tmpTzRule.setTravelDate(tmpTravelDate);
					tmpTzRule.setPenalties(tmpPenalties);
					tzRuleMorphia.save(tmpTzRule);
				}
            }
			minStay.setMinStayItemList(StayMerger.minMerge(minStayItemList));
			maxStay.setMaxStayItemList(StayMerger.maxMerge(maxStayItemList));
			travelDate.setTravelDateItemList(travelDateItemList);
			penalties.setPenaltiesItemList(PenaltiesMerger.merge(penaltiesItemList));
			tzRuleInfo.setJpeckerRuleId(ids);
			tzRule.setTzRuleInfo(tzRuleInfo);
			tzRule.setMinStay(minStay);
			tzRule.setMaxStay(maxStay);
			tzRule.setTravelDate(travelDate);
			tzRule.setPenalties(penalties);
        }
		logger.info("Jpecker解析结束");
	}
	
	private String fetchOriginalRuleText(String originalRuleInfoId) {
        OriginalRule originalRule = originalRuleMorphia.findById(originalRuleInfoId);
        if (originalRule == null)
        	return null;
        return originalRule.getText();
    }
    
    private Map<Integer,String> splitRuleDoc(String ruleDoc) {
		Map<Integer,String> rawRuleMap = new HashMap<Integer,String>();
		
		String startAnchor = "\n(\\d{2})\\.[A-Z]";
		String endAnchor = "(?=\\*+ END \\*+)";
		
		Pattern pattern = Pattern.compile(startAnchor + "[\\w\\W]+?" + endAnchor);
		Matcher matcher = pattern.matcher(ruleDoc);
		while (matcher.find()) {
			List<String> list = new ArrayList<String>(Arrays.asList
				("06","07","14","16"));
			if (!list.contains(matcher.group(1))) {
				continue;
			}
			int ruleIndex = Integer.parseInt(matcher.group(1));
			String ruleText = matcher.group(0).substring(matcher.group(0).indexOf("\n", 1) + 1);
			ruleText = ruleText.replaceAll("\n\\*{2} {2}", "\n");
			rawRuleMap.put(ruleIndex, ruleText);
		}
		return rawRuleMap;
	}
}
