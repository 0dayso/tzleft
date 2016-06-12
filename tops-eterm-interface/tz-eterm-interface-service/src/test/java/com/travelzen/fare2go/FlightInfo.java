package com.travelzen.fare2go;

import java.io.Serializable;

/**
 * @author hongqiang.mao
 *
 * @date 2013-5-6 上午10:30:20
 *
 * @description
 */
public class FlightInfo implements Serializable {
	private static final long serialVersionUID = 3473266558721500912L;
	/**
	 * No 航班号(flightNumber)
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
	 * A 航空公司代码(airCo)
	 */
	private String airCo;
	/**
	 * ET 机型(equipType)
	 */
	private String equipType;
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
	 * D 飞行时长(duration)
	 */
	private String duration;
	
	/**
	 * ST 舱位剩余数量
	 */
	private String cabinRemainingCount;
	
	/**
	 * V  是否代码共享航班(0-非共享航班 1-共享航班)
	 */
	private String codeShareFlight;
	/**
	 * T  是否经停航班(0-否 1-是)
	 */
	private String stop;
	/**
	 * IFT	起飞航站楼
	 */
	private String leaveTerminal;
	/**
	 * IDT	到达航站楼
	 */
	private String arriveTerminal;
	
	/**
	 * IST	经停机场
	 */
	private String stopAirport;
	
	/**
	 * STIME	经停时间
	 */
	private String stopTime;

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

	public String getAirCo() {
		return airCo;
	}

	public void setAirCo(String airCo) {
		this.airCo = airCo;
	}

	public String getEquipType() {
		return equipType;
	}

	public void setEquipType(String equipType) {
		this.equipType = equipType;
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

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getCabinRemainingCount() {
		return cabinRemainingCount;
	}

	public void setCabinRemainingCount(String cabinRemainingCount) {
		this.cabinRemainingCount = cabinRemainingCount;
	}

	public String getCodeShareFlight() {
		return codeShareFlight;
	}

	public void setCodeShareFlight(String codeShareFlight) {
		this.codeShareFlight = codeShareFlight;
	}

	public String getStop() {
		return stop;
	}

	public void setStop(String stop) {
		this.stop = stop;
	}

	public String getLeaveTerminal() {
		return leaveTerminal;
	}

	public void setLeaveTerminal(String leaveTerminal) {
		this.leaveTerminal = leaveTerminal;
	}

	public String getArriveTerminal() {
		return arriveTerminal;
	}

	public void setArriveTerminal(String arriveTerminal) {
		this.arriveTerminal = arriveTerminal;
	}

	public String getStopAirport() {
		return stopAirport;
	}

	public void setStopAirport(String stopAirport) {
		this.stopAirport = stopAirport;
	}

	public String getStopTime() {
		return stopTime;
	}

	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}

	@Override
	public String toString() {
		return "FlightInfo [flightNumber=" + flightNumber + ", fromAirport="
				+ fromAirport + ", toAirport=" + toAirport + ", airCo=" + airCo
				+ ", equipType=" + equipType + ", fromDate=" + fromDate
				+ ", fromTime=" + fromTime + ", toDate=" + toDate + ", toTime="
				+ toTime + ", duration=" + duration + ", cabinRemainingCount="
				+ cabinRemainingCount + ", codeShareFlight=" + codeShareFlight
				+ ", stop=" + stop + ", leaveTerminal=" + leaveTerminal
				+ ", arriveTerminal=" + arriveTerminal + ", stopAirport="
				+ stopAirport + ", stopTime=" + stopTime + "]";
	}
}
