package com.travelzen.etermface

import com.travelzen.etermface.service.PNRParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author liang.wang
 *
 */
class TestAvh {

	private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

	static main(args) {

		def content =
				'''   28NOV(THU) PVGTYO VIA MU  
1- *MU8763  DS# JZ YA MZ HL NL                 PVGNRT 0850   1235   767 0    E  (   JL872                                                           T1 T2  2:45
2   MU523   DS# JA CA D7 I5 YA BA MA EA HA KA  PVGNRT 0910   1250   323 0^   E  (               LA NA RA SA V1 TS GQ ZA QS                          T1 T2  2:40
3  *MU8705  DS# JC Y4 MC HC N4                 PVGNRT 1210   1600   767 0    E  (   JL874                                                           T1 T2  2:50
4  *MU8723  DS# JC Y4 MC HC N4                 PVGNRT 1400   1745   767 0    E  (   JL876                                                           T1 T2  2:45
5   MU271   DS# JA C8 D6 I4 YA BA MA EA HA K4  PVGNRT 1700   2055   321 0^D  E  (               LS NS RS SS VS TS GQ ZA QS                          T1 T2  2:55
6+  MU2335  DS# F8 P2 YA BQ MQ EQ HQ KQ LQ NQ  PVGXIY 0800   1040   320 0^S  E  (               RQ SQ VQ TQ GQ ZA Q5                                T1 T3  2:40
    MU521   DS# J6 C1 D1 IS YA BA MA EA HA KA     NRT 0800+1 1555+1 JET 1^M  E  (               LA NA RA S7 V5 T2 GQ ZA Q2                          T2 T2 30:55
 **  CZ6999 PLEASE CHECK IN 45 MINUTES BEFORE DEPARTURE AT PVG 
 **  HO FLIGHT DEPARTURE/ARRIVAL AT PVG T2 FROM 18DEC12, 
 **  HO PVG CHECK IN 45 MINUTES BEFORE DEPARTURE
'''
				
	PNRParser.processAvh4CodeShare(content, "MU8763");

 
 
 
	}
}
