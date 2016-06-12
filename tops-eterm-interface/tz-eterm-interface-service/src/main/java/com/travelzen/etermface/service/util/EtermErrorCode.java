package com.travelzen.etermface.service.util;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:15/1/21
 * Time:上午11:42
 * <p/>
 * Description:
 */
public enum EtermErrorCode {
    ELE_NBR("ELE NBR", "旅客序号不正确,或对联程航段操作出现该提示", "检查旅客及对应的序号输入是否正确,或对联程航段一起做RR更改"),
    STOCK("STOCK", "打票机票号使用完或不够用,重新上新票号", "TN重新上新票号"),
    OUT_OF_STOCK("OUT OF STOCK", "票号用完", "重新TN上票");

    EtermErrorCode(String code, String desc, String solution) {
        this.code = code;
        this.desc = desc;
        this.solution = solution;
    }

    private String code;
    private String desc;
    private String solution;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getSolution() {
        return solution;
    }
}
