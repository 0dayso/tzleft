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
class Uapi4 {

    private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

    static main(args) {

        def pnrContent =

                ''' RTJGHDBM
                                                  
 1.谷明中 JGHDBM                                                                
 2.  SC4761 Z   SU09MAR  CKGHRB HK1   0905 1355          E                      
 3.CKG/T CKG/T 023-63871111/CKG KUAI TONG AVATION SERVICE CO.,LTD/LI JING       
     ABCDEFG                                                                    
 4.023-86528000                                                                 
 5.TL/1050/06MAR/CKG113                                                         
 6.SSR FOID SC HK1 NI512322196911063410/P1                                      
 7.SSR ADTK 1E BY CKG06MAR14/2300 OR CXL SC4761 Z09MAR                          
 8.OSI SC CTCM18983920631/P1                                                    
 9.OSI SC CTCT18983920631                                                       
10.RMK CA/NWQ9PM                                                                
11.CKG113                                                                       

PAT:A
>PAT:A
>PAT:A                                                                          
01 Z FARE:CNY780.00 TAX:CNY50.00 YQ:CNY120.00  TOTAL:950.00                     
 SFC:01       
'''

        //officeId=SHA255, source=uapi, needAccurateCodeShare=true, isDomestic=true, role=uapi, needFare=true, needXsfsm=false
        ParseConfBean parseConfBean = new ParseConfBean();

        //	, officeId=SHA255, source=uapi, needAccurateCodeShare=true, isDomestic=true, role=uapi, needFare=true, needXsfsm=false
        parseConfBean.isDomestic = true;
        parseConfBean.needFare = true;
        //	parseConfBean.source = "uapi"
        //
        //	parseConfBean.role='uapi'
        parseConfBean.role = PnrParserConstant.ROLE_OPERATOR;

        parseConfBean.needAccurateCodeShare = true

        parseConfBean.needXsfsm = false

        String xml = new PNRParser().parsePnrContent(parseConfBean, pnrContent).getValue0();

        print xml
    }
}
