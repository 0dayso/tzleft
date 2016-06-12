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

@Component("checkAirAvailAXmlCron")
public class CheckAirAvailAXmlCron {

	private static Logger logger = LoggerFactory.getLogger(CheckAirAvailAXmlCron.class);
	
	private static final AirService service = IbeClient.getInstance().getAirService();

	public void exe() throws InterruptedException {
		logger.info("测试航班查询接口AirAvail_A");
		String postValue = 
				"<OTA_AVE_RQ>"
				+ "<Header>*GAPP</Header>"
				+ "<InFormat>1</InFormat>"
				+ "<OutFormat>1</OutFormat>"
				+ "<Office>SHA255</Office>"
				+ "<Command>"
					+ "<Statement>"
						+ "<SysType>C</SysType>"
						+ "<PID>23329</PID"
						+ "><WA>B</WA>"
						+ "<StatementName>AV</StatementName>"
						+ "<GDSName>1E</GDSName>"
						+ "<Office>SHA255</Office>"
						+ "<SerialNo>23329_1368510979</SerialNo>"
						+ "<format1>"
							+ "<origin>SHA</origin>"
							+ "<dest>HKG</dest>"
							+ "<date>" + DateUtil.getAvailDate("ddMMMyy") + "</date>"
							+ "<seats>1</seats>"
							+ "<gds>1E</gds>"
						+ "</format1>"
					+ "</Statement>"
				+ "</Command>"
				+ "</OTA_AVE_RQ>";
		IbeResponse ibeResponse = service.getAirAvailAXml(UUID.randomUUID().toString(), postValue);
		for (int i=0; i<3; i++) {
			if (ibeResponse.isStatus())
				break;
			ibeResponse = service.getAirAvailAXml(UUID.randomUUID().toString(), postValue);
			Thread.sleep(1000);
		}
		if (!ibeResponse.isStatus()) {
			IbeApiStates.airAvailAXmlState.setSuccess(false);
			IbeApiStates.airAvailAXmlState.setError(ibeResponse.getValue());
		} else {
			if (ibeResponse.getValue() == null) {
				logger.info("AirService返回内容为空!");
				return;
			}
			Pair<Boolean, String> errorPair = ErrorConst.getErrorText(ibeResponse.getValue());
			if (errorPair.getValue0())
				IbeApiStates.airAvailAXmlState.setSuccess(true);
			else {
				IbeApiStates.airAvailAXmlState.setSuccess(false);
				IbeApiStates.airAvailAXmlState.setError(ErrorConst.getError(errorPair.getValue1()));
			}
		}
		IbeApiStates.airAvailAXmlState.setResult(ibeResponse.getValue());
		logger.info("AirAvail_A接口测试结果, ", IbeApiStates.airAvailAXmlState);
		System.out.println(IbeApiStates.airAvailAXmlState);
	}
	
	public static void main(String[] args) {
		IbeApiStates.init();
		CheckAirAvailAXmlCron cron = new CheckAirAvailAXmlCron();
		try {
			cron.exe();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
