package com.travelzen.rosetta.eterm.common.pojo.enums;

/**
 * 乘客证件类型枚举类
 * <p>
 * @author yiming.yan
 * @Date Dec 04, 2015
 */
public enum PassengerIdType {
    /**
     * 身份证
     */
    NI("身份证"),
    /**
     * 护照
     */
    PP("护照"),
    /**
     * 其他证件类型
     */
    OTHER("其他证件类型");

    private String description;

    private PassengerIdType(String description) {
        this.description = description;
    }

    public String getPersistentValue() {
        return this.description;
    }

}
