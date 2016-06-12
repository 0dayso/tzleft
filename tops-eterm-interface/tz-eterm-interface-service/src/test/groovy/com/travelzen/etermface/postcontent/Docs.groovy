package com.travelzen.etermface.postcontent

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.travelzen.etermface.service.PNRParser
import com.travelzen.etermface.service.entity.ParseConfBean;

/**
 * @author liang.wang
 *
 */
class Docs {

    private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

    static main(args) {

        def pnrContent =

                '''**ELECTRONIC TICKET PNR**
 1.GAO/YIFENG 2.GU/MIN JXQRVC
  
 3.  MH389  O   TU24JUN  PVGKUL HK2   1535 2100          E  
 4.  MH869  O   WE25JUN  KULDPS HK2   0300 0600          E  
 5.  MH850  O   SA28JUN  DPSKUL HK2   1605 1905          E  
 6.  MH386  O   SU29JUN  KULPVG HK2   0140 0700          E  
 7.SHA/T SHA/T021-62277798/BU YE CHENG BOOKING OFFICE/JINHUA ABCDEFG
 8.T  
 9.SSR OTHS 1E AUTO XX IF SSR TKNA/E/M/C NOT RCVD BY MH BY 1600/04MAR/ SHA LT   10.SSR OTHS 1E MOBILE AND EMAIL CONTACT REQUIRED X PLS PROVIDE ASAP 
11.SSR OTHS 1E AUTO XX IF SSR TKNA/E/M/C NOT RCVD BY MH BY 1600/03MAR/ SHA LT   12.SSR DOCS MH HK1 P/CHN/G30475856/CHN/14APR76/F/23APR17/GU/MIN/P2              
13.SSR TKNE MH HK1 PVGKUL 389 O24JUN 2324794716521/1/P1                        
14.SSR TKNE MH HK1 KULDPS 869 O25JUN 2324794716521/2/P1 
15.SSR TKNE MH HK1 DPSKUL 850 O28JUN 2324794716521/3/P1 
16.SSR TKNE MH HK1 KULPVG 386 O29JUN 2324794716521/4/P1 
17.SSR TKNE MH HK1 KULDPS 869 O25JUN 2324794716522/2/P2 
18.SSR TKNE MH HK1 DPSKUL 850 O28JUN 2324794716522/3/P2 
19.SSR TKNE MH HK1 KULPVG 386 O29JUN 2324794716522/4/P2 
20.SSR DOCS MH HK1 P/CHN/G24535369/CHN/28JAN74/M/28AUG17/GAO/YIFENG/P1  
22.RMK TJ SHA255
23.RMK MH/KR12B 
24.FN/A/FCNY2850.00/SCNY2850.00/C4.00/XCNY90.00/TCNY90.00CN/ACNY2940.00        
25.TN/232-4794716521/P1                                                        
26.TN/232-4794716522/P2 
27.FP/CASH,CNY  
28.SHA255'''


        ParseConfBean parseConfBean = new ParseConfBean();

        //		, officeId=SHA255, source=etermface, needAccurateCodeShare=true, isDomestic=true, role=operator, needFare=true, needXsfsm=false
        parseConfBean.isDomestic = false;
        //		parseConfBean.needFare = true ;
        parseConfBean.needFare = false;
        //		parseConfBean.source = 'uapi';

        String xml = new PNRParser().parsePnrContent(parseConfBean, pnrContent).getValue0();

        print xml


    }
}
