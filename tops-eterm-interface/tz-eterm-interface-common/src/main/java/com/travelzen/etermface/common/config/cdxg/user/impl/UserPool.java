package com.travelzen.etermface.common.config.cdxg.user.impl;

import com.google.common.collect.Sets;
import com.travelzen.etermface.common.config.cdxg.PoolUserFactory;
import com.travelzen.etermface.common.config.cdxg.pojo.User;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class UserPool extends GenericObjectPool<User> {
    private static Logger logger = LoggerFactory.getLogger(UserPool.class);
    //正在被使用的配置，禁止指令重排，让线程对set进行修改之后，要立刻回写到主内存。
    //线程对变量读取的时候，要从主内存中读，而不是缓存。
    private volatile Set<Integer> set = Sets.newConcurrentHashSet();

    public UserPool(PoolUserFactory factory, GenericObjectPool.Config config) {
        super(factory, config);
    }

    public UserPool(PoolUserFactory factory) {
        super(factory);
        this.setLifo(false);
        this.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
        this.setTestOnReturn(true);
        this.setTestOnBorrow(false);
    }

    @Override
    public User borrowObject() throws Exception {
        User user = super.borrowObject();
        set.add(user.hashCode());
        logger.info("borrow object from user pool");
        return user;
    }

    @Override
    public void returnObject(User user) throws Exception {
        if (set.contains(user.hashCode())) {
            //千万别颠倒这两步的顺序
            set.remove(user.hashCode());
            super.returnObject(user);
            logger.info("return object to user pool");
        } else {
            logger.warn("object exist in the pool");
        }
    }
}
