package com.imooc.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.miaosha.domain.User;
import com.imooc.miaosha.rabbitmq.MQSender;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.redis.UserKey;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.UserService;

@Controller
@RequestMapping("/demo")
public class DemoController {
	@Autowired
	UserService userService;
	@Autowired
	RedisService redisService;
	@Autowired
	MQSender MQSender;
	
//	@RequestMapping("/mq")
//	@ResponseBody
//	public Result<String> mq() {
//		MQSender.send("hello world");
//		return Result.success("hello mq");
//	}
//	
//	@RequestMapping("/mq/topic")
//	@ResponseBody
//	public Result<String> mqtopic() {
//		MQSender.sendTopic("hello world");
//		return Result.success("hello mq topic");
//	}
//
//	@RequestMapping("/mq/fanout")
//	@ResponseBody
//	public Result<String> mqfanout() {
//		MQSender.sendFanout("hello world");
//		return Result.success("hello mq fanout");
//	}
//	
//	@RequestMapping("/mq/header")
//	@ResponseBody
//	public Result<String> mqheader() {
//		MQSender.sendHeader("hello world");
//		return Result.success("hello mq header");
//	}
	
	@RequestMapping("/")
	@ResponseBody
	String home() {
		return "Hello World";
	}

	@RequestMapping("/hello")
	@ResponseBody
	public Result<String> hello() {
		return Result.success("hello");
	}

	@RequestMapping("/helloError")
	@ResponseBody
	public Result<String> helloError() {
		return Result.error(CodeMsg.SERVER_ERROR);
	}

	@RequestMapping("/thymeleaf")
	public String thymeleaf(Model model) {
		model.addAttribute("name", "lishuibo");
		return "hello";
	}

	@RequestMapping("/db/get")
	@ResponseBody
	public Result<User> dbGet() {
		User user = userService.getById(1);
		return Result.success(user);
	}

	@RequestMapping("/db/tx")
	@ResponseBody
	public Result<Boolean> dbtx() {
		boolean tx = userService.tx();
		return Result.success(tx);
	}

	@RequestMapping("/redis/get")
	@ResponseBody
	public Result<User> redisGet() {
		User user = redisService.get(UserKey.getById, ""+1,User.class);
		return Result.success(user);
	}

	@RequestMapping("/redis/set")
	@ResponseBody
	public Result<Boolean> redisSet() {
		User user = new User();
		user.setId(1);
		user.setName("lishuibo");
		Boolean set = redisService.set(UserKey.getById, ""+1,user);
		return Result.success(set);
	}
}
