package com.travelzen.etermface.service.ticket

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author liang.wang
 *
 */
class TestFullTol {

	private static Logger logger = LoggerFactory.getLogger(TestFullTol.class)

	static main(args) {

		def  tolCont =
				'''ISSUED BY:                           ORG/DST: SHA/SZX                 ARL-D 
E/R: 变更退票收费   
TOUR CODE:                                                    RECEIPT PRINTED  
PASSENGER: MIMURA/HIDEMICHI 
EXCH:                               CONJ TKT:   
O FM:1SHA MU    5323  Y 05NOV 0930 OK Y                        20K USED/FLOWN        T2-- RL:MCKZM2  /  
  TO: SZX     
FC:   
FARE:                       FOP:
TAX:                        OI: 
TOTAL:                      TKTN: 781-2146670378'''
				

def  tolCont1 =
'''ISSUED BY:                           ORG/DST: SHA/SHA                 ARL-D
E/R: 不得退改签/KMG13467
TOUR CODE:                                                   RECEIPT PRINTED
PASSENGER: 施晓礼
EXCH:                               CONJ TKT: 781-2149319881-83
O FM:1SHA MU    5806  Q 07DEC 1420 OK QA                       20K OPEN FOR USE
	 T2-- RL:NL9MNV  /
O TO:2KMG MU    5971  Q 08DEC 0910 OK QA                       20K OPEN FOR USE
		  RL:NL9MNV  /
  TO: LUM
FC:
FARE:                      |FOP:
TAX:                       |OI:
TOTAL:                     |TKTN: 781-2149319881'''


	def  tolCont2 =
				'''ISSUED BY:                           ORG/DST: SHA/SHA                 ARL-D
E/R: 不得退改签/KMG13467
TOUR CODE:                                                   RECEIPT PRINTED
PASSENGER: 施晓礼
EXCH:                               CONJ TKT: 781-2149319881-83
O FM:1LUM       VOID    VOID                                       VOID
		  RL:
O TO:2BSD MU    5966  Q 11DEC 2130 OK QA                       20K OPEN FOR USE
		  RL:NL9MNV  /
  TO: KMG
FC:
FARE:                      |FOP:
TAX:                       |OI:
TOTAL:                     |TKTN: 781-2149319882'''


	def tolCont3 ='''ISSUED BY: CHINA EASTERN AIRLINES    ORG/DST: SHA/MIG                 BSP-D 
TOUR CODE:    
PASSENGER: 秦晨曦 INF(JUL13)   
EXCH:                               CONJ TKT:   
O FM:1PVG FM    9407  B 26JAN 0820 NS YIN                      0L  OPEN FOR USE      T1-- RL:NW3FXH  /JG2P1G1E  
  TO: MIG     
FC: 26JAN14PVG FM MIG150.00CNY150.00END**(IN)   
FARE:           CNY  150.00 FOP:CASH
TAX:               EXEMPTCN OI: 
TAX:               EXEMPTYQ 
TOTAL:          CNY  150.00 TKTN: 781-4604629152'''

		DetrParser parser = new DetrParser();


		parser.setRawResultProvider( new IRawResultProvider(){
					String getString(){
						return tolCont1;
					}
				}
				) ;

		 parser.parse();

//		println (syntaxTree)



		//		TolParser parser = new TolParser()
		//		TolConfig tolConfig = new TolConfig(3)
		//
		//		TolResult res = parser.parseTol(tolCont).getObject()
		//		logger.info(res.toString())
	}
}
