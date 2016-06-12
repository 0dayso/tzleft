package com.travelzen.controller;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14/11/21
 * Time:下午1:55 
 *
 * Description:
 *
 * etdz接口的单元测试代码
 */
public class EtdzControllerTest {
	@Ignore
    /**
     * 国内自动出票
     */
    @Test
    public void domesticEtdz() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
//        HttpGet httpGet = new HttpGet("http://127.0.0.1:8080/etdz?pnr=HG59F9&officeId=SHA255&priceBase=Y%2bYM&price=3400&printId=67");
        HttpGet httpGet = new HttpGet("http://127.0.0.1:8080/etdz?pnr=JNEWR4&officeId=SHA255&priceBase=R%2B&price=1850&printId=67");

        try {
            HttpResponse response1 = httpClient.execute(httpGet);
            String value1 = EntityUtils.toString(response1.getEntity(), HTTP.UTF_8);
            System.out.println(value1);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * 国际自动出票
     */
    @Test
    public void internationalEtdz() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
//        HttpGet httpGet = new HttpGet("http://127.0.0.1:8080/etdz?pnr=HG59F9&officeId=SHA255&priceBase=Y%2bYM&price=3400&printId=67");
        HttpGet httpGet = new HttpGet("http://127.0.0.1:8080/tz-eterm-interface-web/inetdz?pnr=HPY40Q&officeId=SHA255&airways=BR&printId=67&price=8888");

        try {
            HttpResponse response1 = httpClient.execute(httpGet);
            String value1 = EntityUtils.toString(response1.getEntity(), HTTP.UTF_8);
            System.out.println(value1);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
