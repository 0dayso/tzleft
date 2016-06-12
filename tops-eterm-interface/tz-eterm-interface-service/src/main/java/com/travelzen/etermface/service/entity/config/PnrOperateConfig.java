package com.travelzen.etermface.service.entity.config;

public class PnrOperateConfig {

    public String pnr;

    public String adtFareBasis;
    public String chdFareBasis;
    public double adtFare ;
    public double chdFare;

    public String officeId;

    @Override
	public String toString() {
		return "PnrOperateConfig [pnr=" + pnr + ", adtFareBasis=" + adtFareBasis + ", chdFareBasis=" + chdFareBasis + ", adtFare=" + adtFare + ", chdFare="
				+ chdFare + ", officeId=" + officeId + "]";
	}



	/**
     * @param args
     */
    public static void main(String[] args) {

    }

}
