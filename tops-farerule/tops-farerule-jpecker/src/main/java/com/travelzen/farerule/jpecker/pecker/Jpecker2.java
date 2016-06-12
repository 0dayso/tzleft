package com.travelzen.farerule.jpecker.pecker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.farerule.DayTime;
import com.travelzen.farerule.condition.OriginCondition;
import com.travelzen.farerule.jpecker.consts.DateConst;
import com.travelzen.farerule.jpecker.struct.RuleTextBlock;
import com.travelzen.farerule.jpecker.tool.ConditionTransducer;
import com.travelzen.farerule.rule.AmPmEnum;
import com.travelzen.farerule.rule.DayTimeItem;
import com.travelzen.farerule.rule.DayTimePoint;
import com.travelzen.farerule.rule.DayTimeSubItem;
import com.travelzen.farerule.rule.Judge;
import com.travelzen.farerule.rule.WeekDayEnum;

public class Jpecker2 extends JpeckerBase {

	private static final Logger logger = LoggerFactory.getLogger(Jpecker2.class);
	
	public static DayTime parse(String ruleText) {
		if (ruleText.contains("NO DAY/TIME TRAVEL RESTRICTIONS")) {
			return null;
		}
		
		List<DayTimeItem> dayTimeItemList = new ArrayList<DayTimeItem>();
		
		List<RuleTextBlock> ruleTextBlockList = splitOrigins(ruleText);
		
		for (RuleTextBlock ruleTextBlock:ruleTextBlockList) {
			DayTimeItem dayTimeItem = new DayTimeItem();
			Judge judge = judge(ruleTextBlock.getText());
			if (judge == null)
				continue;
			dayTimeItem.setJudge(judge);
			List<DayTimeSubItem> dayTimeSubItemList = new ArrayList<DayTimeSubItem>();
			String[] segments = ruleTextBlock.getText().split("\\s(OR|AND)\\s");
			for (String segment:segments) {
				List<DayTimeSubItem> tmpDayTimeSubItemList = parseDayTimeSubItem(segment);
				if (tmpDayTimeSubItemList.size() != 0)
					dayTimeSubItemList.addAll(tmpDayTimeSubItemList);
			}
			
			if (dayTimeSubItemList.size() != 0) {
				if (ruleTextBlock.getOrigin() != null) {
					OriginCondition originCondition = ConditionTransducer.parseOrigin(ruleTextBlock.getOrigin());
					dayTimeItem.setOriginCondition(originCondition);
				}
				dayTimeItem.setDayTimeSubItemList(dayTimeSubItemList);
				dayTimeItemList.add(dayTimeItem);
			}
		}
		
		DayTime dayTime = new DayTime();
		dayTime.setDayTimeItemList(dayTimeItemList);
		return dayTime;
	}
	
	private static Judge judge(String text) {
		Matcher matcher1 = Pattern.compile("PERMITTED").matcher(text);
		Matcher matcher2 = Pattern.compile("NOT\\s*PERMITTED").matcher(text);
		if (matcher2.find()) {
			return Judge.NEGATIVE;
		} else if (matcher1.find()) {
			return Judge.POSITIVE;
		}
		return null;
	}
	
	private static List<DayTimeSubItem> parseDayTimeSubItem(String text) {
		Pattern pattern1 = Pattern.compile(
			DateConst.TIME+"\\s*"+DateConst.WEEKDAY+"\\s*(?:TO|THROUGH)\\s*"+DateConst.TIME+"\\s*"+DateConst.WEEKDAY);
		Matcher matcher1 = pattern1.matcher(text);
		Pattern pattern2 = Pattern.compile(
			DateConst.TIME+"\\s*(?:TO|THROUGH)\\s*"+DateConst.TIME+"\\s*"+DateConst.WEEKDAYS);
		Matcher matcher2 = pattern2.matcher(text);
		Pattern pattern3 = Pattern.compile(
			DateConst.TIME+"\\s*(?:TO|THROUGH)\\s*"+DateConst.TIME+"\\s*"+DateConst.WEEKDAY);
		Matcher matcher3 = pattern3.matcher(text);
		Pattern pattern4 = Pattern.compile(
			DateConst.WEEKDAY+"\\s*(?:TO|THROUGH)\\s*"+DateConst.WEEKDAY);
		Matcher matcher4 = pattern4.matcher(text);
		Pattern pattern5 = Pattern.compile(DateConst.WEEKDAYS);
		Matcher matcher5 = pattern5.matcher(text);
		Pattern pattern6 = Pattern.compile(DateConst.WEEKDAY);
		Matcher matcher6 = pattern6.matcher(text);
		
		List<DayTimeSubItem> dayTimeSubItemList = new ArrayList<DayTimeSubItem>();
		// list中的每个对象都得new一个，是不同的对象
		DayTimeSubItem dayTimeSubItem = new DayTimeSubItem();
		if (matcher1.find()) {
			dayTimeSubItem.setStartTimePoint(generateDayTimePonit(matcher1.group(1), matcher1.group(2)));
			dayTimeSubItem.setEndTimePoint(generateDayTimePonit(matcher1.group(3), matcher1.group(4)));
			dayTimeSubItemList.add(dayTimeSubItem);
		} else if (matcher2.find()) {
			String[] weekdays = matcher2.group(3).split("\\/");
			for (String weekday:weekdays) {
				DayTimeSubItem dayTimeSubItemTmp = new DayTimeSubItem();
				dayTimeSubItemTmp.setStartTimePoint(generateDayTimePonit(matcher2.group(1), weekday));
				dayTimeSubItemTmp.setEndTimePoint(generateDayTimePonit(matcher2.group(2), weekday));
				dayTimeSubItemList.add(dayTimeSubItemTmp);
			}
		} else if (matcher3.find()) {
			dayTimeSubItem.setStartTimePoint(generateDayTimePonit(matcher3.group(1), matcher3.group(3)));
			dayTimeSubItem.setEndTimePoint(generateDayTimePonit(matcher3.group(2), matcher3.group(3)));
			dayTimeSubItemList.add(dayTimeSubItem);
		} else if (matcher4.find()) {
			dayTimeSubItem.setStartTimePoint(generateDayTimePonit("1201AM", matcher4.group(1)));
			dayTimeSubItem.setEndTimePoint(generateDayTimePonit("1159PM", matcher4.group(2)));
			dayTimeSubItemList.add(dayTimeSubItem);
		} else if (matcher5.find()) {
			String[] weekdays = matcher5.group(1).split("\\/");
			for (String weekday:weekdays) {
				DayTimeSubItem dayTimeSubItemTmp = new DayTimeSubItem();
				dayTimeSubItemTmp.setStartTimePoint(generateDayTimePonit("1201AM", weekday));
				dayTimeSubItemTmp.setEndTimePoint(generateDayTimePonit("1159PM", weekday));
				dayTimeSubItemList.add(dayTimeSubItemTmp);
			}
		} else if (matcher6.find()) {
			dayTimeSubItem.setStartTimePoint(generateDayTimePonit("1201AM", matcher6.group(1)));
			dayTimeSubItem.setEndTimePoint(generateDayTimePonit("1159PM", matcher6.group(1)));
			dayTimeSubItemList.add(dayTimeSubItem);
		}
		
		return dayTimeSubItemList;
	}
	
	private static DayTimePoint generateDayTimePonit(String timeStr, String weekStr) {
		DayTimePoint dayTimePoint = new DayTimePoint();
		if (timeStr.equals("NOON")) {
			dayTimePoint.setTime("1200");
			dayTimePoint.setAmpm(AmPmEnum.PM);
		} else if (timeStr.equals("MIDNIGHT")) {
			dayTimePoint.setTime("1200");
			dayTimePoint.setAmpm(AmPmEnum.AM);
		} else {
			dayTimePoint.setTime(timeStr.substring(0, timeStr.length()-2));
			dayTimePoint.setAmpm(AmPmEnum.valueOf(timeStr.substring(timeStr.length()-2)));
		}
		dayTimePoint.setWeekday(WeekDayEnum.valueOf(weekStr.substring(0, 3)));
		return dayTimePoint;
	}

	public static void main(String[] args) {
		String s1 = " 02.DAY/TIME\n"
				+ "NO DAY/TIME TRAVEL RESTRICTIONS.";
		String s2 = " 02.DAY/TIME\n"
				+ "OUTBOUND -\n"
				+ "PERMITTED 1201AM SAT THROUGH 1159PM WED\n"
				+ "INBOUND -\n"
				+ "PERMITTED 1201AM SUN THROUGH 1159PM THU";
		String s3 = " 02.DAY/TIME\n"
				+ "PERMITTED FRI/SAT AND SUN ON EACH TRANSPACIFIC SECTOR.";
		String s4 = " 02.DAY/TIME\n"
				+ "ORIGINATING SHA -\n"
				+ "TRAVEL FROM THE FARE ORIGIN IS NOT PERMITTED SAT THROUGH SUN";
		String s5 = "  02.DAY/TIME\nPERMITTED 301PM TO 959AM SUN";
		String s6 = "  02.DAY/TIME\nNOT PERMITTED 1201AM TO NOON MON/TUE/WED";
		String s7 = "  02.DAY/TIME\n"
				+ "PERMITTED MON/TUE/WED/THU/FRI/SAT OR 601AM TO 959AM SUN";
		String s8 = " 02.DAY/TIME\n"
				+ "TRAVEL FROM THE FARE ORIGIN IS PERMITTED\n"
				+ "301PM TO MIDNIGHT THU AND 1201AM TO NOON SAT AND FRI";
		String s9 = " 02.DAY/TIME\nTRAVEL FROM THE FARE ORIGIN IS PERMITTED MON\n"
				+ "OR FRI/SAT/SUN OR 1201AM TO NOON SUN OR 1201PM SUN THROUGH 759AM MON";
		String s10 = " 02.DAY/TIME\n"
				+ "NOT PERMITTED 1201AM TO 759PM SUN OR 1201PM SUN THROUGH\n"
				+ "759AM MON OR 1201PM TUE THROUGH 1159AM THU OR 401PM THU THROUGH 1159PM FRI\n"
				+ "OR 401PM FRI THROUGH 1159PM SAT";
		
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
	}

}
