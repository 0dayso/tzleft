package com.travelzen.etermface.service.fare.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.etermface.common.pojo.PassengerType;
import com.travelzen.etermface.common.pojo.fare.PatFareResponse.PatFare;
import com.travelzen.etermface.common.pojo.fare.PatFareResponse.PatFareItem;

/**
 * PAT解析
 * @author yiming.yan
 */
public enum PatFareParser {

	;
	
	private static Logger logger = LoggerFactory.getLogger(PatFareParser.class);
	
	private static Pattern pattern_segment = Pattern.compile("[\n\r]\\d{2} ([\\w\\W]+?)(?=[\n\r]\\d{2}|$)");
	
	private static Pattern pattern_fareBasis = Pattern.compile("^(.+) FARE");
	private static Pattern pattern_fare = Pattern.compile("FARE:([A-Z]{3}\\d+\\.\\d{2})");
	private static Pattern pattern_tax = Pattern.compile("TAX:[A-Z]{3}(\\d+\\.\\d{2})");
	private static Pattern pattern_yq = Pattern.compile("YQ:[A-Z]{3}(\\d+\\.\\d{2})");
	private static Pattern pattern_total = Pattern.compile("TOTAL:(\\d+\\.\\d{2})");
	private static Pattern pattern_sfc = Pattern.compile("SFC:(\\d{2})");
	
	public static PatFare parseGov(String pat) {
		if (pat.startsWith("  NO ACTIVE")){
			logger.info("PAT:A解析 NO ACTIVE");
			return null;
		}
		if (pat.startsWith("  NO PNR")){
			logger.info("PAT:A解析 NO PNR");
			return null;
		}
		PatFare fare = new PatFare(PassengerType.GOV);
		List<PatFareItem> fareItems = new ArrayList<PatFareItem>();
		Matcher matcher = pattern_segment.matcher(pat);
		while (matcher.find()) {
			PatFareItem fareItem = parseAdtItem(matcher.group(1));
			if (null != fareItem)
				fareItems.add(fareItem);
		}
		if (fareItems.size() == 0) {
			logger.info("PAT政府报价解析失败！解析文本：" + pat);
			return null;
		}
		fare.setFareItems(fareItems);
		fare.setPatText(pat);
		return fare;
	}
	
	public static PatFare parseAdt(String pat) {
		if (pat.startsWith("  NO ACTIVE")){
			logger.info("PAT:A解析 NO ACTIVE");
			return null;
		}
		if (pat.startsWith("  NO PNR")){
			logger.info("PAT:A解析 NO PNR");
			return null;
		}
		PatFare fare = new PatFare(PassengerType.ADT);
		List<PatFareItem> fareItems = new ArrayList<PatFareItem>();
		Matcher matcher = pattern_segment.matcher(pat);
		while (matcher.find()) {
			PatFareItem fareItem = parseAdtItem(matcher.group(1));
			if (null != fareItem)
				fareItems.add(fareItem);
		}
		if (fareItems.size() == 0) {
			logger.info("PAT:A解析失败！解析文本：" + pat);
			return null;
		}
		fare.setFareItems(fareItems);
		fare.setPatText(pat);
		return fare;
	}
	
	private static PatFareItem parseAdtItem(String patItem) {
		Matcher matcher_fareBasis = pattern_fareBasis.matcher(patItem);
		Matcher matcher_fare = pattern_fare.matcher(patItem);
		Matcher matcher_tax = pattern_tax.matcher(patItem);
		Matcher matcher_yq = pattern_yq.matcher(patItem);
		Matcher matcher_total = pattern_total.matcher(patItem);
		Matcher matcher_sfc = pattern_sfc.matcher(patItem);
		PatFareItem fareItem = new PatFareItem();
		if (matcher_fareBasis.find() && matcher_fare.find() && matcher_total.find()) {
			fareItem.setFareBasis(matcher_fareBasis.group(1));
			fareItem.setFaceFare(matcher_fare.group(1));
			fareItem.setCurrentFare(matcher_fare.group(1).substring(3));
			fareItem.setTotalFare(matcher_total.group(1));
		} else {
			logger.info("PAT:A segment解析失败！解析文本：" + patItem);
			return null;
		}
		if (matcher_tax.find())
			fareItem.setCnTax(matcher_tax.group(1));
		if (matcher_yq.find())
			fareItem.setYqTax(matcher_yq.group(1));
		if (matcher_sfc.find())
			fareItem.setSfc(matcher_sfc.group(1));
		fareItem.setPatItemText(patItem);
		return fareItem;
	}
	
	public static PatFare parseChd(String patChd) {
		if (patChd.startsWith("  NO ACTIVE")){
			logger.info("PAT:A解析 NO ACTIVE");
			return null;
		}
		if (patChd.startsWith("  NO PNR")){
			logger.info("PAT:A解析 NO PNR");
			return null;
		}
		if (patChd.startsWith("  CAN NOT USE *CH")){
			logger.info("PAT:A解析 CAN NOT USE *CH");
			return null;
		}
		PatFare fare = new PatFare(PassengerType.CHD);
		List<PatFareItem> fareItems = new ArrayList<PatFareItem>();
		Matcher matcher = pattern_segment.matcher(patChd);
		while (matcher.find()) {
			PatFareItem fareItem = parseChdItem(matcher.group(1));
			if (null != fareItem)
				fareItems.add(fareItem);
		}
		if (fareItems.size() == 0) {
			logger.info("PAT:A*CH解析失败！解析文本：" + patChd);
			return null;
		}
		fare.setFareItems(fareItems);
		fare.setPatText(patChd);
		return fare;
	}

	private static PatFareItem parseChdItem(String patChdItem) {
		Matcher matcher_fareBasis = pattern_fareBasis.matcher(patChdItem);
		Matcher matcher_fare = pattern_fare.matcher(patChdItem);
		Matcher matcher_total = pattern_total.matcher(patChdItem);
		Matcher matcher_sfc = pattern_sfc.matcher(patChdItem);
		PatFareItem fareItem = new PatFareItem();
		if (matcher_fareBasis.find() && matcher_fare.find() && matcher_total.find()) {
			fareItem.setFareBasis(matcher_fareBasis.group(1));
			fareItem.setFaceFare(matcher_fare.group(1));
			fareItem.setCurrentFare(matcher_fare.group(1).substring(3));
			fareItem.setTotalFare(matcher_total.group(1));
		} else {
			logger.info("PAT:A*CH segment解析失败！解析文本：" + patChdItem);
			return null;
		}
		if (matcher_sfc.find())
			fareItem.setSfc(matcher_sfc.group(1));
		fareItem.setPatItemText(patChdItem);
		return fareItem;
	}
	
	public static PatFare parseInf(String patInf) {
		if (patInf.startsWith("  NO ACTIVE")){
			logger.info("PAT:A解析 NO ACTIVE");
			return null;
		}
		if (patInf.startsWith("  NO PNR")){
			logger.info("PAT:A解析 NO PNR");
			return null;
		}
		PatFare fare = new PatFare(PassengerType.INF);
		List<PatFareItem> fareItems = new ArrayList<PatFareItem>();
		Matcher matcher = pattern_segment.matcher(patInf);
		while (matcher.find()) {
			PatFareItem fareItem = parseInfItem(matcher.group(1));
			if (null != fareItem)
				fareItems.add(fareItem);
		}
		if (fareItems.size() == 0) {
			logger.info("PAT:A*IN解析失败！解析文本：" + patInf);
			return null;
		}
		fare.setFareItems(fareItems);
		fare.setPatText(patInf);
		return fare;
	}

	private static PatFareItem parseInfItem(String patInfItem) {
		Matcher matcher_fareBasis = pattern_fareBasis.matcher(patInfItem);
		Matcher matcher_fare = pattern_fare.matcher(patInfItem);
		Matcher matcher_total = pattern_total.matcher(patInfItem);
		Matcher matcher_sfc = pattern_sfc.matcher(patInfItem);
		PatFareItem fareItem = new PatFareItem();
		if (matcher_fareBasis.find() && matcher_fare.find() && matcher_total.find()) {
			fareItem.setFareBasis(matcher_fareBasis.group(1));
			fareItem.setFaceFare(matcher_fare.group(1));
			fareItem.setCurrentFare(matcher_fare.group(1).substring(3));
			fareItem.setTotalFare(matcher_total.group(1));
		} else {
			logger.info("PAT:A*IN segment解析失败！解析文本：" + patInfItem);
			return null;
		}
		if (matcher_sfc.find())
			fareItem.setSfc(matcher_sfc.group(1));
		fareItem.setPatItemText(patInfItem);
		return fareItem;
	}
	
	public static void main(String[] args) {
		String text0 = "";
		String text1 = "  (PAT:A  \n"
				+ "01 Y+Y FARE:CNY2480.00 TAX:CNY100.00 YQ:CNY60.00  TOTAL:2640.00 \n"
				+ " SFC:01    SFN:01/01    SFN:01/02\n"
				+ "02 Y/Y FARE:CNY1480.00 TAX:CNY100.00 YQ:CNY60.00  TOTAL:1640.00 \n"
				+ " SFC:02    SFN:01/01    SFN:01/02";
		String text2 = "  (PAT:A*CH   \n"
				+ "01 YCH FARE:CNY620.00 TAX:TEXEMPTCN YQ:CNY10.00  TOTAL:630.00   \n"
				+ " SFC:01    SFN:01";
		String text3 = "  (PAT:A*IN   \n"
				+ "01 YIN+YIN FARE:CNY240.00 TAX:TEXEMPTCN YQ:TEXEMPTYQ  TOTAL:240.00  \n"
				+ " SFC:01    SFN:01/01    SFN:01/02";
		String text4 = "  (PAT:A  \r"
				+ "01 YZ35WLDH FARE:CNY570.00 TAX:CNY50.00 YQ:TEXEMPTYQ  TOTAL:620.00  \r"
				+ " SFC:01    SFN:01";
		String text5 = "(PAT:A\n"
				+ "01 RT/R+RT/T FARE:CNY1240.00 TAX:CNY100.00 YQ:CNY220.00 TOTAL:1560.00\n"
				+ "SFC:01\n"
				+ "02 R+T FARE:CNY1380.00 TAX:CNY100.00 YQ:CNY220.00 TOTAL:1700.00\n"
				+ "SFC:02";
		System.out.println(parseAdt(text0));
		System.out.println(parseAdt(text1).toString());
		System.out.println(parseChd(text2).toString());
		System.out.println(parseInf(text3).toString());
		System.out.println(parseAdt(text4).toString());
		System.out.println(parseAdt(text5).toString());
	}

}
