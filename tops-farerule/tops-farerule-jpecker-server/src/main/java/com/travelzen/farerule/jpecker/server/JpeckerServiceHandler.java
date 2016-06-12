package com.travelzen.farerule.jpecker.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.thrift.TException;
import org.slf4j.Logger;

import com.travelzen.fare.RuleInfo;
import com.travelzen.farerule.TzRule;
import com.travelzen.farerule.exchange.ExchangeFetcher;
import com.travelzen.farerule.jpecker.Jpecker;
import com.travelzen.farerule.jpecker.nest.LocationNest;
import com.travelzen.farerule.mongo.OriginalRule;
import com.travelzen.farerule.mongo.morphia.OriginalRuleMorphia;
import com.travelzen.farerule.mongo.morphia.RuleInfoMorphia;
import com.travelzen.farerule.mongo.morphia.TzRuleMorphia;
import com.travelzen.farerule.translator.RuleTranslator14;
import com.travelzen.farerule.translator.RuleTranslator16;
import com.travelzen.farerule.translator.RuleTranslator6;
import com.travelzen.farerule.translator.RuleTranslator7;
import com.travelzen.framework.logger.core.ri.RequestIdentityLogger;

public class JpeckerServiceHandler implements JpeckerService.Iface {
	
	private static final Logger logger = RequestIdentityLogger.getLogger(JpeckerServiceHandler.class);
	
	private static OriginalRuleMorphia originalRuleMorphia;
    private static TzRuleMorphia tzRuleMorphia;
    private static RuleInfoMorphia ruleInfoMorphia;
    
    private static ExchangeFetcher exchangeFetcher;
    
    static {
        try {
            originalRuleMorphia = OriginalRuleMorphia.Instance;
            tzRuleMorphia = TzRuleMorphia.Instance;
            ruleInfoMorphia = RuleInfoMorphia.Instance;
            LocationNest.nest();
			exchangeFetcher = new ExchangeFetcher();
			exchangeFetcher.start();
        } catch (Exception e) {
            logger.error("数据库连接失败" + e.getMessage(), e);
        }
    }
    
	@Override
	public DisplayRule fareRulePeck(List<String> idList) throws TException {
		logger.info("运价规则解析开始");
		Jpecker jpecker = new Jpecker();
		jpecker.peck(idList);
		DisplayRule displayRule = jpecker.getDisplayRule();
		logger.info("运价规则解析结束");
		return displayRule;
	}
	
	@Override
	public List<String> fetchOriginalRule(String id) throws TException {
		logger.info("运价规则原始文本查询开始");
		List<String> ruleTextList = new ArrayList<String>();
        if (id == null || id.length() == 0) {
        	logger.info("请求的id为空！");
        	logger.info("运价规则原始文本查询结束");
        	return ruleTextList;
        }
        String[] ids = id.split(",");
        for (String i:ids) {
        	ruleTextList.addAll(getOriginalRule(i));
        }
        logger.info("运价规则原始文本查询结束");
        return ruleTextList;
	}
	
	private List<String> getOriginalRule(String originalRuleInfoId) {
        try {
        	List<String> tmpTextList = new ArrayList<String>();
            if (originalRuleMorphia.findById(originalRuleInfoId) != null) {
            	tmpTextList.add("<b>ID:</b>" + originalRuleInfoId + "\n");
            	logger.info("开始生成原始文本");
            	String originalRuleText = createOriginalRuleText(originalRuleInfoId);
            	tmpTextList.add(originalRuleText);
            	logger.info("开始生成jpecker结果");
            	String jpeckerRuleText = createJpeckerRuleText(originalRuleInfoId);
            	tmpTextList.add(jpeckerRuleText);
            	logger.info("开始生成ibeplus结果");
            	RuleInfo ruleInfo = ruleInfoMorphia.findByOriginalRuleId(originalRuleInfoId);
            	if (ruleInfo != null) {
            		String ibeplusRuleText = createIbeplusRuleText(ruleInfo.getStructuredRules());
                	tmpTextList.add(ibeplusRuleText);
            	} else {
            		logger.info("原始文本查询异常！RuleInfo查询失败！");
            		tmpTextList.add("RuleInfo查询失败。");
            	}
                return tmpTextList;
            } else {
            	logger.info("原始文本查询异常！id不存在：" + originalRuleInfoId);
            	return tmpTextList;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
    
    private String createOriginalRuleText(String originalRuleInfoId) {
		StringBuilder jpeckerRuleText = new StringBuilder();
		OriginalRule originalRule = originalRuleMorphia.findById(originalRuleInfoId);
		Map<Integer, String> originalRuleMap = splitRuleDoc(originalRule.getText());
		jpeckerRuleText.append("<b>航司: </b>" + originalRule.getAirCompany())
			.append("\n<b>运价规则原始文本</b>")
			.append("\nRULE6:\n").append(originalRuleMap.get(6))
			.append("\nRULE7:\n").append(originalRuleMap.get(7))
			.append("\nRULE14:\n").append(originalRuleMap.get(14))
			.append("\nRULE16:\n").append(originalRuleMap.get(16)).append("\n");
		return jpeckerRuleText.toString();
	}
	
	private String createJpeckerRuleText(String originalRuleInfoId) {
		TzRule tzRule = tzRuleMorphia.findByJpeckerId(originalRuleInfoId);
		if (tzRule == null)
			return null;
		StringBuilder jpeckerRuleText = new StringBuilder();
		jpeckerRuleText.append("<b>jpecker运价解析结果</b>")
			.append("\nRULE6:\n").append(RuleTranslator6.translate(tzRule.getMinStay()))
			.append("\n\nRULE7:\n").append(RuleTranslator7.translate(tzRule.getMaxStay()))
			.append("\n\nRULE14:\n").append(RuleTranslator14.translate(tzRule.getTravelDate()))
			.append("\n\nRULE16:\n").append(RuleTranslator16.translate(tzRule.getPenalties())).append("\n");
		return jpeckerRuleText.toString();
	}
	
	private String createIbeplusRuleText(Map<String,String> ibeplusRuleMap) {
		String ibeplusRuleText = "";
		if (ibeplusRuleMap.get("Penalties-Cancel/Refund") != null
				&& ibeplusRuleMap.get("Penalties-Cancel/Refund") != null
				&& ibeplusRuleMap.get("Penalties-Cancel/Refund") != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("<b>ibeplus运价结果</b>")
				.append("\n最短停留:\n").append(ibeplusRuleMap.get("MinStay"))
				.append("\n\n最长停留:\n").append(ibeplusRuleMap.get("MaxStay"))
				.append("\n\n退改签:")
				.append("\n退票:\n").append(ibeplusRuleMap.get("Penalties-Cancel/Refund"))
				.append("\n改签:\n").append(ibeplusRuleMap.get("Penalties-Change"))
				.append("\n误机:\n").append(ibeplusRuleMap.get("Penalties-Noshow"));
			sb.append("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n\n");
			ibeplusRuleText =  sb.toString();
		} else {
			ibeplusRuleText = "ibeplus退改签结果为空。";
		}
		return ibeplusRuleText;
	}
	
	private Map<Integer, String> splitRuleDoc(String ruleDoc) {
        Map<Integer, String> rawRuleMap = new HashMap<Integer, String>();

        String startAnchor = "\n(\\d{2})\\.[A-Z]";
        String endAnchor = "(?=\n\\d{2}\\.[A-Z]|\\*+ *END *\\*+)";

        Pattern pattern = Pattern.compile(startAnchor + "[\\w\\W]+?" + endAnchor);
        Matcher matcher = pattern.matcher(ruleDoc);
        while (matcher.find()) {
            List<String> list = new ArrayList<String>(Arrays.asList(
            		"01", "02", "03", "04", "05", "06", "07", "11", "14", "15", "16", "19"));
            if (!list.contains(matcher.group(1))) {
                continue;
            }
            int ruleIndex = Integer.parseInt(matcher.group(1));
            String ruleText = matcher.group(0).substring(matcher.group(0).indexOf("\n", 1) + 1);
            rawRuleMap.put(ruleIndex, ruleText);
        }
        return rawRuleMap;
    }

}
