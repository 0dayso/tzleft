package com.travelzen.etermface.common.config.cdxg.user.impl;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:15/5/5
 * Time:下午1:28
 * <p/>
 * Description:
 */
public class CheckUserTimeoutBean {
    public void checkTxnTimeout() {
        RPCServiceSingleton.checkTxnTimeout();
    }
}
