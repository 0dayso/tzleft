package com.travelzen.farerule.jpecker.client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;

import com.travelzen.fare.DisplayRule;
import com.travelzen.farerule.jpecker.client.properties.ConstProperties;
import com.travelzen.farerule.jpecker.server.JpeckerService;
import com.travelzen.framework.logger.core.ri.RequestIdentityLogger;

public class JpeckerClient {

    private final static Logger logger = RequestIdentityLogger.getLogger(JpeckerClient.class);

    public static final SimpleDateFormat YYYY_MM_DD_HH_MM_SS_SSS_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

    static JpeckerClientLoadBalancingChannel loadBalancingChannel = null;

    static {
        String ip = ConstProperties.THRIFT_SERVER_IP;
        String port = ConstProperties.THRIFT_SERVER_PORT;
        String ipAndPort = ip + ":" + port;
        List<String> list = new ArrayList<String>();
        list.add(ipAndPort);
        loadBalancingChannel = new JpeckerClientLoadBalancingChannel(list, ConstProperties.THRIFT_PREFIX, ConstProperties.THRIFT_SERVER_NAME, false);
    }
    
    public static JpeckerService.Iface getInstance() throws Exception {
    	return loadBalancingChannel.proxy();
    }

    public static DisplayRule fareRulePeck(List<String> idList) {
        JpeckerService.Iface client = null;
        DisplayRule displayRule = null;
        long begin = System.currentTimeMillis();
        try {
            client = loadBalancingChannel.proxy();
            com.travelzen.farerule.jpecker.server.DisplayRule tmpDisplayRule = client.fareRulePeck(idList);
            displayRule = convertDisplayRule(tmpDisplayRule);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        long end = System.currentTimeMillis();
        long last = end - begin;
        logger.info("运价规则解析请求id:" + idList + "请求开始时间:" + YYYY_MM_DD_HH_MM_SS_SSS_FORMAT.format(new Date(begin)) + "请求结束时间：" + YYYY_MM_DD_HH_MM_SS_SSS_FORMAT.format(new Date(end)) + "耗时：" + last
                / 1000 + "秒" + last % 1000 + "毫秒");
        return displayRule;
    }

    public static DisplayRule convertDisplayRule(com.travelzen.farerule.jpecker.server.DisplayRule tmpDisplayRule) {
        if (tmpDisplayRule == null)
            return null;
        DisplayRule displayRule = new DisplayRule();
        displayRule.setId(tmpDisplayRule.getId());
        if (tmpDisplayRule.getMinStay() != null)
        	displayRule.setMinStay(tmpDisplayRule.getMinStay());
        if (tmpDisplayRule.getMaxStay() != null)
        	displayRule.setMaxStay(tmpDisplayRule.getMaxStay());
        if (tmpDisplayRule.getTravelDate() != null)
        	displayRule.setTravelDate(tmpDisplayRule.getTravelDate());
        if (tmpDisplayRule.getPenalties() != null)
        	displayRule.setPenalties(tmpDisplayRule.getPenalties());
        return displayRule;
    }

    public static List<String> fetchOriginalRule(String id) {
        JpeckerService.Iface client = null;
        List<String> ruleTextList = new ArrayList<String>();
        long begin = System.currentTimeMillis();
        try {
            client = loadBalancingChannel.proxy();
            ruleTextList = client.fetchOriginalRule(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        long end = System.currentTimeMillis();
        long last = end - begin;
        logger.info("运价规则原始文本查询请求id:" + id + "请求开始时间:" + YYYY_MM_DD_HH_MM_SS_SSS_FORMAT.format(new Date(begin)) + "请求结束时间：" + YYYY_MM_DD_HH_MM_SS_SSS_FORMAT.format(new Date(end)) + "耗时：" + last
                / 1000 + "秒" + last % 1000 + "毫秒");
        return ruleTextList;
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<String>();
        String id = "5827e05e1c6b720a7dd57ddcd86e3495";
        list.add(id);
        System.out.println(JpeckerClient.fareRulePeck(list));
        System.out.println(JpeckerClient.fetchOriginalRule(id));
    }
}
