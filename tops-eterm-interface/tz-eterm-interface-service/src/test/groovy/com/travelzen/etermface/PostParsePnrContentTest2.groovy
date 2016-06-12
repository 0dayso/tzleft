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
class PostParsePnrContentTest2 {

	private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

	static main(args) {
		
		StringBuffer lvBuffer = new StringBuffer();
		lvBuffer.append("RTHSC195\n");
		lvBuffer.append("1.商明 HSC195   \n");
		lvBuffer.append(" 2.  CA1560 Y   SA06JUL  TAOPEK HK1   1105 1225          E --T3 \n");
		lvBuffer.append(" 3.SHA/T SHA/T021-62277798/BU YE CHENG BOOKING OFFICE/JINHUA ABCDEFG  \n");
		lvBuffer.append("4.4007206666\n");
		lvBuffer.append(" 5.TL/0000/03JUL/SHA255 \n");
		lvBuffer.append("6.SSR FOID CA HK1 NI220182197808288215/P1 \n");
		lvBuffer.append(" 7.SSR FQTV CA HK1 TAOPEK 1560 Y06JUL CA217869890/P1 \n");
		lvBuffer.append(" 8.SSR ADTK 1E BY SHA28JUN13/1140 OR CXL CA ALL SEGS \n");
		lvBuffer.append(" 9.OSI YY CTCT4007206666   \n");
		lvBuffer.append("10.OSI CA CTCT13621624330         \n");
		lvBuffer.append("11.RMK 56067906            \n");
		lvBuffer.append("12.RMK AO/ABE/KH-0901040336090/OP-NN BYCCXKD01/DT-20130628104038\n");
		lvBuffer.append("13.RMK CA/MHEF37\n");
		lvBuffer.append("14.SHA255\n");


		def pnrContent =lvBuffer.toString() ;
				

		HttpPost httpost = new HttpPost("http://localhost:8180/eterm-interface/PnrContentParser");
		 httpost = new HttpPost("http://192.168.160.183:8080/eterm-interface/PnrContentParser");
		
//		http://localhost:8180/etermface-interfaces/PnrContentParser

		List<NameValuePair> nvps = new ArrayList <NameValuePair>();

		nvps.add(new BasicNameValuePair("pnrContent", pnrContent ));
		
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
