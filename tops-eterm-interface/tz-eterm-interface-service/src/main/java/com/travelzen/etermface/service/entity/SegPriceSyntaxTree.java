package com.travelzen.etermface.service.entity;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.annotations.XStreamAlias;


@XStreamAlias("SegPriceInfo")
public class SegPriceSyntaxTree {

	public String psgType = "";

	public double nucValue = 0;
	public double roeValue = 0;
	public double rateValue = 0;
	public double qmount = 0 ;
	public int qvalue = 0;
	public List<Double> dvalue =  Lists.newArrayList();;
	
	public String nucType = "NUC";

	transient public List<String> curCityCodeList = Lists.newArrayList();
	transient public SegPrice curSegmentsPrice = new SegPrice();

	public List<SegPrice> segmentsPrices = Lists.newArrayList();

	@Override
	public String toString() {

		String str = "SegPriceSyntaxTree [nucValue=" + nucValue + ", roeValue=" + roeValue + ",nucType="+nucType+ ",dvalue="+dvalue.toString()+"\n" ;
		str += StringUtils.join(segmentsPrices, "\n") + "\n";
		return str;

	}

	public void cleanAdd(List<String> CityCodeList) {
		curCityCodeList.clear();
		curCityCodeList.addAll(CityCodeList);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
