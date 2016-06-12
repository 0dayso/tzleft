package com.travelzen.rosetta.eterm.common.pojo.enums;

/**
 * 乘客类型枚举类
 * <p>
 * @author yiming.yan
 * @Date Dec 04, 2015
 */
public enum PassengerType {
    /**
     * 乘客类型，成人
     */
    ADT("成人"),
    /**
     * 乘客类型，儿童
     */
    CHD("儿童"),
    /**
     * 乘客类型，婴儿
     */
    INF("婴儿"),
    /**
     * 乘客类型,学生
     */
    SD("学生"),
    /**
     * 乘客类型,海员
     */
    SC("海员"),
    /**
     * 乘客类型,移民
     */
    EM("移民"),
    /**
     * 乘客类型,青年
     */
    ZZ("青年"),
    /**
     * 乘客类型,青年学生
     */
    ZS("青年学生"),
    /**
     * 乘客类型,劳务
     */
    DL("劳务"),
    /**
     * 乘客类型,政府
     */
    GOV("政府"),
    /**
     * 乘客类型,第三方（报价类型）
     */
    TRD("三方");

    private String description;

    private PassengerType(String description) {
        this.description = description;
    }

    public String getPersistentValue() {
        return this.description;
    }

}
