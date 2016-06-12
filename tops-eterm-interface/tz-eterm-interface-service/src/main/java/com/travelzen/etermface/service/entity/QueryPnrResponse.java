package com.travelzen.etermface.service.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;



 

@XStreamAlias("response")
public class QueryPnrResponse {

	@XStreamAlias("header")
	public static class Header {
		public String pnr;
		public String returnCode;
		public String status;
		public String errorInfo;
	}

	@XStreamAlias("body")
	public static class Body {
	}
	
	public Header header;
	public Body body;
	
	public static void main(String argv []) {
		
//		QueryPnrResponse qpr  = new QueryPnrResponse();
//		
//		
//		qpr.header = new QueryPnrResponse.Header();
//		qpr.body = new QueryPnrResponse.Body();
//		
//		
//		qpr.header.accountID ="acc";
//		qpr.body.reqcontent =  new QueryPnrResponse.Body.ReqContent();
//		
//		qpr.body.reqcontent.name ="myname";
//		qpr.body.reqcontent.flightNumber="MU5701";
//		qpr.body.reqcontent.fromDate="2013-08-31";
//		qpr.body.reqcontent.cerNo ="310225198205260000";
//		
//		
//		XStream xstream = new XStream();
//		xstream.processAnnotations(QueryPnrResponse.class);
//
//		StringWriter writer = new StringWriter();
//		xstream.marshal(qpr, new PrettyPrintWriter(writer, new NoNameCoder()));
//
//		String xml = writer.toString();
//		
//		System.out.println(xml);
		
		
	}

}
