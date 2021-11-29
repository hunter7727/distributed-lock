package com.hunter.lock.mysql.service;

import com.hunter.lock.Lock;
import com.hunter.lock.mysql.dao.LockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class MysqlLock implements Lock {

    @Autowired
    private LockMapper lockMapper;


    @Override
    public boolean lock(Object key) {
        if(key==null || ! (key instanceof Integer)){
            return false;
        }
        try{
            lockMapper.insert((Integer) key);
        }catch (Exception e){
            return false;
        }
        return true;
    }


    @Override
    public boolean unLock(Object key) {
        if(key==null || ! (key instanceof Integer)){
            return false;
        }
        lockMapper.delete((Integer) key);
        return true;
    }
}
