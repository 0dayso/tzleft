package com.travelzen.fare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import com.thoughtworks.xstream.XStream;
import com.travelzen.etermface.service.abe_imitator.fare.FareSearch;
import com.travelzen.etermface.service.abe_imitator.fare.pojo.FareSearchRequest;
import com.travelzen.etermface.service.abe_imitator.fare.pojo.FareSearchResponse;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.framework.core.exception.BizException;

public class FareSearchTest {

    @Test
    public void test() {
        String rs = "192019";
        if (rs.startsWith("19")) {
            rs = rs.replaceFirst("19", "20");
        }
        System.out.println(rs);
    }

    @Test
    public void publicFareSearch() throws SessionExpireException {

        ServletContext servletContext = new MockServletContext();
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                "classpath*:spring/eterm-interface-appctx.xml", "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);


        // 22MAY(WED) WNZSHA
        FareSearchRequest req = new FareSearchRequest();
        req.setFrom("SHA");
        req.setArrive("PEK");
        req.setDate("2014-04-02");
        req.setCarrier("FM");
        // AV H /SHAPEK/23JUL/CA
        try {
            FareSearch fareSearch = new FareSearch();
            FareSearchResponse fareSearchResponse = fareSearch.publicFareSearch(req);
            XStream xstream = new XStream();
//            System.out.println(xstream.toXML(fareSearchResponse));
        } catch (Exception e) {
            throw BizException.instance("from or arrive are not IATA code!");
        }
    }

    @Test
    public void bargainFareSearch() throws SessionExpireException {

        ServletContext servletContext = new MockServletContext();
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                "classpath*:spring/eterm-interface-appctx.xml", "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);


        // 22MAY(WED) WNZSHA NFDDQATAO
        // FDDLCSHAD CKG XIY/01DEC13/HU
        FareSearchRequest req = new FareSearchRequest();
        req.setFrom("PVG");
        req.setArrive("PEK");
        req.setDate("2014-06-02");
        req.setCarrier("FM");
        // AV H /SHAPEK/23JUL/CA
        try {
            FareSearch fareSearch = new FareSearch();
            FareSearchResponse fareSearchResponse = fareSearch.bargainFareSearch(req);
            XStream xstream = new XStream();
            System.out.println(xstream.toXML(fareSearchResponse));
        } catch (Exception e) {
            e.printStackTrace();
            throw BizException.instance("from or arrive are not IATA code!");
        }
    }

    @Test
    public void allErrorBargainFareSearch() throws SessionExpireException {

        ServletContext servletContext = new MockServletContext();
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                "classpath*:spring/eterm-interface-appctx.xml", "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);


        List<String> lines = new ArrayList<>();
        try {
            BufferedReader brReader = new BufferedReader(
                    new FileReader(new File("/home/guohuaxue/Documents/error")));
            String line = null;
            while((line = brReader.readLine()) != null){
                lines.add(line.trim());
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        for(String string : lines){
            String[] strings = string.split(",");
            
            FareSearchRequest req = new FareSearchRequest();
            req.setFrom(strings[0]);
            req.setArrive(strings[1]);
            req.setDate(strings[2]);
            req.setCarrier(strings[3]);
            try {
                FareSearch fareSearch = new FareSearch();
                FareSearchResponse fareSearchResponse = fareSearch.bargainFareSearch(req);
                XStream xstream = new XStream();
                System.out.println(xstream.toXML(fareSearchResponse));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 22MAY(WED) WNZSHA
        // FDDLCSHA

        // AV H /SHAPEK/23JUL/CA
    }
}
