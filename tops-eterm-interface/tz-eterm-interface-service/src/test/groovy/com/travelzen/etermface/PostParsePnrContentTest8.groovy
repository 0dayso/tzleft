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
class PostParsePnrContentTest8 {

	private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

	static main(args) {

		def pnrContent =
 
'''RTJYKM4W
MARRIED SEGMENT EXIST IN THE PNR                                               
- 1.WANG/ZHEN MR JYKM4W  
2.  EY556  L1  SU05JAN  BGWAUH HK1   1245 1600      SEAME  
3.  EY862  L1  SU05JAN  AUHPVG HK1   2300 1110+1    SEAME  
4.SHA/T SHA/T-021-62180333/SHANGHAI NEW COMFORT INTERNATIONAL TRAV/XU FEIJUN   
    ABCDEFG 
5.62727233 DAI 
6.T
7.SSR OTHS 1E ADTK BY 09DEC13 1431 SHA LT OR EY SPACE WILL BE CXLD 
8.SSR DOCS EY HK1 P/CN/P01700291/CN/01JUN66/M/06MAY18/WANG/ZHEN/P1 
9.RMK  
10.RMK EY/BFLNDY                                                               +
 
>PN
11.RMK TJ AUTH SHA255                                                          
-12.FN/  
13.TN/607-4792108117/P1 
14.FP/  
15.SHA678'''
				

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
