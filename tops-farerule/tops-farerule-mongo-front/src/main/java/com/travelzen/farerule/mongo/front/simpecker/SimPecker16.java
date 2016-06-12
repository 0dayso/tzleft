package com.travelzen.farerule.mongo.front.simpecker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.travelzen.farerule.Penalties;
import com.travelzen.farerule.condition.RuleCondition;
import com.travelzen.farerule.jpecker.struct.RuleTextSegment;
import com.travelzen.farerule.mongo.front.simpecker.tool.ConditionTransducer;
import com.travelzen.farerule.rule.PenaltiesItem;
import com.travelzen.farerule.rule.PenaltyCancelItem;
import com.travelzen.farerule.rule.PenaltyCancelTypeEnum;
import com.travelzen.farerule.rule.PenaltyChangeItem;
import com.travelzen.farerule.rule.PenaltyChangeTypeEnum;
import com.travelzen.farerule.rule.PenaltyCondition;
import com.travelzen.farerule.rule.PenaltyConditionTypeEnum;
import com.travelzen.farerule.rule.PenaltyContent;
import com.travelzen.farerule.rule.PenaltyFee;
import com.travelzen.farerule.rule.PenaltyRatio;
import com.travelzen.farerule.rule.PenaltyTypeEnum;

public class SimPecker16 extends SimPeckerBase {

	private Penalties penalties;
	private List<PenaltiesItem> penaltiesItemList;
	
	public SimPecker16() {
		penalties = new Penalties();
		penaltiesItemList = new ArrayList<PenaltiesItem>();
	}
	
	public Penalties getPenalties() {
		return penalties;
	}
	
	public void process(String ruleText) {
		if (ruleText.contains("无限制")) {
			penalties = null;
			return;
		}
		
		if (ruleText.contains("请咨询航空公司")) {
			return;
		}
		
		splitOrigins(ruleText);
		splitDates(ruleTextBlockList);
		
		for (RuleTextSegment ruleTextSegment:ruleTextSegmentList) {
			PenaltiesItem penaltiesItem = new PenaltiesItem();
			String text = ruleTextSegment.getText();
			Matcher matcher = Pattern.compile(
					"退票：([\\w\\W]+)改签：([\\w\\W]+)$").matcher(text);
			if (matcher.find()) {
				penaltiesItem.setPenaltyCancelItemList(parseCancel(matcher.group(1)));
				penaltiesItem.setPenaltyChangeItemList(parseChange(matcher.group(2)));
			} else {
				continue;
			}
			
			RuleCondition ruleCondition = new RuleCondition();
			if (!ruleTextSegment.getOrigin().equals("")) {
				ruleCondition.setOriginCondition(
						ConditionTransducer.parseOriginSim(ruleTextSegment.getOrigin()));
			}
			if (!ruleTextSegment.getSalesDate().equals("")) {
				ruleCondition.setSalesDateCondition(
						ConditionTransducer.parseSalesDateSim(ruleTextSegment.getSalesDate()));
			}
			if (!ruleTextSegment.getTravelDate().equals("")) {
				ruleCondition.setTravelDateCondition(
						ConditionTransducer.parseTravelDateSim(ruleTextSegment.getTravelDate()));
			}
			penaltiesItem.setRuleCondition(ruleCondition);
			
			penaltiesItemList.add(penaltiesItem);
		}
		
		penalties.setPenaltiesItemList(penaltiesItemList);
	}
	
	private List<PenaltyCancelItem> parseCancel(String text) {
		List<PenaltyCancelItem> penaltyCancelItemList = new ArrayList<PenaltyCancelItem>();
		String[] lines = text.split("。");
		for (String line:lines) {
			PenaltyCancelItem penaltyCancelItem = parseCancelItem(line);
			if (penaltyCancelItem != null)
				penaltyCancelItemList.add(penaltyCancelItem);
		}
		return penaltyCancelItemList;
	}
	
	private List<PenaltyChangeItem> parseChange(String text) {
		List<PenaltyChangeItem> penaltyChangeItemList = new ArrayList<PenaltyChangeItem>();
		String[] lines = text.split("。");
		for (String line:lines) {
			PenaltyChangeItem penaltyChangeItem = parseChangeItem(line);
			if (penaltyChangeItem != null)
				penaltyChangeItemList.add(penaltyChangeItem);
		}
		return penaltyChangeItemList;
	}
	
	private PenaltyCancelItem parseCancelItem(String text) {
		PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
		if (text.contains("退票")) {
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
			if (text.contains("完全未使用机票")) {
				penaltyCancelItem.setUsed(false);
			} else if (text.contains("部分已使用机票")) {
				penaltyCancelItem.setUsed(true);
			}
			if (text.contains("起飞前")) {
				penaltyCancelItem.setPenaltyCondition(
						new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.BEFORE_DEPT));
			} else if (text.contains("起飞后")) {
				penaltyCancelItem.setPenaltyCondition(
						new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.AFTER_DEPT));
			}
		} else if (text.contains("误机")) {
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.NOSHOW);
		} else 
			return null;
		PenaltyContent penaltyContent = parsePenaltyContent(text);
		penaltyCancelItem.setPenaltyContent(penaltyContent);
		return penaltyCancelItem;
	}
	
	private PenaltyChangeItem parseChangeItem(String text) {
		PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
		if (text.contains("改期")) {
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
			if (text.contains("完全未使用机票")) {
				penaltyChangeItem.setUsed(false);
			} else if (text.contains("部分已使用机票")) {
				penaltyChangeItem.setUsed(true);
			}
			if (text.contains("起飞前")) {
				penaltyChangeItem.setPenaltyCondition(
						new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.BEFORE_DEPT));
			} else if (text.contains("起飞后")) {
				penaltyChangeItem.setPenaltyCondition(
						new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.AFTER_DEPT));
			}
		} else if (text.contains("误机")) {
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.NOSHOW);
		} else if (text.contains("换开")) {
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.REISSUE);
//		} else if (text.contains("更改路线")) {
//			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.REROUTE);
//		} else if (text.contains("签转")) {
//			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.ENDORSE);
		} else 
			return null;
		PenaltyContent penaltyContent = parsePenaltyContent(text);
		penaltyChangeItem.setPenaltyContent(penaltyContent);
		return penaltyChangeItem;
	}
	
	public PenaltyContent testPenaltyContent(String text) {
		return parsePenaltyContent(text);
	}
	
	private PenaltyContent parsePenaltyContent(String text) {
		text = text.replaceAll("\\.0+", "");
		PenaltyContent penaltyContent = new PenaltyContent();
		if (text.contains("不允许"))
			penaltyContent.setPenaltyType(PenaltyTypeEnum.NOT_PERMIT);
		else if (text.contains("允许")|| text.contains("不加收"))
			penaltyContent.setPenaltyType(PenaltyTypeEnum.PERMIT);
		else {
			Matcher matcher_ratio = Pattern.compile("(\\d+)%").matcher(text);
			Matcher matcher_fee = Pattern.compile(
					"费(\\d+[\\u4e00-\\u9fa5]+?(?:/\\d+[\\u4e00-\\u9fa5]+?)*)(?=$|或)").matcher(text);
			boolean hasRatio = matcher_ratio.find();
			boolean hasFee = matcher_fee.find();
			if (hasRatio && hasFee) {
				penaltyContent.setPenaltyType(PenaltyTypeEnum.HIGHER);
				penaltyContent.setPenaltyRatio(new PenaltyRatio().setRatio(Double.parseDouble(matcher_ratio.group(1))));
				penaltyContent.setPenaltyFeeList(parsePenaltyFeeList(matcher_fee.group(1)));
			} else if (hasRatio) {
				penaltyContent.setPenaltyType(PenaltyTypeEnum.RATIO);
				penaltyContent.setPenaltyRatio(new PenaltyRatio().setRatio(Double.parseDouble(matcher_ratio.group(1))));
			} else if (hasFee) {
				penaltyContent.setPenaltyType(PenaltyTypeEnum.FEE);
				penaltyContent.setPenaltyFeeList(parsePenaltyFeeList(matcher_fee.group(1)));
			}
		}
		return penaltyContent;
	}

	private List<PenaltyFee> parsePenaltyFeeList(String fees) {
		List<PenaltyFee> penaltyFeeList = new ArrayList<PenaltyFee>();
		Matcher matcher = Pattern.compile("(\\d+)([\\u4e00-\\u9fa5]+)").matcher(fees);
		while (matcher.find()) {
			PenaltyFee penaltyFee = new PenaltyFee();
			penaltyFee.setCurrency(matcher.group(2));
			penaltyFee.setAmount(Double.parseDouble(matcher.group(1)));
			penaltyFeeList.add(penaltyFee);
		}
		return penaltyFeeList;
	}
}
