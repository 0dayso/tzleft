/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.airsorter;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.travelzen.farerule.airsorter.sorters.*;

public class AirSorter {
	
/*	## Five variables per line:
	id,flight_order_item_id,orig_flight_segment_id,flight_segment_index,air_plain_code,
	is_share_code,flight_no,class_code,department_city_code,department_city_name,
	department_airport_code,department_airport_name,department_terminal,department_date,department_time,
	arrival_city_code,arrival_city_name,arrival_airport_code,arrival_airport_name,arrival_terminal,
	arrival_date,arrival_time,action_code,state_code,valid_not_before,
	valid_not_after,allow_luggage_weight,aircraft_code,stopover,milage,	
	y_fare,basis_fare,create_date,update_date,air_plain_name,
	aircraft_name,share_air_plain_code,share_air_plain_name,class_code_desc,air_construction_fee,
	fuel_surcharge,trip_id,class_rank,discount,class_base_code,
	share_flight_no,child_fuel_surcharge,child_class_code,adult_air_fare,child_air_fare,
	infant_air_fare
*/
	Map<String, Integer> map;
	Map<String, Integer> sortedMap;
	BubbleSort bubbleSort;
	InsertSort insertSort;
	QuickSort quickSort;
	MergeSort mergeSort;
	
	public AirSorter() {
		map = new HashMap<String, Integer>();
		bubbleSort = new BubbleSort();
		insertSort = new InsertSort();
		quickSort = new QuickSort();
		mergeSort = new MergeSort();
	}
	
	public void run() {
		createMap();
//		printMap(map);
		sortedMap = mergeSort.sort(map);
		printMap(sortedMap);
//		saveMap(sortedMap);
	}
	
	private void createMap() {
		String airline = "";
		int count = 0;
		Pattern pattern = Pattern.compile("^(?:[\\w\\W]*?,){10}([\\w\\W]*?),(?:[\\w\\W]*?,){6}([\\w\\W]*?),");
		try {
			BufferedReader reader = new BufferedReader(new FileReader("resources/export.csv"));
			String line = "";
			line = reader.readLine();
			while ((line = reader.readLine()) != null) {
//				System.out.println(line);
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					if (!matcher.group(1).matches("^[A-Z]+$") || !matcher.group(2).matches("^[A-Z]+$"))
						continue;
					airline = matcher.group(1)+"~"+matcher.group(2);
//					System.out.println(airline);
					if (map.containsKey(airline)) {
						count = map.get(airline) + 1;
						map.put(airline, count);
					} else {
						map.put(airline, 1);
					}
				} else {
					System.out.println(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createSortedMap() {
	}
	
	private void printMap(Map<String, Integer> map) {
		System.out.println("MAP:");
		for (Entry<String, Integer> entry:map.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}
	}
	
	private void saveMap(Map<String, Integer> map) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("resources/sorted.txt"));
			StringBuilder sb = new StringBuilder();
			for (Entry<String, Integer> entry:map.entrySet()) {
				sb.append(entry.getKey() + " : " + entry.getValue() + "\n");
			}
			writer.write(sb.toString());
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
