package com.travelzen.fare2go;


/**
 * @author hongqiang.mao
 *
 * @date 2013-5-4 下午2:14:24
 *
 * @description 
 */
public enum TripType  {
	/**
	 * 单程
	 */
	ONE_WAY("OW"),
	/**
	 * 往返
	 */
	ROUND_TRIP("RT"),
	
	/**
	 * 缺口程
	 */
	OPEN_JAW("OJ")
	;
	
	private String tripType;
	private TripType(String tripType){
		this.tripType = tripType;
	}

    public String getPersistentValue() {
        return this.tripType;
    }
}
