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
class Uapi7 {

    private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

    static main(args) {

        def pnrContent =

                ''' 1.董国英  2.金雪丹  HNZ5H3
  3.  FM9103 N   TH15MAY  SHAPEK HK2   1330 1555          E T2T2                  
  4.CAN/T CAN/T020-86395379/IN GUANGZHOU OF A TRADING COMPANY LIMITED/            
     /ZHANGGUOHUA ABCDEFG                                                         
  5.02083530090                                                                   
  6.TL/2024/12MAY/CAN527                                                          
  7.SSR FOID FM HK1 NI330422197103190942/P1                                       
  8.SSR FOID FM HK1 NI330425197501040527/P2                                       
  9.SSR FQTV FM HK1 SHAPEK 9103 N15MAY MU650287901218/P1                          
 10.SSR ADTK 1E BY CAN12MAY14/2024 OR CXL FM9103 N15MAY                           
 11.OSI YY CTCT02083530090                                                        
 12.OSI FM CTCT13241846760                                                     -  
 13.RMK CA/MYKQK0                                                                 
 14.CAN527                                                                        
  
 PAT:A 
 >PAT:A 
 PAT A                                                                           
 >PAT:A                                                                           
 01 N FARE:CNY780.00 TAX:CNY50.00 YQ:CNY120.00  TOTAL:950.00                      
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
