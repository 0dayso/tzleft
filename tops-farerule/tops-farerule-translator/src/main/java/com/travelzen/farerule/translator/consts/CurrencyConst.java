package com.travelzen.farerule.translator.consts;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrencyConst {
	
	public static final Map<String, String> currencyMap = new HashMap<String, String>() {
		private static final long serialVersionUID = -5848743783049978622L;
	{
		put("AED", "阿联酋迪尔汗");
		put("AFN", "阿富汗尼");
		put("ALL", "阿尔巴尼亚列克");
		put("AMD", "亚美尼亚德拉姆");
		put("ANG", "荷属安的列斯盾");
		put("AOA", "安哥拉宽扎");
		put("ARS", "阿根廷比索");
		put("AUD", "澳元");
		put("AWG", "阿鲁巴弗罗林");
		put("AZM", "阿塞拜疆马纳特");
		put("BAM", "波黑马克");
		put("BBD", "巴巴多斯元");
		put("BDT", "孟加拉塔卡");
		put("BGN", "保加利亚列弗");
		put("BHD", "巴林第纳尔");
		put("BIF", "布隆迪法郎");
		put("BMD", "百慕大元");
		put("BND", "文莱元");
		put("BOB", "玻利维亚诺");
		put("BOV", "玻利维亚");
		put("BRL", "巴西雷亚尔");
		put("BSD", "巴哈马元");
		put("BTN", "不丹努扎姆");
		put("BWP", "博茨瓦纳普拉");
		put("BYR", "白俄罗斯卢布");
		put("BZD", "伯利兹元");
		put("CAD", "加拿大元");
		put("CDF", "刚果法郎");
		put("CHF", "瑞士法郎");
		put("CLF", "智利比索");
		put("CLP", "智利比索");
		put("CNY", "人民币");
		put("COP", "哥伦比亚比索");
		put("COU", "哥伦比亚");
		put("CRC", "--");
		put("CSD", "塞尔维亚第纳尔");
		put("CUP", "古巴比索");
		put("CVE", "佛得角埃斯库多");
		put("CYP", "塞浦路斯镑");
		put("CZK", "捷克克朗");
		put("DJF", "吉布提法郎");
		put("DKK", "丹麦克朗");
		put("DOP", "多米尼加比索");
		put("DZD", "阿尔及利亚第纳尔");
		put("ECU", "欧元");
		put("EEK", "爱沙尼亚克朗");
		put("EGP", "埃及镑");
		put("ERN", "厄立特里亚纳克法");
		put("ETB", "埃塞俄比亚比尔");
		put("EUR", "欧元");
		put("FJD", "斐济元");
		put("FKP", "福克兰镑");
		put("GBP", "英镑");
		put("GEL", "格鲁吉亚拉里");
		put("GHC", "加纳塞地");
		put("GIP", "直布罗陀镑");
		put("GMD", "冈比亚达拉西");
		put("GNF", "几内亚法郎");
		put("GTQ", "危地马拉格查尔");
		put("GYD", "圭亚那元");
		put("HKD", "港币");
		put("HNL", "洪都拉斯伦皮拉");
		put("HRK", "克罗地亚库纳");
		put("HTG", "海地古德");
		put("HUF", "匈牙利福林");
		put("IDR", "印度尼西亚盾");
		put("ILS", "以色列新谢克尔");
		put("INR", "印度卢比");
		put("IQD", "伊拉克第纳尔");
		put("IRR", "伊朗里亚尔");
		put("ISK", "冰岛克朗");
		put("JMD", "牙买加元");
		put("JOD", "约旦第纳尔");
		put("JPY", "日元");
		put("KES", "肯尼亚先令");
		put("KGS", "吉尔吉斯斯坦索姆");
		put("KHR", "柬埔寨瑞尔");
		put("KMF", "科摩罗法郎");
		put("KPW", "北韩元");
		put("KRW", "韩元");
		put("KWD", "科威特第纳尔");
		put("KYD", "开曼元");
		put("KZT", "--");
		put("LAK", "老挝基普");
		put("LBP", "黎巴嫩镑");
		put("LKR", "斯里兰卡卢比");
		put("LRD", "利比里亚元");
		put("LSL", "莱索托洛蒂");
		put("LTL", "立陶宛立特");
		put("LVL", "拉脱维亚拉特");
		put("LYD", "利比亚第纳尔");
		put("MAD", "摩洛哥迪拉姆");
		put("MDL", "摩尔多瓦列伊");
		put("MGA", "马达加斯加阿里亚里");
		put("MKD", "前南马其顿代纳尔");
		put("MMK", "缅甸元");
		put("MNT", "蒙古图格里克");
		put("MOP", "澳门元");
		put("MRO", "毛里塔尼亚乌吉亚");
		put("MTL", "马耳他里拉");
		put("MUR", "毛里求斯卢比");
		put("MVR", "马尔代夫拉菲亚");
		put("MWK", "--");
		put("MXN", "墨西哥比索");
		put("MXV", "墨西哥");
		put("MYR", "马来西亚令吉");
		put("MZM", "莫桑比克梅蒂卡尔");
		put("NAD", "纳米比亚元");
		put("NGN", "尼日利亚奈拉");
		put("NIO", "尼加拉瓜科多巴");
		put("NOK", "挪威克朗");
		put("NPR", "尼泊尔卢比");
		put("NZD", "新西兰元");
		put("OMR", "阿曼里亚尔");
		put("PAB", "巴拿马巴波亚");
		put("PEN", "秘鲁新索尔");
		put("PGK", "巴布亚新几内亚基那");
		put("PHP", "菲律宾比索");
		put("PKR", "巴基斯坦卢比");
		put("PLN", "波兰兹罗提");
		put("PYG", "巴拉圭瓜拉尼");
		put("QAR", "卡塔尔里亚尔");
		put("ROL", "罗马尼亚列伊");
		put("RUB", "俄罗斯卢布");
		put("RWF", "卢旺达法郎");
		put("SAR", "沙特里亚尔");
		put("SBD", "所罗门群岛元");
		put("SCR", "塞舌尔卢比");
		put("SDD", "苏丹第纳尔");
		put("SEK", "瑞典克朗");
		put("SGD", "新加坡元");
		put("SHP", "圣赫勒拿镑");
		put("SIT", "--");
		put("SKK", "斯洛伐克克朗");
		put("SLL", "塞拉利昂利昂");
		put("SOS", "索马里先令");
		put("SRD", "苏里南元");
		put("STD", "--");
		put("SVC", "萨尔瓦多科朗");
		put("SYP", "叙利亚镑");
		put("SZL", "斯威士兰里兰吉尼");
		put("THB", "泰铢");
		put("TJS", "塔吉克斯坦索莫尼");
		put("TMM", "土库曼斯坦马纳特");
		put("TND", "突尼西亚第纳尔");
		put("TOP", "汤加潘加");
		put("TRL", "土耳其里拉");
		put("TRY", "新土耳其里拉");
		put("TTD", "千里达及托巴哥元");
		put("TWD", "新台币");
		put("TZS", "坦桑尼亚先令");
		put("UAH", "乌克兰格里夫尼亚");
		put("UGX", "乌干达先令");
		put("USD", "美元");
		put("USN", "美元");
		put("USS", "美元");
		put("UYU", "乌拉圭比索");
		put("UZS", "乌兹别克斯坦苏姆");
		put("VEB", "委内瑞拉博利瓦");
		put("VND", "越南盾");
		put("VUV", "瓦努阿图瓦图");
		put("WST", "萨摩亚塔拉");
		put("XAF", "中非法郎");
		put("XAG", "银");
		put("XAU", "金");
		put("XCD", "东加勒比元");
		put("XOF", "西非法郎");
		put("XPF", "太平洋法郎");
		put("YER", "也门里亚尔");
		put("ZAR", "南非兰特");
		put("ZMK", "赞比亚克瓦查");
		put("ZWD", "津巴布韦元");
	}};
	
	public final static List<String> enList = Arrays.asList(
			new String[]{"AED","AFN","ALL","AMD","ANG","AOA","ARS","AUD","AWG","AZM","BAM","BBD","BDT","BGN",
					"BHD","BIF","BMD","BND","BOB","BOV","BRL","BSD","BTN","BWP","BYR","BZD","CAD","CDF","CHF",
					"CLF","CLP","CNY","COP","COU","CRC","CSD","CUP","CVE","CYP","CZK","DJF","DKK","DOP","DZD",
					"ECU","EEK","EGP","ERN","ETB","EUR","FJD","FKP","GBP","GEL","GHC","GIP","GMD","GNF","GTQ",
					"GYD","HKD","HNL","HRK","HTG","HUF","IDR","ILS","INR","IQD","IRR","ISK","JMD","JOD","JPY",
					"KES","KGS","KHR","KMF","KPW","KRW","KWD","KYD","KZT","LAK","LBP","LKR","LRD","LSL","LTL",
					"LVL","LYD","MAD","MDL","MGA","MKD","MMK","MNT","MOP","MRO","MTL","MUR","MVR","MWK","MXN",
					"MXV","MYR","MZM","NAD","NGN","NIO","NOK","NPR","NZD","OMR","PAB","PEN","PGK","PHP","PKR",
					"PLN","PYG","QAR","ROL","RUB","RWF","SAR","SBD","SCR","SDD","SEK","SGD","SHP","SIT","SKK",
					"SLL","SOS","SRD","STD","SVC","SYP","SZL","THB","TJS","TMM","TND","TOP","TRL","TRY","TTD",
					"TWD","TZS","UAH","UGX","USD","USN","USS","UYU","UZS","VEB","VND","VUV","WST","XAF","XAG",
					"XAU","XCD","XOF","XPF","YER","ZAR","ZMK","ZWD"});
	
	public final static List<String> cnList = Arrays.asList(
			new String[]{"阿联酋迪尔汗","阿富汗尼","阿尔巴尼亚列克","亚美尼亚德拉姆","荷属安的列斯盾","安哥拉宽扎","阿根廷比索",
					"澳元","阿鲁巴弗罗林","阿塞拜疆马纳特","波黑马克","巴巴多斯元","孟加拉塔卡","保加利亚列弗","巴林第纳尔",
					"布隆迪法郎","百慕大元","文莱元","玻利维亚诺","玻利维亚","巴西雷亚尔","巴哈马元","不丹努扎姆","博茨瓦纳普拉",
					"白俄罗斯卢布","伯利兹元","加拿大元","刚果法郎","瑞士法郎","智利比索","智利比索","人民币","哥伦比亚比索",
					"哥伦比亚","--","塞尔维亚第纳尔","古巴比索","佛得角埃斯库多","塞浦路斯镑","捷克克朗","吉布提法郎","丹麦克朗",
					"多米尼加比索","阿尔及利亚第纳尔","欧元","爱沙尼亚克朗","埃及镑","厄立特里亚纳克法","埃塞俄比亚比尔","欧元",
					"斐济元","福克兰镑","英镑","格鲁吉亚拉里","加纳塞地","直布罗陀镑","冈比亚达拉西","几内亚法郎","危地马拉格查尔",
					"圭亚那元","港币","洪都拉斯伦皮拉","克罗地亚库纳","海地古德","匈牙利福林","印度尼西亚盾","以色列新谢克尔",
					"印度卢比","伊拉克第纳尔","伊朗里亚尔","冰岛克朗","牙买加元","约旦第纳尔","日元","肯尼亚先令","吉尔吉斯斯坦索姆",
					"柬埔寨瑞尔","科摩罗法郎","北韩元","韩元","科威特第纳尔","开曼元","--","老挝基普","黎巴嫩镑","斯里兰卡卢比",
					"利比里亚元","莱索托洛蒂","立陶宛立特","拉脱维亚拉特","利比亚第纳尔","摩洛哥迪拉姆","摩尔多瓦列伊","马达加斯加阿里亚里",
					"前南马其顿代纳尔","缅甸元","蒙古图格里克","澳门元","毛里塔尼亚乌吉亚","马耳他里拉","毛里求斯卢比","马尔代夫拉菲亚",
					"--","墨西哥比索","墨西哥","马来西亚令吉","莫桑比克梅蒂卡尔","纳米比亚元","尼日利亚奈拉","尼加拉瓜科多巴","挪威克朗",
					"尼泊尔卢比","新西兰元","阿曼里亚尔","巴拿马巴波亚","秘鲁新索尔","巴布亚新几内亚基那","菲律宾比索","巴基斯坦卢比",
					"波兰兹罗提","巴拉圭瓜拉尼","卡塔尔里亚尔","罗马尼亚列伊","俄罗斯卢布","卢旺达法郎","沙特里亚尔","所罗门群岛元",
					"塞舌尔卢比","苏丹第纳尔","瑞典克朗","新加坡元","圣赫勒拿镑","--","斯洛伐克克朗","塞拉利昂利昂","索马里先令",
					"苏里南元","--","萨尔瓦多科朗","叙利亚镑","斯威士兰里兰吉尼","泰铢","塔吉克斯坦索莫尼","土库曼斯坦马纳特",
					"突尼西亚第纳尔","汤加潘加","土耳其里拉","新土耳其里拉","千里达及托巴哥元","新台币","坦桑尼亚先令","乌克兰格里夫尼亚",
					"乌干达先令","美元","美元","美元","乌拉圭比索","乌兹别克斯坦苏姆","委内瑞拉博利瓦","越南盾","瓦努阿图瓦图",
					"萨摩亚塔拉","中非法郎","银","金","东加勒比元","西非法郎","太平洋法郎","也门里亚尔","南非兰特","赞比亚克瓦查",
					"津巴布韦元"});
	
	public static void main(String[] args) {
		for (int i=0;i<enList.size();i++) {
//			System.out.println(enList.get(i));
			System.out.println("put(\""+enList.get(i)+"\", \""+cnList.get(i)+"\");");
		}
	}
}
