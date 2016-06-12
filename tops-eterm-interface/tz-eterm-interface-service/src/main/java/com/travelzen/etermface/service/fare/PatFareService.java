package com.travelzen.etermface.service.fare;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.common.ufis.util.UfisException;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.common.pojo.PassengerType;
import com.travelzen.etermface.common.pojo.fare.PatFareBySegmentParams;
import com.travelzen.etermface.common.pojo.fare.PatFareRequest;
import com.travelzen.etermface.common.pojo.fare.PatFareResponse;
import com.travelzen.etermface.common.pojo.fare.PatFareResponse.PatFare;
import com.travelzen.etermface.common.pojo.fare.PatFareResponse.PatFareItem;
import com.travelzen.etermface.service.EtermUfisClient;
import com.travelzen.etermface.service.EtermWebClient;
import com.travelzen.etermface.service.constant.UfisStatus;
import com.travelzen.etermface.service.fare.util.PatFareParser;
import com.travelzen.etermface.service.fare.util.SsCmdGenerator;
import com.travelzen.framework.core.common.ReturnClass;

/**
 * 获取PAT报价通用接口
 * @author yiming.yan
 */
public class PatFareService {

	private static Logger logger = LoggerFactory.getLogger(PatFareService.class);
	
	public PatFareResponse getPat(PatFareRequest patFareRequest) {
		if (patFareRequest.getPnr() == null) {
			logger.info("PAT通过PNR获取报价请求PNR为空！");
			return null;
		}
    	logger.info("PAT通过PNR获取报价请求：" + patFareRequest.getPnr());
    	List<PatFare> fares = null;
    	if (UfisStatus.active && UfisStatus.patFare) {
    		EtermUfisClient client = null;
    		try {
    			client = new EtermUfisClient(patFareRequest.getOffice());
            	logger.info("PAT解析开始");
            	// 先RT
            	client.execRt(patFareRequest.getPnr(), true);
            	// 再PAT
            	fares = generalPatByUfis(patFareRequest, client);
    		} catch (UfisException e) {
    			logger.info("getPat时Ufis异常。");
    		} finally {
    			if (null != client)
    				client.close();
    		}
    	} else {
    		EtermWebClient client = new EtermWebClient();
    		try {
    			client.connect(patFareRequest.getOffice());
            	logger.info("PAT解析开始");
            	// 先RT
            	client.getRT(patFareRequest.getPnr(), false);
            	// 再PAT
            	fares = generalPat(patFareRequest, client);
    		} catch (SessionExpireException e) {
    			logger.info("getPat时session超时。");
    		} finally {
    			client.close();
    		}
    	}
		if (null == fares || 0 == fares.size()) {
			logger.info("PatParser未解析出价格。");
			return null;
		}
		PatFareResponse patFareResponse = new PatFareResponse();
		patFareResponse.setFares(fares);
		logger.info("PAT解析结果：" + patFareResponse);
		return patFareResponse;
	}
	
	public PatFareResponse getPatBySegment(PatFareRequest patFareRequest){
		if (patFareRequest.getPatFareBySegmentParams() == null) {
			logger.info("PAT通过航段获取报价请求参数为空！");
			return null;
		}
    	logger.info("PAT通过航段获取报价请求：" + patFareRequest.getPatFareBySegmentParams());
    	if (null == patFareRequest.getPassengerTypes()) {
    		logger.info("PAT错误：请求无乘客类型！");
    		return null;
    	}
    	boolean isChild = patFareRequest.getPassengerTypes().contains(PassengerType.CHD)?true:false;
    	String ssCmd = SsCmdGenerator.convertCommand(patFareRequest.getPatFareBySegmentParams(), isChild);
    	if (null == ssCmd) {
    		logger.info("PAT错误：SS指令转化失败！");
    		return null;
    	}
    	List<PatFare> fares = null;
    	if (UfisStatus.active && UfisStatus.patFare) {
    		EtermUfisClient client = null;
    		try {
    			client = new EtermUfisClient(patFareRequest.getOffice());
            	// 先SS
            	boolean ssSuccess = enhanceSsByUfis(patFareRequest.getPatFareBySegmentParams(), isChild, client);
            	if (!ssSuccess) {
            		logger.info("PAT错误：SS指令错误！");
        			client.close();
        			return null;
            	}
            	// 再PAT
            	fares = generalPatByUfis(patFareRequest, client);
    		} catch (UfisException e) {
    			logger.info("getPat时Ufis异常。");
    		} finally {
    			if (null != client)
    				client.close();
    		}
    	} else {
    		EtermWebClient client = new EtermWebClient();
    		try {
    			client.connect(patFareRequest.getOffice(), 30000);
            	// 先SS
            	boolean ssSuccess = enhanceSs(patFareRequest.getPatFareBySegmentParams(), isChild, client);
            	if (!ssSuccess) {
            		logger.info("PAT错误：SS指令错误！");
        			client.close();
        			return null;
            	}
            	// 再PAT
            	fares = generalPat(patFareRequest, client);
    		} catch (SessionExpireException e) {
    			logger.info("getPat时session超时。");
    		} finally {
    			client.close();
    		}
    	}
    	if (0 == fares.size()) {
			logger.info("PAT错误：PatParser未解析出价格。");
			return null;
		}
    	PatFareResponse patFareResponse = new PatFareResponse();
		patFareResponse.setFares(fares);
		logger.info("PAT结果：" + patFareResponse);
		return patFareResponse;
	}
	
	private boolean enhanceSs(PatFareBySegmentParams patParams, boolean isChild, EtermWebClient client) throws SessionExpireException {
		DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
    	DateTime dateTime = DateTime.parse(patParams.getDeptDate(), format);
    	String ssCmd = SsCmdGenerator.convertCommand(patParams, isChild);
    	if (null == ssCmd) {
    		logger.info("PAT错误：SS指令转化失败！");
    		return false;
    	}
    	client.extendSessionExpireMillsec(3000, "ss+");
    	ReturnClass<String> ssStr = client.executeCmdWithRetry(ssCmd, true);
    	while (ssStr.getObject().contains("请一次完成PNR并封口")) {
    		dateTime = dateTime.plusDays(2);
    		patParams.setDeptDate(dateTime.toString("yyyy-MM-dd"));
    		ssCmd = SsCmdGenerator.convertCommand(patParams, isChild);
        	if (null == ssCmd) {
        		logger.info("PAT错误：SS指令转化失败！");
        		return false;
        	}
        	client.extendSessionExpireMillsec(3000, "ss+");
        	ssStr = client.executeCmdWithRetry(ssCmd, true);
    	}
    	if (ssStr.getObject().contains("UNABLE TO SELL")) {
    		dateTime = dateTime.minusDays(1);
    		patParams.setDeptDate(dateTime.toString("yyyy-MM-dd"));
    		ssCmd = SsCmdGenerator.convertCommand(patParams, isChild);
        	if (null == ssCmd) {
        		logger.info("PAT错误：SS指令转化失败！");
        		return false;
        	}
        	client.extendSessionExpireMillsec(3000, "ss+");
        	ssStr = client.executeCmdWithRetry(ssCmd, true);
    	}
    	if (ssStr.getObject().contains("UNABLE TO SELL")) {
    		logger.info("PAT错误：请一次完成PNR并封口 + UNABLE TO SELL！");
    		return false;
    	}
    	return true;
	}
	
	private boolean enhanceSsByUfis(PatFareBySegmentParams patParams, boolean isChild, EtermUfisClient client) throws UfisException {
		DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
    	DateTime dateTime = DateTime.parse(patParams.getDeptDate(), format);
    	String ssCmd = SsCmdGenerator.convertCommand(patParams, isChild);
    	if (null == ssCmd) {
    		logger.info("PAT错误：SS指令转化失败！");
    		return false;
    	}
    	client.extendSessionExpire(3000);
    	String ssStr = client.execCmd(ssCmd, false);
    	while (ssStr.contains("请一次完成PNR并封口")) {
    		dateTime = dateTime.plusDays(2);
    		patParams.setDeptDate(dateTime.toString("yyyy-MM-dd"));
    		ssCmd = SsCmdGenerator.convertCommand(patParams, isChild);
        	if (null == ssCmd) {
        		logger.info("PAT错误：SS指令转化失败！");
        		return false;
        	}
        	client.extendSessionExpire(3000);
        	ssStr = client.execCmd(ssCmd, false);
    	}
    	if (ssStr.contains("UNABLE TO SELL")) {
    		dateTime = dateTime.minusDays(1);
    		patParams.setDeptDate(dateTime.toString("yyyy-MM-dd"));
    		ssCmd = SsCmdGenerator.convertCommand(patParams, isChild);
        	if (null == ssCmd) {
        		logger.info("PAT错误：SS指令转化失败！");
        		return false;
        	}
        	client.extendSessionExpire(3000);
        	ssStr = client.execCmd(ssCmd, false);
    	}
    	if (ssStr.contains("UNABLE TO SELL")) {
    		logger.info("PAT错误：请一次完成PNR并封口 + UNABLE TO SELL！");
    		return false;
    	}
    	return true;
	}

	private List<PatFare> generalPat(PatFareRequest patFareRequest, EtermWebClient client) throws SessionExpireException {
		List<PatFare> fares = new ArrayList<PatFare>();
		if (patFareRequest.getTriPartiteNo() != null) {
			logger.info("PAT：三方报价");
			String pat = client.getPAT("A#C" + patFareRequest.getTriPartiteNo()).getObject();
			PatFare fare = PatFareParser.parseAdt(pat);
			if (fare != null)
				fares.add(fare);
		} else {
			logger.info("PAT：非三方报价");
			if (patFareRequest.getPassengerTypes().contains(PassengerType.GOV)) {
				String pat = client.getPAT("A#CGP/CC").getObject();
				PatFare fare = PatFareParser.parseGov(pat);
				if (fare != null) {
					//此处会获取非政府报价，政府报价的CabinNumber结尾为GP，所以需要过滤下
					List<PatFareItem> govFareItems = new ArrayList<PatFareItem>();
					for (PatFareItem fareItem:fare.getFareItems()) {
						if (fareItem.getFareBasis().contains("GP")) {
							govFareItems.add(fareItem);
						}
					}
					if (govFareItems.size() != 0) {
						fare.setFareItems(govFareItems);
						fares.add(fare);
					}
				}
			}
			if (patFareRequest.getPassengerTypes().contains(PassengerType.ADT)) {
				String pat = client.getPAT("A").getObject();
				PatFare fare = PatFareParser.parseAdt(pat);
				if (fare != null)
					fares.add(fare);
			}
			if (patFareRequest.getPassengerTypes().contains(PassengerType.CHD)) {
				String pat = client.getPAT("A*CH").getObject();
				PatFare fare = PatFareParser.parseChd(pat);
				if (fare != null)
					fares.add(fare);
			}
			if (patFareRequest.getPassengerTypes().contains(PassengerType.INF)) {
				String patInf = client.getPAT("A*IN").getObject();
				PatFare fareInf = PatFareParser.parseInf(patInf);
				if (fareInf != null)
					fares.add(fareInf);
			}
		}
		return fares;
	}
	
	private List<PatFare> generalPatByUfis(PatFareRequest patFareRequest, EtermUfisClient client) throws UfisException {
		List<PatFare> fares = new ArrayList<PatFare>();
		if (patFareRequest.getTriPartiteNo() != null) {
			logger.info("PAT：三方报价");
			String pat = client.execCmd("PAT:A#C" + patFareRequest.getTriPartiteNo());
			PatFare fare = PatFareParser.parseAdt(pat);
			if (fare != null)
				fares.add(fare);
		} else {
			logger.info("PAT：非三方报价");
			if (patFareRequest.getPassengerTypes().contains(PassengerType.GOV)) {
				String pat = client.execCmd("PAT:A#CGP/CC");
				PatFare fare = PatFareParser.parseGov(pat);
				if (fare != null) {
					//此处会获取非政府报价，政府报价的CabinNumber结尾为GP，所以需要过滤下
					List<PatFareItem> govFareItems = new ArrayList<PatFareItem>();
					for (PatFareItem fareItem:fare.getFareItems()) {
						if (fareItem.getFareBasis().contains("GP")) {
							govFareItems.add(fareItem);
						}
					}
					if (govFareItems.size() != 0) {
						fare.setFareItems(govFareItems);
						fares.add(fare);
					}
				}
			}
			if (patFareRequest.getPassengerTypes().contains(PassengerType.ADT)) {
				String pat = client.execCmd("PAT:A");
				PatFare fare = PatFareParser.parseAdt(pat);
				if (fare != null)
					fares.add(fare);
			}
			if (patFareRequest.getPassengerTypes().contains(PassengerType.CHD)) {
				String pat = client.execCmd("PAT:A*CH");
				PatFare fare = PatFareParser.parseChd(pat);
				if (fare != null)
					fares.add(fare);
			}
			if (patFareRequest.getPassengerTypes().contains(PassengerType.INF)) {
				String patInf = client.execCmd("PAT:A*IN");
				PatFare fareInf = PatFareParser.parseInf(patInf);
				if (fareInf != null)
					fares.add(fareInf);
			}
		}
		return fares;
	}

}
