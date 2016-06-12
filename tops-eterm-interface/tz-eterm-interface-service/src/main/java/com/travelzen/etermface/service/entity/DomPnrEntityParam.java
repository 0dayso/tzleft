package com.travelzen.etermface.service.entity;

import java.io.Serializable;
import java.util.List;

import com.thoughtworks.xstream.XStream;

public class DomPnrEntityParam implements Serializable {

	private static final long serialVersionUID = 9202848368917419270L;
	
	private String pnr;
	// 乘客
	private List<PassengerInfo> passengerList; 
	// 航段
	private List<FlightInfo> airSegList; 

	public List<PassengerInfo> getPassengerList() {
		return passengerList;
	}

	public void setPassengerList(List<PassengerInfo> passengerList) {
		this.passengerList = passengerList;
	}

	public List<FlightInfo> getAirSegList() {
		return airSegList;
	}

	public void setAirSegList(List<FlightInfo> airSegList) {
		this.airSegList = airSegList;
	}

	
	public String getPnr() {
		return pnr;
	}

	public void setPnr(String pnr) {
		this.pnr = pnr;
	}

		
	@Override
	public String toString() {
		return "DomPnrEntityParam [pnr=" + pnr + ", passengerList=" + passengerList + ", airSegList=" + airSegList
				+ "]";
	}

	public static String toXml(DomPnrEntityParam param) {
		if (null == param) {
			return null;
		}

		XStream lvXStream = new XStream();

		lvXStream.alias("pnrEntityParam", DomPnrEntityParam.class);
		lvXStream.alias("pnrPassenger", PassengerInfo.class);
		lvXStream.alias("pnrAirSeg", FlightInfo.class);

		return lvXStream.toXML(param);
	}

	
	
	public static DomPnrEntityParam formXml(String xml) {

		if (null == xml) {
			return null;
		}

		XStream lvXStream = new XStream();

		lvXStream.alias("pnrEntityParam", DomPnrEntityParam.class);
		lvXStream.alias("pnrPassenger", PassengerInfo.class);
		lvXStream.alias("pnrAirSeg", FlightInfo.class);

		DomPnrEntityParam param = null;
		
		try {
			param = (DomPnrEntityParam)lvXStream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return param;
	}
}
