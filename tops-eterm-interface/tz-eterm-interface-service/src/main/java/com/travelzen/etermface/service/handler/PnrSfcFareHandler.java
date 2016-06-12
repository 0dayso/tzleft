package com.travelzen.etermface.service.handler;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.common.ufis.util.UfisException;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.EtermUfisClient;
import com.travelzen.etermface.service.EtermWebClient;
import com.travelzen.etermface.service.constant.UfisStatus;
import com.travelzen.etermface.service.entity.PnrOperateResult;
import com.travelzen.etermface.service.entity.config.PnrOperateConfig;
import com.travelzen.framework.core.common.ReturnClass;
import com.travelzen.framework.core.common.ReturnCode;
import com.travelzen.framework.core.time.DateTimeUtil;
import com.travelzen.rosetta.eterm.common.pojo.enums.PassengerType;
import com.travelzen.rosetta.eterm.common.pojo.rt.FnInfo;
import com.travelzen.rosetta.eterm.common.pojo.rt.PatInfo;
import com.travelzen.rosetta.eterm.common.pojo.rt.FnInfo.FnFare;
import com.travelzen.rosetta.eterm.common.pojo.rt.FnInfo.FnFareItem;
import com.travelzen.rosetta.eterm.common.pojo.rt.PatInfo.PatFare;
import com.travelzen.rosetta.eterm.common.pojo.rt.PatInfo.PatFareItem;
import com.travelzen.rosetta.eterm.parser.rt.subparser.FnInfoParser;
import com.travelzen.rosetta.eterm.parser.rt.subparser.PatInfoParser;

/**
 * PnrSfcFareHandler
 * <p>
 * @author yiming.yan
 * @Date Dec 22, 2015
 */
public class PnrSfcFareHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(PnrSfcFareHandler.class);
	
	private static String office = null;
	
	public PnrSfcFareHandler(String office) {
		PnrSfcFareHandler.office = office;
	}
	
	public ReturnClass<PnrOperateResult> handle(PnrOperateConfig config) throws SessionExpireException {
		LOGGER.info("PnrSfcFare　PnrOperateConfig:{}", config);
		
		PnrOperateResult result = new PnrOperateResult();
		ReturnClass<PnrOperateResult> resultRet = null;
		
		if (UfisStatus.active && UfisStatus.sfcFare) {
			EtermUfisClient client = null;
			try {
				client = new EtermUfisClient(office);
				resultRet = sfcFareByUfis(config, client);
			} catch (UfisException e) {
				LOGGER.error("Ufis异常：{}", e.getMessage());
				result.status = ReturnCode.ERROR.toString();
				result.error = "Ufis异常：" + e.getMessage();
				resultRet = new ReturnClass<PnrOperateResult>();
				resultRet.setStatus(ReturnCode.ERROR);
				resultRet.setObject(result);
			} finally {
				if (null != client)
					client.close();
			}
		} else {
			EtermWebClient client = new EtermWebClient(office);
			client.connect();
			try {
				resultRet = sfcFare(config, client);
			} finally {
				client.close();
			}
		}
		
		if (null == resultRet) {
			LOGGER.info("SFC Fare 失败");
			result.status = ReturnCode.ERROR.toString();
			result.error = "SFC Fare 失败";
			resultRet = new ReturnClass<PnrOperateResult>();
			resultRet.setStatus(ReturnCode.ERROR);
			resultRet.setObject(result);
		}
		
		LOGGER.info("PnrSfcFare　return:{}", resultRet);
		return resultRet;
	}
	
	public ReturnClass<PnrOperateResult> sfcFare(PnrOperateConfig config, EtermWebClient client) throws SessionExpireException {
		ReturnClass<PnrOperateResult> resultRet = new ReturnClass<PnrOperateResult>();
		PnrOperateResult result = new PnrOperateResult();
		
		ReturnClass<String> rtRet = client.getRT(config.pnr, false);
		if (rtRet == null || !rtRet.isSuccess()) {
			LOGGER.info("RT失败。");
			result.status = ReturnCode.ERROR.toString();
			result.error = "RT失败。";
			resultRet.setStatus(ReturnCode.ERROR);
			resultRet.setObject(result);
			return resultRet;
		}
		if (!checkFnFare(rtRet.getObject())) {
			LOGGER.info("PNR已有价格。");
			result.status = ReturnCode.ERROR.toString();
			result.error = "PNR已有价格。";
			resultRet.setStatus(ReturnCode.ERROR);
			resultRet.setObject(result);
			return resultRet;
		}
		
		ReturnClass<String> patRet = null;
		if (StringUtils.isNoneBlank(config.adtFareBasis)) {
			patRet = client.getPAT("A");
		} else if (StringUtils.isNoneBlank(config.chdFareBasis)) {
			patRet = client.getPAT("A*CH");
		} else {
			LOGGER.info("请求参数缺少fareBasis。");
			result.status = ReturnCode.ERROR.toString();
			result.error = "请求参数缺少fareBasis。";
			resultRet.setStatus(ReturnCode.ERROR);
			resultRet.setObject(result);
			return resultRet;
		}
		if (!patRet.isSuccess()) {
			LOGGER.info("PAT失败。");
			result.status = ReturnCode.ERROR.toString();
			result.error = "PAT失败。";
			resultRet.setStatus(ReturnCode.ERROR);
			resultRet.setObject(result);
			return resultRet;
		}
		
		PatInfo patInfo = PatInfoParser.parse(patRet.getObject().replaceAll("[\r\n]+", "\n"));
		if (null == patInfo) {
			LOGGER.info("PAT解析失败。");
			result.status = ReturnCode.ERROR.toString();
			result.error = "PAT解析失败。";
			resultRet.setStatus(ReturnCode.ERROR);
			resultRet.setObject(result);
			return resultRet;
		}
		List<PatFare> patFares = patInfo.getFares();
		ReturnClass<String> sfcRet = null;
		PATFARES:
		for (PatFare patFare:patFares) {
			if (patFare.getPsgType() == PassengerType.ADT) {
				for (PatFareItem patFareItem:patFare.getFareItems()) {
					if (StringUtils.equalsIgnoreCase(patFareItem.getFareBasis(), config.adtFareBasis)
							&& Double.valueOf(patFareItem.getCurrentFare()) == config.adtFare) {
						client.getPAT("A");
						sfcRet = client.rawExecuteCmd("SFC:" + patFareItem.getSfc());
						LOGGER.info("sfcRet结果:{}", sfcRet.getObject());
						break PATFARES;
					}
				}
				// QUNAR传的运价基础有误，因此只匹配价格
				for (PatFareItem patFareItem:patFare.getFareItems()) {
					if (Double.valueOf(patFareItem.getCurrentFare()) == config.adtFare) {
						client.getPAT("A");
						sfcRet = client.rawExecuteCmd("SFC:" + patFareItem.getSfc());
						LOGGER.info("sfcRet结果:{}", sfcRet.getObject());
						break PATFARES;
					}
				}
			} else if (patFare.getPsgType() == PassengerType.CHD) {
				for (PatFareItem patFareItem:patFare.getFareItems()) {
					if (StringUtils.equalsIgnoreCase(patFareItem.getFareBasis(), config.chdFareBasis)
							&& Double.valueOf(patFareItem.getCurrentFare()) == config.chdFare) {
						client.getPAT("A*CH");
						sfcRet = client.rawExecuteCmd("SFC:" + patFareItem.getSfc());
						LOGGER.info("sfcRet结果:{}", sfcRet.getObject());
						break PATFARES;
					}
				}
				// QUNAR传的运价基础有误，因此只匹配价格
				for (PatFareItem patFareItem:patFare.getFareItems()) {
					if (Double.valueOf(patFareItem.getCurrentFare()) == config.chdFare) {
						client.getPAT("A*CH");
						sfcRet = client.rawExecuteCmd("SFC:" + patFareItem.getSfc());
						LOGGER.info("sfcRet结果:{}", sfcRet.getObject());
						break PATFARES;
					}
				}
			}
		}
		if (null == sfcRet) {
			LOGGER.info("未找到匹配的价格，SFC失败。");
			result.status = ReturnCode.ERROR.toString();
			result.error = "未找到匹配的价格，SFC失败。";
			resultRet.setStatus(ReturnCode.ERROR);
			resultRet.setObject(result);
			return resultRet;
		}
		if (!sfcRet.isSuccess()) {
			LOGGER.info("SFC失败。");
			result.status = ReturnCode.ERROR.toString();
			result.error = "SFC失败。";
			resultRet.setStatus(ReturnCode.ERROR);
			resultRet.setObject(result);
			return resultRet;
		}
		
		ReturnClass<String> iRet = client.rawExecuteCmd("@I ", true);
		DateTimeUtil.SleepSec(1);
		iRet = client.rawExecuteCmd("@I ", true);
		if (!iRet.isSuccess()) {
			LOGGER.info("@I封口失败。");
			result.status = ReturnCode.ERROR.toString();
			result.error = "@I封口失败。";
			resultRet.setStatus(ReturnCode.ERROR);
			resultRet.setObject(result);
			return resultRet;
		}
		
		if (!checkFnFare(config, client)) {
			LOGGER.info("FN价格校验失败。");
			result.status = ReturnCode.ERROR.toString();
			result.error = "FN价格校验失败。";
			resultRet.setStatus(ReturnCode.ERROR);
			resultRet.setObject(result);
			return resultRet;
		}
		
		LOGGER.info("SFC Fare 成功");
		result.status = ReturnCode.SUCCESS.toString();
		resultRet.setStatus(ReturnCode.SUCCESS);
		resultRet.setObject(result);
		return resultRet;
	}
	
	// 校验FN价格是否已存在
	private boolean checkFnFare(String rtText) {
		for (String line:rtText.split("\n")) {
			if (line.contains(".FC/A") || line.contains(".FN/A") || line.contains(".FP/")) {
				return false;
			}
		}
		return true;
	}

	// 校验注入的FN价格是否正确
	private boolean checkFnFare(PnrOperateConfig config, EtermWebClient client) throws SessionExpireException {
		ReturnClass<String> rtRet = client.getRT(config.pnr, false);
		if (null == rtRet || !rtRet.isSuccess()) {
			LOGGER.info("FN价格校验时RT失败。");
			return false;
		}
		String rtText = rtRet.getObject().replaceAll("[\r\n]+", "\n");
		if (checkFnFare(rtText)) {
			LOGGER.info("FN价格校验时未找到FN价格。");
			return false;
		}
		FnInfo fnInfo = FnInfoParser.parse(rtText, false, null);
		LOGGER.info("解出FN价格：{}", fnInfo);
		if (null != fnInfo && null != fnInfo.getFares() && 0 != fnInfo.getFares().size()) {
			FnFare fnFare = fnInfo.getFares().get(0);
			if (null != fnFare && null != fnFare.getFareItems() && 0 != fnFare.getFareItems().size()) {
				FnFareItem fnFareItem = fnFare.getFareItems().get(0);
				// 只匹配价格 for QUNAR
//				if (StringUtils.isNotBlank(config.adtFareBasis) && Double.valueOf(fnFareItem.getCurrentFare()) == config.adtFare)
				if (Double.valueOf(fnFareItem.getCurrentFare()) == config.adtFare)
					return true;
//				else if (StringUtils.isNotBlank(config.chdFareBasis) && Double.valueOf(fnFareItem.getCurrentFare()) == config.chdFare)
				else if (Double.valueOf(fnFareItem.getCurrentFare()) == config.chdFare)
					return true;
			}
		}
		LOGGER.info("FN价格校验不匹配。");
		return false;
	}
	
	public ReturnClass<PnrOperateResult> sfcFareByUfis(PnrOperateConfig config, EtermUfisClient client) throws UfisException {
		ReturnClass<PnrOperateResult> resultRet = new ReturnClass<PnrOperateResult>();
		PnrOperateResult result = new PnrOperateResult();
		
		String rtRet = client.execRt(config.pnr, true);
		if (rtRet == null) {
			LOGGER.info("RT失败。");
			result.status = ReturnCode.ERROR.toString();
			result.error = "RT失败。";
			resultRet.setStatus(ReturnCode.ERROR);
			resultRet.setObject(result);
			return resultRet;
		}
		if (!checkFnFare(rtRet)) {
			LOGGER.info("PNR已有价格。");
			result.status = ReturnCode.ERROR.toString();
			result.error = "PNR已有价格。";
			resultRet.setStatus(ReturnCode.ERROR);
			resultRet.setObject(result);
			return resultRet;
		}
		
		String patRet = null;
		if (StringUtils.isNoneBlank(config.adtFareBasis)) {
			patRet = client.execCmd("PAT:A", true);
		} else if (StringUtils.isNoneBlank(config.chdFareBasis)) {
			patRet = client.execCmd("PAT:A*CH", true);
		} else {
			LOGGER.info("请求参数缺少fareBasis。");
			result.status = ReturnCode.ERROR.toString();
			result.error = "请求参数缺少fareBasis。";
			resultRet.setStatus(ReturnCode.ERROR);
			resultRet.setObject(result);
			return resultRet;
		}
		if (null == patRet) {
			LOGGER.info("PAT失败。");
			result.status = ReturnCode.ERROR.toString();
			result.error = "PAT失败。";
			resultRet.setStatus(ReturnCode.ERROR);
			resultRet.setObject(result);
			return resultRet;
		}
		
		PatInfo patInfo = PatInfoParser.parse(patRet.replaceAll("[\r\n]+", "\n"));
		if (null == patInfo) {
			LOGGER.info("PAT解析失败。");
			result.status = ReturnCode.ERROR.toString();
			result.error = "PAT解析失败。";
			resultRet.setStatus(ReturnCode.ERROR);
			resultRet.setObject(result);
			return resultRet;
		}
		List<PatFare> patFares = patInfo.getFares();
		String sfcRet = null;
		PATFARES:
		for (PatFare patFare:patFares) {
			if (patFare.getPsgType() == PassengerType.ADT) {
				for (PatFareItem patFareItem:patFare.getFareItems()) {
					if (StringUtils.equalsIgnoreCase(patFareItem.getFareBasis(), config.adtFareBasis)
							&& Double.valueOf(patFareItem.getCurrentFare()) == config.adtFare) {
						client.execCmd("PAT:A");
						sfcRet = client.execCmd("SFC:" + patFareItem.getSfc());
						LOGGER.info("sfcRet结果:{}", sfcRet);
						break PATFARES;
					}
				}
				// QUNAR传的运价基础有误，因此只匹配价格
				for (PatFareItem patFareItem:patFare.getFareItems()) {
					if (Double.valueOf(patFareItem.getCurrentFare()) == config.adtFare) {
						client.execCmd("PAT:A");
						sfcRet = client.execCmd("SFC:" + patFareItem.getSfc());
						LOGGER.info("sfcRet结果:{}", sfcRet);
						break PATFARES;
					}
				}
			} else if (patFare.getPsgType() == PassengerType.CHD) {
				for (PatFareItem patFareItem:patFare.getFareItems()) {
					if (StringUtils.equalsIgnoreCase(patFareItem.getFareBasis(), config.chdFareBasis)
							&& Double.valueOf(patFareItem.getCurrentFare()) == config.chdFare) {
						client.execCmd("PAT:A*CH");
						sfcRet = client.execCmd("SFC:" + patFareItem.getSfc());
						LOGGER.info("sfcRet结果:{}", sfcRet);
						break PATFARES;
					}
				}
				// QUNAR传的运价基础有误，因此只匹配价格
				for (PatFareItem patFareItem:patFare.getFareItems()) {
					if (Double.valueOf(patFareItem.getCurrentFare()) == config.chdFare) {
						client.execCmd("PAT:A*CH");
						sfcRet = client.execCmd("SFC:" + patFareItem.getSfc());
						LOGGER.info("sfcRet结果:{}", sfcRet);
						break PATFARES;
					}
				}
			}
		}
		if (null == sfcRet) {
			LOGGER.info("SFC失败。");
			result.status = ReturnCode.ERROR.toString();
			result.error = "SFC失败。";
			resultRet.setStatus(ReturnCode.ERROR);
			resultRet.setObject(result);
			return resultRet;
		}
		
		String iRet = client.execCmd("@I ");
		DateTimeUtil.SleepSec(1);
		iRet = client.execCmd("@I ");
		if (null == iRet) {
			LOGGER.info("@I封口失败。");
			result.status = ReturnCode.ERROR.toString();
			result.error = "@I封口失败。";
			resultRet.setStatus(ReturnCode.ERROR);
			resultRet.setObject(result);
			return resultRet;
		}
		
		if (!checkFnFareByUfis(config, client)) {
			LOGGER.info("FN价格校验失败。");
			result.status = ReturnCode.ERROR.toString();
			result.error = "FN价格校验失败。";
			resultRet.setStatus(ReturnCode.ERROR);
			resultRet.setObject(result);
			return resultRet;
		}
		
		LOGGER.info("SFC Fare 成功");
		result.status = ReturnCode.SUCCESS.toString();
		resultRet.setStatus(ReturnCode.SUCCESS);
		resultRet.setObject(result);
		return resultRet;
	}
	
	// 校验注入的FN价格是否正确
	private boolean checkFnFareByUfis(PnrOperateConfig config, EtermUfisClient client) throws UfisException {
		String rtRet = client.execRt(config.pnr, true);
		if (null == rtRet) {
			LOGGER.info("FN价格校验时RT失败。");
			return false;
		}
		String rtText = rtRet.replaceAll("[\r\n]+", "\n");
		if (checkFnFare(rtText)) {
			LOGGER.info("FN价格校验时未找到FN价格。");
			return false;
		}
		FnInfo fnInfo = FnInfoParser.parse(rtText, false, null);
		LOGGER.info("解出FN价格：{}", fnInfo);
		if (null != fnInfo && null != fnInfo.getFares() && 0 != fnInfo.getFares().size()) {
			FnFare fnFare = fnInfo.getFares().get(0);
			if (null != fnFare && null != fnFare.getFareItems() && 0 != fnFare.getFareItems().size()) {
				FnFareItem fnFareItem = fnFare.getFareItems().get(0);
				// 只匹配价格 for QUNAR
//				if (StringUtils.isNotBlank(config.adtFareBasis) && Double.valueOf(fnFareItem.getCurrentFare()) == config.adtFare)
				if (Double.valueOf(fnFareItem.getCurrentFare()) == config.adtFare)
					return true;
//				else if (StringUtils.isNotBlank(config.chdFareBasis) && Double.valueOf(fnFareItem.getCurrentFare()) == config.chdFare)
				else if (Double.valueOf(fnFareItem.getCurrentFare()) == config.chdFare)
					return true;
			}
		}
		LOGGER.info("FN价格校验不匹配。");
		return false;
	}
	
//	private void doLast(PnrOperateConfig param) throws SessionExpireException {
//		client.connect();
//		System.out.println(client.getRT(param.pnr, false).getObject());
//		client.getXEs(Arrays.asList(new Integer[]{14,15,16,17}));
//		System.out.println(client.getRT(param.pnr, false).getObject());
//		client.close();
//	}

}
