package com.travelzen.etermface.client.data;

import java.io.IOException;
import java.util.HashMap;

import com.travelzen.etermface.common.pojo.fare.PatFareRequest;
import com.travelzen.etermface.common.pojo.fare.PatFareResponse;
import com.travelzen.etermface.common.utils.HttpClientUtil;
import com.travelzen.framework.core.json.JsonUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:15/4/13
 * Time:下午2:26
 * <p/>
 * Description:
 * <p/>
 * 获取Pat价格的client
 */
public class PatFareClient {
    private static Logger logger = LoggerFactory.getLogger(PatFareClient.class);

    /**
     * @param prefixUrl  远程服务提供方
     *                   </p>
     *                   op:http://192.168.160.183:8080
     *                   <p/>
     *                   op3:http://192.168.161.87:8880
     *                   <p/>
     *                   beta:http://eterm.if.beta.tdxinfo.com
     *                   <p/>
     *                   product:http://eterm.if.tdxinfo.com
     * @param patRequest 封装请求参数的对象
     */
    public static PatFareResponse getPat(String prefixUrl, PatFareRequest patRequest) {
        String url = prefixUrl + "/tz-eterm-interface-web/patFare";
        HashMap<String, String> params = new HashMap<String, String>();
        try {
            params.put("patRequest", JsonUtil.toJson(patRequest, false));
        } catch (IOException e) {
            logger.error("PatFareRequest序列化出现异常", e);
        }
        logger.info("访问eterm-interface获取国内Pat");
        String responseXml = HttpClientUtil.get(url, params, "UTF-8", "UTF-8", 5 * 1000, 120 * 1000);
        if (null == responseXml)
        	return null;
        PatFareResponse patFareResponse = null;
		try {
			patFareResponse = (PatFareResponse) JsonUtil.fromJson(responseXml, PatFareResponse.class);
		} catch (IOException e) {
			logger.error("PatFareResponse反序列化出现异常", e);
		}
        return patFareResponse;
    }
    
    /**
     * @param prefixUrl  远程服务提供方
     *                   </p>
     *                   op:http://192.168.160.183:8080
     *                   <p/>
     *                   op3:http://192.168.161.87:8880
     *                   <p/>
     *                   beta:http://eterm.if.beta.tdxinfo.com
     *                   <p/>
     *                   product:http://eterm.if.tdxinfo.com
     * @param patRequest 封装请求参数的对象
     */
    public static PatFareResponse getPatBySegment(String prefixUrl, PatFareRequest patRequest) {
        String url = prefixUrl + "/tz-eterm-interface-web/patFareBySegment";
        HashMap<String, String> params = new HashMap<String, String>();
        try {
            params.put("patRequest", JsonUtil.toJson(patRequest, false));
        } catch (IOException e) {
            logger.error("PatFareRequest序列化出现异常", e);
        }
        logger.info("访问eterm-interface获取国内Pat");
        String responseXml = HttpClientUtil.get(url, params, "UTF-8", "UTF-8", 5 * 1000, 120 * 1000);
        if (null == responseXml)
        	return null;
        PatFareResponse patFareResponse = null;
		try {
			patFareResponse = (PatFareResponse) JsonUtil.fromJson(responseXml, PatFareResponse.class);
		} catch (IOException e) {
			logger.error("PatFareResponse反序列化出现异常", e);
		}
        return patFareResponse;
    }

}
