/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */
 
package com.travelzen.farerule.jpecker.consts;

public class ConditionConst {

	public final static String origin = "\n *(?:AND[ -]*)?(ORIGINATING +[A-Z0-9,\\(\\) ]*?|OUTBOUND|INBOUND) *-";
	public final static String ORIGINSHARE = "^([\\w\\W]*?)" + origin;
	public final static String ORIGINS = origin + "([\\w\\W]+?)" + "(?=" + origin + "|$)";
	
	public final static String date = "\\d{2}\\s*" + DateConst.month + "(?:\\s*\\d{2,4})?";
	public final static String DATESHARE = "^([\\w\\W]*?)FOR\\s(?:TICKETING|TRAVEL)\\s(ON/(?:BEFORE|AFTER)\\s" + date + ")";
	
	public final static String salesTag = 
			"(?:^|\n)\\s*FOR\\sTICKETING\\s(ON/(?:BEFORE|AFTER)\\s" + date
			+ "(?:\\sAND\\sON/BEFORE\\s" + date + ")?)"
			+ "(?:/FOR\\sTRAVEL\\s(ON/(?:BEFORE|AFTER)\\s" + date 
			+ "(?:\\sAND\\sON/BEFORE\\s" + date + ")?))?";
	public final static String SALESRES = salesTag + "([\\w\\W]+?)" + "(?=" + salesTag + "|$)";
	
	public final static String travelTag = 
			"(?:^|\n)\\s*FOR\\sTRAVEL\\s(ON/(?:BEFORE|AFTER)\\s" + date 
			+ "(?:\\sAND\\sON/BEFORE\\s" + date + ")?)";
	public final static String TRAVELRES = travelTag + "([\\w\\W]+?)" + "(?=" + travelTag + "|$)";
}
