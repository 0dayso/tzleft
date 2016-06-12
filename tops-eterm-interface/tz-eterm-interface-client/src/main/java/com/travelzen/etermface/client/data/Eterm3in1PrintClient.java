package com.travelzen.etermface.client.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.etermface.common.pojo.eterm3in1.UfisDtResponse;
import com.travelzen.etermface.common.pojo.eterm3in1.UfisPidResponse;
import com.travelzen.etermface.common.pojo.eterm3in1.UfisVbiResponse;
import com.travelzen.etermface.common.pojo.eterm3in1.UfisViResponse;
import com.travelzen.etermface.common.utils.HttpClientUtil;
import com.travelzen.framework.config.tops.TopsConfEnum.ConfScope;
import com.travelzen.framework.config.tops.TopsConfReader;
import com.travelzen.framework.core.json.JsonUtil;

/**
 * Eterm 三合一打印插件
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
 * @Date Dec 18, 2015
 */
public class Eterm3in1PrintClient {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Eterm3in1PrintClient.class);
	
	private static final String ETERM_SERVER_ADDRESS = "tz-eterm-interface-client/eterm-server-address.properties";
	
	private static String prefixUrl = null;
	
	static {
		prefixUrl = TopsConfReader.getConfContent(ETERM_SERVER_ADDRESS, "url", ConfScope.R);
	}
	
	/**
	 * 打印国内报销凭证
	 * <p>
	 * @param ticketNo 票号
	 * @param iteneraryNo　行程单号
	 * @param officeId　Office号
	 * @return　UfisPidResponse
	 */
	public static UfisPidResponse pid(String ticketNo, String iteneraryNo, String officeId) {
		LOGGER.info("/tz-eterm-interface-web/eterm3in1print/pid 请求 "
				+ "ticketNo:{}, iteneraryNo:{}, officeId:{}", ticketNo, iteneraryNo, officeId);
		if (null == prefixUrl) {
			LOGGER.error("获取tz-eterm server address失败！");
			return new UfisPidResponse(false, "获取tz-eterm server address失败！");
		}
		String url = prefixUrl + "/tz-eterm-interface-web/eterm3in1print/pid";
		Map<String, String> params = new HashMap<String, String>();
		params.put("ticketNo", ticketNo);
		params.put("iteneraryNo", iteneraryNo);
		params.put("officeId", officeId);
		String responseXml = HttpClientUtil.post(url, params, "UTF-8", "UTF-8", 5 * 1000, 120 * 1000);
        if (null == responseXml) {
        	LOGGER.error("/tz-eterm-interface-web/eterm3in1print/pid 返回结果为空！");
        	return new UfisPidResponse(false, "tz-eterm返回结果为空！");
        }
        UfisPidResponse ufisResponse = null;
		try {
			ufisResponse = (UfisPidResponse) JsonUtil.fromJson(responseXml, UfisPidResponse.class);
		} catch (IOException e) {
			LOGGER.error("Json反序列化异常", e);
        	return new UfisPidResponse(false, "tz-eterm返回结果异常！");
		}
		LOGGER.info("/tz-eterm-interface-web/eterm3in1print/pid 返回 {}", ufisResponse);
		return ufisResponse;
	}
	
	/**
	 * 作废报销凭证
	 * <p>
	 * @param ticketNo 票号
	 * @param iteneraryNo　行程单号
	 * @param officeId　Office号
	 * @return　UfisViResponse
	 */
	public static UfisViResponse vi(String ticketNo, String iteneraryNo, String officeId) {
		LOGGER.info("/tz-eterm-interface-web/eterm3in1print/vi 请求 "
				+ "ticketNo:{}, iteneraryNo:{}, officeId:{}", ticketNo, iteneraryNo, officeId);
		if (null == prefixUrl) {
			LOGGER.error("获取tz-eterm server address失败！");
			return new UfisViResponse(false, "获取tz-eterm server address失败！");
		}
		String url = prefixUrl + "/tz-eterm-interface-web/eterm3in1print/vi";
		Map<String, String> params = new HashMap<String, String>();
		params.put("ticketNo", ticketNo);
		params.put("iteneraryNo", iteneraryNo);
		params.put("officeId", officeId);
		String responseXml = HttpClientUtil.post(url, params, "UTF-8", "UTF-8", 5 * 1000, 120 * 1000);
		if (null == responseXml) {
			LOGGER.error("/tz-eterm-interface-web/eterm3in1print/vi 返回结果为空！");
        	return new UfisViResponse(false, "tz-eterm返回结果为空！");
        }
        UfisViResponse ufisResponse = null;
		try {
			ufisResponse = (UfisViResponse) JsonUtil.fromJson(responseXml, UfisViResponse.class);
		} catch (IOException e) {
			LOGGER.error("Json反序列化异常", e);
        	return new UfisViResponse(false, "tz-eterm返回结果异常！");
		}
		LOGGER.info("/tz-eterm-interface-web/eterm3in1print/vi 返回 {}", ufisResponse);
		return ufisResponse;
	}
	
	/**
	 * 作废空白报销凭证
	 * <p>
	 * @param iteneraryNo　行程单号
	 * @param officeId　Office号
	 * @return　UfisVbiResponse
	 */
	public static UfisVbiResponse vbi(String iteneraryNo, String officeId) {
		LOGGER.info("/tz-eterm-interface-web/eterm3in1print/vbi 请求 "
				+ "iteneraryNo:{}, officeId:{}", iteneraryNo, officeId);
		if (null == prefixUrl) {
			LOGGER.error("获取tz-eterm server address失败！");
			return new UfisVbiResponse(false, "获取tz-eterm server address失败！");
		}
		String url = prefixUrl + "/tz-eterm-interface-web/eterm3in1print/vbi";
		Map<String, String> params = new HashMap<String, String>();
		params.put("iteneraryNo", iteneraryNo);
		params.put("officeId", officeId);
		String responseXml = HttpClientUtil.post(url, params, "UTF-8", "UTF-8", 5 * 1000, 120 * 1000);
		if (null == responseXml) {
			LOGGER.error("/tz-eterm-interface-web/eterm3in1print/vbi 返回结果为空！");
        	return new UfisVbiResponse(false, "tz-eterm返回结果为空！");
        }
        UfisVbiResponse ufisResponse = null;
		try {
			ufisResponse = (UfisVbiResponse) JsonUtil.fromJson(responseXml, UfisVbiResponse.class);
		} catch (IOException e) {
			LOGGER.error("Json反序列化异常", e);
        	return new UfisVbiResponse(false, "tz-eterm返回结果异常！");
		}
		LOGGER.info("/tz-eterm-interface-web/eterm3in1print/vbi 返回 {}", ufisResponse);
		return ufisResponse;
	}
	
	/**
	 * 展示行程单:电子客票
	 * <p>
	 * @param ticketNo 票号
	 * @param officeId　Office号
	 * @return　UfisDtResponse
	 */
	public static UfisDtResponse dt(String ticketNo, String officeId) {
		LOGGER.info("/tz-eterm-interface-web/eterm3in1print/dt 请求 "
				+ "ticketNo:{}, officeId:{}", ticketNo, officeId);
		if (null == prefixUrl) {
			LOGGER.error("获取tz-eterm server address失败！");
        	return new UfisDtResponse(false, "获取tz-eterm server address失败！");
		}
		String url = prefixUrl + "/tz-eterm-interface-web/eterm3in1print/dt";
		Map<String, String> params = new HashMap<String, String>();
		params.put("ticketNo", ticketNo);
		params.put("officeId", officeId);
		String responseXml = HttpClientUtil.post(url, params, "UTF-8", "UTF-8", 5 * 1000, 120 * 1000);
		if (null == responseXml) {
        	LOGGER.error("/tz-eterm-interface-web/eterm3in1print/dt 返回结果为空！");
        	return new UfisDtResponse(false, "tz-eterm返回结果为空！");
        }
        UfisDtResponse ufisResponse = null;
		try {
			ufisResponse = (UfisDtResponse) JsonUtil.fromJson(responseXml, UfisDtResponse.class);
		} catch (IOException e) {
			LOGGER.error("Json反序列化异常", e);
        	return new UfisDtResponse(false, "tz-eterm返回结果异常！");
		}
		LOGGER.info("/tz-eterm-interface-web/eterm3in1print/dt 返回 {}", ufisResponse);
		return ufisResponse;
	}

}
