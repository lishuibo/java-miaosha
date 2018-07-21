package com.imooc.miaosha.redis;

public abstract class BasePrefix implements KeyPrefix {
	private int expireSeconds;
	private String prefix;

	public BasePrefix(String prefix) {
		this(0,prefix);
	}
	
	public BasePrefix(int expireSeconds, String prefix) {
		this.expireSeconds = expireSeconds;
		this.prefix = prefix;
	}

	@Override
	public int expireSeconds() {
		// TODO Auto-generated method stub
		return expireSeconds;
	}

	@Override
	public String getPrefix() {
		// TODO Auto-generated method stub
		String className = getClass().getSimpleName();
		return className + ":" + prefix;
	}

}
