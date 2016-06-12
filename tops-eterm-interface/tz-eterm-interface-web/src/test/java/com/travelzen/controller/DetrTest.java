package com.travelzen.controller;

import com.travelzen.controller.cpbs.HttpRequest;
import com.travelzen.controller.cpbs.HttpRequestType;
import com.travelzen.etermface.service.entity.PatParams;
import com.travelzen.etermface.service.entity.SeatPrice;
import com.travelzen.framework.logger.core.ri.RequestIdentityHolder;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14/11/7
 * Time:下午3:33
 * <p/>
 * Description:
 * <p/>
 * detr指令测试
 */
public class DetrTest {
    @Test
    public void test() {
        RequestIdentityHolder.init();
        List<SeatPrice> list = null;
        DefaultHttpClient httpClient = new DefaultHttpClient();
        //op环境
//        HttpRequest httpost1 = new HttpRequest("http://192.168.160.183:8080/tz-eterm-interface-web/flight/ticket/detrCombine?tktNumber=7246773509913");
        //op3环境
//        HttpRequest httpost1 = new HttpRequest("http://192.168.161.87:8880/tz-etermface-interfaces-web/flight/ticket/detrCombine?tktNumber=7816770488188");
        //开发环境
//        HttpRequest httpost1 = new HttpRequest("http://127.0.0.1:8080/flight/ticket/detrCombine?tktNumber=784-1954622455");
        //        HttpGet httpost1 = new HttpGet("http://127.0.0.1:8080/flight/ticket/detrCombine?tktNumber=7812478517651");
        //        HttpGet httpost1 = new HttpGet("http://127.0.0.1:8080/flight/ticket/detrCombine?tktNumber=7811948952089");
        //        HttpGet httpost1 = new HttpGet("http://127.0.0.1:8080/flight/ticket/detrf?tktNumber=7811948952089");
        HttpRequest httpost1 = new HttpRequest("http://127.0.0.1:8080/tz-eterm-interface-web/flight/ticket/detrs?tktNumber=9999756867057");
        //        HttpGet httpost1 = new HttpGet("http://127.0.0.1:8080/flight/ticket/detr?tktNumber=7312392568535");

        try {
            httpost1.setHttpRequestType(HttpRequestType.POST);
            System.out.println(httpost1.call());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
