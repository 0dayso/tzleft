package com.travelzen.etermface.service.segprice;

import it.unimi.dsi.lang.MutableString;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class SegpriceLexInfo {

	public int tkStartIdx = 0;
	public int tkEndIdx = 0;
	public double tkImgNumberValue = 0;
	public SPTokenType targetType;

	public boolean accept(MutableString tkImg) {
		return true;
	}

	public MutableString buildToken(MutableString tkImg) {
		return tkImg;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
