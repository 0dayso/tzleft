package com.travelzen.etermface

import com.travelzen.etermface.service.FlightSegmentPatParser
import org.junit.Before
import org.junit.Test
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TestFlightSegmentsPatParser2 {


	
	private static Logger logger = LoggerFactory.getLogger(TestFlightSegmentsPatParser2.class);
	
	FlightSegmentPatParser parser = new FlightSegmentPatParser();


	@Before void setUp() {
		//		rtParserHandlerList.add( new TicketExtractorHandler() )
	}



	@Test
	void process() {

		HttpPost  httpost = new HttpPost("http://localhost:8180/eterm-interface/price/query/getByFlightSegmentPAT");
		httpost = new HttpPost("http://192.168.160.183:8080/eterm-interface/price/query/getByFlightSegmentPAT");


		

		String reqInfo ='''<Flights><Flight ElementNo="1" ID="1" Type="0" Carrier="HO" Flight="1123" BoardPoint="SHA" OffPoint="CSX" DepartureDate="2013-08-28" Class="Q" ActionCode="LL"/>
												<Flight ElementNo="1" ID="1" Type="0" Carrier="HO" Flight="1556" BoardPoint="CSX" OffPoint="SHA" DepartureDate="2013-08-29" Class="Q" ActionCode="LL"/>
  </Flights>'''




		try {
			logger.info("set utf-8 form entity to httppost");
			httpost.setEntity( new StringEntity(reqInfo, HTTP.UTF_8));

			
			HttpResponse response = null;

			try {
				DefaultHttpClient httpclient = new DefaultHttpClient();
				response = httpclient.execute(httpost);

				System.out.println(response.getEntity().toString());

				println "--\n"+EntityUtils.toString(response.getEntity(), "utf-8") +"\n---";
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
 
}

