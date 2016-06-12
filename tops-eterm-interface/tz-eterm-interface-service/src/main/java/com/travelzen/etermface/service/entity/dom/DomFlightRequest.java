package com.travelzen.etermface.service.entity.dom;

import com.thoughtworks.xstream.annotations.XStreamAlias;


//- <request>
//- <header>
//  <accountID>cs005</accountID> 
//  <serviceName>getFlightPrice</serviceName> 
//  <digitalSign>F74F684FEC21E74C</digitalSign> 
//  <gzip>0</gzip> 
//  </header>
//- <body>
//  <oa>SHA</oa> 
//  <aa>CSX</aa> 
//  <fDate>2012-12-16</fDate> 
//  <cabinClass /> 
//  <carrier /> 
//  </body>
//  </request>
  
@XStreamAlias("request")
public class DomFlightRequest {

	@XStreamAlias("header")
	public static class Header {
		public String accountID;
		public String serviceName;
		public String digitalSign;
		public String gzip;
	}

	@XStreamAlias("body")
	public static class Body {
		String oa;
		String aa;
		String fDate;
		String cabinClass;
		String carrier ;
	}
 

	Header header = new Header();
	Body body = new Body();
}
