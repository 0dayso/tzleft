package com.travelzen.etermface.web.controller;

import java.io.StringWriter;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.constant.PnrParserConstant;
import com.travelzen.etermface.service.entity.DetrCombineResult;
import com.travelzen.etermface.service.entity.DetrResult;
import com.travelzen.etermface.service.entity.DetrTimeResult;
import com.travelzen.etermface.service.entity.DetrfResult;
import com.travelzen.etermface.service.entity.DetrsResult;
import com.travelzen.etermface.service.entity.ParseConfBean;
import com.travelzen.etermface.service.entity.config.FullDetrConfig;
import com.travelzen.etermface.service.ticket.DetrParser;
import com.travelzen.etermface.service.ticket.DetrTimeParser;
import com.travelzen.etermface.service.ticket.DetrfParser;
import com.travelzen.etermface.service.ticket.DetrsParser;
import com.travelzen.framework.core.common.ReturnClass;
import com.travelzen.framework.core.common.ReturnCode;
import com.travelzen.framework.core.util.TZUtil;

/**
 * 根据票号来处理的逻辑，添加了授权验证逻辑，写的不是很优雅，此处是优化点。
 */
@Controller
public class DetrController {
	
    private static Logger logger = LoggerFactory.getLogger(DetrController.class);
    private static String NOAUTHORITY = "未授权";


    /**
     * detr/detrs/detrf合并功能
     *
     * @param request
     * @param locale
     * @param tktNumber
     * @param officeId
     * @param model
     * @return
     */
    @RequestMapping(value = "/flight/ticket/detrCombine", method = {RequestMethod.POST, RequestMethod.GET}, produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String detrCombine(HttpServletRequest request, Locale locale, @RequestParam("tktNumber") String tktNumber, String officeId, ModelMap model) {

        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        StringWriter writer = new StringWriter();
        xstream.processAnnotations(DetrCombineResult.class);

        ParseConfBean confBean = new ParseConfBean();
        if (StringUtils.isNotBlank(officeId)) {
            confBean.setOfficeId(officeId);
        }

        FullDetrConfig cfg = new FullDetrConfig();
        cfg.tktNumber = tktNumber;

        DetrfParser detrFParser = new DetrfParser(confBean);
        ReturnClass<DetrfResult> detrfResultRet = new ReturnClass<>(ReturnCode.ERROR);

        DetrParser detrParser = new DetrParser(confBean);
        ReturnClass<DetrResult> detrResultRet = new ReturnClass<>(ReturnCode.ERROR);

        DetrsParser detrsParser = new DetrsParser(confBean);
        ReturnClass<DetrsResult> detrsResultRet = new ReturnClass<>(ReturnCode.ERROR);

        DetrCombineResult detrCombineResult = new DetrCombineResult();

        RETRY:
        for (int cnt = 0; cnt < PnrParserConstant.RETRY_ON_SESSION_EXPIRE; cnt++) {
            detrfResultRet = detrFParser.parse(cfg);
			detrResultRet = detrParser.parse(cfg);
			detrsResultRet = detrsParser.parserDetr(tktNumber);

			if (detrfResultRet.isSuccess()) {
			    detrCombineResult.detrfResult = detrfResultRet.getObject();
			} else if (detrfResultRet.getStatus().equals(ReturnCode.E_ORDER_AUTHORIZATION_ERROR)) {
			    if (detrfResultRet.getObject() == null) {
			        detrfResultRet.setObject(new DetrfResult());
			    }
			    detrfResultRet.getObject().STATUS = ReturnCode.E_ORDER_AUTHORIZATION_ERROR.getErrorCode();
			    detrfResultRet.getObject().ERRORS = NOAUTHORITY;
			    detrCombineResult.detrfResult = detrfResultRet.getObject();
			}
			if (detrResultRet.isSuccess()) {
			    detrCombineResult.detrResult = detrResultRet.getObject();
			} else if (detrResultRet.getStatus().equals(ReturnCode.E_ORDER_AUTHORIZATION_ERROR)) {
			    if (detrResultRet.getObject() == null) {
			        detrResultRet.setObject(new DetrResult());
			    }
			    detrResultRet.getObject().STATUS = ReturnCode.E_ORDER_AUTHORIZATION_ERROR.getErrorCode();
			    detrResultRet.getObject().ERRORS = NOAUTHORITY;
			    detrCombineResult.detrResult = detrResultRet.getObject();
			}
			if (detrsResultRet.isSuccess()) {
			    detrCombineResult.detrsResult = detrsResultRet.getObject();
			} else if (detrsResultRet.getStatus().equals(ReturnCode.E_ORDER_AUTHORIZATION_ERROR)) {
			    if (detrsResultRet.getObject() == null) {
			        detrsResultRet.setObject(new DetrsResult());
			    }
			    detrsResultRet.getObject().STATUS = ReturnCode.E_ORDER_AUTHORIZATION_ERROR.getErrorCode();
			    detrsResultRet.getObject().ERRORS = NOAUTHORITY;
			    detrCombineResult.detrsResult = detrsResultRet.getObject();
			}
			break RETRY;
        }

        xstream.marshal(detrCombineResult, new PrettyPrintWriter(writer, new NoNameCoder()));
        String xml = writer.toString();
        return xml;
    }

    /**
     * detrf处理逻辑
     *
     * @param request
     * @param locale
     * @param tktNumber
     * @param officeId
     * @param model
     * @return
     */
    @RequestMapping(value = "/flight/ticket/detrf", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String detrf(HttpServletRequest request, Locale locale, @RequestParam("tktNumber") String tktNumber, String officeId, ModelMap model) {
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        StringWriter writer = new StringWriter();
        xstream.processAnnotations(DetrfResult.class);

        DetrfResult fullDetrResult = new DetrfResult();

        ParseConfBean confBean = new ParseConfBean();
        if (StringUtils.isNotBlank(officeId)) {
            confBean.setOfficeId(officeId);
        } else {
        	confBean.setOfficeId("SHA255");
        }
        DetrfParser parser = new DetrfParser(confBean);
        FullDetrConfig cfg = new FullDetrConfig();
        cfg.tktNumber = tktNumber;

        ReturnClass<DetrfResult> ret = new ReturnClass<>(ReturnCode.ERROR);

        RETRY:
        for (int cnt = 0; cnt < PnrParserConstant.RETRY_ON_SESSION_EXPIRE; cnt++) {
            ret = parser.parse(cfg);
			break RETRY;
        }

        if (ret.isSuccess()) {
            fullDetrResult.STATUS = ReturnCode.SUCCESS.toString();
        } else if (ret.getStatus().equals(ReturnCode.E_ORDER_AUTHORIZATION_ERROR)) {
            fullDetrResult.STATUS = ReturnCode.E_ORDER_AUTHORIZATION_ERROR.getErrorCode();
            fullDetrResult.ERRORS = NOAUTHORITY;
        } else {
            fullDetrResult.STATUS = ReturnCode.ERROR.toString();
            fullDetrResult.ERRORS = ret.getMessage();
        }
        if (ret.getObject() == null) {
            ret.setObject(new DetrfResult());
        }
        ret.getObject().STATUS = fullDetrResult.STATUS;
        ret.getObject().ERRORS = fullDetrResult.ERRORS;

        xstream.marshal(ret.getObject(), new PrettyPrintWriter(writer, new NoNameCoder()));
        String xml = writer.toString();

        return xml;
    }

    /**
     * detr指令处理逻辑
     *
     * @param request
     * @param locale
     * @param tktNumber
     * @param officeId
     * @param model
     * @return
     */
    @RequestMapping(value = "/flight/ticket/detr", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String detr(HttpServletRequest request, Locale locale, @RequestParam("tktNumber") String tktNumber, String officeId, ModelMap model) {

        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        StringWriter writer = new StringWriter();
        xstream.processAnnotations(DetrResult.class);

        DetrResult fullDetrResult = new DetrResult();

        ParseConfBean confBean = new ParseConfBean();
        if (StringUtils.isNotBlank(officeId)) {
            confBean.setOfficeId(officeId);
        }

        DetrParser parser = new DetrParser(confBean);

        FullDetrConfig cfg = new FullDetrConfig();
        cfg.tktNumber = tktNumber;

        ReturnClass<DetrResult> ret = new ReturnClass<>(ReturnCode.ERROR);

        RETRY:
        for (int cnt = 0; cnt < PnrParserConstant.RETRY_ON_SESSION_EXPIRE; cnt++) {
        	ret = parser.parse(cfg);
            break RETRY;
        }

        if (ret.isSuccess()) {
            fullDetrResult.STATUS = ReturnCode.SUCCESS.toString();
        } else if (ret.getStatus().equals(ReturnCode.E_ORDER_AUTHORIZATION_ERROR)) {
            fullDetrResult.STATUS = ReturnCode.E_ORDER_AUTHORIZATION_ERROR.getErrorCode();
            fullDetrResult.ERRORS = NOAUTHORITY;
        } else {
            fullDetrResult.STATUS = ReturnCode.ERROR.toString();
            fullDetrResult.ERRORS = ret.getMessage();
        }
        if (ret.getObject() == null) {
            ret.setObject(new DetrResult());
        }
        ret.getObject().STATUS = fullDetrResult.STATUS;
        ret.getObject().ERRORS = fullDetrResult.ERRORS;

        xstream.marshal(ret.getObject(), new PrettyPrintWriter(writer, new NoNameCoder()));
        String xml = writer.toString();

        return xml;
    }

    /**
     * detrs处理逻辑
     *
     * @param request
     * @param locale
     * @param tktNumber
     * @param officeId
     * @param model
     * @return
     */
    @RequestMapping(value = "/flight/ticket/detrs", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String detrs(HttpServletRequest request, Locale locale, @RequestParam("tktNumber") String tktNumber, String officeId, ModelMap model) {
        ParseConfBean confBean = new ParseConfBean();
        officeId = "HKG999"; //写死
        if (StringUtils.isNotBlank(officeId)) {
            confBean.setOfficeId(officeId);
        }

        DetrsParser parser = new DetrsParser(confBean);

        ReturnClass<DetrsResult> ret = new ReturnClass<>(ReturnCode.ERROR);

        RETRY:
        for (int cnt = 0; cnt < PnrParserConstant.RETRY_ON_SESSION_EXPIRE; cnt++) {
        	ret = parser.parserDetr(tktNumber);
            break RETRY;
        }

        logger.info("{} success:{}", tktNumber, ret.getObject());

        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        StringWriter writer = new StringWriter();


        if (ret.isSuccess()) {
            ret.getObject().STATUS = ReturnCode.SUCCESS.toString();
        } else if (ret.getStatus().equals(ReturnCode.E_ORDER_AUTHORIZATION_ERROR)) {
            if (ret.getObject() == null) {
                ret.setObject(new DetrsResult());
            }
            ret.getObject().STATUS = ReturnCode.E_ORDER_AUTHORIZATION_ERROR.getErrorCode();
            ret.getObject().ERRORS = NOAUTHORITY;
        } else {
            ret.getObject().STATUS = ReturnCode.ERROR.toString();
            ret.getObject().ERRORS = ret.getMessage();
        }

        xstream.processAnnotations(DetrsResult.class);
        xstream.alias("DetrsResult", DetrsResult.class);
        xstream.marshal(ret.getObject(), new PrettyPrintWriter(writer, new NoNameCoder()));
        String xml = writer.toString();

        return xml;
    }
    
    @RequestMapping(value = "/flight/pnr/detrTime", method = {RequestMethod.POST, RequestMethod.GET}, produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String detrTime(HttpServletRequest request, Locale locale, @RequestParam("pnr") String pnr, String officeId, String traceId, ModelMap model) {
        ParseConfBean conf = new ParseConfBean();
        conf.setTraceId(traceId);
        DetrTimeParser detrTimeParser = new DetrTimeParser();
        try {
            ReturnClass<DetrTimeResult> detrTimeResult = detrTimeParser.parse(pnr);

            StaxDriver sd = new StaxDriver(new NoNameCoder());
            XStream xstream = new XStream(sd);
            StringWriter writer = new StringWriter();
            xstream.processAnnotations(DetrTimeResult.class);

            xstream.marshal(detrTimeResult.getObject(), new PrettyPrintWriter(writer, new NoNameCoder()));
            String xml = writer.toString();
            return xml;

        } catch (SessionExpireException e) {
            logger.error("{}",TZUtil.stringifyException(e));
            e.printStackTrace();
        }
        return "ERROR";
    }

}
