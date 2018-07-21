package com.imooc.miaosha.vo;

import com.imooc.miaosha.domain.MiaoshaUser;

public class GoodsDetailVo {
	private GoodsVo goods;
	private MiaoshaUser miaoshaUser;
	private int miaoshaStatus = 0;
	private int remainSeconds = 0;

	public GoodsVo getGoods() {
		return goods;
	}

	public void setGoods(GoodsVo goods) {
		this.goods = goods;
	}

	public int getMiaoshaStatus() {
		return miaoshaStatus;
	}

	public void setMiaoshaStatus(int miaoshaStatus) {
		this.miaoshaStatus = miaoshaStatus;
	}

	public int getRemainSeconds() {
		return remainSeconds;
	}

	public void setRemainSeconds(int remainSeconds) {
		this.remainSeconds = remainSeconds;
	}

	public MiaoshaUser getMiaoshaUser() {
		return miaoshaUser;
	}

	public void setMiaoshaUser(MiaoshaUser miaoshaUser) {
		this.miaoshaUser = miaoshaUser;
	}

}
