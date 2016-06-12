package com.travelzen.etermface.common.consts;

/**
 * Description
 * <p>
 * @author yiming.yan
 * @Date Mar 29, 2016
 */
public enum EtermError {
	
	NULL_RETURN("", "指令返回结果为空"),
	
	CDXG_SESSION_EXPIRE("", "请求CDXG超时"),
	
	UFIS_EXCEPTION("", "Ufis服务器异常");
	
	String errorCode;
	String errorMsg;
	
	EtermError(String errorCode, String errorMsg) {
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

}
