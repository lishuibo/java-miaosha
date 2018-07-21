package com.imooc.miaosha.redis;

public class UserKey extends BasePrefix {
	private UserKey(String prefix) {
		super(prefix);
		// TODO Auto-generated constructor stub
	}

	public static UserKey getById = new UserKey("id");
	public static UserKey getByName = new UserKey("name");	
}
