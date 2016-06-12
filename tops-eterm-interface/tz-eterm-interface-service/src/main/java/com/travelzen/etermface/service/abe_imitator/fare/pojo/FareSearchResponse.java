package com.travelzen.etermface.service.abe_imitator.fare.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.travelzen.framework.core.time.DateTimeUtil;

@XStreamAlias("FareSearchResponse")
public class FareSearchResponse {

    /**
     * 航空公司
     */
    private String airLine;

    /**
     * 航班号
     */
    private String flightNumber;
    /**
     * 出发机场
     */
    private String fromAirPort;

    /**
     * 到达机场
     */
    private String toAirPort;

    /**
     * 出发日期
     */
    private String fromDate;

    /**
     * 备注
     */
    private String note;
    /**
     * 距离
     */
    private String distance;
    /**
     * 该航空公司，特定航段，特定天内票价情况信息
     */

    private List<CabinInfo> cabinInfos;

    /**
     * 是否成功
     */
    private boolean isSuccess;

    private String errorInfo;

    public void addCabinInFo(CabinInfo cabinInfo) {
        if (cabinInfos == null) {
            cabinInfos = new ArrayList<CabinInfo>();
        }
        cabinInfos.add(cabinInfo);
    }

    public List<CabinInfo> getCabinInfos() {
        return cabinInfos;
    }

    public void setCabinInfos(List<CabinInfo> cabinInfos) {
        this.cabinInfos = cabinInfos;
    }

    public String getAirLine() {
        return airLine;
    }

    public void setAirLine(String airLine) {
        this.airLine = airLine;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
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

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
	
        DateTime date = DateTimeUtil.getDate(fromDate, "ddMMMyy", Locale.US);
        
        
        String rs = DateTimeUtil.formatDate(date, "yyyyMMdd");
        ;
        this.fromDate = rs;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        if (this.note == null) {
            this.note = "";
        }
        this.note = this.note + " " + note;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setDistanceByNote() {
        if (note.contains("TPM")) {
            int index = note.indexOf("TPM");
            String tmp = note.substring(index + 3);
            if (tmp.length() > 0) {
                tmp = tmp.trim();
                if (tmp.contains(" ")) {
                    index = tmp.indexOf(" ");
                    tmp = tmp.substring(0, index);
                    tmp = tmp.trim();
                }
                distance = tmp;
            }
        }
    }

    public String toXML() {
        XStream xstream = new XStream();
        xstream.processAnnotations(FareSearchResponse.class);
        xstream.processAnnotations(CabinInfo.class);
        return xstream.toXML(this);
    }

	@Override
	public String toString() {
		return "FareSearchResponse [airLine=" + airLine + ", flightNumber="
				+ flightNumber + ", fromAirPort=" + fromAirPort
				+ ", toAirPort=" + toAirPort + ", fromDate=" + fromDate
				+ ", note=" + note + ", distance=" + distance + ", cabinInfos="
				+ cabinInfos + ", isSuccess=" + isSuccess + ", errorInfo="
				+ errorInfo + "]";
	}
}
