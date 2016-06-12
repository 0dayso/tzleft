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
class ParsePnrCont1 {

	private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

	static main(args) {

		def pnrContent =
 
'''1.谭玉丽 JYHZSE
 
  2.  3U8968 H   SA26JUL  DLCCKG HK1   2000 2305 E
  3.BJS/T BJS/010-64229810/BEIJING KAIHUA AIR SERVER CO.,LTD/XU ZI ZHONG
      80176-95 85446-65 ABCDEFG
  4.LENG S2
  5.TL/1200/19JUL14/BJS349
  6.SSR FOID 3U HK1 NI500105198409201222/P1
  7.SSR OTHS 1E 1 PNR RR AND PRINTED
  8.SSR OTHS 1E 1 ET-3UAIRLINES ET PNR
  9.SSR ADTK 1E BY BJS15JUL14/1509 OR CXL 3U8968 H24JUL
10.SSR ADTK 1E BY BJS03JUL14/2319 OR CXL G52661 Y20JUL
11.OSI G5 CTCT13601230940
12.RMK TJ AUTH PEK263
13.RMK CA/MDZWXT
14.BJS349
 
PAT:A
>PAT:A
01 H FARE:CNY1260.00 TAX:CNY50.00 YQ:CNY120.00 TOTAL:1430.00
'''
				

		HttpPost httpost = new HttpPost("http://localhost:8380/eterm-interface/PnrContentParser");
		 httpost = new HttpPost("http://192.168.160.183:8080/tz-eterm-interface-web/PnrContentParser");
		
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
