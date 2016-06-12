package com.travelzen.etermface.common.config.cdxg.pojo;

import com.travelzen.etermface.common.config.AccountInfo;

import java.util.concurrent.atomic.AtomicLong;

public class User {
    private AccountInfo accountInfo;
    private String sessionid;
    private String termid;
    private String reuestid;
    //该账号的开始使用时间
    private AtomicLong beginTime;
    //该账号的最长占用时间
    private AtomicLong maxkeepTime;

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public void setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getTermid() {
        return termid;
    }

    public void setTermid(String termid) {
        this.termid = termid;
    }

    public String getReuestid() {
        return reuestid;
    }

    public void setReuestid(String reuestid) {
        this.reuestid = reuestid;
    }

    public AtomicLong getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(AtomicLong beginTime) {
        this.beginTime = beginTime;
    }

    public AtomicLong getMaxkeepTime() {
        return maxkeepTime;
    }

    public void setMaxkeepTime(AtomicLong maxkeepTime) {
        this.maxkeepTime = maxkeepTime;
    }

    private User() {
    }

    public User(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
        this.sessionid = accountInfo.getAgentName();
        this.termid = accountInfo.getAgentName();
        this.reuestid = "01";
    }

    @Override
    public String toString() {
        return "User{" +
                "accountInfo=" + accountInfo +
                ", sessionid='" + sessionid + '\'' +
                ", termid='" + termid + '\'' +
                ", reuestid='" + reuestid + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!accountInfo.equals(user.accountInfo)) return false;
        if (!reuestid.equals(user.reuestid)) return false;
        if (!sessionid.equals(user.sessionid)) return false;
        if (!termid.equals(user.termid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = accountInfo.hashCode();
        result = 31 * result + sessionid.hashCode();
        result = 31 * result + termid.hashCode();
        result = 31 * result + reuestid.hashCode();
        return result;
    }
}
