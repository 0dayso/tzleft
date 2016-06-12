package com.travelzen.etermface.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.travelzen.etermface.service.handler.Eterm3in1PrintHandler;

/**
 * Eterm 三合一打印
 * <p>
 * @author yiming.yan
 * @Date Dec 8, 2015
 */
@Controller
@RequestMapping("/eterm3in1print")
public class Eterm3in1PrintController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Eterm3in1PrintController.class);
	
	/**
	 * 打印国内报销凭证
	 * <p>
	 * @param ticketNo 票号
	 * @param iteneraryNo　行程单号
	 * @return
	 */
	@RequestMapping(value = "/pid", method = RequestMethod.POST)
	@ResponseBody
	public String pid(@RequestParam String ticketNo, @RequestParam String iteneraryNo, String officeId) {
		LOGGER.info("/eterm3in1print/pid　请求：ticketNo:{}, iteneraryNo:{}, officeId:{}", ticketNo, iteneraryNo, officeId);
		String result = null;
		try {
			result = Eterm3in1PrintHandler.handlePid(ticketNo, iteneraryNo, officeId);
		} catch (Exception e) {
			LOGGER.info(e.getMessage(), e);
		}
		LOGGER.info("/eterm3in1print/pid　返回：{}", result);
		return result;
	}
	
	/**
	 * 打印国际报销凭证
	 * <p>
	 * @param ticketNo 票号
	 * @param iteneraryNo　行程单号
	 * @param ticketPrice 机票价格
	 * @return
	 */
	@RequestMapping(value = "/pii", method = RequestMethod.POST)
	@ResponseBody
	public String pii(@RequestParam String ticketNo, @RequestParam String iteneraryNo, @RequestParam String ticketPrice, String officeId) {
		LOGGER.info("/eterm3in1print/pii　请求： ticketNo:{}, iteneraryNo:{}, ticketPrice:{}, officeId:{}", ticketNo, iteneraryNo, ticketPrice, officeId);
		String result = null;
		try {
			result = Eterm3in1PrintHandler.handlePii(ticketNo, iteneraryNo, ticketPrice, officeId);
		} catch (Exception e) {
			LOGGER.info(e.getMessage(), e);
		}
		LOGGER.info("/eterm3in1print/pii　返回：{}", result);
		return result;
	}
	
	/**
	 * 作废报销凭证
	 * <p>
	 * @param ticketNo 票号
	 * @param iteneraryNo　行程单号
	 * @return
	 */
	@RequestMapping(value = "/vi", method = RequestMethod.POST)
	@ResponseBody
	public String vi(@RequestParam String ticketNo, @RequestParam String iteneraryNo, String officeId) {
		LOGGER.info("/eterm3in1print/vi　请求： ticketNo:{}, iteneraryNo:{}, officeId:{}", ticketNo, iteneraryNo, officeId);
		String result = null;
		try {
			result = Eterm3in1PrintHandler.handleVi(ticketNo, iteneraryNo, officeId);
		} catch (Exception e) {
			LOGGER.info(e.getMessage(), e);
		}
		LOGGER.info("/eterm3in1print/vi　返回：{}", result);
		return result;
	}
	
	/**
	 * 作废空白报销凭证
	 * <p>
	 * @param ticketNo 票号
	 * @param iteneraryNo　行程单号
	 * @return
	 */
	@RequestMapping(value = "/vbi", method = RequestMethod.POST)
	@ResponseBody
	public String vbi(@RequestParam String iteneraryNo, String officeId) {
		LOGGER.info("/eterm3in1print/vbi　请求： iteneraryNo:{}, officeId:{}", iteneraryNo, officeId);
		String result = null;
		try {
			result = Eterm3in1PrintHandler.handleVbi(iteneraryNo, officeId);
		} catch (Exception e) {
			LOGGER.info(e.getMessage(), e);
		}
		LOGGER.info("/eterm3in1print/vbi　返回：{}", result);
		return result;
	}
	
	/**
	 * 本票 && 行程单：PNR
	 * <p>
	 * @param pnr PNR号
	 * @return
	 */
//	@RequestMapping(value = "/op", method = RequestMethod.POST)
	@ResponseBody
	public String op(@RequestParam String pnr, String officeId) {
		
		return null;
	}
	
	/**
	 * 展示行程单:电子客票
	 * <p>
	 * @param ticketNo 票号
	 * @return
	 */
	@RequestMapping(value = "/dt", method = RequestMethod.POST)
	@ResponseBody
	public String dt(@RequestParam String ticketNo, String officeId) {
		LOGGER.info("/eterm3in1print/dt　请求： ticketNo:{}, officeId:{}", ticketNo, officeId);
		LOGGER.info("dt指令写死配置： officeId = SHA255");
		officeId = "SHA255"; // dt指令写死配置SHA255
		String result = null;
		try {
			result = Eterm3in1PrintHandler.handleDt(ticketNo, officeId);
		} catch (Exception e) {
			LOGGER.info(e.getMessage(), e);
		}
		LOGGER.info("/eterm3in1print/dt　返回：{}", result);
		return result;
	}
	
	/**
	 * 展示行程单：普通
	 * <p>
	 * @param pnr PNR号
	 * @return
	 */
	@RequestMapping(value = "/dp", method = RequestMethod.POST)
	@ResponseBody
	public String dp(@RequestParam String pnr, String officeId) {
		LOGGER.info("/eterm3in1print/dp　请求： pnr:{}, officeId:{}", pnr, officeId);
		String result = null;
		try {
			result = Eterm3in1PrintHandler.handleDp(pnr, officeId);
		} catch (Exception e) {
			LOGGER.info(e.getMessage(), e);
		}
		LOGGER.info("/eterm3in1print/dp　返回：{}", result);
		return result;
	}

}
