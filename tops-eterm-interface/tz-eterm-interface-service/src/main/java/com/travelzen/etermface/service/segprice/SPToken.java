package com.travelzen.etermface.service.segprice;

import it.unimi.dsi.lang.MutableString;

import org.apache.commons.lang3.builder.ToStringBuilder;

//缺口程是/或者//或者/-

// 这里面的 X/ 什么意思？ 转机
public class SPToken {
	public MutableString tkImg;
	public double tkImgNumberValue = 0;
	public int tkStartIdx;
	public int tkEndIdx;
	public SPTokenType ttype = SPTokenType.WORD;

	// public int movePidxAhead(int stepLength) {
	// if (stepLength < restStr.length()) {
	// restStr = restStr.substring(stepLength);
	// return restStr.length();
	// } else {
	// restStr = new MutableString("");
	// return 0;
	// }
	// }

	public String getTkImgStr() {
		if (tkImg == null) {
			return "";
		} else {
			return tkImg.toString();
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
