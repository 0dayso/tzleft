package com.travelzen.etermface.service;

import com.travelzen.etermface.common.config.cdxg.CdxgConstant;

public class EtermWebClientConfig {
	public int sessionExpireMillsec = CdxgConstant.BASE_LOCK_KEEP_MILLSEC;

	public String officeId = CdxgConstant.DEFAULT_OFFICE;
	

	public int getSessionExpireMillsec() {
		return sessionExpireMillsec;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public void setSessionExpireMillsec(int sessionExpireMillsec) {
		this.sessionExpireMillsec = sessionExpireMillsec;
	}
}
