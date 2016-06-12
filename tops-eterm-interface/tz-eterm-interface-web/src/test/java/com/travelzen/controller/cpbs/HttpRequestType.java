package com.travelzen.controller.cpbs;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14/11/6
 * Time:下午5:14
 * <p/>
 * Description:
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
}
