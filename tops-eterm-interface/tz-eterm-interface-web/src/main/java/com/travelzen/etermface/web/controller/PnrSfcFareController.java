package com.travelzen.etermface.web.controller;

import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.web.bean.PnrSetPriceRequestBean;
import com.travelzen.etermface.service.entity.ParseConfBean;
import com.travelzen.etermface.service.entity.PnrOperateResult;
import com.travelzen.etermface.service.entity.config.PnrOperateConfig;
import com.travelzen.etermface.service.handler.PnrSfcFareHandler;
import com.travelzen.framework.core.common.ReturnClass;

/**
 * Pnr Sfc Fare
 * <p>
 * @author yiming.yan
 * @Date Jan 4, 2016
 */
@Controller
public class PnrSfcFareController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PnrSfcFareController.class);

	/**
	 * 将运价做到pnr里面完成bop自动出票
	 *
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/pnrSfcFare", method = {RequestMethod.POST, RequestMethod.GET}, produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String sfcFare(PnrSetPriceRequestBean req) {
		LOGGER.info("/pnrSfcFare 接口得到请求：{}", req);
		
		XStream xstream = new XStream();
		xstream.processAnnotations(PnrOperateResult.class);
		
		ParseConfBean conf = new ParseConfBean();
		conf.setOfficeId(req.getOfficeId());
		
		PnrOperateConfig config = new PnrOperateConfig();
		config.officeId = req.getOfficeId();
		config.pnr = req.getPnr();
		config.adtFareBasis = req.getAdtFareBasis();
		config.chdFareBasis = req.getChdFareBasis();
		//增加成人儿童票面价
		config.adtFare = req.getAdtFare();
		config.chdFare = req.getChdFare();

		ReturnClass<PnrOperateResult> result = null;
		
		PnrSfcFareHandler handler = new PnrSfcFareHandler(conf.getOfficeId());
		try {
			result = handler.handle(config);
		} catch (SessionExpireException e) {
			LOGGER.error("session超时，{}", e);
			return "Session超时";
		}
		
		StringWriter writer = new StringWriter();
		xstream.marshal(result.getObject(), new PrettyPrintWriter(writer, new NoNameCoder()));

		String xml = writer.toString();
		LOGGER.info("/pnrSfcFare 接口返回结果：{}", xml);
		return xml;
	}
}
