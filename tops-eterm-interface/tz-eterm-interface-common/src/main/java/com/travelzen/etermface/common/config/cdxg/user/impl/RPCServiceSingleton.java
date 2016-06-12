package com.travelzen.etermface.common.config.cdxg.user.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.travelzen.etermface.common.config.AccountInfo;
import com.travelzen.etermface.common.config.ConfigUtil;
import com.travelzen.etermface.common.config.Worker;
import com.travelzen.etermface.common.config.cdxg.PoolUserFactory;
import com.travelzen.etermface.common.config.cdxg.exception.EtermException;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.common.config.cdxg.pojo.User;
import com.travelzen.etermface.common.config.EtermProtocol;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.travelzen.etermface.common.config.cdxg.CdxgConstant;
import com.travelzen.framework.core.common.ReturnCode;
import com.travelzen.framework.core.util.TZUtil;

/**
 * 注意，该类千万别随意改动，否者非常容易导致配置不安全
 */
public class RPCServiceSingleton {
    private static Logger logger = LoggerFactory.getLogger(RPCServiceSingleton.class);
    private static RPCServiceSingleton instance;
    //借用User的map
    private static volatile Map<String, UserPool> getUserMap = Maps.newConcurrentMap();
    //所有已经创建的User和包含该对象的池的映射
    private static volatile Map<User, UserPool> allUserMap = Maps.newConcurrentMap();
    //记录当前线程使用的User和使用的开始时间。时间的作用，由于对象资源没有排它锁，当前线程的资源被资源被回收器回收之后，别的线程修改了开始时间，
    // 而当前线程还持有该对象，并且还需要执行指令，此时如果不判断时间，就可能存在上下文不能保持的问题。
    private static ThreadLocal<Pair<User, Long>> threadLocal = new ThreadLocal<>();
    //放在此处是为了让zk日志不必等到RPCServiceSingleton初始化完成才打印，如果没有流量进来，ops会持续报警
    private static final Map<String, List<AccountInfo>> multiOfficeConfig = ConfigUtil.getConfig();

    private RPCServiceSingleton() {
        GenericObjectPool.Config poolConfig = new GenericObjectPool.Config();
        poolConfig.lifo = false;
        poolConfig.testOnReturn = true;
        poolConfig.testOnBorrow = false;

        for (String key : multiOfficeConfig.keySet()) {
            //创建工厂
            List<AccountInfo> accountInfos = multiOfficeConfig.get(key);
            PoolUserFactory factory = new PoolUserFactory(accountInfos);
            //创建对象池
            UserPool userPool = new UserPool(factory, poolConfig);
            userPool.setMaxActive(accountInfos.size());
            //设置对象池，最多等待12秒
            userPool.setMaxWait(12000);
            getUserMap.put(key, userPool);
            logger.info("对象池{}创建成功", key);
        }
    }


    public static RPCServiceSingleton getInstance() {
        if (instance == null) {
            synchronized (RPCServiceSingleton.class) {
                if (instance == null) {
                    instance = new RPCServiceSingleton();
                }
            }
        }
        return instance;
    }


    public void extendSessionExpireMillsec(int millsec, String reason) throws SessionExpireException {
        Pair<User, Long> cacheUserInfo = threadLocal.get();
        if (cacheUserInfo != null) {
            if ((System.currentTimeMillis() - cacheUserInfo.getValue0().getBeginTime().get() < cacheUserInfo.getValue0().getMaxkeepTime().get()) && (cacheUserInfo.getValue0().getBeginTime().get() == cacheUserInfo.getValue1())) {
                long maxKeepTime = cacheUserInfo.getValue0().getMaxkeepTime().get();

                if (maxKeepTime + millsec > CdxgConstant.MAX_LOCK_EXTEND_MILLSEC) {
                    logger.warn("调用方由于：{}，尝试将当前线程占用账号资源的时间延长为：{}，大于了最大的延长时长：{} ", reason, maxKeepTime + millsec, CdxgConstant.MAX_LOCK_EXTEND_MILLSEC);
                    return;
                }

                cacheUserInfo.getValue0().getMaxkeepTime().getAndAdd(millsec);
                logger.info("调用方由于：{}，当前线程账号资源占用的时间延长为：{}", reason, maxKeepTime + millsec);
            } else {
                throw new SessionExpireException();
            }
        } else {
            logger.warn("该线程上的session已经关闭或者没有开启");
        }
    }

    public static void checkTxnTimeout() {
        logger.info("对象池回收器开始执行");
        for (Map.Entry<User, UserPool> entry : allUserMap.entrySet()) {
            User user = entry.getKey();
            if (user.getBeginTime().get() != 0L && (System.currentTimeMillis() - user.getBeginTime().get() > user.getMaxkeepTime().get())) {
                UserPool userPool = entry.getValue();
                try {
                    user.setBeginTime(new AtomicLong(0L));
                    userPool.returnObject(user);
                    logger.info("对象{}被对象池回收器回收", user);
                } catch (Exception e) {
                    logger.error("致命异常，未能成功将配置归还到池中。{}", TZUtil.stringifyException(e));
                }
            }
        }
    }

    public void openSession(String officeId, int sessionExpireMillsec) throws EtermException {
        openSession(officeId, EtermProtocol.cdxg, sessionExpireMillsec);
    }

    public void openSession(String officeId, EtermProtocol protocol, int sessionExpireMillsec) throws EtermException {
        Pair<User, Long> cacheUserInfo = threadLocal.get();
        if (cacheUserInfo != null) {
            //限定一个线程只能获取一个配置，如果尝试多次获取session便会抛出该异常
            logger.warn("别在一个线程中多次openSession");
        } else {
            if (sessionExpireMillsec > CdxgConstant.MAX_LOCK_KEEP_MILLSEC) {
                logger.warn("调用方尝试占用账号资源的时间为：{}，大于了允许占用的最长时间：{} ", sessionExpireMillsec, CdxgConstant.MAX_LOCK_KEEP_MILLSEC);
                sessionExpireMillsec = CdxgConstant.MAX_LOCK_KEEP_MILLSEC;
            }
            String key = ConfigUtil.partKey + "-" + officeId + "-" + protocol.name();
            UserPool userPool = getUserMap.get(key);

            if (userPool == null) {
                logger.error("不存在该Office的配置：{}", officeId);
                EtermException etermException = new EtermException(ReturnCode.ERROR);
                etermException.setRetMsg("不存在该Office的配置：" + officeId);
                etermException.setObject(new Exception());

                throw etermException;
            }
            
            logger.info("对象池{}，MaxActive：{}，NumActive：{} ", key, userPool.getMaxActive(), userPool.getNumActive());

            try {
                User user = userPool.borrowObject();
                long beginTime = System.currentTimeMillis();
                user.setBeginTime(new AtomicLong(beginTime));
                user.setMaxkeepTime(new AtomicLong(sessionExpireMillsec));

                allUserMap.put(user, userPool);
                threadLocal.remove();
                threadLocal.set(Pair.with(user, beginTime));
                logger.info("session：{}新建成功，所属对象池：{}", user, key);
            } catch (Exception e) {
                logger.error("未能获取到资源，{}", e);
                EtermException etermException = new EtermException(ReturnCode.ERROR);
                etermException.setRetMsg("服务器繁忙，无可用的资源");
                etermException.setObject(e);

                throw etermException;
            }
        }
    }

    public void closeSession() {
        Pair<User, Long> cacheUserInfo = threadLocal.get();
        if (cacheUserInfo != null) {
            threadLocal.remove();
            if ((System.currentTimeMillis() - cacheUserInfo.getValue0().getBeginTime().get() < cacheUserInfo.getValue0().getMaxkeepTime().get()) && (cacheUserInfo.getValue0().getBeginTime().get() == cacheUserInfo.getValue1())) {
                try {
                    User user = cacheUserInfo.getValue0();
                    user.setBeginTime(new AtomicLong(0L));
                    allUserMap.get(user).returnObject(user);
                    logger.info("Session:{},返回对象池成功", cacheUserInfo);
                } catch (Exception e) {
                    logger.error("致命异常,未能成功将配置归还到池中.{}", TZUtil.stringifyException(e));
                }
                Worker.getRawResult(cacheUserInfo.getValue0(), "IG");
                logger.info("Session:{},IG命令执行成功", cacheUserInfo);
                logger.info("session:{},关闭成功", cacheUserInfo);
            } else {
                logger.warn("该线程上的session已经关闭");
            }
        }
    }

    /**
     * 执行原始指令
     *
     * @param cmd
     * @return
     * @throws SessionExpireException
     */
    public String getRawResult(String cmd) throws SessionExpireException {
        String result = "";
        Pair<User, Long> cacheUserInfo = threadLocal.get();
        if (cacheUserInfo != null) {
            //判断User的持有者是否没有发生转变且没有超时
            if ((System.currentTimeMillis() - cacheUserInfo.getValue0().getBeginTime().get() < cacheUserInfo.getValue0().getMaxkeepTime().get()) && (cacheUserInfo.getValue0().getBeginTime().get() == cacheUserInfo.getValue1())) {
                result = Worker.getRawResult(cacheUserInfo.getValue0(), cmd);

                if (StringUtils.trim(result).startsWith("NEED EOT")) {
                    Worker.getRawResult(cacheUserInfo.getValue0(), "IG");
                    logger.info("由于NEED EOT,执行IG命令成功", cacheUserInfo);
                    result = Worker.getRawResult(cacheUserInfo.getValue0(), cmd);
                }
            } else {
                throw new SessionExpireException();
            }
        } else {
            logger.error("没有session可用，请创建Session");
        }
        logger.info("指令结果为：{}", result);
        return result;
    }

    /**
     * 执行包装过的定制指令
     *
     * @param cmd
     * @param cmdType
     * @return
     * @throws SessionExpireException
     */
    public String getWrapResult(String cmd, String cmdType) throws SessionExpireException {
        String result = "";
        Pair<User, Long> cacheUserInfo = threadLocal.get();
        if (cacheUserInfo != null) {
            if ((System.currentTimeMillis() - cacheUserInfo.getValue0().getBeginTime().get() < cacheUserInfo.getValue0().getMaxkeepTime().get()) && (cacheUserInfo.getValue0().getBeginTime().get() == cacheUserInfo.getValue1())) {
                result = Worker.getResult(cacheUserInfo.getValue0(), cmd, cmdType);

                if (StringUtils.trim(result).startsWith("NEED EOT")) {
                    Worker.getRawResult(cacheUserInfo.getValue0(), "IG");
                    logger.info("由于NEED EOT,执行IG命令成功", cacheUserInfo);
                    result = Worker.getResult(cacheUserInfo.getValue0(), cmd, cmdType);
                }
            } else {
                throw new SessionExpireException();
            }
        } else {
            logger.error("没有session可用，请创建Session");
        }
        logger.info("指令结果为：{}", result);
        return result;
    }
}
