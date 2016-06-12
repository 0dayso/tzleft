package com.travelzen.etermface.service.entity;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created with IntelliJ IDEA.
 * User: lyy
 * Date: 13-6-22
 * Time: 下午8:07
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("SeatPrice")
public class SeatPrice {
	private String passengerType;
	private String index;
	private String cabinNumber;
	private String fareType;
	private double fare;
	private double tax;
	private double yq;
	private double total;
	private String sfc;
	private boolean patByCabinCode;

	public void setPassengerType(String passengerType) {
		this.passengerType = passengerType;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public void setCabinNumber(String cabinNumber) {
		this.cabinNumber = cabinNumber;
	}

	public void setFareType(String fareType) {
		this.fareType = fareType;
	}

	public void setFare(double fare) {
		this.fare = fare;
	}

	public void setTax(double tax) {
		this.tax = tax;
	}

	public void setYq(double yq) {
		this.yq = yq;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public void setSfc(String sfc) {
		this.sfc = sfc;
	}

	public String getPassengerType() {
		return passengerType;
	}

	public String getIndex() {
		return index;
	}

	public String getCabinNumber() {
		return cabinNumber;
	}

	public String getFareType() {
		return fareType;
	}

	public double getFare() {
		return fare;
	}

	public double getTax() {
		return tax;
	}

	public double getYq() {
		return yq;
	}

	public double getTotal() {
		return total;
	}

	public String getSfc() {
		return sfc;
	}
	
	public boolean isPatByCabinCode() {
		return patByCabinCode;
	}

	public void setPatByCabinCode(boolean patByCabinCode) {
		this.patByCabinCode = patByCabinCode;
	}

	@Override
	public String toString() {
		return "SeatPrice [passengerType=" + passengerType + ", index=" + index + ", cabinNumber=" + cabinNumber
				+ ", fareType=" + fareType + ", fare=" + fare + ", tax=" + tax + ", yq=" + yq + ", total=" + total
				+ ", sfc=" + sfc + ", patByCabinCode=" + patByCabinCode + "]";
	}

	/**
	 * 将PAT结果转化为SeatPrice List
	 * @param pPatContent
	 * @param passengerType
	 * @return
	 */
	public static List<SeatPrice> parseSeatPrice(String pPatContent,String passengerType){
		String[] lvLinesPATResult = pPatContent.trim().replaceAll("\r", "\n").split("\n");
		List<SeatPrice> seatPriceList = new ArrayList<SeatPrice>();
		SeatPrice seatPrice = null;
		for (int i = 1; i < lvLinesPATResult.length; ++i) {
			String[] lvStrings = lvLinesPATResult[i].trim().split("\\s+");
			if(null == lvStrings || lvStrings.length == 0){
				continue;
			}
			if(lvStrings[0].trim().matches("\\d+")){
				seatPrice = convert(lvStrings);
				if(null != seatPrice){
					seatPrice.setIndex(lvStrings[0].trim());
					seatPrice.setSfc("SFC:"+lvStrings[0].trim());
					seatPrice.setPassengerType(passengerType);
					seatPriceList.add(seatPrice);
				}
			}
		}
		return seatPriceList;
	}
	
	/**
	 * 转化成SeatPrice对象
	 * @param pStrs
	 * @return
	 */
	private static SeatPrice convert(String[] pStrs){
		if(null == pStrs || pStrs.length < 6){
			return null;
		}
		
		SeatPrice lvSeatPrice = new SeatPrice();
		lvSeatPrice.setCabinNumber(pStrs[1]);
		/**
		 * 解析票面价部分
		 */
		String[] lvFares = pStrs[2].split(":");
		if(null == lvFares || lvFares.length <2 || !"FARE".equals(lvFares[0])){
			return null;
		}
		int index = firstDigitalIndex(lvFares[1]);
		if(-1 == index){
			return null;
		}
		
		lvSeatPrice.setFareType(lvFares[1].substring(0, index));
		try {
			lvSeatPrice.setFare(Double.parseDouble(lvFares[1].substring(index).trim()));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
		
		/**
		 * 解析机场建设费
		 */
		String[] lvDepartureTaxes = pStrs[3].split(":");
		if(null == lvDepartureTaxes || lvDepartureTaxes.length <2 || !"TAX".equals(lvDepartureTaxes[0])){
			return null;
		}
		index = firstDigitalIndex(lvDepartureTaxes[1]);
		if(-1 == index){
			lvSeatPrice.setTax(0);
		}else{
			try {
				lvSeatPrice.setTax(Double.parseDouble(lvDepartureTaxes[1].substring(index).trim()));
				if(lvSeatPrice.getTax() == 0){
					lvSeatPrice.setTax(-1);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				lvSeatPrice.setTax(-1);
			}
		}
		
		/**
		 * 解析燃油附加费
		 */
		String[] lvFuelTaxes = pStrs[4].split(":");
		if(null == lvFuelTaxes || lvFuelTaxes.length <2 || !"YQ".equals(lvFuelTaxes[0])){
			return null;
		}
		index = firstDigitalIndex(lvFuelTaxes[1]);
		if(-1 == index){
			lvSeatPrice.setYq(0);
		}else{
			try {
				lvSeatPrice.setYq(Double.parseDouble(lvFuelTaxes[1].substring(index).trim()));
				if(lvSeatPrice.getYq() == 0){
					lvSeatPrice.setYq(-1);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				lvSeatPrice.setYq(-1);
			}
		}
		
		
	
		
		/**
		 * 解析总价格
		 */
		String[] lvTotals = pStrs[5].split(":");
		if(null == lvTotals || lvTotals.length <2 || !"TOTAL".equals(lvTotals[0])){
			return null;
		}
		index = firstDigitalIndex(lvTotals[1]);
		if(-1 == index){
			lvSeatPrice.setTotal(0);
		}else{
			try {
				lvSeatPrice.setTotal(Double.parseDouble(lvTotals[1].substring(index).trim()));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				lvSeatPrice.setTotal(0);
			}
		}
		
		return lvSeatPrice;
	}
	
	/**
	 * 首个数字的位置
	 * @param str
	 * @return
	 */
	private static int firstDigitalIndex(String str) {
		for (int index = 0; index < str.length(); ++index)
			if (str.charAt(index) >= '0' && str.charAt(index) <= '9')
				return index;
		return -1;
	}
}
