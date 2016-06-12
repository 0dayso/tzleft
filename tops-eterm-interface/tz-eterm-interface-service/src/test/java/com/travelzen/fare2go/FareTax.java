package com.travelzen.fare2go;

import java.io.Serializable;

/**
 * @author hongqiang.mao
 *
 * @date 2013-5-7 下午7:05:23
 *
 * @description
 */
public class FareTax implements Serializable{
	private static final long serialVersionUID = -8119367057717566073L;
	
	/**
	 * 费用编号
	 */
	private String fareNo;
	
	/**
	 * 价格文件号
	 */
	private String priceNo;
	/**
	 * 价格
	 */
	private String fare;
	/**
	 * 税
	 */
	private String tax;
	
	/**
	 * 
	 */
	private String standPrice;
	/**
	 * 
	 */
	private String qValue;
	/**
	 * 
	 */
	private String qCHDValue;
	/**
	 * 
	 */
	private String chdFare;
	/**
	 * 
	 */
	private String chdFareIncludeCommision;
	/**
	 * 
	 */
	private String chdTax;
	
	/**
	 * 含代理费票面价格
	 */
	private String priceIncludeCommision;
	
	/**
	 * 人名格式为niu/liang;zhao/baocheng
	 */
	private String passenger;
	
	/**
	 * 主航空公司
	 */
	private String airCo;
	
	/**
	 * 
	 */
	private String ticketLimit;
	
	/**
	 * 是否特价
	 */
	private String isSp;
	
	/**
	 * 特价编号
	 */
	private String spNo;
	

	public String getFareNo() {
		return fareNo;
	}

	public void setFareNo(String fareNo) {
		this.fareNo = fareNo;
	}

	public String getPriceNo() {
		return priceNo;
	}

	public void setPriceNo(String priceNo) {
		this.priceNo = priceNo;
	}

	public String getFare() {
		return fare;
	}

	public void setFare(String fare) {
		this.fare = fare;
	}

	public String getTax() {
		return tax;
	}

	public void setTax(String tax) {
		this.tax = tax;
	}

	public String getPassenger() {
		return passenger;
	}

	public void setPassenger(String passenger) {
		this.passenger = passenger;
	}

	public String getAirCo() {
		return airCo;
	}

	public void setAirCo(String airCo) {
		this.airCo = airCo;
	}

	public String getTicketLimit() {
		return ticketLimit;
	}

	public void setTicketLimit(String ticketLimit) {
		this.ticketLimit = ticketLimit;
	}

	public String getIsSp() {
		return isSp;
	}

	public void setIsSp(String isSp) {
		this.isSp = isSp;
	}

	public String getSpNo() {
		return spNo;
	}

	public void setSpNo(String spNo) {
		this.spNo = spNo;
	}

	public String getPriceIncludeCommision() {
		return priceIncludeCommision;
	}

	public void setPriceIncludeCommision(String priceIncludeCommision) {
		this.priceIncludeCommision = priceIncludeCommision;
	}

	public String getStandPrice() {
		return standPrice;
	}

	public void setStandPrice(String standPrice) {
		this.standPrice = standPrice;
	}

	public String getqValue() {
		return qValue;
	}

	public void setqValue(String qValue) {
		this.qValue = qValue;
	}

	public String getqCHDValue() {
		return qCHDValue;
	}

	public void setqCHDValue(String qCHDValue) {
		this.qCHDValue = qCHDValue;
	}

	public String getChdFare() {
		return chdFare;
	}

	public void setChdFare(String chdFare) {
		this.chdFare = chdFare;
	}

	public String getChdFareIncludeCommision() {
		return chdFareIncludeCommision;
	}

	public void setChdFareIncludeCommision(String chdFareIncludeCommision) {
		this.chdFareIncludeCommision = chdFareIncludeCommision;
	}

	public String getChdTax() {
		return chdTax;
	}

	public void setChdTax(String chdTax) {
		this.chdTax = chdTax;
	}

	@Override
	public String toString() {
		return "FareTax [fareNo=" + fareNo + ", priceNo=" + priceNo + ", fare="
				+ fare + ", tax=" + tax + ", standPrice=" + standPrice
				+ ", qValue=" + qValue + ", qCHDValue=" + qCHDValue
				+ ", chdFare=" + chdFare + ", chdFareIncludeCommision="
				+ chdFareIncludeCommision + ", chdTax=" + chdTax
				+ ", priceIncludeCommision=" + priceIncludeCommision
				+ ", passenger=" + passenger + ", airCo=" + airCo
				+ ", ticketLimit=" + ticketLimit + ", isSp=" + isSp + ", spNo="
				+ spNo + "]";
	}	
}
