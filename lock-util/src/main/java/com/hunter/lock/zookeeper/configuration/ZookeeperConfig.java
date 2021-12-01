package com.hunter.lock.zookeeper.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * zookeeper配置
 */
@Slf4j
@Configuration
public class ZookeeperConfig {

    public final static String LOCK_ROOT_PATH = "/locks";

    public final static String DEFAULT = LOCK_ROOT_PATH + "/" + "default";

    @Value("${zookeeper.address}")
    private String connectString;

    @Value("${zookeeper.timeout}")
    private int timeout;

    @Bean
    public ZooKeeper initZookeeper(){
        ZooKeeper zooKeeper = null;
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            zooKeeper = new ZooKeeper(connectString, timeout, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (Event.KeeperState.SyncConnected == event.getState()) {
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
            log.info("初始化zookeeper完成,状态：{}", zooKeeper.getState());
            //初始化分布式锁目录
            Stat rootStat = zooKeeper.exists(LOCK_ROOT_PATH, false);
            if (null == rootStat) {
                // 创建根节点
                zooKeeper.create(LOCK_ROOT_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            Stat corporationDetailStat = zooKeeper.exists(DEFAULT, false);
            if(corporationDetailStat==null){
                zooKeeper.create(DEFAULT, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            return zooKeeper;
        } catch (IOException | InterruptedException e) {
            log.error("初始化zookeeper失败：{}", e);
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        return zooKeeper;
    }
}
