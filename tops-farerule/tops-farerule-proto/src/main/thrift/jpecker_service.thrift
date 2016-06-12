namespace java com.travelzen.farerule.jpecker.server

struct DisplayRule {
  6:optional string minStay,
  7:optional string maxStay,
  8:optional string stopovers,
  14:optional string travelDate,
  16:optional string penalties,
  100:optional string id,
}

service JpeckerService {
	DisplayRule fareRulePeck(1:list<string> idList),
	list<string> fetchOriginalRule(1:string id),
}