package com.donglu.carpark.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.donglu.carpark.service.CarparkQrCodeInOutService;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.base.Strings;

public class CarparkQrCodeInOutServiceImpl implements CarparkQrCodeInOutService {
	Logger LOGGER = LoggerFactory.getLogger(CarparkQrCodeInOutServiceImpl.class);

	private String host="www.dongluhitec.net";
	private int port=8991;
	private Socket s;
	private String parkId="9e7b56480c9f454c87339b0633b913eb";
	//String app_id =1
	String app_id = "1";
	//String secret_key =123qwer
	String secret_key = "123qwer";

	@Override
	public void initService(String buildId,CarparkQrCodeInOutCallback callback) throws Exception {
		byte[] b = new byte[1024];
		createLongConnect(buildId);
		new Thread(new Runnable() {
			public void run() {
					while (!s.isClosed()) {
						try {
							s.setSoTimeout(10000);
							InputStream is = s.getInputStream();
							int read = is.read(b);
							String trim = new String(b,0,read,"UTF-8").trim();
							callback.call(trim);
						} catch (Exception e) {
							try {
								if (e instanceof SocketTimeoutException) {
									OutputStream os = s.getOutputStream();
									JSONObject jo=new JSONObject();
									jo.put("buildingId", buildId);
									jo.put("type", "PING_MSG");
									os.write((jo.toJSONString()+"\n").getBytes());
									os.flush();
								}else{
									e.printStackTrace();
									createLongConnect(buildId);
								}
							}catch (Exception e1) {
								LOGGER.error("二维码进出服务发生错误"+e1,e1);
							}
						}
					}
				
			}
		}).start();
	}

	/**
	 * @param buildId
	 * @return
	 * @throws SocketException
	 * @throws IOException
	 * @throws Exception
	 */
	public void createLongConnect(String buildId) throws Exception {
//		buildId="cb0dcb84a37e451c9c3825ebb04e5d3b";
		s = new Socket(host, port);
		s.setSoTimeout(10000);
		InputStream is = s.getInputStream();
		byte[] b = new byte[1024];
		is.read(b);
		String result = new String(b).trim();
		LOGGER.info("连接云平台：{}",result);
		JSONObject jsonObject = JSONObject.parseObject(result);
		if(jsonObject.getString("type").equals("connectSuccess")){
			OutputStream os = s.getOutputStream();
			JSONObject jo=new JSONObject();
			jo.put("buildingId", buildId);
			jo.put("type", "PING_MSG");
			os.write((jo.toJSONString()+"\n").getBytes());
			os.flush();
			LOGGER.info("发送消息：{} 成功",jo.toJSONString());
			is.read(b);
			result = new String(b).trim();
			jsonObject = JSONObject.parseObject(result);
			if(!jsonObject.getString("type").equals("PONG_MSG")){
				throw new Exception("建立长连接失败");
			}
			LOGGER.info("连接云平台：{}",result);
		}else{
			throw new Exception("连接失败");
		}
		s.setSoTimeout(0);
	}

	@Override
	public String getQrCodeUrl(String parkId,String plate,String ip, int type) {
		if (!StrUtil.isEmpty(plate)) {
			return "http://"+host+"/weixin_parkingRecord/test/parking_detail.html?recordId=";
		}
		return getUrl(host, parkId, app_id, secret_key, ip, type);
	}
	
	public void stopService(){
		try {
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param ip
	 * @param type
	 * @return
	 */
	public static String getUrl(String host,String parkId,String app_id,String secret_key,String ip, int type) {
		try {
			String s="http://"+host+"/third_api/QRCode_parking.action?";
			String sign="parkId="+parkId+"&deviceId="+URLEncoder.encode(ip,"UTF-8")+"&status="+type;
			System.out.println("加密的数据："+sign+secret_key);
			sign = s +sign+ "&app_id=" + app_id + "&sign=" + md5(sign+secret_key);
			System.out.println(sign.length());
			return sign;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
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
	
	public static void main(String[] args) throws Exception {
		System.out.println("拼接后的二维码："+getUrl("www.dongluhitec.net","9e7b56480c9f454c87339b0633b913eb","1","123qwer","192.168.1.88",0));
		
		
		try {
			String s="http://www.dongluhitec.net/third_api/ocm_login?";
			String sign="tel=13537630413&password=123456";
			System.out.println("加密的数据："+sign+"123qwer");
			sign = s +sign+ "&app_id=1&sign=" + md5(sign+"123qwer");
			System.out.println(sign.length()+"=="+sign);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		new Thread(new Runnable() {
			public void run() {
				Socket socket=null;
				try{
					socket = new Socket("www.dongluhitec.net", 8991);
					while (true) {
						InputStream is = socket.getInputStream();
						byte[] bs = new byte[1024];
						int read = is.read(bs);
						System.out.println(read+"===="+new String(bs, "UTF-8").trim());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
