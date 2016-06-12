package com.travelzen.etermface.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.travelzen.etermface.service.QteBySegmentsParser;

@Controller
public class QteBySegmentsController {

	private static Logger logger = LoggerFactory.getLogger(QteBySegmentsController.class);
	
	@RequestMapping(value = "/QteBySegments", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String getQteBySegments(HttpServletRequest request) {
		logger.info("/QteBySegments接口");
		String PatParams = request.getParameter("QteBySegments");
        String xml = "";
        xml = new QteBySegmentsParser().getQteResultBySegementsParam(PatParams);
        return xml;
	}
	
}
