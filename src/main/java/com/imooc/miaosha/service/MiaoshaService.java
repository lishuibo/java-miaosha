package com.imooc.miaosha.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.codehaus.groovy.reflection.stdclasses.IntegerCachedClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.MiaoshaKey;
import com.imooc.miaosha.redis.MiaoshaUserKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.GoodsVo;

@Service
public class MiaoshaService {
	@Autowired
	GoodsService goodsService;

	@Autowired
	OrderService orderService;

	@Autowired
	RedisService redisService;

	@Transactional
	public OrderInfo miaosha(MiaoshaUser miaoshaUser, GoodsVo goods) {
		// TODO Auto-generated method stub
		boolean success = goodsService.reduceStock(goods);
		if (success) {
			return orderService.createOrder(miaoshaUser, goods);
		} else {
			setGoodsOver(goods.getId());
			return null;
		}

	}

	public long getMiaoshaResult(Long userId, long goodsId) {
		// TODO Auto-generated method stub
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
		if (order != null) {
			return order.getOrderId();
		} else {
			boolean isOver = getGoodsOver(goodsId);
			if (isOver) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	private void setGoodsOver(Long goodsId) {
		// TODO Auto-generated method stub
		redisService.set(MiaoshaKey.isGoodOver, "" + goodsId, true);
	}

	private boolean getGoodsOver(long goodsId) {
		// TODO Auto-generated method stub
		return redisService.exists(MiaoshaKey.isGoodOver, "" + goodsId);
	}

	public boolean checkPath(MiaoshaUser miaoshaUser, long goodsId, String path) {
		if (miaoshaUser == null || path == null) {
			return false;
		}
		// TODO Auto-generated method stub
		String old_path = redisService.get(MiaoshaKey.getMiaoshaPath, "" + miaoshaUser.getId() + "_" + goodsId,
				String.class);
		return path.equals(old_path);
	}

	public String createMiaoshaPath(MiaoshaUser miaoshaUser, long goodsId) {
		// TODO Auto-generated method stub
		if (miaoshaUser == null || goodsId <= 0) {
			return null;
		}
		String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
		redisService.set(MiaoshaKey.getMiaoshaPath, "" + miaoshaUser.getId() + "_" + goodsId, str);
		return str;
	}

	public BufferedImage createVerifyCode(MiaoshaUser miaoshaUser, long goodsId) {
		// TODO Auto-generated method stub
		if (miaoshaUser == null || goodsId <= 0) {
			return null;
		}
		int width = 80;
		int height = 32;
		// create the image
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		// set the background color
		g.setColor(new Color(0xDCDCDC));
		g.fillRect(0, 0, width, height);
		// draw the border
		g.setColor(Color.black);
		g.drawRect(0, 0, width - 1, height - 1);
		// create a random instance to generate the codes
		Random rdm = new Random();
		// make some confusion
		for (int i = 0; i < 50; i++) {
			int x = rdm.nextInt(width);
			int y = rdm.nextInt(height);
			g.drawOval(x, y, 0, 0);
		}
		// generate a random code
		String verifyCode = generateVerifyCode(rdm);
		g.setColor(new Color(0, 100, 0));
		g.setFont(new Font("Candara", Font.BOLD, 24));
		g.drawString(verifyCode, 8, 24);
		g.dispose();
		// 把验证码存到redis中
		int rnd = calc(verifyCode);
		redisService.set(MiaoshaKey.getMiaoshaVerifyCode, miaoshaUser.getId() + "," + goodsId, rnd);
		// 输出图片
		return image;
	}

	public static void main(String[] args) {
		System.out.println(calc("1+3-8"));
	}

	private static int calc(String exp) {
		// TODO Auto-generated method stub
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("JavaScript");
			return (Integer) engine.eval(exp);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return 0;
	}

	private static char[] ops = new char[] { '+', '-', '*' };

	private String generateVerifyCode(Random rdm) {
		// TODO Auto-generated method stub
		int num1 = rdm.nextInt(10);
		int num2 = rdm.nextInt(10);
		int num3 = rdm.nextInt(10);
		char op1 = ops[rdm.nextInt(3)];
		char op2 = ops[rdm.nextInt(3)];
		String exp = "" + num1 + op1 + num2 + op2 + num3;
		return exp;
	}

	public boolean checkVerifyCode(MiaoshaUser miaoshaUser, long goodsId, int verifyCode) {
		// TODO Auto-generated method stub
		if (miaoshaUser == null || goodsId <= 0) {
			return false;
		}
		Integer old_code = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, miaoshaUser.getId() + "," + goodsId,
				Integer.class);

		if (old_code == null || old_code - verifyCode != 0) {
			return false;
		}
		redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, miaoshaUser.getId() + "," + goodsId);
		return true;
	}

}
