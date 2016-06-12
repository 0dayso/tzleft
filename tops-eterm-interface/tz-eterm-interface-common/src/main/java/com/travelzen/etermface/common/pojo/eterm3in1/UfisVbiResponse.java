package com.travelzen.etermface.common.pojo.eterm3in1;

/**
 * Ufis vbi　指令返回结果类
 * <p>
 * @author yiming.yan
 * @Date Dec 16, 2015
 */
public class UfisVbiResponse implements UfisResponse {
	
	private boolean success;
	/**
	 * 错误信息
	 */
	private String errorMsg;
	
	public UfisVbiResponse() {
	}
	
	public UfisVbiResponse(boolean success, String errorMsg) {
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
		return "UfisVbiResponse [success=" + success 
				+ ", errorMsg=" + errorMsg + "]";
	}

}
