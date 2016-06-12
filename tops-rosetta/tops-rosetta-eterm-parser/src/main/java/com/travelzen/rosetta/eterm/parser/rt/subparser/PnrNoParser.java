package com.travelzen.rosetta.eterm.parser.rt.subparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PNR号解析
 * @author yiming.yan
 */
public enum PnrNoParser {

	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PnrNoParser.class);
	
	public static String parse(String text) {
		String bigPnr = null;
		Pattern pattern = Pattern.compile("[ \\n]([0-9A-Z]{6})[ \\n]*(?:$|1\\.)");
		Matcher matcher = pattern.matcher(text);
		if (matcher.find())
			bigPnr = matcher.group(1);
		else 
			LOGGER.error("PNR解析：PNR号解析失败！解析文本：{}", text);
		return bigPnr;
	}
	
	public static void main(String[] args) {
		String text0 = "";
		String text1 = " 1.任晓博 \nHS1PZW  \n";
		String text2 = " 1.高东海 2.彭件林 3.彭耀明 4.彭振瀛 5.杨六妹 6.张金锁   7.张云飚 JF69LN\n";
		String text3 = "1.BIBONIMANA/MARCEL 2.BIZABISHAKA/CLEOPHAS 3.GOUTONDJI/EUGENE GBENOU  \n"
				+ " 4.NIBIZI/INNOCENT 5.NIYONGERE/ERIC MAGLOIRE 6.NKESHIMANA/APOLLINAIRE      \n"
				+ " 7.TCHABAT/MESMER BARBARA 8.YOUSSOUF/FARADJ MABROUK KVZMW9       \n";
		String text4 = " 0.10AAA NM10 KGG6RY\n"
				+ " 1.测验 2.大富翁 3.读我 4.覅改成 5.谷歌 6.晚一点 7.尭吧\n"
				+ " 8.一二 9.医生 10.一样";
		
		System.out.println(parse(text0));
		System.out.println(parse(text1));
		System.out.println(parse(text2));
		System.out.println(parse(text3));
		System.out.println(parse(text4));
	}
	
}
