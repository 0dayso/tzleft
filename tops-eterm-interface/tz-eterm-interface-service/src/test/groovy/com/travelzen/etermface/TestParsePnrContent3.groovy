package com.travelzen.etermface

import com.travelzen.etermface.service.PNRParser
import org.javatuples.Pair
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.travelzen.etermface.service.entity.ParseConfBean
import com.travelzen.framework.core.exception.BizException

/**
 * @author liang.wang
 *
 */
class TestParsePnrContent3 {

    private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

    static main(args) {

        def pnrContent =

                '''rt JN99J8
 1.蔡沪军 2.郑滢 JN99J8                                                         
 3.  HU7846 M   TU22NOV13PVGXIY RR2   1105 1225          E --T2              
 4.SHA/T SHA/T021-62277798/BU YE CHENG BOOKING OFFICE/JINHUA ABCDEFG            
 5.REM 0626 1714 YW6201                                                         
 6.51297821                                                                     
 7.TL/1200/28JUN/SHA255                                                         
 8.SSR FOID HU HK1 NI510107198106104227/P2                                      
 9.SSR FOID HU HK1 NI35020319750501403X/P1                                      
10.SSR OTHS 1E PNR RR AND PRINTED                                               
11.SSR ADTK 1E BY SHA26JUN13/1946 OR CXL HU7846 M28JUN                          
12.OSI YY CTCT51297821                                                          
13.OSI HU CTCT13341977386                                                      +

14.OSI 1E HUET TN/880-2158940313 1CAIHUJUN                                     -
15.OSI 1E HUET TN/880-2158940314 1ZHENGYING                                     
16.RMK CA/MHZP6B                                                                
17.RMK TJ AUTH NGB105                                                           
18.SHA255   

>PAT:A                                                                          
01 M1 FARE:CNY820.00 TAX:CNY50.00 YQ:CNY110.00  TOTAL:980.00                    
SFC:01                                                                         
02 M FARE:CNY880.00 TAX:CNY50.00 YQ:CNY110.00  TOTAL:1040.00                    
SFC:02     
'''

        String xml = "";
        try {
            ParseConfBean parseConfBean = new ParseConfBean()

            parseConfBean.isDomestic = true

            Pair<String, String> ret = new PNRParser().parsePnrContent(parseConfBean, pnrContent);

            System.out.println(ret.getValue0());
        } catch (BizException e) {
//			logger.error("{}", YRUtil.stringifyException(e));
        }
    }
}
