package com.travelzen.fare2go;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;



/**
 * @author hongqiang.mao
 * 
 * @date 2013-5-4 下午10:18:56
 * 
 * @description
 */
public class HttpRequest implements Callable<Object> {
	
	private String url;
	private String params;
	private HttpRequestType httpRequestType;
	public HttpRequest(String url){
		this(url,null,HttpRequestType.GET);
	}
	
	public HttpRequest(String url,String params){
		this(url,params,HttpRequestType.GET);
	}
	
	public HttpRequest(String url,String params,HttpRequestType httpRequestType){
		this.setUrl(url);
		this.setParams(params);
		this.setHttpRequestType(httpRequestType);
	}
	
	

	public String getUrl() {
		return url;
	}



	public void setUrl(String url) {
		this.url = url;
	}



	public String getParams() {
		return params;
	}



	public void setParams(String params) {
		this.params = params;
	}



	public HttpRequestType getHttpRequestType() {
		return httpRequestType;
	}



	public void setHttpRequestType(HttpRequestType httpRequestType) {
		this.httpRequestType = httpRequestType;
	}



	/**
	 * 获取指定链接的响应信息，GET方式
	 * @param pUrl
	 * @return
	 */
	public String sendGetRequest() {
		HttpClient lvClient = new DefaultHttpClient();
		HttpGet lvRequest = new HttpGet(this.url);
		String lvResponseString = null;
  		try {
  			HttpResponse response = lvClient.execute(lvRequest);
  			lvResponseString = EntityUtils.toString(response.getEntity());
  		} catch (Exception e) {
  			e.printStackTrace();
  		}
  		return lvResponseString;
	}
	
	/**
	 * 获取指定链接的响应信息，POST方式
	 * @param pUrl
	 * @param pParams 参数列表
	 * @return
	 */
	public String sendPostRequest() {
	    System.out.println("URL>>>>>"+url);
		HttpClient lvClient = new DefaultHttpClient();
		HttpPost lvHttpPost = new HttpPost(this.url);
  		String lvResponseString = null;
  		try {
  			lvHttpPost.setEntity(new StringEntity(this.params));
  			HttpResponse response = lvClient.execute(lvHttpPost);
  			lvResponseString = EntityUtils.toString(response.getEntity(),"GB2312");
  		} catch (Exception e) {
  			e.printStackTrace();
  		}
  		return lvResponseString;
	}
	/**
     * 统一提交请求接口
     * @param pUrl
     * @param pParams
     * @param pHttpRequestType
     * @return
     */
    public static String sendRequest(String pUrl,String pParams,HttpRequestType pHttpRequestType){
        Callable<Object> lvCallable= new HttpRequest(pUrl,pParams,pHttpRequestType);
        Future<Object> lvFuture = ThreadPoolService.getInstance().submit(lvCallable);
        String lvResponse = null;
        try {
            lvResponse = (String)lvFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return lvResponse;
    }
    
	@Override
	public String call() throws Exception {
		if(this.getHttpRequestType() == HttpRequestType.GET){
			return sendGetRequest();
		}
		return sendPostRequest();
	}
	

}
