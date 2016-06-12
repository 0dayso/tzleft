package com.travelzen.farerule.jpecker.pecker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.farerule.AdvanceTicket;
import com.travelzen.farerule.condition.OriginCondition;
import com.travelzen.farerule.jpecker.struct.RuleTextBlock;
import com.travelzen.farerule.jpecker.tool.ConditionTransducer;
import com.travelzen.farerule.rule.AdvanceTicketItem;
import com.travelzen.farerule.rule.AdvanceTicketSubItem;
import com.travelzen.farerule.rule.TimeTypeEnum;

public class Jpecker5 extends JpeckerBase {

	private static final Logger logger = LoggerFactory.getLogger(Jpecker5.class);
	
	public static AdvanceTicket parse(String ruleText) {
		if (ruleText.contains("NO ADVANCED RESERVATION/TICKET REQUIREMENTS")) {
			return null;
		}
		
		List<AdvanceTicketItem> advanceTicketItemList = new ArrayList<AdvanceTicketItem>();
		
		List<RuleTextBlock> ruleTextBlockList = splitOrigins(ruleText);
		
		for (RuleTextBlock ruleTextBlock:ruleTextBlockList) {
			AdvanceTicketItem advanceTicketItem = new AdvanceTicketItem();
			List<AdvanceTicketSubItem> advanceTicketSubItemList = new ArrayList<AdvanceTicketSubItem>();
			String[] segments = ruleTextBlock.getText().split("\\bOR\\s*-");
			for (String segment:segments) {
				AdvanceTicketSubItem advanceTicketSubItem = new AdvanceTicketSubItem();
				Pair<TimeTypeEnum, Integer> timeAR = parseTimeAfterReservation(segment);
				Pair<TimeTypeEnum, Integer> timeBD = parseTimeBeforeDeparture(segment);
				if (timeAR == null && timeBD == null)
					continue;
				if (timeAR != null) {
					advanceTicketSubItem.setTimeTypeAfterReservation(timeAR.getValue0());
					advanceTicketSubItem.setTimeNumAfterReservation(timeAR.getValue1());
				}
				if (timeBD != null) {
					advanceTicketSubItem.setTimeTypeBeforeDeparture(timeBD.getValue0());
					advanceTicketSubItem.setTimeNumBeforeDeparture(timeBD.getValue1());
				}
				Pair<TimeTypeEnum, Integer> rTime = parseReservationTime(segment);
				if (rTime != null) {
					advanceTicketSubItem.setReservationTimeType(rTime.getValue0());
					advanceTicketSubItem.setReservationTimeNum(rTime.getValue1());
				}
				advanceTicketSubItemList.add(advanceTicketSubItem);
			}
			
			if (advanceTicketSubItemList.size() != 0) {
				if (ruleTextBlock.getOrigin() != null) {
					OriginCondition originCondition = ConditionTransducer.parseOrigin(ruleTextBlock.getOrigin());
					advanceTicketItem.setOriginCondition(originCondition);
				}
				advanceTicketItem.setAdvanceTicketSubItemList(advanceTicketSubItemList);
				advanceTicketItemList.add(advanceTicketItem);
			}
		}
		
		AdvanceTicket advanceTicket = new AdvanceTicket();
		advanceTicket.setAdvanceTicketItemList(advanceTicketItemList);
		return advanceTicket;
	}
	
	private static Pair<TimeTypeEnum, Integer> parseReservationTime(String text) {
		Matcher matcher = Pattern.compile(
				"(\\d{1,3})\\s*(DAY|MONTH|YEAR|HOUR)(?:S)?\\s*BEFORE\\s*DEPARTURE\\s*TICKETING").matcher(text);
		if (matcher.find()) {
			return Pair.with(TimeTypeEnum.valueOf(matcher.group(2)), Integer.parseInt(matcher.group(1)));
		}
		return null;
	}
	
	private static Pair<TimeTypeEnum, Integer> parseTimeAfterReservation(String text) {
		Matcher matcher = Pattern.compile(
				"(\\d{1,3})\\s*(DAY|MONTH|YEAR|HOUR)(?:S)?\\s*AFTER\\s*RESERVATION").matcher(text);
		if (matcher.find()) {
			return Pair.with(TimeTypeEnum.valueOf(matcher.group(2)), Integer.parseInt(matcher.group(1)));
		}
		return null;
	}
	
	private static Pair<TimeTypeEnum, Integer> parseTimeBeforeDeparture(String text) {
		Matcher matcher = Pattern.compile(
				"(\\d{1,3})\\s*(DAY|MONTH|YEAR|HOUR)(?:S)?\\s*BEFORE\\s*DEPARTURE(?!\\s*TICKETING)").matcher(text);
		if (matcher.find()) {
			return Pair.with(TimeTypeEnum.valueOf(matcher.group(2)), Integer.parseInt(matcher.group(1)));
		}
		return null;
	}
	
	public static void main(String[] args) {
		String s1 = " 05.ADVANCE RES/TICKETING\n"
				+ "NO ADVANCED RESERVATION/TICKET REQUIREMENTS.";
		String s2 = " 05.ADVANCE RES/TICKETING\n"
				+ "TICKETING MUST BE COMPLETED AT LEAST 3 DAYS BEFORE\n"
				+ "DEPARTURE.";
		String s3 = " 05.ADVANCE RES/TICKETING\n"
				+ "RESERVATIONS AND TICKETING ARE REQUIRED AT LEAST 2 MONTHS\n"
				+ "BEFORE DEPARTURE.\n"
				+ "OPEN RETURNS PERMITTED.";
		String s4 = " 05.ADVANCE RES/TICKETING\n"
				+ "TICKETING MUST BE COMPLETED WITHIN 24 HOURS AFTER\n"
				+ "RESERVATIONS ARE MADE.";
		String s5 = " 05.ADVANCE RES/TICKETING\n"
				+ "TICKETING MUST BE COMPLETED WITHIN 72 HOURS AFTER\n"
				+ "RESERVATIONS ARE MADE OR AT LEAST 10 DAYS BEFORE DEPARTURE\n"
				+ "WHICHEVER IS EARLIER.";
		String s6 = " 05.ADVANCE RES/TICKETING\n"
				+ "RESERVATIONS ARE REQUIRED FOR ALL SECTORS.\n"
				+ "WHEN RESERVATIONS ARE MADE AT LEAST 100 DAYS BEFORE\n"
				+ "DEPARTURE TICKETING MUST BE COMPLETED AT LEAST 93 DAYS\n"
				+ "BEFORE DEPARTURE.\n"
				+ "OR - RESERVATIONS ARE REQUIRED FOR ALL SECTORS.\n"
				+ "WHEN RESERVATIONS ARE MADE AT LEAST 11 DAYS BEFORE\n"
				+ "DEPARTURE TICKETING MUST BE COMPLETED WITHIN 50 DAYS\n"
				+ "AFTER RESERVATIONS ARE MADE OR AT LEAST 30 DAYS\n"
				+ "BEFORE DEPARTURE WHICHEVER IS EARLIER.\n"
				+ "OR - RESERVATIONS ARE REQUIRED FOR ALL SECTORS.\n"
				+ "TICKETING MUST BE COMPLETED WITHIN 24 HOURS AFTER\n"
				+ "RESERVATIONS ARE MADE.";
		System.out.println(parse(s1));
		System.out.println(parse(s2));
		System.out.println(parse(s3));
		System.out.println(parse(s4));
		System.out.println(parse(s5));
		System.out.println(parse(s6));
	}

}
