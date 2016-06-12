package com.travelzen.etermface.web.bean;

public class PnrSetPriceRequestBean {
	
	private String eiSessionId;
	private boolean isDomestic;
	private String adtFareBasis;
	private String chdFareBasis;
	private double adtFare;
	private double chdFare;
	private String source;
	private String role;
	private String tripartCode;
	private String officeId;
	private String pnr;

    public String getEiSessionId() {
        return eiSessionId;
    }

    public void setEiSessionId(String eiSessionId) {
        this.eiSessionId = eiSessionId;
    }

    public boolean isDomestic() {
        return isDomestic;
    }

    public void setDomestic(boolean isDomestic) {
        this.isDomestic = isDomestic;
    }

    public String getAdtFareBasis() {
        return adtFareBasis;
    }

    public void setAdtFareBasis(String adtFareBasis) {
        this.adtFareBasis = adtFareBasis;
    }

    public String getChdFareBasis() {
        return chdFareBasis;
    }

    public void setChdFareBasis(String chdFareBasis) {
        this.chdFareBasis = chdFareBasis;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTripartCode() {
        return tripartCode;
    }

    public void setTripartCode(String tripartCode) {
        this.tripartCode = tripartCode;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public double getAdtFare() {
        return adtFare;
    }

    public void setAdtFare(double adtFare) {
        this.adtFare = adtFare;
    }

    public double getChdFare() {
        return chdFare;
    }

    public void setChdFare(double chdFare) {
        this.chdFare = chdFare;
    }

	@Override
	public String toString() {
		return "PnrSetPriceRequestBean [eiSessionId=" + eiSessionId
				+ ", isDomestic=" + isDomestic + ", adtFareBasis="
				+ adtFareBasis + ", chdFareBasis=" + chdFareBasis
				+ ", adtFare=" + adtFare + ", chdFare=" + chdFare + ", source="
				+ source + ", role=" + role + ", tripartCode=" + tripartCode
				+ ", officeId=" + officeId + ", pnr=" + pnr + "]";
	}

}
