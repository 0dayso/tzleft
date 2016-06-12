package com.travelzen.etermface.postcontent

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.travelzen.etermface.service.PNRParser
import com.travelzen.etermface.service.entity.ParseConfBean;

/**
 * @author liang.wang
 *
 */
class ParseGroup2 {

    private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

    static main(args) {

        def pnrContent =

                '''1.崔殿伟 2.丁鲁芹 3.王其宪 JN8GN9
 4. 3U8963 F FR28MAR CTUPVG HK3 1245 1515 E T1-- 
 5.BJS/T BJS/T 010-82082660/BJS AI TE BO LU YUN AIR CO.,LTD/SHAO LIXIN ABCDEFG 
 6.END 
 7.TL/0000/25MAR/SHA255 
 8.SSR FOID 3U HK1 NI372524198111123792/P1 
 9.SSR FOID 3U HK1 NI110108198211072716/P3 
10.SSR FOID 3U HK1 NI370405198812210622/P2 
11.SSR ADTK 1E BY BJS21MAR14/1245 OR CXL 3U8963 F28MAR 
12.OSI 3U CTCT13699169896 
13.OSI 3U CTCM13811197805/P3 
14.OSI 3U CTCM15810685495/P1 
15.RMK AO/ABE/KH-212241106001/OP-NN LQDING/DT-20140123103847 
16.RMK CA/NX919N 
17.BJS407 
PAT:A
>PAT:A
>PAT:A 
01 F FARE:CNY2900.00 TAX:CNY50.00 YQ:CNY120.00 TOTAL:3070.00 
 SFC:01'''



        ParseConfBean parseConfBean = new ParseConfBean();

        //		, officeId=SHA255, source=etermface, needAccurateCodeShare=true, isDomestic=true, role=operator, needFare=true, needXsfsm=false
        parseConfBean.isDomestic = true;
        parseConfBean.needFare = true;
        parseConfBean.source = 'uapi';

        String xml = new PNRParser().parsePnrContent(parseConfBean, pnrContent).getValue0();

        print xml


    }
}
