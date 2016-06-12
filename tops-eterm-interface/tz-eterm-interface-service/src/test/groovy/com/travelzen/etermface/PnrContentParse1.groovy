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
class PnrContentParse1 {

	private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

	static main(args) {

		def pnrContent =
 
'''rt JTMFZ5                                                                      
MARRIED SEGMENT EXIST IN THE PNR                                                
 1.LI/XIAOHONG JTMFZ5                                                           
 2.  LH729  W1  SA11JAN  PVGFRA HK1   1350 1905      SEAME                      
 3.  LH290  W1  SA11JAN  FRABLQ HK1   2105 2225      SEAME                      
 4.  LH1873 T2  FR24JAN  BLQMUC HK1   1310 1420      SEAME                      
 5.  LH726  T2  FR24JAN  MUCPVG HK1   2145 1555+1    SEAME                      
 6.SHA/T SHA/T-021-62839061 SHA DONG LI BUSINESS LTD.,CO /TANG/ZHEN FENG        
     ABCDEFG                                                                    
 7.13773251756                                                                  
 8.TL/1000/09JAN/SHA697                                                         
 9.RMK 1A/8RD3PN                                                                
10.SHA697                                                                       
                                                                                
                                                                                
                                                                                
 qte:/lh                                                                        
FSI/LH                                                                          
S LH   729W11JAN PVG1350 1905FRA0X    388                                       
S LH   290W11JAN FRA2105 2225BLQ0S    E90                                       
S LH  1873T24JAN BLQ1310 1420MUC0X    E95                                       
S LH   726T24JAN MUC2145>1555PVG0S    346                                       
01 WLNCBB+TL*           8770 CNY                    INCL TAX                    
*SYSTEM DEFAULT-CHECK OPERATING CARRIER                                         
*ATTN PRICED ON 08JAN14*0940                                                    
 SHA                                                                            
XFRA WLNCBB          NVB11JAN NVA11JAN 1PC                                      
 BLQ WLNCBB          NVB11JAN NVA11JAN 1PC                                      
XMUC TLNNBB          NVB24JAN NVA24JAN 1PC                                      
 SHA TLNNBB          NVB24JAN NVA24JAN 1PC                                      
FARE  CNY    5100                                                               
TAX   CNY     90CN CNY     55DE CNY   3525XT                                    
TOTAL CNY    8770                                                               
11JAN14SHA LH X/FRA LH BLQ M489.18LH X/MUC LH SHA M349.41NUC                    
838.59END ROE6.081590                                                           
XT CNY 311RA CNY 11EX CNY 54HB CNY 58IT CNY 7MJ                                 
XT CNY 22VT CNY 3062YQ                                                          
ENDOS *NONREF/FL/CHG RESTRICTED                                                 
ENDOS *CHECK FARE NOTE                                                          
*AUTO BAGGAGE INFORMATION AVAILABLE - SEE FSB '''

		HttpPost httpost = new HttpPost("http://localhost:8380/eterm-interface/PnrContentParser");
//		 httpost = new HttpPost("http://192.168.160.183:8080/etermface-interfaces/PnrContentParser");
		
//		http://localhost:8180/etermface-interfaces/PnrContentParser

		List<NameValuePair> nvps = new ArrayList <NameValuePair>();

		nvps.add(new BasicNameValuePair("pnrContent", pnrContent ));
		
//		nvps.add(new BasicNameValuePair("isDomestic", "true" ));
//		nvps.add(new BasicNameValuePair("isDomestic", "true" ));
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
