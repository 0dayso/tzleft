package com.travelzen.etermface.service.entity;

import java.util.List;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.travelzen.etermface.service.constant.PnrParserConstant;

@XStreamAlias("FullDetrResult")
public class DetrResult {

    public String STATUS;
    public String ERRORS;

    @XStreamAlias("FlightSeg")
    public static class FlightSeg {
	public String segType = PnrParserConstant.CONST_SEGTYPE_FLT;
	public String from;
	public String flightNumber;
	public String classCode;
	public String date;
	public String time;
	public String status; //客票状态
	public String arriveTime;
	public String fareBasis;
	public String carrier;
	public String luggageWeight;
	public String fromT;
	public String toT;
	//public String pnr;
	//public String airLinePnr;
	public String period;

	@Override
	public String toString() {
	    return "FlightSeg [segType=" + segType + ", from=" + from + ", flightNumber=" + flightNumber + ", classCode=" + classCode + ", date=" + date
		    + ", time=" + time + ", status=" + status + ", arriveTime=" + arriveTime + ", period=" + period + ", fareBasis=" + fareBasis + ", carrier="
		    + carrier + ", luggageWeight=" + luggageWeight + ", fromT=" + fromT + ", toT=" + toT + "]\n";
	}

    }

    public String er;
    public String tourCode;
    public String conjTkt;
    public List<FlightSeg> lsFlightSeg = Lists.newArrayList();
    //票价计算
    public String fc;
    //付款方式
    public String fop;
    //税款
    public List<String> taxs = Lists.newArrayList();
    //机票款
    public String fare;
    //总额
    public String total;
    //旅客姓名
    public String name;
    //public String detrTime;
    //订票记录编号
    public String pnr;
    //航空公司记录编号
    public String airLinePnr;

    // 1）退改签提示
    // 2）是否已打印行程单
    // 3）连续票号
    // 4）行李重量
    // 5）客票中的行程信息，出发，目的地，航司，航班号，舱位，日期，时间，运价基础
    // 6）销售单位及agent code

    @Override
    public String toString() {
	return "FullDetrResult [er=" + er + ", pnr=" + pnr + ", airLinePnr=" + airLinePnr + ", name=" + name + ", tourCode=" + tourCode + ", conjTkt="
		+ conjTkt + ", fc=" + fc + ", fop=" + fop + ", fare=" + fare + ", total=" + total + "\ntaxs=" + taxs + ", \nlsFlightSeg=" + lsFlightSeg + "]";
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	// TODO Auto-generated method stub

    }

}
