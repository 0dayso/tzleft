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
class PostParsePnrContentTest9 {

	private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

	static main(args) {

		def pnrContent =
 
'''MARRIED SEGMENT EXIST IN THE PNR                                                
 1.CHEN/JIAKAN MR 2.CHEN/XINYUE MSTR 3.PAN/YI MS 4.SHAN/YING MS          
 5.SONG/YIRAN MS 6.ZHANG/WEIYI MSTR  7.ZHANG/YUQING MR 8.ZHENG/YIHAN MS   
   JMCV6Z                                                                       
 9.  LH727  S1  FR27JUN  PVGMUC HK8   2335 0530+1    SEAME                      
10.  LH2470 S1  SA28JUN  MUCLHR HK8   0715 0820      SEAME                      
11.  LH981  W2  SA12JUL  DUBFRA HK8   1800 2100      SEAME                      
12.  LH732  W2  SA12JUL  FRAPVG HK8   2205 1445+1    SEAME                      
13.SHA/T SHA/T021-62277798/BU YE CHENG BOOKING OFFICE/JINHUA ABCDEFG            
14.REM 0116 1342 YW2310                                                         
15.REM 0116 1343 YW2310                                                         
16.T62271290                                                                    
17.REM 0116 1344 YW2310                                                        
18.TL/2135/27JUN/SHA255                                                         
19.SSR OTHS 1E PLS ISS AUTOMATIC TKT BY 19JAN14/2359Z OR LH OPTG/MKTG SEGS      
     WILL BE XLD. APPLIC FARE RULE APPLIES IF IT DEMANDS EARLIER TKTG.XE/I      
20.SSR DOCS LH HK1 P/CHN/E03250785/CHN/10FEB99/F/20AUG19/ZHENG/YIHAN MS/P8      
21.SSR DOCS LH HK1 P/CHN/G28711538/CHN/12OCT60/M/20AUG19/ZHANG/YUQING MR/P7     
22.SSR DOCS LH HK1 P/CHN/E32650658/CHN/31DEC04/F/20AUG10/ZHANG/WEIYI MS/P6      
23.SSR DOCS LH HK1 P/CHN/G28711579/CHN/05JUL80/F/20AUG19/SONG/YIRAN MS/P5       
24.SSR DOCS LH HK1 P/CHN/G38861946/CHN/23JUL72/F/20AUG21/SHAN/YING MS/P4        
25.SSR DOCS LH HK1 P/CHN/G38177291/CHN/20NOV80/F/20AUG20/PAN/YI MS/P3           
26.SSR DOCS LH HK1 P/CHN/G38177290/CHN/28FEB06/F/20AUG20/CHEN/XINYUE MS/P2      
27.SSR DOCS LH HK1 P/CHN/G38178401/CHN/28SEP79/M/20AUG19/CHEN/JIAKAN MR/P1      
28.RMK 1A/447HOO                                                               
29.RMK TJ AUTH SHA205                                                           
30.SHA255  '''
				

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
