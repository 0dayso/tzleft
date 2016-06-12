package com.travelzen.etermface.common.pojo.eterm3in1;

/**
 * Ufis 指令返回结果接口
 * <p>
 * @author yiming.yan
 * @Date Dec 16, 2015
 */
public interface UfisResponse {
	
	boolean isSuccess();
	void setSuccess(boolean success);
	String getErrorMsg();
	void setErrorMsg(String errorMsg);
	
	String toString();

}
