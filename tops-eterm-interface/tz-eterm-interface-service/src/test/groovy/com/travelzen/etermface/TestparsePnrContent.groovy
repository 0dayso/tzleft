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
class TestparsePnrContent {

	private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

	static main(args) {

		def pnrContent =

				'''rt JTSS64                                                                      
 0.25BYC NM20 JTSS64                                                            
21.  FM9333 S   WE06NOV  SHAKWL KK25  0850 1125          E T2--                 
22.  MU5382 S   SA09NOV  KWLPVG KK25  1430 1635          E --T1                 
23.SHA/T SHA/T021-62277798/BU YE CHENG BOOKING OFFICE/JINHUA ABCDEFG            
24.62662215                                                                     
25.TL/1200/01NOV/SHA255                                                         
26.SSR FOID FM HK1 NI342723197204290316/P4                                      
27.SSR FOID MU HK1 NI342723197204290316/P4                                      
28.SSR FOID FM HK1 NI310109198810250012/P1                                      
29.SSR FOID MU HK1 NI310109198810250012/P1                                      
30.SSR FOID FM HK1 NI310102199001173651/P20                                     
31.SSR FOID MU HK1 NI310102199001173651/P20
32.SSR FOID FM HK1 NI310110197411120414/P6                                     -
33.SSR FOID MU HK1 NI310110197411120414/P6                                      
34.SSR FOID FM HK1 NI310115198211080653/P11                                     
35.SSR FOID MU HK1 NI310115198211080653/P11                                     
36.SSR FOID FM HK1 NI310108197712235248/P5                                      
37.SSR FOID MU HK1 NI310108197712235248/P5                                      
38.SSR FOID FM HK1 NI320282198412261415/P10                                     
39.SSR FOID MU HK1 NI320282198412261415/P10                                     
40.SSR FOID FM HK1 NI330902197907190317/P13                                     
41.SSR FOID MU HK1 NI330902197907190317/P13                                     
42.SSR FOID FM HK1 NI620102198807053657/P9                                      
43.SSR FOID MU HK1 NI620102198807053657/P9
44.SSR FOID FM HK1 NI130229197906190619/P2                                     -
45.SSR FOID MU HK1 NI130229197906190619/P2                                      
46.SSR FOID FM HK1 NI310115198706245210/P15                                     
47.SSR FOID MU HK1 NI310115198706245210/P15                                     
48.SSR FOID FM HK1 NI370681198808046812/P17                                     
49.SSR FOID MU HK1 NI370681198808046812/P17                                     
50.SSR FOID FM HK1 NI310221197209146412/P7                                      
51.SSR FOID MU HK1 NI310221197209146412/P7                                      
52.SSR FOID FM HK1 NI310101197006193234/P8                                      
53.SSR FOID MU HK1 NI310101197006193234/P8                                      
54.SSR FOID FM HK1 NI321181198608060024/P14                                     
55.SSR FOID MU HK1 NI321181198608060024/P14
56.SSR FOID FM HK1 NI310105197404022014/P12                                    -
57.SSR FOID MU HK1 NI310105197404022014/P12                                     
58.SSR FOID FM HK1 NI32092319670325453X/P18                                     
59.SSR FOID MU HK1 NI32092319670325453X/P18                                     
60.SSR FOID FM HK1 NI310107195703040419/P19                                     
61.SSR FOID MU HK1 NI310107195703040419/P19                                     
62.SSR FOID FM HK1 NI32032419840612191X/P3                                      
63.SSR FOID MU HK1 NI32032419840612191X/P3                                      
64.SSR FOID FM HK1 NI310115197102053619/P16                                     
65.SSR FOID MU HK1 NI310115197102053619/P16                                     
66.SSR OTHS FM WITH CHG PNR MXQTYM                                              
67.SSR CKIN MU
68.SSR CKIN FM                                                                 -
69.SSR CKIN MU                                                                  
70.SSR CKIN FM                                                                  
71.SSR CKIN MU                                                                  
72.SSR CKIN FM                                                                  
73.SSR CKIN MU                                                                  
74.SSR CKIN FM                                                                  
75.SSR FQTV FM HK1 SHAKWL 9333 S06NOV MU640286126192/P18                        
76.SSR FQTV FM HK1 SHAKWL 9333 S06NOV MU650301000017/P12                        
77.SSR FQTV FM HK1 SHAKWL 9333 S06NOV MU610286126105/P15                        
78.SSR FQTV FM HK1 SHAKWL 9333 S06NOV MU640262832327/P10                        
79.SSR FQTV MU HK1 KWLPVG 5382 S09NOV MU640262832327/P10
80.SSR FQTV MU HK1 KWLPVG 5382 S09NOV MU610286126105/P15                       -
81.SSR FQTV MU HK1 KWLPVG 5382 S09NOV MU650301000017/P12                        
82.SSR FQTV MU HK1 KWLPVG 5382 S09NOV MU640286126192/P18                        
83.SSR ADTK 1E BY SHA26OCT13/1200 OR CXL FM SET BY CTL                          
84.OSI YY CTCT62662215                                                          
85.RMK CA/MDEP8R                                                                
86.SHA255    
'''


		String xml = "";
		try {
			ParseConfBean parseConfBean = new ParseConfBean()

			parseConfBean.isDomestic = true

			Pair<String, String> ret = new PNRParser().parsePnrContent(parseConfBean ,pnrContent);


			//System.out.println(ret.getValue0());
		} catch (BizException e) {
			//			logger.error("{}", YRUtil.stringifyException(e));
		}
	}
}
