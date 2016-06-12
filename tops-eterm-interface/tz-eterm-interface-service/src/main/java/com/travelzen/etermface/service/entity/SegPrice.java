package com.travelzen.etermface.service.entity;

import java.util.List;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.travelzen.etermface.service.constant.PnrParserConstant;

@XStreamAlias("SegPrice")
public class SegPrice {

	public double price;
	public List<Double> qvalue = Lists.newArrayList() ;
	public List<Double> svalue = Lists.newArrayList() ;
	public String segtype = PnrParserConstant.CONST_SEGTYPE_FLT;
	public String pricetype = PnrParserConstant.CONST_PRICETYPE_N;
	
	public List<String> cityCodeList = Lists.newArrayList();

	public void cleanAddAll(List<String> list) {
		cityCodeList.clear();
		cityCodeList.addAll(list);
	}

	@Override
	public String toString() {
		return "SegPrice [price=" + price + ", qvalue=" + qvalue + ", svalue=" + svalue + ", segtype=" + segtype + ", pricetype=" + pricetype
				+ ", cityCodeList=" + cityCodeList + "]";
	}
	

	@Override
	public SegPrice clone() {

		SegPrice sp = new SegPrice();
		sp.price = this.price ;
		sp.segtype = this.segtype;
		sp.qvalue.addAll(this.qvalue);
		sp.svalue.addAll(this.svalue);
		sp.cityCodeList.addAll(this.cityCodeList);
		sp.segtype = this.segtype;
		sp.pricetype = this.pricetype;
		return sp;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
