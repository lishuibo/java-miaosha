package com.imooc.miaosha.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import com.alibaba.druid.util.StringUtils;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.redis.GoodsKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.vo.GoodsDetailVo;
import com.imooc.miaosha.vo.GoodsVo;

@Controller
@RequestMapping("/goods")
public class GoodsController {
	private static Logger log = LoggerFactory.getLogger(GoodsController.class);
	@Autowired
	MiaoshaUserService miaoshaUserService;

	@Autowired
	RedisService redisService;

	@Autowired
	GoodsService goodsService;

	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;

	@Autowired
	ApplicationContext applicationContext;

	@RequestMapping(value = "/to_list", produces = "text/html")
	@ResponseBody
	public String toLogin(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser miaoshaUser) {
		model.addAttribute("miaoshaUser", miaoshaUser);

		String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
		if (!StringUtils.isEmpty(html)) {
			return html;
		}
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		model.addAttribute("goodsList", goodsList);
		// return "goods_list";

		SpringWebContext context = new SpringWebContext(request, response, request.getServletContext(),
				request.getLocale(), model.asMap(), applicationContext);
		html = thymeleafViewResolver.getTemplateEngine().process("goods_list", context);
		if (!StringUtils.isEmpty(html)) {
			redisService.set(GoodsKey.getGoodsList, "", html);
		}
		return html;
	}

	@RequestMapping(value = "/to_detail/{goodsId}", produces = "text/html")
	@ResponseBody
	public String to_detail(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser miaoshaUser,
			@PathVariable("goodsId") long goodsId) {
		model.addAttribute("miaoshaUser", miaoshaUser);

		String html = redisService.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);
		if (!StringUtils.isEmpty(html)) {
			return html;
		}

		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		model.addAttribute("goods", goods);

		long start_time = goods.getStartDate().getTime();
		long end_time = goods.getEndDate().getTime();
		long current_time = System.currentTimeMillis();

		int miaoshaStatus = 0;
		int remainSeconds = 0;

		if (current_time < start_time) {
			miaoshaStatus = 0;
			remainSeconds = (int) ((start_time - current_time) / 1000);
		} else if (current_time > end_time) {
			miaoshaStatus = 2;
			remainSeconds = -1;
		} else {
			miaoshaStatus = 1;
			remainSeconds = 0;
		}

		model.addAttribute("miaoshaStatus", miaoshaStatus);
		model.addAttribute("remainSeconds", remainSeconds);
		// return "goods_detail";

		SpringWebContext context = new SpringWebContext(request, response, request.getServletContext(),
				request.getLocale(), model.asMap(), applicationContext);
		html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", context);
		if (!StringUtils.isEmpty(html)) {
			redisService.set(GoodsKey.getGoodsDetail, "" + goodsId, html);
		}
		return html;
	}

	@RequestMapping(value = "/detail/{goodsId}")
	@ResponseBody
	public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model,
			MiaoshaUser miaoshaUser, @PathVariable("goodsId") long goodsId) {
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

		long start_time = goods.getStartDate().getTime();
		long end_time = goods.getEndDate().getTime();
		long current_time = System.currentTimeMillis();

		int miaoshaStatus = 0;
		int remainSeconds = 0;

		if (current_time < start_time) {
			miaoshaStatus = 0;
			remainSeconds = (int) ((start_time - current_time) / 1000);
		} else if (current_time > end_time) {
			miaoshaStatus = 2;
			remainSeconds = -1;
		} else {
			miaoshaStatus = 1;
			remainSeconds = 0;
		}

		GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
		goodsDetailVo.setGoods(goods);
		goodsDetailVo.setMiaoshaUser(miaoshaUser);
		goodsDetailVo.setMiaoshaStatus(miaoshaStatus);
		goodsDetailVo.setRemainSeconds(remainSeconds);
		return Result.success(goodsDetailVo);
	}
}
