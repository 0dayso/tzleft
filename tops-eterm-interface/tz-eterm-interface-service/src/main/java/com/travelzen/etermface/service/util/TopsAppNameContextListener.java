package com.travelzen.etermface.service.util;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import com.travelzen.etermface.common.config.TomcatParser;
import com.travelzen.framework.config.tops.util.TopsAppRegistry;
import com.travelzen.framework.core.util.StringUtil;
import com.travelzen.framework.logger.core.util.IpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TopsAppNameContextListener implements ServletContextListener {
    public final static String SERVER_HTTP_PROTOCOL = "HTTP/1.1"; //应用通信所用的服务器协议

    public final static String SERVER_HTTP_SCHEME = "http"; //应用调用的服务器schema

    @Override
    public void contextInitialized(ServletContextEvent event) {
        changeAppName();

        String appName = event.getServletContext().getInitParameter("appName");
        if (StringUtils.isNotBlank(appName)) {
            TopsAppRegistry.setApplicationName(StringUtil.trim(appName));
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    /**
     * 修改日志配置
     */
    private void changeAppName() {
        String port = TomcatParser.getHttpPort(SERVER_HTTP_PROTOCOL, SERVER_HTTP_SCHEME);
        String appName = "tz-eterm-interface-web";
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();
        loggerContext.putProperty("APP_NAME", appName);
        loggerContext.putProperty("IP", IpUtil.getIP());
        loggerContext.putProperty("PORT", port);
        ContextInitializer ci = new ContextInitializer(loggerContext);
        try {
            ci.autoConfig();
        } catch (JoranException e) {
            e.printStackTrace();
        }
    }
}
