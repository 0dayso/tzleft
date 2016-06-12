package com.travelzen.etermface.service.entity.qte;

public class ExchangeRate {

    private String preCountryCode;

    private String suffixCountryCode;

    private double rate;

    public String getPreCountryCode() {
        return preCountryCode;
    }

    public void setPreCountryCode(String preCountryCode) {
        this.preCountryCode = preCountryCode;
    }

    public String getSuffixCountryCode() {
        return suffixCountryCode;
    }

    public void setSuffixCountryCode(String suffixCountryCode) {
        this.suffixCountryCode = suffixCountryCode;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

	@Override
	public String toString() {
		return "ExchangeRate [preCountryCode=" + preCountryCode
				+ ", suffixCountryCode=" + suffixCountryCode + ", rate=" + rate
				+ "]";
	}
    
}
