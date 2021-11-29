package com.hunter.business.dao;

import com.hunter.business.domain.Goods;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodsMapper {

    Goods selectById(Integer goodsId);

    void updateById(Goods goods);
}