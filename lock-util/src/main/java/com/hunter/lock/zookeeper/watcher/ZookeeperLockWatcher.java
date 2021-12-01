package com.hunter.lock.zookeeper.watcher;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

public class ZookeeperLockWatcher implements Watcher {

    private CountDownLatch latch;

    public ZookeeperLockWatcher(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeDeleted)
            latch.countDown();
    }
}
