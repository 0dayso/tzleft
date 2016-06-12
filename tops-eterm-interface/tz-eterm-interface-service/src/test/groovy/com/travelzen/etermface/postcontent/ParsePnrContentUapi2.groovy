package com.travelzen.etermface.postcontent

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

import com.travelzen.etermface.service.PNRParser

/**
 * @author liang.wang
 *
 */
class ParsePnrContentUapi2 {

	private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

	static main(args) {

		def pnrContent =
 
'''1.彭显琴 JMC00N 
                                                                 2.  MU5137 Y   FR18APR  SHAPEK HK1   0700 0920          E T2T2 
                  3.SHA/T SHA/T021-62277798/BU YE CHENG BOOKING OFFICE/JINHUA ABCDEFG 
             4.TL/0000/18APR/SHA255 
                                                          5.SSR FOID MU HK1 NI737648922/P1 
                                                6.SSR ADTK 1E BY SHA25JAN14/1700 OR CXL MU5137 Y18APR 
                           7.OSI MU CTCT13827367283 
                                                        8.RMK CA/NJCM36 
                                                                 9.SHA255 
                                             PAT:A 
                                                             01 Y FARE:CNY1130.00 TAX:CNY50.00 YQ:CNY120.00  TOTAL:1300.00 '''
				

		HttpPost httpost = new HttpPost("http://localhost:8380/eterm-interface/PnrContentParser");
//		 httpost = new HttpPost("http://192.168.160.183:8080/etermface-interfaces/PnrContentParser");
		
//		http://localhost:8180/etermface-interfaces/PnrContentParser

		List<NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("pnrContent", pnrContent ));
		
		nvps.add(new BasicNameValuePair("source", "uapi" ));
		
		nvps.add(new BasicNameValuePair("isDomestic", "true" ));
//		nvps.add(new BasicNameValuePair("isDomestic", "false" ));
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
