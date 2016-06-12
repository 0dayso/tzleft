/**
 * 
 */
package com.travelzen.etermface.service.entity;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * @author hongqiangmao
 *
 */
@XStreamAlias("QteParams")
public class QteParams {
	/**
	 * 价格
	 */
	private double price;
	
	/**
	 * 乘客类型
	 */
	private String passengerType;
	
	/**
	 * 航班列表
	 */
	@XStreamImplicit(itemFieldName="FlightInfoList")
	private List<FlightInfo> flightInfoList;

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getPassengerType() {
		return passengerType;
	}

	public void setPassengerType(String passengerType) {
		this.passengerType = passengerType;
	}

	public List<FlightInfo> getFlightInfoList() {
		return flightInfoList;
	}

	public void setFlightInfoList(List<FlightInfo> flightInfoList) {
		this.flightInfoList = flightInfoList;
	}

	@Override
	public String toString() {
		return "QteParams [price=" + price + ", passengerType=" + passengerType
				+ ", flightInfoList=" + flightInfoList + "]";
	}
	
	
	public static String convertToXml(QteParams qteParams){
		StaxDriver sd = new StaxDriver(new NoNameCoder());
		XStream xstream = new XStream(sd);
		xstream.processAnnotations(QteParams.class);
		xstream.processAnnotations(List.class);
		xstream.processAnnotations(FlightInfo.class);
		return xstream.toXML(qteParams);
	}
	
	public static String convertToXml(List<QteParams> qteParamsList){
		StaxDriver sd = new StaxDriver(new NoNameCoder());
		XStream xstream = new XStream(sd);
		xstream.processAnnotations(List.class);
		xstream.processAnnotations(QteParams.class);
		xstream.processAnnotations(FlightInfo.class);
		xstream.alias("QteParamsList", List.class);
		return xstream.toXML(qteParamsList);
	}
	
	public static QteParams convertToObject(String xml){
		StaxDriver sd = new StaxDriver(new NoNameCoder());
		XStream xstream = new XStream(sd);
		xstream.processAnnotations(List.class);
		xstream.processAnnotations(QteParams.class);
		xstream.processAnnotations(FlightInfo.class);
		return (QteParams)xstream.fromXML(xml);
	}
	
	public static List<QteParams> convertToObjectList(String xml){
		StaxDriver sd = new StaxDriver(new NoNameCoder());
		XStream xstream = new XStream(sd);
		xstream.processAnnotations(List.class);
		xstream.processAnnotations(QteParams.class);
		xstream.processAnnotations(FlightInfo.class);
		xstream.alias("QteParamsList", List.class);
		return (List<QteParams>)xstream.fromXML(xml);
	}
	
	public static void main(String[] args){
		QteParams qteParams= new QteParams();
		qteParams.setPrice(1200);
		qteParams.setPassengerType("ADT");
		
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		qteParams.setFlightInfoList(flightInfoList);
		
		FlightInfo flightInfo = new FlightInfo();
		flightInfoList.add(flightInfo);
		
		flightInfo.setFlightNo("MU5102");
		flightInfo.setFromAirPort("PEK");
		flightInfo.setToAirPort("PEK");
		
		List<QteParams> qteParamsList = new ArrayList<QteParams>();
		qteParamsList.add(qteParams);
		
		String xml = convertToXml(qteParamsList);
		System.out.println(xml);
		
		qteParamsList = convertToObjectList(xml);
		System.out.println(qteParamsList);
	}
}
