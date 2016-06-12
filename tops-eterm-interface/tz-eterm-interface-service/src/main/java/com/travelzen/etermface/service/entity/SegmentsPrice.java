package com.travelzen.etermface.service.entity;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("SegmentsPrice")
public class SegmentsPrice {
	private double price;
	
	@XStreamImplicit(itemFieldName="FlightInfoList")
	private List<FlightInfo> flightInfoList;
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public List<FlightInfo> getFlightInfoList() {
		return flightInfoList;
	}
	public void setFlightInfoList(List<FlightInfo> flightInfoList) {
		this.flightInfoList = flightInfoList;
	}
	@Override
	public String toString() {
		return "SegmentsPrice [price=" + price + ", flightInfoList="
				+ flightInfoList + "]";
	}
}
