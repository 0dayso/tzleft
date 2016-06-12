package com.travelzen.etermface.common.config.cdxg;

public class CdxgConstant {
    public static final String DEFAULT_OFFICE = "SHA255";
    public static final String DEFAULT_AGENT_CODE = "08307736";
    //持有资源的最长时间
    public static int MAX_LOCK_KEEP_MILLSEC = 60_000;
    //持有资源的默认时间
    public static int BASE_LOCK_KEEP_MILLSEC = 20_000;
    //最大延长时长
    public static int MAX_LOCK_EXTEND_MILLSEC = MAX_LOCK_KEEP_MILLSEC * 3;
}
