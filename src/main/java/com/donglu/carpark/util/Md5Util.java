package com.donglu.carpark.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import com.google.common.base.Strings;

public class Md5Util {
	/**利用MD5进行加密
     * @param str  待加密的字符串
     * @return  加密后的字符串
     * @throws NoSuchAlgorithmException  没有这种产生消息摘要的算法
     * @throws UnsupportedEncodingException  
     */
    public static String md5(String str) throws Exception{
        //确定计算方法
        MessageDigest md5=MessageDigest.getInstance("MD5");
        //加密后的字符串
        byte[] digest = md5.digest(str.getBytes("utf-8"));
        String s="";
        for (byte b : digest) {
			s+=Strings.padStart(Integer.toHexString(b&0xff), 2, '0');
		}
        return s;
    }
}
