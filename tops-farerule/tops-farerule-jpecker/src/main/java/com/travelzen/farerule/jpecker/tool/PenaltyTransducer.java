/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.jpecker.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.farerule.rule.PenaltyContent;
import com.travelzen.farerule.rule.PenaltyFee;
import com.travelzen.farerule.rule.PenaltyRatio;
import com.travelzen.farerule.rule.PenaltyTypeEnum;

public class PenaltyTransducer {
	
	private static final Logger log = LoggerFactory.getLogger(PenaltyTransducer.class);
	
	public static PenaltyContent parsePenaltyContent(String str) {
		PenaltyContent penaltyContent = new PenaltyContent();
		if (str == "")
			return null;
		if (str.equals("-1"))
			penaltyContent.setPenaltyType(PenaltyTypeEnum.NOT_PERMIT);
		else if (str.equals("0") || str.matches("[A-Z]{3} ?0"))
			penaltyContent.setPenaltyType(PenaltyTypeEnum.PERMIT);
//		else if (str.equals("-9"))
//			penaltyContent.setPenaltyType(PenaltyTypeEnum.DIFF);
		else if (str.contains("%")) {
			penaltyContent.setPenaltyType(PenaltyTypeEnum.RATIO);
			PenaltyRatio ratio = new PenaltyRatio();
			ratio.setRatio(Double.parseDouble(str.substring(0, str.length()-1)));
			penaltyContent.setPenaltyRatio(ratio);
		} else {
			str.replaceAll(" ", "");
			penaltyContent.setPenaltyType(PenaltyTypeEnum.FEE);
			List<PenaltyFee> penaltyFeeList = new ArrayList<PenaltyFee>();
			String[] fees = str.split("/");
			for (String fee:fees) {
				PenaltyFee penaltyFee = new PenaltyFee();
				Matcher matcher_currency = Pattern.compile("[A-Z]{3}").matcher(fee);
				if (matcher_currency.find())
					penaltyFee.setCurrency(matcher_currency.group());
				Matcher matcher_amount = Pattern.compile("\\d+").matcher(fee);
				if (matcher_amount.find())
					penaltyFee.setAmount(Double.parseDouble(matcher_amount.group()));
				if (penaltyFee.getCurrency() == null || penaltyFee.getAmount() == 0) {
					log.error("Unparsable Penalty Content: " + str);
					continue;
				}
				penaltyFeeList.add(penaltyFee);
			}
			penaltyContent.setPenaltyFeeList(penaltyFeeList);
		} 
		return penaltyContent;
	}

}
