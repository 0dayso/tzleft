package com.travelzen;

import com.travelzen.etermface.common.config.AccountInfo;
import com.travelzen.etermface.common.config.cdxg.PoolUserFactory;
import com.travelzen.etermface.common.config.cdxg.pojo.User;
import com.travelzen.etermface.common.config.cdxg.user.impl.UserPool;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:15/3/9
 * Time:下午3:18
 * <p/>
 * Description:
 */
public class UserPoolTest {
    /**
     * 测试相同对象向对象池中归还，是否会导致归还多个
     *
     * @throws Exception
     */
    @Test
    public void test1() throws Exception {
        List<AccountInfo> cdxgUsers = new ArrayList<>();
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAgentHost("127.0.0.1");
        accountInfo.setAgentName("yangguo");
        accountInfo.setAgentPort(8080);
        cdxgUsers.add(accountInfo);
        UserPool userPool = new UserPool(new PoolUserFactory(cdxgUsers));
        User user = userPool.borrowObject();
        userPool.returnObject(user);
        user.setMaxkeepTime(new AtomicLong(111));
        userPool.returnObject(user);
        userPool.returnObject(user);
        userPool.returnObject(user);
        System.out.println(userPool.getMaxActive());
        System.out.println("------");
    }


    public static void main(String[] args) throws Exception {
        List<AccountInfo> cdxgUsers = new ArrayList<>();
        AccountInfo accountInfo1 = new AccountInfo();
        accountInfo1.setAgentHost("127.0.0.1");
        accountInfo1.setAgentName("yangguo");
        accountInfo1.setAgentPort(8080);
        cdxgUsers.add(accountInfo1);
        UserPool userPool = new UserPool(new PoolUserFactory(cdxgUsers));
        User user1 = userPool.borrowObject();
        System.out.println("active:" + userPool.getNumActive() + ",idle:" + userPool.getNumIdle());
        userPool.returnObject(user1);
        userPool.returnObject(user1);
        userPool.returnObject(user1);
        System.out.println("将借用的对象，修改MaxKeepTime之后，归还到池中");
        user1.setMaxkeepTime(new AtomicLong(111));
        userPool.returnObject(user1);
        System.out.println("active:" + userPool.getNumActive() + ",idle:" + userPool.getNumIdle());
        System.out.println("再次归还");
        userPool.returnObject(user1);
        System.out.println("active:" + userPool.getNumActive() + ",idle:" + userPool.getNumIdle());
        System.out.println("再次归还");
        userPool.returnObject(user1);
        System.out.println("active:" + userPool.getNumActive() + ",idle:" + userPool.getNumIdle());
        System.out.println("手动创建一个User对象，并尝试归还到池中");
        AccountInfo accountInfo2 = new AccountInfo();
        accountInfo2.setAgentHost("127.0.0.1");
        accountInfo2.setAgentName("yangdong");
        accountInfo2.setAgentPort(8081);
        User user2 = new User(accountInfo2);
        userPool.returnObject(user2);
        System.out.println("active:" + userPool.getNumActive() + ",idle:" + userPool.getNumIdle());
    }


    //测试LIFO
    @Test
    public void test2() throws Exception {
        List<AccountInfo> cdxgUsers = new ArrayList<>();
        AccountInfo accountInfo1 = new AccountInfo();
        accountInfo1.setAgentHost("127.0.0.1");
        accountInfo1.setAgentName("yangguo1");
        accountInfo1.setAgentPort(8080);


        AccountInfo accountInfo2 = new AccountInfo();
        accountInfo2.setAgentHost("127.0.0.1");
        accountInfo2.setAgentName("yangguo2");
        accountInfo2.setAgentPort(8080);

        AccountInfo accountInfo3 = new AccountInfo();
        accountInfo3.setAgentHost("127.0.0.1");
        accountInfo3.setAgentName("yangguo3");
        accountInfo3.setAgentPort(8080);


        cdxgUsers.add(accountInfo1);
        cdxgUsers.add(accountInfo2);
        cdxgUsers.add(accountInfo3);

        UserPool userPool = new UserPool(new PoolUserFactory(cdxgUsers));
        User user1 = userPool.borrowObject();
        User user2 = userPool.borrowObject();
        User user3 = userPool.borrowObject();

        System.out.println(user1.getAccountInfo().getAgentName());
        System.out.println(user2.getAccountInfo().getAgentName());
        System.out.println(user3.getAccountInfo().getAgentName());

        userPool.returnObject(user1);
        userPool.returnObject(user2);
        userPool.returnObject(user3);


        User user4=userPool.borrowObject();
        System.out.println(user4.getAccountInfo().getAgentName());
        userPool.returnObject(user4);

        User user5=userPool.borrowObject();
        System.out.println(user5.getAccountInfo().getAgentName());
        userPool.returnObject(user5);

    }
}
