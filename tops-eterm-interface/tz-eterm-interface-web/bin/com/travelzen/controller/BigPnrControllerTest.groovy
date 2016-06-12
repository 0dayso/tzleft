package com.travelzen.controller

import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.protocol.HTTP
import org.apache.http.util.EntityUtils
import org.junit.Test

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14/12/29
 * Time:下午1:37 
 *
 * Description:
 */
class BigPnrControllerTest {
    static DefaultHttpClient httpClient = new DefaultHttpClient();

    @Test
    public void getBigPnrTest() {
        //本地
//        HttpGet httpost = new HttpGet("http://127.0.0.1:8080/getBigPnr?pnr=JDS54M&officeId=BJS407");
        //op
        HttpGet httpost = new HttpGet("http://192.168.160.183:8080/tz-eterm-interface-web/getBigPnr?pnr=JDS54M&officeId=BJS407");

        HttpResponse response = httpClient.execute(httpost);
        println(EntityUtils.toString(response.getEntity(), HTTP.UTF_8));
        //op3
//        HttpPost httpost = new HttpPost("http://192.168.161.87:8880/tz-etermface-interfaces-web/rtpat?pnr=" + pnr + "&officeId=" + officeId);

    }
}
