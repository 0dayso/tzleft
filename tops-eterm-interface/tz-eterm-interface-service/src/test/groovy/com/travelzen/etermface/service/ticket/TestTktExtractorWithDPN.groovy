package com.travelzen.etermface.service.ticket;


import org.junit.Before
import org.junit.Test

import com.travelzen.etermface.service.PNRParser
import com.travelzen.etermface.service.PNRParser.FlightResult
import com.travelzen.etermface.service.PNRParser.RtInfoBean
import com.travelzen.etermface.service.entity.ParseConfBean
import com.travelzen.etermface.service.entity.RtInterceptorBean
import com.travelzen.etermface.service.handler.RtBeforeParserHandler
import com.travelzen.etermface.service.handler.impl.GeneralTktExtractorHandler;

class TestTktExtractorWithDPN {


    PNRParser parser
    FlightResult flightResult = new FlightResult()
    List<RtBeforeParserHandler> rtParserHandlerList = new ArrayList()


    public TestTktExtractorWithDPN() {
        parser = new PNRParser()
    }


    @Before
    void setUp() {
        rtParserHandlerList.add(new TicketExtractorHandler())
    }


    @Test
    void additionIsWorking() {
        String cont = '''**ELECTRONIC TICKET PNR**
 1.WANG/SHU LIN 2.ZHANG/FU SHENG HMFEES 
 3.  MH371  O   WE03JUL  PEKKUL HK2   0900 1510          E  
 4.  MH370  O   SU07JUL  KULPEK HK2   0025 0635          E  
10.SSR OTHS 1E AUTO XX IF SSR TKNA/E/M/C NOT RCVD BY MH BY 1600/11JUN/ BJS LT   11.SSR TKNE MH HK1 PEKKUL 371 O03JUL 2323982069277/1/P2                        
12.SSR TKNE HU XX1 HRBCGO 7292 Q13JAN 8804609135160/1/DPN1178
13.SSR TKNE MH HK1 KULPEK 370 O07JUL 2323982069276/2/P1 
14.SSR TKNE MH HK1 KULPEK 370 O07JUL 2323982069277/2/P2 
16.SSR DOCS   
17.OSI CTC    
18.RMK TJ SHA255
19.RMK MH/W2MZ7 
20.RMK TJ AUTH SHA255   
21.FN/A/FCNY1630.00/SCNY1630.00/C7.00/XCNY1600.00/TCNY90.00CN/TCNY132.00MY/ 
    TCNY1378.00XT/ACNY3230.00   
22.TN/232-3982069276/P1                                                        
23.TN/232-3982069277/P2                                                        
24.FP/CASH,CNY  
25.BJS725'''

        String pnr = 'HMFEES'

        ParseConfBean parseConfBean = new ParseConfBean();
        parseConfBean.isDomestic = true;
        parseConfBean.isDomestic = false;

        parser.setParseConfBean(parseConfBean);

        RtInterceptorBean rtInterceptorBean = new RtInterceptorBean();

        ITktExtractorLogic tktExtractorLogic;

        if (parseConfBean.isDomestic) {
            tktExtractorLogic = new DomTktExtractorLogic();
        } else {
            tktExtractorLogic = new IntTktExtractorLogic();
        }

        GeneralTktExtractorHandler generalTktExtractorHandler = new GeneralTktExtractorHandler(tktExtractorLogic);

        rtInterceptorBean.rtParserAfterHandlerList.add(generalTktExtractorHandler);

        RtInfoBean rtInfoBean = new RtInfoBean();

        rtInfoBean.rt = cont
        rtInfoBean.pnr = pnr
        parser.parseRT(rtInfoBean, rtInterceptorBean);
    }
}

