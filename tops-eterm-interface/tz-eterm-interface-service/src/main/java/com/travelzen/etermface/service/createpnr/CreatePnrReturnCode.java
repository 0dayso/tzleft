package com.travelzen.etermface.service.createpnr;

public enum CreatePnrReturnCode {

	SUCCESS(0, "成功"),
	
	CONNECT_ETERM_ERR(9, "连接成都新港出错"),

	SYS_TIME_OUT_ERR(30, "系统超时无反馈"),
	


	SUBMIT_DATE_ERR(101, "提交数据有误"),

	NO_FLIGHT_INFO_ERR(102, "没有航班信息"),

	DEPART_AIRPORT_ERR(103, "出发机场有误"),

	ARRIVE_AIRPORT_ERR(104, "到达机场有误"),

	CARRIER__ERR(105, "航空公司有误"),

	FLIGHT_NUMER_ERR(106, "航班号有误"),

	CLASS_CODE_ERR(107, "舱位有误"),

	FLIGHT_DATE_ERR(108, "航班日期有误"),

	NO_PASSENGER_ERR(111, "没有乘机人"),
	
	PASSENGER_NAME_ERR(112, "乘机人姓名有误"),
	
	PASSENGER_TYPE_ERR(113, "乘客类型有误"),
	
	PASSENGER_BIRTHDAY_ERR(114, "乘机人生日有误"),
	
	SD_ERR(115, "SD失败"),

	PASSENGER_IDENTITICATION_ERR(116, "乘机人证件有误"),
	
	PASSENGER_IDENTITICATION_VALIDTIME_ERR(117, "乘机人证件有效期有误"),
	
	PASSENGER_CE_COUNTRY_ERR(118, "乘机人证件所在国家有误"),
	
	PASSENGER_COUNTRY_ERR(119, "乘机人所在国家有误"),
	
	PASSENGER_INF_COUNT_ERR(120, "婴儿人数大于成人人数"),
	
	CONTACTS_ERR(150, "联系方式有误"),

	// OPEN_PORT_ERR ( "12", "端口打开失败"),
	// OPEN_PORT_ERR ( "12", "登录失败"),
	// 锁定账号失败



	COMMAND_NO_RETURN_ERR(126, "指令没有返回结果"),

	NO_USAGE_FLIGHT_INFO_ERR(127, "没有可用航班信息"),

	AVAILABILITY_ERROR(129, "无座位"),

	CLASS_CODE_CAN_NOT_BOOK_ERR(138, "该舱位不能预订"),

	NAME_ITEM_ERR(139, "姓名项有误"),

	CT_ITEM_ERR(200, "CT项有误"),

	DOCS_ITEM_ERR(201, "DOCS项有误"),

	PNR_ITEM_ERR(202, "PNR某项有误"),

	NO_CONTACTOR(203, "没有联系人"),

	OSI_ITEM_ERR(204, "OSI 项有误"),

	SS_ITEM_ERR(205, "SS 项有误"),

	TKT_ITEM_ERR(206, "TKT 项有误"),

	CLOSE_OP_TIME_OUT_ERR(303, "封口超时，请检查是否已经生成PNR"),

	COMMAN_EXECUTE_TIMEOUT_ERR(304, "指令超时"),

	PNR_PARSE_ERR(305, "PNR解析失败"),
	
	
	
	CHECK_CONTINUITY(401, "检查航段连续性"),

	//

	AUTHORITY(601, " 此pid没有操作所输入指令的权限 解决：用有权限pid"),

	CHECK_BLINK_CODE(602, "pnr状态问题  解决：/k"),

	DUP_ID(403, "PNR中某项重复，或缺少旅客标识"),

	ET_PASSENGER_DATA_NOT_FOUND(604, " 电子客票信息没有发现"),

	FORMAT(605, "输入格式不正确"),

	MANUAL(606, "手工出票 ei项太长"),

	PLEASE_CHECK_TKT_ELEMENT(607, "请检查票号组"),

	PSGR_ID(608, " 旅客标识不正确"),

	STOCK(609, "票号不正确，检查票号是否足够"),

//	CTCT 项的错误类型： 
//	FORMAT 
//		格式错误。
//		在输入CTCM 中没有PASSENGER ID，或者在输入CTCT
//		中带有PASSENGER ID。
//		
//		
//	SIZE
//		电话号码长度超过30 位。
//	
//	
//	PLEASE INPUT OSI (AIRLINE) CTCT	OR OSI (AIRLINE) CTCM
//			PNR 中没有OSI CA CTC 项。
//		
//	PLEASE INPUT VALID TEL NUMBER   
//		输入“13800138000”等无效号码。
//		
//	DUPLICATE TEL NUMBER 
//		旅客间的手机号重复，或者联系人间的手机号重复。	
//		
//	DUPLICATE 
//		同一个旅客输入了多个电话号码。
	
	MARRIED_SEGMENT_ERR(991, "MARRIED SEGMENT EXIST IN THE PNR"),
	
	NO_SEAT_ERR(992, "您选择的航班库存不足，请重新查询预订！"),
	
	INVALID_OFFICE_ERR(997, "请联系平台客服：4007206666"), //不存在该Office的配置
	
	SESSION_EXPIRE_ERR(998, "会话超时"), 
	
	UNKNOWN_ERR(999, "未知错误"),
	
	UFIS_ERR(1000, "Ufis异常"), ;

	int errorCode;
	String errorDetail;

	CreatePnrReturnCode(int code, String detail) {
		this.errorCode = code;
		this.errorDetail = detail;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDetail() {
		return errorDetail;
	}

	public void setErrorDetail(String errorDetail) {
		this.errorDetail = errorDetail;
	}

}
