package com.hunter.lock.zookeeper;

import com.hunter.lock.Lock;
import com.hunter.lock.zookeeper.configuration.ZookeeperConfig;
import com.hunter.lock.zookeeper.watcher.ZookeeperLockWatcher;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

@Primary
@Service
public class ZookeeperLock implements Lock {

    @Autowired
    private ZooKeeper zooKeeper;

    ThreadLocal tl = new ThreadLocal();

    @Override
    public boolean lock(Object key) {
        String lockName =  key+"";
        try {
            // 创建临时子节点
            String myNode = zooKeeper.create(ZookeeperConfig.LOCK_ROOT_PATH + "/" + lockName , new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);

            // 取出所有子节点
            List<String> subNodes = zooKeeper.getChildren(ZookeeperConfig.LOCK_ROOT_PATH, false);
            TreeSet<String> sortedNodes = new TreeSet<>();
            for(String node :subNodes) {
                sortedNodes.add(ZookeeperConfig.LOCK_ROOT_PATH +"/" +node);
            }

            String smallNode = sortedNodes.first();
            String preNode = sortedNodes.lower(myNode);

            if (myNode.equals( smallNode)) {
                // 如果是最小的节点,则表示取得锁
                addContext(lockName,myNode);
                return true;
            }

            CountDownLatch latch = new CountDownLatch(1);
            Stat stat = zooKeeper.exists(preNode, new ZookeeperLockWatcher(latch));// 同时注册监听。
            // 判断比自己小一个数的节点是否存在,如果不存在则无需等待锁,同时注册监听
            if (stat != null) {
                latch.await();// 等待，这里应该一直等待其他线程释放锁
                latch = null;
            }
            addContext(lockName,myNode);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean unLock(Object key) {
        String keyName = key+"";
        Object o = tl.get();
        if(o!=null){
            Map<String,String> map = (Map)o;
           try{
               String lockKey = map.get(keyName);
               if(lockKey!=null){
                   zooKeeper.delete( lockKey, -1);
                   map.remove(keyName);
               }
           }catch (Exception e){
               e.printStackTrace();
               return false;
           }finally {
               if(CollectionUtils.isEmpty(map)){
                   tl.remove();
               }
           }
        }
        return true;
    }


    private void addContext(String keyName,String lockKey){
        Object tlo = tl.get();
        Map<String, String> map;
        if(tlo==null){
            map = new HashMap<>();
        }else {
            map = (Map)tlo;
        }
        map.put(keyName,lockKey);
        tl.set(map);
    }


}
