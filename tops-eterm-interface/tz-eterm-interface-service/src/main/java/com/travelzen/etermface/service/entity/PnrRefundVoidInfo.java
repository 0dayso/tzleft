package com.travelzen.etermface.service.entity;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * PNR退废结果
 * @author hetao
 */
@XStreamAlias("pnrRefundVoidInfo")
public class PnrRefundVoidInfo {

	private String returnCode;
	private String message;
	//是否退废成功
	private boolean isRefundedVoid;
	//票号list
	@XStreamImplicit
	private List<Eticket> tickets;
	//航段list
	@XStreamImplicit
	private List<FlightSegment> flightSegments;
	
	public boolean isRefundedVoid() {
		return isRefundedVoid;
	}

	public void setRefundedVoid(boolean isRefundedVoid) {
		this.isRefundedVoid = isRefundedVoid;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public List<Eticket> getTickets() {
		return tickets;
	}

	public void setTickets(List<Eticket> tickets) {
		this.tickets = tickets;
	}

	public List<FlightSegment> getFlightSegments() {
		return flightSegments;
	}

	public void setFlightSegments(List<FlightSegment> flightSegments) {
		this.flightSegments = flightSegments;
	}
	
	@XStreamAlias("eticket")
	public static class Eticket {
		private String ticketNo;
		
		public Eticket() {
			
		}
		
		public Eticket(String tNo) {
			this.ticketNo = tNo;
		}
		
		

		public String getTicketNo() {
			return ticketNo;
		}

		public void setTicketNo(String ticketNo) {
			this.ticketNo = ticketNo;
		}
		
		@Override
		public int hashCode() {
			return this.ticketNo.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			
			if(obj instanceof Eticket) {
				Eticket e = (Eticket)obj;
				if(null==e.getTicketNo() || null==this.getTicketNo()) {
					return false;
				}
				return e.getTicketNo().equals(this.getTicketNo());
			}
			
			return false;
		}
	}

	@XStreamAlias("flightSegment")
	public static class FlightSegment {
		
		private String departureAirPort;
		private String arriveAirPort;
		
		public FlightSegment() {
			
		}
		
		public FlightSegment(String departureAirPort, String arriveAirPort) {
			this.departureAirPort = departureAirPort;
			this.arriveAirPort = arriveAirPort;
		}
		
		public String getDepartureAirPort() {
			return departureAirPort;
		}
		public void setDepartureAirPort(String departureAirPort) {
			this.departureAirPort = departureAirPort;
		}
		public String getArriveAirPort() {
			return arriveAirPort;
		}
		public void setArriveAirPort(String arriveAirPort) {
			this.arriveAirPort = arriveAirPort;
		}
	}

	public String toXML() {
		StaxDriver sd = new StaxDriver(new NoNameCoder());
		XStream xstream = new XStream(sd);
//		XStream xstream = new XStream();
		xstream.processAnnotations(this.getClass());
		return xstream.toXML(this);
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
	public static void main(String[] args) {
		
		PnrRefundVoidInfo infos = new PnrRefundVoidInfo();
		
		infos.setMessage("mess");
		infos.setRefundedVoid(true);
		infos.setReturnCode("123");
		
		List<FlightSegment> segments = new ArrayList<>();
		FlightSegment seg1 = new FlightSegment();
		FlightSegment seg2 = new FlightSegment();
		seg1.setArriveAirPort("pvg");
		seg1.setDepartureAirPort("sha");
		seg2.setArriveAirPort("pvg");
		seg2.setDepartureAirPort("sha");
		segments.add(seg1);
		segments.add(seg2);
		infos.setFlightSegments(segments);
		
		List<Eticket> tickets = new ArrayList<>();
		Eticket et1 = new Eticket();
		Eticket et2 = new Eticket();
		et1.setTicketNo("1234567890");
		et2.setTicketNo("0987654321");
		tickets.add(et1);
		tickets.add(et2);
		infos.setTickets(tickets);
		
		System.out.println(infos.toXML());
	}
}
