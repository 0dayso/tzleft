package com.travelzen.etermface.service.entity.dom;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("rule")
public class Rule {
	@XStreamAlias("refund")
	private String refund;
	@XStreamAlias("cDate")
	private String cDate;
	@XStreamAlias("turn")
	private String turn;

	public String getRefund() {
		return refund;
	}

	public void setRefund(String refund) {
		this.refund = refund;
	}

	public String getcDate() {
		return cDate;
	}

	public void setcDate(String cDate) {
		this.cDate = cDate;
	}

	public String getTurn() {
		return turn;
	}

	public void setTurn(String turn) {
		this.turn = turn;
	}

}
