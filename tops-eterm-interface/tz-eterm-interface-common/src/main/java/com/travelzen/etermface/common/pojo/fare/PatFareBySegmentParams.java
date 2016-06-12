package com.travelzen.etermface.common.pojo.fare;

import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;

@XStreamAlias("PatFareBySegmentParams")
public class PatFareBySegmentParams {
	/**
     * 航司
     */
    private String airCompany;
    /**
     * 航班号
     */
    private String flightNum;
    /**
     * 舱位等级
     */
    private String bookingClass;
    /**
     * 舱位代码
     */
    private String cabinCode;
    /**
     * 子舱位号
     */
    private String subCabinCode;
    /**
     * 出发日期
     */
    private String deptDate;
    /**
     * 到达日期
     */
    private String arrDate;
    /**
     * 出发时间
     */
    private String deptTime;
    /**
     * 到达时间
     */
    private String arrTime;
    /**
     * 出发机场
     */
    private String deptAirport;
    /**
     * 到达机场
     */
    private String arrAirport;
    /**
     * 座位数
     */
    private String seatNum;
    /**
     * 机型
     */
    private String planeType;
    /**
     * office号
     */
    private String officeId;
    /**
     * 共享航班号
     */
    private String shareFlightNum;
	/**
     * 可能传入的白屏查询价格
     */
    private double searchFare;

	public String getAirCompany() {
		return airCompany;
	}

	public void setAirCompany(String airCompany) {
		this.airCompany = airCompany;
	}

	public String getFlightNum() {
		return flightNum;
	}

	public void setFlightNum(String flightNum) {
		this.flightNum = flightNum;
	}

	public String getBookingClass() {
		return bookingClass;
	}

	public void setBookingClass(String bookingClass) {
		this.bookingClass = bookingClass;
	}

	public String getCabinCode() {
		return cabinCode;
	}

	public void setCabinCode(String cabinCode) {
		this.cabinCode = cabinCode;
	}

	public String getSubCabinCode() {
		return subCabinCode;
	}

	public void setSubCabinCode(String subCabinCode) {
		this.subCabinCode = subCabinCode;
	}

	public String getDeptDate() {
		return deptDate;
	}

	public void setDeptDate(String deptDate) {
		this.deptDate = deptDate;
	}

	public String getArrDate() {
		return arrDate;
	}

	public void setArrDate(String arrDate) {
		this.arrDate = arrDate;
	}

	public String getDeptTime() {
		return deptTime;
	}

	public void setDeptTime(String deptTime) {
		this.deptTime = deptTime;
	}

	public String getArrTime() {
		return arrTime;
	}

	public void setArrTime(String arrTime) {
		this.arrTime = arrTime;
	}

	public String getDeptAirport() {
		return deptAirport;
	}

	public void setDeptAirport(String deptAirport) {
		this.deptAirport = deptAirport;
	}

	public String getArrAirport() {
		return arrAirport;
	}

	public void setArrAirport(String arrAirport) {
		this.arrAirport = arrAirport;
	}

	public String getSeatNum() {
		return seatNum;
	}

	public void setSeatNum(String seatNum) {
		this.seatNum = seatNum;
	}

	public String getPlaneType() {
		return planeType;
	}

	public void setPlaneType(String planeType) {
		this.planeType = planeType;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getShareFlightNum() {
		return shareFlightNum;
	}

	public void setShareFlightNum(String shareFlightNum) {
		this.shareFlightNum = shareFlightNum;
	}

	public double getSearchFare() {
		return searchFare;
	}

	public void setSearchFare(double searchFare) {
		this.searchFare = searchFare;
	}
	
	@Override
	public String toString() {
		return "PatFareBySegmentParams [airCompany=" + airCompany
				+ ", flightNum=" + flightNum + ", bookingClass=" + bookingClass
				+ ", cabinCode=" + cabinCode + ", subCabinCode=" + subCabinCode
				+ ", deptDate=" + deptDate + ", arrDate=" + arrDate
				+ ", deptTime=" + deptTime + ", arrTime=" + arrTime
				+ ", deptAirport=" + deptAirport + ", arrAirport=" + arrAirport
				+ ", seatNum=" + seatNum + ", planeType=" + planeType
				+ ", officeId=" + officeId + ", shareFlightNum="
				+ shareFlightNum + ", searchFare=" + searchFare + "]";
	}

	/**
     * 从xml转化为object
     * 
     * @param String
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<PatFareBySegmentParams> fromXML(String xml) {
        if (null == xml) {
            return null;
        }
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        xstream.processAnnotations(List.class);
        xstream.processAnnotations(PatFareBySegmentParams.class);
        xstream.alias("PatParamsList", List.class);
        return (List<PatFareBySegmentParams>) xstream.fromXML(xml);
    }

    /**
     * 将对象转化为XML
     * 
     * @param PatFareBySegmentParams
     * @return
     */
    public static String toXML(List<PatFareBySegmentParams> params) {
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        xstream.processAnnotations(List.class);
        xstream.processAnnotations(PatFareBySegmentParams.class);
        xstream.alias("PatParamsList", List.class);
        return xstream.toXML(params);
    }
}
