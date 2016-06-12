package com.travelzen.etermface

import com.travelzen.etermface.service.FlightSegmentPatParser
import org.junit.Before
import org.junit.Test

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.naming.NoNameCoder
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter
import com.travelzen.etermface.service.entity.FlightSegments
import com.travelzen.etermface.service.entity.ParseConfBean

class TestFlightSegmentsPatParser {


	FlightSegmentPatParser parser = new FlightSegmentPatParser();


	@Before void setUp() {
		//		rtParserHandlerList.add( new TicketExtractorHandler() )
	}



	@Test
	void process() {
		
		XStream xstream = new XStream()
		xstream.processAnnotations(FlightSegments.class)
		
		FlightSegments fs = new FlightSegments();
		fs.flightList = new ArrayList();
		
		FlightSegments.Flight flt  = new FlightSegments.Flight();
		flt.ActionCode ="LL"
		flt.Carrier="HO"
		flt.Flight="1123"
		flt.BoardPoint="SHA"
		
		fs.flightList.add(flt);
		
		StringWriter writer = new StringWriter();
		xstream.marshal(fs, new PrettyPrintWriter(writer, new NoNameCoder()));

		String xml = writer.toString();
		
		println  xml;
		
		
		
		String reqInfo ='''<Flights><Flight ElementNo="1" ID="1" Type="0" Carrier="HO" Flight="1123" BoardPoint="SHA" OffPoint="CSX" DepartureDate="2013-08-28" Class="Q" ActionCode="LL"/>
												<Flight ElementNo="1" ID="1" Type="0" Carrier="HO" Flight="1556" BoardPoint="CSX" OffPoint="SHA" DepartureDate="2013-08-29" Class="Q" ActionCode="LL"/>
  </Flights>'''


		FlightSegments flightSegments =  (FlightSegments) xstream.fromXML(reqInfo)

		ParseConfBean parseConfBean  = new ParseConfBean();
		
		parseConfBean.isDomestic = true;

		println parser.getByFlightSegmentPAT(parseConfBean, flightSegments);
	}
}

