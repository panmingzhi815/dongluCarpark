package com.donglu.carpark.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.donglu.carpark.service.CarparkQrCodeInOutService;
import com.donglu.carpark.util.NetUtils;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.base.Strings;

public class CarparkQrCodeInOutServiceImpl implements CarparkQrCodeInOutService {
	Logger LOGGER = LoggerFactory.getLogger(CarparkQrCodeInOutServiceImpl.class);

	private String host="www.dongluhitec.net";
	private String mqttHost="mqtt.dongluhitec.net";
	private int port=8991;
	private Socket s;
	private String parkId="9e7b56480c9f454c87339b0633b913eb";
	//String app_id =1
	String app_id = "1";
	//String secret_key =123qwer
	String secret_key = "123qwer";
	private static final Map<String, MqttClient> mapMqttClient=new HashMap<>();
	private static final Map<String, Long> mapMqttLastMsgTime=new HashMap<>();
	
	private String qrUrl="http://www.dongluhitec.net/dongyun_pay/parking_detail.html";
	
	public CarparkQrCodeInOutServiceImpl() {
		try {
			List<String> list = Files.readAllLines(Paths.get("setting.txt"));
			if (!StrUtil.isEmpty(list)) {
				host=list.get(0);
				if (list.size()>1) {
					mqttHost = list.get(1);
				}
				if (list.size()>2) {
					qrUrl = list.get(2);
				}
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void initService(String buildId,CarparkQrCodeInOutCallback callback) throws Exception {
		if (System.getProperty("useMqtt", "true").equals("true")) {
			initMqttService(buildId, callback);
			return;
		}
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
								LOGGER.error("二维码进出服务发生错误"+e1);
							}
						}
					}
				
			}
		}).start();
	}
	
	public void initMqttService(String buildId,CarparkQrCodeInOutCallback callback) throws Exception {
		MqttClient mqttClient = createLongConnectMqtt(buildId,callback);
		mapMqttClient.put(buildId, mqttClient);
//		new Timer().scheduleAtFixedRate(new TimerTask() {
//			@Override
//			public void run() {
//				LOGGER.info("检测MQTT:{}连接状态",buildId);
//				MqttClient client = mapMqttClient.get(buildId);
//				if (client==null||!client.isConnected()) {
//					try {
//						MqttClient mqttClient = createLongConnectMqtt(buildId,callback);
//						mapMqttClient.put(buildId, mqttClient);
//					} catch (Exception e) {
//						LOGGER.error("重连mqtt:"+buildId+" 失败",e);
//					}
//				}else {
//					if (mapMqttLastMsgTime.getOrDefault(buildId, 0l)==0) {
//						try {
//							JSONObject jo = new JSONObject();
//							jo.put("type", "test");
//							jo.put("buildId", buildId);
//							mqttClient.publish("lightcar/ipms/" + buildId, new MqttMessage(jo.toJSONString().getBytes("UTF-8")));
//							mapMqttLastMsgTime.put(buildId, System.currentTimeMillis());
//						} catch (Exception e) {
//							LOGGER.error("发布MQTT测试消息时发生错误，准备重连", e);
//							try {
//								mqttClient.disconnect();
//								mapMqttClient.remove(buildId);
//							} catch (MqttException e1) {
//								if(e1.getMessage().contains("已断开")) {
//									mapMqttClient.remove(buildId);
//								}
//							}
//						} 
//					}else {
//						try {
//							LOGGER.info("未收到本地发送的消息,断开MQTT连接，准备重连");
//							mqttClient.disconnect();
//							mapMqttClient.remove(buildId);
//						} catch (MqttException e) {
//							if(e.getMessage().contains("客户机未连接")) {
//								mapMqttClient.remove(buildId);
//							}
//						}
//					}
//				}
//			}
//		}, 60000, 60000);
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
		LOGGER.info("准备与与云平台：{}：{}建立长连接",host,buildId);
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

	public MqttClient createLongConnectMqtt(String buildId, CarparkQrCodeInOutCallback callback) {
		try {
			MqttClient client = new MqttClient("tcp://"+mqttHost+":1883", "DY-VLPR-"+NetUtils.getMacAddress().split(",")[0]+new Random().nextInt(99),new MemoryPersistence());
			String topic = "lightcar/ipms/"+buildId;
			int qos = 1;
			// 创建MqttClient
			client.setCallback(new MqttCallbackExtended() {
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String msg = new String(message.getPayload(),"UTF-8");
					LOGGER.info("接收到主题：{} 消息：{}",topic,msg);
					try {
						JSONObject jo = JSON.parseObject(msg);
						String type = jo.getString("type");
						if(type!=null&&type.equals("test")) {
							mapMqttLastMsgTime.remove(jo.getString("buildId"));
							return;
						}
						callback.call(msg);
					} catch (Exception e) {
						LOGGER.error("回调数据时发生异常", e);
					}
				}
				
				@Override
				public void deliveryComplete(IMqttDeliveryToken token) {
					
				}
				
				@Override
				public void connectionLost(Throwable cause) {
					LOGGER.info("{}的mqtt连接断开:{}",buildId,cause);
				}

				@Override
				public void connectComplete(boolean reconnect, String serverURI) {
					try {
						System.out.println(serverURI);
						if(!reconnect) {
							return;
						}
						LOGGER.info("准备订阅主题：{}:{} ：{}",serverURI,topic,reconnect);
						client.subscribe(topic, qos);
						LOGGER.info("订阅主题：{} 成功",topic);
					} catch (MqttException e) {
						LOGGER.error("订阅主题失败",e);
					};
				}
			});
			MqttConnectOptions conOptions = new MqttConnectOptions();
			conOptions.setUserName(System.getProperty("mqttLoginUser", "admin"));
			conOptions.setPassword(System.getProperty("mqttLoginPassword","dongyun!@512809").toCharArray());
			conOptions.setCleanSession(true);
			conOptions.setKeepAliveInterval(20);
			conOptions.setAutomaticReconnect(true);
			
			client.connect(conOptions);
			client.subscribe(topic, qos);
			boolean isSuccess = client.isConnected();
			LOGGER.info("连接mqtt：{}:{} 状态：{}",mqttHost,buildId,isSuccess);
			// client.disconnect();
			return client;
		} catch (Exception e) {
			throw new RuntimeException("连接mqtt失败:"+e.getMessage(),e);
		}
	}

	@Override
	public String getQrCodeUrl(String parkId,String plate,String ip, int type) {
		if (!StrUtil.isEmpty(plate)) {
			return qrUrl+"?recordId=";
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
			System.out.println(sign.length()+"=="+sign);
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
    @Override
    public boolean sendMqtt(String buildId, String string) {
		MqttClient mqttClient = mapMqttClient.get(buildId);
		try {
			mqttClient.publish("lightcar/ipms/"+buildId, new MqttMessage(string.getBytes("UTF-8")));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
    
	
	public static void main(String[] args) throws Exception {
		CarparkQrCodeInOutServiceImpl impl = new CarparkQrCodeInOutServiceImpl();
		impl.initService("7e257819d2764bb6aa5c1fd43baf2f71", new CarparkQrCodeInOutCallback() {
			@Override
			public void call(String ip) {
				System.out.println(ip);
			}
		});
	}
}
