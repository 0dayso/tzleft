package com.travelzen.etermface.service.entity;

import java.io.StringWriter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;




//<request>
//<header>
//	<accountID>BYC-INT</accountID>
//	<serviceName>getPnrByRt</serviceName>
//	<digitalSign>F74F684FEC21E74C</digitalSign>
//	<gzip>0</gzip>
//</header>
//<body>
//<reqcontent>
//<name>JI/wnag</name>
//<flightNumber>number12312</flightNumber>
//<fromDate>09sep</fromDate>
//<cerNo>G12535128</cerNo>
//</reqcontent>
//</body>
//</request>

@XStreamAlias("request")
public class QueryPnrRequest {

	@XStreamAlias("header")
	public static class Header {
		public String accountID;
		public String serviceName;
		public String digitalSign;
		public String gzip;
	}

	@XStreamAlias("body")
	public static class Body {

		@XStreamAlias("reqcontent")
		public static class ReqContent {

			public String name;
			public String flightNumber;
			public String fromDate;
			public String cerNo;

		}
		
		public ReqContent reqcontent;
		
	}
	
	public Header header;
	public Body body;
	
	public static void main(String argv []) {
		
		QueryPnrRequest qpr  = new QueryPnrRequest();
		
		
		qpr.header = new QueryPnrRequest.Header();
		qpr.body = new QueryPnrRequest.Body();
		
		
		qpr.header.accountID ="acc";
		qpr.body.reqcontent =  new QueryPnrRequest.Body.ReqContent();
		
		qpr.body.reqcontent.name ="myname";
		qpr.body.reqcontent.flightNumber="MU5701";
		qpr.body.reqcontent.fromDate="2013-08-31";
		qpr.body.reqcontent.cerNo ="310225198205260000";
		
		
		XStream xstream = new XStream();
		xstream.processAnnotations(QueryPnrRequest.class);

		StringWriter writer = new StringWriter();
		xstream.marshal(qpr, new PrettyPrintWriter(writer, new NoNameCoder()));

		String xml = writer.toString();
		
		System.out.println(xml);
		
		
	}

}
