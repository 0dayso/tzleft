package com.travelzen.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

/**
 * Description
 * <p>
 * @author yiming.yan
 * @Date Mar 3, 2016
 */
public class RtktTest {
	
	@Test
	public void t() {
		HttpClientBuilder builder = HttpClientBuilder.create();
		CloseableHttpClient client = builder.build();
//		HttpPost post = new HttpPost("http://127.0.0.1:8080/tz-eterm-interface-web/rtkt");
		HttpPost post = new HttpPost("http://192.168.160.183:8080/tz-eterm-interface-web/rtkt");
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("office", "SHA255"));
		params.add(new BasicNameValuePair("tktNo", "8511789464614"));
		params.add(new BasicNameValuePair("isDomestic", "false"));
		try {
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			CloseableHttpResponse response = client.execute(post);
			String result = EntityUtils.toString(response.getEntity());
			System.out.println(result);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
