package com.travelzen.etermface.postcontent

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.travelzen.etermface.service.PNRParser
import com.travelzen.etermface.service.constant.PnrParserConstant
import com.travelzen.etermface.service.entity.ParseConfBean

/**
 * @author liang.wang
 *
 */
class Cont1 {

    private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

    static main(args) {

        def pnrContent =

                ''' RTJDKKQQ
 1.陈豪 JDKKQQ                                                                   
 2.  KN5915 K   SU30MAR  NAYWNZ HK1   1125 1405          E                      
 3.PEK/T PEK/T010-51669611/BEIJING YUANYANG GANXIAN INTERNATIONAL AVIATION      
     SERVICE CO.,/G ABCDEFG                                                     
 4.13901159633                                                                  
 5.TL/1857/28MAR/PEK325                                                         
 6.SSR FOID KN HK1 NI330323196809112635/P1                                      
 7.SSR CKIN KN                                                                  
 8.SSR ADTK 1E BY PEK28MAR14/1657 OR CXL KN5915 K30MAR                          
 9.OSI YY CTCT13901159633                                                       
10.OSI KN CTCT13161958149                                                       
11.RMK CA/NJYC57                                                               +
       
'''

//officeId=SHA255, source=uapi, needAccurateCodeShare=true, isDomestic=true, role=uapi, needFare=true, needXsfsm=false

        ParseConfBean parseConfBean = new ParseConfBean();

//	, officeId=SHA255, source=uapi, needAccurateCodeShare=true, isDomestic=true, role=uapi, needFare=true, needXsfsm=false
        parseConfBean.isDomestic = true;
        parseConfBean.needFare = true;

        parseConfBean.role = PnrParserConstant.ROLE_OPERATOR;

//	parseConfBean.source = "uapi"

//	parseConfBean.role='uapi'

        parseConfBean.needAccurateCodeShare = true

        parseConfBean.needXsfsm = false

        String xml = new PNRParser().parsePnrContent(parseConfBean, pnrContent).getValue0();

        print xml


    }
}
