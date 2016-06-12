package com.travelzen.etermface.service.entity;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.travelzen.etermface.service.entity.pnr.convertor.CreateIntPNROARequest;
import com.travelzen.etermface.service.entity.pnr.convertor.CreateIntPNROARequest.Body.OTA_PNRRequest.FlightSegment;
import com.travelzen.etermface.service.entity.pnr.convertor.CreateIntPNROARequest.Body.OTA_PNRRequest.PassengerDetail;

public class IntPnrCreateRequest {

	private List<PassengerInfo> passengerList;
	
	private List<FlightInfo> flightInfoList;
	
	/**
	 * 出票时限
	 */
	private String tktl;
	
	/**
	 * 行动代码
	 */
	private String actionCode;
	
	/**
	 * 座位数
	 */
	private int seatNum;
	
	/**
	 * 联系电话
	 */
	private String telephone;
	
	/**
	 * 出票office号
	 */
	private String officeNo;
	
	/**
	 * 授权office号
	 */
	private String authOfficeNo;
	
	public List<PassengerInfo> getPassengerList() {
		return passengerList;
	}

	public void setPassengerList(List<PassengerInfo> passengerList) {
		this.passengerList = passengerList;
	}

	public List<FlightInfo> getFlightInfoList() {
		return flightInfoList;
	}

	public void setFlightInfoList(List<FlightInfo> flightInfoList) {
		this.flightInfoList = flightInfoList;
	}

	public String getTktl() {
		return tktl;
	}

	public void setTktl(String tktl) {
		this.tktl = tktl;
	}
	
	public int getSeatNum() {
		return seatNum;
	}

	public void setSeatNum(int seatNum) {
		this.seatNum = seatNum;
	}

	public String getActionCode() {
		return actionCode;
	}

	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}

	public String getOfficeNo() {
		return officeNo;
	}

	public void setOfficeNo(String officeNo) {
		this.officeNo = officeNo;
	}
	
	public String getAuthOfficeNo() {
		return authOfficeNo;
	}

	public void setAuthOfficeNo(String authOfficeNo) {
		this.authOfficeNo = authOfficeNo;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public static String convertToXml(IntPnrCreateRequest request){
		XStream xstream = getXStream();
		return xstream.toXML(request);
	}
	
	
	public static IntPnrCreateRequest convertToObject(String xml){
		XStream xstream = getXStream();
		return (IntPnrCreateRequest)xstream.fromXML(xml);
	}
	
	
	public static XStream getXStream(){
		XStream xstream = new XStream();
		xstream.processAnnotations(List.class);
		xstream.processAnnotations(PassengerInfo.class);
		xstream.processAnnotations(FlightInfo.class);
		xstream.alias("IntPnrCreateRequest", IntPnrCreateRequest.class);
		xstream.alias("PassengerInfo", PassengerInfo.class);
		xstream.alias("FlightInfo", FlightInfo.class);
		return xstream;
	}
	
	
	
	public static IntPnrCreateRequest convertFromOARequest(CreateIntPNROARequest oaRequest){
		if(null == oaRequest){
			return null;
		}
		IntPnrCreateRequest request = new IntPnrCreateRequest();
		
		List<FlightInfo> flightInfoList  = new ArrayList<FlightInfo>();
		List<PassengerInfo> passengerList = new ArrayList<PassengerInfo>();
		
		request.setFlightInfoList(flightInfoList);
		request.setPassengerList(passengerList);
		request.setTelephone("02132069555");
		
		if(null != oaRequest.body.otaPNRRequest){
			if(null != oaRequest.body.otaPNRRequest.flightInfo){
				for(FlightSegment flight:oaRequest.body.otaPNRRequest.flightInfo){
					FlightInfo flightInfo = new FlightInfo();
					flightInfo.setCabinCode(flight.Class);
					flightInfo.setCarrier(flight.AirCo);
					flightInfo.setFromCity(flight.FromCity);
					flightInfo.setToCity(flight.DestCity);
					flightInfo.setFlightNo(flight.FlightNumer);
					flightInfo.setFromDate(flight.FromDate);
					flightInfoList.add(flightInfo);
				}
			}
			
			if(null != oaRequest.body.otaPNRRequest.passenger){
				for(PassengerDetail passenger:oaRequest.body.otaPNRRequest.passenger){
					PassengerInfo passengerInfo = new PassengerInfo();
					passengerInfo.setBirthDay(passenger.BirthDay);
					passengerInfo.setCerCountry(passenger.CerCountry);
					passengerInfo.setCerNo(passenger.CerNo);
					passengerInfo.setCerType(passenger.CerType);
					passengerInfo.setCerValidity(passenger.CerValidity);
					passengerInfo.setGender(passenger.Sex);
					passengerInfo.setSurName(passenger.SurName);
			        passengerInfo.setSurNameWithAppellation(passenger.SurNameWithAppellation);
					passengerInfo.setNationality(passenger.Nationality);
					passengerInfo.setPassengerType(passenger.PassengerType);
					passengerList.add(passengerInfo);
				}
			}
		}
		return request;		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
