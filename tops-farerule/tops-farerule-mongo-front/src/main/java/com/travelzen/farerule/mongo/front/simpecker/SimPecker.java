package com.travelzen.farerule.mongo.front.simpecker;

import com.travelzen.farerule.MaxStay;
import com.travelzen.farerule.MinStay;
import com.travelzen.farerule.Penalties;
import com.travelzen.farerule.TravelDate;
import com.travelzen.farerule.TzRule;
import com.travelzen.farerule.TzRuleInfo;

public class SimPecker {
	
	private TzRule tzRule;
	
	public SimPecker() {
		tzRule = new TzRule();
	}
	
	public TzRule getTzRule() {
		return tzRule;
	}

	public void process(TzRuleInfo tzRuleInfo, String minStayText, String maxStayText, String travelDateText, String penaltiesText) {
		MinStay minStay = parseMinStay(minStayText);
		MaxStay maxStay = parseMaxStay(maxStayText);
		TravelDate travelDate = parseTravelDate(travelDateText);
		Penalties penalties = parsePenalties(penaltiesText);
		tzRule.setMinStay(minStay);
		tzRule.setMaxStay(maxStay);
		tzRule.setTravelDate(travelDate);
		tzRule.setPenalties(penalties);
		tzRule.setTzRuleInfo(tzRuleInfo);
		tzRule.setEdited(true);
	}

	private MinStay parseMinStay(String text) {
		MinStay minStay = new MinStay();
		SimPecker6 sp6 = new SimPecker6();
		sp6.process(text);
		minStay = sp6.getMinStay();
		return minStay;
	}

	private MaxStay parseMaxStay(String text) {
		MaxStay maxStay = new MaxStay();
		SimPecker7 sp7 = new SimPecker7();
		sp7.process(text);
		maxStay = sp7.getMaxStay();
		return maxStay;
	}

	private TravelDate parseTravelDate(String text) {
		TravelDate travelDate = new TravelDate();
		SimPecker14 sp14 = new SimPecker14();
		sp14.process(text);
		travelDate = sp14.getTravelDate();
		return travelDate;
	}
	
	private Penalties parsePenalties(String text) {
		Penalties penalties = new Penalties();
		SimPecker16 sp16 = new SimPecker16();
		sp16.process(text);
		penalties = sp16.getPenalties();
		return penalties;
	}
	
}
