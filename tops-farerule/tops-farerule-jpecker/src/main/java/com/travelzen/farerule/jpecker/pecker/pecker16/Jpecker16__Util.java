package com.travelzen.farerule.jpecker.pecker.pecker16;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.travelzen.farerule.jpecker.consts.PenaltyConst;

public class Jpecker16__Util {

	public static String rmNoise(String ruleText) {
		// General Information
		Matcher matcher_geninfo = Pattern.compile("^([\\w\\W]+)"
				+ "\n *GENERAL INFORMATION *\n").matcher(ruleText);
		if (matcher_geninfo.find()) {
			ruleText = matcher_geninfo.group(1);
		}
		// Child/Infant
		Matcher matcher_chd = Pattern.compile("^([\\w\\W]+)"
				+ "\n *NOTE[ -]*"
				+ "\n *(?:(?:UN)?ACCOMPANIED )?(?:CHILD|INFANT) (?:UNDER \\d|\\d-\\d)").matcher(ruleText);
		if (matcher_chd.find()) {
			ruleText = matcher_chd.group(1);
		}
		return ruleText;
	}
	
	public static String rmNote(String text) {
		Matcher matcher = Pattern.compile("^([\\w\\W]+?)\n *NOTE[ -]*\n").matcher(text);
		if(matcher.find()) {
			return matcher.group(1);
		}
		return text;
	}
	
	public static String parseFirst(String text) {
		String penalty = "";
		Matcher matcher = Pattern.compile(PenaltyConst.PENALTIES).matcher(text);
		if(matcher.find())
			penalty = parsePenalties(matcher.group("penalty"));
		return penalty;
	}
	
	public static String parseInside(String text) {
		List<String> penaltyList = new ArrayList<String>();
		Matcher matcher = Pattern.compile(PenaltyConst.PENALTIES).matcher(text);
		while(matcher.find())
			penaltyList.add(parsePenalties(matcher.group("penalty")));
		return sortPenalties(penaltyList);
	}
	
	public static String parsePenalties(String penalties) {
		String result = "";
			
		Matcher matcher0 = Pattern.compile(PenaltyConst.PENALTY0).matcher(penalties);
		Matcher matcher1 = Pattern.compile(PenaltyConst.PENALTY1).matcher(penalties);
		Matcher matcher2 = Pattern.compile(PenaltyConst.PENALTY2).matcher(penalties);
		Matcher matcher3 = Pattern.compile(PenaltyConst.PENALTY3).matcher(penalties);
		Matcher matcher4 = Pattern.compile(PenaltyConst.PENALTY4).matcher(penalties);
//		Matcher matcher5 = Pattern.compile(PenaltyConst.PENALTY5).matcher(penalties);
		
		if (matcher0.find()) {
			result = matcher0.group(1) + "%";
		} else if (matcher1.find()) {
			result = matcher1.group(1);
		} else if (matcher2.find()) {
			result = matcher2.group(1);
		} else if (matcher3.find()) {
			result = "-1";
		} else if (matcher4.find()) {
			result = "0";
		} 
//		else if (matcher5.find()) {
//			result = "-9";
//		}
		return result;
	}
	
	public static String sortPenalties(List<String> penaltyList) {
		String finalPenalty = "";
		int penaltyTag = Integer.MIN_VALUE;
		Pattern pattern = Pattern.compile("-?\\d+");		
		for (String penalty:penaltyList) {
			Matcher matcher = pattern.matcher(penalty);
			if (matcher.find()) {
				int tag = Integer.parseInt(matcher.group(0));
				if (tag == -1)
					tag = Integer.MAX_VALUE;
				if (tag > penaltyTag) {
					finalPenalty = penalty;
					penaltyTag = tag;
				}
			}
		}
		return finalPenalty;
	}
	
	public static String sortTwoPenalties(String penalties1, String penalties2) {
		List<String> penaltyList = new ArrayList<String>();
		penaltyList.add(penalties1);
		penaltyList.add(penalties2);
		return sortPenalties(penaltyList);
	}
	
	public static String parseAnd(String text) {
		String result = "";
		Matcher matcher = Pattern.compile(PenaltyConst.BOTH + "[-\\s]*" + PenaltyConst.PENALTIES).matcher(text);
		while (matcher.find()) {
			result = Jpecker16__Util.parsePenalties(matcher.group("penalty"));
		}
		return result;
	}
	
	public static String parseRefund(String airCompany, String text) {
		String refund = "";
		
		Matcher matcher1 = Pattern.compile(PenaltyConst.PENALTIES 
			+ "\\s*(?:FOR|IN\\s*CASE\\s*OF)\\s*([\\w\\W]{1,15}/)*\\s*" + PenaltyConst.cancel).matcher(text);
		Matcher matcher2 = Pattern.compile(PenaltyConst.cancel 
			+ "\\s*FEE[\\s-]*(?:CHARGE|FOR\\sPER\\sTICKET)?\\s*" + PenaltyConst.PENALTIES).matcher(text);
		Matcher matcher3 = Pattern.compile(PenaltyConst.cancel 
			+ "\\s*SERVICE\\s*CHARGE\\s*" + PenaltyConst.PENALTIES_SUM).matcher(text);
		Matcher matcher4 = Pattern.compile(PenaltyConst.PENALTIES_SUM 
			+ "\\s*REFUND\\s*SERVICE\\s*FEE").matcher(text);
		Matcher matcher5 = Pattern.compile(PenaltyConst.cancel 
			+ "[\\s-]*CHARGE\\s*SERVICE\\s*FEE(?:\\s*OF)?\\s*" + PenaltyConst.PENALTIES).matcher(text);
		Matcher matcher6 = Pattern.compile("\n *" + PenaltyConst.cancel 
			+ " *PERMITTED(?: *FOR *(?:CANCEL/)?REFUND)? *(?:.|\n|WITHIN VALIDITY FOR FREE)").matcher(text);
		
		List<String> refundList = new ArrayList<String>();
		while (matcher1.find()) {
			refundList.add(parsePenalties(matcher1.group("penalty")));
		}
		while (matcher2.find()) {
			refundList.add(parsePenalties(matcher2.group("penalty")));
		}
		while (matcher3.find()) {
			refundList.add(parsePenalties(matcher3.group("penalty")));
		}
		while (matcher4.find()) {
			refundList.add(parsePenalties(matcher4.group("penalty")));
		}
		while (matcher5.find()) {
			refundList.add(parsePenalties(matcher5.group("penalty")));
		}
		if (matcher6.find()) {
			refundList.add("0");
		}
		
		if (refundList.size() != 0)
			refund = sortPenalties(refundList);
		return refund;
	}
	
	public static String parseRevalidation(String airCompany, String text) {
		String revalidation = "";
		
		Matcher matcher1 = Pattern.compile(PenaltyConst.PENALTIES 
			+ "\\s*(FOR|IN\\s*CASE\\s*OF)\\s*([\\w\\W]{1,15}/)*\\s*" + PenaltyConst.change).matcher(text);
		Matcher matcher2 = Pattern.compile(PenaltyConst.change 
			+ "\\s*FEE[\\s-]*(\\s*CHARGE)?\\s*" + PenaltyConst.PENALTIES).matcher(text);		
		Matcher matcher3 = Pattern.compile(PenaltyConst.change 
			+ "[\\s-]*CHARGE\\s*SERVICE\\s*FEE(\\s*OF)?\\s*" + PenaltyConst.PENALTIES).matcher(text);
		Matcher matcher4 = Pattern.compile("\n *" + PenaltyConst.change 
			+ " *PERMITTED( *FOR *(REISSUE/)?REVALIDATION)? *(.|\n|WITHIN VALIDITY FOR FREE)").matcher(text);
		
		List<String> revalidationList = new ArrayList<String>();
		while (matcher1.find()) {
			revalidationList.add(parsePenalties(matcher1.group("penalty")));
		}
		while (matcher2.find()) {
			revalidationList.add(parsePenalties(matcher2.group("penalty")));
		}
		while (matcher3.find()) {
			revalidationList.add(parsePenalties(matcher3.group("penalty")));
		}
		if (matcher4.find()) {
			revalidationList.add("0");
		}
		if (revalidationList.size() != 0)
			revalidation = sortPenalties(revalidationList);
		return revalidation;
	}
	
	public static String parseReissue(String airCompany, String text) {
		String reissue = "";
		
		Matcher matcher1 = Pattern.compile(
			"REISSUE(?:\\s*FEE)?[\\s-]*(?:\\s*CHARGE)?(?:\\s*SERVICE)?(?:\\s*FEE)?(?:\\s*OF)?\\s*" 
			+ PenaltyConst.PENALTIES).matcher(text);
		Matcher matcher2 = Pattern.compile(PenaltyConst.PENALTIES 
			+ " *(?:FOR|IN\\s*CASE\\s*OF)\\s*(?:[\\w\\W]{1,20}/)?REISSUE").matcher(text);
		Matcher matcher3 = Pattern.compile(
			"\n *(?:[A-Z]+ *(?:AND|/) *)*REISSUE(?: *(?:AND|/) *[A-Z]+)*[- ]*\n"
			+ "(?: *ANY ?TIME *\n)?"
			+ "(?: *CHARGE)? *" 
			+ PenaltyConst.PENALTIES).matcher(text);
		
		List<String> reissueList = new ArrayList<String>();
		while (matcher1.find()) {
			reissueList.add(parsePenalties(matcher1.group("penalty")));
		}
		while (matcher2.find()) {
			reissueList.add(parsePenalties(matcher2.group("penalty")));
		}
		while (matcher3.find()) {
			reissueList.add(parsePenalties(matcher3.group("penalty")));
		}
		
		// For BR/B7 airline
		if (airCompany.equals("BR") || airCompany.equals("B7")) {
			Matcher matcher_br = Pattern.compile(
					"REISSUANCE\\sFEE\\s" + PenaltyConst.PENALTY1).matcher(text);
			if (matcher_br.find()) {
				reissueList.add(matcher_br.group(1));
			}
		}
		// For GA airline
		else if (airCompany.equals("GA")) {
			Matcher matcher_ga = Pattern.compile(
					"REISSUED\\sPERMITTED\\sWITH\\sA\\sCHARGE\\s" + PenaltyConst.PENALTY1).matcher(text);
			if (matcher_ga.find()) {
				reissueList.add(matcher_ga.group(1));
			}
		}		
		// For CI airline
		else if (airCompany.equals("CI")) {
			Matcher matcher_ci = Pattern.compile(
					"\nREISSUE--\n[A-Z ]+" + PenaltyConst.PENALTIES).matcher(text);
			if (matcher_ci.find())
				reissueList.add(parsePenalties(matcher_ci.group("penalty")));
		}
		// For OZ airline
		else if (airCompany.equals("OZ")) {
			Matcher matcher_oz = Pattern.compile(
					"NOTE -\n1. REISSUE CASE\n[\\w\\W]{0,20}APPLY").matcher(text);
			if (matcher_oz.find())
				reissueList.add("0");
		}
		
		if (reissueList.size() != 0)
			reissue = sortPenalties(reissueList);
		return reissue;
	}
	
	public static String parseReroute(String airCompany, String text) {
		String reroute = "";
		
		Matcher matcher1 = Pattern.compile(
			"REROUT(?:E|ING)(?:\\s*FEE)?[\\s-]*(?:\\s*CHARGE)?(?:\\s*SERVICE)?(?:\\s*FEE)?\\s*" 
			+ PenaltyConst.PENALTIES).matcher(text);
		Matcher matcher2 = Pattern.compile(PenaltyConst.PENALTIES 
			+ "\\s*(?:FOR|IN\\s*CASE\\s*OF)\\s*(?:[\\w\\W]{1,20}/)?REROUT(?:E|ING)").matcher(text);
		Matcher matcher3 = Pattern.compile(
			"\n *(?:[A-Z]+ *(?:AND|/) *)*RE-?ROUN?T(?:E|ING)(?: *(?:AND|/) *[A-Z]+)*[- ]*\n"
			+ "(?: *ANY ?TIME *\n)?"
			+ "(?: *CHANGES)?(?: *CHARGE)? *" 
			+ PenaltyConst.PENALTIES).matcher(text);
		Matcher matcher4 = Pattern.compile(
			"RE-?ROUT(?:E|ING)[ -]*(?:IS )?NOT PERMITTED(?:\\.|\n)").matcher(text);
		
		List<String> rerouteList = new ArrayList<String>();
		while (matcher1.find()) {
			rerouteList.add(parsePenalties(matcher1.group("penalty")));
		}
		while (matcher2.find()) {
			rerouteList.add(parsePenalties(matcher2.group("penalty")));
		}
		while (matcher3.find()) {
			rerouteList.add(parsePenalties(matcher3.group("penalty")));
		}
		if (matcher4.find()) {
			rerouteList.add("-1");
		}
		
		if (rerouteList.size() != 0)
			reroute = sortPenalties(rerouteList);
		return reroute;
	}
	
	public static String parseEndorse(String airCompany, String text) {
		String endorse = "";
		
		Matcher matcher1 = Pattern.compile(
			"\n *(?:[A-Z]+ *(AND|/) *)*ENDORSE?(?:MENT)?(?: *(?:AND|/) *[A-Z]+)*[- ]*\n"
			+ "(?: *ANY ?TIME *\n)?"
			+ "(?: *CHARGE)? *" 
			+ PenaltyConst.PENALTIES).matcher(text);
		Matcher matcher2 = Pattern.compile(
			"ENDORSE(?:MENT)?[ -]*NOT PERMITTED\\.").matcher(text);
		
		List<String> endorseList = new ArrayList<String>();
		while (matcher1.find()) {
			endorseList.add(parsePenalties(matcher1.group("penalty")));
		}
		if (matcher2.find()) {
			endorseList.add("-1");
		}
		if (endorseList.size() != 0)
			endorse = sortPenalties(endorseList);
		return endorse;
	}
	
	public static String parseNoshow(String airCompany, String text) {
		String noshow = "";
		
		Matcher matcher1 = Pattern.compile(
				"NO[ -]?SHOW(S)?(\\s*FEE)?[\\s-]*(\\s*CHARGE)?(\\s*SERVICE)?(\\s*FEE)?(\\s*(OF|AT))?\\s*" 
				+ PenaltyConst.PENALTIES_SIM).matcher(text);
		Matcher matcher2 = Pattern.compile(
				PenaltyConst.PENALTIES_SIM 
				+ "\\s*(FOR|IN\\s*CASE\\s*OF)\\s*([\\w\\W]{1,20}/)?NO[-\\s]*SHOW").matcher(text);
		Matcher matcher3 = Pattern.compile(
				"IN CASE OF NO[ -]?SHOW ([\\w\\W]{1,40}?)\\.").matcher(text);
		Matcher matcher4 = Pattern.compile(
				"\n\\s*([A-Z]+ *(AND|/) *)*NO[ -]*SHOW( *(AND|/) *[A-Z]+)*\\s*\n(\\s*ANY *TIME\\s*\n)?(\\s*CHARGE)?\\s*" 
				+ PenaltyConst.PENALTIES_SIM).matcher(text);
		
		List<String> noshowList = new ArrayList<String>();
		while (matcher1.find()) {
			noshowList.add(parsePenalties(matcher1.group("penalty")));
		}
		while (matcher2.find()) {
			noshowList.add(parsePenalties(matcher2.group("penalty")));
		}
		while (matcher3.find()) {
			noshowList.add(parsePenalties(matcher3.group(1)));
		}
		while (matcher4.find()) {
			noshowList.add(parsePenalties(matcher4.group("penalty")));
		}
		
		// EK airline
		if (airCompany.equals("EK")) {
			Matcher matcher_ek1 = Pattern.compile(
					"FAILURE\\sTO\\sOCCUPY\\sA\\sRESERVED\\sSEAT\\sON\\sANY\\sSEGMENT\\s"
					+ "OF\\sTHE\\sITINERARY\\sWILL\\sRESULT\\sIN\\sALL\\sSUBSEQUENT\\s"
					+ "SEGMENTS\\sOF\\sTHE\\sITINERARY\\sBEING\\sCANCELLED\\.\\s?"
					+ "IN\\sSUCH\\sCASES([\\w\\W]+?)\\.").matcher(text);
			Matcher matcher_ek2 = Pattern.compile(
					"IN\\sCASE\\sOF\\sNO-SHOW\\s[A-Z/\\s]{5,30}\\sIS\\s" + PenaltyConst.PENALTY1).matcher(text);
			while (matcher_ek1.find()) {
				Matcher matcher_no = Pattern.compile(
					"NOT\\sBE\\sPERMITTED").matcher(matcher_ek1.group(1));
				if(matcher_no.find())
					noshowList.add("-1");
				else
					noshowList.add(parsePenalties(matcher_ek1.group(1)));
			}
			if (matcher_ek2.find()) {
				noshowList.add(matcher_ek2.group(1));
			}
		}
		// AY airline
		else if (airCompany.equals("AY")) {
			Matcher matcher_ay = Pattern.compile(
					"IF\\sPASSENGER\\sIS\\sNO\\sSHOW\\s-\\sFINNAIR\\sHAS\\sA\\sRIGHT\\s"
					+ "TO\\sCANCEL\\sONWARD\\sOR\\sRETURN\\sRESERVATION").matcher(text);
			if (matcher_ay.find()) {
				noshowList.add("0");
			}
		}
		// BA airline
		else if (airCompany.equals("BA")) {
			Matcher matcher_ba1 = Pattern.compile(
					"NO-SHOW FOR A FLIGHT\n"
					+ " *TICKET WILL HAVE NO VALUE FOR REFUND").matcher(text);
			Matcher matcher_ba2 = Pattern.compile(
					"NO-SHOW(?:S)? FOR A FLIGHT (?:WILL BE|ARE)\\s*CONSIDERED(?: AS)? A\\s*"
					+ "CANCELLATION AFTER DEPARTURE\\s*AND").matcher(text);
			Matcher matcher_ba3 = Pattern.compile(
					"NO-SHOWS TICKET WILL HAVE NO VALUE").matcher(text);
			if (matcher_ba1.find() || matcher_ba2.find() || matcher_ba3.find()) {
				noshowList.add("-1");
			}
		}
		// JL airline
		else if (airCompany.equals("JL")) {
			Matcher matcher_jl = Pattern.compile(
					"IN CASE OF NO-SHOW TICKET HAS NO VALUE").matcher(text);
			if (matcher_jl.find()) {
				noshowList.add("-1");
			}
		}
		// EY airline
		else if (airCompany.equals("EY")) {
			Matcher matcher_ey = Pattern.compile(
					"NO-SHOW IS NON-REFUNDABLE").matcher(text);
			if (matcher_ey.find()) {
				noshowList.add("-1");
			}
		}
		// NX airline
		else if (airCompany.equals("NX")) {
			Matcher matcher_nx = Pattern.compile(
					"NO SHOW FEE ARE NOT REFUNDABLE").matcher(text);
			if (matcher_nx.find()) {
				noshowList.add("-1");
			}
		}
				
		if (noshowList.size() != 0)
			noshow = sortPenalties(noshowList);
		return noshow;
	}

}
