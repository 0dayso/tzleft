package com.travelzen.etermface.service.nlp;

public class TimeLimitToken {

    public TimeLimitToken(TimeLimitTokenType tokenType, String image) {
	this.tokenType = tokenType;
	this.image = image;
    }

    public TimeLimitToken(TimeLimitTokenType tokenType, String image, int tkIdx) {
	this.tokenType = tokenType;
	this.image = image;
	this.tkIdx = tkIdx;
    }

    public TimeLimitTokenRole tokenRole;

    public TimeLimitTokenType tokenType;

    public TimeLimitTokenSubType tokenSubType;

    public int tkIdx;

    public String image;

    @Override
    public String toString() {
	return "TimeLimitToken [tokenRole=" + tokenRole + ", tokenType=" + tokenType + ", tokenSubType=" + tokenSubType + ", tkIdx=" + tkIdx + ", image="
		+ image + "]";
    }

}
