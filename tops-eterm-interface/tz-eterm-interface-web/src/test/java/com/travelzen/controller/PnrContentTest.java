package com.travelzen.controller;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.travelzen.etermface.service.entity.PnrContentResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14-9-25
 * Time:上午10:19
 * <p/>
 * Description:
 */
public class PnrContentTest {

    /**
     * 根据指定的pnr获取pnr内容
     */
    @Test
    public void getPnrContent() {
        String pnr = "JT44X2";
        String office="SHA255";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        xstream.processAnnotations(PnrContentResponse.class);
        //本地环境
//        HttpPost httpost1 = new HttpPost("http://127.0.0.1:8080/tz-eterm-interface-web/PnrContent");
        //测试环境的链接
        HttpPost httpost1 = new HttpPost("http://192.168.160.183:8080/tz-eterm-interface-web/PnrContent");
//        HttpPost httpost1 = new HttpPost("http://192.168.161.87:8880/tz-eterm-interface-web/PnrContent");
        List<BasicNameValuePair> list1 = new ArrayList<BasicNameValuePair>();
        list1.add(new BasicNameValuePair("pnr", pnr));
        list1.add(new BasicNameValuePair("office", office));
        try {
            httpost1.setEntity(new UrlEncodedFormEntity(list1, HTTP.UTF_8));
            HttpResponse response1 = httpClient.execute(httpost1);
            String value1 = EntityUtils.toString(response1.getEntity(), HTTP.UTF_8);
            PnrContentResponse pnrContentResponse = (PnrContentResponse) xstream.fromXML(value1);
            System.out.println(pnrContentResponse);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Ignore
    /**
     * 传pnr自动获取黑屏内容，黑屏文本解析
     */
    @Test
    public void pnrContentParser1() throws Exception {
        String pnr = "KF961C";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        xstream.processAnnotations(PnrContentResponse.class);
        //本地环境
        HttpPost httpost1 = new HttpPost("http://127.0.0.1:8080/PnrContent");
        //测试环境的链接
//        HttpPost httpost1 = new HttpPost("http://192.168.160.183:8080/tz-etermface-interfaces-web/PnrContent");
        List<BasicNameValuePair> list1 = new ArrayList<BasicNameValuePair>();
        list1.add(new BasicNameValuePair("pnr", pnr));
        httpost1.setEntity(new UrlEncodedFormEntity(list1, HTTP.UTF_8));
        HttpResponse response1 = httpClient.execute(httpost1);
        String value1 = EntityUtils.toString(response1.getEntity(), HTTP.UTF_8);
        PnrContentResponse pnrContentResponse = (PnrContentResponse) xstream.fromXML(value1);
        String content = pnrContentResponse.getContent();

        System.out.println(content);

        System.out.println("--------------------");

        HttpPost httpPost2 = new HttpPost("http://127.0.0.1:8080/PnrContentParser");
//        HttpPost httpPost2=new HttpPost("http://192.168.160.183:8080/tz-etermface-interfaces-web/PnrContentParser");
        List<BasicNameValuePair> list2 = new ArrayList<BasicNameValuePair>();
        list2.add(new BasicNameValuePair("passengerType", "ADT"));
        list2.add(new BasicNameValuePair("role", "distributor"));
        list2.add(new BasicNameValuePair("source", "distributor"));
        list2.add(new BasicNameValuePair("pnrContent", content));
        httpPost2.setEntity(new UrlEncodedFormEntity(list2, HTTP.UTF_8));
        HttpResponse response2 = httpClient.execute(httpPost2);
        String value2 = EntityUtils.toString(response2.getEntity(), HTTP.UTF_8);
        System.out.println(value2);


    }


    /**
     * 手动传黑屏内容
     */
    @Test
    public void pnrContentParser2() throws Exception {
//        String content = "rt  HF3792                                                                    \n" +
//                " 1.黄磊 HF3792                                                                 \n" +
//                " 2.  CA1389 V   FR27MAR  TSNHGH HK1   1010 1210          E      V1              \n" +
//                " 3.SHA/T SHA/T021-31087705/BU YE CHENG BOOKING OFFICE/JINHUA ABCDEFG           \n" +
//                " 4.REM 0311 1615 TSD                                                           \n" +
//                " 5.TL/0810/27MAR/SHA255                                                        \n" +
//                " 6.SSR FOID CA HK1 NI1453525352/P1                                             \n" +
//                " 7.SSR ADTK 1E BY SHA11MAR15/1915 OR CXL CA ALL SEGS                           \n" +
//                " 8.OSI CA CTCT13921480289                                                      \n" +
//                " 9.RMK CA/MHTGJK                                                                \n" +
//                "10.SHA255                                                                      \n" +
//                "                                                                               \n" +
//                "                                                                                                                                                            \n" +
//                " pat:a                                                                         \n" +
//                ">PAT:A                                                                          \n" +
//                "01 V1 FARE:CNY580.00 TAX:CNY50.00 YQ:TEXEMPTYQ  TOTAL:630.00                   \n" +
//                " SFC:01    SFN:01 ";


        String content = " 1.HSIEH/HSIANGYU 2.HSIEH/MINGTSANG 3.HSIEH/YUSHAN CHD 4.TSAI/YINGCHEN KNQD91   \n"
                + " 5.  B7501  V   SU24MAY  PVGTSA HX4   1120 1310      SEAME                      \n"
                + " 6.  BR722  V   WE27MAY  TPEPVG HX4   1630 1825          E                    \n"
                + " 7.SHA/T SHA/T51028189/SHANGHAI QUANSHUN BUSINESS INT?L TRAVEL AGENT CO.,LTD/   \n"
                + "    /REN JIE ABCDEFG                \n"
                + " 8.REM 0518 0943 209                \n"
                + " 9.TL/0920/25MAY/SHA355             \n"
                + "10.SSR OTHS 1E BR CANCELLATION DUE TO NO TICKET               \n"
                + "11.SSR OTHS 1E B7 CANCELLATION DUE TO NO TICKET               \n"
                + "12.SSR OTHS 1E B7 501 24MAY OPERATED BY BR OPERATED BY EVA AIR            \n"
                + "13.SSR ADTK 1E TO BR BY 21MAY 1500 HKG OTHERWISE WILL BE XLD              \n"
                + "14.SSR ADTK 1E TO B7 BY 21MAY 1500 OTHERWISE WILL BE XLD                  \n"
                + "15.SSR DOCS                                                        \n"
                + "23.RMK TJ AUTH SHA255                                              \n"
                + "24.SHA355";
        DefaultHttpClient httpClient = new DefaultHttpClient();


        HttpPost httpPost2 = new HttpPost("http://127.0.0.1:8080/tz-eterm-interface-web/PnrContentParser");
        //op
//        HttpPost httpPost2=new HttpPost("http://192.168.160.183:8080/tz-etermface-interfaces-web/PnrContentParser");
        //op3
//        HttpPost httpPost2 = new HttpPost("http://192.168.161.87:8880/tz-eterm-interface-web/PnrContentParser");
        List<BasicNameValuePair> list2 = new ArrayList<BasicNameValuePair>();
        list2.add(new BasicNameValuePair("passengerType", "ADT"));
        list2.add(new BasicNameValuePair("role", "distributor"));
        list2.add(new BasicNameValuePair("source", "distributor"));
        list2.add(new BasicNameValuePair("pnrContent", content));
        httpPost2.setEntity(new UrlEncodedFormEntity(list2, HTTP.UTF_8));
        HttpResponse response2 = httpClient.execute(httpPost2);
        String value2 = EntityUtils.toString(response2.getEntity(), HTTP.UTF_8);
        System.out.println(value2);


    }
}
