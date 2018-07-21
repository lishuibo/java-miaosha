package com.imooc.miaosha.config;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.alibaba.druid.util.StringUtils;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.service.MiaoshaUserService;

@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
	@Autowired
	MiaoshaUserService miaoshaUserService;

	@Override
	public Object resolveArgument(MethodParameter arg0, ModelAndViewContainer arg1, NativeWebRequest arg2,
			WebDataBinderFactory arg3) throws Exception {
		// TODO Auto-generated method stub
		return UserContext.getUser();
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

	@Override
	public boolean supportsParameter(MethodParameter arg0) {
		// TODO Auto-generated method stub
		Class<?> clazz = arg0.getParameterType();
		return clazz == MiaoshaUser.class;
	}

}
