package com.travelzen.controller;

import com.travelzen.controller.cpbs.HttpRequest;
import com.travelzen.controller.cpbs.HttpRequestType;
import com.travelzen.etermface.service.entity.SeatPrice;
import com.travelzen.framework.logger.core.ri.RequestIdentityHolder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:15/2/5
 * Time:上午11:08
 * <p/>
 * Description:
 */
public class PriceActionTest {
    @Test
    public void test() {
        RequestIdentityHolder.init();
        List<SeatPrice> list = null;
        DefaultHttpClient httpClient = new DefaultHttpClient();
        //op环境
//        HttpRequest httpost1 = new HttpRequest("http://192.168.160.183:8080/tz-etermface-interfaces-web/flight/ticket/detrCombine?tktNumber=7312392568535");
        //op3环境
        HttpRequest httpost1 = new HttpRequest("http://192.168.161.87:8880/tz-eterm-interface-web/flight/ticket/detrCombine?tktNumber=7816770488188");
        //开发环境
//        HttpRequest httpost1 = new HttpRequest("http://127.0.0.1:8080/flight/pnr/price/setPrice?officeId=SHA255&adtFare=980.0&pnr=KF4QF6&chdFare=0.0&adtFareBasis=U");
        //        HttpGet httpost1 = new HttpGet("http://127.0.0.1:8080/flight/ticket/detrCombine?tktNumber=7812478517651");
        //        HttpGet httpost1 = new HttpGet("http://127.0.0.1:8080/flight/ticket/detrCombine?tktNumber=7811948952089");
        //        HttpGet httpost1 = new HttpGet("http://127.0.0.1:8080/flight/ticket/detrf?tktNumber=7811948952089");
        //        HttpGet httpost1 = new HttpGet("http://127.0.0.1:8080/flight/ticket/detrs?tktNumber=7811948952089");
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
