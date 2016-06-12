package com.travelzen.etermface.postcontent


import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.travelzen.etermface.service.PNRParser
import com.travelzen.etermface.service.constant.PnrParserConstant;
import com.travelzen.etermface.service.entity.ParseConfBean

/**
 * @author liang.wang
 *
 */
class Uapi5 {

    private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

    static main(args) {

        def pnrContent =

                ''' RTJPRZ0K
 1.问丽 JPRZ0K                                                                  
 2.  CA1621 B   FR18APR  PEKHRB HK1   1905 2100          E T3--                 
 3.HRB/T HRB/T 0451-55116877/HRB HONGQI SHANGWU SERVICE LTD.,CO/WEN LI HUA      
     ABCDEFG                                                                    
 4.55116877                                                                     
 5.TL/1800/18APR/HRB192                                                         
 6.SSR FOID CA HK1 NI65441/P1                                                   
 7.SSR ADTK 1E BY HRB18APR14/1618 OR CXL CA ALL SEGS                            
 8.OSI YY CTCT55116877                                                          
 9.RMK CA/NZ79FM                                                                
10.RMK TLWBINSD                                                                 
11.HRB192                                                                       

PAT:A
>PAT:A
01 B FARE:CNY860.00 TAX:CNY50.00 YQ:CNY120.00  TOTAL:1030.00                    
SFC:01     
'''

        //officeId=SHA255, source=uapi, needAccurateCodeShare=true, isDomestic=true, role=uapi, needFare=true, needXsfsm=false
        ParseConfBean parseConfBean = new ParseConfBean();

        //	, officeId=SHA255, source=uapi, needAccurateCodeShare=true, isDomestic=true, role=uapi, needFare=true, needXsfsm=false
        parseConfBean.isDomestic = true;
        parseConfBean.needFare = true;
        parseConfBean.source = "uapi"
        //
        //	parseConfBean.role='uapi'
        parseConfBean.role = PnrParserConstant.ROLE_OPERATOR;

        parseConfBean.needAccurateCodeShare = true

        parseConfBean.needXsfsm = false

        String xml = new PNRParser().parsePnrContent(parseConfBean, pnrContent).getValue0();

        print xml
    }
}
