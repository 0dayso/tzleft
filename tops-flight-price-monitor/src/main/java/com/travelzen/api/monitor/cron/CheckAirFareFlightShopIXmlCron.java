package com.travelzen.api.monitor.cron;

import java.util.UUID;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.travelzen.flight.ibe.IbeClient;
import com.travelzen.flight.ibe.pojo.IbeResponse;
import com.travelzen.flight.ibe.service.FareService;
import com.travelzen.api.monitor.consts.ErrorConst;
import com.travelzen.api.monitor.pojo.IbeApiStates;
import com.travelzen.api.monitor.util.DateUtil;

@Component("checkAirFareFlightShopIXmlCron")
public class CheckAirFareFlightShopIXmlCron {

private static Logger logger = LoggerFactory.getLogger(CheckAirFareFlightShopIXmlCron.class);
	
	private static final FareService service = IbeClient.getInstance().getFareService();

	public void exe() throws InterruptedException {
		logger.info("测试国际运价Shopping接口AirFareFlightShop_I");
		String postValue = 
				"<TSK_AirfareFlightShop>"
				+ "<TSK_AirfareFlightShopRQ>"
				+ "<OTA_AirLowFareSearchRQ>"
					+ "<POS>"
						+ "<PseudoCityCode>SHA255</PseudoCityCode>"
						+ "<AirportCode>SHA</AirportCode>"
						+ "<ChannelID>1E</ChannelID>"
					+ "</POS>"
					+ "<OriginDestinationInformation>"
						+ "<DepartureDate>"
							+ "<Date>" + DateUtil.getAvailDate("yyyy-MM-dd") + "</Date>"
						+ "</DepartureDate>"
						+ "<OriginLocationCode>PVG</OriginLocationCode>"
						+ "<DestinationLocationCode>HKG</DestinationLocationCode>"
					+ "</OriginDestinationInformation>"
					+ "<TravelPreferences />"
					+ "<TravelerInfoSummary>"
						+ "<AirTravelerAvail>"
							+ "<PassengerTypeQuantity>"
								+ "<Code>ADT</Code>"
								+ "<Quantity>1</Quantity>"
							+ "</PassengerTypeQuantity>"
						+ "</AirTravelerAvail>"
					+ "</TravelerInfoSummary>"
				+ "</OTA_AirLowFareSearchRQ>"
				+ "<AdditionalShopRQData>"
					+ "<IncludeFlightAvailability>true</IncludeFlightAvailability>"
				+ "</AdditionalShopRQData>"
				+ "</TSK_AirfareFlightShopRQ>"
				+ "</TSK_AirfareFlightShop>";
		IbeResponse ibeResponse = service.getAirFareFlightShopIXml(UUID.randomUUID().toString(), postValue);
		for (int i=0; i<3; i++) {
			if (ibeResponse.isStatus())
				break;
			ibeResponse = service.getAirFareFlightShopIXml(UUID.randomUUID().toString(), postValue);
			Thread.sleep(1000);
		}
		if (!ibeResponse.isStatus()) {
			IbeApiStates.airFareFlightShopIXmlState.setSuccess(false);
			IbeApiStates.airFareFlightShopIXmlState.setError(ibeResponse.getValue());
		} else {
			if (ibeResponse.getValue() == null) {
				logger.info("FareService返回内容为空!");
				return;
			}
			Pair<Boolean, String> errorPair = ErrorConst.getErrorText(ibeResponse.getValue());
			if (errorPair.getValue0())
				IbeApiStates.airFareFlightShopIXmlState.setSuccess(true);
			else {
				IbeApiStates.airFareFlightShopIXmlState.setSuccess(false);
				IbeApiStates.airFareFlightShopIXmlState.setError(ErrorConst.getError(errorPair.getValue1()));
			}
		}
		IbeApiStates.airFareFlightShopIXmlState.setResult(ibeResponse.getValue());
		logger.info("AirFareFlightShop_I接口测试结果, ", IbeApiStates.airFareFlightShopIXmlState);
		System.out.println(IbeApiStates.airFareFlightShopIXmlState);
	}
	
	public static void main(String[] args) {
		IbeApiStates.init();
		CheckAirFareFlightShopIXmlCron cron = new CheckAirFareFlightShopIXmlCron();
		try {
			cron.exe();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
