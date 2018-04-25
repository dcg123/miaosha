package com.czmec.miaosha.service;

import com.czmec.miaosha.domain.MiaoshaOrder;
import com.czmec.miaosha.domain.MiaoshaUser;
import com.czmec.miaosha.domain.OrderInfo;
import com.czmec.miaosha.redis.MiaoshaKey;
import com.czmec.miaosha.redis.RedisService;
import com.czmec.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiaoshaService {
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;

	@Autowired
	RedisService redisService;


	@Transactional
	public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
		//减库存 下订单 写入秒杀订单
		boolean success = goodsService.reduceStock(goods);
		if (success){
			//order_info maiosha_order
			return orderService.createOrder(user, goods);
		}
		else {
			setGoodsOver(goods.getId());
			return null;
		}
	}

	public long getMiaoshaResult(Long userId, long goodsId) {
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
		if (order!=null){//秒杀成功
			return order.getOrderId();
		}else {
			boolean isOver=getGoodsOver(goodsId);
			if (isOver){
				return -1;
			}else {
				return 0;
			}
		}
	}

	private void setGoodsOver(Long goodsId) {
		redisService.set(MiaoshaKey.isGoodsOver, ""+goodsId, true);
	}

	private boolean getGoodsOver(long goodsId) {
		return redisService.exists(MiaoshaKey.isGoodsOver, ""+goodsId);
	}
}
