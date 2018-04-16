package com.donglu.carpark.util;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.alibaba.fastjson.JSON;

/**
 * 
* @ClassName: ShortURLUtils  
* 长网址和短网址互转
* @author liang xiangjiang
* @date 2018年1月11日 上午10:25:41 
* @version V1.2  
 */
public class ShortURLUtils {
	/**
	 * 应用的appkey
	 */
	private static String source;
	
	/**
	 * 
	*  longToShort  
	* 长网址转成短网址（转成短网址后缀是.cn）
	* @param  longURL 长网址
	* @return String    返回短网址  
	* @author 
	* @throws  
	 */
	public static String longToShort(String longURL){
		try {
			source = "3271760578";
			String strURL = "http://api.t.sina.com.cn/short_url/shorten.json?source="+source+"&url_long="+URLEncoder.encode(longURL, "UTF-8");
			//发送get请求
			String result = HttpRequestUtil.get(strURL);
			//请求返回内容为：[{"url_short":"http://t.cn/RQAZBUE","url_long":"http://www.g-tingche.com/third_api/QRCode_parking.action?parkId=ddae5d58a1ef4ca3a51e1d8e4aafec83","type":0}]
			//json转map集合
			
			return JSON.parseObject(result.substring(1, result.length() -1)).getString("url_short");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static void main(String[] args) {
		String shortURL = ShortURLUtils.longToShort("http://www.g-tingche.com/third_api/QRCode_parking.action?parkId=ddae5d58a1ef4ca3a51e1d8e4aafec83&deviceId=1&status=1&app_id=F56B7B&sign=358ecf13c98c19ddcb93c1876736e8bf");
		System.out.println(shortURL);
	}
}
