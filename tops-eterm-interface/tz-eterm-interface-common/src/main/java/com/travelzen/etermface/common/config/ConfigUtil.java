package com.travelzen.etermface.common.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.travelzen.etermface.common.config.cdxg.exception.EtermException;
import com.travelzen.framework.config.tops.TopsConfEnum;
import com.travelzen.framework.config.tops.TopsConfReader;
import com.travelzen.framework.config.tops.util.TopsConfigReaderUtil;
import com.travelzen.framework.core.common.ReturnCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:15/2/9
 * Time:上午10:49
 * <p/>
 * Description:
 */
public class ConfigUtil {
    private static final Logger log = LoggerFactory.getLogger(ConfigUtil.class);
    private static final String Config_File = "properties/users.yaml";
    private static final String User_Key = "config";
    private static final String Ufis_Key = "ufis";
    public static String partKey;
    public static String port;

    static {
        //获取本机配置
        String ip = TopsConfigReaderUtil.getLocalIP();
        port = getTomcatHttpPort();
        log.info("本机ip：{}，port：{}", ip, port);
        if (port == null) {
            throw new IllegalArgumentException();
        }
        partKey = ip + "-" + port;
    }

    /**
     * 确认配置系统获取当前应用的端口号信息
     */
    private static String getTomcatHttpPort() {
        String SERVER_HTTP_PROTOCOL = "HTTP/1.1"; //应用通信所用的服务器协议
        String SERVER_HTTP_SCHEME = "http"; //应用调用的服务器schema

        String port = TomcatParser.getHttpPort(SERVER_HTTP_PROTOCOL, SERVER_HTTP_SCHEME);
        if (StringUtils.isBlank(port)) {
            log.error("获取Tomcat的Http端口号失败");
            throw new EtermException(ReturnCode.ERROR, "获取获取Tomcat的Http的端口号失败");
        }
        return port;
    }

    public static Map<String, List<AccountInfo>> getConfig() {
        Map<String, List<AccountInfo>> map = new HashMap<>();
        String value = TopsConfReader.getConfContent(Config_File, User_Key, TopsConfEnum.ConfScope.G);
        String[] configs = value.split(";");
        for (String config : configs) {
            String[] keyValues = config.split(":");

            String[] keys = keyValues[0].split(",");
            for (String key : keys) {
                List<AccountInfo> list = map.get(key);
                if (list == null) {
                    list = Lists.newArrayList();
                    map.put(key, list);
                }

                String[] tmp = keyValues[1].split("_");
                String[] agentNames = tmp[2].split(",");
                for (String agentName : agentNames) {
                    AccountInfo accountInfo1 = new AccountInfo();
                    accountInfo1.setAgentHost(tmp[0]);
                    accountInfo1.setAgentPort(Integer.valueOf(tmp[1]));
                    accountInfo1.setAgentName(agentName);
                    list.add(accountInfo1);
                }
            }
        }


        Map<String, List<AccountInfo>> result = Maps.newHashMap();
        for (Map.Entry<String, List<AccountInfo>> entry : map.entrySet()) {
            if (entry.getKey().contains(partKey)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

    public static boolean getUfisConfig() {
        Map<String, Boolean> map = new HashMap<>();
        String value = TopsConfReader.getConfContent(Config_File, Ufis_Key, TopsConfEnum.ConfScope.G);
        String[] configs = value.split(";");
        for (String config : configs) {
            String[] keyValues = config.split(":");

            map.put(keyValues[0], Boolean.valueOf(keyValues[1]));
        }

        if (map.get(partKey) == null) {
            return false;
        } else {
            return map.get(partKey) == true ? true : false;
        }
    }
}
