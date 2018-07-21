package com.imooc.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.miaosha.dao.UserDao;
import com.imooc.miaosha.domain.User;

@Service
public class UserService {
	@Autowired
	UserDao userDao;

	public User getById(int id) {
		return userDao.getById(id);
	}
	
	@Transactional
	public boolean tx() {
		User user1 = new User();
		user1.setId(10);
		user1.setName("litianle");
		userDao.insert(user1);
		
		User user2 = new User();
		user2.setId(1);
		user2.setName("litianle");
		userDao.insert(user2);
		
		return true;
	}
}
