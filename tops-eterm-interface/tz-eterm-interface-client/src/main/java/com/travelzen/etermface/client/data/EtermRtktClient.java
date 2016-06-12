package com.travelzen.etermface.client.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.etermface.common.utils.HttpClientUtil;
import com.travelzen.framework.config.tops.TopsConfEnum.ConfScope;
import com.travelzen.framework.config.tops.TopsConfReader;
import com.travelzen.framework.core.json.JsonUtil;
import com.travelzen.rosetta.eterm.common.pojo.EtermRtktResponse;

/**
 * Eterm RTKT 出票后预览票面
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
public class EtermRtktClient {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EtermRtktClient.class);
	
	private static final String ETERM_SERVER_ADDRESS = "tz-eterm-interface-client/eterm-server-address.properties";
	
	private static String prefixUrl = null;
	
	private static ExecutorService threadPool = null;
	
	static {
		prefixUrl = TopsConfReader.getConfContent(ETERM_SERVER_ADDRESS, "url", ConfScope.R);
		// 允许最大线程数根据配置数量调整
		threadPool = Executors.newFixedThreadPool(2);
	}
	
	/**
	 * Eterm RTKT 出票后预览票面
	 * <p>
	 * @param officeId
	 * @param isDomestic
	 * @param tktPacks
	 * @return
	 */
	public static List<EtermRtktResponse> rtkt(String office, boolean isDomestic, List<List<String>> tktPacks) {
		LOGGER.info("Eterm RTKT 打包任务请求 office:{}, isDomestic:{}, tktPacks:{}", office, isDomestic, tktPacks);
		if (null == prefixUrl) {
			LOGGER.error("获取 TZ-Eterm server address 失败！");
			return null;
		}
		
		Map<String, Future<EtermRtktResponse>> futureMap = new HashMap<String, Future<EtermRtktResponse>>();
		for (List<String> tktPack:tktPacks) {
			if (tktPack.size() == 0)
				continue;
			for (String tktNo:tktPack) {
				Future<EtermRtktResponse> future = threadPool.submit(new RtktTask(office, isDomestic, tktNo));
				futureMap.put(tktNo, future);
			}
		}
		
		List<EtermRtktResponse> responses = new ArrayList<EtermRtktResponse>();
		PACK_LOOP:
		for (List<String> tktPack:tktPacks) {
			if (tktPack.size() == 0) {
				LOGGER.error("Eterm RTKT 票号组为空！");
				responses.add(new EtermRtktResponse(false, "Eterm RTKT 票号组为空！"));
				continue PACK_LOOP;
			}
			List<EtermRtktResponse> tmpResponses = new ArrayList<EtermRtktResponse>();
			for (String tktNo:tktPack) {
				try {
					EtermRtktResponse response = futureMap.get(tktNo).get();
					tmpResponses.add(response);
				} catch (Exception e) {
					LOGGER.error("Eterm RTKT 任务执行异常：" + e.getMessage(), e);
					responses.add(new EtermRtktResponse(false, "Eterm RTKT 任务执行异常！"));
					continue PACK_LOOP;
				}
			}
			EtermRtktResponse packResponse = mergePackResponses(tmpResponses);
			if (packResponse.isSuccess()) {
				String mainTktNo = null;
				for (int i=0; i<tmpResponses.size(); i++) {
					if (0 != tmpResponses.get(i).getFare() && 0 != tmpResponses.get(i).getTax()) {
						mainTktNo = tktPack.get(i);
						break;
					}
				}
				if (null != mainTktNo)
					packResponse.setMainTktNo(mainTktNo);
			}
			if (packResponse.getFare() == 0 && packResponse.getTax() == 0) {
				packResponse.setSuccess(false);
				packResponse.setErrorMsg("Eterm RTKT 价格异常！");
			}
			if (tktPack.size() == 1)
				LOGGER.info("Eterm RTKT 票号：{} 结果：{}", tktPack, packResponse);
			else
				LOGGER.info("Eterm RTKT 连续票号：{} 结果：{}", tktPack, packResponse);
			responses.add(packResponse);
		}
		
		if (responses.size() != tktPacks.size()) {
			LOGGER.error("Eterm RTKT 打包任务结果异常！");
			return null;
		}
		LOGGER.info("Eterm RTKT 打包任务结果：{}", responses);
		return responses;
	}
	
	private static EtermRtktResponse mergePackResponses(List<EtermRtktResponse> tmpResponses) {
		if (tmpResponses.size() == 1)
			return tmpResponses.get(0);
		EtermRtktResponse response = tmpResponses.get(0);
		if (!response.isSuccess())
			return new EtermRtktResponse(false, "Eterm RTKT 连续票号的第1个票号解析异常：" + response.getErrorMsg());
		if (tmpResponses.size() == 1)
			return response;
		for (int i = 1; i < tmpResponses.size(); i++) {
			if (!tmpResponses.get(i).isSuccess())
				return new EtermRtktResponse(false, "Eterm RTKT 连续票号的第" + (i+1) + "个票号解析异常：" + response.getErrorMsg());
			response.getFlights().addAll(tmpResponses.get(i).getFlights());
		}
		return response;
	}

	public static class RtktTask implements Callable<EtermRtktResponse> {
		
		private String office;
		private boolean isDomestic;
		private String tktNo;
		
		public RtktTask(String office, boolean isDomestic, String tktNo) {
			this.office = office;
			this.isDomestic = isDomestic;
			this.tktNo = tktNo;
		}

		@Override
		public EtermRtktResponse call() throws Exception {
			LOGGER.info("Eterm RTKT 请求 office:{}, isDomestic:{}, tktNo:{}", office, isDomestic, tktNo);
			String url = prefixUrl + "/tz-eterm-interface-web/rtkt";
			Map<String, String> params = new HashMap<String, String>();
			params.put("office", office);
			params.put("isDomestic", String.valueOf(isDomestic));
			params.put("tktNo", tktNo);
			String responseJson = HttpClientUtil.post(url, params, "UTF-8", "UTF-8", 5 * 1000, 120 * 1000);
	        if (null == responseJson) {
	        	LOGGER.error("Eterm RTKT 返回结果为空！");
	        	return new EtermRtktResponse(false, "TZ-Eterm 返回结果为空！");
	        }
	        LOGGER.info("Eterm RTKT 返回Json {}", responseJson);
	        EtermRtktResponse response = null;
			try {
				response = (EtermRtktResponse) JsonUtil.fromJson(responseJson, EtermRtktResponse.class);
			} catch (IOException e) {
				LOGGER.error("Json反序列化异常：" + e.getMessage(), e);
	        	return new EtermRtktResponse(false, "TZ-Eterm 返回结果异常！");
			}
			LOGGER.info("Eterm RTKT 返回结果 {}", response);
			return response;
		}
	}

}
