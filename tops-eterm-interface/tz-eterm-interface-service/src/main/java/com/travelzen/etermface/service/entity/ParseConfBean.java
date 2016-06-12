package com.travelzen.etermface.service.entity;

import javax.servlet.AsyncContext;

import org.apache.commons.lang3.StringUtils;

import com.travelzen.etermface.common.config.cdxg.CdxgConstant;
import com.travelzen.etermface.service.constant.PnrParserConstant;
import com.travelzen.etermface.service.ticket.DomTktExtractorLogic;
import com.travelzen.etermface.service.ticket.GeneralTktExtractorHandler;
import com.travelzen.etermface.service.ticket.ITktExtractorLogic;

public class ParseConfBean {

    public String traceId;

    public AsyncContext actx;

    public RtInterceptorBean rtInterceptorBean = new RtInterceptorBean();

    // interning flight 国际航班 domestic flight 国际航班
    public boolean isDomestic = false;

    public boolean needFare = false;
    public boolean needSleep = false;

    public boolean needPnrRet = false;
    public boolean needMakeFareByPNR = false;

    public boolean needXsfsm = false;
    public boolean needAccurateCodeShare = false;

    public boolean needIssueTktOffice = false;

    public boolean needERRule = false;

    public boolean useAv4SharecodeAndDistance = false;

    // 三方协议编号
    public String tripartCode = "";

    private String officeId = CdxgConstant.DEFAULT_OFFICE;

    public String source = PnrParserConstant.SOURCE_ETERM;
    public String role = PnrParserConstant.ROLE_OPERATOR;

    // coverageFirst
    public String tktExtractType = PnrParserConstant.TKT_EXTRACT_TYPE_ACCURACY_FIRST;

    public int debugLevel = 0;

    public int sessionExpireMillsec = CdxgConstant.BASE_LOCK_KEEP_MILLSEC;

    public RtInterceptorBean getRtInterceptorBean() {
	return rtInterceptorBean;
    }

    public void setRtInterceptorBean(RtInterceptorBean rtInterceptorBean) {
	this.rtInterceptorBean = rtInterceptorBean;
    }

    public boolean isDomestic() {
	return isDomestic;
    }

    public void setDomestic(boolean isDomestic) {
	this.isDomestic = isDomestic;
    }

    public boolean isNeedFare() {
	return needFare;
    }

    public void setNeedFare(boolean needFare) {
	this.needFare = needFare;
    }

    public boolean isNeedSleep() {
	return needSleep;
    }

    public void setNeedSleep(boolean needSleep) {
	this.needSleep = needSleep;
    }

    public boolean isNeedPnrRet() {
	return needPnrRet;
    }

    public void setNeedPnrRet(boolean needPnrRet) {
	this.needPnrRet = needPnrRet;
    }

    public boolean isNeedMakeFareByPNR() {
	return needMakeFareByPNR;
    }

    public void setNeedMakeFareByPNR(boolean needMakeFareByPNR) {
	this.needMakeFareByPNR = needMakeFareByPNR;
    }

    public boolean isNeedXsfsm() {
	return needXsfsm;
    }

    public void setNeedXsfsm(boolean needXsfsm) {
	this.needXsfsm = needXsfsm;
    }

    public boolean isNeedAccurateCodeShare() {
	return needAccurateCodeShare;
    }

    public void setNeedAccurateCodeShare(boolean needAccurateCodeShare) {
	this.needAccurateCodeShare = needAccurateCodeShare;
    }

    public String getOfficeId() {
	return officeId;
    }

    public void setOfficeId(String officeId) {
	if (StringUtils.isBlank(officeId)) {
	    officeId = "SHA255";
	}
	// if (StringUtils.equalsIgnoreCase(officeId, "SHA886")) {
	// officeId = "SHA255";
	// }
	this.officeId = officeId;
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

    public String getTktExtractType() {
	return tktExtractType;
    }

    public void setTktExtractType(String tktExtractType) {
	this.tktExtractType = tktExtractType;
    }

    public int getDebugLevel() {
	return debugLevel;
    }

    public void setDebugLevel(int debugLevel) {
	this.debugLevel = debugLevel;
    }

    public int getSessionExpireMillsec() {
	return sessionExpireMillsec;
    }

    public void setSessionExpireMillsec(int sessionExpireMillsec) {
	this.sessionExpireMillsec = sessionExpireMillsec;
    }

    public boolean isNeedIssueTktOffice() {
	return needIssueTktOffice;
    }

    public void setNeedIssueTktOffice(boolean needIssueTktOffice) {
	this.needIssueTktOffice = needIssueTktOffice;

	if (needIssueTktOffice) {

	    ITktExtractorLogic tktExtractorLogic = new DomTktExtractorLogic();
	    GeneralTktExtractorHandler generalTktExtractorHandler = new GeneralTktExtractorHandler(tktExtractorLogic);

	    RtInterceptorBean rtInterceptorBean = new RtInterceptorBean();
	    rtInterceptorBean.rtParserAfterHandlerList.add(generalTktExtractorHandler);

	    this.setRtInterceptorBean(rtInterceptorBean);
	}
    }

    public String getTripartCode() {
	return tripartCode;
    }

    public void setTripartCode(String tripartCode) {
	this.tripartCode = tripartCode;
    }

    public boolean isNeedERRule() {
	return needERRule;
    }

    public void setNeedERRule(boolean needERRule) {
	this.needERRule = needERRule;
    }

    public AsyncContext getActx() {
	return actx;
    }

    public void setActx(AsyncContext actx) {
	this.actx = actx;
    }

    public String getTraceId() {
	return traceId;
    }

    public void setTraceId(String traceId) {
	this.traceId = traceId;
    }

}
