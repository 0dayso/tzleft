package com.travelzen.etermface.service.segprice;

public class ProcResult {
	
	boolean  needMakePrice = false ;

	public ProcResult(boolean needMakePrice) {
		super();
		this.needMakePrice = needMakePrice;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public boolean isNeedMakePrice() {
		return needMakePrice;
	}

	public void setNeedMakePrice(boolean needMakePrice) {
		this.needMakePrice = needMakePrice;
	}

}
