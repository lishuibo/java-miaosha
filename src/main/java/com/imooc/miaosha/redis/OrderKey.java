package com.imooc.miaosha.redis;

public class OrderKey extends BasePrefix {

	public OrderKey(String prefix) {
		super(prefix);
		// TODO Auto-generated constructor stub
	}
	
	public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("moug"); 
}
