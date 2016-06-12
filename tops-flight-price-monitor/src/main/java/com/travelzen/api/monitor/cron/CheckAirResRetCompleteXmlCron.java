package com.travelzen.api.monitor.cron;

import java.util.UUID;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.travelzen.flight.ibe.IbeClient;
import com.travelzen.flight.ibe.pojo.IbeResponse;
import com.travelzen.flight.ibe.service.BookService;
import com.travelzen.api.monitor.consts.ErrorConst;
import com.travelzen.api.monitor.pojo.IbeApiStates;

@Component("checkAirResRetCompleteXmlCron")
public class CheckAirResRetCompleteXmlCron {

private static Logger logger = LoggerFactory.getLogger(CheckAirResRetCompleteXmlCron.class);
	
	private static final BookService service = IbeClient.getInstance().getBookService();

	public void exe() throws InterruptedException {
		logger.info("测试PNR原文信息提取接口AirResRetComplete");
		String postValue = 
				"<TES_AirResRetCompleteRQ>"
					+ "<POS>"
						+ "<Source PseudoCityCode=\"SHA255\">"
						+ "</Source>"
					+ "</POS>"
					+ "<BookingReferenceID ID=\"KS3RQS\">"
						+ "<CompanyName/>"
					+ "</BookingReferenceID>"
					+ "<RetrieveType Type=\"HistoryRaw\"/>"
				+ "</TES_AirResRetCompleteRQ>";
		IbeResponse ibeResponse = service.getAirResRetCompleteXml(UUID.randomUUID().toString(), postValue);
		for (int i=0; i<3; i++) {
			if (ibeResponse.isStatus())
				break;
			ibeResponse = service.getAirResRetCompleteXml(UUID.randomUUID().toString(), postValue);
			Thread.sleep(1000);
		}
		if (!ibeResponse.isStatus()) {
			IbeApiStates.airResRetCompleteXmlState.setSuccess(false);
			IbeApiStates.airResRetCompleteXmlState.setError(ibeResponse.getValue());
		} else {
			if (ibeResponse.getValue() == null) {
				logger.info("BookService返回内容为空!");
				return;
			}
			Pair<Boolean, String> errorPair = ErrorConst.getErrorText(ibeResponse.getValue());
			if (errorPair.getValue0())
				IbeApiStates.airResRetCompleteXmlState.setSuccess(true);
			else {
				String errorMsg = ErrorConst.getError(errorPair.getValue1());
				if (errorMsg.equals("NOERROR")) {
					IbeApiStates.airResRetCompleteXmlState.setSuccess(true);
				} else {
					IbeApiStates.airResRetCompleteXmlState.setSuccess(false);
					IbeApiStates.airResRetCompleteXmlState.setError(errorMsg);
				}
			}
		}
		IbeApiStates.airResRetCompleteXmlState.setResult(ibeResponse.getValue());
		logger.info("AirResRetComplete接口测试结果, ", IbeApiStates.airResRetCompleteXmlState);
		System.out.println(IbeApiStates.airResRetCompleteXmlState);
	}
	
	public static void main(String[] args) {
		IbeApiStates.init();
		CheckAirResRetCompleteXmlCron cron = new CheckAirResRetCompleteXmlCron();
		try {
			cron.exe();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
