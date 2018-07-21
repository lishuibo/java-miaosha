package com.imooc.miaosha.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {
	@Autowired
	JedisPool jedisPool;

	public <T> T get(KeyPrefix prefix, String key, Class<T> value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String realkey = prefix.getPrefix() + key;
			String str = jedis.get(realkey);
			T t = stringToBean(str, value);
			return t;
		} finally {
			// TODO Auto-generated catch block
			returnToPool(jedis);
		}
	}

	public <T> Boolean set(KeyPrefix prefix, String key, T value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String realkey = prefix.getPrefix() + key;
			String str = beanToString(value);
			if (str == null || str.length() <= 0) {
				return false;
			}

			int seconds = prefix.expireSeconds();
			if (seconds <= 0) {
				jedis.set(realkey, str);
			} else {
				jedis.setex(realkey, seconds, str);
			}

			return true;
		} finally {
			// TODO Auto-generated catch block
			returnToPool(jedis);
		}
	}

	public <T> Boolean exists(KeyPrefix prefix, String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String realkey = prefix.getPrefix() + key;
			return jedis.exists(realkey);
		} finally {
			// TODO Auto-generated catch block
			returnToPool(jedis);
		}
	}

	public <T> Long incr(KeyPrefix prefix, String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String realkey = prefix.getPrefix() + key;
			return jedis.incr(realkey);
		} finally {
			// TODO Auto-generated catch block
			returnToPool(jedis);
		}
	}

	public <T> Long decr(KeyPrefix prefix, String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String realkey = prefix.getPrefix() + key;
			return jedis.decr(realkey);
		} finally {
			// TODO Auto-generated catch block
			returnToPool(jedis);
		}
	}

	public boolean delete(KeyPrefix prefix, String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String realkey = prefix.getPrefix() + key;
			Long del = jedis.del(key);
			return del > 0;
		} finally {
			// TODO Auto-generated catch block
			returnToPool(jedis);
		}
	}

	public static <T> String beanToString(T value) {
		// TODO Auto-generated method stub
		if (value == null) {
			return null;
		}
		Class<?> clazz = value.getClass();
		if (clazz == int.class || clazz == Integer.class) {
			return "" + value;
		} else if (clazz == String.class) {
			return (String) value;
		} else if (clazz == long.class || clazz == Long.class) {
			return "" + value;
		} else {
			return JSON.toJSONString(value);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T stringToBean(String str, Class<T> clazz) {
		// TODO Auto-generated method stub
		if (str == null || str.length() <= 0 || clazz == null) {
			return null;
		}
		if (clazz == int.class || clazz == Integer.class) {
			return (T) Integer.valueOf(str);
		} else if (clazz == String.class) {
			return (T) str;
		} else if (clazz == long.class || clazz == Long.class) {
			return (T) Long.valueOf(str);
		} else {
			return JSON.toJavaObject(JSON.parseObject(str), clazz);
		}
	}

	private void returnToPool(Jedis jedis) {
		// TODO Auto-generated method stub
		if (jedis != null) {
			jedis.close();
		}
	}

}
