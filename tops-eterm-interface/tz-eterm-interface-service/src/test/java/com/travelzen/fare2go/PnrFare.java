package com.travelzen.fare2go;

import java.io.Serializable;
import java.util.List;

/**
 * @author hongqiang.mao
 *
 * @date 2013-5-7 下午7:03:31
 *
 * @description
 */
public class PnrFare implements Serializable{
	private static final long serialVersionUID = -5517829240900929840L;
	/**
	 * PNR编号
	 */
	private String pnr;
	/**
	 *  费用信息
	 */
	private FareTax fareTax;
	
	/**
	 * 航段信息
	 */
	private List<FlightSegment> flightSegmentList;

	public String getPnr() {
		return pnr;
	}

	public void setPnr(String pnr) {
		this.pnr = pnr;
	}

	public FareTax getFareTax() {
		return fareTax;
	}

	public void setFareTax(FareTax fareTax) {
		this.fareTax = fareTax;
	}

	public List<FlightSegment> getFlightSegmentList() {
		return flightSegmentList;
	}

	public void setFlightSegmentList(List<FlightSegment> flightSegmentList) {
		this.flightSegmentList = flightSegmentList;
	}

	@Override
	public String toString() {
		return "PnrFare [pnr=" + pnr + ", fareTax=" + fareTax
				+ ", flightSegmentList=" + flightSegmentList + "]";
	}
}
