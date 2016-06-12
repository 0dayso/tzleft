package com.travelzen.controller;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14/10/22
 * Time:上午10:11
 * <p/>
 * Description:TopsPnrParser接口的单元测试类
 */
public class TopsPnrParserServletTest {
    @Test
    public void test1() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        //本地环境
        HttpPost httpost1 = new HttpPost("http://localhost:8080/tz-eterm-interface-web/TopsPnrParser");
        //op环境
//        HttpPost httpost1 = new HttpPost("http://192.168.160.183:8080/tz-eterm-interface-web/TopsPnrParser");
        //op3环境
//        HttpPost httpost1 = new HttpPost("http://192.168.161.87:8880/tz-eterm-interface-web/TopsPnrParser");

        List<BasicNameValuePair> list1 = new ArrayList<BasicNameValuePair>();
        list1.add(new BasicNameValuePair("officeId","SHA255"));
        list1.add(new BasicNameValuePair("source","eterm"));
        list1.add(new BasicNameValuePair("needAccurateCodeShare","false"));
        list1.add(new BasicNameValuePair("isDomestic","true"));
        list1.add(new BasicNameValuePair("role","operator"));
        list1.add(new BasicNameValuePair("pnr","KN76W2"));
        list1.add(new BasicNameValuePair("needFare","true"));
        list1.add(new BasicNameValuePair("needXsfsm","false"));


        try {
            httpost1.setEntity(new UrlEncodedFormEntity(list1, HTTP.UTF_8));
            HttpResponse response1 = httpClient.execute(httpost1);
//            String value1 = EntityUtils.toString(response1.getEntity(), HTTP.UTF_8);
            String value1 = EntityUtils.toString(response1.getEntity());
            System.out.println(value1);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
