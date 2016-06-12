package com.travelzen.etermface.service.nlp;

import com.travelzen.etermface.service.util.PnrDateUtil;

public enum TimeLimitTokenType {
	
//	NUMBER("-?[0-9]+"), BINARYOP("[*|/|+|-]"), WHITESPACE("[ \t\f\r\n]+");
	
	
	
//	static Pattern mon3codePstr_pattern = Pattern.compile(PnrDateUtil.MON_3CODE_PSTR, Pattern.LITERAL);
//	static Pattern time_pattern = Pattern.compile("\\d{2}:\\d{2}");
//	static Pattern d4_pattern = Pattern.compile("\\d{4}" );
//	static Pattern d2_pattern = Pattern.compile("\\d{2}" );
//	static Pattern space_pattern = Pattern.compile("\\s+");
	
	MON(PnrDateUtil.MON_3CODE_PSTR_WITHPAIR),
	TIME5("[0-2][0-9]:[0-6][0-9]"),
	TIME4("[0-2][0-9][0-6][0-9]"),
	D2("\\d{2}"),
	WHITESPACE("\\s+")
	
//	DAY,
//	YEAR,
//	TIME,
//	WHITESPACE ;
	;
	public final String pattern;

    private TimeLimitTokenType(String pattern) {
        this.pattern = pattern;
    }
}
