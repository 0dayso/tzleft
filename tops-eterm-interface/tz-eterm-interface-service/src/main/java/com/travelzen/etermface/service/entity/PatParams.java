package com.travelzen.etermface.service.entity;

import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;

@XStreamAlias("PatParams")
public class PatParams {
    /**
     * 航班号
     */
    private String airNo;
    /**
     * 舱位等级
     */
    private String cabin;

    /**
     * 舱位代码
     */
    private String smallCabin;
    /**
     * 出发日期
     */
    private String depatureDate;
    /**
     * 到达日期
     */
    private String arriveDate;
    /**
     * 出发时间
     */
    private String depatureTime;
    /**
     * 到达时间
     */
    private String arriveTime;
    /**
     * 出发机场
     */
    private String fromAirPort;
    /**
     * 到达机场
     */
    private String toAirPort;
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
    private String codeShareAirNo;
    
    /**
     * 子舱位号
     */
    private String subCabinCode;
    
    public String getSubCabinCode() {
		return subCabinCode;
	}

	public void setSubCabinCode(String subCabinCode) {
		this.subCabinCode = subCabinCode;
	}

	public double getSearchFare() {
		return searchFare;
	}

	public void setSearchFare(double searchFare) {
		this.searchFare = searchFare;
	}

	/**
     * 可能传入的白屏查询价格
     */
    private double searchFare;

    public String getAirNo() {
        return airNo;
    }

    public void setAirNo(String airNo) {
        this.airNo = airNo;
    }

    public String getCabin() {
        return cabin;
    }

    public void setCabin(String cabin) {
        this.cabin = cabin;
    }

    public String getDepatureDate() {
        return depatureDate;
    }

    public void setDepatureDate(String depatureDate) {
        this.depatureDate = depatureDate;
    }

    public String getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(String arriveDate) {
        this.arriveDate = arriveDate;
    }

    public String getDepatureTime() {
        return depatureTime;
    }

    public void setDepatureTime(String depatureTime) {
        this.depatureTime = depatureTime;
    }

    public String getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(String arriveTime) {
        this.arriveTime = arriveTime;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    public String getCodeShareAirNo() {
        return codeShareAirNo;
    }

    public void setCodeShareAirNo(String codeShareAirNo) {
        this.codeShareAirNo = codeShareAirNo;
    }

    public String getFromAirPort() {
        return fromAirPort;
    }

    public void setFromAirPort(String fromAirPort) {
        this.fromAirPort = fromAirPort;
    }

    public String getToAirPort() {
        return toAirPort;
    }

    public void setToAirPort(String toAirPort) {
        this.toAirPort = toAirPort;
    }

    public String getSmallCabin() {
        return smallCabin;
    }

    public void setSmallCabin(String smallCabin) {
        this.smallCabin = smallCabin;
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

    @Override
	public String toString() {
        return "PatParams [airNo=" + airNo + ", cabin=" + cabin + ", smallCabin=" + smallCabin + ", depatureDate=" + depatureDate
                + ", depatureTime=" + depatureTime + ", arriveDate=" + arriveDate + "arriveTime=" + arriveTime + ", fromAirPort=" + fromAirPort + ", toAirPort=" + toAirPort + ", seatNum=" + seatNum + ", planeType="
                + planeType + ", officeId=" + officeId + ", codeShareAirNo=" + codeShareAirNo + ", subCabinCode=" + subCabinCode + ", searchFare=" + searchFare + "]";
	}

	/**
     * 从xml转化为object
     * 
     * @param pXml
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<PatParams> convertFromXML(String pXml) {
        if (null == pXml) {
            return null;
        }
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        xstream.processAnnotations(List.class);
        xstream.processAnnotations(PatParams.class);
        xstream.alias("PatParamsList", List.class);
        return (List<PatParams>) xstream.fromXML(pXml);
    }

    /**
     * 将对象转化为XML
     * 
     * @param pPatParams
     * @return
     */
    public static String convertToXML(List<PatParams> pPatParamsList) {
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        xstream.processAnnotations(List.class);
        xstream.processAnnotations(PatParams.class);
        xstream.alias("PatParamsList", List.class);
        return xstream.toXML(pPatParamsList);
    }
}
