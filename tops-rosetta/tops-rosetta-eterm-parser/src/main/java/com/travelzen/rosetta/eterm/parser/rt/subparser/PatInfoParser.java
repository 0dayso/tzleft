package com.travelzen.rosetta.eterm.parser.rt.subparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.rosetta.eterm.common.pojo.rt.PatInfo;
import com.travelzen.rosetta.eterm.common.pojo.rt.PatInfo.PatFare;

public enum PatInfoParser {

	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PatInfoParser.class);
	
	private static Pattern pattern = Pattern.compile(
			"PAT:A(\\*CH|\\*IN|#CGP/CC|#C\\d+)? *\n"
			+ "(?: *\\d+ .+\n"
			+ " *SFC.+(?:\n|$))+");
	
	public static PatInfo parse(String text) {
		LOGGER.info("开始PAT报价解析");
		PatInfo patInfo = new PatInfo();
		List<PatFare> fares = new ArrayList<PatFare>();
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			PatFare patFare = null;
			String type = matcher.group(1);
			if (null == type) {
				LOGGER.info("开始解析成人报价");
				patFare = PatFareParser.parseAdt(matcher.group());
				LOGGER.info("成人报价解析结果：{}", patFare);
			} else if (type.contains("CH")) {
				LOGGER.info("开始解析儿童报价");
				patFare = PatFareParser.parseChd(matcher.group());
				LOGGER.info("儿童报价解析结果：{}", patFare);
			} else if (type.contains("IN")) {
				LOGGER.info("开始解析婴儿报价");
				patFare = PatFareParser.parseInf(matcher.group());
				LOGGER.info("婴儿报价解析结果：{}", patFare);
			} else if (type.contains("CGP/CC")) {
				LOGGER.info("开始解析政府报价");
				patFare = PatFareParser.parseGov(matcher.group());
				LOGGER.info("政府报价解析结果：{}", patFare);
			} else if (type.contains("#C")) {
				LOGGER.info("开始解析三方报价");
				patFare = PatFareParser.parseTrd(matcher.group());
				LOGGER.info("三方报价解析结果：{}", patFare);
			}
			if (null != patFare)
				fares.add(patFare);
		}
		if (fares.size() == 0) {
			LOGGER.error("PatInfoParser未解析出PAT价格。");
			return null;
		}
		patInfo.setFares(fares);
		return patInfo;
	}
	
	public static void main(String[] args) {
		String s1 = "  (PAT:A  \n"
				+ "01 H FARE:CNY860.00 TAX:CNY50.00 YQ:TEXEMPTYQ  TOTAL:910.00 \n"
				+ " SFC:01    SFN:01\n"
				+ "01 HH FARE:CNY860.00 TAX:CNY50.00 YQ:TEXEMPTYQ  TOTAL:910.00 \n"
				+ " SFC:02    SFN:02\n"
				+ "  CAN NOT USE *CH FOR NON CHD PASSENGER\n"
				+ "  (PAT:A*IN   \n"
				+ "01 YIN FARE:CNY110.00 TAX:TEXEMPTCN YQ:TEXEMPTYQ  TOTAL:110.00  \n"
				+ " SFC:01    SFN:01\n"
				+ "  (PAT:A#CGP/CC   \n"
				+ "01 H FARE:CNY860.00 TAX:CNY50.00 YQ:TEXEMPTYQ  TOTAL:910.00 \n"
				+ " SFC:01    SFN:01   \n"
				+ "02 HGP95 FARE:CNY820.00 TAX:CNY50.00 YQ:TEXEMPTYQ  TOTAL:870.00 \n"
				+ " SFC:02    SFN:02";
		String s2 = ">PAT:A  \n"
				+ "01 H FARE:CNY860.00 TAX:CNY50.00 YQ:TEXEMPTYQ  TOTAL:910.00 \n"
				+ "SFC:01   SFN:01\n"
				+ "01 HH FARE:CNY860.00 TAX:CNY50.00 YQ:TEXEMPTYQ  TOTAL:910.00 \n"
				+ "SFC:02   SFN:02\n"
				+ "CAN NOT USE *CH FOR NON CHD PASSENGER\n"
				+ ">PAT:A*IN   \n"
				+ "01 YIN FARE:CNY110.00 TAX:TEXEMPTCN YQ:TEXEMPTYQ  TOTAL:110.00  \n"
				+ "SFC:01   SFN:01\n"
				+ ">PAT:A#CGP/CC   \n"
				+ "01 H FARE:CNY860.00 TAX:CNY50.00 YQ:TEXEMPTYQ  TOTAL:910.00 \n"
				+ "SFC:01   SFN:01   \n"
				+ "02 HGP95 FARE:CNY820.00 TAX:CNY50.00 YQ:TEXEMPTYQ  TOTAL:870.00 \n"
				+ "SFC:02   SFN:02";
		String s3 = "   >PAT:A    \n"
				+ "   01 V FARE:CNY630.00 TAX:CNY50.00 YQ:TEXEMPTYQ  TOTAL:680.00   \n"
				+ "   SFC:01   SFN:01 ";
		String s4 = "PAT:A  \n"
				+ ">PAT:A 01 B FARE:CNY1530.00 TAX:CNY50.00 YQ:TEXEMPTYQ  TOTAL:1580.00   SFC:01   SFN:01";
		String s5 = "pat:a#c9932220 \n"
				+ ">PAT:A#C9932220 \n"
				+ "01 H FARE:CNY1340.00 TAX:CNY50.00 YQ:TEXEMPTYQ TOTAL:1390.00 \n"
				+ "SFC:01 SFN:01";
		
		System.out.println(parse(s1));
		System.out.println(parse(s2));
		System.out.println(parse(s3));
		System.out.println(parse(s4));
		System.out.println(parse(s5));
	}

}
