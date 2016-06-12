package com.travelzen.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import com.travelzen.cpbs.utils.JsonUtil;
import com.travelzen.etermface.common.pojo.fare.NfdFareRequest;
import com.travelzen.etermface.service.abe_imitator.fare.pojo.FareSearchRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14/12/4
 * Time:下午12:44
 * <p/>
 * Description:
 * <p/>
 * NFD价格接口查询
 */
public class FareSearchActionTest {
    @Test
    public void bargainsTest() {
        FareSearchRequest req = new FareSearchRequest();
        req.setFrom("WUH");
        req.setArrive("CKG");
        req.setCarrier("CZ");
        req.setDate("2015-05-15");

        DefaultHttpClient httpClient = new DefaultHttpClient();
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        xstream.processAnnotations(FareSearchRequest.class);
        //本地环境
        HttpPost httpost1 = new HttpPost("http://127.0.0.1:8080/fare/search/bargains");
        //op，8080是成都新港的，8081是ufis的
//        HttpPost httpost1 = new HttpPost("http://192.168.160.183:8081/tz-eterm-interface-web/fare/search/bargains");
        //op3
//        HttpPost httpost1 = new HttpPost("http://192.168.161.87:8880/tz-eterm-interface-web/fare/search/bargains");
        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity(xstream.toXML(req));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        try {
            httpost1.setEntity(stringEntity);
            HttpResponse response1 = httpClient.execute(httpost1);
            String value1 = EntityUtils.toString(response1.getEntity(), HTTP.UTF_8);
            System.out.println(value1);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    //    @Test
    public void publicsTest() {
        FareSearchRequest req = new FareSearchRequest();
        req.setFrom("FUO");
        req.setArrive("PVG");
        req.setCarrier("KN");
        req.setDate("2015-04-15");

        DefaultHttpClient httpClient = new DefaultHttpClient();
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        xstream.processAnnotations(FareSearchRequest.class);
        //本地环境
        HttpPost httpost1 = new HttpPost("http://127.0.0.1:8080/fare/search/publics");
        //op，8080是成都新港的，8081是ufis的
//        HttpPost httpost1 = new HttpPost("http://192.168.160.183:8080/tz-eterm-interface-web/fare/search/publics");
        //op3
//        HttpPost httpost1 = new HttpPost("http://192.168.161.87:8888/tz-eterm-interface-web/fare/search/bargains");
        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity(xstream.toXML(req));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        try {
            httpost1.setEntity(stringEntity);
            HttpResponse response1 = httpClient.execute(httpost1);
            String value1 = EntityUtils.toString(response1.getEntity(), HTTP.UTF_8);
            System.out.println(value1);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void nfdTest() {
        NfdFareRequest nfdRequest = new NfdFareRequest();
        nfdRequest.setDeptAirport("SHA");
        nfdRequest.setArrAirport("PEK");
        nfdRequest.setDeptDate("2015-09-25");
        nfdRequest.setCarrier("CA");

        DefaultHttpClient httpClient = new DefaultHttpClient();
        //本地环境
        HttpPost httpost1 = new HttpPost("http://127.0.0.1:8080/tz-eterm-interface-web/fare/search/nfd");
        //op，8080是成都新港的，8081是ufis的
//        HttpPost httpost1 = new HttpPost("http://192.168.160.183:8080/tz-eterm-interface-web/fare/search/nfd");
        //op3
//        HttpPost httpost1 = new HttpPost("http://192.168.161.87:8888/tz-eterm-interface-web/fare/search/nfd");
        try {
            List<BasicNameValuePair> list2 = new ArrayList<BasicNameValuePair>();
            list2.add(new BasicNameValuePair("nfdRequest", JsonUtil.toJson(nfdRequest, false)));
            httpost1.setEntity(new UrlEncodedFormEntity(list2, HTTP.UTF_8));
            HttpResponse response1 = httpClient.execute(httpost1);
            String value1 = EntityUtils.toString(response1.getEntity(), HTTP.UTF_8);
            System.out.println(value1);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

    }
}
