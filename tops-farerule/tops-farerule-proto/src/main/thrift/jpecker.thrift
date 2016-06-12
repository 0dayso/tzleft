// Used in jpecker

namespace java com.travelzen.farerule.jpecker.struct

include "farerule_rule_penalties.thrift"

struct RuleTextBlock {
  1:optional string origin,
  2:optional string text,
}

struct RuleTextSegment {
  1:optional string origin,
  2:optional string salesDate,
  3:optional string travelDate,
  4:optional string text,
}

struct PenaltyNoshowPack {
  1:optional farerule_rule_penalties.PenaltyCancelItem cancelNoshow,
  2:optional farerule_rule_penalties.PenaltyChangeItem changeNoshow,
}
