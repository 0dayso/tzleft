package com.travelzen.etermface

import com.travelzen.etermface.service.PNRParser
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.protocol.HTTP
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author liang.wang
 *
 */
class PostParsePnrContentTest7MoreChild {

	private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

	static main(args) {

		def pnrContent =
 
'''1.张博 HE7RKF                                                                  
 2.  CA1516 Y   TU03DEC  SHAPEK HK1   1855 2120          E T2T3                 
 3.SHA/T SHA/T021-62277798/BU YE CHENG BOOKING OFFICE/JINHUA ABCDEFG            
 4.REM 1105 1652 160255                                                         
 5.CXLIUDANDAN/8324                                                             
 6.TL/2200/11NOV/SHA255                                                         
 7.SSR FOID CA HK1 NI371326198508265511/P1                                      
 8.SSR ADTK 1E BY SHA05NOV13/2251 OR CXL CA ALL SEGS                            
 9.OSI CA CTCT13636341467                                                       
10.OSI 1E CAET TN/9992120352963                                                 
11.RMK CA/MHDBFG                                                                
12.SHA255'''
				

		HttpPost httpost = new HttpPost("http://localhost:8380/eterm-interface/PnrContentParser");
		 httpost = new HttpPost("http://192.168.160.183:8080/eterm-interface/PnrContentParser");
		
//		http://localhost:8180/etermface-interfaces/PnrContentParser

		List<NameValuePair> nvps = new ArrayList <NameValuePair>();

		nvps.add(new BasicNameValuePair("pnrContent", pnrContent ));
		
//		nvps.add(new BasicNameValuePair("isDomestic", "true" ));
		nvps.add(new BasicNameValuePair("isDomestic", "true" ));
//		nvps.add(new BasicNameValuePair("needFare", "true" ));
		
		 

		try {
			logger.info("set utf-8 form entity to httppost");
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			
			HttpResponse response = null;
			  
			try {
				DefaultHttpClient httpclient = new DefaultHttpClient();
				response = httpclient.execute(httpost);
				
				System.out.println(response.getEntity().toString());
				
				println EntityUtils.toString(response.getEntity(), "utf-8");
						
				
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
