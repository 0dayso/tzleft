/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */
 
package com.travelzen.farerule.jpecker.consts;

public class PenaltyConst {

	public final static String currency = "(AED|AFN|ALL|AMD|ANG|AOA|ARS|AUD|AWG|AZM|BAM|BBD|BDT|BGN|BHD|BIF|BMD|"
			+ "BND|BOB|BOV|BRL|BSD|BTN|BWP|BYR|BZD|CAD|CDF|CHF|CLF|CLP|CNY|COP|COU|CRC|CSD|CUP|CVE|CYP|CZK|DJF|DKK|"
			+ "DOP|DZD|ECU|EEK|EGP|ERN|ETB|EUR|FJD|FKP|GBP|GEL|GHC|GIP|GMD|GNF|GTQ|GYD|HKD|HNL|HRK|HTG|HUF|IDR|ILS|"
			+ "INR|IQD|IRR|ISK|JMD|JOD|JPY|KES|KGS|KHR|KMF|KPW|KRW|KWD|KYD|KZT|LAK|LBP|LKR|LRD|LSL|LTL|LVL|LYD|MAD|"
			+ "MDL|MGA|MKD|MMK|MNT|MOP|MRO|MTL|MUR|MVR|MWK|MXN|MXV|MYR|MZM|NAD|NGN|NIO|NOK|NPR|NZD|OMR|PAB|PEN|PGK|"
			+ "PHP|PKR|PLN|PYG|QAR|ROL|RUB|RWF|SAR|SBD|SCR|SDD|SEK|SGD|SHP|SIT|SKK|SLL|SOS|SRD|STD|SVC|SYP|SZL|THB|"
			+ "TJS|TMM|TND|TOP|TRL|TRY|TTD|TWD|TZS|UAH|UGX|USD|USN|USS|UYU|UZS|VEB|VND|VUV|WST|XAF|XAG|XAU|XCD|XOF|"
			+ "XPF|YER|ZAR|ZMK|ZWD)";
	
	public final static String CONTENT = "(?<content>[\\w\\W]+?)";
	public final static String CONTENT1 = "(?<content1>[\\w\\W]+?)";
	public final static String CONTENT2 = "(?<content2>[\\w\\W]+?)";
	
	public final static String cancel = "(?:CANCEL(?:LATION)?S?|REFUNDS?)";
	public final static String change = "(?:CHANGES?|REBOOKINGS?|REVALIDATIONS?)";
	public final static String noshow = "(?:NO[ -]?SHOW)";
	public final static String CANCEL = "([0-9A-Z]|AND)?[ -\\.]*(([A-Z]+ */ *)*(CANCEL|CANCELLATION(S)?|REFUND(S)?)( */ *[A-Z]+)*|VOLUNTARY *REFUND)";
	public final static String CHANGE = "([0-9A-Z]|AND)?[ -\\.]*(([A-Z]+ */ *)*(CHANGE(S)?|REBOOKING(S)?|REVALIDATION(S)?)( */ *[A-Z]+)*|VOLUNTARY *REBOOK(?:ING)?)";
	public final static String NOSHOW = "([0-9A-Z]|AND)?[ -\\.]*(([A-Z]+ */ *)*(NO[ -]?SHOW)( */ *[A-Z]+)*)";
	public final static String BOTH = "(" + CANCEL + " *AND *" + CHANGE + "|" + CHANGE + " *AND *" + CANCEL + ")";
	public final static String ENDTAG = "(?=(?<!/|FOR)\n[ -]*([0-9A-Z]|AND)?[ -\\.]*(([A-Z]+ *(AND|/) *)*"
			+ "(CANCELLATION(S)?|REFUND(S)?|CHANGE(S)?|REBOOKING(S)?)( *(AND|/) *[A-Z]+)*|"
			+ "VOLUNTARY *REFUND|VOLUNTARY *REBOOK(?:ING)?)[ -]*(?:ANY TIME)?\n|\n-+\n|$)";
	public final static String ENDTAG_BIG = "(?=(?<!/|FOR)\n[ -]*([0-9A-Z]|AND)?[ -\\.]*(([A-Z]+ *(AND|/) *)*"
			+ "(CANCELLATION(S)?|REFUND(S)?|CHANGE(S)?|REBOOKING(S)?)( *(AND|/) *[A-Z]+)*|"
			+ "VOLUNTARY *REFUND|VOLUNTARY *REBOOK(?:ING)?)[ -]*(?:ANY TIME)?\n|$)";
	
	public final static String UNUSED = "(?:TOTAL(?:LY)?|WHOL(?:E|LY)|FULLY?)\\s*UN(?:USE|UTILISE)D(?:\\s*TICKETS?)?";
	public final static String USED = "PARTIAL(?:LY)?\\s*(?:UN)?(?:USE|UTILISE)D(?:\\s*TICKETS?)?";
	public final static String ORUSED = "(?:TOTAL(?:LY)?|WHOL(?:E|LY)|FULLY?)\\s*UN(?:USE|UTILISE)D\\s*/\\s*PARTIAL(?:LY)?\\s*(?:USE|UTILISE)D(?:\\s*TICKETS?)?";
	
	public final static String BD = "(\\d|[A-Z])? *BEFORE *(DEPA(?:R)?TURE( *OF[A-Z ]*JOURNEY)?|DEPT(\\.)?|USE)(?!\\.)";
	public final static String AD = "(\\d|[A-Z])? *AFTER *(DEPA(?:R)?TURE( *OF[A-Z ]*JOURNEY)?|DEPT(\\.)?|USE)(?!\\.)";
	public final static String BORA = "(?:\\s*BEFORE */ *AFTER *(?:DEPA(?:R)?TURE( *OF[A-Z ]*JOURNEY)?|DEPT|USE))?";
	public final static String BDAD = "(\\d|[A-Z])? *(BEFORE|AFTER) *(DEPA(?:R)?TURE( *OF[A-Z ]*JOURNEY)?|DEPT(\\.)?|USE)(?!\\.)";
	
	public final static String PENALTY0 = "\\b(\\d{1,2}) *(?:PERCENT|PCT)";
//	public final static String PENALTY1 = "\\b" + currency + " *(\\d+)(?:/" + currency + " *(\\d+))?\\b";
//	public final static String PENALTY2 = "\\b(\\d+) *" + currency + "(?:/(\\d+) *" + currency + ")?\\b";
	public final static String PENALTY1 = "\\b(" + currency + " ?\\d+(?:/" + currency + " ?\\d+)*)\\b";
	public final static String PENALTY2 = "\\b(\\d+ ?" + currency + "(?:/\\d+ *" + currency + ")*)\\b";
	public final static String PENALTY3 = "(?:NOT\\s(?:PE(?:R)?MITTED|PMTD|ALLOWED|APPLICABLE)|NON[\\s-]*(?:REFUND(?:ABLE|ALBE)?|RFD)|NO(?:\\sPARTIAL)?\\sREFUND|NO\\s(?:REFUND|CHANGE)S?\\sPERMITTED)";
	public final static String PENALTY4 = "(?:PE(?:R)?MITTED|PMTD|FREE|FOC|FULL REFUND|TICKET IS REFUNDABLE|NO (?:PENALTY|CHARGE)|NO CHANGE FEE APPL(?:IES|Y))";
	
	public final static String PENALTY5 = "(?:DIFFERENCE\\sBETWEEN(?:\\sTHE)?\\sFARE\\sPAID\\sAND)";
	public final static String PENALTY6 = "PERMITTED\\s(?:(?:WITHIN\\sVALIDITY\\sTICKET\\s)?WITH\\s(?:A\\s)?FEE(?:\\sOF)?|AT)\\s" + currency + "\\s?(\\d+)\\b";
	public final static String PENALTY7 = "PERMITTED\\.?\\sSUBJECT\\sTO\\s(?:REFUND|REBOOKING)\\sFEE\\s" + currency + "\\s(\\d+)\\b";
	
	public final static String PENALTIES = "(?<penalty>"+PENALTY7+"|"+PENALTY6+"|"+PENALTY5+"|"+PENALTY0+"|"+PENALTY1+"|"+PENALTY2+"|"+PENALTY3+"|"+PENALTY4+")";
	public final static String PENALTIES_SIM = "(?<penalty>"+PENALTY0+"|"+PENALTY1+"|"+PENALTY2+"|"+PENALTY3+"|"+PENALTY4+")";
	public final static String PENALTIES_SUM = "(?<penalty>"+PENALTY1+"|"+PENALTY2+")";
	public final static String PENALTIES_N = "(?:"+PENALTY0+"|"+PENALTY1+"|"+PENALTY2+"|"+PENALTY3+"|"+PENALTY4+")";
}
