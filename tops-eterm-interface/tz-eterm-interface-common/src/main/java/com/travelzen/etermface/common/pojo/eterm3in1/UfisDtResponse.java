package com.travelzen.etermface.common.pojo.eterm3in1;

import java.util.List;

/**
 * Ufis dt　指令返回结果类
 * <p>
 * @author yiming.yan
 * @Date Dec 15, 2015
 */
public class UfisDtResponse implements UfisResponse {
	
	private boolean success;
	/**
	 * 错误信息
	 */
	private String errorMsg;
	/**
	 * PNR号
	 */
	private String pnrNo;
	/**
	 * 大编码
	 */
	private String airlinePnrNo;
	/**
	 * 航协代码
	 */
	private String iataNo;
	/**
	 * 客票信息
	 */
	private Ticket ticket;
	/**
	 * 乘客信息
	 */
	private Passenger passenger;
	/**
	 * 航程信息
	 */
	private List<FlightSegment> flightSegments;
	/**
	 * 价格信息
	 */
	private Fare fare;
	
	public UfisDtResponse() {
	}
	
	public UfisDtResponse(boolean success, String errorMsg) {
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
	public String getPnrNo() {
		return pnrNo;
	}
	public void setPnrNo(String pnrNo) {
		this.pnrNo = pnrNo;
	}
	public String getAirlinePnrNo() {
		return airlinePnrNo;
	}
	public void setAirlinePnrNo(String airlinePnrNo) {
		this.airlinePnrNo = airlinePnrNo;
	}
	public String getIataNo() {
		return iataNo;
	}
	public void setIataNo(String iataNo) {
		this.iataNo = iataNo;
	}
	public Ticket getTicket() {
		return ticket;
	}
	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}
	public Passenger getPassenger() {
		return passenger;
	}
	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}
	public List<FlightSegment> getFlightSegments() {
		return flightSegments;
	}
	public void setFlightSegments(List<FlightSegment> flightSegments) {
		this.flightSegments = flightSegments;
	}
	public Fare getFare() {
		return fare;
	}
	public void setFare(Fare fare) {
		this.fare = fare;
	}
	
	@Override
	public String toString() {
		return "UfisDtResponse [success=" + success + ", errorMsg=" + errorMsg
				+ ", pnrNo=" + pnrNo + ", airlinePnrNo=" + airlinePnrNo
				+ ", iataNo=" + iataNo + ", ticket=" + ticket + ", passenger="
				+ passenger + ", flightSegments=" + flightSegments + ", fare="
				+ fare + "]";
	}
	
	public static class Ticket {
		private String ticketNo;
		private String receiptPnt;
		private String conjTicket;
		private String ticketStatus;
		private String issuedBy;
		private String issuedDate;
		private String er;
		
		public String getTicketNo() {
			return ticketNo;
		}
		public void setTicketNo(String ticketNo) {
			this.ticketNo = ticketNo;
		}
		public String getReceiptPnt() {
			return receiptPnt;
		}
		public void setReceiptPnt(String receiptPnt) {
			this.receiptPnt = receiptPnt;
		}
		public String getConjTicket() {
			return conjTicket;
		}
		public void setConjTicket(String conjTicket) {
			this.conjTicket = conjTicket;
		}
		public String getTicketStatus() {
			return ticketStatus;
		}
		public void setTicketStatus(String ticketStatus) {
			this.ticketStatus = ticketStatus;
		}
		public String getIssuedBy() {
			return issuedBy;
		}
		public void setIssuedBy(String issuedBy) {
			this.issuedBy = issuedBy;
		}
		public String getIssuedDate() {
			return issuedDate;
		}
		public void setIssuedDate(String issuedDate) {
			this.issuedDate = issuedDate;
		}
		public String getEr() {
			return er;
		}
		public void setEr(String er) {
			this.er = er;
		}

		@Override
		public String toString() {
			return "Ticket [ticketNo=" + ticketNo + ", receiptPnt="
					+ receiptPnt + ", conjTicket=" + conjTicket
					+ ", ticketStatus=" + ticketStatus + ", issuedBy="
					+ issuedBy + ", issuedDate=" + issuedDate + ", er=" + er
					+ "]";
		}
	}
	
	public static class Passenger {
		private String name;
		private IdCard idCard;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public IdCard getIdCard() {
			return idCard;
		}
		public void setIdCard(IdCard idCard) {
			this.idCard = idCard;
		}

		@Override
		public String toString() {
			return "Passenger [name=" + name + ", idCard=" + idCard + "]";
		}

		public static class IdCard {
			private String type;
			private String number;
			
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getNumber() {
				return number;
			}
			public void setNumber(String number) {
				this.number = number;
			}

			@Override
			public String toString() {
				return "IdCard [type=" + type + ", number=" + number + "]";
			}
		}
	}
	
	public static class FlightSegment {
		private FlightType flightType;
		private String airline;
		private String flightNo;
		private String deptDate;
		private String cabinClass;
		private String seatStatus;
		private String segStatus;
		private List<Leg> legs;
		
		public FlightType getFlightType() {
			return flightType;
		}
		public void setFlightType(FlightType flightType) {
			this.flightType = flightType;
		}
		public String getAirline() {
			return airline;
		}
		public void setAirline(String airline) {
			this.airline = airline;
		}
		public String getFlightNo() {
			return flightNo;
		}
		public void setFlightNo(String flightNo) {
			this.flightNo = flightNo;
		}
		public String getDeptDate() {
			return deptDate;
		}
		public void setDeptDate(String deptDate) {
			this.deptDate = deptDate;
		}
		public String getCabinClass() {
			return cabinClass;
		}
		public void setCabinClass(String cabinClass) {
			this.cabinClass = cabinClass;
		}
		public String getSeatStatus() {
			return seatStatus;
		}
		public void setSeatStatus(String seatStatus) {
			this.seatStatus = seatStatus;
		}
		public String getSegStatus() {
			return segStatus;
		}
		public void setSegStatus(String segStatus) {
			this.segStatus = segStatus;
		}
		public List<Leg> getLegs() {
			return legs;
		}
		public void setLegs(List<Leg> legs) {
			this.legs = legs;
		}
		
		@Override
		public String toString() {
			return "FlightSegment [flightType=" + flightType + ", airline="
					+ airline + ", flightNo=" + flightNo + ", deptDate="
					+ deptDate + ", cabinClass=" + cabinClass + ", seatStatus="
					+ seatStatus + ", segStatus=" + segStatus + ", legs="
					+ legs + "]";
		}

		public static enum FlightType {
			NORMAL, OPEN, ARNK;
		}

		public static class Leg {
			private String deptAirport;
			private String arrAirport;
			private String deptAirportEn;
			private String arrAirportEn;
			private String deptAirportCh;
			private String arrAirportCh;
			private String deptTerminal;
			private String arrTerminal;
			private String deptDate;
			private String arrDate;
			private String deptTime;
			private String arrTime;
			private String fareBasis;
			private String baggage;
			
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
			public String getDeptAirportEn() {
				return deptAirportEn;
			}
			public void setDeptAirportEn(String deptAirportEn) {
				this.deptAirportEn = deptAirportEn;
			}
			public String getArrAirportEn() {
				return arrAirportEn;
			}
			public void setArrAirportEn(String arrAirportEn) {
				this.arrAirportEn = arrAirportEn;
			}
			public String getDeptAirportCh() {
				return deptAirportCh;
			}
			public void setDeptAirportCh(String deptAirportCh) {
				this.deptAirportCh = deptAirportCh;
			}
			public String getArrAirportCh() {
				return arrAirportCh;
			}
			public void setArrAirportCh(String arrAirportCh) {
				this.arrAirportCh = arrAirportCh;
			}
			public String getDeptTerminal() {
				return deptTerminal;
			}
			public void setDeptTerminal(String deptTerminal) {
				this.deptTerminal = deptTerminal;
			}
			public String getArrTerminal() {
				return arrTerminal;
			}
			public void setArrTerminal(String arrTerminal) {
				this.arrTerminal = arrTerminal;
			}
			public String getDeptDate() {
				return deptDate;
			}
			public void setDeptDate(String deptDate) {
				this.deptDate = deptDate;
			}
			public String getArrDate() {
				return arrDate;
			}
			public void setArrDate(String arrDate) {
				this.arrDate = arrDate;
			}
			public String getDeptTime() {
				return deptTime;
			}
			public void setDeptTime(String deptTime) {
				this.deptTime = deptTime;
			}
			public String getArrTime() {
				return arrTime;
			}
			public void setArrTime(String arrTime) {
				this.arrTime = arrTime;
			}
			public String getFareBasis() {
				return fareBasis;
			}
			public void setFareBasis(String fareBasis) {
				this.fareBasis = fareBasis;
			}
			public String getBaggage() {
				return baggage;
			}
			public void setBaggage(String baggage) {
				this.baggage = baggage;
			}
			
			@Override
			public String toString() {
				return "Leg [deptAirport=" + deptAirport + ", arrAirport="
						+ arrAirport + ", deptAirportEn=" + deptAirportEn
						+ ", arrAirportEn=" + arrAirportEn + ", deptAirportCh="
						+ deptAirportCh + ", arrAirportCh=" + arrAirportCh
						+ ", deptTerminal=" + deptTerminal + ", arrTerminal="
						+ arrTerminal + ", deptDate=" + deptDate + ", arrDate="
						+ arrDate + ", deptTime=" + deptTime + ", arrTime="
						+ arrTime + ", fareBasis=" + fareBasis + ", baggage="
						+ baggage + "]";
			}
		}
		
	}
	
	public static class Fare {
		private FareDetail ticketFare;
		private FareDetail cnTax;
		private FareDetail yqTax;
		private FareDetail obTax;
		private FareDetail totalFare;
		private String fareCompute;
		private String paymentMode;
		
		public FareDetail getTicketFare() {
			return ticketFare;
		}
		public void setTicketFare(FareDetail ticketFare) {
			this.ticketFare = ticketFare;
		}
		public FareDetail getCnTax() {
			return cnTax;
		}
		public void setCnTax(FareDetail cnTax) {
			this.cnTax = cnTax;
		}
		public FareDetail getYqTax() {
			return yqTax;
		}
		public void setYqTax(FareDetail yqTax) {
			this.yqTax = yqTax;
		}
		public FareDetail getObTax() {
			return obTax;
		}
		public void setObTax(FareDetail obTax) {
			this.obTax = obTax;
		}
		public FareDetail getTotalFare() {
			return totalFare;
		}
		public void setTotalFare(FareDetail totalFare) {
			this.totalFare = totalFare;
		}
		public String getFareCompute() {
			return fareCompute;
		}
		public void setFareCompute(String fareCompute) {
			this.fareCompute = fareCompute;
		}
		public String getPaymentMode() {
			return paymentMode;
		}
		public void setPaymentMode(String paymentMode) {
			this.paymentMode = paymentMode;
		}
		
		@Override
		public String toString() {
			return "Fare [ticketFare=" + ticketFare + ", cnTax=" + cnTax
					+ ", yqTax=" + yqTax + ", obTax=" + obTax + ", totalFare="
					+ totalFare + ", fareCompute=" + fareCompute
					+ ", paymentMode=" + paymentMode + "]";
		}
		
		public static class FareDetail {
			private double amount;
			private String currency;
			
			public double getAmount() {
				return amount;
			}
			public void setAmount(double amount) {
				this.amount = amount;
			}
			public String getCurrency() {
				return currency;
			}
			public void setCurrency(String currency) {
				this.currency = currency;
			}
			
			@Override
			public String toString() {
				return "FareDetail [amount=" + amount + ", currency=" + currency
						+ "]";
			}
		}
	}

}
