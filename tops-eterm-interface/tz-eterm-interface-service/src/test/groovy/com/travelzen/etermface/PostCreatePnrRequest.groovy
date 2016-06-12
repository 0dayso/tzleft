package com.travelzen.etermface

import com.travelzen.etermface.service.PNRParser
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author liang.wang
 *
 */
class PostCreatePnrRequest {

	private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

	static main(args) {

		def content =
				'''<?xml version="1.0" encoding="utf-8"?>
<request>
	<header>
		<accountID>bycsu01</accountID>
		<serviceName>orderFlightGj</serviceName>
		<digitalSign>F74F684FEC21E74C</digitalSign>
		<gzip>0</gzip>
	</header>
	<body>
		<OTA_PNRRequest>
			<FlightInfo>
				<FlightSegment>
					<FromCity>PEK</FromCity>
					<DestCity>SHA</DestCity>
					<AirCo>MU</AirCo>
					<FlightNumer>MU5138</FlightNumer>
					<Class>Y</Class>
					<QueryDate>2013-09-30</QueryDate>
					<FromDate>2013-09-30</FromDate>
					<FromTime>0700</FromTime>
					<DestDate>2013-09-30</DestDate>
					<DestTime>0910</DestTime>
				</FlightSegment>
				<FlightSegment>
					<FromCity>PVG</FromCity>
					<DestCity>FRA</DestCity>
					<AirCo>MU</AirCo>
					<FlightNumer>MU219</FlightNumer>
					<Class>H</Class>
					<QueryDate>2013-09-30</QueryDate>
					<FromDate>2013-09-30</FromDate>
					<FromTime>2355</FromTime>
					<DestDate>2013-10-01</DestDate>
					<DestTime>0605</DestTime>
				</FlightSegment>
			</FlightInfo>
			<Passenger>
				<PassengerDetail>
					<SurName>liu/lejiang</SurName>
					<CerType>P</CerType>
					<CerCountry>CN</CerCountry>
					<CerNo>232324231</CerNo>
					<Nationality>CN</Nationality>
					<BirthDay>1990-04-19</BirthDay>
					<Sex>M</Sex>
					<CerValidity>2018-04-19</CerValidity>
					<PassengerType>ADT</PassengerType>
				</PassengerDetail>
			</Passenger>
			<OP>
				<Operator></Operator>
				<AirCo>MU</AirCo>
				<PNRUser></PNRUser>
				<OfficeNo />
			</OP>
		</OTA_PNRRequest>
	</body>
</request>
'''
				

		HttpPost httpost = new HttpPost("http://localhost:8380/eterm-interface/flight/pnr/createIntPnr");
		
		
		String responseString = null;
		try {
			
			DefaultHttpClient httpclient = new DefaultHttpClient();
			
			httpost.setEntity( new StringEntity(content, "UTF-8"));
			HttpResponse response = httpclient.execute(httpost);
//			responseString = EntityUtils.toString(response.getEntity());
			
			println EntityUtils.toString(response.getEntity(), "utf-8");
			
		} catch (Exception e) {
				e.printStackTrace();
		}
//		return responseString;
		
		
// 
//		try {
//			logger.info("set utf-8 form entity to httppost");
//			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
//			
//			HttpResponse response = null;
//			  
//			try {
//				
//				response = httpclient.execute(httpost);
//				
//				System.out.println(response.getEntity().toString());
//				
//				println EntityUtils.toString(response.getEntity(), "utf-8");
//				
//						
//				
//			} catch (ClientProtocolException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
 
	}
}
