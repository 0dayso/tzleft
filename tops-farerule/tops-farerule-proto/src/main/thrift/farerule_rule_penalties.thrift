// ...退改签...

namespace java com.travelzen.farerule.rule

include "farerule_condition.thrift"

// 惩罚类型：不允许，允许，百分比，费用，差价
enum PenaltyTypeEnum {
  NOT_PERMIT 	= 0,
  PERMIT		= 1,
  RATIO 		= 2,
  FEE 			= 3,
  HIGHER		= 4,
}

// 惩罚-百分比
struct PenaltyRatio {
  1:optional double ratio,
}

// 惩罚-费用
struct PenaltyFee {
  1:optional string currency,
  2:optional double amount,
}

// 惩罚内容
struct PenaltyContent {
  1:required PenaltyTypeEnum penaltyType,
  2:optional PenaltyRatio penaltyRatio,
  3:optional list<PenaltyFee> penaltyFeeList,
  4:optional bool containDiff,
}

// 惩罚条件类型：起飞前，起飞后，起飞前?小时
enum PenaltyConditionTypeEnum {
  BEFORE_DEPT 		= 0,
  AFTER_DEPT 		= 1,
  BEFORE_DEPT_TIME 	= 2,
}

// 惩罚类型
struct PenaltyCondition {
  1:required PenaltyConditionTypeEnum penaltyConditionType,
  2:optional i32 beforeDeptHour,
}

// 退票类型：退票，误机
enum PenaltyCancelTypeEnum {
  CANCEL = 0,
  NOSHOW = 1,
}

// 退票项
struct PenaltyCancelItem {
  1:required PenaltyCancelTypeEnum penaltyCancelType,
  2:optional bool used,
  3:optional PenaltyCondition penaltyCondition,
  4:optional PenaltyContent penaltyContent,
}

// 改签类型：改期，误机，换开，更改路线，签转
enum PenaltyChangeTypeEnum {
  CHANGE 	= 0,
  NOSHOW 	= 1,
  REISSUE 	= 2,
  REROUTE 	= 3,
  ENDORSE 	= 4,
  UPSELL	= 5,
}

// 改签项
struct PenaltyChangeItem {
  1:required PenaltyChangeTypeEnum penaltyChangeType,
  2:optional bool used,
  3:optional PenaltyCondition penaltyCondition,
  4:optional PenaltyContent penaltyContent,
}

enum PenaltyAdditionEnum {
  NO_CLASS_CHANGE 	= 0,	// 同舱位换乘
  NO_AHEAD 			= 1,	// 始发日不得提前
  NO_EXTENTION 		= 2,	// 更改后不得超过原有效期
}

// 退改签备注
struct PenaltiesRemark {
  1:optional string remark,
}

// 退改签项：1-限制条件，2-退票，3-改签
struct PenaltiesItem {
  1:optional farerule_condition.RuleCondition ruleCondition,
  2:optional list<PenaltyCancelItem> PenaltyCancelItemList,
  3:optional list<PenaltyChangeItem> PenaltyChangeItemList,
  4:optional list<PenaltyAdditionEnum> penaltyAdditionList,
}
