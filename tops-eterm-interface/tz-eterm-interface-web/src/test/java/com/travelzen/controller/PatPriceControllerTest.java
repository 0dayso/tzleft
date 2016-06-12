package com.travelzen.controller;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.travelzen.etermface.service.entity.PatParams;
import com.travelzen.etermface.service.entity.SeatPrice;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14-10-10
 * Time:上午11:23
 * <p/>
 * Description:PAT报价测试
 */
public class PatPriceControllerTest {
    /**
     * 获取成人报价，成人报价中含有含有婴儿报价
     */
    @Test
    public void ADTTest() {
        List<SeatPrice> list = null;
        DefaultHttpClient httpClient = new DefaultHttpClient();
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        xstream.processAnnotations(PatParams.class);
        xstream.processAnnotations(SeatPrice.class);

        HttpPost httpost1 = new HttpPost("http://127.0.0.1:8080/PatPrice");

        PatParams patParams = new PatParams();
        patParams.setFromAirPort("URC");
        patParams.setToAirPort("AKU");
        patParams.setCabin("Y");
        patParams.setAirNo("HU7353");
        patParams.setSmallCabin("T");
        patParams.setSubCabinCode("T1");
        patParams.setDepatureDate("2015-01-29");


        List<BasicNameValuePair> list1 = new ArrayList<BasicNameValuePair>();
        List<PatParams> list2 = new ArrayList<>();
        list2.add(patParams);
        list1.add(new BasicNameValuePair("PatParamsList", xstream.toXML(list2)));
        list1.add(new BasicNameValuePair("PassengerType", "ADT"));
        try {
            httpost1.setEntity(new UrlEncodedFormEntity(list1, HTTP.UTF_8));
            HttpResponse response1 = httpClient.execute(httpost1);
            String value1 = EntityUtils.toString(response1.getEntity(), HTTP.UTF_8);
            System.out.println(value1);
            xstream.alias("PatResult", List.class);
            list = (List<SeatPrice>) xstream.fromXML(value1);
            System.out.println(list);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * 获取政府人员报价
     */
    @Test
    public void GoverTest() {
        List<SeatPrice> list = null;
        DefaultHttpClient httpClient = new DefaultHttpClient();
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        xstream.processAnnotations(PatParams.class);
        xstream.processAnnotations(SeatPrice.class);

        HttpPost httpost1 = new HttpPost("http://127.0.0.1:8080/PatPrice");

        PatParams patParams1 = new PatParams();
        patParams1.setFromAirPort("PEK");
        patParams1.setToAirPort("SHA");
        patParams1.setCabin("Y");
        patParams1.setAirNo("MU5138");
        patParams1.setSmallCabin("B");
        patParams1.setDepatureDate("2015-01-30");

        PatParams patParams2 = new PatParams();
        patParams2.setFromAirPort("SHA");
        patParams2.setToAirPort("PEK");
        patParams2.setCabin("Y");
        patParams2.setAirNo("MU5125");
        patParams2.setSmallCabin("K");
        patParams2.setDepatureDate("2015-03-01");


        List<BasicNameValuePair> list1 = new ArrayList<BasicNameValuePair>();
        List<PatParams> list2 = new ArrayList<>();
        list2.add(patParams1);
        list2.add(patParams2);
        list1.add(new BasicNameValuePair("PatParamsList", xstream.toXML(list2)));
        list1.add(new BasicNameValuePair("PassengerType", "ADT"));
        list1.add(new BasicNameValuePair("isGovern","true"));
        try {
            httpost1.setEntity(new UrlEncodedFormEntity(list1, HTTP.UTF_8));
            HttpResponse response1 = httpClient.execute(httpost1);
            String value1 = EntityUtils.toString(response1.getEntity(), HTTP.UTF_8);
            System.out.println(value1);
            xstream.alias("PatResult", List.class);
            list = (List<SeatPrice>) xstream.fromXML(value1);
            System.out.println(list);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void test1(){
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime dateTime = DateTime.parse("2015-01-28",format);
        dateTime=dateTime.plusDays(30);
        System.out.println(dateTime.toString("yyyy-MM-dd"));
    }
}
