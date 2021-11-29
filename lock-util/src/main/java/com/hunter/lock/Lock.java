package com.hunter.lock;

public interface Lock {

    boolean lock(Object key);

    boolean unLock(Object key);
}
