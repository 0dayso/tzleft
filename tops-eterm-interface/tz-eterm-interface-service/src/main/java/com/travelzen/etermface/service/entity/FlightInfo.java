package com.travelzen.etermface.service.entity;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("FlightInfo")
public class FlightInfo implements Serializable{

	private static final long serialVersionUID = -1184736370802262133L;

	/**
	 * 航空公司
	 */
	private String carrier;
	
	/**
	 * 航班号
	 */
	private String flightNo;
	
	/**
	 * 舱位代码
	 */
	private String cabinCode;
	
	/**
	 * 出发城市
	 */
	private String fromCity;
	
	/**
	 * 到达城市
	 */
	private String toCity;
	/**
	 * 出发机场
	 */
	private String fromAirPort;
	
	/**
	 * 出发机场是否为国内机场
	 */
	private boolean fromDomestic;
	
	/**
	 * 到达机场
	 */
	private String toAirPort;
	
	/**
	 * 到达机场是否为国内机场
	 */
	private boolean toDomestic;
	
	/**
	 * 机场距离
	 */
	private int distance;
	
	/**
	 * 飞行时常
	 */
	private String duration;
	
	/**
	 * 出发日期
	 */
	private String fromDate;
	
	/**
	 * 出发时间
	 */
	private String fromTime;
	
	/**
	 * 到达日期
	 */
	private String toDate;
	/**
	 * 到达时间
	 */
	private String toTime;
	
	/**
	 * 代码共享航班
	 */
	private boolean isCodeShare;
	
	/**
	 * 剩余座位数
	 */
	private String seats;
	
	/**
	 * 出发航站楼
	 */
	private String fromTerminal;
	
	/**
	 * 到达航站楼
	 */
	private String toTerminal;
	
	/**
	 * 机型
	 */
	private String planeModel;
	
	private String realCarrier;
	
	private String realFlightNo;
	
	private String meal;

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getFlightNo() {
		return flightNo;
	}

	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}

	public String getCabinCode() {
		return cabinCode;
	}

	public void setCabinCode(String cabinCode) {
		this.cabinCode = cabinCode;
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

	public String getFromAirPort() {
		return fromAirPort;
	}

	public void setFromAirPort(String fromAirPort) {
		this.fromAirPort = fromAirPort;
	}

	public boolean isFromDomestic() {
		return fromDomestic;
	}

	public void setFromDomestic(boolean fromDomestic) {
		this.fromDomestic = fromDomestic;
	}

	public String getToAirPort() {
		return toAirPort;
	}

	public void setToAirPort(String toAirPort) {
		this.toAirPort = toAirPort;
	}

	public boolean isToDomestic() {
		return toDomestic;
	}

	public void setToDomestic(boolean toDomestic) {
		this.toDomestic = toDomestic;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
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

	public boolean isCodeShare() {
		return isCodeShare;
	}

	public void setCodeShare(boolean isCodeShare) {
		this.isCodeShare = isCodeShare;
	}

	public String getSeats() {
		return seats;
	}

	public void setSeats(String seats) {
		this.seats = seats;
	}

	public String getFromTerminal() {
		return fromTerminal;
	}

	public void setFromTerminal(String fromTerminal) {
		this.fromTerminal = fromTerminal;
	}

	public String getToTerminal() {
		return toTerminal;
	}

	public void setToTerminal(String toTerminal) {
		this.toTerminal = toTerminal;
	}

	public String getPlaneModel() {
		return planeModel;
	}

	public void setPlaneModel(String planeModel) {
		this.planeModel = planeModel;
	}

	public String getRealCarrier() {
		return realCarrier;
	}

	public void setRealCarrier(String realCarrier) {
		this.realCarrier = realCarrier;
	}

	public String getRealFlightNo() {
		return realFlightNo;
	}

	public void setRealFlightNo(String realFlightNo) {
		this.realFlightNo = realFlightNo;
	}

	public String getMeal() {
		return meal;
	}

	public void setMeal(String meal) {
		this.meal = meal;
	}

	@Override
	public String toString() {
		return "FlightInfo [carrier=" + carrier + ", flightNo=" + flightNo + ", cabinCode=" + cabinCode + ", fromCity="
				+ fromCity + ", toCity=" + toCity + ", fromAirPort=" + fromAirPort + ", fromDomestic=" + fromDomestic
				+ ", toAirPort=" + toAirPort + ", toDomestic=" + toDomestic + ", distance=" + distance + ", duration="
				+ duration + ", fromDate=" + fromDate + ", fromTime=" + fromTime + ", toDate=" + toDate + ", toTime="
				+ toTime + ", isCodeShare=" + isCodeShare + ", seats=" + seats + ", fromTerminal=" + fromTerminal
				+ ", toTerminal=" + toTerminal + ", planeModel=" + planeModel + ", realCarrier=" + realCarrier
				+ ", realFlightNo=" + realFlightNo + ", meal=" + meal + "]";
	}

	
}