package com.travelzen.etermface.service.createpnr;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.thoughtworks.xstream.XStream;
import com.travelzen.etermface.service.entity.FlightInfo;
import com.travelzen.etermface.service.entity.FlightSegments;
import com.travelzen.etermface.service.entity.IntPnrCreateRequest;
import com.travelzen.etermface.service.entity.PassengerInfo;
import com.travelzen.etermface.service.entity.FlightSegments.Flight;

public class CreatePnrReturnClass<T> {

	private T object = null;
	private Object[] statusObjects = new Object[0];

	public CreatePnrReturnClass() {
	}
	
	public CreatePnrReturnClass(CreatePnrReturnCode returnCode) {
		this.returnCode = returnCode;
	}

	// error
	CreatePnrReturnCode returnCode = CreatePnrReturnCode.SUCCESS;
	
	private String pnr;
	
	private FlightSegments flightSegments;

	public void setStatusObjects(Object[] statusObjects) {
		this.statusObjects = statusObjects;
	}

	public int getStatus() {
		return returnCode.getErrorCode();
	}

//	public void setErrorDetail(String detail) {
//		returnCode.setErrorDetail(detail);
//	}
//


	public String getMessage(String format, String separator) {
		return String.format(format, StringUtils.join(statusObjects, separator));
	}

	public String getMessage(String format) {
		return String.format(format, statusObjects);
	}

	public String getMessage() {

		if (statusObjects.length != 0) {
			return String.format(returnCode.getErrorDetail() + ":%s", StringUtils.join(statusObjects, ","));
		} else {
			return returnCode.getErrorDetail();
		}

	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public boolean isSuccess() {
		return returnCode.errorCode != 0;
	}

	public boolean isError() {
		return returnCode.errorCode > 0;
	}

	public CreatePnrReturnCode getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(CreatePnrReturnCode returnCode) {
		this.returnCode = returnCode;
	}

	/**
	 *   just for  compatibility
	 * @param createPnrReturnCode
	 */
	public void setStatus(CreatePnrReturnCode createPnrReturnCode) {
		this.returnCode = createPnrReturnCode;
	}
	

	public Object[] getStatusObjects() {
		return statusObjects;
	}

	public String getPnr() {
		return pnr;
	}

	public void setPnr(String pnr) {
		this.pnr = pnr;
	}

	public FlightSegments getFlightSegments() {
		return flightSegments;
	}

	public void setFlightSegments(FlightSegments flightSegments) {
		this.flightSegments = flightSegments;
	}

	@Override
	public String toString() {
		return "CreatePnrReturnClass [object=" + object + ", statusObjects=" + Arrays.toString(statusObjects)
				+ ", returnCode=" + returnCode + ", pnr=" + pnr + ", flightSegments=" + flightSegments + "]";
	}
	
	
	public static String convertToXml(CreatePnrReturnClass request){
		XStream xstream = getXStream();
		return xstream.toXML(request);
	}
	
	
	public static CreatePnrReturnClass convertToObject(String xml){
		XStream xstream = getXStream();
		return (CreatePnrReturnClass)xstream.fromXML(xml);
	}
	
	public static XStream getXStream(){
		XStream xstream = new XStream();
		xstream.processAnnotations(CreatePnrReturnClass.class);
		xstream.processAnnotations(CreatePnrReturnCode.class);
		xstream.processAnnotations(FlightSegments.class);
		xstream.processAnnotations(Flight.class);
		xstream.alias("CreatePnrReturnClass", CreatePnrReturnClass.class);
		xstream.alias("CreatePnrReturnCode", CreatePnrReturnCode.class);
		xstream.alias("FlightSegments", FlightSegments.class);
		xstream.alias("Flight", Flight.class);
		return xstream;
	}
}
