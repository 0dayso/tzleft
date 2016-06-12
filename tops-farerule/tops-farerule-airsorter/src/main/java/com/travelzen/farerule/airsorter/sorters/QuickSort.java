/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.airsorter.sorters;

import java.util.*;

//import com.travelzen.tops.flight.cache.AirPortCache;

public class QuickSort {

	Map<String, Integer> airMap;

	public Map<String, Integer> sort(Map<String, Integer> map) {
		airMap = map;
		Object[] keyArray = map.keySet().toArray();
		quickSort(keyArray, 0, keyArray.length-1);
		
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (int i=0; i<keyArray.length; i++) {
			String airline = keyArray[i].toString();
//			if (AirPortCache.isDomestic(airline.substring(0,3)) && AirPortCache.isDomestic(airline.substring(4,7)))
//				continue;
			sortedMap.put(airline, map.get(airline));
		}
		return sortedMap;
	}
	
	private void quickSort(Object[] keyArray, int i, int j) {	
		if (i < j) {
			int pivot = partition(keyArray, i, j);
			quickSort(keyArray, i, pivot-1);
			quickSort(keyArray, pivot+1, j);
		}
	}
	
	private int partition(Object[] keyArray, int low, int high) {
		int i = low, j = high;
		Object x = keyArray[i];
		Integer x_value = airMap.get(x);
		while (i<j) {
			while (i<j && airMap.get(keyArray[j])<=x_value) 
				j--;
			if (i<j) {
				keyArray[i] = keyArray[j];
				i++;
			}
			while (i<j && airMap.get(keyArray[i])>=x_value)
				i++;
			if (i<j) {
				keyArray[j] = keyArray[i];
				j--;
			}
		}
		// After while, i==j
		keyArray[i] = x;
		return i;
	}
}
