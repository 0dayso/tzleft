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
class ParsePnrContUapi5 {

	private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

	static main(args) {

		def pnrContent =
 
'''0.12QINGDAOLVYOU NM12 JG83XZ                                                   
 1.艾青 2.陈浩 3.李白 4.李梅 5.林亮 6.李想 7.李振    8.秦磊 9.田馥甄 10.俞灏明 1
1.语嫣 12.张琳                                                                  
13.  MU5137 Y   WE12NOV  SHAPEK HN12  0700 0920          E T2T2                 
14.SHA/T SHA/T021-31087705/BU YE CHENG BOOKING OFFICE/JINHUA ABCDEFG            
15.REM 0804 1605 CSL0557                                                        
16.TL/0500/12NOV/SHA255                                                         
17.SSR FOID MU HK1 PP010111/P3                                                  
18.SSR FOID MU HK1 PP010110/P7                                                  
19.SSR FOID MU HK1 PP010109/P8                                                  
20.SSR FOID MU HK1 PP010108/P5                                                  
21.SSR FOID MU HK1 PP010108/P6                                                  
22.SSR FOID MU HK1 PP010107/P9                                                  
23.SSR FOID MU HK1 PP010106/P10                                                 
24.SSR FOID MU HK1 PP010105/P2                                                  
25.SSR FOID MU HK1 PP010104/P11                                                 
26.SSR FOID MU HK1 PP010102/P4                                                  
27.SSR FOID MU HK1 PP010101/P12                                                 
28.SSR FOID MU HK1 PP010103/P1                                                 +
                                                                               
                                                                                
                                                                                
pn                                                                             
29.OSI MU CTCT13812960346                                                      -
30.RMK CA/NJ451L                                                                
31.SHA255                                                                       
                                                                               
                                                                                
                                                                                
pat:a                                                                          
>PAT:A                                                                          
01 Y FARE:CNY1130.00 TAX:CNY50.00 YQ:CNY120.00  TOTAL:1300.00                   
SFC:01       
'''
				

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
