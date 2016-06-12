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
import com.travelzen.etermface.service.SingleFlightInfoAVParser;
import com.travelzen.framework.core.json.JsonUtil;
import com.travelzen.rosetta.eterm.common.pojo.EtermAvResponse;
import com.travelzen.rosetta.eterm.common.pojo.enums.EtermCmdSource;
import com.travelzen.rosetta.eterm.parser.EtermAvParser;

/**
 * Eterm AV 指令控制类
 * <p>
 * @author yiming.yan
 * @Date Mar 3, 2016
 */
@Controller
public class EtermAvController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EtermAvController.class);
	
	/**
     * AV/AVH
     * <p>
     * @param request
     * @param office
     * @param tktNo
     * @param isDomestic
     * @return
     */
    @RequestMapping(value = "/av", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String av(HttpServletRequest request, @RequestParam boolean showMore, @RequestParam String deptCity, 
    		@RequestParam String arrCity, @RequestParam String date, @RequestParam String airCompany) {
    	LOGGER.info("/av 请求 H：{} 出发机场：{}　到达机场：{} 出发时间：{} 国内国际：{}", showMore, deptCity, arrCity, date, airCompany);
    	StringBuilder cmdAv = new StringBuilder();
		cmdAv.append(deptCity).append(arrCity).append("/").append(date);
		if (null != airCompany)
			cmdAv.append("/").append(airCompany);
		if (showMore)
			cmdAv.insert(0, "AV:H/");
		else
			cmdAv.insert(0, "AV:");
    	EtermUfisClient client = null;
		String text = null;
		try {
			client = new EtermUfisClient();
			text = client.execAv(cmdAv.toString(), 3);
		} catch (UfisException e) {
			LOGGER.error("UfisException: {}", e.getMessage());
			try {
				return JsonUtil.toJson(new EtermAvResponse(false, "Ufis 服务器异常！"), false);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} finally {
			client.close();
		}
		LOGGER.info("Eterm AV 指令返回：　{}", text);
		EtermAvResponse avResponse = null;
		try {
			avResponse = EtermAvParser.parse(text, showMore, EtermCmdSource.UFIS);
		} catch (Exception e) {
			LOGGER.info("EtermAvResponse 解析异常！");
			avResponse = new EtermAvResponse(false, "EtermAvResponse 解析异常！");
		}
		LOGGER.info("Eterm AV 解析结果：　{}", avResponse);
		String result = null;
		try {
			result = JsonUtil.toJson(avResponse, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
    }
    
    /**
     * av single flight
     * @param request
     * @return
     */
    @RequestMapping(value = "/SingleFlightInfo", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String getSingleFlightInfo(HttpServletRequest request) {
		String flightNo = request.getParameter("flightNo");
        String date = request.getParameter("date");
        LOGGER.info("/SingleFlightInfo接口请求　flightNo:{} date:{}", flightNo, date);
        String xml = "";
        try {
            xml = new SingleFlightInfoAVParser().getSingleFlightInfo(flightNo, date);
        } catch (SessionExpireException e) {
        	LOGGER.error("获取SingleFlightInfo时发生session超时");
        }
        LOGGER.info("/SingleFlightInfo接口返回:{}", xml);
        return xml;
	}

}
