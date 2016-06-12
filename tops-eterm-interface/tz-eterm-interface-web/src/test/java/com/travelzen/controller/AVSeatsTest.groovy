package com.travelzen.controller

import com.travelzen.etermface.service.abe_imitator.av_seats.pojo.AvDatasetRequest
import com.travelzen.etermface.service.util.XmlUtils
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.protocol.HTTP
import org.apache.http.util.EntityUtils
import org.junit.Test

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14/11/19
 * Time:下午2:23 
 *
 * Description:
 */
class AVSeatsTest {
    static DefaultHttpClient httpClient = new DefaultHttpClient();
    /**
     * 验证接口 /flight/avSeats/avAllFlightSeats
     */
    @Test
    public void avAllFlightSeatsTest() {
        AvDatasetRequest request = new AvDatasetRequest();
        request.setArrive("SHA");
        request.setDate("20141129");
        request.setFrom("PEK");
        request.setPageNum(1);
        String xml = XmlUtils.toXML(request, AvDatasetRequest.class);
        //本地
        HttpPost httpost = new HttpPost("http://127.0.0.1:8080/flight/avSeats/avAllFlightSeats");
        httpost.setEntity(new ByteArrayEntity(xml.getBytes(HTTP.UTF_8)))
        HttpResponse response = httpClient.execute(httpost);
        println(EntityUtils.toString(response.getEntity(), HTTP.UTF_8));
        //op3
//        HttpPost httpost = new HttpPost("http://192.168.161.87:8880/tz-etermface-interfaces-web/rtpat?pnr=" + pnr + "&officeId=" + officeId);

    }
}