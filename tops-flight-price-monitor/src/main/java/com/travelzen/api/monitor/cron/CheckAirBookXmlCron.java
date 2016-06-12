package com.travelzen.api.monitor.cron;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.travelzen.flight.ibe.IbeClient;
import com.travelzen.flight.ibe.pojo.IbeResponse;
import com.travelzen.flight.ibe.service.BookService;
import com.travelzen.api.monitor.consts.ErrorConst;
import com.travelzen.api.monitor.pojo.IbeApiStates;

@Deprecated
@Component("checkAirBookXmlCron")
public class CheckAirBookXmlCron {

private static Logger logger = LoggerFactory.getLogger(CheckAirBookXmlCron.class);
	
	private static final BookService service = IbeClient.getInstance().getBookService();

	public void exe() throws InterruptedException {
		logger.info("测试自动预订接口AirBook");
		String postValue = 
				"<OTA_AirBookRQ>"
				+ ""
				+ "</OTA_AirBookRQ>";
		IbeResponse ibeResponse = service.getAirBookXml(UUID.randomUUID().toString(), postValue);
		for (int i=0; i<3; i++) {
			if (ibeResponse.isStatus())
				break;
			ibeResponse = service.getAirBookXml(UUID.randomUUID().toString(), postValue);
			Thread.sleep(1000);
		}
		IbeApiStates.airBookXmlState.setSuccess(ibeResponse.isStatus());
		IbeApiStates.airBookXmlState.setResult(ibeResponse.getValue());
		if (!ibeResponse.isStatus())
			IbeApiStates.airBookXmlState.setError(ErrorConst.getError(ibeResponse.getValue()));
		System.out.println(IbeApiStates.airBookXmlState);
	}
	
	public static void main(String[] args) {
		IbeApiStates.init();
		CheckAirBookXmlCron cron = new CheckAirBookXmlCron();
		try {
			cron.exe();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
