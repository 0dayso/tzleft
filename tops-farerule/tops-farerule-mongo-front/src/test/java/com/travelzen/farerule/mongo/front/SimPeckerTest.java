package com.travelzen.farerule.mongo.front;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.travelzen.farerule.RuleSourceEnum;
import com.travelzen.farerule.TzRule;
import com.travelzen.farerule.TzRuleInfo;
import com.travelzen.farerule.mongo.front.simpecker.SimPecker;
import com.travelzen.farerule.mongo.morphia.TzRuleMorphia;

@RunWith(JUnit4.class)
public class SimPeckerTest {

	@Test
	public void test() {
		String content6 = "无限制。";
		String content7 = "无限制。";
		String content14 = "必须在2011.12.08或之后出发。";
		String content16 = "退票:收取退票费700.0人民币/100.0美元。\n"
				+ "改签:收取改签费520.0人民币。换开收取费用520.0人民币。";
		SimPecker simPecker = new SimPecker();
		TzRuleInfo tzRuleInfo = new TzRuleInfo().setRuleSource(RuleSourceEnum.JPECKER).setJpeckerRuleId("t1t1t1");
		simPecker.process(tzRuleInfo, content6, content7, content14, content16);
		TzRule tzRule = simPecker.getTzRule()
				.setTzRuleInfo(new TzRuleInfo().setRuleSource(RuleSourceEnum.JPECKER).setJpeckerRuleId("fa51e4fe9d252c93ad679f7b70a1bef4"));
		System.out.println(tzRule);
		TzRuleMorphia morphia = TzRuleMorphia.Instance;
		morphia.forceSave(tzRule);
		System.out.println(morphia.findByTzRuleInfo(tzRule.getTzRuleInfo()));
	}

}
