package com.travelzen.etermface.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.common.ufis.util.UfisException;
import com.travelzen.etermface.common.config.ConfigUtil;
import com.travelzen.etermface.service.fare.NfdFareServiceByUfis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.travelzen.etermface.service.abe_imitator.fare.FareSearch;
import com.travelzen.etermface.service.abe_imitator.fare.FareSearchByUfis;
import com.travelzen.etermface.service.abe_imitator.fare.pojo.FareSearchRequest;
import com.travelzen.etermface.service.abe_imitator.fare.pojo.FareSearchResponse;
import com.travelzen.etermface.service.constant.UfisStatus;
import com.travelzen.etermface.service.fare.NfdFareService;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.common.pojo.fare.NfdFareRequest;
import com.travelzen.etermface.common.pojo.fare.NfdFareResponseNew;
import com.travelzen.framework.core.json.JsonUtil;

/**
 * 获取国内的折扣运价和公布运价
 */
@Controller
@RequestMapping("/fare/search")
public class FareSearchController {

    private static Logger logger = LoggerFactory.getLogger(FareSearchController.class);

    /**
     * 返回国内NFD价格数据,返回的是结构化的数据.
     *
     * @param request
     * @param reqInfo
     * @return
     */
    @RequestMapping(value = "/bargains", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String bargainFareSearch(HttpServletRequest request, @RequestBody String reqInfo) {
        FareSearchRequest req = FareSearchRequest.fromXML(reqInfo);
        logger.info("/fare/search/publics  请求：{}", req);
        FareSearchResponse resp = null;
        if (UfisStatus.active && UfisStatus.publics) {
        	resp = new FareSearchByUfis().bargainFareSearchWithNfn(req);
        } else {
        	resp = new FareSearch().bargainFareSearchWithNfn(req);
        }
        logger.info("/fare/search/publics  返回：{}", resp);
        String rs = "";
        if (resp != null) {
            rs = resp.toXML();
        }
        logger.info(reqInfo);
        logger.info(rs);
        return rs;
    }

    /**
     * 获取国内FD价格,返回的是结构化的数据
     *
     * @param request
     * @param reqInfo
     * @return
     */
    @RequestMapping(value = "/publics", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String publicFareSearch(HttpServletRequest request, @RequestBody String reqInfo) {
        FareSearchRequest req = FareSearchRequest.fromXML(reqInfo);
        logger.info("/fare/search/publics  请求：{}", req);
        FareSearchResponse resp = null;
        if (UfisStatus.active && UfisStatus.publics) {
        	try {
                resp = new FareSearchByUfis().publicFareSearch(req);
            } catch (UfisException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
        	try {
                resp = new FareSearch().publicFareSearch(req);
            } catch (SessionExpireException e) {
                logger.error(e.getMessage(), e);
            }
        }
        logger.info("/fare/search/publics  返回：{}", resp);
        String rs = resp.toXML();
        return rs;
    }

    /**
     * 获取NFD价格信息，返回的是半结构化数据，需要配合eterm-client使用
     *
     * @param request
     * @param reqInfo
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/nfd", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String nfdFareSearch(HttpServletRequest request, @RequestBody String reqInfo) throws IOException {
        logger.info("/fare/search/nfd 请求：{}", request.getParameter("nfdRequest"));
        if (null == request.getParameter("nfdRequest"))
            return null;
        NfdFareRequest nfdRequest = (NfdFareRequest) JsonUtil.fromJson(request.getParameter("nfdRequest"), NfdFareRequest.class);
        logger.info("NFD价格请求: " + nfdRequest);
        NfdFareResponseNew nfdResponse = null;
        if (UfisStatus.active && UfisStatus.nfd) {
            logger.info("使用eterm获取NFD");
            NfdFareService nfdFareService = new NfdFareService();
            try {
                nfdResponse = nfdFareService.getNfd(nfdRequest);
            } catch (SessionExpireException e) {
                logger.info(e.getMessage());
            }
        } else {
            logger.info("使用ufis获取NFD");
            NfdFareServiceByUfis nfdFareServiceByUfis = new NfdFareServiceByUfis();
            nfdResponse = nfdFareServiceByUfis.getNfd(nfdRequest);
        }
        logger.info("NFD价格结果: " + nfdResponse);
        logger.info(reqInfo);
        String nfdResponseRs = JsonUtil.toJson(nfdResponse, false);
        logger.info("NFD价格获取结束。");
        logger.info("/fare/search/nfd 返回：{}", nfdResponseRs);
        return nfdResponseRs;
    }
}
