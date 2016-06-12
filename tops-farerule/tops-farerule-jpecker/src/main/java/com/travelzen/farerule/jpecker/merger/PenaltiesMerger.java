/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */
 
package com.travelzen.farerule.jpecker.merger;

import java.util.*;

import com.travelzen.farerule.exchange.consts.ExchangeConst;
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

public class PenaltiesMerger {
	
	public static List<PenaltiesItem> merge (List<PenaltiesItem> penaltiesItemList) {
		return doSelect(penaltiesItemList);
	}
	
	private static List<PenaltiesItem> doSelect (List<PenaltiesItem> readyPenaltiesItemList) {
		PenaltiesItem finalPenaltiesItem = new PenaltiesItem();
		
		List<PenaltyContent> pcList_cancel_bd = new ArrayList<PenaltyContent>();
		List<PenaltyContent> pcList_cancel_ad = new ArrayList<PenaltyContent>();
		List<PenaltyContent> pcList_cancel_noshow = new ArrayList<PenaltyContent>();
		
		List<PenaltyContent> pcList_change_bd = new ArrayList<PenaltyContent>();
		List<PenaltyContent> pcList_change_ad = new ArrayList<PenaltyContent>();
		List<PenaltyContent> pcList_change_reissue = new ArrayList<PenaltyContent>();
		List<PenaltyContent> pcList_change_reroute = new ArrayList<PenaltyContent>();
		List<PenaltyContent> pcList_change_endorse = new ArrayList<PenaltyContent>();
		List<PenaltyContent> pcList_change_noshow = new ArrayList<PenaltyContent>();
		
		for (PenaltiesItem penaltiesItem:readyPenaltiesItemList) {
			if (penaltiesItem.getPenaltyCancelItemList() != null) {
				for (PenaltyCancelItem penaltyCancelItem:penaltiesItem.getPenaltyCancelItemList()) {
					if (penaltyCancelItem.getPenaltyCancelType() == PenaltyCancelTypeEnum.CANCEL) {
						if (penaltyCancelItem.getPenaltyCondition() == null) {
							pcList_cancel_bd.add(penaltyCancelItem.getPenaltyContent());
							pcList_cancel_ad.add(penaltyCancelItem.getPenaltyContent());
						} else if (penaltyCancelItem.getPenaltyCondition().getPenaltyConditionType() 
								== PenaltyConditionTypeEnum.BEFORE_DEPT) {
							pcList_cancel_bd.add(penaltyCancelItem.getPenaltyContent());
						} else if (penaltyCancelItem.getPenaltyCondition().getPenaltyConditionType() 
								== PenaltyConditionTypeEnum.AFTER_DEPT) {
							pcList_cancel_ad.add(penaltyCancelItem.getPenaltyContent());
						}
					}
					if (penaltyCancelItem.getPenaltyCancelType() == PenaltyCancelTypeEnum.NOSHOW) {
						pcList_cancel_noshow.add(penaltyCancelItem.getPenaltyContent());
					}
				}
			}
			if (penaltiesItem.getPenaltyChangeItemList() != null) {
				for (PenaltyChangeItem penaltyChangeItem:penaltiesItem.getPenaltyChangeItemList()) {
					if (penaltyChangeItem.getPenaltyChangeType() == PenaltyChangeTypeEnum.CHANGE) {
						if (penaltyChangeItem.getPenaltyCondition() == null) {
							pcList_change_bd.add(penaltyChangeItem.getPenaltyContent());
							pcList_change_ad.add(penaltyChangeItem.getPenaltyContent());
						} else if (penaltyChangeItem.getPenaltyCondition().getPenaltyConditionType() 
								== PenaltyConditionTypeEnum.BEFORE_DEPT) {
							pcList_change_bd.add(penaltyChangeItem.getPenaltyContent());
						} else if (penaltyChangeItem.getPenaltyCondition().getPenaltyConditionType() 
								== PenaltyConditionTypeEnum.AFTER_DEPT) {
							pcList_change_ad.add(penaltyChangeItem.getPenaltyContent());
						}
					}
					if (penaltyChangeItem.getPenaltyChangeType() == PenaltyChangeTypeEnum.NOSHOW) {
						pcList_change_noshow.add(penaltyChangeItem.getPenaltyContent());
					}
					if (penaltyChangeItem.getPenaltyChangeType() == PenaltyChangeTypeEnum.REISSUE) {
						pcList_change_reissue.add(penaltyChangeItem.getPenaltyContent());
					}
					if (penaltyChangeItem.getPenaltyChangeType() == PenaltyChangeTypeEnum.REROUTE) {
						pcList_change_reroute.add(penaltyChangeItem.getPenaltyContent());
					}
					if (penaltyChangeItem.getPenaltyChangeType() == PenaltyChangeTypeEnum.ENDORSE) {
						pcList_change_endorse.add(penaltyChangeItem.getPenaltyContent());
					}
				}
			}
		}
		
		List<PenaltyCancelItem> penaltyCancelItemList = new ArrayList<PenaltyCancelItem>();
		List<PenaltyChangeItem> penaltyChangeItemList = new ArrayList<PenaltyChangeItem>();
		// cancel bd
		if (pcList_cancel_bd.size() !=0) {
			PenaltyContent penaltyContent = mergePenaltyContent(pcList_cancel_bd);
			PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
			penaltyCancelItem.setPenaltyCondition(
					new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.BEFORE_DEPT));
			penaltyCancelItem.setPenaltyContent(penaltyContent);
			penaltyCancelItemList.add(penaltyCancelItem);
		}
		// cancel ad
		if (pcList_cancel_ad.size() !=0) {
			PenaltyContent penaltyContent = mergePenaltyContent(pcList_cancel_ad);
			PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
			penaltyCancelItem.setPenaltyCondition(
					new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.AFTER_DEPT));
			penaltyCancelItem.setPenaltyContent(penaltyContent);
			penaltyCancelItemList.add(penaltyCancelItem);
		}
		// merge the same cancel bd && ad
		if (penaltyCancelItemList.size() == 2) {
			PenaltyContent penaltyContent1 = penaltyCancelItemList.get(0).getPenaltyContent();
			PenaltyContent penaltyContent2 = penaltyCancelItemList.get(1).getPenaltyContent();
			if (comparePenaltyContent(penaltyContent1, penaltyContent2)) {
				penaltyCancelItemList.clear();
				PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
				penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.CANCEL);
				penaltyCancelItem.setPenaltyContent(penaltyContent1);
				penaltyCancelItemList.add(penaltyCancelItem);
			}
		}
		// cancel noshow
		if (pcList_cancel_noshow.size() !=0) {
			PenaltyContent penaltyContent = mergePenaltyContent(pcList_cancel_noshow);
			PenaltyCancelItem penaltyCancelItem = new PenaltyCancelItem();
			penaltyCancelItem.setPenaltyCancelType(PenaltyCancelTypeEnum.NOSHOW);
			penaltyCancelItem.setPenaltyContent(penaltyContent);
			penaltyCancelItemList.add(penaltyCancelItem);
		}
		//change bd
		if (pcList_change_bd.size() !=0) {
			PenaltyContent penaltyContent = mergePenaltyContent(pcList_change_bd);
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
			penaltyChangeItem.setPenaltyCondition(
					new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.BEFORE_DEPT));
			penaltyChangeItem.setPenaltyContent(penaltyContent);
			penaltyChangeItemList.add(penaltyChangeItem);
		}
		// change ad
		if (pcList_change_ad.size() !=0) {
			PenaltyContent penaltyContent = mergePenaltyContent(pcList_change_ad);
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
			penaltyChangeItem.setPenaltyCondition(
					new PenaltyCondition().setPenaltyConditionType(PenaltyConditionTypeEnum.AFTER_DEPT));
			penaltyChangeItem.setPenaltyContent(penaltyContent);
			penaltyChangeItemList.add(penaltyChangeItem);
		}
		// merge the same change bd && ad
		if (penaltyChangeItemList.size() == 2) {
			PenaltyContent penaltyContent1 = penaltyChangeItemList.get(0).getPenaltyContent();
			PenaltyContent penaltyContent2 = penaltyChangeItemList.get(1).getPenaltyContent();
			if (comparePenaltyContent(penaltyContent1, penaltyContent2)) {
				penaltyChangeItemList.clear();
				PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
				penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.CHANGE);
				penaltyChangeItem.setPenaltyContent(penaltyContent1);
				penaltyChangeItemList.add(penaltyChangeItem);
			}
		}
		// change noshow
		if (pcList_change_noshow.size() !=0) {
			PenaltyContent penaltyContent = mergePenaltyContent(pcList_change_noshow);
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.NOSHOW);
			penaltyChangeItem.setPenaltyContent(penaltyContent);
			penaltyChangeItemList.add(penaltyChangeItem);
		}
		// change reissue
		if (pcList_change_reissue.size() !=0) {
			PenaltyContent penaltyContent = mergePenaltyContent(pcList_change_reissue);
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.REISSUE);
			penaltyChangeItem.setPenaltyContent(penaltyContent);
			penaltyChangeItemList.add(penaltyChangeItem);
		}
		// change reroute
		if (pcList_change_reissue.size() !=0) {
			PenaltyContent penaltyContent = mergePenaltyContent(pcList_change_reissue);
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.REROUTE);
			penaltyChangeItem.setPenaltyContent(penaltyContent);
			penaltyChangeItemList.add(penaltyChangeItem);
		}
		// change endorse
		if (pcList_change_endorse.size() !=0) {
			PenaltyContent penaltyContent = mergePenaltyContent(pcList_change_endorse);
			PenaltyChangeItem penaltyChangeItem = new PenaltyChangeItem();
			penaltyChangeItem.setPenaltyChangeType(PenaltyChangeTypeEnum.ENDORSE);
			penaltyChangeItem.setPenaltyContent(penaltyContent);
			penaltyChangeItemList.add(penaltyChangeItem);
		}
		
		finalPenaltiesItem.setPenaltyCancelItemList(penaltyCancelItemList);
		finalPenaltiesItem.setPenaltyChangeItemList(penaltyChangeItemList);
		List<PenaltiesItem> penaltiesItemList = new ArrayList<PenaltiesItem>();
		penaltiesItemList.add(finalPenaltiesItem);
		return penaltiesItemList;
	}
	
	private static PenaltyContent mergePenaltyContent(List<PenaltyContent> penaltyContentList) {
		PenaltyContent penaltyContent = new PenaltyContent();
		PenaltyTypeEnum tmpType = null;
		List<PenaltyFee> penaltyFeeList = new ArrayList<PenaltyFee>();
		double tmpRatio = 0;
		for (PenaltyContent tmpPenaltyContent:penaltyContentList) {
			if (tmpPenaltyContent.getPenaltyType() == PenaltyTypeEnum.NOT_PERMIT) {
				tmpType = PenaltyTypeEnum.NOT_PERMIT;
				break;
			} else if (tmpPenaltyContent.getPenaltyType() == PenaltyTypeEnum.PERMIT) {
				if (tmpType == null)
					tmpType = PenaltyTypeEnum.PERMIT;
			} else if (tmpPenaltyContent.getPenaltyType() == PenaltyTypeEnum.FEE) {
				if (tmpType == null || tmpType == PenaltyTypeEnum.PERMIT)
					tmpType = PenaltyTypeEnum.FEE;
				else if (tmpType == PenaltyTypeEnum.RATIO)
					tmpType = PenaltyTypeEnum.HIGHER;
				penaltyFeeList.addAll(tmpPenaltyContent.getPenaltyFeeList());
			} else if (tmpPenaltyContent.getPenaltyType() == PenaltyTypeEnum.RATIO) {
				if (tmpType == null || tmpType == PenaltyTypeEnum.PERMIT)
					tmpType = PenaltyTypeEnum.RATIO;
				else if (tmpType == PenaltyTypeEnum.FEE)
					tmpType = PenaltyTypeEnum.HIGHER;
				if (tmpPenaltyContent.getPenaltyRatio().getRatio() > tmpRatio)
					tmpRatio = tmpPenaltyContent.getPenaltyRatio().getRatio();
			}
		}
		penaltyContent.setPenaltyType(tmpType);
		if (tmpType == PenaltyTypeEnum.FEE) {
			penaltyContent.setPenaltyFeeList(selectPenaltyFee(penaltyFeeList));
		} else if (tmpType == PenaltyTypeEnum.RATIO) {
			penaltyContent.setPenaltyRatio(new PenaltyRatio().setRatio(tmpRatio));
		} else if (tmpType == PenaltyTypeEnum.HIGHER) {
			penaltyContent.setPenaltyFeeList(selectPenaltyFee(penaltyFeeList));
			penaltyContent.setPenaltyRatio(new PenaltyRatio().setRatio(tmpRatio));
		}
		return penaltyContent;
	}

	private static List<PenaltyFee> selectPenaltyFee(List<PenaltyFee> penaltyFeeList) {
		PenaltyFee finalPenaltyFee = new PenaltyFee();
		String finalCurrency = "";
		double finalAmount = 0;
		double ref = Double.MIN_VALUE;
		for (PenaltyFee penaltyFee:penaltyFeeList) {
			double exchange = 1;
			if (ExchangeConst.exchangeMap.containsKey(penaltyFee.getCurrency()))
				exchange = ExchangeConst.exchangeMap.get(penaltyFee.getCurrency());
			double tmpRef = penaltyFee.getAmount() * exchange;
			if (tmpRef > ref) {
				finalCurrency = penaltyFee.getCurrency();
				finalAmount = penaltyFee.getAmount();
				ref = tmpRef;
			}
		}
		finalPenaltyFee.setCurrency(finalCurrency).setAmount(finalAmount);
		List<PenaltyFee> finalPenaltyFeeList = new ArrayList<PenaltyFee>();
		finalPenaltyFeeList.add(finalPenaltyFee);
		return finalPenaltyFeeList;
	}
	
	private static boolean comparePenaltyContent(PenaltyContent penaltyContent1, PenaltyContent penaltyContent2) {
		switch (penaltyContent1.getPenaltyType()) {
		case FEE:
			if (penaltyContent2.getPenaltyType() == PenaltyTypeEnum.FEE &&
					comparePenaltyFee(penaltyContent1.getPenaltyFeeList(), penaltyContent2.getPenaltyFeeList()))
				return true;
			break;
		case RATIO:
			if (penaltyContent2.getPenaltyType() == PenaltyTypeEnum.FEE &&
					comparePenaltyRatio(penaltyContent1.getPenaltyRatio(), penaltyContent2.getPenaltyRatio()))
				return true;
			break;
		default:
			if (penaltyContent1.getPenaltyType() == penaltyContent2.getPenaltyType())
				return true;
			break;
		}
		return false;
	}

	private static boolean comparePenaltyFee(List<PenaltyFee> penaltyFeeList1, List<PenaltyFee> penaltyFeeList2) {
		if (penaltyFeeList1.size() == penaltyFeeList2.size() &&
				penaltyFeeList1.get(0).getCurrency() == penaltyFeeList2.get(0).getCurrency() &&
				penaltyFeeList1.get(0).getAmount() == penaltyFeeList2.get(0).getAmount())
			return true;
		return false;
	}
	
	private static boolean comparePenaltyRatio(PenaltyRatio penaltyRatio1, PenaltyRatio penaltyRatio2) {
		if (penaltyRatio1.getRatio() == penaltyRatio2.getRatio())
			return true;
		return false;
	}
}