package com.imooc.miaosha.redis;

public class MiaoshaUserKey extends BasePrefix {
	
	public static final int TOKEN_EXPIRE = 3600*24*2;
	public MiaoshaUserKey(int expireseconds, String prefix) {
		super(expireseconds,prefix);
		// TODO Auto-generated constructor stub
	}	
	public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPIRE,"tk");	 
	public static MiaoshaUserKey getById = new MiaoshaUserKey(0,"id");	 
}
