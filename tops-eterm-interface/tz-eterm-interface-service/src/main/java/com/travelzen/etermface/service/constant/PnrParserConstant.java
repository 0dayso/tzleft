package com.travelzen.etermface.service.constant;

public class PnrParserConstant {

    public static final int RETRY_ON_SESSION_EXPIRE = 2;

    public static final int AVH_NEED_MILLSEC = 3_000;

    public static final int QTE_NEED_MILLSEC = 10_000;

    public static final int PAT_NEED_MILLSEC = 3_000;

    public static final int SEAT_EXIST_JUDGEMENT_MILLSEC = 5_000;

    public static final int RESUME_MILLSEC = 2_000;

    public static final int DEFAULT_RETRY_CNT = 2;

    public static final int RT_MILLSEC = 1_000;
    //pnr长度
    public static final int PNR_LENGTH = 6;

    public static final String QTE_KEY_FARE = "fare";
    public static final String QTE_KEY_XT = "xt";
    public static final String QTE_KEY_QVALUE = "qvalue";
    public static final String QTE_KEY_RATE = "rate";

    public static final String IS_CODESHARE = "1";


    public static final String BIZ_QTE = "QTE";
    public static final String BIZ_NUC = "NUC";

    public static final String PASSENGER_TYPE_ADT = "ADT";
    public static final String PASSENGER_TYPE_CHD = "CHD";
    public static final String PASSENGER_TYPE_INF = "INF";

    public static final String PAT_TYPE_ADT = "A";
    public static final String PAT_TYPE_CHD = "A*CH";
    public static final String PAT_TYPE_INF = "A*IN";

    public static final int DEBUG_LEVEL_ZERO = 0;
    public static final int DEBUG_LEVEL_RT = 1;
    public static final int DEBUG_LEVEL_PRICE = 2;

    public static final int MAX_TIME_LIMIT_ERROR_INFO_LENGTH = 100;

    public final static String SOURCE_ETERM = "eterm";
    public final static String SOURCE_WHITESCREEN = "whitescreen";
    public final static String SOURCE_UAPI = "uapi";
    public final static String SOURCE_DISTRIBUTOR = "distributor";

    public final static String ROLE_PURCHASER = "purchaser";
    public final static String ROLE_OPERATOR = "operator";

    public final static String TKT_EXTRACT_TYPE_ACCURACY_FIRST = "accuracyFirst";
    public final static String TKT_EXTRACT_TYPE_COVERAGE_FIRST = "coverageFirst";

    public final static String PARAMETER_NAME_TKT_EXTRACT_TYPE = "tktExtractType";

    public final static String CONST_SEGTYPE_FLT = "FLT";
    public final static String CONST_SEGTYPE_ARNK = "ARNK";

    public final static String CONST_PRICETYPE_N = "N";
    public final static String CONST_PRICETYPE_M = "M";
    public final static String CONST_PRICETYPE_IT = "IT";

    public static final int AIRPORT_CODE_LENGTH = 3;
    public static final int CITY_CODE_LENGTH = 3;
    public static final int CARRIER_CODE_LENGTH = 2;
    public static final int DOUBLE_SLASH_LENGTH = 2;

    public final static int LINE_WIDTH = 80;

    public final static String TKT_TYPE_IN = "IN";
    public final static String TKT_MODE_B2B = "B2B";

}
