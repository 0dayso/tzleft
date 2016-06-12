package com.travelzen.etermface.common.pojo.fare;

import java.io.Serializable;

public class NfdFareResponseNew implements Serializable {
	
	private static final long serialVersionUID = -5780828336860654263L;
	/**
     * 是否成功执行
     */
    public boolean success;
    /**
     * 失败的原因
     */
    public String errorInfo;
    /**
     * 出发机场三字码
     */
    public String deptAirport;
    /**
     * 到达机场三字码
     */
    public String arrAirport;
    /**
     * 出发日期(yyyy-mm-dd)
     */
    public String deptDate;
    /**
     * 航司二字码
     */
    public String carrier;
    /**
     * 舱位
     */
    public String cabin;
    /**
     * nfd具体信息JsonStr
     */
    public String nfdInfos;
    
	public boolean isSuccess() {
		return success;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public String getErrorInfo() {
		return errorInfo;
	}
	
	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}
	
	public String getDeptAirport() {
		return deptAirport;
	}
	
	public void setDeptAirport(String deptAirport) {
		this.deptAirport = deptAirport;
	}
	
	public String getArrAirport() {
		return arrAirport;
	}
	
	public void setArrAirport(String arrAirport) {
		this.arrAirport = arrAirport;
	}
	
	public String getDeptDate() {
		return deptDate;
	}
	
	public void setDeptDate(String deptDate) {
		this.deptDate = deptDate;
	}
	
	public String getCarrier() {
		return carrier;
	}
	
	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}
	
	public String getCabin() {
		return cabin;
	}
	
	public void setCabin(String cabin) {
		this.cabin = cabin;
	}
	
	public String getNfdInfos() {
		return nfdInfos;
	}
	
	public void setNfdInfos(String nfdInfos) {
		this.nfdInfos = nfdInfos;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@Override
	public String toString() {
		return "NfdFareResponseNew [success=" + success + ", errorInfo="
				+ errorInfo + ", deptAirport=" + deptAirport + ", arrAirport="
				+ arrAirport + ", deptDate=" + deptDate + ", carrier="
				+ carrier + ", cabin=" + cabin + ", nfdInfos=" + nfdInfos + "]";
	}

}
