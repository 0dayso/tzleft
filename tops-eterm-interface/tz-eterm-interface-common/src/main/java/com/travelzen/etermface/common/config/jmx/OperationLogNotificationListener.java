package com.travelzen.etermface.common.config.jmx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14-9-28
 * Time:下午5:13
 * <p/>
 * Description:notification lister，此处主要监听哪些MBean发生了变动
 */
@Component("operationLogNotificationListener")
public class OperationLogNotificationListener implements NotificationListener, NotificationFilter {
    static private Logger logger = LoggerFactory.getLogger(OperationLogNotificationListener.class);

    /**
     * 过滤Notifaction，需要处理的Notification则返回true，否者返回false
     *
     * @param notification
     * @return
     */
    @Override
    public boolean isNotificationEnabled(Notification notification) {
        return true;
    }

    /**
     * 处理Notification
     *
     * @param notification
     * @param handback
     */
    @Override
    public void handleNotification(Notification notification, Object handback) {
        logger.info("notifcation:{},handback:{}", notification, handback);
    }
}
