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
class ParsePnrContUapi6 {

	private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

	static main(args) {

		def pnrContent =
 
'''1.CHAN/YECK LEE 2.CHUA/CHEE S
ENG 3.CHUA/HOW CHUAN 4.GOH/LEE HOON               
 5.LIM/SIAM KHENG 6.ONG/TEE HUA 7.SIAW/FUNG KEE 8.TIONG/HUA CHIONG              
 9.WONG/LEH CHUO JQPZKB                                                         
10.  MU2355 Z   WE10SEP  XIYHET HK9   0730 0900          E T3--                 
11.SHA/T SHA/T-021-34064880-454360/SHA HUA CHENG SOUTHWEST TRAVEL/SHAO/JI HONG  
    ABCDEFG                                                                     
12.021-51069999X454360/P1                                                       
13.021-51069999X454360/P2                                                       
14.021-51069999X454360/P9                                                       
15.021-51069999X454360/P8                                                       
16.021-51069999X454360/P7                                                       
17.021-51069999X454360/P6                                                       
18.021-51069999X454360/P5                                                      -
19.021-51069999X454360/P4                                                       
20.021-51069999X454360/P3                                                       
21.TL/1200/08SEP/SHA717                                                         
22.SSR FOID                                                                     
23.SSR FOID                                                                     
24.SSR FOID                                                                     
25.SSR FOID                                                                     
26.SSR FOID                                                                     
27.SSR FOID                                                                     
28.SSR FOID                                                                     
29.SSR FOID                                                                     
30.SSR FOID                                                                    -
31.SSR ADTK 1E BY SHA31JUL14/1654 OR CXL MU2355 Z10SEP                          
32.OSI MU CTCT18917919263                                                       
33.RMK CA/MH0GXX                                                                
34.RMK TJ AUTH SHA243                                                           
35.SHA717                                                                       

PAT:A
>PAT:A
PAT A                                                                          
>PAT:A                                                                          
01 YZ35WHDL FARE:CNY240.00 TAX:CNY50.00 YQ:CNY120.00  TOTAL:410.00'''
				

		HttpPost httpost = new HttpPost("http://localhost:8380/tz-eterm-interface-web/PnrContentParser");
//		 httpost = new HttpPost("http://192.168.160.183:8080/tz-etermface-interfaces-web/PnrContentParser");
		
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
