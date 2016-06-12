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
class Uapi6 {

    private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

    static main(args) {

        def pnrContent =

                '''  1.章登万 JM4MK6
 2.  FM9321 S   SA31MAY  SHACAN HK1   1830 2050          E T2--                 
 3.SHA/T SHA/T 021-62772000/SHA PU HUI AIR TECHNOLOGY CO.,LTD/CHEN WEI ABCDEFG  
 4.021-56635489                                                                 
 5.BOOK LIQINGHUA 0526 1515                                                     
 6.TL/1630/31MAY/SHA701                                                         
 7.SSR FOID FM HK1 NI340223196808045414/P1                                      
 8.SSR ADTK 1E BY SHA27MAY14/1517 OR CXL FM9321 S31MAY                          
 9.OSI FM CTCT13391363329                                                       
10.RMK CA/MGLY76                                                                
11.SHA701                                                                       

PAT:A
>PAT:A
PAT:A                                                                             01 S FARE:CNY640.00 TAX:CNY50.00 YQ:CNY120.00  TOTAL:810.00                       SFC:01

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
