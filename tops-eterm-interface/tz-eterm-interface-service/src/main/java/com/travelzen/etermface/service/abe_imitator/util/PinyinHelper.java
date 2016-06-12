package com.travelzen.etermface.service.abe_imitator.util;

import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinHelper {

    public static String toHanyuPinyinString(char cha) {
	HanyuPinyinOutputFormat pyFormat = new HanyuPinyinOutputFormat();
	pyFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE); // 设置样式
	pyFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
	pyFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
	try {
	    String[] strs = net.sourceforge.pinyin4j.PinyinHelper.toHanyuPinyinStringArray(cha, pyFormat);
	    if (strs.length > 0) {
		return strs[0];
	    }
	} catch (BadHanyuPinyinOutputFormatCombination e) {
	    // log
	    e.printStackTrace();
	}
	return "unKnown";
    }

    public static String toHanyuPinyinString(String str) {

	HanyuPinyinOutputFormat pyFormat = new HanyuPinyinOutputFormat();
	// 设置样式
	pyFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
	pyFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
	pyFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
	StringBuffer sb = new StringBuffer();

	for (int i = 0; i < str.length(); i++) {
	    char cha = str.charAt(i);
	    try {
		if (Character.isDigit(cha) || Character.isLowerCase(cha) || Character.isUpperCase(cha)) {
		    sb.append(cha);
		} else {
		    String[] strs = net.sourceforge.pinyin4j.PinyinHelper.toHanyuPinyinStringArray(cha, pyFormat);
		    if (strs != null && strs.length > 0) {
			sb.append(" ");
			sb.append(strs[0]);
		    } else {
			sb.append(" ");
			sb.append("unKnown");
		    }
		}
	    } catch (BadHanyuPinyinOutputFormatCombination e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	String rs = sb.toString().trim();
	if (rs.contains(" ")) {
	    rs = rs.replaceFirst(" ", "/");
	    rs = rs.replaceAll(" ", "");
	}
	return rs;
    }

    public static void main(String[] args) {
	System.out.println(PinyinHelper.toHanyuPinyinString("薛然"));
    }
}
