package com.travelzen.etermface.web.controller;


import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.etermface.service.PatPriceByUfisParser;
import com.travelzen.etermface.service.PatPriceParser;
import com.travelzen.etermface.service.constant.UfisStatus;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * author:yangguo
 * <p/>
 * 通过航段或者pnr获取报价
 * <p/>
 * 该接口目前只为获取政府报价提供服务。
 * <p/>
 * pnr解析中的价格，现在只有国内在使用，后面会从pnr解析中剥离出来，pnr解析只做解析的事情。
 */
@Controller
public class PatPriceController {
    private Logger logger = LoggerFactory.getLogger(PatPriceController.class);

    /**
     * 该接口是国内通过航段获取报价
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/PatPrice", produces = "text/plain;charset=UTF-8", method = RequestMethod.POST)
    @ResponseBody
    public String patPrice(HttpServletRequest request) {
        String patParams = request.getParameter("PatParamsList");
        String passengerType = request.getParameter("PassengerType");
        logger.info("/PatPrice 请求： patParams = " + patParams + ", passengerType = " + passengerType);
        boolean isGovern = request.getParameter("isGovern") == null ? false : Boolean.parseBoolean(request.getParameter("isGovern"));
        String xml = null;
        if (UfisStatus.active && UfisStatus.patPrice) {
        	xml = new PatPriceByUfisParser().patPrice(patParams, passengerType, isGovern);
        } else {
        	xml = new PatPriceParser().patPrice(patParams, passengerType, isGovern);
        }
        logger.info("PatPriceServlet 返回： " + xml);

        return xml;
    }
}
