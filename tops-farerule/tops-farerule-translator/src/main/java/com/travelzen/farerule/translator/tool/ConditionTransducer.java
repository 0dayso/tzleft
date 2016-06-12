/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.translator.tool;

import com.travelzen.farerule.rule.TimeTypeEnum;
import com.travelzen.farerule.translator.consts.DateConst;
import com.travelzen.farerule.translator.consts.LocationConst;

public class ConditionTransducer {
	
	public static String stayTimeTypeToString(TimeTypeEnum stayTimeType) {
		return DateConst.stayTimeTypeMap.get(stayTimeType);
	}
	
	public static String localtionToString(String location) {
		if (LocationConst.originMap.containsKey(location))
			return LocationConst.originMap.get(location);
		return location;
	}
}
