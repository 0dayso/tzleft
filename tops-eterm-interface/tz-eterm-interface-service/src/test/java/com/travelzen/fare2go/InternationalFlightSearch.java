package com.travelzen.fare2go;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.travelzen.etermface.service.entity.PnrRet.Flight;

/**
 * @author hongqiang.mao
 * 
 * @date 2013-5-3 下午5:27:10
 */
public class InternationalFlightSearch {
    private final static Logger LOG = LoggerFactory.getLogger(InternationalFlightSearch.class);

    /**
     * 根据指定条件查询国际机票信息
     * 
     * @param pSearchCriteria
     * @return
     * @throws CriteriaConvertException
     */
    public static List<AirRouteInfo> searchFlight(FlightSearchCriteria pSearchCriteria) {
        String lvUrl = null;
        try {
            lvUrl = FlightSearchCriteriaParser.toQueryString(pSearchCriteria);
        } catch (CriteriaConvertException e) {
            LOG.error(e.toString());
            e.printStackTrace();
            return null;
        }
        if (lvUrl.endsWith("&")) {
            lvUrl = lvUrl.substring(0, lvUrl.length() - 1);
        }
        System.out.println(lvUrl);
        HttpRequest httpRequest = new HttpRequest(lvUrl);
        String lvResponse = httpRequest.sendGetRequest();
        LOG.info(lvResponse);
        System.out.println(lvResponse);
        List<AirRouteInfo> lvResult = FlightSearchResultParser.convertXMLToFlightSearchResult(lvResponse);
        LOG.info(lvResult.toString());
        return lvResult;
    }

    /**
     * 根据PNR生成价格
     * @param pPnrNo
     * @return
     */
    public static PnrFare getFareByPnr(String pPnrNo){
        pPnrNo = pPnrNo +"/byc1;byc11234";
        String lvResponse = HttpRequest.sendRequest(Const.MAKE_FARE_BY_PNR_URL,pPnrNo,HttpRequestType.POST);
        PnrFare lvPNRFare = PnrFareParser.convertXMLToPnrFare(lvResponse);
        return lvPNRFare;
    }
    public static void main(String[] args) {
        FlightSearchCriteria pSearchCriteria = getFlightSearchCriteria();
//        List<AirRouteInfo> rs = searchFlight(pSearchCriteria);
        PnrFare pnrFare = getFareByPnr("JDGPQ0");
        XStream xstream = new XStream();
         System.out.println(xstream.toXML(pnrFare));
//        try {
//            FileWriter fw = new FileWriter(new File("/media/B634186934182F3D/fare2go1.xml"));
//            fw.write(xstream.toXML(rs));
//            fw.flush();
//            fw.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    public static FlightSearchCriteria getFlightSearchCriteria() {

        FlightSearchCriteria pSearchCriteria = new FlightSearchCriteria();

        pSearchCriteria.setFromCity("PVG");
        pSearchCriteria.setToCity("NRT");
        pSearchCriteria.setFromDate("2013-08-22");
        pSearchCriteria.setReturnDate("2013-08-28");
        pSearchCriteria.setTripType(TripType.ROUND_TRIP);
        pSearchCriteria.setFlightNumber("CA929,CA920");
        return pSearchCriteria;
    }

    public static FlightSearchCriteria getFlightSearchCriteria(List<Flight> flights) {

        FlightSearchCriteria pSearchCriteria = new FlightSearchCriteria();
        if (flights == null) {
        }
        if (flights.size() > 2) {
            List<SingleOpenJawSearchCriteria> singleOpenJawSearchCriterias = getSingleOpenJawSearchCriteria(flights);
            pSearchCriteria.setTripType(TripType.OPEN_JAW);
            pSearchCriteria.setOpenJawSearchCriteriaList(singleOpenJawSearchCriterias);
        } else if (flights.size() == 2) {
            
            Flight flight = flights.get(0);
            Flight flight1 = flights.get(1);
            String fStart = flight.BoardPoint;
            String fEnd = flight.OffPoint;
            String rStart = flight1.BoardPoint;
            String rEnd = flight1.OffPoint;
            if (fStart.equals(rEnd) && fEnd.equals(rStart)) {
                pSearchCriteria.setFromCity(flight.BoardPoint);
                pSearchCriteria.setToCity(flight.OffPoint);
                pSearchCriteria.setFromDate(flight.DepartureDate);
                pSearchCriteria.setReturnDate(flight1.DepartureDate);
                pSearchCriteria.setTripType(TripType.ROUND_TRIP);
                pSearchCriteria
                        .setFlightNumber(flight.Carrier + flight.Flight + "," + flight1.Carrier + flight1.Flight);
            } else {
                List<SingleOpenJawSearchCriteria> singleOpenJawSearchCriterias = getSingleOpenJawSearchCriteria(flights);
                pSearchCriteria.setTripType(TripType.OPEN_JAW);
                pSearchCriteria.setOpenJawSearchCriteriaList(singleOpenJawSearchCriterias); }
        } else {
            Flight flight = flights.get(0);
            pSearchCriteria.setFromCity(flight.BoardPoint);
            pSearchCriteria.setToCity(flight.OffPoint);
            pSearchCriteria.setFromDate(flight.DepartureDate);
            pSearchCriteria.setTripType(TripType.ONE_WAY);
            pSearchCriteria.setFlightNumber(flight.Carrier + flight.Flight);
        }
        return pSearchCriteria;
    }

    public static List<SingleOpenJawSearchCriteria> getSingleOpenJawSearchCriteria(List<Flight> flights) {
        List<SingleOpenJawSearchCriteria> singleOpenJawSearchCriterias = new ArrayList<SingleOpenJawSearchCriteria>();
        for (Flight flight : flights) {
            SingleOpenJawSearchCriteria singleOpenJawSearchCriteria = new SingleOpenJawSearchCriteria();
            singleOpenJawSearchCriteria.setFromCity(flight.BoardPoint);
            singleOpenJawSearchCriteria.setToCity(flight.OffPoint);
            singleOpenJawSearchCriteria.setFromDate(flight.DepartureDate);
            singleOpenJawSearchCriterias.add(singleOpenJawSearchCriteria);
        }

        return singleOpenJawSearchCriterias;
    }
}
