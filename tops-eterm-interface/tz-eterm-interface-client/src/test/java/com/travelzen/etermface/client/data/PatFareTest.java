package com.travelzen.etermface.client.data;

import com.travelzen.etermface.common.pojo.PassengerType;
import com.travelzen.etermface.common.pojo.fare.PatFareBySegmentParams;
import com.travelzen.etermface.common.pojo.fare.PatFareRequest;
import com.travelzen.etermface.common.pojo.fare.PatFareResponse;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PatFareTest {
	
	@Ignore
    @Test
    public void patTest() throws IOException {
    	PatFareRequest req = new PatFareRequest();
        req.setPnr("JXPKW3");
        List<PassengerType> passengerTypes = new ArrayList<PassengerType>();
        passengerTypes.add(PassengerType.ADT);
        passengerTypes.add(PassengerType.CHD);
        passengerTypes.add(PassengerType.INF);
     // is Gov?
        passengerTypes.add(PassengerType.GOV);
        req.setPassengerTypes(passengerTypes);

        // dev
        String host = "http://127.0.0.1:8080";
        //op
//		String host = "http://192.168.160.183:8080";
        // op3
//		String host = "http://192.168.161.87:8880";
        PatFareResponse patFareSearchResponse = PatFareClient.getPat(host, req);
        System.out.println(patFareSearchResponse);
        System.out.println("---------");
    }
    
    @Test
    public void patBySegmentTest() throws IOException {
    	PatFareRequest req = new PatFareRequest();
    	PatFareBySegmentParams params = new PatFareBySegmentParams();
    	params.setAirCompany("CA");
    	params.setFlightNum("1893");
    	params.setCabinCode("V");
    	params.setDeptDate("2015-05-01");
    	params.setDeptAirport("PVG");
    	params.setArrAirport("SZX");;
    	req.setPatFareBySegmentParams(params);
        List<PassengerType> passengerTypes = new ArrayList<PassengerType>();
        passengerTypes.add(PassengerType.ADT);
        passengerTypes.add(PassengerType.CHD);
        passengerTypes.add(PassengerType.INF);
     // is Gov?
        passengerTypes.add(PassengerType.GOV);
        req.setPassengerTypes(passengerTypes);

        // dev
        String host = "http://127.0.0.1:8080";
        //op
//		String host = "http://192.168.160.183:8080";
        // op3
//		String host = "http://192.168.161.87:8880";
        PatFareResponse patFareSearchResponse = PatFareClient.getPatBySegment(host, req);
        System.out.println(patFareSearchResponse);
        System.out.println("---------");
    }

}
