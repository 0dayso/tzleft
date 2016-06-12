package com.travelzen.etermface.service.entity;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;

@XStreamAlias("QteResult")
public class QteResult {
	
	/**
	 * 返回状态
	 */
	private String returnCode;
	/**
	 * 错误信息
	 */
	private String message;
	/**
	 * 价格
	 */
	private double price;
	
	/**
	 * 乘客类型
	 */
	private String passengerType;
	
	/**
	 * 分段价格
	 */
	@XStreamImplicit(itemFieldName="SegmentsPriceList")
	private List<SegmentsPrice> segmentsPriceList;


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


	public List<SegmentsPrice> getSegmentsPriceList() {
		return segmentsPriceList;
	}


	public void setSegmentsPriceList(List<SegmentsPrice> segmentsPriceList) {
		this.segmentsPriceList = segmentsPriceList;
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

	
	@Override
	public String toString() {
		return "QteResult [returnCode=" + returnCode + ", message=" + message
				+ ", price=" + price + ", passengerType=" + passengerType
				+ ", segmentsPriceList=" + segmentsPriceList + "]";
	}


	public static String convertToXml(QteResult qteResult){
		StaxDriver sd = new StaxDriver(new NoNameCoder());
		XStream xstream = new XStream(sd);
		xstream.processAnnotations(QteResult.class);
		xstream.processAnnotations(List.class);
		xstream.processAnnotations(SegmentsPrice.class);
		xstream.processAnnotations(FlightInfo.class);
		return xstream.toXML(qteResult);
	}
	
	public static String convertToXml(List<QteResult> qteResultList){
		StaxDriver sd = new StaxDriver(new NoNameCoder());
		XStream xstream = new XStream();
		xstream.processAnnotations(List.class);
		xstream.processAnnotations(QteResult.class);
		xstream.processAnnotations(SegmentsPrice.class);
		xstream.processAnnotations(FlightInfo.class);
		xstream.alias("QteResultList", List.class);
		return xstream.toXML(qteResultList);
	}
	
	public static QteResult convertToObject(String xml){
		StaxDriver sd = new StaxDriver(new NoNameCoder());
		XStream xstream = new XStream(sd);
		xstream.processAnnotations(List.class);
		xstream.processAnnotations(QteResult.class);
		xstream.processAnnotations(SegmentsPrice.class);
		xstream.processAnnotations(FlightInfo.class);
		return (QteResult)xstream.fromXML(xml);
	}
	
	public static List<QteResult> convertToObjectList(String xml){
		StaxDriver sd = new StaxDriver(new NoNameCoder());
		XStream xstream = new XStream(sd);
		xstream.processAnnotations(List.class);
		xstream.processAnnotations(QteResult.class);
		xstream.processAnnotations(SegmentsPrice.class);
		xstream.processAnnotations(FlightInfo.class);
		xstream.alias("QteResultList", List.class);
		return (List<QteResult>)xstream.fromXML(xml);
	}
	
	public static void main(String[] args){
		QteResult qteResult= new QteResult();
		qteResult.setPrice(1200);
		qteResult.setPassengerType("ADT");
		
		List<SegmentsPrice> segmentsPriceList = new ArrayList<SegmentsPrice>();
		SegmentsPrice segmentsPrice = new SegmentsPrice();
		segmentsPriceList.add(segmentsPrice);
		qteResult.setSegmentsPriceList(segmentsPriceList);
		
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		segmentsPrice.setFlightInfoList(flightInfoList);
		
		FlightInfo flightInfo = new FlightInfo();
		flightInfoList.add(flightInfo);
		
		flightInfo.setFlightNo("MU5102");
		flightInfo.setFromAirPort("PEK");
		flightInfo.setToAirPort("PEK");
		
		List<QteResult> qteResultList = new ArrayList<QteResult>();
		qteResultList.add(qteResult);
		
		String xml = convertToXml(qteResultList);
		System.out.println(xml);
		
		qteResultList = convertToObjectList(xml);
		System.out.println(qteResultList);
	}
	
}
