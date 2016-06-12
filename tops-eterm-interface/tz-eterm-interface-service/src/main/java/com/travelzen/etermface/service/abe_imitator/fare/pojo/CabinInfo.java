package com.travelzen.etermface.service.abe_imitator.fare.pojo;

import java.util.Locale;

import org.joda.time.DateTime;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.travelzen.framework.core.time.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XStreamAlias("CabinInfo")
public class CabinInfo {
    private static Logger logger = LoggerFactory.getLogger(CabinInfo.class);
    /**
     * 行号
     */
    private String index;
    /**
     * 舱位等级：Y(经济舱)，C(公务舱)，F(头等舱)
     */
    private String cabinLevel;

    /**
     * 舱位代码:Q,T等
     */
    private String cabinCode;

    /**
     * 扩展舱位
     */
    private String expandCabin;

    /**
     * 运价基础
     */
    private String fareBasis;

    /**
     * 乘客类型
     */
    private String passengerType;

    /**
     * 单程价格
     */
    private float owFare;

    /**
     * 往返
     */
    private float rtFare;

    /**
     * 价格类型:FD,NFD,PAT
     */
    private String fareType;

    /**
     * 价格开始有效时间
     */
    private String effectiveStartDate;

    /**
     * 价格过期时间
     */
    private String effectiveEndDate;

    /**
     * 出票时间限定
     */
    private String ticketTimeLimit;

    /**
     * 货币钟类，默认为CNY（人民币）
     */
    private String currencyType = "CNY";

    /**
     * 运营商
     */
    private String airLine;

    /**
     * 最短停留
     */
    private String minStaty;

    /**
     * 最长停留
     */
    private String maxStaty;

    /**
     * 文件号
     */
    private String fileNum;
    /**
     * 航线
     */
    private String segment;
    /**
     * 原价的百分比，该值可能存在也可能不存在
     */
    private int discout;

    private String prebookEarliestDay;
    private String prebookLatestDay;
    private String suitableWeekdays;
    private String suitableTimeRange;
    private String suitableFltNumber;
    private boolean disable3action = false;
    private String outwardExcludeFltNumber;

    private String earliestIssuteTktDate;
    private String latestIssuteTktDate;

    private boolean hasPreorder = false;

    public boolean isDisable3action() {
        return disable3action;
    }

    public void setDisable3action(boolean disable3action) {
        this.disable3action = disable3action;
    }

    public String getEarliestIssuteTktDate() {
        return earliestIssuteTktDate;
    }

    public void setEarliestIssuteTktDate(String earliestIssuteTktDate) {
        this.earliestIssuteTktDate = earliestIssuteTktDate;
    }

    public String getLatestIssuteTktDate() {
        return latestIssuteTktDate;
    }

    public void setLatestIssuteTktDate(String latestIssuteTktDate) {
        this.latestIssuteTktDate = latestIssuteTktDate;
    }

    public String getOutwardExcludeFltNumber() {
        return outwardExcludeFltNumber;
    }

    public void setOutwardExcludeFltNumber(String outwardExcludeFltNumber) {
        this.outwardExcludeFltNumber = outwardExcludeFltNumber;
    }

    public String getSuitableFltNumber() {
        return suitableFltNumber;
    }

    public void setSuitableFltNumber(String suitableFltNumber) {
        this.suitableFltNumber = suitableFltNumber;
    }

    public boolean isHasPreorder() {
        return hasPreorder;
    }

    public void setHasPreorder(boolean hasPreorder) {
        this.hasPreorder = hasPreorder;
    }

    public String getPrebookEarliestDay() {
        return prebookEarliestDay;
    }

    public void setPrebookEarliestDay(String prebookEarliestDay) {
        this.prebookEarliestDay = prebookEarliestDay;
    }

    public String getPrebookLatestDay() {
        return prebookLatestDay;
    }

    public void setPrebookLatestDay(String prebookLatestDay) {
        this.prebookLatestDay = prebookLatestDay;
    }

    public String getSuitableWeekdays() {
        return suitableWeekdays;
    }

    public void setSuitableWeekdays(String suitableWeekdays) {
        this.suitableWeekdays = suitableWeekdays;
    }

    public String getSuitableTimeRange() {
        return suitableTimeRange;
    }

    public void setSuitableTimeRange(String suitableTimeRange) {
        this.suitableTimeRange = suitableTimeRange;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getFileNum() {
        return fileNum;
    }

    public void setFileNum(String fileNum) {
        this.fileNum = fileNum;
    }

    public String getMinStaty() {
        return minStaty;
    }

    public void setMinStaty(String minStaty) {
        this.minStaty = minStaty;
    }

    public String getMaxStaty() {
        return maxStaty;
    }

    public void setMaxStaty(String maxStaty) {
        this.maxStaty = maxStaty;
    }

    public void setOwFare(float owFare) {
        this.owFare = owFare;
    }

    public void setRtFare(float rtFare) {
        this.rtFare = rtFare;
    }

    public String getAirLine() {
        return airLine;
    }

    public void setAirLine(String airLine) {
        this.airLine = airLine;
    }

    public String getTicketTimeLimit() {
        return ticketTimeLimit;
    }

    public void setTicketTimeLimit(String ticketTimeLimit) {
        this.ticketTimeLimit = ticketTimeLimit;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getCabinLevel() {
        return cabinLevel;
    }

    public void setCabinLevel(String cabinLevel) {
        this.cabinLevel = cabinLevel;
    }

    public String getCabinCode() {
        return cabinCode;
    }

    public void setCabinCode(String cabinCode) {
        this.cabinCode = cabinCode;
    }

    public String getExpandCabin() {
        return expandCabin;
    }

    public void setExpandCabin(String expandCabin) {
        this.expandCabin = expandCabin;
    }

    public String getFareBasis() {
        return fareBasis;
    }

    public void setFareBasis(String fareBasis) {
        this.fareBasis = fareBasis;
    }

    public String getPassengerType() {
        return passengerType;
    }

    public void setPassengerType(String passengerType) {
        this.passengerType = passengerType;
    }

    public float getOwFare() {
        return owFare;
    }

    public void setOwFare(String owFare) {
        float rs = Float.parseFloat(owFare);
        this.owFare = rs;
    }

    public float getRtFare() {
        return rtFare;
    }

    public void setRtFare(String rtFare) {
        float rs = Float.parseFloat(rtFare);
        this.rtFare = rs;
    }

    public String getFareType() {
        return fareType;
    }

    public void setFareType(String fareType) {
        this.fareType = fareType;
    }

    public String getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(String effectiveStartDate) {
        try {
            DateTime date = DateTimeUtil.getDate(effectiveStartDate, "ddMMMyy", Locale.US);
            String rs = DateTimeUtil.formatDate(date, "yyyyMMdd");
            if (rs.startsWith("19")) {
                rs = rs.replaceFirst("19", "20");
            }
            this.effectiveStartDate = rs;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    public String getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(String effectiveEndDate) {
        try {
            DateTime date = DateTimeUtil.getDate(effectiveEndDate, "ddMMMyy", Locale.US);
            String rs = DateTimeUtil.formatDate(date, "yyyyMMdd");
            if (rs.startsWith("19")) {
                rs = rs.replaceFirst("19", "20");
            }
            this.effectiveEndDate = rs;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public int getDiscout() {
        return discout;
    }

    public void setDiscout(int discout) {
        this.discout = discout;
    }

    @Override
    public String toString() {
        return "CabinInfo [index=" + index + ", cabinLevel=" + cabinLevel + ", cabinCode=" + cabinCode + ", expandCabin=" + expandCabin + ", fareBasis="
                + fareBasis + ", passengerType=" + passengerType + ", owFare=" + owFare + ", rtFare=" + rtFare + ", fareType=" + fareType
                + ", effectiveStartDate=" + effectiveStartDate + ", effectiveEndDate=" + effectiveEndDate + ", ticketTimeLimit=" + ticketTimeLimit
                + ", currencyType=" + currencyType + ", airLine=" + airLine + ", minStaty=" + minStaty + ", maxStaty=" + maxStaty + ", fileNum=" + fileNum
                + ", segment=" + segment + ", prebookEarliestDay=" + prebookEarliestDay + ", prebookLatestDay=" + prebookLatestDay + ", suitableWeekdays="
                + suitableWeekdays + ", suitableTimeRange=" + suitableTimeRange + ", hasPreorder=" + hasPreorder + "discount=" + discout + "]";
    }
}
