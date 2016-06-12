package com.travelzen.etermface.client.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.travelzen.etermface.common.pojo.fare.NfdFareRequest;
import com.travelzen.etermface.common.pojo.fare.NfdFareResponse.NfdInfo;
import com.travelzen.etermface.common.pojo.fare.NfdFareResponseNew;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class NfdFareTest {
	
    @Test
    public void nfdTest() throws IOException {
        NfdFareRequest req = new NfdFareRequest();
        req.setDeptAirport("SHA");
        req.setArrAirport("PEK");
        req.setCarrier("CA");
        req.setDeptDate("2015-11-18");
        req.setNeedNfn01(true);

        // dev
        String host = "http://127.0.0.1:8080";
        // op
//		String host = "http://192.168.160.183:8080";
        // op3
//        String host = "http://192.168.161.87:8880";
        NfdFareResponseNew nfdFareSearchResponse = NfdFareClient.getDomesticNfd(host, req);
        System.out.println(nfdFareSearchResponse);
        Gson gson = new Gson();
        List<NfdInfo> nfdInfos = gson.fromJson(nfdFareSearchResponse.getNfdInfos(), new TypeToken<List<NfdInfo>>(){}.getType());
        System.out.println(nfdInfos.size());
        for (NfdInfo nfdInfo:nfdInfos) {
        	System.out.println(nfdInfo);
        }
        System.out.println("---------");
    }

}
