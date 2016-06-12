package com.travelzen.etermface.common.config.cdxg;

import com.travelzen.etermface.common.config.cdxg.pojo.User;

public class GenerateUrl {
    public static String generateRawUrl(User user, String cmd) {
        String url = "http://" + user.getAccountInfo().getAgentHost() + ":" + user.getAccountInfo().getAgentPort() + "/ib_tranx_req.asp?uid=%1$s&sessionid=%2$s&verify=0&termid=%3$s&cmd=raw&ins=%4$s";
        url = String.format(url, user.getAccountInfo().getAgentName(), user.getSessionid(), user.getTermid(), cmd);
        return url;
    }

    public static String generateUrl(User user, String cmd, String cmdType) {
        String url = "http://" + user.getAccountInfo().getAgentHost() + ":" + user.getAccountInfo().getAgentPort() + "/ib_tranx_req.asp?uid=%1$s&sessionid=%2$s&verify=0&termid=%3$s&cmd=%4$s&%5$s";
        url = String.format(url, user.getAccountInfo().getAgentName(), user.getSessionid(), user.getTermid(), cmdType, cmd);
        return url;
    }
}
