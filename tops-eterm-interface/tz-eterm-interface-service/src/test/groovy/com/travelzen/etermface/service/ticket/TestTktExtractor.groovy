package com.travelzen.etermface.service.ticket;

import org.junit.Before
import org.junit.Test

import com.travelzen.etermface.service.PNRParser;
import com.travelzen.etermface.service.PNRParser.FlightResult
import com.travelzen.etermface.service.PNRParser.RtInfoBean
import com.travelzen.etermface.service.entity.ParseConfBean
import com.travelzen.etermface.service.entity.RtInterceptorBean
import com.travelzen.etermface.service.handler.RtBeforeParserHandler
import com.travelzen.etermface.service.handler.impl.GeneralTktExtractorHandler;

class TestTktExtractor {

    PNRParser parser
    FlightResult flightResult = new FlightResult()
    List<RtBeforeParserHandler> rtParserHandlerList = new ArrayList()


    public TestTktExtractor() {
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
17.OSI 1E  TN/876-1234554321 1MAXIAODONG
25.BJS725'''

        String pnr = 'HMFEES'



        RtInterceptorBean rtInterceptorBean = new RtInterceptorBean();

        ITktExtractorLogic tktExtractorLogic;


        GeneralTktExtractorHandler generalTktExtractorHandler = new GeneralTktExtractorHandler(tktExtractorLogic);

        rtInterceptorBean.rtParserAfterHandlerList.add(generalTktExtractorHandler);

        RtInfoBean rtInfoBean = new RtInfoBean();
        rtInfoBean.rt = cont
        rtInfoBean.pnr = pnr

        parser.setParseConfBean(new ParseConfBean())
        parser.getParseConfBean().rtInterceptorBean = rtInterceptorBean

        parser.parseRT(rtInfoBean, rtInterceptorBean);
    }
}

