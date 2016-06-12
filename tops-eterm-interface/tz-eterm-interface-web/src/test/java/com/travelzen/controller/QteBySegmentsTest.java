package com.travelzen.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14/12/3
 * Time:下午3:38
 * <p/>
 * Description:
 */
public class QteBySegmentsTest {
    static DefaultHttpClient httpClient = new DefaultHttpClient();

    @Test
    public void qteBySegmentsTest() {
        String QteBySegments = "<?xml version='1.0' encoding='UTF-8'?><QteBySegmentsParam><qteParams><FlightParam><open>false</open><airCompany>AC</airCompany><flightNum>782</flightNum><cabin>L</cabin><smallCabin>L</smallCabin><depatureDate>2015-02-24</depatureDate><arriveDate>2015-02-24</arriveDate><depatureTime>07:30</depatureTime><arriveTime>15:47</arriveTime><fromAirPort>LAX</fromAirPort><toAirPort>YUL</toAirPort></FlightParam><FlightParam><open>false</open><airCompany>AC</airCompany><flightNum>405</flightNum><cabin>V</cabin><smallCabin>V</smallCabin><depatureDate>2015-03-02</depatureDate><arriveDate>2015-03-02</arriveDate><depatureTime>09:00</depatureTime><arriveTime>10:26</arriveTime><fromAirPort>YUL</fromAirPort><toAirPort>YYZ</toAirPort></FlightParam><FlightParam><open>false</open><airCompany>AC</airCompany><flightNum>087</flightNum><cabin>S</cabin><smallCabin>S</smallCabin><depatureDate>2015-03-02</depatureDate><arriveDate>2015-03-03</arriveDate><depatureTime>12:30</depatureTime><arriveTime>16:00</arriveTime><fromAirPort>YYZ</fromAirPort><toAirPort>PVG</toAirPort></FlightParam></qteParams><ticketingAirlineCompany><string>QF</string><string>MU</string></ticketingAirlineCompany><passengers><string>CHD</string></passengers></QteBySegmentsParam>";
        //本地
        HttpPost httpost = new HttpPost("http://127.0.0.1:8080/tz-eterm-interface-web/QteBySegments");
        //op
//        HttpPost httpost = new HttpPost("http://192.168.160.183:8080/tz-eterm-interface-web/QteBySegments");


        List<BasicNameValuePair> list1 = new ArrayList<BasicNameValuePair>();
        list1.add(new BasicNameValuePair("QteBySegments", QteBySegments));

        try {
            httpost.setEntity(new UrlEncodedFormEntity(list1, HTTP.UTF_8));

            HttpResponse response = httpClient.execute(httpost);

            System.out.println(EntityUtils.toString(response.getEntity(), HTTP.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
