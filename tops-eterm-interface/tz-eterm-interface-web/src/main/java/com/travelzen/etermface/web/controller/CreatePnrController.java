package com.travelzen.etermface.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.travelzen.etermface.common.config.ConfigUtil;
import com.travelzen.etermface.service.CreateIntPNR;
import com.travelzen.etermface.service.handler.CreateDomesticPnrHandler;
import com.travelzen.framework.core.json.JsonUtil;
import com.travelzen.tops.flight.common.pojo.CommonCreatePnrResult;
import com.travelzen.tops.flight.common.pojo.CommonDomCreatePnrCriteria;

/**
 * 创建国内/国际PNR
 * <p>
 * @author yiming.yan
 * @Date Jan 27, 2016
 */
@Controller
public class CreatePnrController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PnrController.class);
    private static boolean useUfis = false;//ConfigUtil.getUfisConfig();
    
    @RequestMapping(value = "/createDomPnr", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String createDomPnr(HttpServletRequest httpRequest) {
    	LOGGER.info("/createDomPnr接口");
        String requestJson = httpRequest.getParameter("createDomPnrRequest");
        LOGGER.info("/createDomPnr接口请求：{}", requestJson);
        String resultJson = null;
		CommonDomCreatePnrCriteria request = null;
		CommonCreatePnrResult result = null;
		try {
			request = (CommonDomCreatePnrCriteria) JsonUtil.fromJson(requestJson, CommonDomCreatePnrCriteria.class);
		} catch (IOException e) {
			LOGGER.info("请求参数Json反序列化失败！");
			result = CreateDomesticPnrHandler.createErrorResult(null, "请求参数错误！");
		}
        if (null != request)
        	result = CreateDomesticPnrHandler.handle(request.getPnrEntityParams(), useUfis);
		try {
			resultJson = JsonUtil.toJson(result, false);
		} catch (IOException e) {
			LOGGER.info("返回结果Json序列化失败！");
			result = CreateDomesticPnrHandler.createErrorResult(null, "返回结果错误！");
		}
		LOGGER.info("/createDomPnr接口返回：{}", resultJson);
        return resultJson;
    }
    
    @RequestMapping(value = "/CreateIntPNR", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String getCreateIntPNR(HttpServletRequest request) {
    	LOGGER.info("/CreateIntPNR接口");
        String requestXml = request.getParameter("IntPnrCreateRequest");
        String xml = new CreateIntPNR().createIntPNR(requestXml);
        LOGGER.info("/CreateIntPNR接口返回：{}", xml);
        return xml;
    }

}
