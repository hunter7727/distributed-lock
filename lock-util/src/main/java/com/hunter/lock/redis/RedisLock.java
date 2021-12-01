package com.hunter.lock.redis;

import com.hunter.lock.Lock;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * todo:这种模式需要优化，不知道为什么这个方式qps很低
 */
//@Primary
//@Service
public class RedisLock implements Lock {

//    @Autowired
//    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redisson;

    ThreadLocal tl = new ThreadLocal();

    @Override
    public boolean lock(Object key) {
        String keyName = key+"";
        RLock lock = redisson.getLock(keyName);
        try{
            lock.lock();
        }catch (Exception e){
            return false;
        }
        Object tlo = tl.get();
        Map<String,RLock> map;
        if(tlo==null){
            map = new HashMap<>();
        }else {
            map = (Map)tlo;
        }
        map.put(keyName,lock);
        tl.set(map);
        return true;
    }

    @Override
    public boolean unLock(Object key) {
        Object o = tl.get();
        if(o!=null){
            Map<String,RLock> map = (Map)o;
           try{
               String keyName = key+"";
               RLock rLock = map.get(keyName);
               if(rLock!=null){
                   rLock.unlock();
                   map.remove(keyName);
               }
           }finally {
               if(CollectionUtils.isEmpty(map)){
                   tl.remove();
               }
           }
        }
        return true;
    }
}
