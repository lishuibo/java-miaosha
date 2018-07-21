package com.imooc.miaosha.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;

@Service
public class MQReceiver {
	@Autowired
	RedisService redisService;

	@Autowired
	GoodsService goodsService;

	@Autowired
	OrderService orderService;

	@Autowired
	MiaoshaService miaoshaService;
	private static Logger log = LoggerFactory.getLogger(MQReceiver.class);
	
	@RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
	public void receive(String message) {
		log.info("receive message:" + message);
		MiaoshaMessage mm = RedisService.stringToBean(message, MiaoshaMessage.class);
		MiaoshaUser miaoshaUser = mm.getMiaoshaUser();
		long goodsId = mm.getGoodsId();
		
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		int stock = goods.getStockCount();
		if (stock < 0) {
			return;
		}

		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(miaoshaUser.getId(), goodsId);
		if (order != null) {
			return;
		}
		
		miaoshaService.miaosha(miaoshaUser, goods);
	}

//	@RabbitListener(queues = MQConfig.QUEUE)
//	public void receive(String message) {
//		log.info("receive message:" + message);
//	}
//
//	@RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
//	public void receiveTopic1(String message) {
//		log.info("receive topic queue1 message:" + message);
//	}
//
//	@RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
//	public void receiveTopic2(String message) {
//		log.info("receive topic queue2 message:" + message);
//	}
//
//	@RabbitListener(queues = MQConfig.HEADER_QUEUE)
//	public void receiveHeaderQueue(byte[] message) {
//		log.info("receive header queue message:" + new String(message));
//	}
}
