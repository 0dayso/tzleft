package com.travelzen.rosetta.eterm.parser;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.rosetta.eterm.common.pojo.EtermXsFscResponse;

/**
 * Eterm XS FSC　指令解析类
 * <p>
 * @author yiming.yan
 * @Date Mar 2, 2016
 */
public class EtermXsFscParser {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EtermXsFscParser.class);
	
	private static final Pattern PATTERN_RATE = Pattern.compile("=(\\d+\\.\\d+)CNY");
	
	public static EtermXsFscResponse parse(String currency, String text) {
		LOGGER.info("Eterm XS FSC　解析请求 文本：{}", text);
		String rateLine = text.split("[\n\r]+")[1].trim();
		if (rateLine.startsWith("UNKNOWN CURRENCY ")) {
			LOGGER.info("Eterm XS FSC　请求货币异常：{}", rateLine);
			return new EtermXsFscResponse(false, "Eterm XS FSC　请求货币异常：" + rateLine);
		}
		String rate = null;
		Matcher matcher = PATTERN_RATE.matcher(rateLine);
		if (matcher.find()) {
			rate = matcher.group(1);
		} else {
			LOGGER.info("Eterm XS FSC　解析文本异常：{}", rateLine);
			return new EtermXsFscResponse(false, "Eterm XS FSC　解析文本异常：" + rateLine);
		}
		EtermXsFscResponse response = new EtermXsFscResponse();
		response.setSuccess(true);
		response.setRate2CNY(Double.parseDouble(rate));
		response.setTimeStamp(DateTime.now().toString("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH));
		LOGGER.info("Eterm XS FSC　解析结果：{}", response);
		return response;
	}
	
	public static void main(String[] args) {
		String string = "FSC 1/HKD/CNY/S/24MAR16                                                         \n"
				+ "RATE BSR 1HKD=0.837085CNY                                                       \n"
				+ "CNY            0.8 TRUNCATED                                                    \n"
				+ "CNY             10 ROUNDED UP TO NEXT 10.00 - FARES                             \n"
				+ "CNY              1 ROUNDED UP TO NEXT 1.00 - TAXES/OTHERS                       \n"
				+ "RFSONLN/1E /EFEP_10/FCC=D/PAGE 1/1 ";
		System.out.println(parse("HKD", string));
	}

}
