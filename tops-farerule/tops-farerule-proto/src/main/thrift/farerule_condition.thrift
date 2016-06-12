// ...限制条件...

namespace java com.travelzen.farerule.condition

// 旅行出发时间限制：1-在这一天或之后，2-在这一天或之前
struct TravelDateSubItem {
  1:optional i64 afterDate,
  2:optional i64 beforeDate,
}

// 出票时间限制：1-在这一天或之后，2-在这一天或之前
struct SalesDateSubItem {
  1:optional i64 afterDate,
  2:optional i64 beforeDate,
}

// 始发地/往返程类型
enum OriginTypeEnum {
  ORIGIN 	= 0,
  OUTBOUND 	= 1,
  INBOUND 	= 2,
  ALL		= 3,
}

// 始发地/往返程限制
struct OriginCondition {
  1:required OriginTypeEnum originType,
  2:optional string location,
}

// 限制条件
struct RuleCondition {
  1:optional OriginCondition originCondition,
  2:optional SalesDateSubItem salesDateCondition,
  3:optional TravelDateSubItem travelDateCondition, 
}

