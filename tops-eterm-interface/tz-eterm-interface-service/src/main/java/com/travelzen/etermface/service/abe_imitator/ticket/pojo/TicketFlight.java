package com.travelzen.etermface.service.abe_imitator.ticket.pojo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.travelzen.etermface.service.abe_imitator.util.DateUtils;

@XStreamAlias("Flight")
public class TicketFlight {
    //顺序号
    @XStreamAsAttribute
    public String ID;
    // 航段来源GDS
    @XStreamAsAttribute
    public String GDS;
    //PNR 号
    @XStreamAsAttribute
    public String PNR;
    //pnr大编号
    @XStreamAsAttribute
    public String ICSPNR;
    //出发机场
    @XStreamAsAttribute
    public String Airport;
    @XStreamAsAttribute
    public String AirportName;
     //航司
    @XStreamAsAttribute
    public String Carrier;
    @XStreamAsAttribute
    public String CarrierName;
    //共享航司
    @XStreamAsAttribute
    public String ShareCarrier;
    //航班号
    @XStreamAsAttribute
    public String Flight;
    //仓位类型
    @XStreamAsAttribute
    public String Class;
    //日期 格式15MAY
    @XStreamAsAttribute
    public String Date;
    //时间 格式1610
    @XStreamAsAttribute
    public String Time;
    //日期 格式2013-05-15
    @XStreamAsAttribute
    public String DepartureDate;
   //时间 格式16：10
    @XStreamAsAttribute
    public String DepartureTime;
    //状态
    @XStreamAsAttribute
    public String Status;
    //票价基础
    @XStreamAsAttribute
    public String Farebasis;
    //有效开始日期
    @XStreamAsAttribute
    public String NotValidBef;
    //有效结束日期
    @XStreamAsAttribute
    public String NotValidAft;
    //行李重量限制
    @XStreamAsAttribute
    public String Allow;
    //使用情况缩写
    @XStreamAsAttribute
    public String FltStatus;
    //使用情况，指是否已飞等
    @XStreamAsAttribute
    public String FltStatusMsg;
    @XStreamAsAttribute
    public String AirportTerminal;
    //出发航站楼
    @XStreamAsAttribute
    public String BoardPointAT;
    //到达航站楼
    @XStreamAsAttribute
    public String OffpointAT;
    //Detr结果中航段信息开始时的字母 
    public String startType;

    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }

    public String getGDS() {
        return GDS;
    }

    public void setGDS(String gDS) {
        GDS = gDS;
    }

    public String getPNR() {
        return PNR;
    }

    public void setPNR(String pNR) {
        PNR = pNR;
    }

    public String getICSPNR() {
        return ICSPNR;
    }

    public void setICSPNR(String iCSPNR) {
        ICSPNR = iCSPNR;
    }

    public String getAirport() {
        return Airport;
    }

    public void setAirport(String airport) {
        Airport = airport;
    }

    public String getAirportName() {
        return AirportName;
    }

    public void setAirportName(String airportName) {
        AirportName = airportName;
    }

    public String getCarrier() {
        return Carrier;
    }

    public void setCarrier(String carrier) {
        Carrier = carrier;
    }

    public String getCarrierName() {
        return CarrierName;
    }

    public void setCarrierName(String carrierName) {
        CarrierName = carrierName;
    }

    public String getShareCarrier() {
        return ShareCarrier;
    }

    public void setShareCarrier(String shareCarrier) {
        ShareCarrier = shareCarrier;
    }

    public String getFlight() {
        return Flight;
    }

    public void setFlight(String flight) {
        Flight = flight;
    }

    public void setClass(String class1) {
        Class = class1;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
        String departureDate = DateUtils.dateByDdMmm(date);
        setDepartureDate(departureDate);
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
        if (time.length() == 4 && !time.equals("OPEN")) {
            String departureTime = time.substring(0, 2);
            departureTime = departureTime + ":";
            departureTime = departureTime + time.substring(2);
            setDepartureTime(departureTime);
        }
    }

    public String getDepartureDate() {
        return DepartureDate;
    }

    public void setDepartureDate(String departureDate) {
        DepartureDate = departureDate;
    }

    public String getDepartureTime() {
        return DepartureTime;
    }

    public void setDepartureTime(String departureTime) {
        DepartureTime = departureTime;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getFarebasis() {
        return Farebasis;
    }

    public void setFarebasis(String farebasis) {
        Farebasis = farebasis;
    }

    public String getNotValidBef() {
        return NotValidBef;
    }

    public void setNotValidBef(String notValidBef) {
        NotValidBef = notValidBef;
    }

    public String getNotValidAft() {
        return NotValidAft;
    }

    public void setNotValidAft(String notValidAft) {
        NotValidAft = notValidAft;
    }

    public String getAllow() {
        return Allow;
    }

    public void setAllow(String allow) {
        Allow = allow;
    }

    public String getFltStatus() {
        return FltStatus;
    }

    public void setFltStatus(String fltStatus) {
        FltStatus = fltStatus;

    }

    public String getFltStatusMsg() {
        return FltStatusMsg;
    }

    public void setFltStatusMsg(String fltStatusMsg) {
        FltStatusMsg = fltStatusMsg;
        if (fltStatusMsg != null && fltStatusMsg.length() > 0) {
            String fltStatus = String.valueOf(fltStatusMsg.charAt(0));
            if (fltStatusMsg.equals("USED/FLOWN")) {
                fltStatus = "F";
            }
            setFltStatus(fltStatus);
        }
    }

    public String getAirportTerminal() {
        return AirportTerminal;
    }

    public void setAirportTerminal(String airportTerminal) {
        AirportTerminal = airportTerminal;
    }

    public String getBoardPointAT() {
        return BoardPointAT;
    }

    public void setBoardPointAT(String boardPointAT) {
        BoardPointAT = boardPointAT;
    }

    public String getOffpointAT() {
        return OffpointAT;
    }

    public void setOffpointAT(String offpointAT) {
        OffpointAT = offpointAT;
    }

    public String getStartType() {
        return startType;
    }

    public void setStartType(String startType) {
        this.startType = startType;
    }
}