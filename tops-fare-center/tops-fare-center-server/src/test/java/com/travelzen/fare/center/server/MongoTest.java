package com.travelzen.fare.center.server;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.common.ufis.util.UfisException;
import com.travelzen.fare.center.mongo.morphia.FlightMorphia;
import com.travelzen.rosetta.eterm.common.pojo.EtermAvResponse;
import com.travelzen.rosetta.eterm.common.pojo.av.Flight;
import com.travelzen.rosetta.eterm.common.pojo.enums.EtermCmdSource;
import com.travelzen.rosetta.eterm.parser.EtermAvParser;

/**
 * @author yiming.yan
 * @Date Nov 26, 2015
 */
public class MongoTest {
	
	@Ignore
	@Test
	public void insert() {
		EtermUfisClient client = new EtermUfisClient();
		String result = null;
		try {
			result = client.executeCmdAv("AV:H/PVGLAX/21JUN16/D", 5);
		} catch (UfisException e) {
			e.printStackTrace();
		} finally {
			client.close();
		}
		System.out.println(result);
		EtermAvResponse response = EtermAvParser.parse(result, EtermCmdSource.UFIS);
		System.out.println(response);
		FlightMorphia.INSTANCE.deleteAll();
		FlightMorphia.INSTANCE.insert(response.getFlights());
	}
	
//	@Ignore
	@Test
	public void find() {
		List<Flight> flights = FlightMorphia.INSTANCE.find("2016-06-21", "PVG", "LAX");
		System.out.println(flights.size());
		flights = FlightMorphia.INSTANCE.find("2016-06-21", "PVG", "LAX", "DL");
		System.out.println(flights.size());
	}

}
