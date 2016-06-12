package com.travelzen.etermface.service.entity;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created with IntelliJ IDEA.
 * User: lyy
 * Date: 13-6-22
 * Time: 下午8:09
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("CabinFareCheckResult")
public class CabinFareCheckResult {
	private boolean hasCabin;
	private String cabinCheckMessage;
	private boolean hasFare;
	@XStreamAlias("FareList")
	private List<SeatPrice> fareList;

	public void setHasCabin(boolean hasCabin) {
		this.hasCabin = hasCabin;
	}

	public void setCabinCheckMessage(String cabinCheckMessage) {
		this.cabinCheckMessage = cabinCheckMessage;
	}

	public void setHasFare(boolean hasFare) {
		this.hasFare = hasFare;
	}

	public void setFareList(List<SeatPrice> fareList) {
		this.fareList = fareList;
	}

	public List<SeatPrice> getFareList() {
		return fareList;
	}

	public boolean isHasCabin() {
		return hasCabin;
	}

	public String getCabinCheckMessage() {
		return cabinCheckMessage;
	}

	public boolean isHasFare() {
		return hasFare;
	}

	@Override
	public String toString() {
		return "CabinFareCheckResult [hasCabin=" + hasCabin
				+ ", cabinCheckMessage=" + cabinCheckMessage + ", hasFare="
				+ hasFare + ", fareList=" + fareList + "]";
	}
}
