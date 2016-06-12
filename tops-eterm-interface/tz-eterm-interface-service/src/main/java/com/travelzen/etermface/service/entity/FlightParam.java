package com.travelzen.etermface.service.entity;

import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;

@XStreamAlias("FlightParam")
public class FlightParam {

    /**
     * 是否是open航段
     */
    private boolean open;

    /**
     * 航是代码
     */
    private String airCompany;
    /**
     * 航班号
     */
    private String flightNum;
    /**
     * 舱位
     */
    private String cabin;

    /**
     * 小舱位
     */
    private String smallCabin;
    /**
     * 出发日期 格式：2014-06-30
     */
    private String depatureDate;
    /**
     * 到达日期 格式：2014-06-30
     */
    private String arriveDate;
    /**
     * 出发时间 格式：05:30
     */
    private String depatureTime;
    /**
     * 到达时间 格式：20:30
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
     * 共享航司
     */
    private String codeShareAirCompany;
    
    /**
     * 共享航班号
     */
    private String codeShareFlightNum;

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

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

    public String getCodeShareAirCompany() {
        return codeShareAirCompany;
    }

    public void setCodeShareAirCompany(String codeShareAirCompany) {
        this.codeShareAirCompany = codeShareAirCompany;
    }

    public String getCodeShareFlightNum() {
        return codeShareFlightNum;
    }

    public void setCodeShareFlightNum(String codeShareFlightNum) {
        this.codeShareFlightNum = codeShareFlightNum;
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
        return "FlightParam [open=" + open + ", airCompany=" + airCompany + ", flightNum=" + flightNum + ", cabin="
                + cabin + ", smallCabin=" + smallCabin + ", depatureDate=" + depatureDate + ", depatureTime="
                + depatureTime + ", arriveDate=" + arriveDate + "arriveTime=" + arriveTime + ", fromAirPort="
                + fromAirPort + ", toAirPort=" + toAirPort + ", seatNum=" + seatNum + ", planeType=" + planeType
                + ", officeId=" + officeId + ", codeShareAirCompany=" + codeShareAirCompany + ", codeShareFlightNum=" + codeShareFlightNum+ "]";
    }

    /**
     * 从xml转化为object
     * 
     * @param pXml
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<FlightParam> convertFromXML(String pXml) {
        if (null == pXml) {
            return null;
        }
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        xstream.processAnnotations(List.class);
        xstream.processAnnotations(FlightParam.class);
        xstream.alias("FlightParamList", List.class);
        return (List<FlightParam>) xstream.fromXML(pXml);
    }

    /**
     * 将对象转化为XML
     * 
     * @param pPatParams
     * @return
     */
    public static String convertToXML(List<FlightParam> pFlightParamList) {
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        xstream.processAnnotations(List.class);
        xstream.processAnnotations(FlightParam.class);
        xstream.alias("FlightParamList", List.class);
        return xstream.toXML(pFlightParamList);
    }
}
