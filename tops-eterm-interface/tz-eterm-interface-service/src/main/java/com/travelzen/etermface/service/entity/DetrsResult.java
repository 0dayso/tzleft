package com.travelzen.etermface.service.entity;

public class DetrsResult {

    public String STATUS;
    public String ERRORS;

    public String issueTktOffice;
    public String issueTktDate;    //出票时间
    public String agentCode;    //航协代码
    /**
     * public String issueAgent;	//出票代理人
     * public String agencyAddress;	//代理人地址
     * public String tel;	//代理人电话
     * public String fax;	//代理人传真
     */
    public String psgName;
    public PnrRet.PatResult price;
    public String docCode;    //身份识别号码


    @Override
    public String toString() {
        return "DetrsResult [issueTktOffice=" + issueTktOffice + ", issueTktDate=" + issueTktDate
                + ", docCode=" + docCode + ", psgName=" + psgName + ", price=[" + price + "\n]";
    }
}
