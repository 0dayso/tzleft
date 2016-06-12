package com.travelzen.farerule.translator;

import java.util.ArrayList;
import java.util.List;

import com.travelzen.farerule.Penalties;
import com.travelzen.farerule.condition.RuleCondition;
import com.travelzen.farerule.rule.PenaltiesItem;
import com.travelzen.farerule.rule.PenaltiesRemark;
import com.travelzen.farerule.rule.PenaltyAdditionEnum;
import com.travelzen.farerule.rule.PenaltyCancelItem;
import com.travelzen.farerule.rule.PenaltyCancelTypeEnum;
import com.travelzen.farerule.rule.PenaltyChangeItem;
import com.travelzen.farerule.rule.PenaltyChangeTypeEnum;
import com.travelzen.farerule.rule.PenaltyConditionTypeEnum;
import com.travelzen.farerule.rule.PenaltyContent;
import com.travelzen.farerule.rule.PenaltyFee;
import com.travelzen.farerule.rule.PenaltyTypeEnum;
import com.travelzen.farerule.translator.tool.PenaltyTransducer;

public class RuleTranslator16 extends RuleTranslatorBase {

	public static String translate(Penalties penalties) {
		if (penalties == null)
			return "无限制。";
		List<PenaltiesItem> penaltiesItemList = penalties.getPenaltiesItemList();
		if ((penaltiesItemList == null || penaltiesItemList.size() == 0) && 
				(penalties.getPenaltiesCancelRemarkList() == null || penalties.getPenaltiesCancelRemarkList().size() == 0) && 
				(penalties.getPenaltiesChangeRemarkList() == null || penalties.getPenaltiesChangeRemarkList().size() == 0))
			return "请咨询航空公司。";
		StringBuilder sb = new StringBuilder();
		if (penaltiesItemList != null && penaltiesItemList.size() != 0) {
			for (PenaltiesItem penaltiesItem:penaltiesItemList) {
				sb.append(translateAnItem(penaltiesItem));
			}
		}
		if (penalties.getPenaltiesCancelRemarkList() != null && 
				penalties.getPenaltiesCancelRemarkList().size() != 0) {
			sb.append("退票备注\n");
			for (PenaltiesRemark penaltiesRemark:penalties.getPenaltiesCancelRemarkList()) {
				sb.append(penaltiesRemark.getRemark()).append("\n");
			}
		}
		if (penalties.getPenaltiesChangeRemarkList() != null && 
				penalties.getPenaltiesChangeRemarkList().size() != 0) {
			sb.append("改签备注\n");
			for (PenaltiesRemark penaltiesRemark:penalties.getPenaltiesChangeRemarkList()) {
				sb.append(penaltiesRemark.getRemark()).append("\n");
			}
		}
		if (sb.toString().endsWith("\n"))
			sb.replace(sb.length()-1, sb.length(), "");
		return sb.toString();
	}
	
	private static StringBuilder translateAnItem(PenaltiesItem penaltiesItem) {
		StringBuilder sb = new StringBuilder();
		RuleCondition ruleCondition = penaltiesItem.getRuleCondition();
		if (ruleCondition != null) {
			sb.append(translateRuleCondition(ruleCondition));
		}
		List<PenaltyCancelItem> penaltyCancelItemList = penaltiesItem.getPenaltyCancelItemList();
		if (penaltyCancelItemList != null && penaltyCancelItemList.size() != 0) {
			sb.append("退票：");
			for (PenaltyCancelItem penaltyCancelItem:penaltyCancelItemList) {
				if (penaltyCancelItem.getPenaltyCancelType() == PenaltyCancelTypeEnum.CANCEL) {
					if (penaltyCancelItem.isSetUsed())
						sb.append(PenaltyTransducer.transIfUsed(penaltyCancelItem.isUsed()));
					if (penaltyCancelItem.getPenaltyCondition() != null) {
						if (penaltyCancelItem.getPenaltyCondition().getPenaltyConditionType() == 
								PenaltyConditionTypeEnum.BEFORE_DEPT) {
							sb.append("起飞前");
						} else if (penaltyCancelItem.getPenaltyCondition().getPenaltyConditionType() == 
								PenaltyConditionTypeEnum.AFTER_DEPT) {
							sb.append("起飞后");
						} else if (penaltyCancelItem.getPenaltyCondition().getPenaltyConditionType() == 
								PenaltyConditionTypeEnum.BEFORE_DEPT_TIME) {
							sb.append("起飞前" + penaltyCancelItem.getPenaltyCondition().getBeforeDeptHour() + "小时");
						}
					}
					sb.append(PenaltyTransducer.cancelToString(penaltyCancelItem.getPenaltyContent()));
				}
				if (penaltyCancelItem.getPenaltyCancelType() == PenaltyCancelTypeEnum.NOSHOW) {
					if (penaltyCancelItem.isSetUsed())
						sb.append(PenaltyTransducer.transIfUsed(penaltyCancelItem.isUsed()));
					if (penaltyCancelItem.getPenaltyCondition() != null) {
						sb.append("误机(");
						if (penaltyCancelItem.getPenaltyCondition().getPenaltyConditionType() == 
								PenaltyConditionTypeEnum.BEFORE_DEPT) {
							sb.append("起飞前");
						} else if (penaltyCancelItem.getPenaltyCondition().getPenaltyConditionType() == 
								PenaltyConditionTypeEnum.AFTER_DEPT) {
							sb.append("起飞后");
						} else if (penaltyCancelItem.getPenaltyCondition().getPenaltyConditionType() == 
								PenaltyConditionTypeEnum.BEFORE_DEPT_TIME) {
							sb.append("起飞前" + penaltyCancelItem.getPenaltyCondition().getBeforeDeptHour() + "小时");
						}
						sb.append(")情况下，");
					} else {
						sb.append("误机情况下，");
					}
					sb.append(PenaltyTransducer.cancelNoshowToString(penaltyCancelItem.getPenaltyContent()));
				}
			}
			sb.append("\n");
		}
		List<PenaltyChangeItem> penaltyChangeItemList = penaltiesItem.getPenaltyChangeItemList();
		if (penaltyChangeItemList != null && penaltyChangeItemList.size() != 0) {
			sb.append("改签：");
			for (PenaltyChangeItem penaltyChangeItem:penaltyChangeItemList) {
				if (penaltyChangeItem.getPenaltyChangeType() == PenaltyChangeTypeEnum.CHANGE) {
					if (penaltyChangeItem.isSetUsed())
						sb.append(PenaltyTransducer.transIfUsed(penaltyChangeItem.isUsed()));
					if (penaltyChangeItem.getPenaltyCondition() != null) {
						if (penaltyChangeItem.getPenaltyCondition().getPenaltyConditionType() == 
								PenaltyConditionTypeEnum.BEFORE_DEPT) {
							sb.append("起飞前");
						} else if (penaltyChangeItem.getPenaltyCondition().getPenaltyConditionType() == 
								PenaltyConditionTypeEnum.AFTER_DEPT) {
							sb.append("起飞后");
						} else if (penaltyChangeItem.getPenaltyCondition().getPenaltyConditionType() == 
								PenaltyConditionTypeEnum.BEFORE_DEPT_TIME) {
							sb.append("起飞前" + penaltyChangeItem.getPenaltyCondition().getBeforeDeptHour() + "小时");
						}
					}
					sb.append(PenaltyTransducer.changeToString(penaltyChangeItem.getPenaltyContent()));
				}
				if (penaltyChangeItem.getPenaltyChangeType() == PenaltyChangeTypeEnum.NOSHOW) {
					if (penaltyChangeItem.isSetUsed())
						sb.append(PenaltyTransducer.transIfUsed(penaltyChangeItem.isUsed()));
					if (penaltyChangeItem.getPenaltyCondition() != null) {
						sb.append("误机(");
						if (penaltyChangeItem.getPenaltyCondition().getPenaltyConditionType() == 
								PenaltyConditionTypeEnum.BEFORE_DEPT) {
							sb.append("起飞前");
						} else if (penaltyChangeItem.getPenaltyCondition().getPenaltyConditionType() == 
								PenaltyConditionTypeEnum.AFTER_DEPT) {
							sb.append("起飞后");
						} else if (penaltyChangeItem.getPenaltyCondition().getPenaltyConditionType() == 
								PenaltyConditionTypeEnum.BEFORE_DEPT_TIME) {
							sb.append("起飞前" + penaltyChangeItem.getPenaltyCondition().getBeforeDeptHour() + "小时");
						}
						sb.append(")情况下，");
					} else {
						sb.append("误机情况下，");
					}
					sb.append(PenaltyTransducer.changeNoshowToString(penaltyChangeItem.getPenaltyContent()));
				}
				if (penaltyChangeItem.getPenaltyChangeType() == PenaltyChangeTypeEnum.REISSUE) {
					if (penaltyChangeItem.isSetUsed())
						sb.append(PenaltyTransducer.transIfUsed(penaltyChangeItem.isUsed()));
					if (penaltyChangeItem.getPenaltyCondition() != null) {
						if (penaltyChangeItem.getPenaltyCondition().getPenaltyConditionType() == 
								PenaltyConditionTypeEnum.BEFORE_DEPT) {
							sb.append("起飞前");
						} else if (penaltyChangeItem.getPenaltyCondition().getPenaltyConditionType() == 
								PenaltyConditionTypeEnum.AFTER_DEPT) {
							sb.append("起飞后");
						} else if (penaltyChangeItem.getPenaltyCondition().getPenaltyConditionType() == 
								PenaltyConditionTypeEnum.BEFORE_DEPT_TIME) {
							sb.append("起飞前" + penaltyChangeItem.getPenaltyCondition().getBeforeDeptHour() + "小时");
						}
					}
					sb.append(PenaltyTransducer.reissueToString(penaltyChangeItem.getPenaltyContent()));
				}
//				if (penaltyChangeItem.getPenaltyChangeType() == PenaltyChangeTypeEnum.REROUTE) {
//					sb.append(PenaltyTransducer.rerouteToString(penaltyChangeItem.getPenaltyContent()));
//				}
//				if (penaltyChangeItem.getPenaltyChangeType() == PenaltyChangeTypeEnum.ENDORSE) {
//					sb.append(PenaltyTransducer.endorseToString(penaltyChangeItem.getPenaltyContent()));
//				}
			}
			sb.append("\n");
		}
		List<PenaltyAdditionEnum> penaltyAdditionList = penaltiesItem.getPenaltyAdditionList();
		if (penaltyAdditionList != null && penaltyAdditionList.size() != 0) {
			if (penaltyChangeItemList == null || penaltyChangeItemList.size() == 0) {
				sb.append("改签：");
			} else {
				sb.replace(sb.length()-1, sb.length(), "");
			}
			for (PenaltyAdditionEnum penaltyAddition:penaltyAdditionList) {
				sb.append(PenaltyTransducer.penaltyAdditionToString(penaltyAddition));
			}
			sb.append("\n");
		}
		return sb;
	}
	
	public static void main(String[] args) {
		List<PenaltiesItem> l1 = new ArrayList<PenaltiesItem>();
		PenaltiesItem p = new PenaltiesItem();
		List<PenaltyCancelItem> ll1 = new ArrayList<PenaltyCancelItem>();
		ll1.add(new PenaltyCancelItem().setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL).setPenaltyContent(new PenaltyContent().setPenaltyType(PenaltyTypeEnum.NOT_PERMIT).setContainDiff(true)));
		p.setPenaltyCancelItemList(ll1);
		List<PenaltyChangeItem> ll2 = new ArrayList<PenaltyChangeItem>();
		ArrayList<PenaltyFee> feel = new ArrayList<PenaltyFee>();
		feel.add(new PenaltyFee().setCurrency("CNY").setAmount(300));
		ll2.add(new PenaltyChangeItem().setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE).setPenaltyContent(new PenaltyContent().setPenaltyType(PenaltyTypeEnum.FEE).setPenaltyFeeList(feel)));
		p.setPenaltyChangeItemList(ll2);
		List<PenaltyAdditionEnum> al = new ArrayList<PenaltyAdditionEnum>();
		al.add(PenaltyAdditionEnum.NO_AHEAD);
		p.setPenaltyAdditionList(al);
		l1.add(p);
		List<PenaltiesRemark> l2 = new ArrayList<PenaltiesRemark>();
		l2.add(new PenaltiesRemark().setRemark("备注！！！"));
		Penalties penalties = new Penalties().setPenaltiesItemList(l1).setPenaltiesCancelRemarkList(l2).setPenaltiesChangeRemarkList(l2);
		System.out.println(translate(penalties));
	}
}
