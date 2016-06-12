package com.travelzen.farerule.mongo.morphia;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class RuleInfoMorphiaTest {

	@Test
	public void testOriginalRule() {
		RuleInfoMorphia morphia = RuleInfoMorphia.Instance;
		System.out.println(morphia.count());
		System.out.println(morphia.findByRuleInfoId("8814c68d837154cca949065d0a6815a4"));
		System.out.println(morphia.findByOriginalRuleId("b01ef07bbdda15d972c6dbc48d87ba27"));
	}
}
