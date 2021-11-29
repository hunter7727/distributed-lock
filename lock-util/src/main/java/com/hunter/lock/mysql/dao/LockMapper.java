package com.hunter.lock.mysql.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LockMapper {

    void insert(Integer id);

    void delete(Integer id);
}
