package com.travelzen.rosetta.eterm.common.pojo;

import java.util.List;

/**
 * Eterm RTKT　指令解析结果类
 * <p>
 * @author yiming.yan
 * @Date Mar 2, 2016
 */
public class EtermRtktResponse {
	
	private boolean success;
	private String errorMsg;
	private String mainTktNo;
	private PsgType psgType = PsgType.ADT;
	private double fare;
	private double commission;
	private double tax;
	private String nucStr;
	private List<Flight> flights;
	
	public EtermRtktResponse() {
	}
	
	public EtermRtktResponse(boolean success, String errorMsg) {
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
	public String getMainTktNo() {
		return mainTktNo;
	}
	public void setMainTktNo(String mainTktNo) {
		this.mainTktNo = mainTktNo;
	}
	public PsgType getPsgType() {
		return psgType;
	}
	public void setPsgType(PsgType psgType) {
		this.psgType = psgType;
	}
	public double getFare() {
		return fare;
	}
	public void setFare(double fare) {
		this.fare = fare;
	}
	public double getCommission() {
		return commission;
	}
	public void setCommission(double commission) {
		this.commission = commission;
	}
	public double getTax() {
		return tax;
	}
	public void setTax(double tax) {
		this.tax = tax;
	}
	public String getNucStr() {
		return nucStr;
	}
	public void setNucStr(String nucStr) {
		this.nucStr = nucStr;
	}
	public List<Flight> getFlights() {
		return flights;
	}
	public void setFlights(List<Flight> flights) {
		this.flights = flights;
	}
	
	@Override
	public String toString() {
		return "EtermRtktResponse [success=" + success + ", errorMsg="
				+ errorMsg + ", mainTktNo=" + mainTktNo + ", psgType="
				+ psgType + ", fare=" + fare + ", commission=" + commission
				+ ", tax=" + tax + ", nucStr=" + nucStr + ", flights="
				+ flights + "]";
	}

	public static enum PsgType {
		ADT, CHD, INF;
	}

	public static class Flight {
		private String airCompany;
		private String flightNo;
		private boolean isShared;
		private String cabin;
		private String deptAirport;
		private String arrAirport;
		private String deptDate;
		private String deptTime;
		private List<Stopover> stopovers;
		
		public String getAirCompany() {
			return airCompany;
		}
		public void setAirCompany(String airCompany) {
			this.airCompany = airCompany;
		}
		public String getFlightNo() {
			return flightNo;
		}
		public void setFlightNo(String flightNo) {
			this.flightNo = flightNo;
		}
		public boolean isShared() {
			return isShared;
		}
		public void setShared(boolean isShared) {
			this.isShared = isShared;
		}
		public String getCabin() {
			return cabin;
		}
		public void setCabin(String cabin) {
			this.cabin = cabin;
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
		public String getDeptTime() {
			return deptTime;
		}
		public void setDeptTime(String deptTime) {
			this.deptTime = deptTime;
		}
		public List<Stopover> getStopovers() {
			return stopovers;
		}
		public void setStopovers(List<Stopover> stopovers) {
			this.stopovers = stopovers;
		}
		
		@Override
		public String toString() {
			return "Flight [airCompany=" + airCompany + ", flightNo="
					+ flightNo + ", isShared=" + isShared + ", cabin=" + cabin
					+ ", deptAirport=" + deptAirport + ", arrAirport="
					+ arrAirport + ", deptDate=" + deptDate + ", deptTime="
					+ deptTime + ", stopovers=" + stopovers + "]";
		}
		
		public static class Stopover {
			private String airCompany;
			private String flightNo;
			private boolean isShared;
			private String cabin;
			private String airport;
			private String deptDate;
			private String deptTime;
			
			public String getAirCompany() {
				return airCompany;
			}
			public void setAirCompany(String airCompany) {
				this.airCompany = airCompany;
			}
			public String getFlightNo() {
				return flightNo;
			}
			public void setFlightNo(String flightNo) {
				this.flightNo = flightNo;
			}
			public boolean isShared() {
				return isShared;
			}
			public void setShared(boolean isShared) {
				this.isShared = isShared;
			}
			public String getCabin() {
				return cabin;
			}
			public void setCabin(String cabin) {
				this.cabin = cabin;
			}
			public String getAirport() {
				return airport;
			}
			public void setAirport(String airport) {
				this.airport = airport;
			}
			public String getDeptDate() {
				return deptDate;
			}
			public void setDeptDate(String deptDate) {
				this.deptDate = deptDate;
			}
			public String getDeptTime() {
				return deptTime;
			}
			public void setDeptTime(String deptTime) {
				this.deptTime = deptTime;
			}
			
			@Override
			public String toString() {
				return "Stopover [airCompany=" + airCompany + ", flightNo="
						+ flightNo + ", isShared=" + isShared + ", cabin="
						+ cabin + ", airport=" + airport + ", deptDate="
						+ deptDate + ", deptTime=" + deptTime + "]";
			}
		}
	}

}
