package com.travelzen.etermface.service.entity;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

//<Flights><Flight ElementNo="1" ID="1" Type="0" Carrier="HO" Flight="1123" BoardPoint="SHA" OffPoint="CSX" DepartureDate="2013-08-28" Class="Q" ActionCode="LL"/>
//</Flights>

@XStreamAlias("Flights")
public class FlightSegments {

	@XStreamAlias("Flight")
	public static class Flight {
		@XStreamAsAttribute
		public String ElementNo;
		
		@XStreamAsAttribute
		public String ID;
		
		@XStreamAsAttribute
		public String Type;
		
		@XStreamAsAttribute
		public String Carrier;
		
		@XStreamAsAttribute
		public String Flight;
		
		@XStreamAsAttribute
		public String BoardPoint;
		
		@XStreamAsAttribute
		public String OffPoint;
		
		@XStreamAsAttribute
		public String DepartureDate;
		
		@XStreamAsAttribute
		public String BookingClass;
		
		@XStreamAsAttribute
		public String ActionCode;

		@Override
		public String toString() {
			return "Flight [ElementNo=" + ElementNo + ", ID=" + ID + ", Type=" + Type + ", Carrier=" + Carrier
					+ ", Flight=" + Flight + ", BoardPoint=" + BoardPoint + ", OffPoint=" + OffPoint
					+ ", DepartureDate=" + DepartureDate + ", BookingClass=" + BookingClass + ", ActionCode="
					+ ActionCode + "]";
		}
	}

	@XStreamImplicit(itemFieldName = "Flight")
	public List<Flight> flightList;

	@Override
	public String toString() {
		return "FlightSegments [flightList=" + flightList + "]";
	}

}
