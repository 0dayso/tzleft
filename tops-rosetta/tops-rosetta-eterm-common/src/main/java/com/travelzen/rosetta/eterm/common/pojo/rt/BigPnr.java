package com.travelzen.rosetta.eterm.common.pojo.rt;

/**
 * 大编码
 *
 * @author yiming.yan
 */
public class BigPnr {
    /**
     * 航司
     */
    private String carrier;
    /**
     * 大编码
     */
    private String bigPnr;

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getBigPnr() {
        return bigPnr;
    }

    public void setBigPnr(String bigPnr) {
        this.bigPnr = bigPnr;
    }

    @Override
    public String toString() {
        return "BigPnr [carrier=" + carrier + ", bigPnr=" + bigPnr + "]";
    }

}
