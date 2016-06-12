package com.travelzen.rosetta.eterm.parser.rt.subparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.rosetta.eterm.common.pojo.rt.QteInfo;

/**
 * QTE报价信息解析
 * <p>
 * @author yiming.yan
 * @Date Feb 25, 2016
 */
public enum QteInfoParser {
	
	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(QteInfoParser.class);
	
	public static QteInfo parse(String text) {
		LOGGER.info("开始QTE报价解析");
		
		return null;
	}
	
	public static void main(String[] args) {
		String s = "02 MFETZVAR+*          15419 CNY                    INCL TAX\n"
				+ "BJS     \n"
				+ "XSEA MFETZVAR                          NVB01NOV15 NVA01NOV15 2PC  \n"
				+ " ATL MFETZVAR                          NVB01NOV15 NVA01NOV15 2PC  \n"
				+ "XSEA XLETZSUL                          NVB06NOV15 NVA06NOV15 2PC  \n"
				+ " BJS XLETZSUL                          NVB06NOV15 NVA06NOV15 2PC  \n"
				+ "FARE  CNY   12660     \n"
				+ "TAX   CNY      90CN CNY      72AY CNY    2597XT   \n"
				+ "TOTAL CNY   15419     \n"
				+ "01NOV15BJS DL X/SEA DL ATL M1713.82DL X/SEA DL BJS M273.20NU  \n"
				+ "C1987.02END ROE6.368800   \n"
				+ "XT CNY 226US CNY 32XA CNY 45XY CNY 35YC CNY 2230YR    \n"
				+ "XT CNY 29XFSEA4.5     \n"
				+ "ENDOS 01,02 *REF/CHANGE PENALTIES APPLY   \n"
				+ "ENDOS 03,04 *NONREF/PENALTY APPLIES   \n"
				+ "*AUTO BAGGAGE INFORMATION AVAILABLE - SEE FSB     \n"
				+ "TKT/TL31OCT15*1328    \n"
				+ "RFSONLN/1E /EFEP_19/FCC=W/ \n";
//		new QteBySegmentsParser().getQteResultByQteTxt(s);
		String s2 = "02 MFETZVAR+*          15419 CNY                    INCL TAX\n"
				+ "BJS     \n"
				+ "XSEA MFETZVAR                          NVB01NOV15 NVA01NOV15 2PC  \n"
				+ " ATL MFETZVAR                          NVB01NOV15 NVA01NOV15 2PC  \n"
				+ "XSEA XLETZSUL                          NVB06NOV15 NVA06NOV15 2PC  \n"
				+ " BJS XLETZSUL                          NVB06NOV15 NVA06NOV15 2PC  \n"
				+ "FARE  CNY   12660     \n"
				+ "TAX   CNY      90CN CNY      72AY CNY    2597XT   \n"
				+ "TOTAL CNY   15419     \n"
				+ "20DEC15SHA AM X/TIJ AM MTY Q SHAMTY179.00M/BT //MEX AM SHA Q \n"
				+ " MTYSHA179.00M/BT END ROE6.368800\n"
				+ "XT CNY 226US CNY 32XA CNY 45XY CNY 35YC CNY 2230YR    \n"
				+ "XT CNY 29XFSEA4.5     \n"
				+ "ENDOS 01,02 *REF/CHANGE PENALTIES APPLY   \n"
				+ "ENDOS 03,04 *NONREF/PENALTY APPLIES   \n"
				+ "*AUTO BAGGAGE INFORMATION AVAILABLE - SEE FSB     \n"
				+ "TKT/TL31OCT15*1328    \n"
				+ "RFSONLN/1E /EFEP_19/FCC=W/ \n";
//		new QteBySegmentsParser().getQteResultByQteTxt(s2);
		String s3 = "02 MFETZVAR+*          15419 CNY                    INCL TAX\n"
				+ "BJS     \n"
				+ "XSEA MFETZVAR                          NVB01NOV15 NVA01NOV15 2PC  \n"
				+ " ATL MFETZVAR                          NVB01NOV15 NVA01NOV15 2PC  \n"
				+ "XSEA XLETZSUL                          NVB06NOV15 NVA06NOV15 2PC  \n"
				+ " BJS XLETZSUL                          NVB06NOV15 NVA06NOV15 2PC  \n"
				+ "FARE  CNY   12660     \n"
				+ "TAX   CNY      90CN CNY      72AY CNY    2597XT   \n"
				+ "TOTAL CNY   15419     \n"
				+ "31JAN16SHA AM X/TIJ AM MTY Q SHAMTY179.00 787.43/-MEX AM SHA  \n"
				+ " Q179.00 447.49NUC1592.92END ROE6.368800 \n"
				+ "XT CNY 226US CNY 32XA CNY 45XY CNY 35YC CNY 2230YR    \n"
				+ "XT CNY 29XFSEA4.5     \n"
				+ "ENDOS 01,02 *REF/CHANGE PENALTIES APPLY   \n"
				+ "ENDOS 03,04 *NONREF/PENALTY APPLIES   \n"
				+ "*AUTO BAGGAGE INFORMATION AVAILABLE - SEE FSB     \n"
				+ "TKT/TL31OCT15*1328    \n"
				+ "RFSONLN/1E /EFEP_19/FCC=W/ \n";
//		new QteBySegmentsParser().getQteResultByQteTxt(s3);
		String s4 = "01 NSRWCH+YOW           5647 CNY                    INCL TAX                    \n"
				+ "*SYSTEM DEFAULT-CHECK OPERATING CARRIER                                         \n"
				+ "*INTERLINE AGREEMENT PRICING APPLIED                                            \n"
				+ "*TKT STOCK RESTR                                                                \n"
				+ "*ATTN PRICED ON 08JAN16*1531                                                    \n"
				+ " SHA                                                                            \n"
				+ " HKG NSRWCH                            NVB03FEB16 NVA03FEB16 20K                \n"
				+ "XSEL YOW                               NVB        NVA03FEB17 1PC                \n"
				+ " TYO YOW                               NVB        NVA03FEB17 1PC                \n"
				+ "FARE  CNY    5360                                                               \n"
				+ "TAX   CNY      90CN CNY     102HK CNY      95XT                                 \n"
				+ "TOTAL CNY    5647                                                               \n"
				+ "03FEB16SHA MU HKG186.96OZ X/SEL Q5.80OZ TYO Q HKGTYO24.90M61                    \n"
				+ "6.75NUC834.41END ROE6.418450                                                    \n"
				+ "XT CNY 55BP CNY 40YQ                                                            \n"
				+ "ENDOS 01 *Q/NON-END/RER.                                                        \n"
				+ "ENDOS 01 *RFD/CHG CNY200                                                        \n"
				+ "*AUTO BAGGAGE INFORMATION AVAILABLE - SEE FSB                                   \n"
				+ "RFSONLN/1E /EFEP_36/FCC=T/";
//		new QteBySegmentsParser().getQteResultByQteTxt(s4);
		String s5 = "FSI/AC   \n"
				+ "S AC   026A29APR PVG1555 1130YVR0S    788     \n"
				+ "S AC  8576L02MAY YVR2035 2330YXE0S    CRA     \n"
				+ "S AC  8577A16MAY YXE0640 0742YVR0X    CRA     \n"
				+ "S AC   025A16MAY YVR1115(1410PVG0S    788     \n"
				+ "01 A0LNCD               5954 CNY                    INCL TAX  \n"
				+ "*SYSTEM DEFAULT-CHECK OPERATING CARRIER   \n"
				+ "*CA CHECKIN TAX MAY APPLY. SEE FXT/CA/--  \n"
				+ "*CHECK RULE FOR PAYMENT/TICKETING CONDITIONS  \n"
				+ "*TKT STOCK RESTR  \n"
				+ "*仅限电子票   \n"
				+ "*ATTN PRICED ON 25FEB16*1149  \n"
				+ " SHA  \n"
				+ " YVR A0LNCD                            NVB29APR16 NVA29APR16 2PC  \n"
				+ " YXE A0LNCD                            NVB02MAY16 NVA02MAY16 2PC  \n"
				+ "XYVR A0LNCD                            NVB16MAY16 NVA16MAY16 2PC  \n"
				+ " SHA A0LNCD                            NVB16MAY16 NVA16MAY16 2PC  \n"
				+ "FARE  CNY    3350     \n"
				+ "TAX   CNY      90CN CNY     124CA CNY    2390XT   \n"
				+ "TOTAL CNY    5954     \n"
				+ "29APR16SHA AC YVR AC YXE Q SHAYXE11.11 210.33AC X/YVR AC SHA  \n"
				+ " Q YXESHA11.11 210.33 1S77.90NUC520.78END ROE6.418450     \n"
				+ "XT CNY 190SQ CNY 10XG CNY 2190YQ                                               \n"
				+ "ENDOS *REFUNDABLE/CXLFEE/CHGFEE                                                \n"
				+ "*AUTO BAGGAGE INFORMATION AVAILABLE - SEE FSB   \n"
				+ "*COMMISSION VALIDATED - DATA SOURCE TRAVELSKY   \n"
				+ "TKT/TL26FEB16*1149  \n"
				+ "COMMISSION  3.00 PERCENT OF GROSS   \n"
				+ "RFSONLN/1E /EFEP_37/FCC=W/";
//		new QteBySegmentsParser().getQteResultByQteTxt(s5);
	}

}
