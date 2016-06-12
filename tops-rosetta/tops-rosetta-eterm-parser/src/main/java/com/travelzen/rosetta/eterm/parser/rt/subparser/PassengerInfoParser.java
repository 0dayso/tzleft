package com.travelzen.rosetta.eterm.parser.rt.subparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.rosetta.eterm.common.pojo.enums.PassengerType;
import com.travelzen.rosetta.eterm.common.pojo.rt.PassengerInfo;
import com.travelzen.rosetta.eterm.common.pojo.rt.PassengerInfo.Passenger;
import com.travelzen.rosetta.eterm.parser.util.RtDateTimeUtil;

/**
 * 乘客信息解析
 * @author yiming.yan
 */
public enum PassengerInfoParser {
	
	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PassengerInfoParser.class);
	
	private static Pattern pattern = Pattern.compile(
//			"\\d\\.(?<name>[\\u4e00-\\u9fa5A-Z/ \\n]+?) ?(?<note>CHD|M[RS]|MSTR|MISS|SD|SC)?"
//			"\\d\\.(?<name>\\D+?) ?(?<note>CHD|M[RS]|MSTR|MISS|SD|SC)?"
			"\\d\\.(?<name>\\D+?) ?(?<note>CHD|MSTR|MISS)?"
			+ "(?:(?: DOB)?\\((?<birth>\\d{2}[A-Z]{3}\\d{2})\\))?"
			+ "[ \\n]+(?=\\d{1,3}\\.|[0-9A-Z]{6}[ \\n]*$|$)");
	private static Pattern pattern_group = Pattern.compile(
			"0\\.\\d+.+ NM\\d+"); //　占座个数和实际乘客人数可能不一样
//			"0\\.(?<number>\\d+).+ NM\\k<number>");
	
	public static PassengerInfo parse(String text, boolean isDomestic) {
		PassengerInfo passengerInfo = new PassengerInfo();
		List<Passenger> passengerList = new ArrayList<Passenger>();
		Matcher matcher = pattern.matcher(text + " "); // why '+ ""'? try text10 below
		int psgNo = 0;
		while (matcher.find()) {
			Passenger passenger = new Passenger();
			passenger.setPsgNo(++psgNo);
			String name = matcher.group("name");
			if (name.endsWith("MR"))
				passenger.setSex("M");
			if (name.endsWith("MS"))
				passenger.setSex("F");
			if (!name.matches("[A-Z/ ]+"))
				name = name.replaceAll("\\s", "");
			passenger.setName(name);
			if (matcher.group("note") != null) {
				// 某些航空公司要求12岁以下小孩加不同称谓 MSTR男童 MISS女童
				if (matcher.group("note").matches("CHD|MSTR|MISS")) {
					passenger.setPsgType(PassengerType.CHD);
					if (!passengerInfo.hasChild())
						passengerInfo.setHasChild(true);
				} else {
					passenger.setTitle(matcher.group("note"));
				}
				if (matcher.group("note").matches("MSTR"))
					passenger.setSex("M");
				if (matcher.group("note").matches("MISS"))
					passenger.setSex("F");
					
			}
			if (matcher.group("birth") != null) {
				passenger.setBirthday(RtDateTimeUtil.parseDate(matcher.group("birth")));
			}
			passengerList.add(passenger);
		}
		if (passengerList.size() == 0) {
			LOGGER.error("PNR解析：乘客解析失败！解析文本：{}", text);
		}
		passengerInfo.setPassengers(passengerList);
		Matcher matcher_group = pattern_group.matcher(text);
		if (matcher_group.find())
			passengerInfo.setGroup(true);
		return passengerInfo;
	}
	
	public static void main(String[] args) {
		String text0 = " 1.";
		String text1 = " 1.刘彤 2.ZHOU/WENYI JXEBNC";
		String text2 = " 1.刘彤CHD 2.巴可拉达INF JXEBNC";
		String text3 = " 1.QIN/KEWEI CHD 2.QIN/YOU 3.WEI/XIANGXU CHD KTYW5P";
		String text4 = " 1.LI/YITONG MR 2.ZHOU/WENYI MS JXEBNC";
		String text5 = " 1.LI/YI TONG MR 2.ZHOU/WENYI MS JXEBNC";
		String text6 = "   1.DO/NGOCSON 2.DUONG/TUNGLAM 3.LE/NGOCDE 4.NGUYEN/THIHUONG 5.NGUYEN/THINGOCLAN    JVGDQK";
		String text7 = " 1.BONDON/THEA 2.BONNIN/SOFIA 3.BOUILLET/LEANA 4.BUREAU/THOMAS  \n"
					+ " 5.CLERGUE/AUGUSTIN 6.DESMONS/AMBRE 7.FAYNOT/KEANU NOHEA 8.GUEBEN/ALEXANDRE \n"
					+ " 9.HUET/LENA JSNZR1\n";
		String text8 = " 1.FLOTOW/PETER KAJ C MR 2.SJOBERG/CECILIA J M MS 3.SJOBERG/SAGA L M CHD\n"
				+ " 4.SJOBERG/STELLA N M CHD KSD2P9";
		String text9 = " 1.WANG/WEICHEN MS 2.WONG/JHENJIA MSTR 3.WONG/SHIHAN MR KW2WK5";
		// AFTER RTN
		String text10 = " 0.10AAA NM10 KGG6RY\n"
				+ " 1.测验 2.大富翁 3.读我 4.覅改成 5.谷歌 6.晚一点 7.尭吧\n"
				+ " 8.一二 9.医生 10.一样";
		// 玩我呢！！！
		String text11 = "   1.LEE/ANNECHD 2.LEE/ANNXZEMS 3.SIA/LAYKHOONMS HXHTG0";
		// 儿童带生日
		String text12 = " 1.DAI/ZIXUAN CHD DOB(16AUG09) HP153W";
		String text13 = " 1.DAI/ZIXUAN CHD DOB(16AUG09) 2.DAI/ZIHE CHD DOB(16AUG09) HP153W";
		// 人名和PNR混淆
		String text14 = " 1.ASLAM/AFAQ 2.KHAN/HINA SHAFQAT\n"
				+ " 3.MIAN/FARRUKH NAZEER JSLZZ2\n";
		// 人名被截断
		String text15 = " 1.何秀华 2.黄先康 3.陆博特张 4.陆琦 5.裴晓华 6.卫何婧   7.卫建华 8.徐春花 9.徐 \n"
				+ "锘 JYKH90 ";
		// 序号后没点（确认存在，存疑？）
		String text16 = " 1.卢华 2.石云琪 3.谭芳 4剡辰 JTFD0T";
		// 凉 or 凉 ???
		String text17 = "1.孙铸兴 2.王凉 3.郑鹏程 HG278D";
		String text18 = " 1.LEE/ANNE SD HB87IU";
		String text19 = " 1.POLITZ/ANNA 2.SEIDENBERGER/HEIKE 3.WITTGREBE/JOSHUA CHD(27OCT07) \n"
				+ " 4.WITTGREBE/SEBASTIAN HPHE9B  ";
		String text20 = "0.33CTRIPSHASM/INC NM32 KR94DG\n"
				+ "1.BIAN/HUIFANG 2.CAI/JUN 3.CAO/LU 4.CHEN/YUCHEN 5.FAN/WENXIAO 6.FEI/XIA\n"
				+ "7.GU/LINMEI 8.GU/YIQING 9.HUANG/FANG 10.JIANG/YUNSU 11.LI/HUITING 12.LI/ZONGZE 13.NI/YINGHAI 14.SUN/YU 15.WANG/JINJING 16.WANG/YILUN 17.WANG/YU 18.WU/FANG\n"
				+ "19.XU/CHAO 20.XU/KAN 21.XU/RUSHAN 22.YAO/JING 23.YE/WENYUN 24.YIN/LIPING\n"
				+ "25.YU/HAIYAN 26.YUAN/MENG 27.ZHANG/CHI 28.ZHANG/LI 29.ZHANG/LINYUN\n"
				+ "30.ZHANG/XIAOLING 31.ZHOU/JIA 32.ZHU/TINGTING";
		
		System.out.println(parse(text0, true));
		System.out.println(parse(text1, true));
		System.out.println(parse(text2, true));
		System.out.println(parse(text3, false));
		System.out.println(parse(text4, false));
		System.out.println(parse(text5, false));
		System.out.println(parse(text6, false));
		System.out.println(parse(text7, false));
		System.out.println(parse(text8, false));
		System.out.println(parse(text9, false));
		System.out.println(parse(text10, true));
		System.out.println(parse(text11, false));
		System.out.println(parse(text12, false));
		System.out.println(parse(text13, false));
		System.out.println(parse(text14, false));
		System.out.println(parse(text15, true));
		System.out.println(parse(text16, true));
		System.out.println(parse(text17, true));
		System.out.println(parse(text18, false));
		System.out.println(parse(text19, false));
		System.out.println(parse(text20, false));
	}
	
}
