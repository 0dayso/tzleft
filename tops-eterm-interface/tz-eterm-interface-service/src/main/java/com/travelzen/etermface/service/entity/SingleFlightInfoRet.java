package com.travelzen.etermface.service.entity;

import java.io.Serializable;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;

@XStreamAlias("SingleFlightInfoRet")
public class SingleFlightInfoRet implements Serializable {

	private static final long serialVersionUID = -5849445349218695640L;
	private boolean success;
	private String avResultStr;
	private String message;
	private List<FlightInfo> flightInfos;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getAvResultStr() {
		return avResultStr;
	}

	public void setAvResultStr(String avResultStr) {
		this.avResultStr = avResultStr;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<FlightInfo> getFlightInfos() {
		return flightInfos;
	}

	public void setFlightInfos(List<FlightInfo> flightInfos) {
		this.flightInfos = flightInfos;
	}

	@Override
	public String toString() {
		return "SingleFlightInfoRet [success=" + success + ", avResultStr=" + avResultStr + ", message=" + message
				+ ", flightInfos=" + flightInfos + "]";
	}

	public String toXML(){
		StaxDriver sd = new StaxDriver(new NoNameCoder());
		XStream xstream = new XStream(sd);
		xstream.processAnnotations(SingleFlightInfoRet.class);
		xstream.processAnnotations(FlightInfo.class);
		return xstream.toXML(this);
	}
}
