package com.imooc.miaosha.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.imooc.miaosha.dao.MiaoshaUserDao;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.User;
import com.imooc.miaosha.exception.GlobalException;
import com.imooc.miaosha.redis.MiaoshaUserKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.LoginVo;

@Service
public class MiaoshaUserService {
	public static final String COOKIE_NAME_TOKEN = "token";
	@Autowired
	MiaoshaUserDao miaoshaUserDao;
	@Autowired
	RedisService redisService;

	public MiaoshaUser getById(long id) {
		MiaoshaUser miaoshaUser = redisService.get(MiaoshaUserKey.getById, "" + id, MiaoshaUser.class);
		if (miaoshaUser != null) {
			return miaoshaUser;
		}
		miaoshaUser = miaoshaUserDao.getById(id);
		if (miaoshaUser != null) {
			redisService.set(MiaoshaUserKey.getById, "" + id, miaoshaUser);
		}
		return miaoshaUser;
	}

	public boolean updatePassword(String token, long id, String formPass) {
		MiaoshaUser miaoshaUser = getById(id);
		if (miaoshaUser == null) {
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}

		MiaoshaUser miaoshaUserUpdate = new MiaoshaUser();
		miaoshaUserUpdate.setId(id);
		miaoshaUserUpdate.setPassword(MD5Util.FormPassToDBPass(formPass, miaoshaUser.getSalt()));
		miaoshaUserDao.update(miaoshaUserUpdate);

		redisService.delete(MiaoshaUserKey.getById, "" + id);
		miaoshaUser.setPassword(miaoshaUserUpdate.getPassword());
		redisService.set(MiaoshaUserKey.token, token, miaoshaUser);
		return true;
	}

	public String login(HttpServletResponse response, LoginVo loginVo) {
		// TODO Auto-generated method stub
		if (loginVo == null) {
			// return CodeMsg.SERVER_ERROR;
			throw new GlobalException(CodeMsg.SERVER_ERROR);
		}

		String mobile = loginVo.getMobile();
		String formpassword = loginVo.getPassword();
		MiaoshaUser miaoshaUser = getById(Long.parseLong(mobile));
		if (miaoshaUser == null) {
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		String dbpassword = miaoshaUser.getPassword();
		String salt = miaoshaUser.getSalt();
		String calcpassword = MD5Util.FormPassToDBPass(formpassword, salt);
		if (!calcpassword.equals(dbpassword)) {
			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
		}
		String token = UUIDUtil.uuid();
		addCookie(response, token, miaoshaUser);
		return token;
	}

	public MiaoshaUser getByToken(HttpServletResponse response, String token) {
		// TODO Auto-generated method stub
		if (StringUtils.isEmpty(token)) {
			return null;
		}
		MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
		if (user != null) {
			addCookie(response, token, user);
		}
		return user;
	}

	private void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
		// String token = UUIDUtil.uuid();
		redisService.set(MiaoshaUserKey.token, token, user);
		Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
		cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
		cookie.setPath("/");
		response.addCookie(cookie);
	}
}
