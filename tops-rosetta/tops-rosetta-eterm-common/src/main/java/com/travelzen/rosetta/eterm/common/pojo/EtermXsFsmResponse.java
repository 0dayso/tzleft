package com.travelzen.rosetta.eterm.common.pojo;

import java.util.List;

/**
 * Eterm XS FSM 城市间里程数
 * <p>
 * @author yiming.yan
 * @Date Mar 3, 2016
 */
public class EtermXsFsmResponse {

	private boolean success;
	private String errorMsg;
	private List<CityDistance> cityDistances;
	
	public EtermXsFsmResponse() {
	}
	
	public EtermXsFsmResponse(boolean success, String errorMsg) {
		this.success = success;
		this.errorMsg = errorMsg;
	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public List<CityDistance> getCityDistances() {
		return cityDistances;
	}
	public void setCityDistances(List<CityDistance> cityDistances) {
		this.cityDistances = cityDistances;
	}
	
	@Override
	public String toString() {
		return "EtermXsFsmResponse [success=" + success + ", errorMsg="
				+ errorMsg + ", cityDistances=" + cityDistances + "]";
	}
	
	public static class CityDistance {
		private String fromCity;
		private String toCity;
		private int tpm;
		private int cum;
		private int mpm;
		
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
		public int getTpm() {
			return tpm;
		}
		public void setTpm(int tpm) {
			this.tpm = tpm;
		}
		public int getCum() {
			return cum;
		}
		public void setCum(int cum) {
			this.cum = cum;
		}
		public int getMpm() {
			return mpm;
		}
		public void setMpm(int mpm) {
			this.mpm = mpm;
		}
		
		@Override
		public String toString() {
			return "CityDistance [fromCity=" + fromCity + ", toCity=" + toCity
					+ ", tpm=" + tpm + ", cum=" + cum + ", mpm=" + mpm + "]";
		}
	}

}
