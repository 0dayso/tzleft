package com.travelzen.controller

import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.protocol.HTTP
import org.apache.http.util.EntityUtils
import org.junit.Test

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14/10/20
 * Time:下午5:00 
 *
 * Description:测试RTController的业务逻辑
 */
class RTControllerTest {
    static DefaultHttpClient httpClient = new DefaultHttpClient();

    @Test
    public void rtpatTest() {
        String pnr = "JG75TJ";
        String officeId = "sha255"
        //本地
        HttpPost httpost = new HttpPost("http://127.0.0.1:8080/tz-eterm-interface-web/rtpat?pnr=" + pnr + "&officeId=" + officeId);
        //op3
//        HttpPost httpost = new HttpPost("http://192.168.161.87:8880/tz-eterm-interface-web/rtpat?pnr=" + pnr + "&officeId=" + officeId);
        HttpResponse response = httpClient.execute(httpost);
        println(EntityUtils.toString(response.getEntity(), HTTP.UTF_8));
    }
}
