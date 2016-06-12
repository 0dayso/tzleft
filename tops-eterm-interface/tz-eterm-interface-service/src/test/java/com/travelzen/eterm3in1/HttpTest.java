package com.travelzen.eterm3in1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author yiming.yan
 * @Date Dec 16, 2015
 */
public class HttpTest {
	
	@Ignore
	@Test
	public void pid() {
		Map<String, Object> params = new HashMap<>();
		params.put("ticketNo", "018-9755727634");
		params.put("iteneraryNo", "4719297538");
		String result = null;
		try {
			result = httpCall("http://127.0.0.1:8080/tz-eterm-interface-web/eterm3in1print/pid", params, "POST");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(result);
	}
	
	@Ignore
	@Test
	public void vi() {
		Map<String, Object> params = new HashMap<>();
		params.put("ticketNo", "7819753255645");
		params.put("iteneraryNo", "4719297516");
		String result = null;
		try {
			result = httpCall("http://127.0.0.1:8080/tz-eterm-interface-web/eterm3in1print/vi", params, "POST");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(result);
	}
	
	@Ignore
	@Test
	public void vbi() {
		Map<String, Object> params = new HashMap<>();
		params.put("iteneraryNo", "4719297514");
		String result = null;
		try {
			result = httpCall("http://127.0.0.1:8080/tz-eterm-interface-web/eterm3in1print/vbi", params, "POST");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(result);
	}
	
//	@Ignore
	@Test
	public void dt() {
		Map<String, Object> params = new HashMap<>();
		params.put("ticketNo", "999-2380924325/26");
		String result = null;
		try {
			result = httpCall("http://127.0.0.1:8080/tz-eterm-interface-web/eterm3in1print/dt", params, "POST");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(result);
	}
	
	/**
	 * @param uri 请求地址
	 * @param params 请求参数
	 * @param method GET/POST
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String httpCall(String uri, Map<String, Object> params, String method) throws ClientProtocolException, IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpResponse response = null;
		RequestConfig requestConfig = RequestConfig.custom()
				.build();
		if (method.equals("POST")) {
			HttpPost request = null;
			if (params == null) {
				request = new HttpPost(uri);
			} else {
				request = new HttpPost(uri);
				HttpEntity fromEntity = new UrlEncodedFormEntity(paramsToNameValuePairList(params));
				request.setEntity(fromEntity);
			}
			request.setConfig(requestConfig);
			response = client.execute(request);
		} else {
			HttpGet request = null;
			if (params == null) {
				request = new HttpGet(uri);
			} else {
				request = new HttpGet(uri + paramsToStr(params));
			}
			request.setConfig(requestConfig);
			response = client.execute(request);
		}
		if (response == null)
			return null;
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null)
			sb.append(line).append("\n");
		reader.close();
		client.close();
		if (sb.length() != 0)
			sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	private static String paramsToStr(Map<String, Object> params) {
		if (params == null || params.size() == 0)
			return "";
		StringBuffer sb = new StringBuffer();
		sb.append("?");
		for (Entry<String, Object> entry:params.entrySet()) {
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	private static List<NameValuePair> paramsToNameValuePairList(Map<String, Object> params) {
		if (params == null || params.size() == 0)
			return null;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		for(Entry<String, Object> entry:params.entrySet()) {
			nameValuePairs.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
		}
		return nameValuePairs;
	}

}
