package com.travelzen.etermface.common.pojo;

/**
 * 自动出票返回结果
 * 
 * @variable success true/false
 * @variable errorMsg　success为true时，本字段为null
 */
public class EtdzResponse {
	
	private boolean success;
	private String errorMsg;
	
	public EtdzResponse() {
	}
	
	public EtdzResponse(boolean success, String errorMsg) {
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

	@Override
	public String toString() {
		return "EtdzResponse [success=" + success + ", errorMsg=" + errorMsg
				+ "]";
	}

}
