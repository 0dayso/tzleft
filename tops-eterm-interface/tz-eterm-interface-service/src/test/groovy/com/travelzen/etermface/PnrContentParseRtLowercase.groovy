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
class PnrContentParseRtLowercase {

	private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

	static main(args) {

		def pnrContent =
 
'''pnrContent= RT HYKDFe                                                                      ^M
 1.李炯 HYKDFE                                                                  ^M
 2.  CA1838 Q   SA04JAN  CANSHA HK1   0920 1135          E --T2                 ^M
 3.SHA/T SHA/T021-62277798/BU YE CHENG BOOKING OFFICE/JINHUA ABCDEFG            ^M
 4.REM 0103 2056 8807A1                                                         ^M
 5.TL/0600/04JAN/SHA255                                                         ^M
 6.SSR FOID CA HK1 NI320222197504130518/P1                                      ^M
 7.SSR ADTK 1E BY SHA03JAN14/2157 OR CXL CA ALL SEGS                            ^M
 8.OSI CA CTCT18918901625                                                       ^M
 9.RMK CA/MEXC2Q                                                                ^M
10.SHA255                                     '''
				

pnrContent = ''' 1.代淑丽 2.刘丹阳 3.刘丰晓 JMPW38                                              
 4.  HU7005AT   SA01FEB  HAKCAN HK3   0005 0105          E                      
 5.SHA/T SHA/T021-62277798/BU YE CHENG BOOKING OFFICE/JINHUA ABCDEFG            
 6.REM 0107 1558 FBXINZ                                                         
 7.61020528/54435694                                                            
 8.TL/2205/31JAN/SHA255                                                         
 9.SSR FOID HU HK1 NI370226197104260430/P3                                      
10.SSR FOID HU HK1 NI370226197009052641/P1                                      
11.SSR FOID HU HK1 NI370283199506050619/P2                                      
12.SSR FQTV HU HK1 HAKCAN 7005AT01FEB HU9186811552/P3                           
13.OSI HU CTCT13795266187                                                       
14.RMK CA/MBYK8K                                      '''

		HttpPost httpost = new HttpPost("http://localhost:8380/eterm-interface/PnrContentParser");
//		 httpost = new HttpPost("http://192.168.160.183:8080/etermface-interfaces/PnrContentParser");
		
//		http://localhost:8180/etermface-interfaces/PnrContentParser

		List<NameValuePair> nvps = new ArrayList <NameValuePair>();

		nvps.add(new BasicNameValuePair("pnrContent", pnrContent ));
		
//		nvps.add(new BasicNameValuePair("isDomestic", "true" ));
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
