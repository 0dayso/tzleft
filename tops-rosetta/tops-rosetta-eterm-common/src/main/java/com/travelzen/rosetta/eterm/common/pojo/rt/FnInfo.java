package com.travelzen.rosetta.eterm.common.pojo.rt;

import java.util.List;

import com.travelzen.rosetta.eterm.common.pojo.enums.PassengerType;

public class FnInfo {
	
	// 付款方式
	private String method;
	// 货币
	private String currency;
	// 价格
	private List<FnFare> fares;
	
	public String getMethod() {
		return method;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getCurrency() {
		return currency;
	}
	
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public List<FnFare> getFares() {
		return fares;
	}

	public void setFares(List<FnFare> fares) {
		this.fares = fares;
	}

	@Override
	public String toString() {
		return "FnInfo [method=" + method + ", currency=" + currency
				+ ", fares=" + fares + "]";
	}
	
	public static class FnFare {

		// 乘客类型
		private PassengerType psgType = PassengerType.ADT;
		// 价格
		private List<FnFareItem> fareItems;
		// Text
		private String fnText;
		
		public FnFare() {
		}
		
		public FnFare(PassengerType psgType) {
			this.psgType = psgType;
		}
		
		public PassengerType getPsgType() {
			return psgType;
		}
		
		public void setPsgType(PassengerType psgType) {
			this.psgType = psgType;
		}
		
		public List<FnFareItem> getFareItems() {
			return fareItems;
		}
		
		public void setFareItems(List<FnFareItem> fareItems) {
			this.fareItems = fareItems;
		}

		public String getFnText() {
			return fnText;
		}

		public void setFnText(String fnText) {
			this.fnText = fnText;
		}

		@Override
		public String toString() {
			return "FnFare [psgType=" + psgType + ", fareItems=" + fareItems
					+ "]";
		}

	}
	
	public static class FnFareItem {

		// Fare Basis
		private String fareBasis;
		// 票面价
		private String faceFare;
		// 现价
		private String currentFare;
		// 代理费
		private String commission;
		// 总税收
		private String tax;
		// 机场建设费
		private String cnTax;
		// 燃油附加费 (已取消)
		private String yqTax;
		// 总价
		private String totalFare;
		// SFC
		private String sfc;
		// Text
		private String fnItemText;

		public String getFareBasis() {
			return fareBasis;
		}

		public void setFareBasis(String fareBasis) {
			this.fareBasis = fareBasis;
		}

		public String getFaceFare() {
			return faceFare;
		}
		
		public void setFaceFare(String faceFare) {
			this.faceFare = faceFare;
		}
		
		public String getCurrentFare() {
			return currentFare;
		}
		
		public void setCurrentFare(String currentFare) {
			this.currentFare = currentFare;
		}
		
		public String getCommission() {
			return commission;
		}

		public void setCommission(String commission) {
			this.commission = commission;
		}
		
		public String getTax() {
			return tax;
		}
		
		public void setTax(String tax) {
			this.tax = tax;
		}
		
		public String getCnTax() {
			return cnTax;
		}
		
		public void setCnTax(String cnTax) {
			this.cnTax = cnTax;
		}
		
		public String getYqTax() {
			return yqTax;
		}

		public void setYqTax(String yqTax) {
			this.yqTax = yqTax;
		}

		public String getTotalFare() {
			return totalFare;
		}

		public void setTotalFare(String totalFare) {
			this.totalFare = totalFare;
		}

		public String getSfc() {
			return sfc;
		}

		public void setSfc(String sfc) {
			this.sfc = sfc;
		}

		public String getFnItemText() {
			return fnItemText;
		}

		public void setFnItemText(String fnItemText) {
			this.fnItemText = fnItemText;
		}

		@Override
		public String toString() {
			return "FnFareItem [fareBasis=" + fareBasis + ", faceFare="
					+ faceFare + ", currentFare=" + currentFare + ", commission="
					+ commission + ", tax=" + tax + ", cnTax=" + cnTax + ", yqTax="
					+ yqTax + ", totalFare=" + totalFare + ", sfc=" + sfc + "]";
		}

	}
	
}
