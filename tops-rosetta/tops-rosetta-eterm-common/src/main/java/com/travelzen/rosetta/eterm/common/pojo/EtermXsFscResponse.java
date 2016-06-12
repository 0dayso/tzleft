package com.travelzen.rosetta.eterm.common.pojo;

/**
 * Eterm XS FSC 实时汇率
 * <p>
 * @author yiming.yan
 * @Date Mar 24, 2016
 */
public class EtermXsFscResponse {

	private boolean success;
	private String errorMsg;
	private double rate2CNY;
	private String timeStamp;
	
	public EtermXsFscResponse() {
	}
	public EtermXsFscResponse(boolean success, String errorMsg) {
		this.success = success;
		this.errorMsg = errorMsg;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public double getRate2CNY() {
		return rate2CNY;
	}
	public void setRate2CNY(double rate2cny) {
		rate2CNY = rate2cny;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	@Override
	public String toString() {
		return "EtermXsFscResponse [success=" + success + ", errorMsg="
				+ errorMsg + ", rate2CNY=" + rate2CNY + ", timeStamp="
				+ timeStamp + "]";
	}

}
