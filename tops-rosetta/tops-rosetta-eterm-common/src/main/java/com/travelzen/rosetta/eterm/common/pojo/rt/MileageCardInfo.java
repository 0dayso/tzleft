package com.travelzen.rosetta.eterm.common.pojo.rt;

/**
 * 里程卡信息
 *
 * @author yiming.yan
 */
public class MileageCardInfo {
    /**
     * 乘客序号
     */
    private int psgNo;
    /**
     * 航司
     */
    private String airCompany;
    /**
     * 里程卡号
     */
    private String cardNo;

    public int getPsgNo() {
        return psgNo;
    }

    public void setPsgNo(int psg) {
        this.psgNo = psg;
    }

    public String getAirCompany() {
        return airCompany;
    }

    public void setAirCompany(String airCompany) {
        this.airCompany = airCompany;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    @Override
    public String toString() {
        return "MileageCardInfo [psgNo=" + psgNo + ", airCompany=" + airCompany
                + ", cardNo=" + cardNo + "]";
    }
}
