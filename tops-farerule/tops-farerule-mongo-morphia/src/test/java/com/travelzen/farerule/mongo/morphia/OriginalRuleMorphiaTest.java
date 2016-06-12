package com.travelzen.farerule.mongo.morphia;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.travelzen.farerule.mongo.OriginalRule;

@RunWith(JUnit4.class)
public class OriginalRuleMorphiaTest {

	@Ignore
	@Test
	public void test() {
		OriginalRuleMorphia morphia = OriginalRuleMorphia.Instance;
		OriginalRule rule = new OriginalRule().setId("123").setText("abc");
		morphia.save(rule);
		assertEquals(rule, morphia.findById(rule.getId()));
		assertEquals("abc", morphia.findById(rule.getId()).getText());
		
//		assertEquals(1, morphia.count());
		morphia.deleteById(rule.getId());
//		morphia.deleteAll();
//		assertEquals(0, morphia.count());
	}

	@Test
	public void testyo() {
		OriginalRuleMorphia morphia = OriginalRuleMorphia.Instance;
		System.out.println(morphia.findByAirCompany("UL").size());
	}
}
