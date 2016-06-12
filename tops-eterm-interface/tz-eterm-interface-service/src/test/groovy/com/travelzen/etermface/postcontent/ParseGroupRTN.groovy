package com.travelzen.etermface.postcontent

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.travelzen.etermface.service.PNRParser
import com.travelzen.etermface.service.entity.ParseConfBean;

/**
 * @author liang.wang
 *
 */
class ParseGroupRTN {

    private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

    static main(args) {

        def pnrContent =

                '''0.24BYC NM24 HVL2L9
1.白小伟 2.蔡银 3.曹德权 4.陈红星 5.丁宇飞 6.董运英                            
7.杜小草 8.杜永锋 9.高文飞 10.耿爽 11.郭菲 12.侯聪杰   
13.井刚 14.金继涛 15.孔峰 16.李鉴 17.李群 18.刘阳                                                      
19.李珍珍 20.李忠 21.马烈 22.庞开国 23.潘振兴 24.彭波   25.  CA1831 V   FR28MAR 
PEKSHA RR24  0730 0940          E T3T2 V1                                      
26.SHA/T SHA/T021-31087705/BU YE CHENG BOOKING OFFICE/JINHUA ABCDEFG            
27.REM 0319 1349 289255                                                         
28.HUAMIN 289                                                                   
29.TL/1200/25MAR/SHA255                                                         
30.SSR FOID CA HK1 NI362101198209200014/P15                                     
31.SSR FOID CA HK1 NI412829198104127614/P24                                     
32.SSR FOID CA HK1 NI410411198211305511/P7                                      
33.SSR FOID CA HK1 NI130105197109041817/P21                                     
34.SSR FOID CA HK1 NI110101197909123010/P16                                     
35.SSR FOID CA HK1 NI131181198511060970/P5                                      
36.SSR FOID CA HK1 NI130627198612302236/P12                                     
37.SSR FOID CA HK1 NI210105198312240410/P18                                     
38.SSR FOID CA HK1 NI450981198711050993/P22                                     
39.SSR FOID CA HK1 NI220102198510061811/P13                                     
40.SSR FOID CA HK1 NI370827198609143532/P1     
41.SSR FOID CA HK1 NI15042519860808255X/P8 
42.SSR FOID CA HK1 NI410311198407210017/P23                                     
43.SSR FOID CA HK1 NI412822198701027967/P11                                     
44.SSR FOID CA HK1 NI460003198710042215/P20                                     
45.SSR FOID CA HK1 NI230381198702146623/P19                                     
46.SSR FOID CA HK1 NI130926199106060814/P14                                     
47.SSR FOID CA HK1 NI142201198705295256/P4                                      
48.SSR FOID CA HK1 NI410121197903214313/P9                                      
49.SSR FOID CA HK1 NI231025198903104917/P2                                      
50.SSR FOID CA HK1 NI232161198602234454/P3                                      
51.SSR FOID CA HK1 NI330327198601255674/P17                                     
52.SSR FOID CA HK1 NI34222519900214004X/P10   
53.SSR FOID CA HK1 NI372922198302083323/P6  
54.SSR OTHS 1E 1 PNR RR AND PRINTED                                             
55.SSR OTHS 1E 1 CAAIRLINES ET PNR                                              
56.SSR OTHS 1E PLS ISSUE TKT BEF 1600 19MAR OR CNLD                             
57.SSR GRPS 1E TCP24 BYC                                                        
58.SSR FQTV CA HK1 PEKSHA 1831 V28MAR CA000467662230/P15                        
59.SSR FQTV CA HK1 PEKSHA 1831 V28MAR CA000356793220/P24                        
60.SSR FQTV CA HK1 PEKSHA 1831 V28MAR CA000104863780/P21                        
61.SSR FQTV CA HK1 PEKSHA 1831 V28MAR CA008457387291/P1                         
62.OSI CA CTCT31087513                                                          
63.OSI 1E CAET TN/9992332254644-9992332254667                                   
64.RMK CA/NJX3FV        
65.SHA255'''


        ParseConfBean parseConfBean = new ParseConfBean();

        //		, officeId=SHA255, source=etermface, needAccurateCodeShare=true, isDomestic=true, role=operator, needFare=true, needXsfsm=false
        parseConfBean.isDomestic = true;
        parseConfBean.needFare = true;
        parseConfBean.source = 'uapi';

        String xml = new PNRParser().parsePnrContent(parseConfBean, pnrContent).getValue0();

        print xml


    }
}
