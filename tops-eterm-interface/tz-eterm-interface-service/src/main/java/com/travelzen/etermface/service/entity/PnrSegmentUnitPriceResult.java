package com.travelzen.etermface.service.entity;

import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * 解析pnr分段价格的结果类
 * @author hetao
 */
@XStreamAlias("pnrSegUnitPriceResult")
public class PnrSegmentUnitPriceResult {
	
	private String returnCode;
	private String message;
	@XStreamImplicit
	private List<SegmentUnitPrice> segmentUnitPrices;
	
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

	public List<SegmentUnitPrice> getSegmentUnitPrices() {
		return segmentUnitPrices;
	}

	public void setSegmentUnitPrices(List<SegmentUnitPrice> segmentUnitPrices) {
		this.segmentUnitPrices = segmentUnitPrices;
	}
	
	@XStreamAlias("segUnitPrice")
	public static class SegmentUnitPrice {
		
		@XStreamImplicit
		private List<String> cities;
		private boolean isDomestic;
		private double price;
		
		public List<String> getCities() {
			return cities;
		}
		public void setCities(List<String> cities) {
			this.cities = cities;
		}
		public double getPrice() {
			return price;
		}
		public void setPrice(double price) {
			this.price = price;
		}
		public boolean isDomestic() {
			return isDomestic;
		}
		public void setDomestic(boolean isDomestic) {
			this.isDomestic = isDomestic;
		}
		
	}
	
	/**
	 * new a instance for class PnrSegmentUnitPriceResult<br />
	 * replace the constructor method
	 * @return
	 */
	public static PnrSegmentUnitPriceResult newInstance() {
		return new PnrSegmentUnitPriceResult();
	}
	
	public String toXML() {
		StaxDriver sd = new StaxDriver(new NoNameCoder());
		XStream xstream = new XStream(sd);
//		XStream xstream = new XStream();
		xstream.processAnnotations(this.getClass());
		return xstream.toXML(this);
	}
}
