package com.travelzen.etermface.service.entity.qte;

public class SegmentLimit {

    /**
     * 出发城市
     */
    private String fromCity;

    /**
     * 到达城市
     */
    private String toCity;

    /**
     * 运价基础
     */
    private String fareBasis;

    /**
     * 有效开始日期
     */
    private String nvb;

    /**
     * 有效结束日期
     */
    private String nva;

    /**
     * 行李限额
     */
    private String luggageLimit;

    /**
     * 备注 如地面交通
     */
    private String mark;

    /**
     * 身份优惠折让
     */
    private String passengerdiscout;

    public String getFromCity() {
        return fromCity;
    }

    public void setFromCity(String fromCity) {
        this.fromCity = fromCity;
    }

    public String getToCity() {
        return toCity;
    }

    public void setToCity(String toCity) {
        this.toCity = toCity;
    }

    public String getFareBasis() {
        return fareBasis;
    }

    public void setFareBasis(String fareBasis) {
        this.fareBasis = fareBasis;
    }

    public String getNvb() {
        return nvb;
    }

    public void setNvb(String nvb) {
        this.nvb = nvb;
    }

    public String getNva() {
        return nva;
    }

    public void setNva(String nva) {
        this.nva = nva;
    }

    public String getLuggageLimit() {
        return luggageLimit;
    }

    public void setLuggageLimit(String luggageLimit) {
        this.luggageLimit = luggageLimit;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getPassengerdiscout() {
        return passengerdiscout;
    }

    public void setPassengerdiscout(String passengerdiscout) {
        this.passengerdiscout = passengerdiscout;
    }

	@Override
	public String toString() {
		return "SegmentLimit [fromCity=" + fromCity + ", toCity=" + toCity
				+ ", fareBasis=" + fareBasis + ", nvb=" + nvb + ", nva=" + nva
				+ ", luggageLimit=" + luggageLimit + ", mark=" + mark
				+ ", passengerdiscout=" + passengerdiscout + "]";
	}
    
}
