package com.travelzen.etermface.postcontent

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.travelzen.etermface.service.PNRParser
import com.travelzen.etermface.service.entity.ParseConfBean;

/**
 * @author liang.wang
 *
 */
class ParseGroup1 {

	private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

	static main(args) {

		def pnrContent =

				''' 0.6QC NM6 HE3FNE                                                               
1.陈金妹 2.陈永亮 3.董晓红 4.李海初 5.李书荣 6.张正珠   
7. *MU9455 Q   WE12MAR  SHAKMG HK6   1510 1820          E T2-- OP-FM9455                               
8.  MU9760 Q   TU18MAR  LJGJHG HK6   2200 2300          E                      
9.  MU9746 Q   TH20MAR  JHGKMG HK6   2340 0030+1        E                      
10.  MU5807 Q   FR21MAR  KMGSHA HK6   1600 1905          E --T2                 
11.SHA/T SHA/T 021-55666666/SHA SHENG JIE TOUR TRADE CO.,LTD/NI QI ABCDEFG      
12.REM 0210 1637 QIANCHENG1 021-32506030                                        
13.32506030                                                                     
14.TL/1310/12MAR/SHA476                                                         
15.SSR FOID MU HK1 NI31011019630301242X/P6                                      
16.SSR FOID MU HK1 NI310106195902014073/P4                                      
17.SSR FOID MU HK1 NI310109196001282867/P1                                      
18.SSR FOID MU HK1 NI310101195705132431/P2                                      
19.SSR FOID MU HK1 NI310108196309063664/P3                                      
20.SSR FOID MU HK1 NI310101196110202431/P5                                      
21.SSR GRPS 1E TCP14 QC                                                         
22.OSI MU CTCT18916051726                                                                                                                                   
23.RMK CA/MY5Q01                                                               
24.RMK TLWBINSD                                                                 
25.RMK TJ AUTH SHA255                                                           
26.SHA476'''


		ParseConfBean parseConfBean = new ParseConfBean();

		//		, officeId=SHA255, source=etermface, needAccurateCodeShare=true, isDomestic=true, role=operator, needFare=true, needXsfsm=false
		parseConfBean.isDomestic = true ;
		parseConfBean.needFare = true ;

		String xml = new PNRParser().parsePnrContent(parseConfBean,  pnrContent ).getValue0();

		print xml


	}
}
