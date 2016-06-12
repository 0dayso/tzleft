package com.travelzen.farerule.cpecker.pecker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.travelzen.farerule.cpecker.tool.PenaltyTransducer;
import com.travelzen.farerule.jpecker.struct.PenaltyNoshowPack;
import com.travelzen.farerule.rule.PenaltyCancelItem;
import com.travelzen.farerule.rule.PenaltyCancelTypeEnum;
import com.travelzen.farerule.rule.PenaltyChangeItem;
import com.travelzen.farerule.rule.PenaltyChangeTypeEnum;
import com.travelzen.farerule.rule.PenaltyCondition;
import com.travelzen.farerule.rule.PenaltyConditionTypeEnum;

public class Cpecker16Util {

	public static List<PenaltyCancelItem> parsePenaltyCancel(String text, PenaltyCancelItem penaltyCancelItem_noshow) {
		List<PenaltyCancelItem> penaltyCancelItemList = new ArrayList<PenaltyCancelItem>();
		Matcher matcher_nores = Pattern.compile("^\\s*无限制。?\\s*$").matcher(text);
		if (matcher_nores.find()) {
			return penaltyCancelItemList;
		}
		Matcher matcher_permit = Pattern.compile("^\\s*(无罚金|允许)。?\\s*$").matcher(text);
		if (matcher_permit.find()) {
			PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
			penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent("0"));
			penaltyCancelItemList.add(penaltyCancelItem);
			return penaltyCancelItemList;
		}
		
		Matcher matcher1 = Pattern.compile("(?:完全|全部)未使用的?(?:客票)?([\\w\\W]+?)(?:部分[已未]?使用的?(?:客票)?|起飞后)([\\w\\W]+?)$").matcher(text);
		Matcher matcher1_1 = Pattern.compile("部分[已未]?使用的?(?:客票)?([\\w\\W]+?)(?:完全|全部)未使用的?(?:客票)?([\\w\\W]+?)$").matcher(text);
		Matcher matcher1_2 = Pattern.compile("^([\\w\\W]+?)部分[已未]?使用的?(?:客票)?([\\w\\W]+?)$").matcher(text);
		Matcher matcher1_3 = Pattern.compile("(?:完全|全部)未使用的?(?:客票)?([\\w\\W]+?)$").matcher(text);
		
		Matcher matcher2 = Pattern.compile("(?:起飞|旅行)(?:前|后)([\\w\\W]+?)(?:起飞|旅行)后([\\w\\W]+?)(?:。|$)").matcher(text);
		Matcher matcher2_1 = Pattern.compile("(?:起飞|旅行)前([\\w\\W]+?)(?:。|$)").matcher(text);
		Matcher matcher2_2 = Pattern.compile("(?:起飞|旅行)后([\\w\\W]+?)(?:。|$)").matcher(text);
		Matcher matcher2_3 = Pattern.compile("^\\s*允许。\\s*起飞后([\\w\\W]+?)(?:。|$)").matcher(text);
		
		String unused = "", used = "", bd = "", ad = "";
		if (matcher1.find()) {
			unused = parsePenalties(matcher1.group(1));
			used = parsePenalties(matcher1.group(2));
		} else if (matcher2.find()) {
			bd = parsePenalties(matcher2.group(1));
			ad = parsePenalties(matcher2.group(2));
		} else if (matcher1_1.find()) {
			unused = parsePenalties(matcher1_1.group(2));
			used = parsePenalties(matcher1_1.group(1));
		} else if (matcher1_2.find()) {
			unused = parsePenalties(matcher1_2.group(1));
			used = parsePenalties(matcher1_2.group(2));
		} else if (matcher1_3.find()) {
			unused = parsePenalties(matcher1_3.group(1));
		} else if (matcher2_1.find()) {
			bd = parsePenalties(matcher2_1.group(1));
		} else if (matcher2_3.find()) {
			ad = parsePenalties(matcher2_3.group(1));
			if(ad.equals(""))
				bd = ad = "0";
		} else if (matcher2_2.find()) {
			ad = parsePenalties(matcher2_2.group(1));
		} else {
			Matcher matcher_peak = Pattern.compile(
					"旺季([\\w\\W]+?)。").matcher(text);
			Matcher matcher_ex = Pattern.compile(
					"^([\\w\\W]+?)(?:客票丢失|但是自愿延长最长停留时间|。)").matcher(text);
			if (matcher_peak.find())
				bd = ad = parsePenalties(matcher_peak.group(1));
			else if (matcher_ex.find())
				bd = ad = parsePenalties(matcher_ex.group(1));
			else
				bd = ad = parsePenalties(text);
		}
		
		if (!unused.equals("") && unused.equals(used)) {
			PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
			penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(unused));
			penaltyCancelItemList.add(penaltyCancelItem);
		} else {
			if (!unused.equals("")) {
				PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
				penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
				penaltyCancelItem.setUsed(false);
				penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(unused));
				penaltyCancelItemList.add(penaltyCancelItem);
			}
			if (!used.equals("")) {
				PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
				penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
				penaltyCancelItem.setUsed(true);
				penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(used));
				penaltyCancelItemList.add(penaltyCancelItem);
			}
		}
		if (penaltyCancelItemList.size() != 0) {
			if (penaltyCancelItem_noshow != null)
				penaltyCancelItemList.add(penaltyCancelItem_noshow);
			return penaltyCancelItemList;
		}
		
		if (!bd.equals("") && bd.equals(ad)) {
			PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
			penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(bd));
			penaltyCancelItemList.add(penaltyCancelItem);
		} else {
			if (!bd.equals("")) {
				PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
				penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
				penaltyCancelItem.setPenaltyCondition(
						new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.BEFORE_DEPT));
				penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(bd));
				penaltyCancelItemList.add(penaltyCancelItem);
			}
			if (!ad.equals("")) {
				PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
				penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
				penaltyCancelItem.setPenaltyCondition(
						new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.AFTER_DEPT));
				penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(ad));
				penaltyCancelItemList.add(penaltyCancelItem);
			}
		}
		if (penaltyCancelItem_noshow != null)
			penaltyCancelItemList.add(penaltyCancelItem_noshow);
		return penaltyCancelItemList;
	}
	
	public static List<PenaltyChangeItem> parsePenaltyChange(String text, PenaltyChangeItem penaltyChangeItem_noshow) {
		List<PenaltyChangeItem> penaltyChangeItemList = new ArrayList<PenaltyChangeItem>();
		Matcher matcher_nores = Pattern.compile("^\\s*无限制。?\\s*$").matcher(text);
		if (matcher_nores.find()) {
			return penaltyChangeItemList;
		}
		Matcher matcher_permit = Pattern.compile("^\\s*(无罚金|允许)。?\\s*$").matcher(text);
		if (matcher_permit.find()) {
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
			penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent("0"));
			penaltyChangeItemList.add(penaltyChangeItem);
			return penaltyChangeItemList;
		}
		
		Matcher matcher0 = Pattern.compile("(?:起飞|旅行)前([\\w\\W]+?)(?:起飞|旅行)后([\\w\\W]+?)(?:。|$)").matcher(text);
		Matcher matcher0_1 = Pattern.compile("(?:起飞|旅行)前([\\w\\W]+?)(?:。|$)").matcher(text);
		Matcher matcher0_2 = Pattern.compile("(?:起飞|旅行)后([\\w\\W]+?)(?:。|$)").matcher(text);
		Matcher matcher1 = Pattern.compile("(?:改期|日期更改|^\\s*更改)([\\w\\W]+?)(?:。|$)").matcher(text);
		Matcher matcher2 = Pattern.compile("换开([\\w\\W]+?)(?:。|$)").matcher(text);
//		Matcher matcher3 = Pattern.compile("路线更改([\\w\\W]+?)(?:。|$)").matcher(text);
//		Matcher matcher4 = Pattern.compile("签转([\\w\\W]+?)(?:。|$)").matcher(text);
		
		String bd = "", ad = "", reissue = ""; // reroute = "", endorse = "";
		if (matcher0.find()) {
			bd = parsePenalties(matcher0.group(1));
			ad = parsePenalties(matcher0.group(2));
		} else if (matcher0_1.find()) {
			bd = parsePenalties(matcher0_1.group(1));
		} else if (matcher0_2.find()) {
			ad = parsePenalties(matcher0_2.group(1));
		} else if (matcher1.find()) {
			bd = ad = parsePenalties(matcher1.group(1));
		} else {
			Matcher matcher_ex = Pattern.compile(
					"^([\\w\\W]*?)(?:换开|升舱|签转|路线更改|客票丢失|(?:姓名|名字)更改|但是自愿延长最长停留时间|。)").matcher(text);
			// TODO matcher_ex.group(1) 是否为null
			if (matcher_ex.find())
				bd = ad = parsePenalties(matcher_ex.group(1));
			else
				bd = ad = parsePenalties(text);
		}
		if (matcher2.find()) {
			reissue = parsePenalties(matcher2.group(1));
		}
//		if (matcher3.find()) {
//			reroute = parsePenalties(matcher3.group(1));
//		}
//		if (matcher4.find()) {
//			endorse = parsePenalties(matcher4.group(1));
//		}
		
		if (!bd.equals("") && bd.equals(ad)) {
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
			penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(bd));
			penaltyChangeItemList.add(penaltyChangeItem);
		} else {
			if (!bd.equals("")) {
				PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
				penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
				penaltyChangeItem.setPenaltyCondition(
						new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.BEFORE_DEPT));
				penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(bd));
				penaltyChangeItemList.add(penaltyChangeItem);
			}
			if (!ad.equals("")) {
				PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
				penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
				penaltyChangeItem.setPenaltyCondition(
						new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.AFTER_DEPT));
				penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(ad));
				penaltyChangeItemList.add(penaltyChangeItem);
			}
		}
		
		if (penaltyChangeItem_noshow != null)
			penaltyChangeItemList.add(penaltyChangeItem_noshow);
		
		if (!reissue.equals("")) {
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.REISSUE);
			penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(reissue));
			penaltyChangeItemList.add(penaltyChangeItem);
		}
/*		if (!reroute.equals("")) {
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.REROUTE);
			penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(reroute));
			penaltyChangeItemList.add(penaltyChangeItem);
		}
		if (!endorse.equals("")) {
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.ENDORSE);
			penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(endorse));
			penaltyChangeItemList.add(penaltyChangeItem);
		}
*/		return penaltyChangeItemList;
	}
	
	public static PenaltyNoshowPack parsePenaltyNoshow(String text) {
		PenaltyNoshowPack penaltyNoshowPack = new PenaltyNoshowPack();
		Matcher matcher_nores = Pattern.compile("^\\s*无限制。?\\s*$").matcher(text);
		if (matcher_nores.find()) {
			return penaltyNoshowPack;
		}
		Matcher matcher_0 = Pattern.compile("^\\s*(无罚金|允许)。?\\s*$").matcher(text);
		if (matcher_0.find()) {
			PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.NOSHOW);
			penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent("0"));
			penaltyNoshowPack.setCancelNoshow(penaltyCancelItem);
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.NOSHOW);
			penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent("0"));
			penaltyNoshowPack.setChangeNoshow(penaltyChangeItem);
			return penaltyNoshowPack;
		}
		
		Pattern pattern0 = Pattern.compile("零(?:误机|手续)费");
		Pattern pattern1 = Pattern.compile("不(?:允许|可)");
		Pattern pattern2 = Pattern.compile("误机费 ?(\\d+[\\w\\W]+?)(?:，|。|或)");
		Pattern pattern3 = Pattern.compile("手续费 ?(\\d+[\\w\\W]+?)(?:，|。|或)");
		
		Matcher matcher1 = Pattern.compile("退票([\\w\\W]+?)(?=更改|改期|$)").matcher(text);
		Matcher matcher2 = Pattern.compile("(?:更改|改期)([\\w\\W]+?)(?=退票|$)").matcher(text);
		Matcher matcher3 = Pattern.compile("不(?:可|允许)退票").matcher(text);
		Matcher matcher4 = Pattern.compile("不(?:可|允许)更改").matcher(text);
		Matcher matcher5 = Pattern.compile("允许退票").matcher(text);
		Matcher matcher6 = Pattern.compile("允许更改").matcher(text);
		
		String cancel = "", change = "";
		boolean flag = false;
		if (matcher3.find()) {
			cancel = "-1";
			flag = true;
		} else if (matcher5.find()) {
			cancel = "0";
			flag = true;
		} else if (matcher1.find()) {
			List<String> penaltyList = new ArrayList<String>();
			String tmpText = matcher1.group(1);
			if (matcher1.find())
				tmpText += matcher1.group(1);
			Matcher matcher1_0 = pattern0.matcher(tmpText);
			Matcher matcher1_1 = pattern1.matcher(tmpText);
			Matcher matcher1_2 = pattern2.matcher(tmpText);
			Matcher matcher1_3 = pattern3.matcher(tmpText);
			if (matcher1_0.find()) {
				penaltyList.add("0");
			} else if (matcher1_1.find()) {
				penaltyList.add("-1");
			} else if (matcher1_2.find()) {
				penaltyList.add(matcher1_2.group(1));
			} else if (matcher1_3.find()) {
				penaltyList.add(matcher1_3.group(1));
			}
			if (penaltyList.size() == 0) {
				cancel = parsePenalties(tmpText);
			} else {
				cancel = sortPenalties(penaltyList);
			}
			flag = true;
		}
		if (matcher4.find()) {
			change = "-1";
			flag = true;
		} else if (matcher6.find()) {
			change = "0";
			flag = true;
		} else if (matcher2.find()) {
			List<String> penaltyList = new ArrayList<String>();
			String tmpText = matcher2.group(1);
			if (matcher2.find())
				tmpText += matcher2.group(1);
			Matcher matcher2_0 = pattern0.matcher(tmpText);
			Matcher matcher2_1 = pattern1.matcher(tmpText);
			Matcher matcher2_2 = pattern2.matcher(tmpText);
			Matcher matcher2_3 = pattern3.matcher(tmpText);
			if (matcher2_0.find()) {
				penaltyList.add("0");
			} else if (matcher2_1.find()) {
				penaltyList.add("-1");
			} else if (matcher2_2.find()) {
				penaltyList.add(matcher2_2.group(1));
			} else if (matcher2_3.find()) {
				penaltyList.add(matcher2_3.group(1));
			}
			if (penaltyList.size() == 0) {
				change = parsePenalties(tmpText);
			} else {
				change = sortPenalties(penaltyList);
			}
			flag = true;
		}	
		if (flag == false) {
			List<String> penaltyList = new ArrayList<String>();
			Matcher matcher0_0 = pattern0.matcher(text);
			Matcher matcher0_1 = pattern1.matcher(text);
			Matcher matcher0_2 = pattern2.matcher(text);
			Matcher matcher0_3 = pattern3.matcher(text);
			if (matcher0_0.find()) {
				penaltyList.add("0");
			} else if (matcher0_1.find()) {
				penaltyList.add("-1");
			} else if (matcher0_2.find()) {
				penaltyList.add(matcher0_2.group(1));
			} else if (matcher0_3.find()) {
				penaltyList.add(matcher0_3.group(1));
			}
			if (penaltyList.size() == 0) {
				cancel = change = parsePenalties(text);
			} else {
				cancel = change = sortPenalties(penaltyList);
			}
		}
		
		if (!cancel.equals("")) {
			PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.NOSHOW);
			penaltyCancelItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(cancel));
			penaltyNoshowPack.setCancelNoshow(penaltyCancelItem);
		}
		if (!change.equals("")) {
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.NOSHOW);
			penaltyChangeItem.setPenaltyContent(PenaltyTransducer.parsePenaltyContent(change));
			penaltyNoshowPack.setChangeNoshow(penaltyChangeItem);
		}
		return penaltyNoshowPack;
	}
	
	public static String parsePenalties(String penalty) {
		String result = "";
		penalty = penalty.replaceAll(" |\n|\\.0+", "");
		
		Matcher matcher1 = Pattern.compile("不允许|不得[\\u4e00-\\u9fa5]{2}|不许退").matcher(penalty);
		Matcher matcher2 = Pattern.compile("(\\d+)%").matcher(penalty);
		Matcher matcher3 = Pattern.compile(
			"(?:收取(?:[\\u4e00-\\u9fa5]{2,3})?|费)(\\d+[\\u4e00-\\u9fa5]{1,13}?(?:/\\d+[\\u4e00-\\u9fa5]{1,13}?)*)(?:和|。|，|/|或|$)").matcher(penalty);
		Matcher matcher4 = Pattern.compile("无罚金|允许|免费").matcher(penalty);
		// ADT CHD different penalties
		Matcher matcher5_1 = Pattern.compile("成人票?收取票价的(\\d+%)").matcher(penalty);
		Matcher matcher5_2 = Pattern.compile("儿童票?收取票价的(\\d+%)").matcher(penalty);
		Matcher matcher6_1 = Pattern.compile("成人票?收取[\\u4e00-\\u9fa5]{2}费(\\d+[\\u4e00-\\u9fa5]{1,13}?(?:/\\d+[\\u4e00-\\u9fa5]{1,13}?)*)(?:和|。|，|/|或|$)").matcher(penalty);
		Matcher matcher6_2 = Pattern.compile("儿童票?收取[\\u4e00-\\u9fa5]{2}费(\\d+[\\u4e00-\\u9fa5]{1,13}?(?:/\\d+[\\u4e00-\\u9fa5]{1,13}?)*)(?:和|。|，|/|或|$)").matcher(penalty);
		
		List<String> penaltyList = new ArrayList<String>();
		if (matcher5_1.find() && matcher5_2.find()) {
			result = matcher5_1.group(1);
//			result = "ADT:" + matcher5_1.group(1) + ",CHD:" + matcher5_2.group(1);
			penaltyList.add(result);
		}
		if (matcher6_1.find() && matcher6_2.find()) {
			result = matcher6_1.group(1);
//			result = "ADT:" + matcher6_1.group(1) + ",CHD:" + matcher6_2.group(1);
			penaltyList.add(result);
		}
		while (matcher1.find()) {
			result = "-1";
			penaltyList.add(result);
		}
		while (matcher2.find()) {
			result = matcher2.group(1) + "%";
			penaltyList.add(result);
		}
		while (matcher3.find()) {
			result = matcher3.group(1);
			penaltyList.add(result);
		}
		while (matcher4.find()) {
			result = "0";
			penaltyList.add(result);
		}
		return sortPenalties(penaltyList);
	}
	
	private static String sortPenalties(List<String> penaltyList) {
		String finalPenalty = "";
		int penaltyTag = Integer.MIN_VALUE;
		Pattern pattern = Pattern.compile("-?\\d+");		
		for (String penalty:penaltyList) {
			Matcher matcher = pattern.matcher(penalty);
			if (matcher.find()) {
				int tag = Integer.parseInt(matcher.group(0));
				if (tag == -1)
					tag = Integer.MAX_VALUE;
				if (tag > penaltyTag) {
					finalPenalty = penalty;
					penaltyTag = tag;
				}
			}
		}
		return finalPenalty;
	}

}
