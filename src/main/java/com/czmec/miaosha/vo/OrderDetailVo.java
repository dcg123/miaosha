package com.czmec.miaosha.vo;

import com.czmec.miaosha.domain.OrderInfo;

/**
 * @author dcg
 * Created by user on 2018/4/9.
 */
public class OrderDetailVo {
    private GoodsVo goods;
    private OrderInfo order;
    public GoodsVo getGoods() {
        return goods;
    }
    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }
    public OrderInfo getOrder() {
        return order;
    }
    public void setOrder(OrderInfo order) {
        this.order = order;
    }
}
