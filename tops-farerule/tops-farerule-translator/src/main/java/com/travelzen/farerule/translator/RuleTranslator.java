package com.travelzen.farerule.translator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.farerule.RuleSourceEnum;
import com.travelzen.farerule.TzRule;
import com.travelzen.farerule.TzRuleInfo;
import com.travelzen.farerule.jpecker.server.DisplayRule;

public class RuleTranslator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RuleTranslator.class);

	public static DisplayRule translate(TzRule tzRule) {
		LOGGER.info("RuleTranslator请求: {}", tzRule);
		if (tzRule == null)
			return null;
		DisplayRule displayRule = new DisplayRule();
		TzRuleInfo tzRuleInfo = tzRule.getTzRuleInfo();
		if (tzRuleInfo.getRuleSource().equals(RuleSourceEnum.PAPERFARE))
			displayRule.setId(Long.toString(tzRule.getTzRuleInfo().getPaperfareRuleId()));
		else
			displayRule.setId(tzRule.getTzRuleInfo().getJpeckerRuleId());
		if (tzRule.isSetMinStay())
			displayRule.setMinStay(RuleTranslator6.translate(tzRule.getMinStay()));
		if (tzRule.isSetMaxStay())
			displayRule.setMaxStay(RuleTranslator7.translate(tzRule.getMaxStay()));
		if (tzRule.isSetStopovers())
			displayRule.setStopovers(RuleTranslator8.translate(tzRule.getStopovers()));
		if (tzRule.isSetTravelDate())
			displayRule.setTravelDate(RuleTranslator14.translate(tzRule.getTravelDate()));
		if (tzRule.isSetPenalties())
			displayRule.setPenalties(RuleTranslator16.translate(tzRule.getPenalties()));
		LOGGER.info("RuleTranslator返回结果: {}", displayRule);
		return displayRule;
	}
}
