package com.travelzen.etermface.web.bean;

public class PnrPriceRequestBean {
    String eiSessionId;
    boolean isDomestic;
    boolean needXsfsm;
    boolean needFare;
    boolean needAccurateCodeShare;
    boolean needIssueTktOffice;
    String source;
    String role;
    String tripartCode;
    String officeId;
    String pnr;
    String traceId;

    public boolean isDomestic() {
        return isDomestic;
    }

    public void setDomestic(boolean isDomestic) {
        this.isDomestic = isDomestic;
    }

    public boolean isNeedXsfsm() {
        return needXsfsm;
    }

    public void setNeedXsfsm(boolean needXsfsm) {
        this.needXsfsm = needXsfsm;
    }

    public boolean isNeedFare() {
        return needFare;
    }

    public void setNeedFare(boolean needFare) {
        this.needFare = needFare;
    }

    public boolean isNeedAccurateCodeShare() {
        return needAccurateCodeShare;
    }

    public void setNeedAccurateCodeShare(boolean needAccurateCodeShare) {
        this.needAccurateCodeShare = needAccurateCodeShare;
    }

    public boolean isNeedIssueTktOffice() {
        return needIssueTktOffice;
    }

    public void setNeedIssueTktOffice(boolean needIssueTktOffice) {
        this.needIssueTktOffice = needIssueTktOffice;
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

    public String getEiSessionId() {
        return eiSessionId;
    }

    public void setEiSessionId(String eiSessionId) {
        this.eiSessionId = eiSessionId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
