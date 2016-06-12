package com.travelzen.controller;

import com.travelzen.etermface.service.entity.PatParams;
import com.travelzen.etermface.service.entity.SeatPrice;
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
 * Date:15/3/18
 * Time:下午6:08
 * <p/>
 * Description:
 */
public class PnrAuthTest {
    @Test
    public void test1(){
        DefaultHttpClient httpClient = new DefaultHttpClient();
        //本地环境
        HttpPost httpost1 = new HttpPost("http://127.0.0.1:8080/tz-eterm-interface-web/PnrAuth");
        //op，8080是成都新港的，8081是ufis的
//        HttpPost httpost1 = new HttpPost("http://192.168.160.183:8081/tz-eterm-interface-web/PnrAuth");
        //op3
//        HttpPost httpost1 = new HttpPost("http://192.168.161.87:8888/tz-eterm-interface-web/PnrAuth");


        List<BasicNameValuePair> list1 = new ArrayList<BasicNameValuePair>();
        list1.add(new BasicNameValuePair("pnr", "JRNL69"));
        list1.add(new BasicNameValuePair("ownerOffice", "BJS407"));
        list1.add(new BasicNameValuePair("grantorOffice", "SHA255"));
        try {
            httpost1.setEntity(new UrlEncodedFormEntity(list1, HTTP.UTF_8));
            HttpResponse response1 = httpClient.execute(httpost1);
            String value1 = EntityUtils.toString(response1.getEntity(), HTTP.UTF_8);
            System.out.println(value1);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
