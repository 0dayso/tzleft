package com.travelzen.etermface.common.config.cdxg;

import java.util.List;

import com.google.common.collect.Lists;
import com.travelzen.etermface.common.config.AccountInfo;
import org.apache.commons.pool.PoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.etermface.common.config.cdxg.pojo.User;

/**
 * User工厂
 */
public class PoolUserFactory implements PoolableObjectFactory<User> {
    private static Logger logger = LoggerFactory.getLogger(PoolUserFactory.class);

    List<AccountInfo> allCdxgUsers;
    List<AccountInfo> availableCdxgUsers;

    public PoolUserFactory(List<AccountInfo> cdxgUsers) {
        super();
        this.allCdxgUsers = cdxgUsers;
        this.availableCdxgUsers = Lists.newArrayList(cdxgUsers);
    }

    @Override
    public User makeObject() throws Exception {
        if (availableCdxgUsers.size() > 0) {
            AccountInfo accountInfo = availableCdxgUsers.remove(0);
            User user = new User(accountInfo);
            logger.info("工厂创建对象成功：{}", user);
            return user;
        } else {
            logger.error("用户资源已经耗光，请设置合适的池容量。");
            return null;
        }
    }

    @Override
    public void destroyObject(User obj) throws Exception {
    }

    @Override
    public boolean validateObject(User user) {
        if (!allCdxgUsers.contains(user.getAccountInfo())) {
            logger.error("不能将非配置对象放入池中。");
            return false;
        }
        return true;
    }

    @Override
    public void activateObject(User user) throws Exception {
    }

    @Override
    public void passivateObject(User user) throws Exception {
    }
}
