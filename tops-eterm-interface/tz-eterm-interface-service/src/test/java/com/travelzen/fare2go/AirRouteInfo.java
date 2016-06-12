package com.travelzen.fare2go;

import java.io.Serializable;
import java.util.List;

/**
 * @author hongqiang.mao
 * 
 * @date 2013-5-6 上午9:27:12
 * 
 * @description convert xml to AirRouteInfo object
 */
public class AirRouteInfo implements Serializable {
	private static final long serialVersionUID = 6458045698512329988L;

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
	 * M 金额(不含税、不含基本代理费)(totalFare)
	 */
	private String totalFare;
	/**
	 * PM 金额(不含税、含基本代理费)(newPrice)
	 */
	private String newPrice;
	/**
	 * CM 儿童价（不含税、不含基本代理费）
	 */
	private String childPrice;
	/**
	 * CPM 儿童底价
	 */
	private String childButtomPrice;
	/**
	 * C1 本航空公司代理费
	 */
	private String theAirlineAgencyFee;
	/**
	 * C2 联运航空公司代理费
	 */
	private String multimodalTransportAgencyFee;
	/**
	 * X 参考税(totalTax)
	 */
	private String totalTax;
	/**
	 * Z 转机次数(往返程为中转次数之和)
	 */
	private String totalTransferCount ;
	/**
	 * ZZ 转机次数(单程只有一个值，往返有两个值，以逗号分开，如：1,1);
	 */
	private String transferCount;
	
	/**
	 * N 是否过夜(0否 1是) (单程只有一个值，如：0；往返有两个值，以逗号分开，分别代表去程和回程，如：0,1)
	 */
	private String stayOverNight;
	/**
	 * L 限制条件查询参数，通过此参数，可以获取限制条件
	 */
	private String limit;
	/**
	 * SP 为1表示该价格以票面为准
	 */
	private String specialPrice;
	/**
	 * TP 1代表特殊价格 0非特殊价格
	 */
	private String tp;
	/**
	 * SP
	 */
	private String tf;
	/**
	 * FC 去程航段数（仅往返出现）
	 */
	private String owSegmentNumber;
	/**
	 * MS 航信/GDS返回的固定航班对，必须要按这样的航班组合
	 */
	private String ms;
	/**
	 * 是否为GDS数据，G GDS数据为1，否则为0
	 */
	private String gds;
	/**
	 * PT 乘客类型
	 */
	private String passengerType;
	/**
	 * V 是否需要过境签
	 */
	private String needVisa;
	
	/**
	 * CL 改期级别   0 无法识别  1 改期宽松  2 改期有损失 3 改期严格
	 */
	private String rescheduledLevel;
	
	/**
	 * RL 退票级别   0 无法识别   1 退票宽松 2 退票有损失 3 退票严格
	 */
	private String refundLevel;
	
	/**
	 * LD 代表本价格是否是最后一天
	 */
	private String lastDay;
	
	/**
	 * LCC 是否廉价航空公司价格 0 不是 1 是
	 */
	private String lcc;
	
	/**
	 * SS 航段列表(segments)
	 */
	private List<FlightSegment> flightSegmentList;

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

	public String getTotalFare() {
		return totalFare;
	}

	public void setTotalFare(String totalFare) {
		this.totalFare = totalFare;
	}

	public String getNewPrice() {
		return newPrice;
	}

	public void setNewPrice(String newPrice) {
		this.newPrice = newPrice;
	}

	public String getChildPrice() {
		return childPrice;
	}

	public void setChildPrice(String childPrice) {
		this.childPrice = childPrice;
	}

	public String getChildButtomPrice() {
		return childButtomPrice;
	}

	public void setChildButtomPrice(String childButtomPrice) {
		this.childButtomPrice = childButtomPrice;
	}

	public String getTheAirlineAgencyFee() {
		return theAirlineAgencyFee;
	}

	public void setTheAirlineAgencyFee(String theAirlineAgencyFee) {
		this.theAirlineAgencyFee = theAirlineAgencyFee;
	}

	public String getMultimodalTransportAgencyFee() {
		return multimodalTransportAgencyFee;
	}

	public void setMultimodalTransportAgencyFee(String multimodalTransportAgencyFee) {
		this.multimodalTransportAgencyFee = multimodalTransportAgencyFee;
	}

	public String getTotalTax() {
		return totalTax;
	}

	public void setTotalTax(String totalTax) {
		this.totalTax = totalTax;
	}
	

	public String getTotalTransferCount() {
		return totalTransferCount;
	}

	public void setTotalTransferCount(String totalTransferCount) {
		this.totalTransferCount = totalTransferCount;
	}

	public String getTransferCount() {
		return transferCount;
	}

	public void setTransferCount(String transferCount) {
		this.transferCount = transferCount;
	}

	public String getStayOverNight() {
		return stayOverNight;
	}

	public void setStayOverNight(String stayOverNight) {
		this.stayOverNight = stayOverNight;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getSpecialPrice() {
		return specialPrice;
	}

	public void setSpecialPrice(String specialPrice) {
		this.specialPrice = specialPrice;
	}

	public String getTp() {
		return tp;
	}

	public void setTp(String tp) {
		this.tp = tp;
	}

	public String getTf() {
		return tf;
	}

	public void setTf(String tf) {
		this.tf = tf;
	}

	public String getOwSegmentNumber() {
		return owSegmentNumber;
	}

	public void setOwSegmentNumber(String owSegmentNumber) {
		this.owSegmentNumber = owSegmentNumber;
	}

	public String getMs() {
		return ms;
	}

	public void setMs(String ms) {
		this.ms = ms;
	}

	public String getGds() {
		return gds;
	}

	public void setGds(String gds) {
		this.gds = gds;
	}

	public String getPassengerType() {
		return passengerType;
	}

	public void setPassengerType(String passengerType) {
		this.passengerType = passengerType;
	}

	public String getNeedVisa() {
		return needVisa;
	}

	public void setNeedVisa(String needVisa) {
		this.needVisa = needVisa;
	}

	public String getRescheduledLevel() {
		return rescheduledLevel;
	}

	public void setRescheduledLevel(String rescheduledLevel) {
		this.rescheduledLevel = rescheduledLevel;
	}

	public String getRefundLevel() {
		return refundLevel;
	}

	public void setRefundLevel(String refundLevel) {
		this.refundLevel = refundLevel;
	}

	public String getLastDay() {
		return lastDay;
	}

	public void setLastDay(String lastDay) {
		this.lastDay = lastDay;
	}

	public List<FlightSegment> getFlightSegmentList() {
		return flightSegmentList;
	}

	public void setFlightSegmentList(List<FlightSegment> flightSegmentList) {
		this.flightSegmentList = flightSegmentList;
	}

	public String getLcc() {
		return lcc;
	}

	public void setLcc(String lcc) {
		this.lcc = lcc;
	}

	@Override
	public String toString() {
		return "AirRouteInfo [fromCity=" + fromCity + ", toCity=" + toCity
				+ ", airCo=" + airCo + ", totalFare=" + totalFare
				+ ", newPrice=" + newPrice + ", childPrice=" + childPrice
				+ ", childButtomPrice=" + childButtomPrice
				+ ", theAirlineAgencyFee=" + theAirlineAgencyFee
				+ ", multimodalTransportAgencyFee="
				+ multimodalTransportAgencyFee + ", totalTax=" + totalTax
				+ ", totalTransferCount=" + totalTransferCount
				+ ", transferCount=" + transferCount + ", stayOverNight="
				+ stayOverNight + ", limit=" + limit + ", specialPrice="
				+ specialPrice + ", tp=" + tp + ", tf=" + tf
				+ ", owSegmentNumber=" + owSegmentNumber + ", ms=" + ms
				+ ", gds=" + gds + ", passengerType=" + passengerType
				+ ", needVisa=" + needVisa + ", rescheduledLevel="
				+ rescheduledLevel + ", refundLevel=" + refundLevel
				+ ", lastDay=" + lastDay + ", lcc=" + lcc
				+ ", flightSegmentList=" + flightSegmentList + "]";
	}
}
