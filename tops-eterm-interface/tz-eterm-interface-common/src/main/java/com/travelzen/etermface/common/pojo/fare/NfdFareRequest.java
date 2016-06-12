package com.travelzen.etermface.common.pojo.fare;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:15/4/13
 * Time:下午2:49
 * <p/>
 * Description:
 * <p/>
 * Nfd请求参数所对应的类
 */
public class NfdFareRequest implements Serializable {
    private static final long serialVersionUID = -8247540435785914368L;
    /**
     * 出发机场三字码
     */
    public String deptAirport;
    /**
     * 到达机场三字码
     */
    public String arrAirport;
    /**
     * 出发日期(yyyy-mm-dd)
     */
    public String deptDate;
    /**
     * 航司二字码
     */
    public String carrier;
    /**
     * 舱位
     */
    public String cabin;
    /**
     * 是否获取去程/回程信息
     */
    public boolean needNfn01;
    /**
     * 是否获取适用规定
     */
    public boolean needNfn02;
    /**
     * 是否获取预定规定
     */
    public boolean needNfn04;
    /**
     * 是否获取运价组合
     */
    public boolean needNfn05;
    /**
     * 是否获取团队规定
     */
    public boolean needNfn06;
    /**
     * 是否获取退票规定
     */
    public boolean needNfn08;
    /**
     * 是否获取变更规定
     */
    public boolean needNfn09;
    /**
     * 是否获取其他规定
     */
    public boolean needNfn11;

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

    public String getDeptDate() {
        return deptDate;
    }

    public void setDeptDate(String deptDate) {
        this.deptDate = deptDate;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getCabin() {
        return cabin;
    }

    public void setCabin(String cabin) {
        this.cabin = cabin;
    }

    public boolean isNeedNfn01() {
        return needNfn01;
    }

    public void setNeedNfn01(boolean needNfn01) {
        this.needNfn01 = needNfn01;
    }

    public boolean isNeedNfn02() {
        return needNfn02;
    }

    public void setNeedNfn02(boolean needNfn02) {
        this.needNfn02 = needNfn02;
    }

    public boolean isNeedNfn04() {
        return needNfn04;
    }

    public void setNeedNfn04(boolean needNfn04) {
        this.needNfn04 = needNfn04;
    }

    public boolean isNeedNfn05() {
        return needNfn05;
    }

    public void setNeedNfn05(boolean needNfn05) {
        this.needNfn05 = needNfn05;
    }

    public boolean isNeedNfn06() {
        return needNfn06;
    }

    public void setNeedNfn06(boolean needNfn06) {
        this.needNfn06 = needNfn06;
    }

    public boolean isNeedNfn08() {
        return needNfn08;
    }

    public void setNeedNfn08(boolean needNfn08) {
        this.needNfn08 = needNfn08;
    }

    public boolean isNeedNfn09() {
        return needNfn09;
    }

    public void setNeedNfn09(boolean needNfn09) {
        this.needNfn09 = needNfn09;
    }

    public boolean isNeedNfn11() {
        return needNfn11;
    }

    public void setNeedNfn11(boolean needNfn11) {
        this.needNfn11 = needNfn11;
    }

	@Override
	public String toString() {
		return "NfdFareRequest [deptAirport=" + deptAirport + ", arrAirport="
				+ arrAirport + ", deptDate=" + deptDate + ", carrier="
				+ carrier + ", cabin=" + cabin + ", needNfn01=" + needNfn01
				+ ", needNfn02=" + needNfn02 + ", needNfn04=" + needNfn04
				+ ", needNfn05=" + needNfn05 + ", needNfn06=" + needNfn06
				+ ", needNfn08=" + needNfn08 + ", needNfn09=" + needNfn09
				+ ", needNfn11=" + needNfn11 + "]";
	}
    
}
