package com.travelzen.fare2go;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author hongqiang.mao
 * 
 * @date 2013-5-6 上午10:19:13
 * 
 * @description
 */
public class FlightSegment implements Serializable {
	private static final long serialVersionUID = 5995418525941329842L;

	/**
	 * F 出发城市三字码(fromCity)
	 */
	private String fromCity;
	/**
	 * T 到达城市三字码(toCity)
	 */
	private String toCity;

	/**
	 * A 航空公司代码(airCo)
	 */
	private String airCo;
	/**
	 * No航班号(flightNumber)
	 */
	private String flightNumber;
	/**
	 * FA 起飞机场代码(fromAirport)
	 */
	private String fromAirport;
	/**
	 * TA 到达机场代码(toAirport)
	 */
	private String toAirport;

	/**
	 * 查询日期
	 */
	private String queryDate;

	/**
	 * FD 起飞日期(fromDate)
	 */
	private String fromDate;
	/**
	 * FT 起飞时间(fromTime)
	 */
	private String fromTime;
	/**
	 * TD 到达日期(toDate)
	 */
	private String toDate;

	/**
	 * TT 到达时间(toTime)
	 */
	private String toTime;
	/**
	 * ST 舱位类型编码(seatType)
	 */
	private String seatType;
	/**
	 * FP 航段序号（去程和回程都从1开始，可以根据这个计算转机次数）
	 */
	private String segmentSerialNumber;
	/**
	 * DP 出发为1（返回为0）单程始终为1
	 */
	private String departure;
	/**
	 * M 分段价格
	 */
	private String segmentPrice;
	
	/**
	 * 是否代码共享
	 */
	private String isCodeShare;

	/**
	 * FS 航班列表flights
	 */
	private List<FlightInfo> flightInfoList;

	
	public FlightSegment(){
		SimpleDateFormat lvSDF = new SimpleDateFormat("yyyy-MM-dd");
		this.setQueryDate(lvSDF.format(new Date()));
	}
	public String getFromCity() {
		return fromCity;
	}

	public void setFromCity(String fromCity) {
		this.fromCity = fromCity;
	}

	public String getToCity() {
		return toCity;
	}

	public void setToCity(String toCity) {
		this.toCity = toCity;
	}

	public String getAirCo() {
		return airCo;
	}

	public void setAirCo(String airCo) {
		this.airCo = airCo;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public String getFromAirport() {
		return fromAirport;
	}

	public void setFromAirport(String fromAirport) {
		this.fromAirport = fromAirport;
	}

	public String getToAirport() {
		return toAirport;
	}

	public void setToAirport(String toAirport) {
		this.toAirport = toAirport;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getFromTime() {
		return fromTime;
	}

	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getToTime() {
		return toTime;
	}

	public void setToTime(String toTime) {
		this.toTime = toTime;
	}

	public String getSeatType() {
		return seatType;
	}

	public void setSeatType(String seatType) {
		this.seatType = seatType;
	}

	public String getSegmentSerialNumber() {
		return segmentSerialNumber;
	}

	public void setSegmentSerialNumber(String segmentSerialNumber) {
		this.segmentSerialNumber = segmentSerialNumber;
	}

	public String getDeparture() {
		return departure;
	}

	public void setDeparture(String departure) {
		this.departure = departure;
	}

	public String getSegmentPrice() {
		return segmentPrice;
	}

	public void setSegmentPrice(String segmentPrice) {
		this.segmentPrice = segmentPrice;
	}

	public List<FlightInfo> getFlightInfoList() {
		return flightInfoList;
	}

	public void setFlightInfoList(List<FlightInfo> flightInfoList) {
		this.flightInfoList = flightInfoList;
	}

	public String getQueryDate() {
		if(null == this.queryDate){
			SimpleDateFormat lvSDF = new SimpleDateFormat("yyyy-MM-dd");
			this.setQueryDate(lvSDF.format(new Date()));
		}
		return this.queryDate;
	}

	public void setQueryDate(String queryDate) {
		this.queryDate = queryDate;
	}
	public String getIsCodeShare() {
		return isCodeShare;
	}
	public void setIsCodeShare(String isCodeShare) {
		this.isCodeShare = isCodeShare;
	}
	@Override
	public String toString() {
		return "FlightSegment [fromCity=" + fromCity + ", toCity=" + toCity
				+ ", airCo=" + airCo + ", flightNumber=" + flightNumber
				+ ", fromAirport=" + fromAirport + ", toAirport=" + toAirport
				+ ", queryDate=" + queryDate + ", fromDate=" + fromDate
				+ ", fromTime=" + fromTime + ", toDate=" + toDate + ", toTime="
				+ toTime + ", seatType=" + seatType + ", segmentSerialNumber="
				+ segmentSerialNumber + ", departure=" + departure
				+ ", segmentPrice=" + segmentPrice + ", isCodeShare="
				+ isCodeShare + ", flightInfoList=" + flightInfoList + "]";
	}
}
