package com.travelzen.farerule.translator;

import java.util.List;

import com.travelzen.farerule.MaxStay;
import com.travelzen.farerule.condition.RuleCondition;
import com.travelzen.farerule.rule.MaxStayItem;
import com.travelzen.farerule.translator.tool.ConditionTransducer;
import com.travelzen.farerule.translator.tool.DateTransducer;

public class RuleTranslator7 extends RuleTranslatorBase {

	public static String translate(MaxStay maxStay) {
		StringBuilder sb = new StringBuilder();
		List<MaxStayItem> maxStayItemList = maxStay.getMaxStayItemList();
		if (maxStayItemList == null || maxStayItemList.size() == 0)
			return "无限制。";
		for (MaxStayItem maxStayItem:maxStayItemList) {
			RuleCondition ruleCondition = maxStayItem.getRuleCondition();
			if (ruleCondition != null) {
				sb.append(translateRuleCondition(ruleCondition));
			}
			if (maxStayItem.getStayTimeType() != null) {
				sb.append("最长停留");
				sb.append(maxStayItem.getStayTimeNum() + ConditionTransducer.stayTimeTypeToString(maxStayItem.getStayTimeType()));
			}
			if (maxStayItem.getDate() != 0) {
				if (maxStayItem.getStayTimeType() != null)
					sb.append("，或");
				sb.append("最长停留至").append(DateTransducer.longDateToString(maxStayItem.getDate()));
			}
			sb.append("。\n");
		}
		if (sb.toString().endsWith("\n"))
			sb.replace(sb.length()-1, sb.length(), "");
		return sb.toString();
	}
}
