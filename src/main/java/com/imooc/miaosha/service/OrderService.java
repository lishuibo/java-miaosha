package com.imooc.miaosha.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.miaosha.dao.OrderDao;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.OrderKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.vo.GoodsVo;

@Service
public class OrderService {
	@Autowired
	OrderDao orderDao;
	
	@Autowired
	RedisService redisService;

	public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId) {
		// TODO Auto-generated method stub
//		return orderDao.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
		return redisService.get(OrderKey.getMiaoshaOrderByUidGid, ""+userId+"_"+goodsId, MiaoshaOrder.class);
	}

	@Transactional
	public OrderInfo createOrder(MiaoshaUser miaoshaUser, GoodsVo goods) {
		// TODO Auto-generated method stub
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setCreateDate(new Date());
		orderInfo.setDeliveryAddrId(0L);
		orderInfo.setGoodsCount(1);
		orderInfo.setGoodsId(goods.getId());
		orderInfo.setGoodsName(goods.getGoodsName());
		orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
		orderInfo.setOrderChannel(1);
		orderInfo.setStatus(0);
		orderInfo.setUserId(miaoshaUser.getId());
		
		orderDao.insert(orderInfo);
		MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
		miaoshaOrder.setGoodsId(goods.getId());
		miaoshaOrder.setOrderId(orderInfo.getId());
		miaoshaOrder.setUserId(miaoshaUser.getId());
		orderDao.insertMiaoshaOrder(miaoshaOrder);
		
		redisService.set(OrderKey.getMiaoshaOrderByUidGid, ""+miaoshaUser.getId()+"_"+goods.getId(), miaoshaOrder);
		
		return orderInfo;
	}

	public OrderInfo getOrderById(long orderId) {
		// TODO Auto-generated method stub
		return orderDao.getOrderById(orderId);
	}

}
