package com.imooc.miaosha.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.MiaoshaUserService;

@Controller
@RequestMapping("/miaoshauser")
public class MiaoshUserController {
	private static Logger log = LoggerFactory.getLogger(MiaoshUserController.class);
	@Autowired
	MiaoshaUserService miaoshaUserService;

	@Autowired
	RedisService redisService;

	@RequestMapping("/info")
	@ResponseBody
	public Result<MiaoshaUser> info(Model model, MiaoshaUser miaoshaUser) {
		return Result.success(miaoshaUser);
	}
}
