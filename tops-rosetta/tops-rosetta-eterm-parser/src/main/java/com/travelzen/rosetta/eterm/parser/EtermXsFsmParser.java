package com.travelzen.rosetta.eterm.parser;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.rosetta.eterm.common.pojo.EtermXsFsmResponse;
import com.travelzen.rosetta.eterm.common.pojo.EtermXsFsmResponse.CityDistance;

/**
 * Eterm XS FSM　指令解析类
 * <p>
 * @author yiming.yan
 * @Date Mar 2, 2016
 */
public class EtermXsFsmParser {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EtermXsFsmParser.class);
	
	public static EtermXsFsmResponse parse(int cityNum, String text) {
		LOGGER.info("Eterm XS FSM　解析请求 文本：{}", text);
		String[] lines = text.split("[\n\r]+");
		if (!lines[1].trim().startsWith("CTY")) {
			LOGGER.info("Eterm XS FSM　解析文本异常：{}", lines[1].trim());
			return new EtermXsFsmResponse(false, "Eterm XS FSM　解析文本异常：" + lines[1].trim());
		}
		List<CityDistance> distances = new ArrayList<CityDistance>();
		for (int i = 3; i < 2 + cityNum; i++) {
			CityDistance distance = new CityDistance();
			distance.setFromCity(lines[i-1].trim().substring(0, 3));
			String[] tokens = lines[i].trim().split(" +");
			distance.setToCity(tokens[0]);
			distance.setTpm(Integer.parseInt(tokens[1]));
			distance.setCum(Integer.parseInt(tokens[2]));
			// mpm有*****或者为空
//			distance.setMpm(Integer.parseInt(tokens[3]));
			distances.add(distance);
		}
		if (distances.size() == 0) {
			LOGGER.info("Eterm XS FSM　解析异常！");
			return new EtermXsFsmResponse(false, "Eterm XS FSM　解析异常！");
		}
		EtermXsFsmResponse response = new EtermXsFsmResponse();
		response.setSuccess(true);
		response.setCityDistances(distances);
		LOGGER.info("Eterm XS FSM　解析结果：{}", response);
		return response;
	}
	
	public static void main(String[] args) {
		String string = "FSM   SHA .YY. HKG .YY. LON  \n"
				+ " CTY  TPM   CUM   MPM  LVL  (HGL  (LWL   25M   XTRA  EXC  GI  \n"
				+ " SHA  \n"
				+ " HKG  773   773   927   0M   154     0  1158      0       EH  \n"
				+ " LON 5965  6738  8167   0M  1429     0 10208      0       EH  \n"
				+ "*ALTERNATIVE GLOBAL ROUTE AT EXISTS FOR SHA-DFW \n"
				+ "RFSONLN/1E /EFEP_37/FCC=D/PAGE 1/1";
		System.out.println(parse(3, string));
	}

}
