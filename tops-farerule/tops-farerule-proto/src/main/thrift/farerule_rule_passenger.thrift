// ...乘客信息...

namespace java com.travelzen.farerule.rule

include "farerule_rule_common.thrift"

// 乘客类型
enum PassengerType {
  ADT = 0,
  CHD = 1,
  INF = 2,
  STU = 3,//学生
  SEA = 4,//海员
  YOU = 5,//青年
  LAB = 6,//劳务
  SCT = 7,//老人
  OTH = 8,//其他
}

// 乘客类型和年龄限制
struct PassengerAgeLimitItem {
  1:optional farerule_rule_common.Judge judge,
  2:optional PassengerType passengerType,
  3:optional i32 minAge,
  4:optional i32 maxAge,
  5:optional farerule_rule_common.Judge accompanied,
  6:optional farerule_rule_common.Judge hasSeat,
}

// 乘客人数限制
struct PassengerNumLimitItem {
  1:optional farerule_rule_common.Judge judge,
  2:optional i32 minNum,
  3:optional i32 maxNum,
  4:optional i32 minAdultNum,
}

// 儿童婴儿折扣
struct ChildInfantDiscountItem {
  1:optional PassengerType passengerType,
  2:optional i32 minAge,
  3:optional i32 maxAge,
  4:optional farerule_rule_common.Judge accompanied,
  5:optional farerule_rule_common.Judge hasSeat,
  6:optional i32 discount,
}