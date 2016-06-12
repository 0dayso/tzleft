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
class TestparsePnrContentMarriedSeg {

	private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

	static main(args) {

		def pnrContent =
		
'''  MARRIED SEGMENT EXIST IN THE PNR                                               - 1.GUO/SHAOYA 2.LI/FENGFANG 3.SUN/MINGMING 4.XIAO/JIANPING 5.ZHANG/LE HE6LHV
 6.  KA791  M1  SU20APR  WNZHKG HK5   2005 2155          E  
 7.  CX105  M1  SU20APR  HKGADL HK5   2335 0945+1        E  
 8.  CX104  M2  TH15MAY  ADLHKG HK5   1110 2150          E  
 9.  KA798  M2  FR16MAY  HKGWNZ HK5   0850 1050          E  
10.SHA/T SHA/T021-62277798/BU YE CHENG BOOKING OFFICE/JINHUA ABCDEFG
11.REM 1226 1359 1072255
12.TL/1805/20APR/SHA255 
13.SSR ADTK 1E ADV TKT NBR TO CX/KA BY 21MAR GMT 2359 OR SUBJECT TO CANCEL  
14.OSI KA CTCT13816584827/P2
15.RMK 1A/2Z3IGL                                               
'''		
 

		String xml = "";
		try {
			
			ParseConfBean parseConfBean = new ParseConfBean()
			
 		   parseConfBean.isDomestic = true
			
			Pair<String, String> ret = new PNRParser().parsePnrContent(parseConfBean ,pnrContent);

			System.out.println(ret.getValue0());
		} catch (BizException e) {
//			logger.error("{}", YRUtil.stringifyException(e));
		}
	}
}
