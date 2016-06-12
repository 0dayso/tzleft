package com.travelzen.farerule.translator;

import com.travelzen.farerule.Stopovers;

public class RuleTranslator8 extends RuleTranslatorBase {

	public static String translate(Stopovers stopovers) {
		StringBuilder sb = new StringBuilder();
		if (!stopovers.isSetPermitStopover())
			return "无限制。";
		if (!stopovers.permitStopover) {
			sb.append("不允许中途停留。\n");
		} else {
			sb.append("允许中途停留。\n");
		}
		if (sb.toString().endsWith("\n"))
			sb.replace(sb.length()-1, sb.length(), "");
		return sb.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(RuleTranslator8.translate(new Stopovers().setPermitStopover(false)));
	}
	
}
