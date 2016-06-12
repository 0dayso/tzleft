/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.airsorter.sorters;

import java.util.*;

//import com.travelzen.tops.flight.cache.AirPortCache;

public class BubbleSort {

	public Map<String, Integer> sort(Map<String, Integer> map) {
		Object[] keyArray = map.keySet().toArray();
		for (int i=0; i<keyArray.length; i++) {
			for (int j=0; j<keyArray.length-i-1; j++) {
				String code1 = keyArray[j].toString();
				String code2 = keyArray[j+1].toString();
				if (map.get(code1) < map.get(code2)) {
					keyArray[j] = code2;
					keyArray[j+1] = code1;
				}
			}
		}
		
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (int i=0; i<keyArray.length; i++) {
			String airline = keyArray[i].toString();
//			if (AirPortCache.isDomestic(airline.substring(0,3)) && AirPortCache.isDomestic(airline.substring(4,7)))
//				continue;
			sortedMap.put(airline, map.get(airline));
		}
		return sortedMap;
	}
}
