package com.travelzen.etermface.service.entity.dom;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("response")
public class DomFlightResponse {

	
	@XStreamAlias("header")
	public static class Header {
		public String code ;
		public String content ;
	}
	
	@XStreamAlias("body")
	public static class Body {
		public lList  lList;
	}
	
	public Header header;
	public Body body ;
	
	
}
