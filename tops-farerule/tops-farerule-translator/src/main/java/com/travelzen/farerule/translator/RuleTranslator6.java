package com.travelzen.farerule.translator;

import java.util.List;

import com.travelzen.farerule.MinStay;
import com.travelzen.farerule.condition.RuleCondition;
import com.travelzen.farerule.rule.MinStayItem;
import com.travelzen.farerule.translator.tool.ConditionTransducer;
import com.travelzen.farerule.translator.tool.DateTransducer;

public class RuleTranslator6 extends RuleTranslatorBase {

	public static String translate(MinStay minStay) {
		StringBuilder sb = new StringBuilder();
		List<MinStayItem> minStayItemList = minStay.getMinStayItemList();
		if (minStayItemList == null || (minStayItemList.size() == 0 && !minStay.isSetAcrossWeekend()))
			return "无限制。";
		for (MinStayItem minStayItem:minStayItemList) {
			RuleCondition ruleCondition = minStayItem.getRuleCondition();
			if (ruleCondition != null) {
				sb.append(translateRuleCondition(ruleCondition));
			}
			if (minStayItem.getStayTimeType() != null) {
				sb.append("最短停留");
				sb.append(minStayItem.getStayTimeNum() + ConditionTransducer.stayTimeTypeToString(minStayItem.getStayTimeType()));
			}
			if (minStayItem.getWeekday() != null) {
				if (minStayItem.getStayTimeType() != null)
					sb.append("，或");
				sb.append("至少停留至出发后的第一个").append(DateTransducer.weekEnumToCn(minStayItem.getWeekday()));
			}
			sb.append("。\n");
		}
		if (minStay.isSetAcrossWeekend() && minStay.isAcrossWeekend()) {
			sb.append("必须跨周末。\n");
		}
		if (sb.toString().endsWith("\n"))
			sb.replace(sb.length()-1, sb.length(), "");
		return sb.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(RuleTranslator6.translate(new MinStay().setAcrossWeekend(true)));
	}
	
}
