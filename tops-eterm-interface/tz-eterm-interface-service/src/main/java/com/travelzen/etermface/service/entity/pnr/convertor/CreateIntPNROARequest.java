package com.travelzen.etermface.service.entity.pnr.convertor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("request")
public class CreateIntPNROARequest {
	
	@XStreamAlias("header")
	public Header header;
	
	@XStreamAlias("body")
	public Body body;
	
	@XStreamAlias("header")
	public static class Header {
		public String accountID;
		public String serviceName;
		public String digitalSign;
		public String gzip;
	}
	
	@XStreamAlias("body")
	public static class Body{
		@XStreamAlias("OTA_PNRRequest")
		public OTA_PNRRequest otaPNRRequest;
		
//		@XStreamAlias("OTA_PNRRequest")
		public static class OTA_PNRRequest{
			@XStreamAlias("FlightInfo")
			public List<FlightSegment> flightInfo;
			@XStreamAlias("Passenger")
			public List<PassengerDetail> passenger;
			@XStreamAlias("Fare")
			public Fare fare;
			@XStreamAlias("OP")
			public OP op;
			
			@XStreamAlias("FlightSegment")
			public static class FlightSegment{
				public String FromCity;
				public String DestCity;
				public String AirCo;
				public String FlightNumer;
				public String Class;
				public String QueryDate;
				public String FromDate;
				public String FromTime;
				public String DestDate;
				public String DestTime;
			}
			
			@XStreamAlias("PassengerDetail")
			public static class PassengerDetail{
				public String SurName;
				public String SurNameWithAppellation;
				public String CerType;
				public String CerCountry;
				public String CerNo;
				public String Nationality;
				public String BirthDay;
				public String Sex;
				public String CerValidity;
				public String PassengerType;
			}
			
			@XStreamAlias("Fare")
			public static class Fare{
				public String TotalPrice;
				public String NetFare;
				public String AdtPrice;
				public String ChdPrice;
				public String FareNo;
				public String Tax;
				public String TaxStr;
			}
			
			@XStreamAlias("OP")
			public static class OP{
				public String Operator;
				public String AirCo;
				public String PNRUser;
			}
		}
	}
	
	
	public static CreateIntPNROARequest convertFromXML(String xml){
		XStream xstream = new XStream();
		xstream.processAnnotations(CreateIntPNROARequest.class);
		CreateIntPNROARequest request = null;
		try {
			request = (CreateIntPNROARequest) xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return request;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BufferedReader reader1 = null;
		StringBuffer buffer = new StringBuffer();
		try {
			reader1 = new BufferedReader(new FileReader("/home/hongqiangmao/workspace/tz/tops-eterm-interface/tz-eterm-interface-service/src/test/resources/createpnr/request.xml"));
			String temp = null;
			while ((temp = reader1.readLine()) != null) {
				temp =temp.trim();
				buffer.append(temp);
				buffer.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		XStream xstream = new XStream();
		xstream.processAnnotations(CreateIntPNROARequest.class);
//		System.out.println(buffer.toString());
		CreateIntPNROARequest request = (CreateIntPNROARequest)xstream.fromXML(buffer.toString());
		System.out.println(request.header.accountID);
		
	}

}
