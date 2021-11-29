package com.hunter.business.service;

import com.hunter.business.dao.GoodsMapper;
import com.hunter.business.domain.Goods;
import com.hunter.lock.Lock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private Lock lockService;


    public boolean buyByGoodsId(Integer goodsId, Integer userId) {
        boolean lock = lockService.lock(userId);
        if(lock){
            log.info(userId+":加锁成功");
            try{
                Goods goods = goodsMapper.selectById(goodsId);
                if(goods == null){
                    return false;
                }
                Integer number = goods.getNumber();
                if(number>0){
                    goods.setNumber(--number);
                    goodsMapper.updateById(goods);
                    return true;
                }
            }finally {
                lockService.unLock(userId);
            }
        }else {
            log.error(userId+":加锁失败");
        }
        return false;
    }
}
