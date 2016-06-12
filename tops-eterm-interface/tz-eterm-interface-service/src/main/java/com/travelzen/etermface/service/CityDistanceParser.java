package com.travelzen.etermface.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.common.ufis.util.UfisException;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.entity.CityDistance;
import com.travelzen.framework.core.util.TZUtil;

/**
 * @author hongqiang.mao
 * 
 * @date 2013-6-13 下午8:35:55
 * 
 * @description
 */
public class CityDistanceParser{

	private static Logger logger = LoggerFactory.getLogger(CityDistanceParser.class);

	/**
	 * 找城市距离的起始位置
	 */
	private final static String description = "CTY  TPM   CUM";

	public static List<CityDistance> getDistance(String pStr, String cities) {
		List<CityDistance> lvList = new ArrayList<CityDistance>();
		List<String> cityList = new ArrayList<String>();

		String[] lvLines = pStr.replaceAll("\r", "\n").split("\n");

		String[] lvSplits = null;
		String lvFromCity = null;
		String lvToCity = null;
		String lvDirection = null;
		Integer tpmDistance = 0;
		Integer mpmDistance = 0;
		CityDistance lvCityDistance = null;
		boolean first = true;
		int index = 0;

		if (null != cities && 0 == cities.trim().length() % 3) {
			cities = cities.trim();
			for (index = 0; index < cities.length();) {
				cityList.add(cities.substring(index, index + 3));
				index = index + 3;
			}
		}

		for (index = 0; index < lvLines.length - 1; index++) {
			if (lvLines[index].trim().startsWith(description)) {
				break;
			}
		}

		int beginIndex = index + 1;
		index = 1;

		if (lvLines.length >= 4) {
			for (int lineNum = beginIndex; lineNum < lvLines.length - 1; lineNum++) {
				String lineStr = lvLines[lineNum].replace("*", "");
				lvSplits = lineStr.trim().split(" +");
				if (first) {
					lvFromCity = lvSplits[0];
					first = false;
				} else {
					if (lvSplits.length < 3) {
						break;
					}
					lvToCity = lvSplits[0];
					if (lvSplits.length == 10) {
						lvDirection = lvSplits[9];
					}

					lvCityDistance = new CityDistance();
					if (index < cityList.size()) {
						lvCityDistance.setFromCity(cityList.get(index - 1));
						lvCityDistance.setToCity(cityList.get(index));
					} else {
						lvCityDistance.setFromCity(lvFromCity);
						lvCityDistance.setToCity(lvToCity);
					}

					lvCityDistance.setGi(lvDirection);
					try {
						tpmDistance = Integer.parseInt(lvSplits[1]);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}

					if (lvSplits.length > 3) {
						try {
							mpmDistance = Integer.parseInt(lvSplits[3]);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}

					lvCityDistance.setTpm(tpmDistance);
					lvCityDistance.setMpm(mpmDistance);
					lvList.add(lvCityDistance);
					lvFromCity = lvToCity;
					lvDirection = null;
					tpmDistance = 0;
					mpmDistance = 0;
					index++;
				}
			}
		}

		return lvList;
	}

	public static List<CityDistance> parserDistance(EtermWebClient client, String cities) throws SessionExpireException {

		String str = client.getXS_FSM(cities);
		List<CityDistance> resultList = getDistance(str, cities);

		return resultList;
	}

	public static List<CityDistance> parserDistance(String cities) throws SessionExpireException {
		List<CityDistance> resultList = null;
		EtermWebClient lvEtermWebClient = new EtermWebClient();
		try {
			lvEtermWebClient.connect();
			String str = lvEtermWebClient.getXS_FSM(cities);
			resultList = getDistance(str, cities);
		} catch (Exception e) {
			logger.error("parserDistance err:{}", TZUtil.stringifyException(e));
		} finally {
			lvEtermWebClient.close();
		}
		return resultList;
	}
	
	public static List<CityDistance> parserDistanceByUfis(EtermUfisClient client, String cities) throws UfisException {

		String str = client.execCmd("XS FSM " + cities, true);
		List<CityDistance> resultList = getDistance(str, cities);

		return resultList;
	}

	public static List<CityDistance> parserDistanceByUfis(String cities) throws UfisException {
		List<CityDistance> resultList = null;
		EtermUfisClient lvEtermWebClient = null;
		try {
			lvEtermWebClient = new EtermUfisClient();
			String str = lvEtermWebClient.execCmd("XS FSM " + cities, true);
			resultList = getDistance(str, cities);
		} catch (Exception e) {
			logger.error("parserDistance err:{}", TZUtil.stringifyException(e));
		} finally {
			lvEtermWebClient.close();
		}
		return resultList;
	}
}
