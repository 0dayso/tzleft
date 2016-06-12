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
class PostParsePnrContentTest {

	private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

	static main(args) {

		def pnrContent =
				'''**ELECTRONIC TICKET PNR** 
 1.ANSALDONAVASQUES/ALVARO HGVYHR   
 2. *FM8238 N   SA11MAY13HKGSHA HK1   1535 1800          E --T1 OP-HX238
 3.SHA/T SHA/T 021-62496201/SHA JINGYAN AGENCO/ZHANG RL ABCDEFG 
 4.REM 0507 1149 JINYI  
 5.13917521600  
 6.T  
 7.SSR ADTK 1E BY SHA07MAY13/1845 OR CXL FM 845 R08MAY  
 8.SSR TKNE FM HK1 SHAHKG 845 R08MAY 7743936980810/1/P1 
 9.SSR TKNE FM HK1 HKGSHA 8238 N11MAY 7743936980810/2/P1
10.SSR DOCS   
11.RMK TJ SHA255                                                               
12.RMK                                                                         
13.RMK    
14.RMK    
15.FN/A/FCNY1860.00/SCNY1860.00/C5.00/XCNY528.00/TCNY90.00CN/TCNY96.00HK/   
    TCNY342.00YQ/ACNY2388.00
16.TN/774-3936980810/P1 
17.FP/CASH,CNY  
18.SHA868
'''
				

		HttpPost httpost = new HttpPost("http://localhost:8180/eterm-interface/PnrContentParser");
		 httpost = new HttpPost("http://192.168.160.183:8080/eterm-interface/PnrContentParser");
		
//		http://localhost:8180/etermface-interfaces/PnrContentParser

		List<NameValuePair> nvps = new ArrayList <NameValuePair>();

		nvps.add(new BasicNameValuePair("pnrContent", pnrContent ));
		
		nvps.add(new BasicNameValuePair("isDomestic", "true" ));
		nvps.add(new BasicNameValuePair("needFare", "true" ));
		
		 

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
