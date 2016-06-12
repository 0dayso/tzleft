package com.travelzen.etermface.service.entity.dom;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("position")
public class Position {
	@XStreamAlias("room")
	private String room;
	@XStreamAlias("classStep")
	private String classStep;
	@XStreamAlias("classCode")
	private String classCode;
	@XStreamAlias("sysPrice")
	private String sysPrice;
	@XStreamAlias("rebate")
	private String rebate;
	@XStreamAlias("last")
	private String last;
	@XStreamAlias("rule")
	private Rule rule;

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getClassStep() {
		return classStep;
	}

	public void setClassStep(String classStep) {
		this.classStep = classStep;
	}

	public String getClassCode() {
		return classCode;
	}

	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}

	public String getSysPrice() {
		return sysPrice;
	}

	public void setSysPrice(String sysPrice) {
		this.sysPrice = sysPrice;
	}

	public String getRebate() {
		return rebate;
	}

	public void setRebate(String rebate) {
		this.rebate = rebate;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}

}
