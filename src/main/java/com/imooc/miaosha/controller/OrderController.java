package com.imooc.miaosha.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;
import com.imooc.miaosha.vo.OrderDetailVo;

@Controller
@RequestMapping("/order")
public class OrderController {
	private static Logger log = LoggerFactory.getLogger(OrderController.class);
	@Autowired
	MiaoshaUserService miaoshaUserService;

	@Autowired
	RedisService redisService;

	@Autowired
	OrderService orderService;
	
	@Autowired
	GoodsService goodsService;

	@RequestMapping("/detail")
	@ResponseBody
	public Result<OrderDetailVo> miaosha(Model model, MiaoshaUser miaoshaUser, @RequestParam("orderId") long orderId) {
		if (miaoshaUser == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		
		OrderInfo order = orderService.getOrderById(orderId);
		if (order == null) {
			return Result.error(CodeMsg.ORDER_NOT_EXIST);
		}
		
		long goodsId = order.getGoodsId();
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		OrderDetailVo orderDetailVo = new OrderDetailVo();
		orderDetailVo.setGoods(goods);
		orderDetailVo.setOrder(order);
		return Result.success(orderDetailVo);
	}

}
