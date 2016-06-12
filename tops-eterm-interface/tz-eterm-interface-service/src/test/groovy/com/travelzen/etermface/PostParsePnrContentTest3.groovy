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
class PostParsePnrContentTest3 {

	private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

	static main(args) {

		def pnrContent =
//				''' 
//1.李峰 JW2CY0                                                                  
// 2.  FM9437 Y   MO01JUL  SHABAV RR1   0810 1105          E T2--                 
// 3.SHA/T SHA/T 021-62491919/SHA LING XIANG BUINESS SERVICE CO.,LTD/YANG ER      
//     LING ABCDEFG                                                               
// 4.*                                                                            
// 5.*                                                                            
// 6.TL/0700/01JUL/SHA777                                                         
// 7.SSR FOID                                                                     
// 8.SSR OTHS 1E PNRETDZED                                                        
// 9.SSR OTHS 1E MU                                                               
//10.SSR CKIN FM                                                                  
//11.SSR FQTV FM HK1 SHABAV 9437 Y01JUL MU660280214127/P1
//12.SSR ADTK 1E BY SHA28JUN13/1850 OR CXL FM9437 Y01JUL                         -
//13.OSI CTC                                                                      
//14.OSI CTC                                                                      
//15.OSI 1E MUET TN/7812129970061                                                 
//16.RMK CA/MFCZB5                                                                
//17.RMK TJ AUTH SHA255                                                           
//18.RMK TJ AUTH SHA836                                                           
//19.SHA777 
//'''
				
'''  **ELECTRONIC TICKET PNR**                                                     
 1.SUN/YANYING MS JMPR76                                                        
 2.  MU281  G   TH25JUL  PVGSGN HK1   2205 0055+1        E T1--                 
 3.  MU282  G   TU30JUL  SGNPVG HK1   0155 0655          E --T1                 
 4.SHA/T SHA/T021-51555128/SHANGHAI SHANGYOU INTERNATIONAL TRAVEL SERVICE CO.,  
    LTD./CHEN ABCDEFG                                                           
 5.010-53571714                                                                 
 6.15800814895/745                                                              
 7.T                                                                            
 8.SSR ADTK 1E BY SHA03JUL13/2053 OR CXL MU 282 G30JUL                          
 9.SSR TKNE MU HK1 PVGSGN 281 G25JUL 7814481327459/1/P1                         
10.SSR TKNE MU HK1 SGNPVG 282 G30JUL 7814481327459/2/P1                        +
 
11.SSR DOCS                                                                    -
12.OSI MU CTCT15221848169                                                       
13.RMK TJ SHA831                                                                
14.RMK CA/NDPRET                                                                
15.RMK TJ AUTH SHA831                                                           
16.RMK TJ AUTH SHA255                                                           
17.FN/A/FCNY1260.00/SCNY1260.00/C5.00/XCNY1063.00/TCNY90.00CN/TCNY123.00JC/     
    TCNY850.00YQ/ACNY2323.00                                                    
18.TN/781-4481327459/P1                                                         
19.FP/CASH,CNY                                                                  
20.SHA274'''
				

		HttpPost httpost = new HttpPost("http://localhost:8180/eterm-interface/PnrContentParser");
//		 httpost = new HttpPost("http://192.168.160.183:8080/etermface-interfaces/PnrContentParser");
		
//		http://localhost:8180/etermface-interfaces/PnrContentParser

		List<NameValuePair> nvps = new ArrayList <NameValuePair>();

		nvps.add(new BasicNameValuePair("pnrContent", pnrContent ));
		
//		nvps.add(new BasicNameValuePair("isDomestic", "true" ));
		nvps.add(new BasicNameValuePair("isDomestic", "false" ));
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
