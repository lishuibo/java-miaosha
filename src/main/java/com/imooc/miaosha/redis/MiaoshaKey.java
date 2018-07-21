package com.imooc.miaosha.redis;

public class MiaoshaKey extends BasePrefix {

	public MiaoshaKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
		// TODO Auto-generated constructor stub
	}

	public static MiaoshaKey isGoodOver = new MiaoshaKey(0, "go");
	public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60, "mp");
	public static MiaoshaKey getMiaoshaVerifyCode = new MiaoshaKey(300, "vc");
}
