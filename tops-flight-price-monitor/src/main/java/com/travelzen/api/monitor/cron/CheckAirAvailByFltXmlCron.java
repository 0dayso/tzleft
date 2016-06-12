package com.travelzen.api.monitor.cron;

import java.util.UUID;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.travelzen.flight.ibe.IbeClient;
import com.travelzen.flight.ibe.pojo.IbeResponse;
import com.travelzen.flight.ibe.service.AirService;
import com.travelzen.api.monitor.consts.ErrorConst;
import com.travelzen.api.monitor.pojo.IbeApiStates;
import com.travelzen.api.monitor.util.DateUtil;

@Component("checkAirAvailByFltXmlCron")
public class CheckAirAvailByFltXmlCron {

	private static Logger logger = LoggerFactory.getLogger(CheckAirAvailByFltXmlCron.class);
	
	private static final AirService service = IbeClient.getInstance().getAirService();

	public void exe() throws InterruptedException {
		logger.info("测试根据航班号查询航班接口AirAvailByFlt");
		String postValue = 
				"<OTA_AirAvailRQ xmlns=\"http://espeed.travelsky.com\">"
				+ "<POS>"
				+ "<Source PseudoCityCode=\"SHA255\"/>"
				+ "</POS>"
				+ "<OriginDestinationInformation>"
					+ "<DepartureDateTime>" + DateUtil.getAvailDate("yyyy-MM-dd") + "</DepartureDateTime>"
				+ "</OriginDestinationInformation>"
				+ "<SpecificFlightInfo>"
					+ "<FlightNumber>701</FlightNumber>"
					+ "<Airline Code=\"MU\"/>"
				+ "</SpecificFlightInfo>"
				+ "</OTA_AirAvailRQ>";
		IbeResponse ibeResponse = service.getAirAvailByFltXml(UUID.randomUUID().toString(), postValue);
		for (int i=0; i<3; i++) {
			if (ibeResponse.isStatus())
				break;
			ibeResponse = service.getAirAvailByFltXml(UUID.randomUUID().toString(), postValue);
			Thread.sleep(1000);
		}
		if (!ibeResponse.isStatus()) {
			IbeApiStates.airAvailByFltXmlState.setSuccess(false);
			IbeApiStates.airAvailByFltXmlState.setError(ibeResponse.getValue());
		} else {
			if (ibeResponse.getValue() == null) {
				logger.info("AirService返回内容为空!");
				return;
			}
			Pair<Boolean, String> errorPair = ErrorConst.getErrorText(ibeResponse.getValue());
			if (errorPair.getValue0())
				IbeApiStates.airAvailByFltXmlState.setSuccess(true);
			else {
				IbeApiStates.airAvailByFltXmlState.setSuccess(false);
				IbeApiStates.airAvailByFltXmlState.setError(ErrorConst.getError(errorPair.getValue1()));
			}
		}
		IbeApiStates.airAvailByFltXmlState.setResult(ibeResponse.getValue());
		logger.info("AirAvailByFlt接口测试结果, ", IbeApiStates.airAvailByFltXmlState);
		System.out.println(IbeApiStates.airAvailByFltXmlState);
	}
	
	public static void main(String[] args) {
		IbeApiStates.init();
		CheckAirAvailByFltXmlCron cron = new CheckAirAvailByFltXmlCron();
		try {
			cron.exe();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
