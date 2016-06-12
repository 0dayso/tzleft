package com.travelzen.etermface.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.common.ufis.util.UfisException;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.EtermUfisClient;
import com.travelzen.etermface.service.EtermWebClient;
import com.travelzen.etermface.service.constant.UfisStatus;
import com.travelzen.framework.core.common.ReturnClass;
import com.travelzen.framework.core.json.JsonUtil;
import com.travelzen.rosetta.eterm.common.pojo.EtermRtktResponse;
import com.travelzen.rosetta.eterm.common.pojo.EtermXsFscResponse;
import com.travelzen.rosetta.eterm.common.pojo.EtermXsFsmResponse;
import com.travelzen.rosetta.eterm.parser.EtermRtktParser;
import com.travelzen.rosetta.eterm.parser.EtermXsFscParser;
import com.travelzen.rosetta.eterm.parser.EtermXsFsmParser;

/**
 * Eterm 指令控制类
 * <p>
 * @author yiming.yan
 * @Date Mar 3, 2016
 */
@Controller
public class EtermCmdController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EtermCmdController.class);
	
	/**
     * 出票后预览票面
     * <p>
     * @param request
     * @param office
     * @param tktNo
     * @param isDomestic
     * @return
     */
    @RequestMapping(value = "/rtkt", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String rtkt(HttpServletRequest request, @RequestParam String office, 
    		@RequestParam String tktNo, @RequestParam boolean isDomestic) {
    	LOGGER.info("/rtkt 请求 office：{}　票号：{} 国内国际：{}", office, tktNo, isDomestic);
    	EtermRtktResponse response = null;
    	String text = null;
    	if (null == office || office.equals(""))
    		office = "SHA255";
    	if (UfisStatus.active && UfisStatus.rtkt) {
    		EtermUfisClient client = null;
        	try {
        		client = new EtermUfisClient(office);
        		text = client.execCmd("RTKT " + tktNo, true);
        		if (null == text) {
        			LOGGER.error("Eterm RTKT 指令未获取到结果！");
        			response = new EtermRtktResponse(false, "Eterm RTKT 指令未获取到结果！");
        		}
        	} catch(UfisException e) {
        		LOGGER.error("Eterm RTKT 指令 Ufis异常：" + e.getMessage(), e);
        		response = new EtermRtktResponse(false, "Eterm RTKT 指令 Ufis异常！");
        	} finally {
        		if (null != client)
        			client.close();
        	}
    	} else {
    		EtermWebClient client = new EtermWebClient(office);
        	try {
        		client.connect();
        		ReturnClass<String> ret = client.executeCmdWithRetry("RTKT " + tktNo, false);
        		if (null == ret || !ret.isSuccess() || null == ret.getObject()) {
        			LOGGER.error("Eterm RTKT 指令未获取到结果！");
        			response = new EtermRtktResponse(false, "Eterm RTKT 指令未获取到结果！");
        		} else {
        			text = ret.getObject();
        		}
        	} catch (SessionExpireException e) {
        		LOGGER.error("Eterm RTKT 指令执行异常（超时）：" + e.getMessage(), e);
        		response = new EtermRtktResponse(false, "Eterm RTKT 指令执行异常！");
        	} finally {
        		client.close();
        	}
    	}
		
    	if (null != text) {
    		response = EtermRtktParser.parse(text, isDomestic);
    	}
    	String result = null;
    	try {
			result = JsonUtil.toJson(response, false);
		} catch (IOException e) {
			LOGGER.error("Json序列化　EtermRtktResponse　失败：" + e.getMessage(), e);
		}
    	LOGGER.info("/rtkt　结果：{}", result);
		return result;
    }
    
    @RequestMapping(value = "/xsfsm", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String xsfsm(HttpServletRequest request, @RequestParam String cities) {
    	LOGGER.info("/xsfsm 请求 cities：{}", cities);
    	EtermXsFsmResponse response = null;
    	String text = null;
    	if (UfisStatus.active && UfisStatus.xsfsm) {
    		EtermUfisClient client = null;
        	try {
        		client = new EtermUfisClient();
        		text = client.execCmd("XS FSM " + cities, false);
        		if (null == text) {
        			LOGGER.error("Eterm XS FSM 指令未获取到结果！");
        			response = new EtermXsFsmResponse(false, "Eterm XS FSM 指令未获取到结果！");
        		}
        	} catch(UfisException e) {
        		LOGGER.error("Eterm XS FSM 指令 Ufis异常：" + e.getMessage(), e);
        		response = new EtermXsFsmResponse(false, "Eterm XS FSM 指令 Ufis异常！");
        	} finally {
        		if (null != client)
        			client.close();
        	}
    	} else {
    		EtermWebClient client = new EtermWebClient();
        	try {
        		client.connect();
        		ReturnClass<String> ret = client.executeCmdWithRetry("XS FSM " + cities, false);
        		if (null == ret || !ret.isSuccess() || null == ret.getObject()) {
        			LOGGER.error("Eterm XS FSM 指令未获取到结果！");
        			response = new EtermXsFsmResponse(false, "Eterm XS FSM 指令未获取到结果！");
        		} else {
        			text = ret.getObject();
        		}
        	} catch (SessionExpireException e) {
        		LOGGER.error("Eterm XS FSM 指令执行异常（超时）：" + e.getMessage(), e);
        		response = new EtermXsFsmResponse(false, "Eterm XS FSM 指令执行异常！");
    		} finally {
        		client.close();
        	}
    	}
		
    	if (null != text) {
    		response = EtermXsFsmParser.parse(cities.split(" ").length, text);
    	}
    	String result = null;
    	try {
			result = JsonUtil.toJson(response, false);
		} catch (IOException e) {
			LOGGER.error("Json序列化　EtermXsFsmResponse　失败：" + e.getMessage(), e);
		}
    	LOGGER.info("/xsfsm　结果：{}", result);
		return result;
    }
    
    @RequestMapping(value = "/xsfsc", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String xsfsc(HttpServletRequest request, @RequestParam String currency) {
    	LOGGER.info("/xsfsc 请求 currency：{}", currency);
    	EtermXsFscResponse response = null;
    	String text = null;
    	if (UfisStatus.active && UfisStatus.xsfsc) {
    		EtermUfisClient client = null;
        	try {
        		client = new EtermUfisClient();
        		text = client.execCmd("XS FSC 1" + currency + "/CNY", false);
        		if (null == text) {
        			LOGGER.error("Eterm XS FSC 指令未获取到结果！");
        			response = new EtermXsFscResponse(false, "Eterm XS FSC 指令未获取到结果！");
        		}
        	} catch(UfisException e) {
        		LOGGER.error("Eterm XS FSC 指令 Ufis异常：" + e.getMessage(), e);
        		response = new EtermXsFscResponse(false, "Eterm XS FSC 指令 Ufis异常！");
        	} finally {
        		if (null != client)
        			client.close();
        	}
    	} else {
    		EtermWebClient client = new EtermWebClient();
    		client.connect();
    		try {
				ReturnClass<String> ret = client.executeCmd("XS FSC 1" + currency + "/CNY", true);
				if (ret.isSuccess())
					text = ret.getObject();
			} catch (SessionExpireException e) {
				LOGGER.error("Eterm XS FSM 指令执行异常（超时）：" + e.getMessage(), e);
        		response = new EtermXsFscResponse(false, "Eterm XS FSM 指令执行异常！");
			} finally {
				client.close();
			}
    	}
    	if (null != text) {
    		response = EtermXsFscParser.parse(currency, text);
    	}
    	String result = null;
    	try {
			result = JsonUtil.toJson(response, false);
		} catch (IOException e) {
			LOGGER.error("Json序列化　EtermXsFscResponse　失败：" + e.getMessage(), e);
		}
    	LOGGER.info("/xsfsc　结果：{}", result);
		return result;
    }

}
