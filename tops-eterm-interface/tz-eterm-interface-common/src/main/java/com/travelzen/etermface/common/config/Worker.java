package com.travelzen.etermface.common.config;

import com.travelzen.etermface.common.config.cdxg.Converter;
import com.travelzen.etermface.common.config.cdxg.GenerateUrl;
import com.travelzen.etermface.common.config.cdxg.pojo.Ins;
import com.travelzen.etermface.common.utils.HttpClientUtil;
import com.travelzen.etermface.common.config.cdxg.pojo.User;
import org.apache.commons.lang3.StringUtils;

import com.travelzen.framework.core.exception.BizException;

import java.util.HashMap;

public class Worker {
    /**
     * @param user 资源
     * @param cmd  命令
     */
    public static String getRawResult(User user, String cmd) {
        cmd = Converter.stringToHex(cmd);
        String urlVlue = GenerateUrl.generateRawUrl(user, cmd);

        int socketTimeout = new Long(user.getMaxkeepTime().get()).intValue() - 5000;
        if (socketTimeout > 60_000) {
            socketTimeout = 60_000;
        }
        String str = HttpClientUtil.get(urlVlue, new HashMap<String, String>(), "UTF-8", "GB2312", 5000, socketTimeout);
        if (StringUtils.isBlank(str)) {
            throw BizException.instance("cmd:" + cmd + "，result is null");
        }

        Ins ins = Converter.generateIns(str, true);
        if (ins.getRetValue() == null) {
            throw BizException.instance("converter error!");
        }
        if (ins.getRetValue().equals("0")) {
            throw BizException.instance("request error!");
        }
        return ins.getRsValue();
    }

    /**
     * @param user
     * @param cmd
     * @param cmdType
     * @return
     */
    public static String getResult(User user, String cmd, String cmdType){
        String urlVlue = GenerateUrl.generateUrl(user, cmd, cmdType);
        int socketTimeout = new Long(user.getMaxkeepTime().get()).intValue() - 5000;
        if (socketTimeout > 60_000) {
            socketTimeout = 60_000;
        }
        String str = HttpClientUtil.get(urlVlue, new HashMap<String, String>(), "UTF-8", "GB2312", 5000, socketTimeout);
        if (StringUtils.isBlank(str)) {
            throw BizException.instance("cmd:" + cmd + "，result is null");
        }
        return str;
    }
}
