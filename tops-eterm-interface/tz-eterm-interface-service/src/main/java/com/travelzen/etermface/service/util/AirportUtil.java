package com.travelzen.etermface.service.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.travelzen.framework.logger.core.ri.RequestIdentityLogger;

/**
 * 机场信息查询工具类
 * <p>
 * @author yiming.yan
 * @Date Dec 25, 2015
 */
public class AirportUtil {
	
	private final static Logger LOGGER = RequestIdentityLogger.getLogger(AirportUtil.class);
	
	private static Map<String, String> code_ch_map = new HashMap<String, String>();
	private static Map<String, String> en_ch_map = new HashMap<String, String>();
	private static Map<String, String> en_code_map = new HashMap<String, String>();
	
	static {
		try {
			loadData();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private static void loadData() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				AirportUtil.class.getClassLoader().getResourceAsStream("data/airport.txt")));
		String line = null;
		while ((line = reader.readLine()) != null) {
			String[] tokens = line.split(",");
			code_ch_map.put(tokens[2], tokens[1]);
			en_ch_map.put(tokens[0], tokens[1]);
			en_code_map.put(tokens[0], tokens[2]);
		}
		reader.close();
	}
	
	public static String getChByCode(String code) {
		return code_ch_map.get(code);
	}
	
	public static String getChByEn(String en) {
		return en_ch_map.get(en);
	}
	
	public static String getCodeByEn(String en) {
		return en_code_map.get(en);
	}
	
	public static void main(String[] args) {
		System.out.println(getChByCode("TCZ"));
		System.out.println(getChByEn("TENGCHONG"));
	}

}
