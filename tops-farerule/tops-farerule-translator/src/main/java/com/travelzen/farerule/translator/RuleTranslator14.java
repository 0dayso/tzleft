package com.travelzen.farerule.translator;

import java.util.List;

import com.travelzen.farerule.TravelDate;
import com.travelzen.farerule.condition.OriginCondition;
import com.travelzen.farerule.condition.OriginTypeEnum;
import com.travelzen.farerule.condition.TravelDateSubItem;
import com.travelzen.farerule.rule.TravelDateItem;
import com.travelzen.farerule.translator.tool.ConditionTransducer;
import com.travelzen.farerule.translator.tool.DateTransducer;

public class RuleTranslator14 {

	public static String translate(TravelDate travelDate) {
		if (travelDate == null)
			return "无限制。";
		StringBuilder sb = new StringBuilder();
		List<TravelDateItem> travelDateItemList = travelDate.getTravelDateItemList();
		if (travelDateItemList == null || travelDateItemList.size() == 0)
			return "无限制。";
		for (TravelDateItem travelDateItem:travelDateItemList) {
			if (travelDateItem.getSegmentNum() != 0)
				sb.append("第" + travelDateItem.getSegmentNum() + "段航程\n");
			OriginCondition originCondition = travelDateItem.getOriginCondition();
			if (originCondition != null) {
				if (originCondition.getOriginType() == OriginTypeEnum.ORIGIN)
					sb.append(ConditionTransducer.localtionToString(originCondition.getLocation()) + "始发\n");
				else if (originCondition.getOriginType() == OriginTypeEnum.OUTBOUND)
					sb.append("去程\n");
				else if (originCondition.getOriginType() == OriginTypeEnum.INBOUND)
					sb.append("回程\n");
			}
			List<TravelDateSubItem> travelDateSubItemList = travelDateItem.getTravelDateSubItemList();
			if (travelDateSubItemList != null && travelDateSubItemList.size() != 0) {
				sb.append("必须在");
				for (TravelDateSubItem travelDateSubItem:travelDateSubItemList) {
					if (travelDateSubItem.getBeforeDate() != 0 && travelDateSubItem.getAfterDate() != 0)
						sb.append(DateTransducer.longDateToString(travelDateSubItem.getAfterDate()) + "到" 
								+ DateTransducer.longDateToString(travelDateSubItem.getBeforeDate()) + "之间出发");
					else if (travelDateSubItem.getBeforeDate() != 0)
						sb.append(DateTransducer.longDateToString(travelDateSubItem.getBeforeDate()) + "或之前出发");
					else if (travelDateSubItem.getAfterDate() != 0)
						sb.append(DateTransducer.longDateToString(travelDateSubItem.getAfterDate()) + "或之后出发");
					sb.append("，或");
				}
			}
			if (travelDateItem.getCompleteDate() != 0) {
				sb.replace(sb.length()-1, sb.length(), "");
				sb.append("行程必须于" + DateTransducer.longDateToString(travelDateItem.getCompleteDate()) + "前结束。\n");
			}
			if (sb.toString().endsWith("，或"))
				sb.replace(sb.length()-2, sb.length(), "。\n");
		}
		if (sb.toString().endsWith("\n"))
			sb.replace(sb.length()-1, sb.length(), "");
		return sb.toString();
	}
}
