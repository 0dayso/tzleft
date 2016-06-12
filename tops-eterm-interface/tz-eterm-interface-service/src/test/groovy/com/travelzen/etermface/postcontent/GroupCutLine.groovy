package com.travelzen.etermface.postcontent

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.travelzen.etermface.service.PNRParser
import com.travelzen.etermface.service.entity.ParseConfBean;

/**
 * @author liang.wang
 *
 */
class GroupCutLine {

    private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

    static main(args) {

        def pnrContent =

                '''0.6QC NM6 JXNV2D
 1.蔡林飞 2.龚雪芳 3.倪伟芝 4.汤福仙 5.王飞凤 6.吴思澄   7. *MU9455 Q   TH06MAR  SHAKMG HK6   1510 1820          E T2-- OP-FM9455   
 8.  MU9745 Q   MO10MAR  KMGJHG HK6   2210 2300          E  
9.  MU9746 Q   WE12MAR  JHGKMG HK6   2340 0030+1        E  
10. *MU9456 Q   TH13MAR  KMGSHA HK6   1920 2220          E --T2 OP-FM9456   
11.SHA/T SHA/T 021-55666666/SHA SHENG JIE TOUR TRADE CO.,LTD/NI QI ABCDEFG  '''


        ParseConfBean parseConfBean = new ParseConfBean();

        //		, officeId=SHA255, source=etermface, needAccurateCodeShare=true, isDomestic=true, role=operator, needFare=true, needXsfsm=false
        parseConfBean.isDomestic = true;
        parseConfBean.needFare = true;
        parseConfBean.source = 'uapi';

        String xml = new PNRParser().parsePnrContent(parseConfBean, pnrContent).getValue0();

        print xml


    }
}
