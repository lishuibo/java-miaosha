package com.imooc.miaosha.config;

import com.imooc.miaosha.domain.MiaoshaUser;

public class UserContext {
	private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<MiaoshaUser>();
	
	public static void setUser(MiaoshaUser miaoshaUser) {
		userHolder.set(miaoshaUser);
	}
	
	public static MiaoshaUser getUser() {
		return userHolder.get();
	} 
}
