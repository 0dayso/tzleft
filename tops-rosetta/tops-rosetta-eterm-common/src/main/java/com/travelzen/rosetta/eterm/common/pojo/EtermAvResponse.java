package com.travelzen.rosetta.eterm.common.pojo;

import java.util.List;
import java.util.Map;

/**
 * Eterm AV　指令解析结果类
 * <p>
 * @author yiming.yan
 * @Date Nov 17, 2015
 */
public class EtermAvResponse {
	
	private boolean success;
	private String errorMsg;
	private List<Flight> flights;
	
	public EtermAvResponse() {
	}
	
	public EtermAvResponse(boolean success, String errorMsg) {
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
	public List<Flight> getFlights() {
		return flights;
	}
	public void setFlights(List<Flight> flights) {
		this.flights = flights;
	}
	
	@Override
	public String toString() {
		return "EtermAvResponse [success=" + success + ", errorMsg=" + errorMsg
				+ ", flights=" + flights + "]";
	}
	
	/**
	 * 航班POJO类
	 * <p>
	 * @author yiming.yan
	 * @Date Nov 26, 2015
	 */
	public static class Flight {
		
		private String deptDate;
		private String deptAirport;
		private String destAirport;
		private String airCompany;
		private boolean transit;
		private List<FlightSegment> flightSegments;
		
		public String getDeptDate() {
			return deptDate;
		}
		public void setDeptDate(String deptDate) {
			this.deptDate = deptDate;
		}
		public String getDeptAirport() {
			return deptAirport;
		}
		public void setDeptAirport(String deptAirport) {
			this.deptAirport = deptAirport;
		}
		public String getDestAirport() {
			return destAirport;
		}
		public void setDestAirport(String destAirport) {
			this.destAirport = destAirport;
		}
		public String getAirCompany() {
			return airCompany;
		}
		public void setAirCompany(String airCompany) {
			this.airCompany = airCompany;
		}
		public boolean isTransit() {
			return transit;
		}
		public void setTransit(boolean transit) {
			this.transit = transit;
		}
		public List<FlightSegment> getFlightSegments() {
			return flightSegments;
		}
		public void setFlightSegments(List<FlightSegment> flightSegments) {
			this.flightSegments = flightSegments;
		}
		
		@Override
		public String toString() {
			return "Flight [deptDate=" + deptDate + ", deptAirport="
					+ deptAirport + ", destAirport=" + destAirport
					+ ", airCompany=" + airCompany + ", transit=" + transit
					+ ", flightSegments=" + flightSegments + "]";
		}
		
		/**
		 * 航段POJO类
		 * <p>
		 * @author yiming.yan
		 * @Date Nov 26, 2015
		 */
		public static class FlightSegment {
			
			private String airCompany;
			private String flightNum;
			private boolean isShared;
			private String opAirCompany;
			private String opFlightNum;
			private String deptAirport;
			private String destAirport;
			private String deptDate;
			private String destDate;
			private String deptTime;
			private String destTime;
			private String deptTerminal;
			private String destTerminal;
			private String duration;
			private String aircraftType;
			private boolean stopover;
			private boolean reserveSeat;
			private String meal;
			private Map<String, String> cabinInfo;
			
			public String getAirCompany() {
				return airCompany;
			}
			public void setAirCompany(String airCompany) {
				this.airCompany = airCompany;
			}
			public String getFlightNum() {
				return flightNum;
			}
			public void setFlightNum(String flightNum) {
				this.flightNum = flightNum;
			}
			public boolean isShared() {
				return isShared;
			}
			public void setShared(boolean isShared) {
				this.isShared = isShared;
			}
			public String getOpAirCompany() {
				return opAirCompany;
			}
			public void setOpAirCompany(String opAirCompany) {
				this.opAirCompany = opAirCompany;
			}
			public String getOpFlightNum() {
				return opFlightNum;
			}
			public void setOpFlightNum(String opFlightNum) {
				this.opFlightNum = opFlightNum;
			}
			public String getDeptAirport() {
				return deptAirport;
			}
			public void setDeptAirport(String deptAirport) {
				this.deptAirport = deptAirport;
			}
			public String getDestAirport() {
				return destAirport;
			}
			public void setDestAirport(String destAirport) {
				this.destAirport = destAirport;
			}
			public String getDeptDate() {
				return deptDate;
			}
			public void setDeptDate(String deptDate) {
				this.deptDate = deptDate;
			}
			public String getDestDate() {
				return destDate;
			}
			public void setDestDate(String destDate) {
				this.destDate = destDate;
			}
			public String getDeptTime() {
				return deptTime;
			}
			public void setDeptTime(String deptTime) {
				this.deptTime = deptTime;
			}
			public String getDestTime() {
				return destTime;
			}
			public void setDestTime(String destTime) {
				this.destTime = destTime;
			}
			public String getDeptTerminal() {
				return deptTerminal;
			}
			public void setDeptTerminal(String deptTerminal) {
				this.deptTerminal = deptTerminal;
			}
			public String getDestTerminal() {
				return destTerminal;
			}
			public void setDestTerminal(String destTerminal) {
				this.destTerminal = destTerminal;
			}
			public String getDuration() {
				return duration;
			}
			public void setDuration(String duration) {
				this.duration = duration;
			}
			public String getAircraftType() {
				return aircraftType;
			}
			public void setAircraftType(String aircraftType) {
				this.aircraftType = aircraftType;
			}
			public boolean isStopover() {
				return stopover;
			}
			public void setStopover(boolean stopover) {
				this.stopover = stopover;
			}
			public boolean isReserveSeat() {
				return reserveSeat;
			}
			public void setReserveSeat(boolean reserveSeat) {
				this.reserveSeat = reserveSeat;
			}
			public String getMeal() {
				return meal;
			}
			public void setMeal(String meal) {
				this.meal = meal;
			}
			public Map<String, String> getCabinInfo() {
				return cabinInfo;
			}
			public void setCabinInfo(Map<String, String> cabinInfo) {
				this.cabinInfo = cabinInfo;
			}
			
			@Override
			public String toString() {
				return "FlightSegment [airCompany=" + airCompany
						+ ", flightNum=" + flightNum + ", isShared=" + isShared
						+ ", opAirCompany=" + opAirCompany + ", opFlightNum="
						+ opFlightNum + ", deptAirport=" + deptAirport
						+ ", destAirport=" + destAirport + ", deptDate=" + deptDate
						+ ", destDate=" + destDate + ", deptTime=" + deptTime
						+ ", destTime=" + destTime + ", deptTerminal="
						+ deptTerminal + ", destTerminal=" + destTerminal
						+ ", duration=" + duration + ", aircraftType="
						+ aircraftType + ", stopover=" + stopover
						+ ", reserveSeat=" + reserveSeat + ", meal=" + meal
						+ ", cabinInfo=" + cabinInfo + "]";
			}
		
		}
	
	}

}
