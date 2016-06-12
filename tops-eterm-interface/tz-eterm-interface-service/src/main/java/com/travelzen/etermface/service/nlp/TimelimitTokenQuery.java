package com.travelzen.etermface.service.nlp;

public class TimelimitTokenQuery {

    TimeLimitTokenRepo tokenRepo;

    SearchDirection searchDirection;

    TimeLimitTokenType queryTokenType;

    int targetIdx = -1;

    // span from which termPosition
    int lowboundIdx = -1;

    int highboundIdx = -1;

    // 1 means close near , no token between srcToken and queryToken, even
    // whitespace
    int maxTkSpan = -1;

    // 1 means allow 1 whitespace between srcToken and queryToken
    int maxTkSpanIncludeWritespace = -1;

    public TimelimitTokenQuery createForwardWithSpanLimitTokenQuery(TimeLimitTokenRepo tokenRepo, TimeLimitTokenType queryTokenType, int targetIdx,
	    int maxTkSpan, int maxTkSpanIncludeWritespace) {

	TimelimitTokenQuery query = new TimelimitTokenQuery(queryTokenType);

	query.tokenRepo = tokenRepo;
	query.targetIdx = targetIdx;
	query.searchDirection = SearchDirection.FORWORD;
	query.maxTkSpan = maxTkSpan;
	query.maxTkSpanIncludeWritespace = maxTkSpanIncludeWritespace;

	return query;
    }

    static public TimelimitTokenQuery createForwardTokenQuery(TimeLimitTokenRepo tokenRepo, TimeLimitTokenType queryTokenType, int targetIdx) {

	TimelimitTokenQuery query = new TimelimitTokenQuery(queryTokenType);

	query.tokenRepo = tokenRepo;
	// query.lowboundIdx = lowboundIdx;
	query.targetIdx = targetIdx;
	query.searchDirection = SearchDirection.FORWORD;

	return query;
    }

    static public TimelimitTokenQuery createBackwardTokenQuery(TimeLimitTokenRepo tokenRepo, TimeLimitTokenType queryTokenType, int targetIdx) {

	TimelimitTokenQuery query = new TimelimitTokenQuery(queryTokenType);

	query.tokenRepo = tokenRepo;
	// query.highboundIdx = highboundIdx;
	query.targetIdx = targetIdx;
	query.searchDirection = SearchDirection.BACKWORD;

	return query;
    }

    /**
     * @TODO add maxTkSpan ( filter nonWhiteSpaceToken)
     * @param tkIdx
     * @return
     */
    public boolean judgeSpan(int tkIdx) {
	if (maxTkSpanIncludeWritespace > 0) {
	    return (tkIdx - targetIdx) < maxTkSpanIncludeWritespace + 1;
	}

	return true;
    }

    @Override
    public String toString() {
	return "TimelimitTokenQuery [tokenRepo=" + tokenRepo + ", searchDirection=" + searchDirection + ", queryTokenType=" + queryTokenType + ", targetIdx="
		+ targetIdx + ", lowboundIdx=" + lowboundIdx + ", highboundIdx=" + highboundIdx + ", maxTkSpan=" + maxTkSpan + ", maxTkSpanIncludeWritespace="
		+ maxTkSpanIncludeWritespace + "]";
    }

    public TimelimitTokenQuery(TimeLimitTokenType queryTokenType, int lowboundIdx) {
	super();
	this.queryTokenType = queryTokenType;
	this.lowboundIdx = lowboundIdx;
    }

    public TimelimitTokenQuery(TimeLimitTokenType queryTokenType) {
	super();
	this.queryTokenType = queryTokenType;
    }

    public TimeLimitTokenType getQueryTokenType() {
	return queryTokenType;
    }

    public void setQueryTokenType(TimeLimitTokenType queryTokenType) {
	this.queryTokenType = queryTokenType;
    }

    public int getLowboundIdx() {
	return lowboundIdx;
    }

    public void setLowboundIdx(int lowboundIdx) {
	this.lowboundIdx = lowboundIdx;
    }

    public int getMaxTkSpan() {
	return maxTkSpan;
    }

    public void setMaxTkSpan(int maxTkSpan) {
	this.maxTkSpan = maxTkSpan;
    }

    public int getMaxTkSpanIncludeWritespace() {
	return maxTkSpanIncludeWritespace;
    }

    public void setMaxTkSpanIncludeWritespace(int maxTkSpanIncludeWritespace) {
	this.maxTkSpanIncludeWritespace = maxTkSpanIncludeWritespace;
    }

}
