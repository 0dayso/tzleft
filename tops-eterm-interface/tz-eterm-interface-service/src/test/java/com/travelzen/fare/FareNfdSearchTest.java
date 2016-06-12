package com.travelzen.fare;

import org.junit.Test;

import com.thoughtworks.xstream.XStream;
import com.travelzen.etermface.service.abe_imitator.fare.FareSearch;
import com.travelzen.etermface.service.abe_imitator.fare.pojo.FareSearchRequest;
import com.travelzen.etermface.service.abe_imitator.fare.pojo.FareSearchResponse;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.framework.core.exception.BizException;

public class FareNfdSearchTest {

    @Test
    public void bargainFareSearch() throws SessionExpireException {

        FareSearchRequest req = new FareSearchRequest();

        // PVGSHE
        req.setFrom("PVG");
        req.setArrive("CSX");
        req.setDate("2014-03-25");
        req.setCarrier("CZ");
        // AV H /SHAPEK/23JUL/CA
        try {
            FareSearch fareSearch = new FareSearch();
            FareSearchResponse fareSearchResponse = fareSearch.bargainFareSearchWithNfnInternal(req);
            XStream xstream = new XStream();
            System.out.println(xstream.toXML(fareSearchResponse));
        } catch (Exception e) {
            e.printStackTrace();
            throw BizException.instance("from or arrive are not IATA code!");
        }
    }
}
