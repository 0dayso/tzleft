/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.airsorter.sorters;

import java.util.*;

//import com.travelzen.tops.flight.cache.AirPortCache;

public class InsertSort {

	public Map<String, Integer> sort(Map<String, Integer> map) {
		Object[] keyArray = map.keySet().toArray();
		int i, j, k;
		for (i=1; i<keyArray.length; i++) {
			for (j=i-1; j>=0; j--) {
				if (map.get(keyArray[j]) > map.get(keyArray[i]))
					break;
			}
			Object temp = keyArray[i];
			for (k=i-1; k>j; k--) {
				keyArray[k+1] = keyArray[k];
			}
			keyArray[j+1] = temp;
		}
		
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (int n=0; n<keyArray.length; n++) {
			String airline = keyArray[n].toString();
//			if (AirPortCache.isDomestic(airline.substring(0,3)) && AirPortCache.isDomestic(airline.substring(4,7)))
//				continue;
			sortedMap.put(airline, map.get(airline));
		}
		return sortedMap;
	}
}
