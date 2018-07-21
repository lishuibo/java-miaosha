package com.imooc.miaosha.access;

import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.imooc.miaosha.config.UserContext;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.redis.AccessKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.MiaoshaUserService;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {
	@Autowired
	MiaoshaUserService miaoshaUserService;
	
	@Autowired
	RedisService redisService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub
		if (handler instanceof HandlerMethod) {
			MiaoshaUser miaoshaUser = getUser(request, response);
			UserContext.setUser(miaoshaUser);

			HandlerMethod hm = (HandlerMethod) handler;
			AccessLimit limit = hm.getMethodAnnotation(AccessLimit.class);
			if (limit == null) {
				return true;
			}
			int seconds = limit.seconds();
			int maxCount = limit.maxCount();
			boolean needLogin = limit.needLogin();
			String key = request.getRequestURI();
			if (needLogin) {
				if (miaoshaUser == null) {
					render(response, CodeMsg.SESSION_ERROR);
					return false;
				}
				key += "_" + miaoshaUser.getId();
			} else {
				
			}
			AccessKey accessKey = AccessKey.withExpire(seconds);
			Integer count = redisService.get(accessKey, key, Integer.class);
			if (count == null) {
				redisService.set(accessKey, key, 1);
			} else if (count < 5) {
				redisService.incr(accessKey, key);
			} else {
				render(response,CodeMsg.REQUEST_LIMIT_REACHED);
				return false;
			}
		}
		return true;
	}

	private void render(HttpServletResponse response, CodeMsg cm) throws Exception {
		// TODO Auto-generated method stub
		response.setContentType("application/json;charset=UTF-8");
		OutputStream output = response.getOutputStream();
		String str = JSON.toJSONString(Result.error(cm));
		output.write(str.getBytes("UTF-8"));
		output.flush();
		output.close();
	}

	private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response) {
		String paramToken = request.getParameter(MiaoshaUserService.COOKIE_NAME_TOKEN);
		String cookieToken = getCookieValue(request, MiaoshaUserService.COOKIE_NAME_TOKEN);
		if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
			return null;
		}
		String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
		return miaoshaUserService.getByToken(response, token);
	}

	private String getCookieValue(HttpServletRequest request, String cookieName) {
		// TODO Auto-generated method stub
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length <= 0) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(cookieName)) {
				return cookie.getValue();
			}
		}
		return null;
	}

}
