package com.travelzen.fare2go;


/**
 * @author hongqiang.mao
 *
 * @date 2013-5-3 下午8:56:36
 */
public enum HttpRequestType{
	/**
	 * POST请求方式
	 */
	POST("post"),
	/**
	 * GET请求方式
	 */
	GET("get");
	private String httpRequestType;

	private HttpRequestType(String httpRequestType){
		this.httpRequestType =httpRequestType;
	}
	
	public String getPersistentValue() {
		return this.httpRequestType;
	}
}
