package com.donglu.carpark.util;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.donglu.carpark.server.ServerUI;

/**
 * 
* @ClassName: ShortURLUtils  
* 长网址和短网址互转
* @author liang xiangjiang
* @date 2018年1月11日 上午10:25:41 
* @version V1.2  
 */
public class ShortURLUtils {
	private static final Logger LOGGER=LoggerFactory.getLogger(ServerUI.class);
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
			return longToShortDongyun(longURL);
		} catch (Exception e1) {
			LOGGER.error("dongyun转短网址时发生错误",e1.getMessage());
		}
		try {
			return longToShortMrw(longURL);
		} catch (Exception e1) {
			LOGGER.error("mrw.so转短网址时发生错误",e1.getMessage());
		}
		try {
			source = "3271760578";
			String strURL = "http://api.t.sina.com.cn/short_url/shorten.json?source="+source+"&url_long="+URLEncoder.encode(longURL, "UTF-8");
			//发送get请求
			String result = HttpRequestUtil.get(strURL);
			//请求返回内容为：[{"url_short":"http://t.cn/RQAZBUE","url_long":"http://www.g-tingche.com/third_api/QRCode_parking.action?parkId=ddae5d58a1ef4ca3a51e1d8e4aafec83","type":0}]
			//json转map集合
			LOGGER.info("长连接转短连接返回数据：{}",result);
			return JSON.parseObject(result.substring(1, result.length() -1)).getString("url_short");
		} catch (Exception e) {
			LOGGER.error("长连接转短连接时发生错误！",e.getMessage());
		}
		return null;
	}

	public static String longToShortDongyun(String longURL) throws Exception {
		String strURL = "http://www.dongluhitec.net/longUrlToShortUrl.do?longUrl=" + URLEncoder.encode(longURL, "UTF-8");
		// 发送get请求
		String result = HttpRequestUtil.get(strURL);
		LOGGER.info("长连接转短连接返回数据：{}", result);
		JSONObject jo = JSON.parseObject(result);
		int intValue = jo.getIntValue("ret");
		if (intValue==1) {
			return "http://dongluhitec.net/rtl.do?id="+jo.getString("retInfo");
		}
		throw new RuntimeException(jo.getString("retInfo"));
	}
	
	
	public static String longToShortDonglu(String longURL) throws Exception {
		String strURL = "http://www.dongluhitec.net/s/shorten.json?url_long=" + URLEncoder.encode(longURL, "UTF-8");
		// 发送get请求
		String result = HttpRequestUtil.get(strURL);
		// 请求返回内容为：[{"url_short":"http://t.cn/RQAZBUE","url_long":"http://www.g-tingche.com/third_api/QRCode_parking.action?parkId=ddae5d58a1ef4ca3a51e1d8e4aafec83","type":0}]
		// json转map集合
		LOGGER.info("长连接转短连接返回数据：{}", result);
		return JSON.parseObject(result.substring(1, result.length() - 1)).getString("url_short");
	}
	public static String longToShortBaidu(String longURL) {
		try {
			String token="87c6ed8ddd0f63d42da943ca9077622b";
			JSONObject jo=new JSONObject();
			jo.put("url", longURL);
			String httpPostMssage = HttpRequestUtil.httpPostMssage("https://dwz.cn/admin/v2/create", jo.toJSONString(), 
					new String[] {"Content-Type=application/json","Token="+token}, 5000);
			JSONObject result = JSON.parseObject(httpPostMssage);
			return result.getString("ShortUrl");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String longToShortMrw(String longURL) throws Exception {
			String key="5c9dde46d3c38179a65539ca@d2e1a909a62592906270ea032b423c75";
			String strURL ="http://mrw.so/api.php?url="+URLEncoder.encode(longURL, "UTF-8")+"&key="+key;
			String result = HttpRequestUtil.get(strURL);
			return result;
	}
	
	public static void main(String[] args) throws Exception {
		String longURL = "http://www.g-tingche.com/third_api/QRCode_parking.action?parkId=ddae5d58a1ef4ca3a51e1d8e4aafec83&deviceId=1&status=1"
				+ "&app_id=F56B7B&sign=358ecf13c98c19ddcb93c1876736e8bf";
//		String shortURL = ShortURLUtils.longToShort(longURL);
//		System.out.println(shortURL);
//		longToShortMrw(longURL);
		System.out.println(longToShortDongyun(longURL));
		
//		Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new Runnable() {
//			long id=387604;
//			long successSize=0;
//			@Override
//			public void run() {
//				try {
//					int fee=new Random().nextInt(1000)*100;
//					long currentTimeMillis = System.currentTimeMillis();
//					String s="http://www.dongluhitec.net/weixin_parkingRecord/test/ScanCode.jsp?recordId=8f4cec1316224f17bb4aab5da62bc415"+id+++"&fee="+fee+"&channelId=7e257819d2764bb6aa5c1fd43baf2f71";
//					LOGGER.info("successSize=="+successSize+"==id="+id+"=free="+fee+"===");
//					LOGGER.info(longToShortMrw(s));
//					LOGGER.info("花费时间：{}",System.currentTimeMillis()-currentTimeMillis);
//					successSize++;
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}, 5, 5, TimeUnit.SECONDS);
	}
}
