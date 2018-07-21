package com.imooc.miaosha.vaildator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.alibaba.druid.util.StringUtils;
import com.imooc.miaosha.util.ValidatorUtil;


public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {
	private boolean required = false;
	
	public void initialize(IsMobile arg0) {
		// TODO Auto-generated method stub
		required = arg0.required();
	}

	public boolean isValid(String arg0, ConstraintValidatorContext arg1) {
		// TODO Auto-generated method stub
		if (required) {
			return ValidatorUtil.isMobile(arg0);
		}else {
			if (StringUtils.isEmpty(arg0)) {
				return true;
			}else {
				return ValidatorUtil.isMobile(arg0);
			}
		}
	}
}
