package com.travelzen.etermface.common.pojo.eterm3in1;

/**
 * Ufis pid　指令返回结果类
 * <p>
 * @author yiming.yan
 * @Date Dec 16, 2015
 */
public class UfisPidResponse implements UfisResponse {
	
	private boolean success;
	/**
	 * 政府采购查验单号
	 */
	private String gpNo;
	/**
	 * 成功打印返回文本
	 */
	private String text;
	/**
	 * 错误信息
	 */
	private String errorMsg;
	
	public UfisPidResponse() {
	}
	
	public UfisPidResponse(boolean success, String errorMsg) {
		this.success = success;
		this.errorMsg = errorMsg;
	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getGpNo() {
		return gpNo;
	}
	public void setGpNo(String gpNo) {
		this.gpNo = gpNo;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public String toString() {
		return "UfisPidResponse [success=" + success + ", gpNo=" + gpNo
				+ ", text=" + text + ", errorMsg=" + errorMsg + "]";
	}
}
