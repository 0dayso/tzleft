package com.travelzen.rosetta.eterm.parser.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regex　工具类
 * <p>
 * @author yiming.yan
 * @Date Nov 19, 2015
 */
public enum RegexUtil {

	;

	public static boolean startsWith(String str, String regex) {
		Matcher matcher = Pattern.compile("^" + regex).matcher(str);
		if (matcher.find())
			return true;
		return false;
	}
	
	public static boolean endsWith(String str, String regex) {
		Matcher matcher = Pattern.compile(regex + "$").matcher(str);
		if (matcher.find())
			return true;
		return false;
	}
	
	private static final Pattern PATTERN_TRIM_SUFFIX = Pattern.compile("(.+?) *$");
	
	public static String trimSuffix(String str) {
		Matcher matcher = PATTERN_TRIM_SUFFIX.matcher(str);
		if (matcher.find())
			return matcher.group(1);
		return str;
	}

}
