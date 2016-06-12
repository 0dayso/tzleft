package com.travelzen.farerule.jpecker.pecker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.farerule.PassengerLimit;
import com.travelzen.farerule.jpecker.consts.PassengerConst;
import com.travelzen.farerule.rule.Judge;
import com.travelzen.farerule.rule.PassengerAgeLimitItem;
import com.travelzen.farerule.rule.PassengerNumLimitItem;
import com.travelzen.farerule.rule.PassengerType;

public class Jpecker1 extends JpeckerBase {
	
	private static final Logger logger = LoggerFactory.getLogger(Jpecker1.class);

	public static PassengerLimit parse(String ruleText) {
		List<PassengerAgeLimitItem> passengerAgeLimitItemList = null;
		List<PassengerNumLimitItem> passengerNumLimitItemList = null;
		
		if (ruleText.contains("NO ELIGIBILITY REQUIREMENTS")) {
			logger.info("无限制。");
			return null;
		}
		
		ruleText = ruleText.replaceAll("\n", " ");
		
		passengerAgeLimitItemList = parsePassengerAge(ruleText);
		passengerNumLimitItemList = parsePassengerNum(ruleText);
		
		if (passengerAgeLimitItemList.size() == 0 && passengerNumLimitItemList.size() == 0) {
			logger.info("无有效限制。");
			return null;
		}
		
		PassengerLimit passengerLimit = new PassengerLimit();
		if (passengerAgeLimitItemList.size() != 0)
			passengerLimit.setPassengerAgeLimitItemList(passengerAgeLimitItemList);
		if (passengerNumLimitItemList.size() != 0)
			passengerLimit.setPassengerNumLimitItemList(passengerNumLimitItemList);
		
		return passengerLimit;
	}

	private static List<PassengerAgeLimitItem> parsePassengerAge(String text) {
		List<PassengerAgeLimitItem> passengerAgeLimitItemList = new ArrayList<PassengerAgeLimitItem>();
		
		String positive_pre = "\\b(?:VALID|OR\\s*-)\\s*FOR";
		String negtive_pre = "\\bNOT\\s*(?:VALID|APPLICABLE|PERMITTED)\\s*FOR";
		String negtive_suffix = "(?:IS\\s*|WILL\\s*)?NOT\\s*(?:ELIGIBLE|APPLY|(?:BE\\s*)?PERMITTED)";
		
		Pattern pattern_age1 = Pattern.compile("(?<minAge>\\d+)\\s*-\\s*(?<maxAge>\\d+)");
		Pattern pattern_age2 = Pattern.compile("UNDER\\s*(?<maxAge>\\d+)");
		Pattern pattern_age3 = Pattern.compile("(?<minAge>\\d+)\\s*OR\\s*OLDER");
		
		
		
		return passengerAgeLimitItemList;
	}
	
	private static Judge isAccompanied(String text) {
		return text.contains("UNACCOMPANIED")?Judge.NEGATIVE:Judge.POSITIVE;
	}
	
	private static Judge hasSeat(String text) {
		return text.contains("WITH A SEAT")?Judge.POSITIVE:Judge.NEGATIVE;
	}
	
	private static List<PassengerNumLimitItem> parsePassengerNum(String text) {
		List<PassengerNumLimitItem> passengerNumLimitItemList = new ArrayList<PassengerNumLimitItem>();
		
		Matcher matcher1 = Pattern.compile("MINIMUM\\s*(\\d+)\\s*PASSENGERS").matcher(text);
		Matcher matcher2 = Pattern.compile("MINIMUM\\s*(\\d+)\\s*ADULT\\s*REQUIRED").matcher(text);
		Matcher matcher3 = Pattern.compile("MINIMUM\\s*(\\d+)\\s*ADULT\\s*PASSENGERS").matcher(text);
		Matcher matcher4 = Pattern.compile("GROUPS\\s*OF\\s*(\\d+)\\s*-\\s*(\\d+)(?:ADULT)?\\s*PASSENGERS").matcher(text);
		
		if (matcher1.find()) {
			PassengerNumLimitItem passengerNumLimitItem = new PassengerNumLimitItem();
			passengerNumLimitItem.setJudge(Judge.POSITIVE);
			passengerNumLimitItem.setMinNum(Integer.parseInt(matcher1.group(1)));
//			passengerNumLimitItem.setMaxNum(9);
			if (matcher2.find()) {
				passengerNumLimitItem.setMinAdultNum(Integer.parseInt(matcher2.group(1)));
			}
			passengerNumLimitItemList.add(passengerNumLimitItem);
		}
		if (matcher3.find()) {
			PassengerNumLimitItem passengerNumLimitItem = new PassengerNumLimitItem();
			passengerNumLimitItem.setJudge(Judge.POSITIVE);
			passengerNumLimitItem.setMinNum(Integer.parseInt(matcher3.group(1)));
			passengerNumLimitItem.setMinAdultNum(Integer.parseInt(matcher3.group(1)));
//			passengerNumLimitItem.setMaxNum(9);
			passengerNumLimitItemList.add(passengerNumLimitItem);
		}
		
		if (matcher4.find()) {
			PassengerNumLimitItem passengerNumLimitItem = new PassengerNumLimitItem();
			passengerNumLimitItem.setJudge(Judge.POSITIVE);
			passengerNumLimitItem.setMinNum(Integer.parseInt(matcher4.group(1)));
			passengerNumLimitItem.setMaxNum(Integer.parseInt(matcher4.group(2)));
			passengerNumLimitItemList.add(passengerNumLimitItem);
		}
		
		return passengerNumLimitItemList;
	}
	
	public static void main(String[] args) {
		String s1 = " 01.ELIGIBILITY\nNO ELIGIBILITY REQUIREMENTS.";
		String s2 = " 01.ELIGIBILITY\nVALID FOR STUDENT WITH VALID ID.";
		String s3 = " 01.ELIGIBILITY\nVALID FOR STUDENT 18 OR OLDER WITH ID.";
		String s4 = " 01.ELIGIBILITY\nVALID FOR STUDENT AGD 12-35 WITH ID.";
		String s5 = " 01.ELIGIBILITY\nVALID FOR YOUTH CONFIRMED 12-24 WITH ID.\n"
				+ "OR - FOR UNACCOMPANIED CHILD 12-14";
		String s6 = " 01.ELIGIBILITY\nVALID FOR YOUTH CONFIRMED 12-25 WITH ID.\n"
				+ "OR - FOR ADULT WITH AGE RESTRICTION 61 OR OLDER WITH ID.";
		String s7 = " 01.ELIGIBILITY\nNOT VALID FOR TRAVEL FOR UNACCOMPANIED INFANT AND\n"
				+ "UNACCOMPANIED CHILD -UNDER 5 YEARS-";
		String s8 = " 01.ELIGIBILITY\nFARES NOT APPLICABLE FOR\n"
				+ "UNACCOMPANIED INFANT UNDER 2 YEARS AND\n"
				+ "UNACCOMPANIED CHILD 2-7 YEARS";
		String s9 = " 01.ELIGIBILITY\nNOTE -\nCHILD WITHOUT ADULT ACCOMPANY NOT PERMITTED";
		String s10 = " 01.ELIGIBILITY\nNOTE -\nINFANT - NOT ELIGIBLE.\nCHILD - NOT ELIGIBLE.";
		String s11 = " 01.ELIGIBILITY\nNOTE -\nINFANT UNDER 2 WITHOUTH A SEAT NOT APPLY\n"
				+ "UNACCOMPANIED CHILD 5-11 NOT APPLY";
		String s12 = " 01.ELIGIBILITY\nUNACCOMPANIED CHILD AGED 2-4 YEARS OLD- NOT ELIGIBLE.\n"
				+ "UNACCOMPANIED CHILDREN 2-7 YEARS NOT ELIGIBLE TO TRAVEL.";
		String s13 = " 01.ELIGIBILITY\n"
				+ "UNACCOMPANIED CHILDREN AGED 2-7 YEARS WILL NOT BE\n"
				+ "PERMITTED TO TRAVEL AT THIS FARE.\n"
				+ "UNACCOMPANIED INFANT AGED UNDER 2 YEARS OF AGE\n"
				+ "WILL NOT BE PERMITTED TO TRAVEL AT THIS FARE.";
		String s14 = " 01.ELIGIBILITY\n"
				+ "NOTE - GENERAL RULE DOES NOT APPLY\n"
				+ "VALID FOR GROUP INCLUSIVE TOUR PSGR \n"
				+ "NOTE -\n"
				+ "VALID FOR MINIMUM 2 PASSENGERS ";
		String s15 = " 01.ELIGIBILITY\n"
				+ "NOTE -\n"
				+ "VALID FOR GROUPS OF 5-9 PASSENGERS\n"
				+ "NOTE-\n"
				+ "ALL PASSENGERS MUST BE BOOKED ON SAME PNR \n"
				+ "ALL PASSENGERS MUST HAVE THE SAME ITINERARY AND\n"
				+ "TRAVEL TOGETHER THROUGHOUT THE JOURNEY ";
		String s16 = " 01.ELIGIBILITY\n"
				+ "NOTE -\n"
				+ "1 MINIMUM 2 ADULT PASSENGERS\n"
				+ "ALL PASSENGER MUST TRAVEL TOGETHER FOR\n"
				+ "ENTIRE JORUNEY\n"
				+ "2 RESERVATION MUST BE MADE IN THE SAME RECORD\n"
				+ "FOR ALL PASSENGERS AND TICKET MUST BE ISSUED";
		String s17 = " 01.ELIGIBILITY\n"
				+ "VALID FOR ADULT \n"
				+ "NOTE -\n"
				+ "MINIMUM 3 PASSENGERS - MINIMUM 2 ADULT REQUIRED.\n"
				+ "INFANT NOT OCCUPYING SEAT IS NOT COUNTED AS 1\n"
				+ "PASSENGER  1 ADULT PLUS 1 CHILD IS PERMITTED \n"
				+ "ALL PASSENGERS MUST TRAVEL TOGETHER FOR ENTIRE\n"
				+ "JOURNEY ";
		System.out.println(parse(s1));
		System.out.println(parse(s2));
		System.out.println(parse(s3));
		System.out.println(parse(s4));
		System.out.println(parse(s5));
		System.out.println(parse(s6));
		System.out.println(parse(s7));
		System.out.println(parse(s8));
		System.out.println(parse(s9));
		System.out.println(parse(s10));
		System.out.println(parse(s11));
		System.out.println(parse(s12));
		System.out.println(parse(s13));
		System.out.println(parse(s14));
		System.out.println(parse(s15));
		System.out.println(parse(s16));
		System.out.println(parse(s17));
	}

}
