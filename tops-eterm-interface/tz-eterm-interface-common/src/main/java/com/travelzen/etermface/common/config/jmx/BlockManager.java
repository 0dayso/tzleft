package com.travelzen.etermface.common.config.jmx;

import org.springframework.jmx.export.annotation.*;
import org.springframework.jmx.export.notification.NotificationPublisher;
import org.springframework.jmx.export.notification.NotificationPublisherAware;
import org.springframework.stereotype.Component;

import javax.management.Notification;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14-9-28
 * Time:下午12:46
 * <p/>
 * Description:指令防阻塞MBean
 */
@ManagedResource(objectName = "eterm.jmx:name=blockManager", description = "防阻塞管理")
@Component("blockManager")
public class BlockManager implements NotificationPublisherAware {
    int sequence = 0;
    private NotificationPublisher publisher;
    private static HashMap map = new HashMap();

    @ManagedAttribute(description = "获取规则")
    public String getRules() {
        return map.toString();
    }

    @ManagedOperation(description = "规则设置")
    @ManagedOperationParameter(name = "rule", description = "rule规则")
    public boolean setRule(String rule) {
        String[] value = rule.split("=");
        map.put(value[0], value[1]);
        this.publisher.sendNotification(new Notification("add", rule, sequence++, "rule发生了变更"));
        return true;
    }

    @Override
    public void setNotificationPublisher(NotificationPublisher notificationPublisher) {
        this.publisher = notificationPublisher;
    }
}
