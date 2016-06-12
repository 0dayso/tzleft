package com.travelzen.fare2go;



/**
 * @author hongqiang.mao
 *
 * @date 2013-5-4 下午2:26:25
 *
 * @description 单行程缺口票查询条件
 */
public class SingleOpenJawSearchCriteriaParser {
	/**
	 * 转化为查询条件字符串
	 * @return
	 */
	public static String toQueryString(SingleOpenJawSearchCriteria pSingleOpenJawSearchCriteria) throws CriteriaConvertException{
		if(null == pSingleOpenJawSearchCriteria){
			throw new CriteriaConvertException("OpenJaw Ticket Search Criteria Convert Exception, pSingleOpenJawSearchCriteria can not be null");
		}
		if(CommonTool.emptyString(pSingleOpenJawSearchCriteria.getFromCity())){
			throw new CriteriaConvertException("OpenJaw Ticket Search Criteria Convert Exception, fromCity filed can not be null or empty");
		}
		if(CommonTool.emptyString(pSingleOpenJawSearchCriteria.getToCity())){
			throw new CriteriaConvertException("OpenJaw Ticket Search Criteria Convert Exception, toCity filed can not be null or empty");
		}
		if(CommonTool.emptyString(pSingleOpenJawSearchCriteria.getFromDate())){
			throw new CriteriaConvertException("OpenJaw Ticket Search Criteria Convert Exception, fromDate filed can not be null or empty");
		}
		if(!CommonTool.validateDate(pSingleOpenJawSearchCriteria.getFromDate())){
			throw new CriteriaConvertException("OpenJaw Ticket Search Criteria Convert Exception, fromDate filed's format error ,the correct format likes 2013-05-04");
		}
		StringBuffer lvBuffer = new StringBuffer();
		lvBuffer.append(pSingleOpenJawSearchCriteria.getFromCity());
		lvBuffer.append(",");
		lvBuffer.append(pSingleOpenJawSearchCriteria.getToCity());
		lvBuffer.append(",");
		lvBuffer.append(pSingleOpenJawSearchCriteria.getFromDate());
		return lvBuffer.toString();
	}
}
