package com.travelzen.fare.center.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

/**
 * HTTP工具类
 * <p>
 * @author yiming.yan
 * @Date Oct 21, 2015
 */
public enum HttpUtil {
	
	;
	
	private static final int DEF_CONN_TIMEOUT = 30000;
	
	/**
	 * @param uri 请求地址
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String httpGet(String uri) throws ClientProtocolException, IOException {
		return httpCall(uri, null, "GET");
	}
	
	/**
	 * @param uri 请求地址
	 * @param params 请求参数
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String httpGet(String uri, Map<String, Object> params) throws ClientProtocolException, IOException {
		return httpCall(uri, params, "GET");
	}
	
	/**
	 * @param uri 请求地址
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String httpPost(String uri) throws ClientProtocolException, IOException {
		return httpCall(uri, null, "POST");
	}
	
	/**
	 * @param uri 请求地址
	 * @param params 请求参数
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String httpPost(String uri, Map<String, Object> params) throws ClientProtocolException, IOException {
		return httpCall(uri, params, "POST");
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
				.setConnectionRequestTimeout(DEF_CONN_TIMEOUT)
				.setConnectTimeout(DEF_CONN_TIMEOUT)
				.setSocketTimeout(DEF_CONN_TIMEOUT)
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
