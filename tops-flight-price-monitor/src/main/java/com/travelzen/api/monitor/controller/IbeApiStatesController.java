package com.travelzen.api.monitor.controller;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.travelzen.framework.core.json.JsonUtil;
import com.travelzen.api.monitor.pojo.IbeApiStates;

@Controller
public class IbeApiStatesController {
	
	private static Logger logger= LoggerFactory.getLogger(IbeApiStatesController.class);
	
	@RequestMapping(value = "/test")
	@ResponseBody
	public String test() {
		logger.info("test：{}", "Good");
		return "Good";
	}
	
	@Deprecated
	@RequestMapping(value = "/checkIbeStates")
	@ResponseBody
	public String checkIbeApiStates() throws JsonMappingException, JsonGenerationException, IOException {
		IbeApiStates ibeApiStates = new IbeApiStates();
		logger.info("Ibe所有接口状态：{}", ibeApiStates);
		Gson gson = new Gson();
		return gson.toJson(ibeApiStates);
	}
	
	@RequestMapping(value = "/checkAirAvailAXmlState")
	@ResponseBody
	public String checkAirAvailAXmlState() throws JsonMappingException, JsonGenerationException, IOException {
		logger.info("AirAvailAXml接口状态：{}", IbeApiStates.airAvailAXmlState);
		return JsonUtil.toJson(IbeApiStates.airAvailAXmlState, false);
	}
	
	@RequestMapping(value = "/checkAirAvailByFltXmlState")
	@ResponseBody
	public String checkAirAvailByFltXmlState() throws JsonMappingException, JsonGenerationException, IOException {
		logger.info("AirAvailByFltXml接口状态：{}", IbeApiStates.airAvailByFltXmlState);
		return JsonUtil.toJson(IbeApiStates.airAvailByFltXmlState, false);
	}
	
	@RequestMapping(value = "/checkAirBookXmlState")
	@ResponseBody
	public String checkAirBookXmlState() throws JsonMappingException, JsonGenerationException, IOException {
		logger.info("AirBookXml接口状态：{}", IbeApiStates.airBookXmlState);
		return JsonUtil.toJson(IbeApiStates.airBookXmlState, false);
	}
	
	@RequestMapping(value = "/checkAirResRetCompleteXmlState")
	@ResponseBody
	public String checkAirResRetCompleteXmlState() throws JsonMappingException, JsonGenerationException, IOException {
		logger.info("AirResRetCompleteXml接口状态：{}", IbeApiStates.airResRetCompleteXmlState);
		return JsonUtil.toJson(IbeApiStates.airResRetCompleteXmlState, false);
	}
	
	@RequestMapping(value = "/checkAirFareFlightShopIXmlState")
	@ResponseBody
	public String checkAirFareFlightShopIXmlState() throws JsonMappingException, JsonGenerationException, IOException {
		logger.info("AirFareFlightShopIXml接口状态：{}", IbeApiStates.airFareFlightShopIXmlState);
		return JsonUtil.toJson(IbeApiStates.airFareFlightShopIXmlState, false);
	}

}
