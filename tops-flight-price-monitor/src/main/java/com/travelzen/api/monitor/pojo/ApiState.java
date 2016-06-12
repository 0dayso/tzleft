package com.travelzen.api.monitor.pojo;

public class ApiState {
	
	private boolean success;
	private String result;
	private String error;
	
	public boolean isSuccess() {
		return success;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public String getResult() {
		return result;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public String getError() {
		return error;
	}
	
	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "IbeApiState [success=" + success + ", result=" + result
				+ ", error=" + error + "]";
	}

}
