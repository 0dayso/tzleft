package com.travelzen.farerule.translator;

import com.travelzen.farerule.condition.OriginCondition;
import com.travelzen.farerule.condition.OriginTypeEnum;
import com.travelzen.farerule.condition.RuleCondition;
import com.travelzen.farerule.condition.SalesDateSubItem;
import com.travelzen.farerule.condition.TravelDateSubItem;
import com.travelzen.farerule.translator.tool.ConditionTransducer;
import com.travelzen.farerule.translator.tool.DateTransducer;

public class RuleTranslatorBase {

	protected static StringBuilder translateRuleCondition(RuleCondition ruleCondition) {
		StringBuilder sb = new StringBuilder();
		OriginCondition originCondition = ruleCondition.getOriginCondition();
		SalesDateSubItem salesDateCondition = ruleCondition.getSalesDateCondition();
		TravelDateSubItem travelDateCondition = ruleCondition.getTravelDateCondition();
		if (originCondition != null) {
			if (originCondition.getOriginType() == OriginTypeEnum.ORIGIN)
				sb.append(ConditionTransducer.localtionToString(originCondition.getLocation()) + "始发\n");
			else if (originCondition.getOriginType() == OriginTypeEnum.OUTBOUND)
				sb.append("去程\n");
			else if (originCondition.getOriginType() == OriginTypeEnum.INBOUND)
				sb.append("回程\n");
			else if (originCondition.getOriginType() == OriginTypeEnum.ALL)
				sb.append("全程\n");
		}
		if (salesDateCondition != null) {
			if (salesDateCondition.getBeforeDate() != 0 && salesDateCondition.getAfterDate() != 0)
				sb.append(DateTransducer.longDateToString(salesDateCondition.getAfterDate()) + "到" 
						+ DateTransducer.longDateToString(salesDateCondition.getBeforeDate()) + "之间出票\n");
			else if (salesDateCondition.getBeforeDate() != 0)
				sb.append(DateTransducer.longDateToString(salesDateCondition.getBeforeDate()) + "或之前出票\n");
			else if (salesDateCondition.getAfterDate() != 0)
				sb.append(DateTransducer.longDateToString(salesDateCondition.getAfterDate()) + "或之后出票\n");
		}
		if (travelDateCondition != null) {
			if (travelDateCondition.getBeforeDate() != 0 && travelDateCondition.getAfterDate() != 0)
				sb.append(DateTransducer.longDateToString(travelDateCondition.getAfterDate()) + "到" 
						+ DateTransducer.longDateToString(travelDateCondition.getBeforeDate()) + "之间出发\n");
			else if (travelDateCondition.getBeforeDate() != 0)
				sb.append(DateTransducer.longDateToString(travelDateCondition.getBeforeDate()) + "或之前出发\n");
			else if (travelDateCondition.getAfterDate() != 0)
				sb.append(DateTransducer.longDateToString(travelDateCondition.getAfterDate()) + "或之后出发\n");
		}
		return sb;
	}
}
