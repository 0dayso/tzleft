package com.travelzen.etermface.web.controller;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.codahale.metrics.Timer;
import com.common.ufis.util.UfisException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.travelzen.etermface.common.config.ConfigUtil;
import com.travelzen.etermface.service.*;
import com.travelzen.etermface.service.constant.UfisStatus;
import com.travelzen.etermface.service.entity.ParseConfBean;
import com.travelzen.etermface.service.entity.PnrRet;
import com.travelzen.etermface.service.entity.RtInterceptorBean;
import com.travelzen.etermface.service.handler.EtermRtHandler;
import com.travelzen.etermface.service.handler.TicketNoExtractHandler;
import com.travelzen.etermface.service.ticket.DomTktExtractorLogic;
import com.travelzen.etermface.service.ticket.GeneralTktExtractorHandler;
import com.travelzen.etermface.service.ticket.ITktExtractorLogic;
import com.travelzen.etermface.service.ticket.IntTktExtractorLogic;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.framework.core.common.ReturnClass;
import com.travelzen.framework.core.common.ReturnCode;
import com.travelzen.framework.core.exception.BizException;
import com.travelzen.framework.core.json.JsonUtil;
import com.travelzen.framework.core.util.TZUtil;
import com.travelzen.rosetta.eterm.common.pojo.EtermRtResponse;
import com.travelzen.tops.pricing.monitor.metric.PricingMetric;

@Controller
public class PnrController {

    private static Logger logger = LoggerFactory.getLogger(PnrController.class);
    
    @RequestMapping(value = "/switchUfis", produces = "text/plain;charset=UTF-8")
    public ModelAndView switchUfis(HttpServletRequest request) {
    	logger.info("switch　ufis前状态：{}", UfisStatus.active);
        String switchUfis = request.getParameter("ufis");
        if (null == switchUfis)
        	return new ModelAndView("/switchUfis.jsp", "switch_ufis", "Ufis status: " + UfisStatus.active);
        logger.info("切换Ufis状态为：{}", switchUfis);
        UfisStatus.active = Boolean.parseBoolean(switchUfis);
        logger.info("switch　ufis后状态：{}", UfisStatus.active);
        return new ModelAndView("/switchUfis.jsp", "switch_ufis", "Switch ufis success! Ufis status: " + UfisStatus.active);
    }
    
    @RequestMapping(value = "/PnrContent", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String getPnrContent(HttpServletRequest request) {
        String pnr = request.getParameter("pnr");
        String office = request.getParameter("office");
        logger.info("/PnrContent接口,请求参数:pnr={},office={}", pnr, office);
        if (office == null || "".equals(office)) {
            office = "SHA255";
        }
        String xml = "";
        Timer.Context timeContext = PricingMetric.getInstance().timer(ConfigUtil.port+".RT");
        try {
            xml = EtermRtHandler.handle(pnr, office);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
        	timeContext.close();
        }
        return xml;
    }

    @RequestMapping(value = "/CancelPnr", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String getCancelPnr(HttpServletRequest request) {
        logger.info("/CancelPnr接口");
        String pnr = request.getParameter("pnr");
        String forceCancel = request.getParameter("force");
        String office = request.getParameter("office");
        boolean force = false;
        if (StringUtils.isNotBlank(forceCancel)) {
            force = Boolean.valueOf(forceCancel);
        }
        logger.info("CancelPnr请求参数 pnr:{} office:{} force:{}", pnr, office, force);
        String xml = "";
        try {
            xml = new CancelPNRParser().cancelPNR(pnr, force, office, false);
        } catch (SessionExpireException e) {
            logger.error("取消pnr发生session超时");
        } catch (UfisException e) {
        	logger.error(e.getMessage(), e);
		}
        return xml;
    }
    
    @RequestMapping(value = "/forceCancelPnr", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String getforceCancelPnr(HttpServletRequest request) {
        logger.info("/forceCancelPnr接口");
        String pnr = request.getParameter("pnr");
        String forceCancel = request.getParameter("force");
        String office = request.getParameter("office");
        boolean force = false;
        if (StringUtils.isNotBlank(forceCancel)) {
            force = Boolean.valueOf(forceCancel);
        }
        logger.info("forceCancelPnr请求参数 pnr:{} office:{} force:{}", pnr, office, force);
        String xml = "";
        try {
            xml = new CancelPNRParser().cancelPNR(pnr, force, office, true);
        } catch (SessionExpireException e) {
            logger.error("取消pnr发生session超时");
        } catch (UfisException e) {
        	logger.error(e.getMessage(), e);
		}
        return xml;
    }

    @RequestMapping(value = "/PnrAuth", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String getPnrAuth(HttpServletRequest request) {
        logger.info("/PnrAuth接口");
        String pnr = request.getParameter("pnr");
        String ownerOffice = request.getParameter("ownerOffice");
        String grantorOffice = request.getParameter("grantorOffice");
        String xml = new PNRAuthParser().pnrAuthorizeWithRetry(pnr, ownerOffice, grantorOffice);
        return xml;
    }

    @RequestMapping(value = "/CancelPnrAuth", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String getCancelPnrAuth(HttpServletRequest request) {
        logger.info("/CancelPnrAuth接口");
        String pnr = request.getParameter("pnr");
        String ownerOffice = request.getParameter("ownerOffice");
        String grantorOffice = request.getParameter("grantorOffice");
        String xml = "";
        try {
            xml = new PNRAuthParser().cancelPnrAuthorize(pnr, ownerOffice, grantorOffice);
        } catch (SessionExpireException e) {
            logger.error("执行取消pnr授权时发生session超时");
        }
        return xml;
    }
    
    /**
     * 根据PNR提取票号(新)
     */
    @RequestMapping(value = "/extractTktNoByPnr", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String extractTktNoByPnr(HttpServletRequest request) {
    	String pnr = request.getParameter("pnr");
    	String office = request.getParameter("officeId");
        boolean isDomestic = Boolean.parseBoolean(request.getParameter("isDomestic"));
        logger.info("/extractTktNoByPnr接口请求 pnr={}, officeId={}, isDomestic={}", pnr, office, isDomestic);
        EtermRtResponse etermRtResponse = TicketNoExtractHandler.handle(pnr, office, isDomestic);
        String etermRtResponseJson = null;
		try {
			etermRtResponseJson = JsonUtil.toJson(etermRtResponse, false);
		} catch (IOException e) {
			logger.info("JSON转化异常：", e.getMessage());
		}
		logger.info("/extractTktNoByPnr接口返回结果：{}", etermRtResponseJson);
        return etermRtResponseJson;
    }
    
    /**
     * 根据PNR提取票号
     */
    @RequestMapping(value = "/PnrTktExtractor", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String getPnrTktExtractor(HttpServletRequest request) {
        logger.info("/PnrTktExtractor接口");
        boolean needFare = Boolean.parseBoolean(request.getParameter("needFare"));
        boolean needIssueTktOffice = Boolean.parseBoolean(request.getParameter("needIssueTktOffice"));
        boolean needERRule = Boolean.parseBoolean(request.getParameter("needERRule"));
        String officeId = request.getParameter("officeId");
        String traceId = request.getParameter("traceId");
        String pnr = request.getParameter("pnr");
        boolean isDomestic = Boolean.parseBoolean(request.getParameter("isDomestic"));

        logger.info("needFare={},needIssueTktOffice={},needERRule={},officeId={},traceId={},pnr={},isDomestic={}", needFare,
                needIssueTktOffice, needERRule, officeId, traceId, pnr, isDomestic);

        ParseConfBean parseConfBean = new ParseConfBean();
        parseConfBean.setDomestic(isDomestic);
        RtInterceptorBean rtInterceptorBean = new RtInterceptorBean();

        ITktExtractorLogic tktExtractorLogic;
        if (parseConfBean.isDomestic) {
            tktExtractorLogic = new DomTktExtractorLogic();
        } else {
            tktExtractorLogic = new IntTktExtractorLogic();
        }

        if (StringUtils.isNotBlank(officeId)) {
            parseConfBean.setOfficeId(officeId);
        }

        if (StringUtils.isNotBlank(traceId)) {
            parseConfBean.setTraceId(traceId);
        }

        GeneralTktExtractorHandler generalTktExtractorHandler = new GeneralTktExtractorHandler(tktExtractorLogic);
        rtInterceptorBean.rtParserAfterHandlerList.add(generalTktExtractorHandler);
        parseConfBean.rtInterceptorBean = rtInterceptorBean;
        parseConfBean.needFare = needFare;
        parseConfBean.setNeedIssueTktOffice(needIssueTktOffice);
        parseConfBean.setNeedERRule(needERRule);

        String xml = "";
        int RETRY_ON_SESSION_EXPIRE = 1;
        RETRY:
        for (int cnt = 0; cnt < RETRY_ON_SESSION_EXPIRE; cnt++) {
            try {
                xml = new PNRParser(parseConfBean).parsePnrWithRetryOnceOnSessionExpire(parseConfBean, pnr).getValue0();
            } catch (BizException e) {
                if (e.getRetCode() == ReturnCode.E_SESSION_EXPIRED) {
                    logger.info("retry:{}", pnr);
                    continue RETRY;
                } else {
                    logger.error(" error:{}", TZUtil.stringifyException(e));
                }
            }
        }
        return xml;
    }
    
    //RTR中的结果：-CA-NBGX6P
    private static Pattern BIG_PNR_RTR_PATTERN = Pattern.compile(".*-(\\w{2})-(\\w{6})");
    /**
     * 大编码获取接口，该接口主要是用在RMK获取大编码失败的情况下，通过该接口获取大编码
     */
    @RequestMapping(value = "/getBigPnr", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String getBigPnr(HttpServletRequest request, @RequestParam String pnr) {
        String officeId = request.getParameter("officeId");
        logger.info("/getBigPnr接口请求参数:pnr={}", pnr);
        PnrRet.BIG_PNR bigPnr = new PnrRet.BIG_PNR();
        String[] vaues = null;
        if (UfisStatus.active && UfisStatus.getBigPnr) {
        	EtermUfisClient client = null;
        	try {
        		client = new EtermUfisClient(officeId);
        		client.execRt(pnr, false);
        		client.execCmd("RTR");
        		vaues = client.execCmd("PF").trim().replaceAll("\r", "\n").split("\n");
        	} catch(UfisException e) {
        		logger.error("获取大编码 Ufis异常：" + e.getMessage(), e);
        	} finally {
        		if (null != client)
        			client.close();
        	}
    	} else {
    		EtermWebClient client;
            if (officeId != null) {
                client = new EtermWebClient(officeId);
                client.connect(officeId);
            } else {
                client = new EtermWebClient();
                client.connect();
            }
            try {
                client.getRT(pnr, true);
                client.getRTR();
                vaues = client.getPF().split("\n");
            } catch (SessionExpireException e) {
                logger.error("获取大编码逻辑超时,{}", e);
            } finally {
                client.close();
            }
    	}
        
        if (vaues != null) {
            for (String rtr : vaues) {
                Matcher matcher = BIG_PNR_RTR_PATTERN.matcher(rtr);
                if (matcher.find()) {
                    bigPnr.Carrier = matcher.group(1);
                    bigPnr.PNR = matcher.group(2);
                    break;
                }
            }
        }
        
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        xstream.processAnnotations(PnrRet.BIG_PNR.class);

        return xstream.toXML(bigPnr);
    }
    
    /**
     * 根据PNR获取rt和pat内容，返回纯文本
     *
     * @param request
     * @param pnr
     * @return
     */
    @RequestMapping(value = "/rtpat", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String getRTPAT(HttpServletRequest request,
                           @RequestParam String pnr,
                           @RequestParam String officeId) {
        StringBuilder sb = new StringBuilder();
        if (UfisStatus.active && UfisStatus.rtpat) {
        	EtermUfisClient client = null;
        	try {
        		client = new EtermUfisClient(officeId);
        		String rtText = client.execRt(pnr, true);
        		sb.append("RTResult=");
                sb.append(rtText.replaceAll("\r", "\n"));
        		String patText = client.execCmd("PAT:A", true);
        		sb.append(";PATResult=");
        		sb.append(patText.replaceAll("\r", "\n"));
        	} catch(UfisException e) {
        		logger.error("Eterm RT+PAT　指令 Ufis异常：" + e.getMessage(), e);
        	} finally {
        		if (null != client)
        			client.close();
        	}
    	} else {
    		EtermWebClient etermWebClient = new EtermWebClient();
            try {
                etermWebClient.connect(officeId);
                ReturnClass<String> rtReturn = etermWebClient.getRT(pnr, false);
                if (rtReturn.isSuccess()) {
                    sb.append("RTResult=");
                    sb.append(rtReturn.getObject().replaceAll("\r", "\n"));
                    ReturnClass<String> patReturn = etermWebClient.executeCmdWithRetry("pat:a", false);
                    if (patReturn.isSuccess()) {
                        sb.append(";PATResult=");
                        sb.append(patReturn.getObject().replaceAll("\r", "\n"));
                    }
                }
            } catch (SessionExpireException e) {
                e.printStackTrace();
            } finally {
                etermWebClient.close();
            }
    	}
        return sb.toString();
    }

}