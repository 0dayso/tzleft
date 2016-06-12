package com.travelzen.rosetta.eterm.parser.rt.subparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.rosetta.eterm.common.pojo.enums.PassengerIdType;
import com.travelzen.rosetta.eterm.common.pojo.enums.PassengerType;
import com.travelzen.rosetta.eterm.common.pojo.rt.PassengerInfo;
import com.travelzen.rosetta.eterm.common.pojo.rt.PassengerInfo.Passenger;
import com.travelzen.rosetta.eterm.common.pojo.rt.PassengerInfo.PassengerId;
import com.travelzen.rosetta.eterm.parser.util.RtDateTimeUtil;

/**
 * 国际证件信息解析
 * @author yiming.yan
 */
public enum SsrDocsParser {

	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SsrDocsParser.class);
	
	public static List<PassengerId> parse(String text) {
		List<PassengerId> passengerIds = new ArrayList<PassengerId>();
		Matcher matcher_line = Pattern.compile(""
				+ "SSR DOCS "
				+ "[\\w\\W]+?"
				+ "/P\\d+[ +-]*(?:\n|$)").matcher(text);
		Pattern pattern_detail = Pattern.compile(""
				+ "SSR DOCS [A-Z0-9]{2} [A-Z]{2}\\d+ "
				+ "(?<idType>[A-Z])"
				+ "/(?<issuedat>[A-Z]+)/(?<id>[0-9A-Z]+)/(?<nationality>[A-Z]+)"
				+ "/(?<birthday>\\d{2}[A-Z]{3}\\d{2})"
				+ "/(?<sex>M|F|MI|FI)"
				+ "/(?<validity>\\d{2}[A-Z]{3}\\d{2})"
				+ "(?:/H)?/(?<name>[A-Z/ ]+?)(?:/H)?\\s*"
				+ "/P(?<psgNo>\\d+)");
		while (matcher_line.find()) {
			Matcher matcher_detail = pattern_detail.matcher(matcher_line.group().replaceAll("\n", ""));
			if (matcher_detail.find()) {
				PassengerId pid = new PassengerId();
				pid.setPsgNo(Integer.parseInt(matcher_detail.group("psgNo")));
				pid.setName(matcher_detail.group("name"));
				pid.setIdType(matcher_detail.group("idType"));
				pid.setId(matcher_detail.group("id"));
				pid.setIssuedAt(matcher_detail.group("issuedat"));
				pid.setNationality(matcher_detail.group("nationality"));
				pid.setBirthday(RtDateTimeUtil.parseDate(matcher_detail.group("birthday")));
				pid.setValidity(RtDateTimeUtil.parseDate(matcher_detail.group("validity")));
				pid.setSex(matcher_detail.group("sex"));
				passengerIds.add(pid);
			} else
				System.out.println("error: " + matcher_line.group());
		}
		return passengerIds;
	}
	
	public static PassengerInfo parse(String text, PassengerInfo passengerInfo) {
		Matcher matcher_line = Pattern.compile(""
				+ "SSR DOCS "
				+ "[\\w\\W]+?"
				+ "/P\\d+[ +-]*(?:\n|$)").matcher(text);
		Pattern pattern_detail = Pattern.compile(""
				+ "SSR DOCS [A-Z0-9]{2} [A-Z]{2}\\d+ "
				+ "(?<idType>[A-Z])"
				+ "/(?<issuedat>[A-Z]+)/(?<id>[0-9A-Z]+)/(?<nationality>[A-Z]+)"
				+ "/(?<birthday>\\d{2}[A-Z]{3}\\d{2})"
				+ "/(?<sex>M|F|MI|FI)"
				+ "/(?<validity>\\d{2}[A-Z]{3}\\d{2})"
				+ "(?:/H)?/(?<name>[A-Z/ ]+?)(?:/H)?\\s*"
				+ "/P(?<psgNo>\\d+)");
		while (matcher_line.find()) {
			Matcher matcher_detail = pattern_detail.matcher(matcher_line.group().replaceAll("\n", ""));
			if (matcher_detail.find()) {
				int psgNo = Integer.parseInt(matcher_detail.group("psgNo"));
				boolean isAdult = matcher_detail.group("sex").length()==1?true:false;
				if (passengerInfo.getPassengers().size() < psgNo) {
					LOGGER.error("PNR解析：国际证件信息解析失败！乘客序号不匹配！解析文本：{}", matcher_detail.group());
					continue;
				}
				for (int i = 0; i < passengerInfo.getPassengers().size(); i++) {
					Passenger passenger = passengerInfo.getPassengers().get(i);
					if ((isAdult && passenger.getPsgNo() == psgNo) || (!isAdult && passenger.getFoPsgNo() == psgNo)) {
						if (matcher_detail.group("idType").equals("P"))
							passengerInfo.getPassengers().get(i).setIdType(PassengerIdType.PP);
						else
							passengerInfo.getPassengers().get(i).setIdType(PassengerIdType.OTHER);
						passengerInfo.getPassengers().get(i).setId(matcher_detail.group("id"));
						passengerInfo.getPassengers().get(i).setIssuedAt(matcher_detail.group("issuedat"));
						passengerInfo.getPassengers().get(i).setNationality(matcher_detail.group("nationality"));
						passengerInfo.getPassengers().get(i).setBirthday(RtDateTimeUtil.parseDate(matcher_detail.group("birthday")));
						passengerInfo.getPassengers().get(i).setValidity(RtDateTimeUtil.parseDate(matcher_detail.group("validity")));
						passengerInfo.getPassengers().get(i).setSex(matcher_detail.group("sex"));
						break;
					} else if (!isAdult) {
						if (ifExist(psgNo, passengerInfo.getPassengers()).getValue0())
							break;
						Passenger infant = new Passenger(PassengerType.INF);
						infant.setName(matcher_detail.group("name"));
						if (matcher_detail.group("idType").equals("P"))
							infant.setIdType(PassengerIdType.PP);
						else
							infant.setIdType(PassengerIdType.OTHER);
						infant.setId(matcher_detail.group("id"));
						infant.setIssuedAt(matcher_detail.group("issuedat"));
						infant.setNationality(matcher_detail.group("nationality"));
						infant.setBirthday(RtDateTimeUtil.parseDate(matcher_detail.group("birthday")));
						infant.setValidity(RtDateTimeUtil.parseDate(matcher_detail.group("validity")));
						infant.setSex(matcher_detail.group("sex"));
						infant.setFoPsgNo(psgNo);
						passengerInfo.getPassengers().add(infant);
						break;
					}
				}
			} else
				LOGGER.error("PNR解析：国际证件信息解析失败！解析文本：{}", matcher_line.group());
		}
		return passengerInfo;
	}
	
	private static Pair<Boolean, Integer> ifExist(int foPsgNo, List<Passenger> passengers) {
		for (int i = 0; i < passengers.size(); i++) {
			Passenger passenger = passengers.get(i);
			if (foPsgNo == passenger.getFoPsgNo())
				return Pair.with(true, i);
		}
		return Pair.with(false, 0);
	}
	
	public static void main(String[] args) {
		String text0 = "";
		String text1 = "11.SSR DOCS MI HK1 P/CHN/E39552731/CHN/31MAR13/MI/16NOV19/TANG/WEN/P1   ";
		String text2 = "14.SSR DOCS MU HK1 P/JP/TZ0500806/JP/02FEB59/M/07JUN17/NAKAGAWA/KIYOHITO/P1 ";
		String text3 = "14.SSR DOCS CA HK1 P/CN/E05935086/CN/27DEC55/F/16DEC22/LI/XIANG/ZHI/P1  ";
		String text4 = "15.SSR DOCS CA HK1 P/CN/G21280899/CN/05NOV80/F/12FEB17/WU/RURU/P2   ";
		String text5 = "15.SSR DOCS CI HK1 P/TW/00264023/TW/16MAY64/M/30DEC16/YU/TZU CHEN MR/P1 ";
		System.out.println(parse(text0));
		System.out.println(parse(text1));
		System.out.println(parse(text2));
		System.out.println(parse(text3));
		System.out.println(parse(text4));
		System.out.println(parse(text5));
		
		String text6 = "336.SSR DOCS AA HK1 P/CHN/E32620501/CHN/27APR56/M/07NOV23/XU/SHENG HONG/H/P20   337.SSR DOCS AA HK1 P/CHN/G55541131/CHN/13SEP56/F/22SEP21/SONG/WEI BIN/H/P19 \n"
				+ "338.SSR DOCS AA HK1 P/CHN/G26329050/CHN/01AUG54/M/17DEC17/YU/DE JUN/H/P18   \n"
				+ "339.SSR DOCS AA HK1 P/CHN/G33406292/CHN/27SEP55/F/04FEB19/HUANG/LI HUA/H/P17\n"
				+ "340.SSR DOCS AA HK1 P/CHN/E10850088/CHN/11SEP60/F/04DEC22/TANG/XIAO YAN/H/P16  \n"
				+ "341.SSR DOCS AA HK1 P/CHN/G55202240/CHN/26SEP54/F/06SEP21/GONG/RONG FANG/H     \n"
				+ "    /P15  \n"
				+ "342.SSR DOCS AA HK1 P/CHN/G41572762/CHN/08SEP86/F/25APR20/LANG/MEI/H/P8 \n"
				+ "343.SSR DOCS AA HK1 P/CHN/G42500281/CHN/07AUG63/F/06MAY20/ZHU/GUO YING/H/P7 ";
		System.out.println(parse(text6));
		
		String text7 = "28.SSR DOCS MU HK1 P/FR/12DD17217/FR/31MAY85/M/26SEP22/LEGOFF/LAURENT/P1\n"
				+ "29.SSR DOCS AF HK1 P/FR/12DD17217/FR/31MAY85/M/26SEP22/LEGOFF/LAURENT/P1\n"
				+ "30.SSR DOCS MU HK1 P/CN/E21678529/CN/09MAY82/F/05JUN23/TAN/YAN/P2 \n"
				+ "31.SSR DOCS AF HK1 P/CN/E21678529/CN/09MAY82/F/05JUN23/TAN/YAN/P2   \n"
				+ "32.SSR DOCS MU HK1 P/CN/E52459606/CN/08AUG14/MI/02JUN20/H/TAN/JINGYU/P1 \n"
				+ "33.SSR DOCS AF HK1 P/CN/E52459606/CN/08AUG14/MI/02JUN20/H/TAN/JINGYU/P1 ";
		System.out.println(parse(text7));
		
		String text8 = "8.SSR DOCS KA HK1 P/CN/1236573698741/CN/25SEP86/M/30APR16/WANG/\n"
				+ "/XIAOXIAOXIAOXIAO/P4\n"
				+ "9.SSR DOCS KA HK1 P/CN/E50735319/CN/18APR88/M/27JAN17/KOH/KIN CHUAN LINA/P3   +\n"
				+ "10.SSR DOCS MU HK1 P/CL/P0000001/CM/17JUN82/M/20JUL17/WANG/YI/P2";
		System.out.println(parse(text8));
	}

}
