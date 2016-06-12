package com.travelzen.etermface.service.fare.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.etermface.common.pojo.fare.PatFareBySegmentParams;
import com.travelzen.etermface.service.common.PNRDateFormat;

public enum SsCmdGenerator {

	;
	
	private static Logger logger = LoggerFactory.getLogger(SsCmdGenerator.class);
	
	/**
     * 将输入的参数列表转化成对应的SS命令
     *
     * @param patFareBySegmentParams
     * @param isChild
     * @return
     */
    public static String convertCommand(PatFareBySegmentParams patFareBySegmentParams, boolean isChild) {
        logger.info("生成SS指令获取报价");
    	List<String> list = new ArrayList<String>();
        String cmd = convertToBookCommand(patFareBySegmentParams, isChild);
        if (null != cmd) {
            list.add(cmd);
        } else {
        	logger.error("SS指令生成错误！");
            return null;
        }
        logger.info("生成SS指令成功：", list);
        return cmd;
    }

    private static String convertToBookCommand(PatFareBySegmentParams patFareBySegmentParams, boolean isChild) {
        if (null == patFareBySegmentParams) {
        	logger.error("SS请求参数错误，请求参数为空！");
            return null;
        }
        if (null == patFareBySegmentParams.getAirCompany() || patFareBySegmentParams.getAirCompany().isEmpty()) {
        	logger.error("SS请求参数错误，请求参数－航司为空！");
        	return null;
        }
        if (null == patFareBySegmentParams.getFlightNum() || patFareBySegmentParams.getFlightNum().isEmpty()) {
        	logger.error("SS请求参数错误，请求参数－航班号为空！");
        	return null;
        }
        if (null == patFareBySegmentParams.getCabinCode() || patFareBySegmentParams.getCabinCode().isEmpty()) {
        	logger.error("SS请求参数错误，请求参数－舱位为空！");
        	return null;
        }
        if (null == patFareBySegmentParams.getDeptDate() || patFareBySegmentParams.getDeptDate().isEmpty()) {
        	logger.error("SS请求参数错误，请求参数－始发日期为空！");
        	return null;
        }
        if (null == patFareBySegmentParams.getDeptAirport() || patFareBySegmentParams.getDeptAirport().isEmpty()) {
        	logger.error("SS请求参数错误，请求参数－始发机场为空！");
        	return null;
        }
        if (null == patFareBySegmentParams.getArrAirport() || patFareBySegmentParams.getArrAirport().isEmpty()) {
        	logger.error("SS请求参数错误，请求参数－到达机场为空！");
        	return null;
        }

        String date = PNRDateFormat.dayMonthFormat(patFareBySegmentParams.getDeptDate());
        if (null == date) {
        	logger.error("SS请求参数错误，始发日期格式错误：", patFareBySegmentParams.getDeptDate());
            return null;
        }

        StringBuffer resultBuffer = new StringBuffer();
        resultBuffer.append("SS ");
        resultBuffer.append(patFareBySegmentParams.getAirCompany());
        resultBuffer.append(patFareBySegmentParams.getFlightNum());
        resultBuffer.append(" ");
        if (isChild && !StringUtils.isBlank(patFareBySegmentParams.getBookingClass())) {
        	resultBuffer.append(patFareBySegmentParams.getBookingClass());
        } else {
            resultBuffer.append(patFareBySegmentParams.getCabinCode());
        }
        resultBuffer.append(" ");
        resultBuffer.append(date);
        resultBuffer.append(" ");
        resultBuffer.append(patFareBySegmentParams.getDeptAirport());
        resultBuffer.append(patFareBySegmentParams.getArrAirport());
        resultBuffer.append(" NN");
        resultBuffer.append(1);
        return resultBuffer.toString();
    }

}
