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
class ParsePnrContentUapi4 {

	private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

	static main(args) {

		def pnrContent =
 
'''1.崔殿伟 2.丁鲁芹 3.王其宪 JN8GN9 
 4. 3U8963 F FR28MAR CTUPVG HK3 1245 1515 E T1-- 
 5.BJS/T BJS/T 010-82082660/BJS AI TE BO LU YUN AIR CO.,LTD/SHAO LIXIN ABCDEFG 
 6.END 
 7.TL/0000/25MAR/SHA255 
 8.SSR FOID 3U HK1 NI372524198111123792/P1 
 9.SSR FOID 3U HK1 NI110108198211072716/P3 
10.SSR FOID 3U HK1 NI370405198812210622/P2 
11.SSR ADTK 1E BY BJS21MAR14/1245 OR CXL 3U8963 F28MAR 
12.OSI 3U CTCT13699169896 
13.OSI 3U CTCM13811197805/P3 
14.OSI 3U CTCM15810685495/P1 
15.RMK AO/ABE/KH-212241106001/OP-NN LQDING/DT-20140123103847 
16.RMK CA/NX919N 
17.BJS407 
PAT:A
>PAT:A
>PAT:A 
01 F FARE:CNY2900.00 TAX:CNY50.00 YQ:CNY120.00 TOTAL:3070.00 
 SFC:01 '''
				

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
