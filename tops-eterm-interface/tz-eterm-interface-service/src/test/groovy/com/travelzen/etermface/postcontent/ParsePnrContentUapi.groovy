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
class ParsePnrContentUapi {

	private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

	static main(args) {

		def pnrContent =
 
'''rt HNCNPG                                                                      
 1.LEE/HEEWON HNCNPG                                                            
 2.  CA4591 V   TH23JAN  CTUPVG HK1   1855 2125          E T2T2                 
 3.SHA/T SHA/T 02151750527/CHANG SHOU TRAVEL/JINHUA ABCDEFG                     
 4.REM 0117 1704 SHA83601                                                       
 5.TL/1655/23JAN/SHA255                                                         
 6.FC/A/CTU A-17JAN15 CA PVG 720.00V CNY720.00END **9CNA001Z                    
 7.SSR FOID                                                                     
 8.SSR CKIN CA HK1 VICO9CNA001Z                                                 
 9.SSR ADTK 1E BY SHA17JAN14/1804 OR CXL CA ALL SEGS                            
10.OSI CA CTCT13636341467                                                       
11.RMK OT/A/                                                                    
12.RMK 制单交苏财务                                                             
                                                                
                                                                                
pat:a                                                                          
>PAT:A                                                                          
01 V FARE:CNY720.00 TAX:CNY50.00 YQ:CNY120.00  TOTAL:890.00                     
SFC:01                                                                         
                                                                               
                                                                                
                                                                                
pat:a*ch                                                                       
CAN NOT USE *CH FOR NON CHD PASSENGER                                           
                                                                               
                                                                                
                                                                                
pat:a*in                                                                       
>PAT:A*IN                                                                       
01 YIN90 FARE:CNY160.00 TAX:TEXEMPTCN YQ:TEXEMPTYQ  TOTAL:160.00                
SFC:01                                                                         
                                                                               
                                                                                
                                                                                
>PAT:A*IN                                                                       
01 YIN90 FARE:CNY160.00 TAX:TEXEMPTCN YQ:TEXEMPTYQ  TOTAL:160.00                
SFC:01                                                                         
                           '''
				

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
