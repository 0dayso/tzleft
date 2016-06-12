package com.travelzen.etermface.service.entity;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * @author hongqiang.mao
 *
 * @date 2013-6-20 下午5:18:53
 *
 * @description
 */
@XStreamAlias("PnrOpResult")
public class PnrOpResult {
	private String returnCode;
	private String message;
	
	//是否出票
	private boolean issue;
	public String getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isIssue() {
		return issue;
	}
	public void setIssue(boolean issue) {
		this.issue = issue;
	}
	
	
	@Override
	public String toString() {
		return "PnrOpResult [returnCode=" + returnCode + ", message=" + message + ", issue=" + issue + "]";
	}
	/**
	 * 转化成XML
	 * @return
	 */
	public String toXML(){
		StaxDriver sd = new StaxDriver(new NoNameCoder());
		XStream xstream = new XStream(sd);
		xstream.processAnnotations(PnrOpResult.class);
		return xstream.toXML(this);
	}
}
