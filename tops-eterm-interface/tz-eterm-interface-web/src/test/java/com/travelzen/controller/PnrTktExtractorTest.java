package com.travelzen.controller;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.travelzen.etermface.service.entity.PnrRet;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.util.*;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14-9-24
 * Time:下午4:57
 * <p/>
 * Description:通过指定的PNR号，然后获取票号，看是否跟提供的票号是否一致
 */
public class PnrTktExtractorTest {
    /**
     * 国内票号提取
     */
    @Test
    public void domTest() throws Exception {
        String pnr = "HZ6MTN";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        xstream.processAnnotations(PnrRet.class);

        List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
        list.add(new BasicNameValuePair("pnr", pnr));
        list.add(new BasicNameValuePair("needFare", "false"));
        list.add(new BasicNameValuePair("isDomestic", "true"));
        list.add(new BasicNameValuePair("officeId", "SHA255"));
        HttpPost httpPost = new HttpPost("http://127.0.0.1:8080/PnrTktExtractor");
        httpPost.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
        HttpResponse response = httpClient.execute(httpPost);
        String value = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
        PnrRet pnrRet = (PnrRet) xstream.fromXML(value);

        List<PnrRet.TicketInfo> ticketInfos = pnrRet.TicketInfos;
        System.out.println(ticketInfos);
    }

    /**
     * 国际票号提取
     */
    @Test
    public void intTest() throws Exception {
        String pnr = "KZ92C8";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        xstream.processAnnotations(PnrRet.class);
        List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
        list.add(new BasicNameValuePair("pnr", pnr));
        list.add(new BasicNameValuePair("needFare", "false"));
        list.add(new BasicNameValuePair("isDomestic", "false"));
        HttpPost httpPost = new HttpPost("http://127.0.0.1:8080/PnrTktExtractor");
        httpPost.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
        HttpResponse response = httpClient.execute(httpPost);
        String value = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
        PnrRet pnrRet = (PnrRet) xstream.fromXML(value);

        List<PnrRet.TicketInfo> ticketInfos = pnrRet.TicketInfos;
        System.out.println(ticketInfos);
    }
}
