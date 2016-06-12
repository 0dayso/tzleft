// ...总结构...

namespace java com.travelzen.farerule

include "farerule_condition.thrift"
include "farerule_rule_datetime.thrift"
include "farerule_rule_passenger.thrift"
include "farerule_rule_penalties.thrift"

// ...单条规则封装结构...

struct PassengerLimit {
  1:optional list<farerule_rule_passenger.PassengerAgeLimitItem> passengerAgeLimitItemList,
  2:optional list<farerule_rule_passenger.PassengerNumLimitItem> passengerNumLimitItemList,
}

struct DayTime {
  1:optional list<farerule_rule_datetime.DayTimeItem> dayTimeItemList,
}

struct AdvanceTicket {
  1:optional list<farerule_rule_datetime.AdvanceTicketItem> advanceTicketItemList,
}

struct MinStay {
  1:optional list<farerule_rule_datetime.MinStayItem> minStayItemList,
  2:optional bool acrossWeekend,
}

struct MaxStay {
  1:optional list<farerule_rule_datetime.MaxStayItem> maxStayItemList,
}

struct Stopovers {
  1:optional bool permitStopover,
}

struct TravelDate {
  1:optional list<farerule_rule_datetime.TravelDateItem> travelDateItemList,
}

struct SalesDate {
  1:optional list<farerule_rule_datetime.SalesDateItem> salesDateItemList,
}

struct Penalties {
  1:optional list<farerule_rule_penalties.PenaltiesItem> penaltiesItemList,
  2:optional list<farerule_rule_penalties.PenaltiesRemark> penaltiesCancelRemarkList,
  3:optional list<farerule_rule_penalties.PenaltiesRemark> penaltiesChangeRemarkList,
}

struct ChildInfantDiscount {
  1:optional list<farerule_rule_passenger.ChildInfantDiscountItem> childInfantDiscountItemList,
}

// ...运价规则总封装结构...

// 运价规则来源
enum RuleSourceEnum {
  JPECKER 	= 0,
  PAPERFARE = 1,
  TZEN		= 2,
}

// 运价规则基本信息，来源+ID
struct TzRuleInfo {
  1:required RuleSourceEnum ruleSource,
  2:optional string jpeckerRuleId,
  3:optional i64 paperfareRuleId,
  4:optional i64 tzenRuleId,
}

// 总的运价规则结构
struct TzRule {
  6:optional MinStay minStay,
  7:optional MaxStay maxStay,
  8:optional Stopovers stopovers,
  14:optional TravelDate travelDate,
  16:optional Penalties penalties,
  100:required TzRuleInfo tzRuleInfo,
  101:required bool edited = false,
}
