package com.travelzen.etermface.service.createpnr;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

public class CreatePnrException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	private CreatePnrReturnCode retCode;
    private Object[] objects;


    public CreatePnrException(CreatePnrReturnCode retCode) {
		super();
		this.retCode = retCode;
	}

	@Override
    public String toString() {
        return String.format("[retCode=%s,retMsg=%s]", retCode.getErrorCode(), retCode.getErrorDetail());
    }

    @Override
    public String getMessage() {
        return String.format("%s", Arrays.deepToString(objects));
    }

    public String getMessage(String format, String separator) {
        return String.format(format, StringUtils.join(objects, separator));
    }

    public String getMessage(String format) {
        return String.format(format, objects);
    }

}
