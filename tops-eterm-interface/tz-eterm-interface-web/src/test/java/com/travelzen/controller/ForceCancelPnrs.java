package com.travelzen.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
 * @Date Mar 29, 2016
 */
public class ForceCancelPnrs {
	
	@Test
	public void t() {
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("src/test/resources/pnr/pnrs4cancel.txt")));
			while ((line = reader.readLine()) != null)
				sb.append(line.split(" +")[2]).append("\n");
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] pnrs = sb.toString().split("\n");
		Map<String, Future<String>> futureMap = new HashMap<String, Future<String>>();
		ExecutorService threadPool = Executors.newFixedThreadPool(1);
		for (String pnr:pnrs) {
			Future<String> future = threadPool.submit(new d(pnr));
			futureMap.put(pnr, future);
		}
		for (String pnr:pnrs) {
			String result = null;
			try {
				result = futureMap.get(pnr).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			System.out.println(pnr + " :: " + result);
		}
	}
	
	public static class d implements Callable<String> {
		
		String pnr = null;
		
		public d(String pnr) {
			this.pnr = pnr;
		}

		@Override
		public String call() throws Exception {
			String result = null;
			CloseableHttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost("http://192.168.161.87:8880/tz-eterm-interface-web/forceCancelPnr");
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("pnr", pnr));
			params.add(new BasicNameValuePair("office", "SHA125"));
			try {
				post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
				CloseableHttpResponse response = client.execute(post);
				result = EntityUtils.toString(response.getEntity());
//				System.out.println(result);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				client.close();
			}
			return result;
		}
		
	}

}
