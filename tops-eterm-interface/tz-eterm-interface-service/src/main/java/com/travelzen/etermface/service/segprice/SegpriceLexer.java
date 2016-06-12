package com.travelzen.etermface.service.segprice;

import it.unimi.dsi.lang.MutableString;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.travelzen.etermface.service.PNRParser;
import com.travelzen.etermface.service.constant.PnrParserConstant;

public class SegpriceLexer {

    private static Logger logger = LoggerFactory.getLogger(SegpriceLexer.class);

    String fullStr;

    public SegpriceLexer(MutableString fullStr) {
	this.fullStr = fullStr.toString();
    }

    public SegpriceLexer(String fullStr) {
	this.fullStr = fullStr;
    }

    List<Integer> aheadStack = Lists.newArrayList();

    int curIdx = 0;

    public String getRestStr() {
	return fullStr.substring(curIdx);
    }

    public int moveAhead(int aheadStep) {
	aheadStack.add(curIdx);
	curIdx = curIdx + aheadStep;
	return aheadStep;

    }

    public int pushBackTk(int num) {
	int idx = 0;
	Preconditions.checkArgument(num > 0);

	for (int i = num; i > 0; i--) {
	    idx = aheadStack.get(aheadStack.size() - num);
	}
	curIdx = idx;
	return curIdx;
    }

    public SPToken peekToken() {
	SegpriceLexer restLexer = new SegpriceLexer(this.getRestStr());
	return restLexer.nextToken();
    }

    static public List<String> mstrs = Lists.newArrayList();
    static public Pattern M_PATTERN;
    static {
	mstrs.addAll(Arrays.asList("M", "5M", "10M", "15M", "20M", "25M"));

	M_PATTERN = Pattern.compile(StringUtils.join(mstrs, "|"));
    }

    public static boolean isMToken(String restStr) {

	for (String mstr : mstrs) {
	    if (restStr.equalsIgnoreCase(mstr)) {
		return true;
	    }
	}
	return false;
    }

    public static boolean isStartWithMToken(String str) {

	String capStr = str.toUpperCase();

	for (String mstr : mstrs) {
	    if (capStr.startsWith(mstr)) {
		return true;
	    }
	}
	return false;
    }

    public SPTokenType peekTokenType() {

	String restStr = getRestStr();
	restStr = restStr.trim();

	if (restStr.contains("(") && restStr.contains(")")) {
	    int idx = restStr.indexOf("(");
	    String tk = StringUtils.trim(restStr.substring(0, idx));
	    if (PNRParser.isCarrierCode(tk)) {
		return SPTokenType.CARRIER;
	    }
	}

	if (restStr.startsWith(")") || restStr.startsWith("(")) {
	    return SPTokenType.PAR;
	}

	if (restStr.startsWith("Q") && (restStr.length() > 1 && !Character.isAlphabetic(restStr.charAt(1)))) {
	    return SPTokenType.Q;
	}

	if (restStr.startsWith("/-")) {
	    return SPTokenType.ARNK;
	}

	if (restStr.startsWith("X/")) {
	    return SPTokenType.TRANSFER;
	}

	if (restStr.startsWith("/IT")) {
	    return SPTokenType.IT;
	}

	if (restStr.startsWith("//")) {
	    return SPTokenType.DOUBLE_SLASH;
	}

	if (restStr.startsWith("/") && restStr.substring(1).trim().startsWith("IT")) {
	    return SPTokenType.IT;
	}

	if (restStr.startsWith("M") && (restStr.length() > 1 && !Character.isAlphabetic(restStr.charAt(1)))) {
	    return SPTokenType.M;
	}

	if (isStartWithMToken(restStr)) {
	    int idx = restStr.indexOf("M");
	    if (!Character.isAlphabetic(restStr.charAt(idx + 1))) {
		return SPTokenType.M;
	    }
	}

	//		1S  205.53NUC3913.43END ROE6.081590
	//		1S205.53NUC3913.43END ROE6.081590
	if (restStr.startsWith(SegPriceParser.SVALUE)) {
	    int idx = restStr.indexOf(SegPriceParser.SVALUE);
	    SegpriceLexer tlexer = new SegpriceLexer(restStr.substring(idx + SegPriceParser.SVALUE.length()));
	    if (tlexer.peekTokenType() == SPTokenType.NUMBER) {
		return SPTokenType.SVALUE;
	    }
	}

	if (restStr.startsWith(SegPriceParser.ONE_S)) {
	    int idx = restStr.indexOf(SegPriceParser.ONE_S);
	    SegpriceLexer tlexer = new SegpriceLexer(restStr.substring(idx + SegPriceParser.ONE_S.length()));
	    if (tlexer.peekTokenType() == SPTokenType.NUMBER) {
		return SPTokenType.ONE_S;
	    }
	}

	if (restStr.startsWith("E/")) {
	    return SPTokenType.E;
	}

	SegpriceLexer restLexer = new SegpriceLexer(restStr);

	if (!StringUtils.isNumeric(StringUtils.substring(restStr, 0, 1))) {
	    return SPTokenType.WORD;
	}

	SPToken tk = restLexer.nextNumber(new SegpriceLexInfo());
	if (tk.ttype == SPTokenType.NUMBER) {
	    return SPTokenType.NUMBER;
	}

	return SPTokenType.WORD;

    }

    public SPToken nextToken(String str) {
	SegpriceLexer lexer = new SegpriceLexer(str);
	return nextToken(lexer);
    }

    public SPToken nextNumber(String str) {
	SegpriceLexer lexer = new SegpriceLexer(str);
	return lexer.nextNumber();
    }

    public SPToken nextNumber() {

	return nextNumber(new SegpriceLexInfo());
    }

    /**
     * return 0 when exception
     * 
     * @return
     */
    public SPToken nextNumber(SegpriceLexInfo pi) {

	MutableString str = new MutableString(fullStr.substring(curIdx));

	SPToken tk = new SPToken();

	int firstDigit = 0;

	FIND_NONE_DIGIT: while (true) {
	    if (str.length() <= firstDigit) {
		logger.error("firstNotSpace err:{} {}", str, firstDigit);

		SPToken stk = new SPToken();
		stk.ttype = SPTokenType.WORD;
		moveAhead(firstDigit);
		return stk;
	    }
	    char c = str.charAt(firstDigit);
	    if (!Character.isDigit(c) && '-' != c && '.' != c) {
		firstDigit++;
	    } else {
		break FIND_NONE_DIGIT;
	    }
	}

	int firstNotDigit = firstDigit + 1;

	FIND_Number: while (true) {
	    // find next not digit
	    while (true) {
		if (firstNotDigit >= str.length()) {
		    //meet the end of string
		    //					'''04JAN14WAW LH X/FRA LH X/SHA599.09LH X/MUC LH PRG M1878.56NU
		    //					C2477.65END ROE3.067150'''
		    break;
		}

		char c = str.charAt(firstNotDigit);
		if (Character.isDigit(c) || '-' == c || '.' == c) {
		    firstNotDigit++;
		} else {
		    break;
		}
	    }

	    // judge if legal Number
	    String numStr = StringUtils.substring(str.toString(), firstDigit, firstNotDigit);

	    try {
		double numValue = Double.parseDouble(numStr);
		tk.tkImgNumberValue = numValue;
		tk.ttype = SPTokenType.NUMBER;

		tk.tkStartIdx = curIdx + firstDigit;
		tk.tkEndIdx = curIdx + firstNotDigit;

		pi.tkStartIdx = firstDigit;
		pi.tkEndIdx = firstNotDigit;

		tk.tkImg = new MutableString(numStr.trim());

		moveAhead(firstNotDigit);

		return tk;
	    } catch (NumberFormatException nfe) {
		//not very good find-nxt-number method. but just leave it as it be,
		firstNotDigit++;
		continue FIND_Number;
	    }

	}

    }

    int skipToken(int cnt) {

	Preconditions.checkArgument(cnt > 0);

	SegpriceLexInfo fi = new SegpriceLexInfo();

	for (int i = 1; i <= cnt; i++) {
	    SPToken tk = nextToken(fi);
	    if (tk == null) {
		return i;
	    }
	}
	return cnt;
    }

    final static String M = "M";

    boolean isPar(String str) {
	return "(".equals(str) || ")".equals(str);
    }

    public SPToken nextParToken() {

	return nextToken(new SegpriceLexInfo() {
	    @Override
	    public boolean accept(MutableString tkImg) {
		String originImg = tkImg.toString();
		if (isPar(originImg.trim())) {
		    this.targetType = SPTokenType.PAR;
		    return true;
		} else {
		    return false;
		}
	    }

	    @Override
	    public MutableString buildToken(MutableString tkImg) {
		String originImg = tkImg.toString();
		this.tkStartIdx = 0;
		this.tkEndIdx = SegPriceParser.PAR_TYPE_LENGTH;
		this.targetType = SPTokenType.TRANSFER;
		return new MutableString(originImg.substring(SegPriceParser.PAR_TYPE_LENGTH));
	    }
	});
    }

    public SPToken nextXToken() {

	return nextToken(new SegpriceLexInfo() {
	    @Override
	    public boolean accept(MutableString tkImg) {
		String originImg = tkImg.toString();
		if (StringUtils.equals(SegPriceParser.X_TYPE, originImg)) {
		    this.targetType = SPTokenType.TRANSFER;
		    return true;
		} else {
		    return false;
		}
	    }

	    @Override
	    public MutableString buildToken(MutableString tkImg) {
		String originImg = tkImg.toString();
		this.tkStartIdx = 0;
		this.tkEndIdx = SegPriceParser.X_TYPE.length();
		this.targetType = SPTokenType.TRANSFER;
		return new MutableString(originImg.substring(SegPriceParser.X_TYPE.length()));
	    }
	});
    }

    public SPToken nextEToken() {

	return nextToken(new SegpriceLexInfo() {
	    @Override
	    public boolean accept(MutableString tkImg) {
		String originImg = tkImg.toString();
		if (StringUtils.equals(SegPriceParser.E_TYPE, originImg)) {
		    this.targetType = SPTokenType.E;
		    return true;
		} else {
		    return false;
		}
	    }

	    @Override
	    public MutableString buildToken(MutableString tkImg) {
		String originImg = tkImg.toString();
		this.tkStartIdx = 0;
		this.tkEndIdx = SegPriceParser.E_TYPE.length();
		this.targetType = SPTokenType.E;
		return new MutableString(originImg.substring(SegPriceParser.E_TYPE.length()));
	    }
	});
    }

    public SPToken nextOneSToken() {

	return nextToken(new SegpriceLexInfo() {
	    @Override
	    public boolean accept(MutableString tkImg) {
		String originImg = tkImg.toString();
		if (StringUtils.equals(SegPriceParser.ONE_S, originImg)) {
		    this.targetType = SPTokenType.ONE_S;
		    return true;
		} else {
		    return false;
		}
	    }

	    @Override
	    public MutableString buildToken(MutableString tkImg) {
		String originImg = tkImg.toString();
		this.tkStartIdx = 0;
		this.tkEndIdx = SegPriceParser.ONE_S.length();
		//				this.tkImgNumberValue = NumberUtils.toDouble(originImg.substring(SegPriceParser.ONE_S.length()));
		this.targetType = SPTokenType.ONE_S;
		return new MutableString(originImg.substring(SegPriceParser.ONE_S.length()));
	    }
	});
    }

    // Next Token
    public SPToken nextMToken() {
	return nextToken(new SegpriceLexInfo() {
	    @Override
	    public boolean accept(MutableString tkImg) {
		String originImg = tkImg.toString();
		return isMToken(originImg);
	    }

	    @Override
	    public MutableString buildToken(MutableString tkImg) {
		String originImg = tkImg.toString();

		Matcher m = M_PATTERN.matcher(originImg);
		if (m.find()) {
		    this.tkStartIdx = m.start();
		    this.tkEndIdx = m.end();
		    return new MutableString(originImg.substring(tkStartIdx, tkEndIdx));
		} else {
		    return null;
		}
	    }
	});
    }

    public SPToken nextCityToken() {
	return nextToken(new SegpriceLexInfo() {
	    @Override
	    public boolean accept(MutableString tkImg) {
		return tkImg.length() == PnrParserConstant.CITY_CODE_LENGTH && StringUtils.isAlpha(tkImg);
	    }

	    @Override
	    public MutableString buildToken(MutableString tkImg) {
		this.tkStartIdx = 0;
		this.tkEndIdx = PnrParserConstant.CITY_CODE_LENGTH;
		if (tkImg.trim().length() >= PnrParserConstant.CITY_CODE_LENGTH) {
		    return tkImg.substring(0, PnrParserConstant.CITY_CODE_LENGTH);
		} else {
		    this.targetType = SPTokenType.WORD;
		    return tkImg.trim();
		}

	    }
	});
    }

    // Next Token
    public SPToken nextCarrierToken() {
	return nextToken(new SegpriceLexInfo() {
	    @Override
	    public boolean accept(MutableString tkImg) {
		return tkImg.length() == PnrParserConstant.CARRIER_CODE_LENGTH && StringUtils.isAlpha(tkImg);
	    }

	    @Override
	    public MutableString buildToken(MutableString tkImg) {
		this.tkStartIdx = 0;
		this.tkEndIdx = PnrParserConstant.CARRIER_CODE_LENGTH;

		//skip the  (PA) in :
		//SHA AA(PA)DFW1001.14 NUC1001.14END ROE6.130990

		int idxRightPar = tkImg.indexOf(")");
		if (idxRightPar > 0) {
		    this.tkEndIdx = idxRightPar + 1;
		}

		if (tkImg.trim().length() >= PnrParserConstant.CARRIER_CODE_LENGTH) {
		    return tkImg.substring(0, PnrParserConstant.CARRIER_CODE_LENGTH);
		} else {
		    this.targetType = SPTokenType.CARRIER;
		    return tkImg.trim();
		}

	    }
	});
    }

    // Next Token
    public SPToken nextToken(SegpriceLexer lexer) {
	return nextToken(lexer, new SegpriceLexInfo());
    }

    // Next Token
    public SPToken nextToken() {
	return nextToken(new SegpriceLexInfo());
    }

    public SPToken nextToken(SegpriceLexInfo pi) {
	return nextToken(this, pi);
    }

    // Next Token
    public SPToken nextToken(SegpriceLexer lexer, SegpriceLexInfo pi) {

	MutableString str = new MutableString(lexer.fullStr.substring(lexer.curIdx));

	if (str.length() == 0 || StringUtils.isBlank(str.toString())) {
	    return null;
	}

	SPToken tk = new SPToken();

	int firstNotSpace = 0;

	FIND_NONE_CHAR: while (true) {

	    if (firstNotSpace >= str.length()) {
		break FIND_NONE_CHAR;
	    }

	    if (Character.isWhitespace(str.charAt(firstNotSpace))) {
		firstNotSpace++;
	    } else {
		break FIND_NONE_CHAR;
	    }
	}

	int lastNotSpace = firstNotSpace + 1;

	while (true) {
	    if (lastNotSpace >= str.length()) {
		break;
	    }

	    char c = str.charAt(lastNotSpace);
	    if (!Character.isWhitespace(c)) {
		lastNotSpace++;
	    } else {
		break;
	    }
	}

	tk.tkImg = str.substring(firstNotSpace, lastNotSpace);

	int moveAheadCnt = lastNotSpace;

	boolean ttypeDefined = false;
	if (StringUtils.isNumeric(tk.tkImg)) {
	    tk.ttype = SPTokenType.NUMBER;
	    ttypeDefined = true;
	}

	if (!pi.accept(tk.tkImg)) {
	    tk.tkImg = pi.buildToken(tk.tkImg);

	    if (null != tk.tkImg) {

		tk.tkStartIdx = lexer.curIdx + pi.tkStartIdx;
		tk.tkEndIdx = lexer.curIdx + pi.tkEndIdx;

		moveAheadCnt = firstNotSpace + pi.tkEndIdx;
		ttypeDefined = true;

		tk.ttype = pi.targetType;

	    } else {
		ttypeDefined = false;
	    }

	} else {

	    tk.tkStartIdx = lexer.curIdx + firstNotSpace;
	    tk.tkEndIdx = lexer.curIdx + lastNotSpace;
	    tk.ttype = pi.targetType;

	    moveAheadCnt = lastNotSpace;

	    ttypeDefined = true;
	}

	if (pi.targetType == SPTokenType.NUMBER || pi.targetType == SPTokenType.ONE_S) {
	    tk.tkImgNumberValue = pi.tkImgNumberValue;
	}

	if (!ttypeDefined) {
	    tk.ttype = SPTokenType.WORD;
	}

	lexer.moveAhead(moveAheadCnt);
	return tk;

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	// TODO Auto-generated method stub

    }

}
