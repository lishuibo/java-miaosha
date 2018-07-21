package com.imooc.miaosha.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imooc.miaosha.dao.GoodsDao;
import com.imooc.miaosha.domain.MiaoshaGoods;
import com.imooc.miaosha.vo.GoodsVo;

@Service
public class GoodsService {
	@Autowired
	GoodsDao goodsDao;
	
	public List<GoodsVo> listGoodsVo() {
		return goodsDao.listGoodsVo();
	}

	public GoodsVo getGoodsVoByGoodsId(long goodsId) {
		return goodsDao.getGoodsVoByGoodsId(goodsId);
		// TODO Auto-generated method stub
		
	}

	public boolean reduceStock(GoodsVo goods) {
		// TODO Auto-generated method stub
		MiaoshaGoods g = new MiaoshaGoods();
		g.setGoodsId(goods.getId());
		int res = goodsDao.reduceStock(g);
		return res>0;
	} 
}
