package com.imooc.miaosha.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.vo.LoginVo;

@Controller
@RequestMapping("/login")
public class LoginController {
	private static Logger log = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	MiaoshaUserService miaoshaUserService;
	@Autowired
	RedisService redisService;

	@RequestMapping("/to_login")
	public String toLogin() {
		return "login";
	}

	@RequestMapping("/do_login")
	@ResponseBody
	public Result<String> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
		log.info(loginVo.toString());
//		String mobile = loginVo.getMobile();
//		String password = loginVo.getPassword();
//		if (StringUtils.isEmpty(password)) {
//			return Result.error(CodeMsg.PASSWORD_EMPTY);
//		}
//		if (StringUtils.isEmpty(mobile)) {
//			return Result.error(CodeMsg.MOBILE_EMPTY);
//		}
//		if (!ValidatorUtil.isMobile(mobile)) {
//			return Result.error(CodeMsg.MOBILE_ERROR);
//		}
		String token = miaoshaUserService.login(response,loginVo);
//		if (msg.getCode() == 0) {
//			return Result.success(CodeMsg.SUCCESS);
//		} else {
//			return Result.error(msg);
//		}
		return Result.success(token);
	}

}
