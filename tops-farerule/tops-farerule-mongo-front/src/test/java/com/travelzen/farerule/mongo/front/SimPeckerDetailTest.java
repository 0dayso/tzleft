package com.travelzen.farerule.mongo.front;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.travelzen.farerule.mongo.front.simpecker.SimPecker16;
import com.travelzen.farerule.rule.PenaltyContent;

@RunWith(JUnit4.class)
public class SimPeckerDetailTest {

	@Test
	public void test() {
		String text = "允许改期";
		text = "不加收";
		SimPecker16 sim = new SimPecker16();
		PenaltyContent content = sim.testPenaltyContent(text);
		System.out.println(content);
	}
}
