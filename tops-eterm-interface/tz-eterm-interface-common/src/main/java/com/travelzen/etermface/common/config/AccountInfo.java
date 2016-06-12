package com.travelzen.etermface.common.config;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:15/2/9
 * Time:上午10:49
 * <p/>
 * Description:
 * <p/>
 * ZK配置文件所对应的对象
 */
public class AccountInfo {
    //账号
    private String agentName;
    //eterm代理服务的ip或者域名
    private String agentHost;
    //eterm代理服务的端口
    private int agentPort;

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentHost() {
        return agentHost;
    }

    public void setAgentHost(String agentHost) {
        this.agentHost = agentHost;
    }

    public int getAgentPort() {
        return agentPort;
    }

    public void setAgentPort(int agentPort) {
        this.agentPort = agentPort;
    }

    @Override
    public String toString() {
        return "AccountInfo{" +
                "agentName='" + agentName + '\'' +
                ", agentHost='" + agentHost + '\'' +
                ", agentPort=" + agentPort +
                '}';
    }
}
