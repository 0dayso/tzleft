package com.travelzen.etermface.service.entity.qte;

import java.util.List;

public class QteBySegmentsResponse {
    /**
     * 乘客类型
     */
    private String passengerType = "ADT";
    /**
     * 票面价
     */
    private int baseFare;
    /**
     * it or bt fare
     */
    private String itOrBt;
    /**
     * 总价
     */
    private int totalFare;
    /**
     * 总税收= 总价-票面价
     */
    private int totalTax;
    /**
     * 税收详情
     */
    private String taxDetail;

    /**
     * nuc值横式
     */
    private String nucInfo;

    /**
     * 汇率信息
     */
    private ExchangeRate exchangeRate;

    /**
     * 退改签备注
     * 
     */
    private String endos;

    /**
     * 航段限制信息
     * 
     */
    private List<SegmentLimit> segmentLimits;
    /**
     * 
     * @return
     */
    private String commission;
    /**
     * q值
     */
    private int qValue;
    /**
     * 错误信息
     */
    private String errorInfo;

    /**
     * 
     * @return
     */
    private String ticketingAirlineCompany;

    // /**
    // * nuc分析结果
    // *
    // * @return
    // */
    // private SegPriceSyntaxTree segPriceSyntaxTree;

    public String getPassengerType() {
        return passengerType;
    }

    public void setPassengerType(String passengerType) {
        this.passengerType = passengerType;
    }

    public int getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(int baseFare) {
        this.baseFare = baseFare;
    }

    public int getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(int totalFare) {
        this.totalFare = totalFare;
    }

    public int getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(int totalTax) {
        this.totalTax = totalTax;
    }

    public String getTaxDetail() {
        return taxDetail;
    }

    public void setTaxDetail(String taxDetail) {
        this.taxDetail = taxDetail;
    }

    public String getNucInfo() {
        return nucInfo;
    }

    public void setNucInfo(String nucInfo) {
        this.nucInfo = nucInfo;
    }

    public ExchangeRate getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(ExchangeRate exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getEndos() {
        return endos;
    }

    public void setEndos(String endos) {
        this.endos = endos;
    }

    public List<SegmentLimit> getSegmentLimits() {
        return segmentLimits;
    }

    public void setSegmentLimits(List<SegmentLimit> segmentLimits) {
        this.segmentLimits = segmentLimits;
    }

    public String getCommission() {
        return commission;
    }

    public double getqValue() {
        return qValue;
    }

    public void setqValue(int qValue) {
        this.qValue = qValue;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getItOrBt() {
        return itOrBt;
    }

    public void setItOrBt(String itOrBt) {
        this.itOrBt = itOrBt;
    }

    // public SegPriceSyntaxTree getSegPriceSyntaxTree() {
    // return segPriceSyntaxTree;
    // }
    //
    // public void setSegPriceSyntaxTree(SegPriceSyntaxTree segPriceSyntaxTree)
    // {
    // this.segPriceSyntaxTree = segPriceSyntaxTree;
    // }

    public String getTicketingAirlineCompany() {
        return ticketingAirlineCompany;
    }

    public void setTicketingAirlineCompany(String ticketingAirlineCompany) {
        this.ticketingAirlineCompany = ticketingAirlineCompany;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

	@Override
	public String toString() {
		return "QteBySegmentsResponse [passengerType=" + passengerType
				+ ", baseFare=" + baseFare + ", itOrBt=" + itOrBt
				+ ", totalFare=" + totalFare + ", totalTax=" + totalTax
				+ ", taxDetail=" + taxDetail + ", nucInfo=" + nucInfo
				+ ", exchangeRate=" + exchangeRate + ", endos=" + endos
				+ ", segmentLimits=" + segmentLimits + ", commission="
				+ commission + ", qValue=" + qValue + ", errorInfo="
				+ errorInfo + ", ticketingAirlineCompany="
				+ ticketingAirlineCompany + "]";
	}

}
