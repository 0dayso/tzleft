package com.travelzen.farerule.comparator;

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
import com.travelzen.fare.dandelion.ServiceProviderFactory;
import com.travelzen.fare.dandelion.service.FareRuleDBService;
import com.travelzen.fare.dandelion.service.ServiceProvider;
import com.travelzen.farerule.mongo.OriginalRule;
import com.travelzen.farerule.mongo.morphia.OriginalRuleMorphia;

public class ComparatorUtil {
	
	private static final Logger log = LoggerFactory.getLogger(ComparatorUtil.class);
	
	public static List<OriginalRule> fetchOriginalRuleList() {
		OriginalRuleMorphia morphia = OriginalRuleMorphia.Instance;
		List<OriginalRule> originalRuleList = morphia.findAll();
		return originalRuleList;
	}
	
	public static OriginalRule fetchOriginalRuleById(String id) {
		OriginalRuleMorphia morphia = OriginalRuleMorphia.Instance;
		OriginalRule originalRule = morphia.findById(id);
		return originalRule;
	}

	public static Map<Integer,String> splitRuleDoc(String ruleDoc) {
		Map<Integer,String> rawRuleMap = new HashMap<Integer,String>();
		
		String startAnchor = "\n(\\d{2})\\.[A-Z]";
		String endAnchor = "(?=\n\\d{2}\\.[A-Z]|\\*+ *END *\\*+)"; // '|$' produce massive sums of 'null'
		
		Pattern pattern = Pattern.compile(startAnchor + "[\\w\\W]+?" + endAnchor);
		Matcher matcher = pattern.matcher(ruleDoc);
		while (matcher.find()) {
			List<String> list = new ArrayList<String>(Arrays.asList
				("01","02","03","04","05","06","07","11","14","15","16","19"));
			if (!list.contains(matcher.group(1))) {
				continue;
			}
			int ruleIndex = Integer.parseInt(matcher.group(1));
			String ruleText = matcher.group(0).substring(matcher.group(0).indexOf("\n", 1) + 1);
			rawRuleMap.put(ruleIndex, ruleText);
		}
		return rawRuleMap;
	}
    
	public static List<RuleInfo> fetchRuleInfoList(long updateTime) {
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
