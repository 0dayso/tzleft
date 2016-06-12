package com.travelzen.fare2go;

/**
 * @author hongqiang.mao
 * 
 * @date 2013-5-4 下午1:37:58
 */
public enum CabinType {
	/**
	 * 经济舱：Y
	 */
	Y("Y"),
	/**
	 * 商务舱：C
	 */
	C("C"),
	/**
	 * 头等舱：F
	 */
	F("F");
	private String cabinType;

	private CabinType(String cabinType) {
		this.cabinType = cabinType;
	}


    public String getPersistentValue() {
        return this.cabinType;
    }
}
