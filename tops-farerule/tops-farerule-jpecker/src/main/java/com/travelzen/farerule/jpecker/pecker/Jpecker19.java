package com.travelzen.farerule.jpecker.pecker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.farerule.ChildInfantDiscount;
import com.travelzen.farerule.jpecker.consts.PassengerConst;
import com.travelzen.farerule.rule.ChildInfantDiscountItem;
import com.travelzen.farerule.rule.Judge;
import com.travelzen.farerule.rule.PassengerType;

public class Jpecker19 extends JpeckerBase {
	
	private static final Logger logger = LoggerFactory.getLogger(Jpecker19.class);
	
	public static ChildInfantDiscount parse(String ruleText) {
		List<ChildInfantDiscountItem> childInfantDiscountItemList = null;
		
		ruleText = ruleText.replaceAll("\n", " ").replaceAll("\\.", " ");
		
		childInfantDiscountItemList = parseChildInfantDiscountItemList(ruleText);
		
		if (childInfantDiscountItemList.size() == 0) {
			logger.info("无有效限制。");
			return null;
		}
		
		ChildInfantDiscount childInfantDiscount = new ChildInfantDiscount();
		childInfantDiscount.setChildInfantDiscountItemList(childInfantDiscountItemList);
		
		return childInfantDiscount;
	}

	// ACCOMPANIED CHILD 2-11 - NO DISCOUNT.
	// ACCOMPANIED CHILD 2-11 - CHARGE 75 PERCENT OF THE FARE.
	// OR - UNACCOMPANIED CHILD 5-11 - CHARGE 100 PERCENT OF THE FARE.
	// OR - INFANT UNDER 2 WITHOUT A SEAT - CHARGE 10 PERCENT OF THE FARE. 
	// OR - INFANT UNDER 2 WITH A SEAT - CHARGE 75 PERCENT OF THE FARE.
	private static List<ChildInfantDiscountItem> parseChildInfantDiscountItemList(String text) {
		List<ChildInfantDiscountItem> childInfantDiscountItemList = new ArrayList<ChildInfantDiscountItem>();
		
		String[] segments = text.split("\\b(NOTE|OR)\\s*-");		
		for (String segment:segments) {
			ChildInfantDiscountItem childInfantDiscountItem = parseChildInfantDiscountItem(segment);
			if (childInfantDiscountItem != null)
				childInfantDiscountItemList.add(childInfantDiscountItem);
		}
		
		return childInfantDiscountItemList;
	}
	
	private static ChildInfantDiscountItem parseChildInfantDiscountItem(String text) {
		ChildInfantDiscountItem childInfantDiscountItem = new ChildInfantDiscountItem();
		
		// Infant
		Matcher matcher_inf1 = Pattern.compile(
				PassengerConst.INF1 + "[-\\s]*" + PassengerConst.DISCOUNT).matcher(text);
		Matcher matcher_inf2 = Pattern.compile(
				PassengerConst.INF2 + "[-\\s]*" + PassengerConst.DISCOUNT).matcher(text);
		Matcher matcher_inf3 = Pattern.compile(
				PassengerConst.INF3 + "[-\\s]*" + PassengerConst.DISCOUNT).matcher(text);
		// Child
		Matcher matcher_chd1 = Pattern.compile(
				PassengerConst.CHD1 + "[-\\s]*" + PassengerConst.DISCOUNT).matcher(text);
		Matcher matcher_chd2 = Pattern.compile(
				PassengerConst.CHD2 + "[-\\s]*" + PassengerConst.DISCOUNT).matcher(text);
		Matcher matcher_chd3 = Pattern.compile(
				PassengerConst.CHD3 + "[-\\s]*" + PassengerConst.DISCOUNT).matcher(text);
		
		if (matcher_inf3.find()) {
			childInfantDiscountItem.setPassengerType(PassengerType.INF);
			childInfantDiscountItem.setMinAge(Integer.parseInt(matcher_inf3.group(2)));
			childInfantDiscountItem.setMaxAge(Integer.parseInt(matcher_inf3.group(3)));
			if (matcher_inf3.group(1) != null) {
				childInfantDiscountItem.setAccompanied(isAccompanied(matcher_inf3.group(1)));
			}
			if (matcher_inf3.group(4) != null) {
				childInfantDiscountItem.setHasSeat(hasSeat(matcher_inf3.group(4)));
			}
			childInfantDiscountItem.setDiscount(parseDiscount(matcher_inf3.group(5)));
		} else if (matcher_inf2.find()) {
			childInfantDiscountItem.setPassengerType(PassengerType.INF);
			childInfantDiscountItem.setMaxAge(Integer.parseInt(matcher_inf2.group(2)));
			if (matcher_inf2.group(1) != null) {
				childInfantDiscountItem.setAccompanied(isAccompanied(matcher_inf2.group(1)));
			}
			if (matcher_inf2.group(3) != null) {
				childInfantDiscountItem.setHasSeat(hasSeat(matcher_inf2.group(3)));
			}
			childInfantDiscountItem.setDiscount(parseDiscount(matcher_inf2.group(4)));
		} else if (matcher_inf1.find()) {
			childInfantDiscountItem.setPassengerType(PassengerType.INF);
			if (matcher_inf1.group(1) != null) {
				childInfantDiscountItem.setAccompanied(isAccompanied(matcher_inf1.group(1)));
			}
			if (matcher_inf1.group(2) != null) {
				childInfantDiscountItem.setHasSeat(hasSeat(matcher_inf1.group(2)));
			}
			childInfantDiscountItem.setDiscount(parseDiscount(matcher_inf1.group(3)));
		}
			
		if (matcher_chd3.find()) {
			childInfantDiscountItem.setPassengerType(PassengerType.CHD);
			childInfantDiscountItem.setMinAge(Integer.parseInt(matcher_chd3.group(2)));
			childInfantDiscountItem.setMaxAge(Integer.parseInt(matcher_chd3.group(3)));
			if (matcher_chd3.group(1) != null) {
				childInfantDiscountItem.setAccompanied(isAccompanied(matcher_chd3.group(1)));
			}
			childInfantDiscountItem.setDiscount(parseDiscount(matcher_chd3.group(4)));
		} else if (matcher_chd2.find()) {
			childInfantDiscountItem.setPassengerType(PassengerType.CHD);
			childInfantDiscountItem.setMaxAge(Integer.parseInt(matcher_chd2.group(2)));
			if (matcher_chd2.group(1) != null) {
				childInfantDiscountItem.setAccompanied(isAccompanied(matcher_chd2.group(1)));
			}
			childInfantDiscountItem.setDiscount(parseDiscount(matcher_chd2.group(3)));
		} else if (matcher_chd1.find()) {
			childInfantDiscountItem.setPassengerType(PassengerType.CHD);
			if (matcher_chd1.group(1) != null) {
				childInfantDiscountItem.setAccompanied(isAccompanied(matcher_chd1.group(1)));
			}
			if (matcher_chd1.group(2) != null) {
				childInfantDiscountItem.setAccompanied(Judge.NEGATIVE);
			}
			childInfantDiscountItem.setDiscount(parseDiscount(matcher_chd1.group(3)));
		}
		
		return childInfantDiscountItem;
	}
	
	private static Judge isAccompanied(String text) {
		return text.contains("UNACCOMPANIED")?Judge.NEGATIVE:Judge.NEGATIVE;
	}
	
	private static Judge hasSeat(String text) {
		return text.contains("WITH A SEAT")?Judge.POSITIVE:Judge.NEGATIVE;
	}
	
	private static int parseDiscount(String text) {
		int discount = 0;
		Matcher matcher1 = Pattern.compile("CHARGE\\s*(\\d+)").matcher(text);
		Matcher matcher2 = Pattern.compile("NO\\s*DISCOUNT").matcher(text);
		Matcher matcher3 = Pattern.compile("NOT\\s*PERMITTED").matcher(text);
		if (matcher1.find()) {
			discount = Integer.parseInt((String)matcher1.group(1));
		} else if (matcher2.find()) {
			discount = 100;
		} else if (matcher3.find()) {
			discount = -1;
		}
		return discount;
	}
	
	public static void main(String[] args) {
		String s1 = " 19.CHILDREN/INFANT DISCOUNTS\n"
				+ "ACCOMPANIED CHILD 2-11 - NO DISCOUNT.\n"
				+ "OR - INFANT UNDER 2 WITHOUT A SEAT - CHARGE 20 PERCENT OF THE FARE.\n"
				+ "OR - INFANT UNDER 2 WITH A SEAT - CHARGE 75 PERCENT OF THE FARE.";
		System.out.println(Jpecker19.parse(s1));
		
		String s2 = " 19.CHILDREN/INFANT DISCOUNTS\n"
				+ "ACCOMPANIED CHILD 2-11 - CHARGE 75 PERCENT OF THE FARE.\n"
				+ "OR - UNACCOMPANIED CHILD 5-11 - CHARGE 100 PERCENT OF THE FARE.\n"
				+ "OR - INFANT UNDER 2 WITHOUT A SEAT - CHARGE 10 PERCENT OF THE FARE. \n"
				+ "OR - INFANT UNDER 2 WITH A SEAT - CHARGE 75 PERCENT OF THE FARE.";
		System.out.println(Jpecker19.parse(s2));
	}
	
}
