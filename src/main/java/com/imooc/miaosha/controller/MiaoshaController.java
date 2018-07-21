package com.imooc.miaosha.controller;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.miaosha.access.AccessLimit;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.rabbitmq.MQSender;
import com.imooc.miaosha.rabbitmq.MiaoshaMessage;
import com.imooc.miaosha.redis.AccessKey;
import com.imooc.miaosha.redis.GoodsKey;
import com.imooc.miaosha.redis.MiaoshaKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.GoodsVo;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {
	private static Logger log = LoggerFactory.getLogger(MiaoshaController.class);
	@Autowired
	MiaoshaUserService miaoshaUserService;

	@Autowired
	RedisService redisService;

	@Autowired
	GoodsService goodsService;

	@Autowired
	OrderService orderService;

	@Autowired
	MiaoshaService miaoshaService;

	@Autowired
	MQSender mqSender;

	private Map<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

	@RequestMapping(value = "/{path}/do_miaosha", method = RequestMethod.POST)
	@ResponseBody
	public Result<Integer> miaosha(Model model, MiaoshaUser miaoshaUser, @RequestParam("goodsId") long goodsId,
			@PathVariable("path") String path) {
		model.addAttribute("miaoshaUser", miaoshaUser);
		if (miaoshaUser == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}

		boolean check = miaoshaService.checkPath(miaoshaUser, goodsId, path);
		if (!check) {
			return Result.error(CodeMsg.REQUEST_ILLEGAL);
		}

		Boolean over = localOverMap.get(goodsId);
		if (over) {
			return Result.error(CodeMsg.MIAOSHA_OVER);
		}

		Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
		if (stock < 0) {
			localOverMap.put(goodsId, true);
			return Result.error(CodeMsg.MIAOSHA_OVER);
		}

		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(miaoshaUser.getId(), goodsId);
		if (order != null) {
			return Result.error(CodeMsg.REPEAT_MIAOSHA);
		}
		MiaoshaMessage mm = new MiaoshaMessage();
		mm.setMiaoshaUser(miaoshaUser);
		mm.setGoodsId(goodsId);
		mqSender.sendMiaoshaMessage(mm);
		return Result.success(0);

		/*
		 * GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId); int
		 * stockCount = goods.getStockCount(); if (stockCount <= 0) { return
		 * Result.error(CodeMsg.MIAOSHA_OVER); }
		 * 
		 * MiaoshaOrder order =
		 * orderService.getMiaoshaOrderByUserIdGoodsId(miaoshaUser.getId(),
		 * goodsId); // log.info(order.toString()); if (order != null) { return
		 * Result.error(CodeMsg.REPEAT_MIAOSHA); }
		 * 
		 * OrderInfo orderInfo = miaoshaService.miaosha(miaoshaUser, goods);
		 * return Result.success(orderInfo);
		 */
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		if (goodsList == null) {
			return;
		}
		for (GoodsVo goods : goodsList) {
			redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goods.getId(), goods.getStockCount());
			localOverMap.put(goods.getId(), false);
		}
	}

	@AccessLimit(seconds = 5, maxCount = 10, needLogin = true)
	@RequestMapping(value = "/result", method = RequestMethod.GET)
	@ResponseBody
	public Result<Long> miaoshaResult(Model model, MiaoshaUser miaoshaUser, @RequestParam("goodsId") long goodsId) {
		model.addAttribute("miaoshaUser", miaoshaUser);
		if (miaoshaUser == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		long result = miaoshaService.getMiaoshaResult(miaoshaUser.getId(), goodsId);
		return Result.success(result);
	}

	@AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
	@RequestMapping(value = "/path", method = RequestMethod.GET)
	@ResponseBody
	public Result<String> getMiaoshaPath(HttpServletRequest request, MiaoshaUser miaoshaUser,
			@RequestParam("goodsId") long goodsId,
			@RequestParam(value = "verifyCode", defaultValue = "0") int verifyCode) {
		if (miaoshaUser == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}

		boolean check = miaoshaService.checkVerifyCode(miaoshaUser, goodsId, verifyCode);
		if (!check) {
			return Result.error(CodeMsg.REQUEST_ILLEGAL);
		}
		String path = miaoshaService.createMiaoshaPath(miaoshaUser, goodsId);
		// String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
		// redisService.set(MiaoshaKey.getMiaoshaPath, "" + miaoshaUser.getId()
		// + "_" + goodsId, str);
		return Result.success(path);
	}

	@RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
	@ResponseBody
	public Result<String> getMiaoshaVerifyCode(HttpServletResponse response, MiaoshaUser miaoshaUser,
			@RequestParam("goodsId") long goodsId) {
		if (miaoshaUser == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		BufferedImage image = miaoshaService.createVerifyCode(miaoshaUser, goodsId);
		try {
			OutputStream output = response.getOutputStream();
			ImageIO.write(image, "JPEG", output);
			output.flush();
			output.close();
			return null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return Result.error(CodeMsg.MIAOSHA_FAIL);
		}
	}

}
