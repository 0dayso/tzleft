package com.travelzen.fare2go;

import java.util.List;

import com.thoughtworks.xstream.XStream;


/**
 * @author hongqiang.mao
 * 
 * @date 2013-5-6 上午10:52:18
 * 
 * @description
 */
public class FlightSearchResultParser {
	@SuppressWarnings("unchecked")
	public static List<AirRouteInfo> convertXMLToFlightSearchResult(String pXml) {
		if(CommonTool.emptyString(pXml)){
			return null;
		}
		XStream lvXStream = new XStream();
		lvXStream.alias("RS", List.class);
		lvXStream.alias("R", AirRouteInfo.class);
		lvXStream.alias("SS", List.class);
		lvXStream.alias("S", FlightSegment.class);
		lvXStream.alias("FS", List.class);
		lvXStream.alias("F", FlightInfo.class);

		lvXStream.aliasField("F", AirRouteInfo.class, "fromCity");
		lvXStream.aliasField("T", AirRouteInfo.class, "toCity");
		lvXStream.aliasField("A", AirRouteInfo.class, "airCo");
		lvXStream.aliasField("M", AirRouteInfo.class, "totalFare");
		lvXStream.aliasField("PM", AirRouteInfo.class, "newPrice");
		lvXStream.aliasField("X", AirRouteInfo.class, "totalTax");
		lvXStream.aliasField("CM", AirRouteInfo.class, "childPrice");
		lvXStream.aliasField("CPM", AirRouteInfo.class, "childButtomPrice");
		lvXStream.aliasField("C1", AirRouteInfo.class, "theAirlineAgencyFee");
		lvXStream.aliasField("C2", AirRouteInfo.class,
				"multimodalTransportAgencyFee");
		lvXStream.aliasField("Z", AirRouteInfo.class, "totalTransferCount");
		lvXStream.aliasField("ZZ", AirRouteInfo.class, "transferCount");
		lvXStream.aliasField("N", AirRouteInfo.class, "stayOverNight");
		lvXStream.aliasField("L", AirRouteInfo.class, "limit");
		lvXStream.aliasField("SP", AirRouteInfo.class, "specialPrice");
		lvXStream.aliasField("TP", AirRouteInfo.class, "tp");
		lvXStream.aliasField("TF", AirRouteInfo.class, "tf");
		lvXStream.aliasField("FC", AirRouteInfo.class, "owSegmentNumber");
		lvXStream.aliasField("MS", AirRouteInfo.class, "ms");
		lvXStream.aliasField("G", AirRouteInfo.class, "gds");
		lvXStream.aliasField("PT", AirRouteInfo.class, "passengerType");
		lvXStream.aliasField("V", AirRouteInfo.class, "needVisa");
		lvXStream.aliasField("CL", AirRouteInfo.class, "rescheduledLevel");
		lvXStream.aliasField("RL", AirRouteInfo.class, "refundLevel");
		lvXStream.aliasField("LD", AirRouteInfo.class, "lastDay");
		lvXStream.aliasField("LCC", AirRouteInfo.class, "lcc");
		lvXStream.aliasField("SS", AirRouteInfo.class, "flightSegmentList");

		lvXStream.aliasField("F", FlightSegment.class, "fromCity");
		lvXStream.aliasField("T", FlightSegment.class, "toCity");
		lvXStream.aliasField("A", FlightSegment.class, "airCo");
		lvXStream.aliasField("No", FlightSegment.class, "flightNumber");
		lvXStream.aliasField("FA", FlightSegment.class, "fromAirport");
		lvXStream.aliasField("TA", FlightSegment.class, "toAirport");
		lvXStream.aliasField("FD", FlightSegment.class, "fromDate");
		lvXStream.aliasField("FT", FlightSegment.class, "fromTime");
		lvXStream.aliasField("TD", FlightSegment.class, "toDate");
		lvXStream.aliasField("TT", FlightSegment.class, "toTime");
		lvXStream.aliasField("ST", FlightSegment.class, "seatType");
		lvXStream.aliasField("M", FlightSegment.class, "segmentPrice");
		lvXStream.aliasField("DP", FlightSegment.class, "departure");
		lvXStream.aliasField("FP", FlightSegment.class, "segmentSerialNumber");
		lvXStream.aliasField("FS", FlightSegment.class, "flightInfoList");

		lvXStream.aliasField("No", FlightInfo.class, "flightNumber");
		lvXStream.aliasField("FA", FlightInfo.class, "fromAirport");
		lvXStream.aliasField("TA", FlightInfo.class, "toAirport");
		lvXStream.aliasField("A", FlightInfo.class, "airCo");
		lvXStream.aliasField("ET", FlightInfo.class, "equipType");
		lvXStream.aliasField("FD", FlightInfo.class, "fromDate");
		lvXStream.aliasField("FT", FlightInfo.class, "fromTime");
		lvXStream.aliasField("TD", FlightInfo.class, "toDate");
		lvXStream.aliasField("TT", FlightInfo.class, "toTime");
		lvXStream.aliasField("D", FlightInfo.class, "duration");
		lvXStream.aliasField("ST", FlightInfo.class, "cabinRemainingCount");
		lvXStream.aliasField("V", FlightInfo.class, "codeShareFlight");
		lvXStream.aliasField("T", FlightInfo.class, "stop");
		lvXStream.aliasField("IFT", FlightInfo.class, "leaveTerminal");
		lvXStream.aliasField("IDT", FlightInfo.class, "arriveTerminal");
		lvXStream.aliasField("IST", FlightInfo.class, "stopAirport");
		lvXStream.aliasField("STIME", FlightInfo.class, "stopTime");

		List<AirRouteInfo> lvResult = (List<AirRouteInfo>) lvXStream
				.fromXML(pXml);
		return lvResult;
	}
}
