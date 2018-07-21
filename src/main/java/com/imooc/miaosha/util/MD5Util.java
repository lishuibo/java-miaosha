package com.imooc.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
	public static String md5(String src) {
		return DigestUtils.md5Hex(src);
	}

	public static final String salt = "1a2b3c4d";

	public static String inputPassToFormPass(String inputPass) {
		String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
		System.out.println(str);
		return md5(str);
	}
	
	public static String FormPassToDBPass(String formpass,String salt) {
		String str = "" + salt.charAt(0) + salt.charAt(2) + formpass + salt.charAt(5) + salt.charAt(4);
		return md5(str);
	}
	
	public static String inputPassToDBPass(String inputpass,String salltDB) {
		String formpass = inputPassToFormPass(inputpass);
		String dbpass = FormPassToDBPass(formpass, salltDB);
		return dbpass;
	}
	
	public static void main(String[] args) {
		System.out.println(inputPassToDBPass("123456", "1a2b3c4d"));
	}
}
