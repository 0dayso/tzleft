package com.travelzen.etermface.service.nlp;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.travelzen.etermface.service.util.PnrDateUtil;
import com.travelzen.framework.core.exception.SException;

public class TimeLimitLexer {

    private static Logger logger = LoggerFactory.getLogger(TimeLimitLexer.class);

    static String mon3codePstr = PnrDateUtil.MON_3CODE_PSTR;
    // 1555
    static String time = "\\d{2}:\\d{2}";
    static String d4 = "\\d{4}";
    static String d2 = "\\d{2}";

    // static Pattern mon3codePstr_pattern =
    // Pattern.compile(PnrDateUtil.MON_3CODE_PSTR, Pattern.LITERAL);
    // static Pattern time_pattern = Pattern.compile("\\d{2}:\\d{2}");
    // static Pattern d4_pattern = Pattern.compile("\\d{4}" );
    // static Pattern d2_pattern = Pattern.compile("\\d{2}" );
    // static Pattern space_pattern = Pattern.compile("\\s+");

    static Map<TimeLimitTokenType, Pattern> definitePatternMap = Maps.newEnumMap(TimeLimitTokenType.class);

    static Pattern TokenExtractorPattern;

    static String patternFormat = "(%s)|(%s)|(%s)|(%s)|(\\\\s)";

    static {
	String patternStr = String.format(patternFormat, mon3codePstr, time, d4, d2);

	StringBuffer tokenPatternsBuffer = new StringBuffer();
	for (TimeLimitTokenType tokenType : TimeLimitTokenType.values())
	    tokenPatternsBuffer.append(String.format("|(?<%s>%s)", tokenType.name(), tokenType.pattern));

	TokenExtractorPattern = Pattern.compile(new String(tokenPatternsBuffer.substring(1)));

	// TokenExtractorPattern = Pattern.compile(patternStr);

	// patternList.addAll(Arrays.asList(mon3codePstr_pattern, time_pattern,
	// d4_pattern, d2_pattern));

	// definitePatternMap.put(TokenType.MON, mon3codePstr_pattern);
	// definitePatternMap.put(TokenType.TIME, time_pattern);

    }

    private void init() {

    }

    // TimeLimitTokenRepo repo ;

    public TimeLimitTokenRepo lex(String str) {

	//		logger.info(str);

	TimeLimitTokenRepo tkRepo = new TimeLimitTokenRepo();
	List<TimeLimitToken> tokens = Lists.newArrayList();

	tkRepo.lsTimeLimitToken = tokens;

	Matcher matcher = TokenExtractorPattern.matcher(str);

	int tkIdx = 0;
	while (matcher.find()) {

	    for (TimeLimitTokenType tkType : TimeLimitTokenType.values())

		if (matcher.group(tkType.name()) != null) {

		    TimeLimitToken tk = new TimeLimitToken(tkType, matcher.group());
		    tk.tkIdx = tkIdx;

		    tkRepo.addToken(tk);

		    tkIdx++;

		    if (tk.tokenType != TimeLimitTokenType.WHITESPACE) {
			int k = 0;
		    }

		    continue;
		}
	}
	return tkRepo;
    }

    // public boolean parse() {
    // return parse(this.repo);
    // }

    public boolean parse(TimeLimitTokenRepo tkRepo) {

	Mutable<TimeLimitToken> result = new MutableObject<>();

	TimeLimitToken monTk = null;

	if (tkRepo.findTk1st(TimeLimitTokenType.MON, result)) {
	    monTk = result.getValue();
	    monTk.tokenRole = TimeLimitTokenRole.MON;
	    tkRepo.updateTokenRole2TermPositionSet(monTk);

	} else {
	    throw new SException(NLPErrorCode.PARSE_ERROR, "'no MON token'  eror");
	}

	TimelimitTokenQuery tq = TimelimitTokenQuery.createForwardTokenQuery(tkRepo, TimeLimitTokenType.D2, monTk.tkIdx);
	// allow 1 white
	tq.setMaxTkSpanIncludeWritespace(1);

	// year
	if (tkRepo.tkfind(tq, result)) {
	    TimeLimitToken year = result.getValue();
	    year.tokenRole = TimeLimitTokenRole.YEAR;
	    tkRepo.updateTokenRole2TermPositionSet(year);
	}

	// time
	// find neartest time5 or time4 pattern

	List<TimeLimitTokenType> lsTypes = Arrays.asList(TimeLimitTokenType.TIME5, TimeLimitTokenType.TIME4);

	FIND_TIME: for (TimeLimitTokenType timeType : lsTypes) {

	    boolean found = tkRepo.tkfindNear(timeType, monTk.tkIdx, result, SearchDirectionPriority.BACKWORD_FIRST);

	    if (found) {
		TimeLimitToken time = result.getValue();
		if (time != null && PnrDateUtil.isTimeLimitTimeStr(time.image)) {
		    time.tokenRole = TimeLimitTokenRole.HOUR_MIN;
		    tkRepo.updateTokenRole2TermPositionSet(time);
		    break FIND_TIME;
		}
	    }
	}
	// day
	TimelimitTokenQuery backTkQuery = TimelimitTokenQuery.createBackwardTokenQuery(tkRepo, TimeLimitTokenType.D2, monTk.tkIdx);
	if (tkRepo.tkfind(backTkQuery, result)) {
	    TimeLimitToken day = result.getValue();
	    day.tokenRole = TimeLimitTokenRole.DAY;
	    tkRepo.updateTokenRole2TermPositionSet(day);
	}

	return true;

    }

}
