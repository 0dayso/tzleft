package com.travelzen.etermface.service.util;

import java.util.Arrays;
import java.util.Set;

import com.google.common.collect.Sets;

public class PnrPassengerUtil {

	static Set<String> ptypeSet = Sets.newHashSet();

	static Set<String> childPtypeSet = Sets.newHashSet();
	
	static Set<String> infPtypeSet = Sets.newHashSet();
	
	static {
		ptypeSet.addAll(Arrays.asList("MSTR", "MR", "MS", "MISS","CHD","SD"));

		childPtypeSet.addAll(Arrays.asList("MSTR", "MISS","CHD"));
		infPtypeSet.addAll(Arrays.asList("INF"));
	}

	public static boolean isPtype(String str) {
		return ptypeSet.contains(str);

	}
	
	public static String getPsgType(String str){
		if(infPtypeSet.contains(str)){
			return "INF";
		}
		if(childPtypeSet.contains(str)){
			return "CHD";
		}
		
		return "ADT";
		
	}
	
	public static boolean isChildPtype(String str) {
		return childPtypeSet.contains(str);

	}

	public static void main(String[] args) {
		// System.out.println(beforeDate(now(), 1));
		// 12NOV12

		// System.out.println(ibeDateStrToDateStr("12APR12"));
		// System.out.println(dateStrToIbeDateStr("2012-10-12"));
		// System.out.println(afterDate("31DEC12", 1));
		// System.out.println(dateStrToIbeMonthDateStr("2012-12-14"));
		// System.err.println(getDateAdd("2012-11-12", 1));
		// System.out.println(defaultTimeFormat);
	}

}
