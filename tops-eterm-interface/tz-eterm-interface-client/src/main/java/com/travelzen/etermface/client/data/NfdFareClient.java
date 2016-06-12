package com.travelzen.etermface.client.data;

import java.io.IOException;
import java.util.HashMap;

import com.google.gson.Gson;
import com.travelzen.etermface.common.pojo.fare.NfdFareRequest;
import com.travelzen.etermface.common.pojo.fare.NfdFareResponseNew;
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
 * 获取NFD价格的client
 */
public class NfdFareClient {
    private static Logger logger = LoggerFactory.getLogger(NfdFareClient.class);

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
     * @param nfdRequest 封装请求参数的对象
     */
    public static NfdFareResponseNew getDomesticNfd(String prefixUrl, NfdFareRequest nfdRequest) {
        String url = prefixUrl + "/tz-eterm-interface-web/fare/search/nfd";
        HashMap<String, String> params = new HashMap<String, String>();
        try {
            params.put("nfdRequest", JsonUtil.toJson(nfdRequest, false));
        } catch (IOException e) {
            logger.error("序列化出现异常", e);
        }
        logger.info("访问eterm-interface获取国内Nfd");
        String nfdResultStr = HttpClientUtil.get(url, params, "UTF-8", "UTF-8", 5 * 1000, 120 * 1000);
        Gson gson = new Gson();
        NfdFareResponseNew nfdFareResponseNew = gson.fromJson(nfdResultStr, NfdFareResponseNew.class);
        return nfdFareResponseNew;
    }
}
