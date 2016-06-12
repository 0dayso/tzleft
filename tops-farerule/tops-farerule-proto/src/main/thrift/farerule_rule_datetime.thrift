// ...时间...

namespace java com.travelzen.farerule.rule

include "farerule_condition.thrift"
include "farerule_rule_common.thrift"

// 时间类型
enum TimeTypeEnum {
  DAY		= 0,
  MONTH		= 1,
  YEAR		= 2,
  HOUR		= 3,
}

// 周时间类型
enum WeekDayEnum {
  SUN 	= 0,
  MON 	= 1,
  TUE 	= 2,
  WED 	= 3,
  THU 	= 4,
  FRI 	= 5,
  SAT 	= 6,
  EVE	= 9,
}

// AM/PM类型
enum AmPmEnum {
  AM	= 0,
  PM	= 1,
}

// 时间点
struct DayTimePoint {
  1:optional string time,
  2:optional AmPmEnum ampm,
  3:optional WeekDayEnum weekday,
}

// 时间限制子项
struct DayTimeSubItem {
  1:optional DayTimePoint startTimePoint,
  2:optional DayTimePoint endTimePoint,
}

// 时间限制项
struct DayTimeItem {
  1:optional farerule_condition.OriginCondition originCondition,
  2:optional list<DayTimeSubItem> dayTimeSubItemList,
  3:optional farerule_rule_common.Judge judge,
}

// 预订与提前出票子项
struct AdvanceTicketSubItem {
  1:optional TimeTypeEnum reservationTimeType,
  2:optional i32 reservationTimeNum,
  3:optional TimeTypeEnum timeTypeAfterReservation,
  4:optional i32 timeNumAfterReservation,
  5:optional TimeTypeEnum timeTypeBeforeDeparture,
  6:optional i32 timeNumBeforeDeparture,
}

// 预订与提前出票项
struct AdvanceTicketItem {
  1:optional farerule_condition.OriginCondition originCondition,
  2:optional list<AdvanceTicketSubItem> advanceTicketSubItemList,
}

// 文件价停留时间航段限制
struct SegmentNode {
  1:required i32 nodeIndex;
  2:required bool isArrivePoint;
}

struct StaySegment {
  1:required SegmentNode fromSegmentNode;
  2:required SegmentNode toSegmentNode;
}

// 最小停留时间限制
struct MinStayItem {
  1:optional farerule_condition.RuleCondition ruleCondition,
  2:optional TimeTypeEnum stayTimeType,
  3:optional i32 stayTimeNum,
  4:optional WeekDayEnum weekday,
  5:optional list<StaySegment> staySegmentList,
}

// 最大停留时间限制
struct MaxStayItem {
  1:optional farerule_condition.RuleCondition ruleCondition,
  2:optional TimeTypeEnum stayTimeType,
  3:optional i32 stayTimeNum,
  4:optional i64 date,
  5:optional list<StaySegment> staySegmentList,
}

// 旅行出发时间项
struct TravelDateItem {
  1:optional farerule_condition.OriginCondition originCondition,
  2:optional list<farerule_condition.TravelDateSubItem> travelDateSubItemList,
  3:optional i64 completeDate,
  4:optional i32 segmentNum,
}

// 出票时间项
struct SalesDateItem {
  1:optional farerule_condition.OriginCondition originCondition,
  2:optional list<farerule_condition.SalesDateSubItem> salesDateSubItemList,
  3:optional i32 segmentNum,
}
