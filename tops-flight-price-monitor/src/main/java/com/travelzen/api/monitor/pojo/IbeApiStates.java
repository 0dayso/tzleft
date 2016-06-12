package com.travelzen.api.monitor.pojo;

public class IbeApiStates {
	
	public static ApiState airAvailAXmlState;
	public static ApiState airAvailByFltXmlState;
	public static ApiState airBookXmlState;
	public static ApiState airResRetCompleteXmlState;
	public static ApiState airFareFlightShopIXmlState;
	
	public static void init() {
		airAvailAXmlState = new ApiState();
		airAvailByFltXmlState = new ApiState();
		airBookXmlState = new ApiState();
		airResRetCompleteXmlState = new ApiState();
		airFareFlightShopIXmlState = new ApiState();
	}
	
	public static ApiState getAirAvailAXmlState() {
		return airAvailAXmlState;
	}

	public static void setAirAvailAXmlState(ApiState airAvailAXmlState) {
		IbeApiStates.airAvailAXmlState = airAvailAXmlState;
	}

	public static ApiState getAirAvailByFltXmlState() {
		return airAvailByFltXmlState;
	}

	public static void setAirAvailByFltXmlState(ApiState airAvailByFltXmlState) {
		IbeApiStates.airAvailByFltXmlState = airAvailByFltXmlState;
	}

	public static ApiState getAirBookXmlState() {
		return airBookXmlState;
	}

	public static void setAirBookXmlState(ApiState airBookXmlState) {
		IbeApiStates.airBookXmlState = airBookXmlState;
	}

	public static ApiState getAirResRetCompleteXmlState() {
		return airResRetCompleteXmlState;
	}

	public static void setAirResRetCompleteXmlState(
			ApiState airResRetCompleteXmlState) {
		IbeApiStates.airResRetCompleteXmlState = airResRetCompleteXmlState;
	}

	public static ApiState getAirFareFlightShopIXmlState() {
		return airFareFlightShopIXmlState;
	}

	public static void setAirFareFlightShopIXmlState(
			ApiState airFareFlightShopIXmlState) {
		IbeApiStates.airFareFlightShopIXmlState = airFareFlightShopIXmlState;
	}

	@Override
	public String toString() {
		return "IbeApiStates [airAvailAXmlState=" + airAvailAXmlState
				+ ", airAvailByFltXmlState=" + airAvailByFltXmlState
				+ ", airBookXmlState=" + airBookXmlState
				+ ", airResRetCompleteXmlState=" + airResRetCompleteXmlState
				+ ", airFareFlightShopIXmlState=" + airFareFlightShopIXmlState
				+ "]";
	}
	
	public static void main(String[] args) {
		System.out.println(new IbeApiStates());
		IbeApiStates.init();
		System.out.println(new IbeApiStates());
	}

}
