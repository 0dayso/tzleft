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
class PostParsePnrContentTest6 {

	private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

	static main(args) {

		def pnrContent =
 
'''0.4XKH/IN NM4 HE4VG4                                                           
 5.  OZ362  V   TU04FEB  PVGICN HK4   1205 1500      CLAME                      
 6.  OZ202  V   TU04FEB  ICNLAX HK4   1630 1010      CLAME                      
 7.    ARNK              LAXHNL                                                 
 8.  OZ231  V   SU16FEB  HNLICN HK4   1120 1740+1    CLAME                      
 9.  OZ367  V   MO17FEB  ICNPVG HK4   2000 2105      CLAME                      
10.SHA/T SHA/T021-62277798/BU YE CHENG BOOKING OFFICE/JINHUA ABCDEFG            
11.00862162703164                                                               
12.T                                                                            
13.SSR GRPF OZ GV                                                               
14.SSR GRPS OZ XKH/IN                                                           
15.SSR OTHS OZ 02FEB/2255 - TICKET TL                                           
16.SSR OTHS OZ 21JAN/0830 - NAME TL 30 PERCENT                                  
17.SSR OTHS OZ 28JAN/0830 - NAME TL 100 PERCENT                                 
18.SSR GRPS YY TCP10XKH/IN                                                      
19.SSR TKNE OZ HK1 PVGICN 362 V04FEB 9884792108197/1/P4                         
20.SSR TKNE OZ HK1 PVGICN 362 V04FEB 9884792108195/1/P3                                                                                                    
21.SSR TKNE OZ HK1 PVGICN 362 V04FEB 9884792108193/1/P2                        
22.SSR TKNE OZ HK1 PVGICN 362 V04FEB 9884792108191/1/P1                         
23.SSR TKNE OZ HK1 ICNLAX 202 V04FEB 9884792108191/2/P1                         
24.SSR TKNE OZ HK1 HNLICN 231 V16FEB 9884792108191/4/P1                         
25.SSR TKNE OZ HK1 ICNPVG 367 V17FEB 9884792108192/1/P1                         
26.SSR TKNE OZ HK1 ICNLAX 202 V04FEB 9884792108193/2/P2                         
27.SSR TKNE OZ HK1 HNLICN 231 V16FEB 9884792108193/4/P2                         
28.SSR TKNE OZ HK1 ICNPVG 367 V17FEB 9884792108194/1/P2                         
29.SSR TKNE OZ HK1 ICNLAX 202 V04FEB 9884792108195/2/P3                         
30.SSR TKNE OZ HK1 HNLICN 231 V16FEB 9884792108195/4/P3                         
31.SSR TKNE OZ HK1 ICNPVG 367 V17FEB 9884792108196/1/P3                        
32.SSR TKNE OZ HK1 ICNLAX 202 V04FEB 9884792108197/2/P4                                                                                                   
33.SSR TKNE OZ HK1 HNLICN 231 V16FEB 9884792108197/4/P4                        
34.SSR TKNE OZ HK1 ICNPVG 367 V17FEB 9884792108198/1/P4                         
35.SSR DOCS OZ HK1 P/CN/E25281017/CN/04APR79/F/28AUG23/SUN/MINHUI/P4            
36.SSR DOCS OZ HK1 P/CN/G21442695/CN/22SEP77/M/08MAR17/LU/HAIFENG/P1            
37.SSR DOCS OZ HK1 P/CN/G40419706/CN/15SEP83/F/07FEB20/LU/YI/P3                 
38.SSR DOCS OZ HK1 P/CN/G27731509/CN/14SEP49/M/24FEB18/LU/LIFU/P2               
39.RMK TJ SHA255                                                                
40.RMK SEG 2 OK QUAJBRC13NOV                                                    
41.RMK SEG4 X HNL V OK QEDBCRC14NOV                                             
42.RMK I-AUTH REQUESTED                                                         
43.RMK 1A/4G4YZR                                                                
44.FN/IT//SCNY5100.00/C0.00/XCNY2596.00/TCNY90.00CN/TCNY120.00BP/TCNY2386.00XT                                                                          
45.TN/988-4792108191-92/P1                                                     
46.TN/988-4792108193-94/P2                                                      
47.TN/988-4792108195-96/P3                                                      
48.TN/988-4792108197-98/P4                                                      
49.FP/CASH,CNY                                                                  
50.SHA255'''
				

		HttpPost httpost = new HttpPost("http://localhost:8380/eterm-interface/PnrContentParser");
//		 httpost = new HttpPost("http://192.168.160.183:8080/etermface-interfaces/PnrContentParser");
		
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
