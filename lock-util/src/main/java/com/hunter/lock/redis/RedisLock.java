package com.hunter.lock.redis;

import com.hunter.lock.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Primary
@Service
public class RedisLock implements Lock {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean lock(Object key) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, null, 10, TimeUnit.SECONDS);
        return success;
    }

    @Override
    public boolean unLock(Object key) {
        return redisTemplate.delete(key);
    }
}
