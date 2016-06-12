package com.travelzen.controller.pnr;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import com.travelzen.etermface.service.QteBySegmentsParser;
import com.travelzen.etermface.service.entity.FlightParam;
import com.travelzen.etermface.service.entity.QteBySegmentsParam;

public class TestQteBySegments {
    public static void main(String[] args) {

//     QteBySegmentsParser.getQTotaolValueFromNuc(
//             "03JUL14YYC Q0l01 AC YTO Q3.00Q20.00ABC/.T  Q SHATYO57.07 1308.00CAD1331.00END ROE123   ", 1);
        ServletContext servletContext = new MockServletContext();
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                "classpath*:spring/eterm-interface-appctx.xml", "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);
        // 2 2. TG663 H FR22AUG PVGBKK HK1 0730 1105 SEAME
        // S MU 596M05OCT HKG1700 1855HGH0S
        List<FlightParam> qteParams = new ArrayList<>();
        FlightParam pPatParam1 = new FlightParam();
        pPatParam1.setAirCompany("MU");
        pPatParam1.setFlightNum("596");
        pPatParam1.setDepatureDate("2014-10-05");
        pPatParam1.setArriveDate("2014-10-05");
        pPatParam1.setFromAirPort("HKG");
        pPatParam1.setToAirPort("HGH");
        pPatParam1.setSmallCabin("M");
        pPatParam1.setDepatureTime("17:00");
        pPatParam1.setArriveTime("18:55");
        qteParams.add(pPatParam1);
        // 3. LH3380 V SU24AUG DUSLHR HK1 0700 0725 SEAME
//     FlightParam pPatParam2 = new FlightParam();
//     pPatParam2.setAirCompany("LH");
//     pPatParam2.setFlightNum("3380");
//     pPatParam2.setDepatureDate("2014-08-24");
//     pPatParam2.setArriveDate("2014-08-24");
//     pPatParam2.setFromAirPort("DUS");
//     pPatParam2.setToAirPort("LHR");
//     pPatParam2.setSmallCabin("V");
//     pPatParam2.setDepatureTime("07:00");
//     pPatParam2.setArriveTime("07:25");
//     qteParams.add(pPatParam2);

     QteBySegmentsParser seatNumParser = new QteBySegmentsParser();
     QteBySegmentsParam qteBySegmentsParam = new QteBySegmentsParam();
     qteBySegmentsParam.getTicketingAirlineCompany().add("MU");
     qteBySegmentsParam.setPassengers(new ArrayList<String>());
      qteBySegmentsParam.getPassengers().add("ADT");
      qteBySegmentsParam.getPassengers().add("CHD");
      qteBySegmentsParam.getPassengers().add("INF");
     qteBySegmentsParam.setQteParams(qteParams);
     String qteBySegmentsParamString = QteBySegmentsParam.convertToXML(qteBySegmentsParam);
     System.out.println(qteBySegmentsParamString);
     System.out.println(seatNumParser.getQteResultBySegementsParam(qteBySegmentsParamString));
 
   }
}
