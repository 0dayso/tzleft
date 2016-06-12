package com.travelzen.pnr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.etermface.service.nlp.TimeLimitParser;
import com.travelzen.etermface.service.nlp.TimeLimitTokenRepo;
import com.travelzen.etermface.service.util.PnrDateUtil;

public class TestTimeLimit extends TestCase {

	private static Logger logger = LoggerFactory.getLogger(TestTimeLimit.class);

	private String process(final String timelimitStr) {

		String tktLimit = "";
		// boolean recoginizeTicketLimit = false;

		while (true) {
			Matcher m = PnrDateUtil.TIME_LIMIT_PATTERN.matcher(timelimitStr);
			if (m.find()) {
				// recoginizeTicketLimit = true;
				String adtkTimelimit = m.group(1);
				tktLimit = PnrDateUtil.getTimeLimitFromADTK(adtkTimelimit);
				break;
			}

			m = PnrDateUtil.TIME_LIMIT_PATTERN_2.matcher(timelimitStr);
			if (m.find()) {
				// recoginizeTicketLimit = true;
				tktLimit = PnrDateUtil.getTimeLimitFromADTK2(m.group(0));

				break;
			}

			m = PnrDateUtil.TIME_LIMIT_PATTERN_3.matcher(timelimitStr);
			if (m.find()) {

				// recoginizeTicketLimit = true;

				String dateStr = m.group(1);
				String timeStr = m.group(m.groupCount());

				tktLimit = PnrDateUtil.getTimeLimitFromADTK3_4(dateStr, timeStr);
				break;
			}

			m = PnrDateUtil.TIME_LIMIT_PATTERN_4.matcher(timelimitStr);
			if (m.find()) {

				// recoginizeTicketLimit = true;

				String dateStr = m.group(1);
				String timeStr = m.group(m.groupCount());

				tktLimit = PnrDateUtil.getTimeLimitFromADTK3_4(dateStr, timeStr);
				break;
			}

			break;

		}

		return tktLimit;
	}

	public static void main(String arv[]) {
		new TestTimeLimit().test8();
	}

	@Test
	public void test8() {
		
		String[] timelimitsOTHS = {
				
				"SSR OTHS 1E ULTL/ PLS ADVS TKT BY 06AUG13 20:43 SHA LT",		
				"SSR OTHS 1E AUTO XX IF SSR TKNA/E/M/C NOT RCVD BY MH BY 0250/07AUG/ SHA LT",
				"SSR OTHS 1E IRC-2/ADV OTO TKT OR XX BY 09AUG/1200",
				"SSR OTHS 1E ATTN LAST DAY FOR TICKETING 07AUG13 OR PNR WILL BE CNLD" 
		};
		
		
		
		
		String[] timelimits = {
				
				
		"SSR OTHS 1E ULTL/ PLS ADVS TKT BY 06AUG13 20:43 SHA LT",		

		"SSR ADTK 1E TO BR BY 08MAY 1100 OTHERWISE WILL BE XLD",

		"SSR ADTK 1E PLZ ADTK B4 1600/21MAR FR SHATSAGE0337V24JUN OR AUTO CANC EL ",

		"SSR ADTK 1E NBR TO OZ BY 12DEC13 1555 PEK//OR WL BE XXLD",

		"SSR OTHS 1E AUTO XX IF SSR TKNA/E/M/C NOT RCVD BY MH BY 0250/07AUG/ SHA LT",

		"SSR ADTK 1E TO AF BY 07AUG OTHERWISE WILL BE XLD",

		"SSR ADTK 1E BY SHA06AUG13/1639 OR CXL NX 110 W13AUG",

		"SSR OTHS 1E IRC-2/ADV OTO TKT OR XX BY 09AUG/1200",

		"SSR ADTK 1E ADV TKT NBR TO CX/KA BY 05AUG GMT 2359 OR SUBJECT TO CANCEL",

		"SSR ADTK 1E PLZ ADTK B4 1200/06AUG FR PVGTPECI0502T08AUG OR AUTO CANC EL",

		"SSR OTHS 1E ATTN LAST DAY FOR TICKETING 07AUG13 OR PNR WILL BE CNLD" };

		for (String str : timelimits) {

			TimeLimitTokenRepo repo = new TimeLimitParser(str).parse();

			
			logger.info(str);
//			logger.info(PnrDateUtil.getTimeLimitFromTokenRepo(repo));
			//
			//
			// String ret = process(str);
			// if(StringUtils.isBlank(ret)){
			// System.out.println(str);
			// }else{
			// System.out.println(ret);
			// }

		}

	}

	@Test
	public void testPNR() {

		Matcher m;
		//
		String t2 = "32.SSR ADTK 1E NBR TO OZ BY 02AUG13 SEL//OR WL BE XXLD  ";
		Pattern TIME_LIMIT_PATTERN_2 = Pattern.compile("(\\d{2}" + PnrDateUtil.MON_3CODE_PSTR_WITHPAIR + "\\d{2})");
		m = TIME_LIMIT_PATTERN_2.matcher(t2);
		if (m.find()) {
			// logger.info(m.group());
		}

		// SHA03MAY13/2135
		// Pattern TIME_LIMIT_PATTERN = Pattern.compile("(\\w{3}\\d{2}"+
		// PnrDateUtil.MON_3CODE_PSTR_WITHPAIR+"\\d{2}/\\d{4})");

		Pattern TIME_LIMIT_PATTERN = PnrDateUtil.TIME_LIMIT_PATTERN;
		String t1 = "SSR ADTK 1E BY SHA03AUG13/1023 OR CXL CZ3082 Q06AUG";
		// String t1 = "SHA03MAY13/2135";
		m = TIME_LIMIT_PATTERN.matcher(t1);
		if (m.find()) {
			// logger.info(m.group());
		}

		// 团体票

		// SSR ADTK 1E TO BR BY 08MAY 1100 OTHERWISE WILL BE XLD
		String t3 = "SSR ADTK 1E TO BR BY 08MAY 1100 OTHERWISE WILL BE XLD";
		// String t4 = "SSR ADTK 1E TO BR BY 08MAY1100 OTHERWISE WILL BE XLD";
		Pattern TIME_LIMIT_PATTERN_3 = Pattern.compile("(\\d{2}" + PnrDateUtil.MON_3CODE_PSTR_WITHPAIR + ")\\s*(\\d{4})");

		m = TIME_LIMIT_PATTERN_3.matcher(t3);

		String t4 = "SSR ADTK 1E ADV TKT NBR TO CX/KA BY 05AUG GMT 2359 OR SUBJECT TO CANCEL";
		Pattern TIME_LIMIT_PATTERN_4 = PnrDateUtil.TIME_LIMIT_PATTERN_4;

		m = TIME_LIMIT_PATTERN_4.matcher(t4);

		if (m.find()) {

			// logger.info("cnt:" + m.groupCount());

			// logger.info(m.group());
			for (int i = 0; i <= m.groupCount(); i++) {
				// logger.info(i + ":" + m.group(i));
			}
		}

		// groupCount=15, 从 group(0)到group(15)分别是：
		// 0:08MAY 1100
		// 1:08MAY
		// 2:MAY
		// 3:null
		// 4:null
		// 5:null
		// 6:null
		// 7:MAY
		// 8:null
		// 9:null
		// 10:null
		// 11:null
		// 12:null
		// 13:null
		// 14:null
		// 15:1100

		if (m.find()) {

			// logger.info("cnt:" + m.groupCount());

			// logger.info(m.group());
			for (int i = 0; i <= m.groupCount(); i++) {
				// logger.info(i + ":" + m.group(i));
			}
		}

		// m = TIME_LIMIT_PATTERN_3.matcher(t4);
		// if (m.find()) {
		// logger.info("cnt:"+m.groupCount());
		// logger.info(m.group());
		// logger.info(m.group(1));
		// logger.info(m.group(3));
		// }

	}
}
