package com.travelzen.farerule.mongo.morphia;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.travelzen.farerule.MaxStay;
import com.travelzen.farerule.MinStay;
import com.travelzen.farerule.Penalties;
import com.travelzen.farerule.RuleSourceEnum;
import com.travelzen.farerule.TravelDate;
import com.travelzen.farerule.TzRule;
import com.travelzen.farerule.TzRuleInfo;

@RunWith(JUnit4.class)
public class TzRuleMorphiaTest {

	@Test
	public void test() {
		TzRuleMorphia morphia = TzRuleMorphia.Instance;
		TzRule rule = new TzRule()
			.setTzRuleInfo(new TzRuleInfo().setRuleSource(RuleSourceEnum.JPECKER).setJpeckerRuleId("2e2e2e"))
			.setMinStay(new MinStay())
			.setMaxStay(new MaxStay())
			.setTravelDate(new TravelDate())
			.setPenalties(new Penalties());
		morphia.forceSave(rule);
		System.out.println(morphia.findByTzRuleInfo(rule.getTzRuleInfo()));
		
//		assertEquals(1, morphia.count());
		morphia.deleteById(rule.getTzRuleInfo());
//		morphia.deleteAll();
//		assertEquals(0, morphia.count());
	}

}
