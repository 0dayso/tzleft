package com.travelzen.fare2go;

import com.travelzen.etermface.common.pojo.PassengerType;

import java.io.Serializable;
import java.util.List;


/**
 * 
 * @author hongqiang.mao
 *
 * @date 2013-5-3 下午8:24:44
 * 
 * @description flight查询条件
 */
public class FlightSearchCriteria implements Serializable {

	private static final long serialVersionUID = -1395569807320123698L;
	/**
	 * 非缺口票必填，出发城市三字码，如：PEK
	 */
	private String fromCity;
	
	/**
	 * 非缺口票必填，到达城市三字码，如：FRA
	 */
	private String toCity;
	
	/**
	 * 非缺口票必填，去程日期，格式：yyyy-mm-dd，如：2013-05-31
	 */
	private String fromDate;
	
	/**
	 * 非缺口票必填，回程日期，格式：yyyy-mm-dd，如：2013-05-31
	 */
	private String returnDate;
	
	/**
	 * 查询航程类型，单程、往返、缺口票
	 */
	private TripType tripType;
	/**
	 * 可选，航空公司代码，缺省为All
	 */
	private String airCo;
	
	/**
	 * 可选，乘客类型，成人：ADT，留学生：STU，缺省为ADT
	 */
	private PassengerType passengerType;
	
	/**
	 * 可选，舱位类型，经济舱：Y，商务舱：C，头等舱：F，缺省为Y
	 */
	private CabinType cabinType;
	
	/**
	 * 可选，乘客人数，缺省为1
	 */
	private int adultCount;
	
	/**
	 * 可选，指定航班号，缺省不指定
	 */
	private String flightNumber;
	
	/**
	 * 可选，特殊价格：有以下三种取值：1、缺省为空，不显示特殊价格；2、传入指定的特殊价格类型，如果有多个，以【半角逗号】分隔，；3、传入all，显示所有特殊价格
	 */
	private String specialPrice;
	/**
	 * 缺口票查询条件列表
	 */
	private List<SingleOpenJawSearchCriteria> openJawSearchCriteriaList;
	
	
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
	public String getReturnDate() {
		return returnDate;
	}
	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}
	
	public TripType getTripType() {
		return tripType;
	}

	public void setTripType(TripType tripType) {
		this.tripType = tripType;
	}

	public String getAirCo() {
		return airCo;
	}
	public void setAirCo(String airCo) {
		this.airCo = airCo;
	}
	
	public PassengerType getPassengerType() {
		return passengerType;
	}

	public void setPassengerType(PassengerType passengerType) {
		this.passengerType = passengerType;
	}

	public CabinType getCabinType() {
		return cabinType;
	}

	public void setCabinType(CabinType cabinType) {
		this.cabinType = cabinType;
	}

	public int getAdultCount() {
		return adultCount;
	}

	public void setAdultCount(int adultCount) {
		this.adultCount = adultCount;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	
	public String getSpecialPrice() {
		return specialPrice;
	}

	public void setSpecialPrice(String specialPrice) {
		this.specialPrice = specialPrice;
	}

	public List<SingleOpenJawSearchCriteria> getOpenJawSearchCriteriaList() {
		return openJawSearchCriteriaList;
	}

	
	public void setOpenJawSearchCriteriaList(
			List<SingleOpenJawSearchCriteria> openJawSearchCriteriaList) {
		this.openJawSearchCriteriaList = openJawSearchCriteriaList;
	}

	@Override
	public String toString() {
		return "FlightSearchCriteria [fromCity=" + fromCity + ", toCity="
				+ toCity + ", fromDate=" + fromDate + ", returnDate="
				+ returnDate + ", tripType=" + tripType + ", airCo=" + airCo
				+ ", passengerType=" + passengerType + ", cabinType="
				+ cabinType + ", adultCount=" + adultCount + ", flightNumber="
				+ flightNumber + ", specialPrice=" + specialPrice
				+ ", openJawSearchCriteriaList=" + openJawSearchCriteriaList
				+ "]";
	}
}
