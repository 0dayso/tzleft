package com.travelzen.etermface.service.entity;

import java.util.List;

import com.google.common.collect.Lists;

public class TkneInfo {

    // PVGATL 296 Y01DEC 0064387157883/1/P1

    public String flt6code;
    public String fltNum;
    public String classCode;
    public String departDate;
    public String tktNum;
    public List<String> fltsegIdxs = Lists.newArrayList();
    public int psgIdx;
    public String tktType;
    public boolean isAccurate = false;
    
	@Override
	public String toString() {
		return "TkneInfo [flt6code=" + flt6code + ", fltNum=" + fltNum
				+ ", classCode=" + classCode + ", departDate=" + departDate
				+ ", tktNum=" + tktNum + ", fltsegIdxs=" + fltsegIdxs
				+ ", psgIdx=" + psgIdx + ", tktType=" + tktType
				+ ", isAccurate=" + isAccurate + "]";
	}

}
