package com.travelzen.controller.cpbs

import com.travelzen.framework.logger.core.ri.CallInfo
import com.travelzen.framework.logger.core.ri.RequestIdentityHolder
import org.junit.Test

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14/12/14
 * Time:上午11:07 
 *
 * Description:
 */
class CpbsTest {
    @Test
    public void test1(){
        RequestIdentityHolder.init();
//        RequestIdentityHolder.get().others.put("command","RT")
//        RequestIdentityHolder.get().product="B2G";
        try {

//            HttpRequest httpRequest = new HttpRequest("http://localhost:8080/sayHello/yangguo");
//            HttpRequest httpRequest = new HttpRequest("http://192.168.161.87:8880/tz-etermface-interfaces-web/sayHello/yangguo");
            //op
            HttpRequest httpRequest = new HttpRequest("http://192.168.160.183:8080/tz-eterm-interface-web/sayHello/yangguo");
            httpRequest.setHttpRequestType(HttpRequestType.GET);
            println httpRequest.call();
            Thread.sleep(1000 * 3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
