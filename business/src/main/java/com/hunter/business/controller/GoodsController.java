package com.hunter.business.controller;

import com.hunter.business.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @RequestMapping("buyByGoodsId")
    public boolean buyByGoodsId(Integer goodsId,Integer userId){
        if(goodsId == null || userId == null){
            return false;
        }
        return goodsService.buyByGoodsId(goodsId,userId);
    }
}
