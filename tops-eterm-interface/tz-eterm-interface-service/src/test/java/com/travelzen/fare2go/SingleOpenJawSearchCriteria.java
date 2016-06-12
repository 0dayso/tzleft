package com.travelzen.fare2go;

import java.io.Serializable;

/**
 * @author hongqiang.mao
 *
 * @date 2013-5-4 下午2:26:25
 *
 * @description 单行程缺口票查询条件
 */
public class SingleOpenJawSearchCriteria implements Serializable {
	private static final long serialVersionUID = -1237027821913851960L;

	/**
	 * 出发城市三字码，如：PEK
	 */
	private String fromCity;
	
	/**
	 * 出发城市名称
	 */
	private String fromCityName;
	
	/**
	 * 到达城市三字码，如：FRA
	 */
	private String toCity;
	
	/**
	 * 到达城市名称
	 */
	private String toCityName;
	
	/**
	 * 去程日期，格式：yyyy-mm-dd，如：2013-05-31
	 */
	private String fromDate;

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

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getFromCityName() {
		return fromCityName;
	}

	public void setFromCityName(String fromCityName) {
		this.fromCityName = fromCityName;
	}

	public String getToCityName() {
		return toCityName;
	}

	public void setToCityName(String toCityName) {
		this.toCityName = toCityName;
	}

	/**
	 * 缓存key值
	 * @return
	 */
	public String toCacheKeyString(){
		StringBuffer lvBuffer = new StringBuffer();
		lvBuffer.append(this.fromCity);
		lvBuffer.append(this.toCity);
		lvBuffer.append(this.fromDate);
		return lvBuffer.toString();
	}
	
	

	@Override
	public String toString() {
		return "SingleOpenJawSearchCriteria [fromCity=" + fromCity
				+ ", fromCityName=" + fromCityName + ", toCity=" + toCity
				+ ", toCityName=" + toCityName + ", fromDate=" + fromDate + "]";
	}
	
}
