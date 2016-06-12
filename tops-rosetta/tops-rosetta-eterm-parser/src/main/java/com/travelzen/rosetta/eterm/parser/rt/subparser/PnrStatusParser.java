package com.travelzen.rosetta.eterm.parser.rt.subparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.travelzen.rosetta.eterm.common.pojo.rt.PnrStatus;

/**
 * PNR status解析
 * @author yiming.yan
 */
public enum PnrStatusParser {

	;
	
	public static PnrStatus parse(String text) {
		PnrStatus status = null;
		Matcher matcher_n = Pattern.compile("^\\s*NO PNR").matcher(text);
		Matcher matcher_u = Pattern.compile("^\\s*需要授权").matcher(text);
		Matcher matcher_e = Pattern.compile("\\*ELECTRONIC TICKET").matcher(text);
		Matcher matcher_c = Pattern.compile("ENTIRELY CANCELLED\\*").matcher(text);
		if (matcher_n.find())
			status = PnrStatus.NO_PNR;
		else if (matcher_u.find())
			status = PnrStatus.UNAUTHORIZED;
		else if (matcher_e.find())
			status = PnrStatus.ELECTRONIC_TICKET;
		else if (matcher_c.find())
			status = PnrStatus.CANCELLED;
		else 
			status = PnrStatus.RAW_TICKET;
		return status;
	}
	
	public static void main(String[] args) {
		String text0 = "";
		String text1 = "    **ELECTRONIC TICKET PNR** \n";
		String text2 = "     *THIS PNR WAS ENTIRELY CANCELLED*\n";
		String text3 = "  需要授权^M";
		String text4 = "\n需要授权^M";
		String text5 = "  NO PNR^M";
		String text6 = "\nNO PNR^M";
		
		System.out.println(parse(text0));
		System.out.println(parse(text1));
		System.out.println(parse(text2));
		System.out.println(parse(text3));
		System.out.println(parse(text4));
		System.out.println(parse(text5));
		System.out.println(parse(text6));
	}
	
}
