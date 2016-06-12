package com.travelzen.etermface.service.entity;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class ERRuleResult {

	private String ERRule;

	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public String getERRule() {
		return ERRule;
	}

	public void setERRule(String eRRule) {
		ERRule = eRRule;
	}

 

 

}
