// MONGO中的结构

namespace java com.travelzen.farerule.mongo

// MONGO中的OriginalRule数据结构
struct OriginalRule {
  1:required string id,
  2:optional string airCompany,
  3:optional string text,
}

// MONGO中的Location数据结构
struct Location {
  1:required string enLoc,
  2:optional string cnLoc,
}