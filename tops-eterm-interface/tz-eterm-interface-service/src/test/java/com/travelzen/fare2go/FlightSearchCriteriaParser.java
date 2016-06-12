package com.travelzen.fare2go;

/**
 * 
 * @author hongqiang.mao
 *
 * @date 2013-5-3 下午8:24:44
 * 
 * @description flight查询条件
 */
public class FlightSearchCriteriaParser {
    /**
     * 将FlightSearchCriteria对象转化为查询条件字符串
     * 单程查询链接示例：http://接口地址?fromCity=PEK&toCity=FRA&fromDate=2011-12-31
     * 往返查询链接示例：http://接口地址?fromCity=PEK&toCity=FRA&fromDate=2011-12-20&returnDate=2011-12-31
     * 缺口程查询链接示例：http://intf.fare2go.com/data.php?trip1=BJS,BUD,2012-02-10&trip2=BUD,SHA,2012-02-12
     * @return 查询条件字符串
     */
    public static String toQueryString(FlightSearchCriteria pFlightSearchCriteria) throws CriteriaConvertException {
	if (null == pFlightSearchCriteria) {
	    throw new CriteriaConvertException("Flight Search Criteria Convert Exception, pFlightSearchCriteria can not be null");
	}
	StringBuffer lvBuffer = new StringBuffer(Const.FAREGO_SEARCH_URL);
	if (TripType.OPEN_JAW == pFlightSearchCriteria.getTripType()) {
	    if (CommonTool.emptyList(pFlightSearchCriteria.getOpenJawSearchCriteriaList())) {
		throw new CriteriaConvertException("OpenJaw Ticket Search Criteria Convert Exception, openJawSearchCriteriaList filed can not be null or empty");
	    }
	    int lvOrder = 1;
	    for (SingleOpenJawSearchCriteria tpSingleOpenJawSearchCriteria : pFlightSearchCriteria.getOpenJawSearchCriteriaList()) {
		lvBuffer.append("trip");
		lvBuffer.append(lvOrder);
		lvBuffer.append("=");
		lvBuffer.append(SingleOpenJawSearchCriteriaParser.toQueryString(tpSingleOpenJawSearchCriteria));
		lvBuffer.append("&");
		lvOrder++;
	    }
	} else {
	    if (CommonTool.emptyString(pFlightSearchCriteria.getFromCity())) {
		throw new CriteriaConvertException("Flight Search Criteria Convert Exception, fromCity filed can not be null or empty");
	    }
	    if (CommonTool.emptyString(pFlightSearchCriteria.getToCity())) {
		throw new CriteriaConvertException("Flight Search Criteria Convert Exception, toCity filed can not be null or empty");
	    }
	    if (CommonTool.emptyString(pFlightSearchCriteria.getFromDate())) {
		throw new CriteriaConvertException("Flight Search Criteria Convert Exception, fromDate filed can not be null or empty");
	    }
	    if (!CommonTool.validateDate(pFlightSearchCriteria.getFromDate())) {
		throw new CriteriaConvertException(
			"Flight Search Criteria Convert Exception, fromDate filed's format error ,the correct format likes 2013-05-04");
	    }

	    lvBuffer.append("fromCity=");
	    lvBuffer.append(pFlightSearchCriteria.getFromCity());
	    lvBuffer.append("&");

	    lvBuffer.append("toCity=");
	    lvBuffer.append(pFlightSearchCriteria.getToCity());
	    lvBuffer.append("&");

	    lvBuffer.append("fromDate=");
	    lvBuffer.append(pFlightSearchCriteria.getFromDate());
	    lvBuffer.append("&");

	    if (TripType.ROUND_TRIP == pFlightSearchCriteria.getTripType()) {
		if (CommonTool.emptyString(pFlightSearchCriteria.getReturnDate())) {
		    throw new CriteriaConvertException(
			    "Flight Search Criteria Convert Exception, in RoundTrip case , returnDate filed can not be null or empty");
		}
		if (!CommonTool.validateDate(pFlightSearchCriteria.getReturnDate())) {
		    throw new CriteriaConvertException(
			    "Flight Search Criteria Convert Exception, in RoundTrip case , returnDate filed's format error ,the correct format likes 2013-05-04");
		}

		if (0 < CommonTool.compareDate(pFlightSearchCriteria.getFromDate(), pFlightSearchCriteria.getReturnDate())) {
		    throw new CriteriaConvertException("Flight Search Criteria Convert Exception, returnDate can not be smaller than fromDate");
		}

		lvBuffer.append("returnDate=");
		lvBuffer.append(pFlightSearchCriteria.getReturnDate());
		lvBuffer.append("&");
	    }
	}

	/**
	 * 以下是可选参数
	 */

	//航空公司代码
	if (!CommonTool.emptyString(pFlightSearchCriteria.getAirCo())) {
	    lvBuffer.append("airCo=");
	    lvBuffer.append(pFlightSearchCriteria.getAirCo());
	    lvBuffer.append("&");
	}

	//乘客类型
	if (null != pFlightSearchCriteria.getPassengerType()) {
	    lvBuffer.append("passengerType=");
	    lvBuffer.append(pFlightSearchCriteria.getPassengerType().getPersistentValue());
	    lvBuffer.append("&");
	}

	//舱位类型
	if (null != pFlightSearchCriteria.getCabinType()) {
	    lvBuffer.append("seatType=");
	    lvBuffer.append(pFlightSearchCriteria.getCabinType().getPersistentValue());
	    lvBuffer.append("&");
	}

	//乘客人数
	if (0 < pFlightSearchCriteria.getAdultCount()) {
	    lvBuffer.append("adultCount=");
	    lvBuffer.append(String.valueOf(pFlightSearchCriteria.getAdultCount()));
	    lvBuffer.append("&");
	}

	//航班号
	if (!CommonTool.emptyString(pFlightSearchCriteria.getFlightNumber())) {
	    lvBuffer.append("flightNumber=");
	    lvBuffer.append(String.valueOf(pFlightSearchCriteria.getFlightNumber()));
	    lvBuffer.append("&");
	}

	//特殊价格
	if (!CommonTool.emptyString(pFlightSearchCriteria.getSpecialPrice())) {
	    lvBuffer.append("sp=");
	    lvBuffer.append(String.valueOf(pFlightSearchCriteria.getSpecialPrice()));
	}
	return lvBuffer.toString();
    }
}
