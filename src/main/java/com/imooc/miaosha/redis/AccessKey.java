package com.imooc.miaosha.redis;

public class AccessKey extends BasePrefix {
	public AccessKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
		// TODO Auto-generated constructor stub
	}

	public static AccessKey withExpire(int expireSeconds) {
		return new AccessKey(expireSeconds,"access");
	}
}
