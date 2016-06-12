/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.translator.tool;

import java.util.List;

import com.travelzen.farerule.rule.PenaltyAdditionEnum;
import com.travelzen.farerule.rule.PenaltyContent;
import com.travelzen.farerule.rule.PenaltyFee;
import com.travelzen.farerule.rule.PenaltyTypeEnum;
import com.travelzen.farerule.translator.consts.CurrencyConst;

public class PenaltyTransducer {
	
	public static String transIfUsed(boolean used) {
		if (used)
			return "部分已使用机票";
		return "完全未使用机票";
	}

	public static String penaltyFeeToString(List<PenaltyFee> penaltyFeeList) {
		if (penaltyFeeList.size() == 0)
			return null;
		String fee = "";
		PenaltyFee penaltyFee = penaltyFeeList.get(0);
		fee = penaltyFee.getAmount() + transCurrency(penaltyFee.getCurrency());
		if (penaltyFeeList.size() > 1) {
			for (int i=1; i<penaltyFeeList.size(); i++) {
				penaltyFee = penaltyFeeList.get(i);
				fee = fee + "/" + penaltyFee.getAmount() + transCurrency(penaltyFee.getCurrency());
			}
		}
		return fee;
	}
	
	public static String transCurrency(String currency) {
		if (currency.matches("[A-Z]{3}") && CurrencyConst.currencyMap.containsKey(currency))
			return CurrencyConst.currencyMap.get(currency);
		return currency;
	}
	
	public static String cancelToString(PenaltyContent penaltyContent) {
		return penaltyToString(penaltyContent, "退票");
	}

	public static String changeToString(PenaltyContent penaltyContent) {
		return penaltyToString(penaltyContent, "改期");
	}
	
	public static String reissueToString(PenaltyContent penaltyContent) {
		return penaltyToString(penaltyContent, "换开");
	}

	public static String rerouteToString(PenaltyContent penaltyContent) {
		return penaltyToString(penaltyContent, "更改路线");
	}

	public static String endorseToString(PenaltyContent penaltyContent) {
		return penaltyToString(penaltyContent, "签转");
	}
	
	public static String cancelNoshowToString(PenaltyContent penaltyContent) {
		return penaltyNoshowToString(penaltyContent, "退票");
	}

	public static String changeNoshowToString(PenaltyContent penaltyContent) {
		return penaltyNoshowToString(penaltyContent, "改期");
	}
	
	public static String penaltyToString(PenaltyContent penaltyContent, String type) {
		String result = "";
		PenaltyTypeEnum penaltyType = penaltyContent.getPenaltyType();
		if (penaltyType == PenaltyTypeEnum.PERMIT) {
			result = "允许" + type;
		} else if (penaltyType == PenaltyTypeEnum.NOT_PERMIT) {
			result = "不允许" + type;
		} else {
			if (penaltyType == PenaltyTypeEnum.FEE) {
				String fee = penaltyFeeToString(penaltyContent.getPenaltyFeeList());
				result = type + "收取" + fee;
			} else if (penaltyType == PenaltyTypeEnum.RATIO) {
				String ratio = penaltyContent.getPenaltyRatio().getRatio() + "%";
				result = type + "收取机票价格的" + ratio;
			} else if (penaltyType == PenaltyTypeEnum.HIGHER) {
				String fee = penaltyFeeToString(penaltyContent.getPenaltyFeeList());
				String ratio = penaltyContent.getPenaltyRatio().getRatio() + "%";
				result = type + "收取" + fee + "或机票价格的" + ratio + "中较高的";
			} else {
				return "";
			}
			if (!type.equals("退票")) {
				if (penaltyContent.isContainDiff())
					result += "(包含差价)";
				else
					result += "(不含差价)";
			}
		}
		result += "。";
		return result;
	}

	public static String penaltyNoshowToString(PenaltyContent penaltyContent, String type) {
		String result = "";
		PenaltyTypeEnum penaltyType = penaltyContent.getPenaltyType();
		if (penaltyType == PenaltyTypeEnum.FEE) {
			String fee = penaltyFeeToString(penaltyContent.getPenaltyFeeList());
			result = result + "加收误机费" + fee;
		} else if (penaltyType == PenaltyTypeEnum.PERMIT) {
			result = result + "不加收误机费";
		} else if (penaltyType == PenaltyTypeEnum.NOT_PERMIT) {
			result = result + "不允许" + type;
		} else if (penaltyType == PenaltyTypeEnum.RATIO) {
			String ratio = penaltyContent.getPenaltyRatio().getRatio() + "%";
			result = result + "加收机票价格的" + ratio + "作为误机费";
		} else if (penaltyType == PenaltyTypeEnum.HIGHER) {
			String fee = penaltyFeeToString(penaltyContent.getPenaltyFeeList());
			String ratio = penaltyContent.getPenaltyRatio().getRatio() + "%";
			result = result + "加收误机费" + fee + "或机票价格的" + ratio + "中较高的";
		} else {
			return "";
		}
		result += "。";
		return result;
	}
	
	public static String penaltyAdditionToString(PenaltyAdditionEnum penaltyAddition) {
		String result = "";
		switch (penaltyAddition) {
		case NO_CLASS_CHANGE:
			result = "只允许同舱位换乘。";
			break;
		case NO_AHEAD:
			result = "始发日不得提前。";
			break;
		case NO_EXTENTION:
			result = "更改后不得超过原有效期。";
			break;
		default:
			break;
		}
		return result;
	}

}
