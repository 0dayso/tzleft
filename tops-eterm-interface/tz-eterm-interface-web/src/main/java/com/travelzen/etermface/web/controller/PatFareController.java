package com.travelzen.etermface.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.etermface.common.pojo.fare.PatFareRequest;
import com.travelzen.etermface.common.pojo.fare.PatFareResponse;
import com.travelzen.etermface.service.fare.PatFareService;
import com.travelzen.framework.core.json.JsonUtil;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p/>
 * 获取PAT报价通用接口。
 * <p/>
 * @author yiming.yan
 */
@Controller
public class PatFareController {
	
	private Logger logger = LoggerFactory.getLogger(PatFareController.class);

	/**
     * 通过PNR获取PAT报价
     *
     * @param request
     * @return
     */
	@RequestMapping(value = "/patFare", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String patPrice(HttpServletRequest request) {
		String patParams = request.getParameter("patRequest");
		logger.info("开始通过PNR获取PAT价格..." + patParams);
        PatFareRequest patFareRequest = null;
		try {
			patFareRequest = (PatFareRequest) JsonUtil.fromJson(patParams, PatFareRequest.class);
		} catch (IOException e) {
			logger.error("PatFareRequest序列化出现异常", e);
		}
        PatFareService patFareService = new PatFareService();
        PatFareResponse patFareResponse = patFareService.getPat(patFareRequest);
        logger.info("PAT价格结果: " + patFareResponse);
        String responseXml = null;
		try {
			responseXml = JsonUtil.toJson(patFareResponse, false);
		} catch (IOException e) {
			logger.error("PatFareResponse反序列化出现异常", e);
		}
        logger.info("通过PNR获取PAT价格结束。");
        return responseXml;
	}
	
	/**
     * 通过航段获取PAT报价
     *
     * @param request
     * @return
     */
	@RequestMapping(value = "/patFareBySegment", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String patPriceBySegment(HttpServletRequest request) {
		String patParams = request.getParameter("patRequest");
		logger.info("开始通过航段获取PAT价格..." + patParams);
        PatFareRequest patFareRequest = null;
		try {
			patFareRequest = (PatFareRequest) JsonUtil.fromJson(patParams, PatFareRequest.class);
		} catch (IOException e) {
			logger.error("PatFareRequest序列化出现异常", e);
		}
        PatFareService patFareService = new PatFareService();
        PatFareResponse patFareResponse = patFareService.getPatBySegment(patFareRequest);
        logger.info("PAT价格结果: " + patFareResponse);
        String responseXml = null;
		try {
			responseXml = JsonUtil.toJson(patFareResponse, false);
		} catch (IOException e) {
			logger.error("PatFareResponse反序列化出现异常", e);
		}
        logger.info("通过航段PAT价格获取结束。");
        return responseXml;
	}

}
