package com.travelzen.farerule.jpecker.merger;

import java.util.ArrayList;
import java.util.List;

import com.travelzen.farerule.rule.MaxStayItem;
import com.travelzen.farerule.rule.MinStayItem;
import com.travelzen.farerule.rule.TimeTypeEnum;

public class StayMerger {
	
	public static List<MinStayItem> minMerge(List<MinStayItem> minStayItemList) {
		TimeTypeEnum stayTimeType = null;
		int stayTimeNum = 0;
		int dayNumSum = 0;
		for (MinStayItem minStayItem:minStayItemList) {
			if (minStayItem.getStayTimeType() == null)
				continue;
			int tmpDayNumSum = 0;
			if (minStayItem.getStayTimeType() == TimeTypeEnum.DAY)
				tmpDayNumSum = minStayItem.getStayTimeNum();
			else if (minStayItem.getStayTimeType() == TimeTypeEnum.MONTH)
				tmpDayNumSum = minStayItem.getStayTimeNum()*30;
			if (tmpDayNumSum > dayNumSum) {
				stayTimeType = minStayItem.getStayTimeType();
				stayTimeNum = minStayItem.getStayTimeNum();
				dayNumSum = tmpDayNumSum;
			}
		}
		List<MinStayItem> finalMinStayItemList = new ArrayList<MinStayItem>();
		if (dayNumSum == 0)
			return finalMinStayItemList;
		MinStayItem minStayItem = new MinStayItem();
		minStayItem.setStayTimeType(stayTimeType);
		minStayItem.setStayTimeNum(stayTimeNum);
		finalMinStayItemList.add(minStayItem);
		return finalMinStayItemList;
	}

	public static List<MaxStayItem> maxMerge(List<MaxStayItem> maxStayItemList) {
		TimeTypeEnum stayTimeType = null;
		int stayTimeNum = 0;
		int dayNumSum = Integer.MAX_VALUE;
		for (MaxStayItem maxStayItem:maxStayItemList) {
			if (maxStayItem.getStayTimeType() == null)
				continue;
			int tmpDayNumSum = Integer.MAX_VALUE;
			if (maxStayItem.getStayTimeType() == TimeTypeEnum.DAY)
				tmpDayNumSum = maxStayItem.getStayTimeNum();
			else if (maxStayItem.getStayTimeType() == TimeTypeEnum.MONTH)
				tmpDayNumSum = maxStayItem.getStayTimeNum()*30;
			if (tmpDayNumSum < dayNumSum) {
				stayTimeType = maxStayItem.getStayTimeType();
				stayTimeNum = maxStayItem.getStayTimeNum();
				dayNumSum = tmpDayNumSum;
			}
		}
		List<MaxStayItem> finalMaxStayItemList = new ArrayList<MaxStayItem>();
		if (dayNumSum == Integer.MAX_VALUE)
			return finalMaxStayItemList;
		MaxStayItem maxStayItem = new MaxStayItem();
		maxStayItem.setStayTimeType(stayTimeType);
		maxStayItem.setStayTimeNum(stayTimeNum);
		finalMaxStayItemList.add(maxStayItem);
		return finalMaxStayItemList;
	}
}
