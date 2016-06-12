package com.travelzen.fare2go;

import java.util.List;

import com.thoughtworks.xstream.XStream;

/**
 * @author hongqiang.mao
 *
 * @date 2013-5-7 下午7:12:21
 *
 * @description
 */
public class PnrFareParser {
	/**
	 * 
	 * @param pXml
	 * @return
	 */
	public static PnrFare convertXMLToPnrFare(String pXml){
		if(CommonTool.emptyString(pXml)){
			return null;
		}
		XStream lvXStream = new XStream();
		lvXStream.alias("Unilink_MakeFareByPNR", PnrFare.class);
		lvXStream.alias("Fare_Tax", FareTax.class);
		lvXStream.alias("FlightInfo", List.class);
		lvXStream.alias("FlightSegment", FlightSegment.class);
		
		lvXStream.aliasField("PNR", PnrFare.class, "pnr");
		lvXStream.aliasField("Fare_Tax", PnrFare.class, "fareTax");
		lvXStream.aliasField("FlightInfo", PnrFare.class, "flightSegmentList");
		
		lvXStream.aliasField("FareNo", FareTax.class, "fareNo");
		lvXStream.aliasField("PriceNo", FareTax.class, "priceNo");
		lvXStream.aliasField("StandPrice", FareTax.class, "standPrice");
		lvXStream.aliasField("Fare", FareTax.class, "fare");
		lvXStream.aliasField("Tax", FareTax.class, "tax");
		lvXStream.aliasField("QValue", FareTax.class, "qValue");
		lvXStream.aliasField("QCHDValue", FareTax.class, "qCHDValue");
		lvXStream.aliasField("CHDFare", FareTax.class, "chdFare");
		lvXStream.aliasField("CHDFareIncludeCommision", FareTax.class, "chdFareIncludeCommision");
		lvXStream.aliasField("CHDTax", FareTax.class, "chdTax");
		lvXStream.aliasField("PriceIncludeCommision", FareTax.class, "priceIncludeCommision");
		lvXStream.aliasField("Passenger", FareTax.class, "passenger");
		lvXStream.aliasField("AirCo", FareTax.class, "airCo");
		lvXStream.aliasField("Ticket_Limit", FareTax.class, "ticketLimit");
		lvXStream.aliasField("IsSp", FareTax.class, "isSp");
		lvXStream.aliasField("SpNo", FareTax.class, "spNo");

		
		lvXStream.aliasField("FromCity", FlightSegment.class, "fromCity");
		lvXStream.aliasField("DestCity", FlightSegment.class, "toCity");
		lvXStream.aliasField("FromDate", FlightSegment.class, "fromDate");
		lvXStream.aliasField("FromTime", FlightSegment.class, "fromTime");
		lvXStream.aliasField("DestTime", FlightSegment.class, "toTime");
		lvXStream.aliasField("FlightNumber", FlightSegment.class, "flightNumber");
		lvXStream.aliasField("CW", FlightSegment.class, "seatType");
		lvXStream.aliasField("IsCodeShare", FlightSegment.class, "isCodeShare");
		
		
		PnrFare lvPNRFare = (PnrFare)lvXStream
				.fromXML(pXml);;
		return lvPNRFare;
	}
}
