package com.travelzen.etermface.client.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.etermface.common.utils.HttpClientUtil;
import com.travelzen.framework.config.tops.TopsConfReader;
import com.travelzen.framework.config.tops.TopsConfEnum.ConfScope;
import com.travelzen.framework.core.json.JsonUtil;
import com.travelzen.rosetta.eterm.common.pojo.EtermXsFscResponse;
import com.travelzen.rosetta.eterm.common.pojo.EtermXsFsmResponse;

/**
 * Eterm 指令接口
 * <p>
 * 远程服务提供方:
 *    <p>
 *    op:http://192.168.160.183:8080
 *    <p>
 *    op3:http://192.168.161.87:8880
 *    <p>
 *    beta:http://eterm.if.beta.tdxinfo.com
 *    <p>
 *    product:http://eterm.if.tdxinfo.com
 * <p>
 * @author yiming.yan
 * @Date Mar 2, 2016
 */
public class EtermCmdClient {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EtermCmdClient.class);
	
	private static final String ETERM_SERVER_ADDRESS = "tz-eterm-interface-client/eterm-server-address.properties";
	
	private static String prefixUrl = null;
	
	static {
		prefixUrl = TopsConfReader.getConfContent(ETERM_SERVER_ADDRESS, "url", ConfScope.R);
	}
	
	/**
	 * 获取城市间里程数
	 * <p>
	 * @param cities
	 * @return
	 */
	public static EtermXsFsmResponse xsfsm(List<String> cities) {
		LOGGER.info("Eterm XS FSM 请求 cities:{}", cities);
		if (null == prefixUrl) {
			LOGGER.error("获取 TZ-Eterm server address 失败！");
			return new EtermXsFsmResponse(false, "获取 TZ-Eterm server address 失败！");
		}
		if (null == cities || cities.size() == 0) {
			LOGGER.error("Eterm XS FSM 请求参数为空！");
			return new EtermXsFsmResponse(false, "Eterm XS FSM 请求参数为空！");
		}
		String url = prefixUrl + "/tz-eterm-interface-web/xsfsm";
		Map<String, String> params = new HashMap<String, String>();
		StringBuilder citiesBuilder = new StringBuilder();
		for (String city:cities) {
			citiesBuilder.append(city).append(" ");
		}
		params.put("cities", citiesBuilder.toString().trim());
		String responseJson = HttpClientUtil.post(url, params, "UTF-8", "UTF-8", 5 * 1000, 120 * 1000);
        if (null == responseJson) {
        	LOGGER.error("Eterm XS FSM 返回结果为空！");
        	return new EtermXsFsmResponse(false, "Eterm XS FSM 返回结果为空！");
        }
        EtermXsFsmResponse response = null;
        try {
			response = (EtermXsFsmResponse) JsonUtil.fromJson(responseJson, EtermXsFsmResponse.class);
		} catch (IOException e) {
			LOGGER.error("Json反序列化异常", e);
        	return new EtermXsFsmResponse(false, "Eterm XS FSM 返回结果转化异常！");
		}
		if (null ==  response || response.getCityDistances().size() != (cities.size() - 1)) {
			LOGGER.error("Eterm XS FSM 返回结果异常！");
			return new EtermXsFsmResponse(false, "Eterm XS FSM 返回结果异常！");
		}
		LOGGER.info("Eterm XS FSM 结果：{}", response);
		return response;
	}
	
	/**
	 * 获取实时汇率
	 * <p>
	 * @param currency
	 * @return
	 */
	public static EtermXsFscResponse xsfsc(String currency) {
		LOGGER.info("Eterm XS FSC 请求 currency:{}", currency);
		if (null == prefixUrl) {
			LOGGER.error("获取 TZ-Eterm server address 失败！");
			return new EtermXsFscResponse(false, "获取 TZ-Eterm server address 失败！");
		}
		if (null == currency) {
			LOGGER.error("Eterm XS FSC 请求参数为空！");
			return new EtermXsFscResponse(false, "Eterm XS FSC 请求参数为空！");
		}
		if (currency.length() != 3) {
			LOGGER.error("Eterm XS FSC 请求参数错误！");
			return new EtermXsFscResponse(false, "Eterm XS FSC 请求参数错误！");
		}
		String url = prefixUrl + "/tz-eterm-interface-web/xsfsc";
		Map<String, String> params = new HashMap<String, String>();
		params.put("currency", currency);
		String responseJson = HttpClientUtil.post(url, params, "UTF-8", "UTF-8", 5 * 1000, 120 * 1000);
        if (null == responseJson) {
        	LOGGER.error("Eterm XS FSC 返回结果为空！");
        	return new EtermXsFscResponse(false, "Eterm XS FSC 返回结果为空！");
        }
        EtermXsFscResponse response = null;
        try {
			response = (EtermXsFscResponse) JsonUtil.fromJson(responseJson, EtermXsFscResponse.class);
		} catch (IOException e) {
			LOGGER.error("Json反序列化异常", e);
        	return new EtermXsFscResponse(false, "Eterm XS FSC 返回结果转化异常！");
		}
		if (null ==  response || response.getRate2CNY() == 0) {
			LOGGER.error("Eterm XS FSC 返回结果异常！");
			return new EtermXsFscResponse(false, "Eterm XS FSC 返回结果异常！");
		}
		LOGGER.info("Eterm XS FSC 结果：{}", response);
		return response;
	}

}
