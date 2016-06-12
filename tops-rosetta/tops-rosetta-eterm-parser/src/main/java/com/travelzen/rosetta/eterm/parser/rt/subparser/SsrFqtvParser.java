package com.travelzen.rosetta.eterm.parser.rt.subparser;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.rosetta.eterm.common.pojo.rt.MileageCardInfo;

/**
 * 里程卡信息解析
 * @author yiming.yan
 */
public enum SsrFqtvParser {

	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SsrFqtvParser.class);
	
	public static Set<MileageCardInfo> parse(String text, boolean isDomestic) {
		Set<MileageCardInfo> cardInfos = new HashSet<MileageCardInfo>();
		Pattern pattern = null;
		if (isDomestic)
			pattern = Pattern.compile("([0-9A-Z]{2}) ?(\\d+)(?:/C)?/P(\\d{1,2})");
		else
			pattern = Pattern.compile("([0-9A-Z]{2}) ?([0-9A-Z\\.]+)(?:/C)?/P(\\d{1,2})");
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			if (ifExist(matcher.group(2), cardInfos)) {
				continue;
			}
			MileageCardInfo cardInfo = new MileageCardInfo();
			cardInfo.setPsgNo(Integer.parseInt(matcher.group(3)));
			cardInfo.setAirCompany(matcher.group(1));
			cardInfo.setCardNo(matcher.group(2));
			cardInfos.add(cardInfo);
		}
		if (cardInfos.size() == 0)
			LOGGER.error("PNR解析：里程卡信息解析失败！解析文本：{}", text);
		return cardInfos;
	}
	
	private static boolean ifExist(String cardNo, Set<MileageCardInfo> cardInfos) {
		for (MileageCardInfo cardInfo:cardInfos) {
			if (cardNo.equals(cardInfo.getCardNo())) {
				return true;
			}
		}
		return false;
	}
	
	public static void main(String[] args) {
		String text0 = "";
		String text1 = " 6.SSR FQTV FM HK1 SHABAV 9437 R17JAN 3U624010154687/P1 \n"
				+ " 7.SSR FQTV FM HK1 SHABAV 9437 R17JAN 3U624010154687/P1";
		String text2 = "14.SSR FQTV CA HK1 SHACTU 4516 H19JAN CA000211517320/P3 \n"
				+ "15.SSR FQTV CA HK1 CTUSHA 4501 G20JAN CA000211517320/P3";
		String text3 = "12.SSR FQTV TV HK1 LXACTU 9824 I05JUN CA125000326525/P4  \n"
				+ "13.SSR FQTV TV HK1 LXACTU 9824 I05JUN CA125512758508/P3       \n"
				+ "14.SSR FQTV TV HK1 LXACTU 9824 I05JUN CA008761324281/P2    ";
		String text4 = " 10.SSR FQTV CZ HK/ KEBK13692587.1/C/P1     ";
		
		System.out.println(parse(text0, true));
		System.out.println(parse(text1, true));
		System.out.println(parse(text2, true));
		System.out.println(parse(text3, true));
		System.out.println(parse(text4, false));
	}
	
}
