package com.donglu.carpark.util;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * use dependency 	<dependency>
			<groupId>org.eclipse.paho</groupId>
			<artifactId>org.eclipse.paho.client.mqttv3</artifactId>
			<version>1.2.1</version>
		</dependency>
 * 
 */
public class MqttService {
	private String host="mqtt.dongluhitec.net";

	private int port=1883;
	Logger LOGGER=LoggerFactory.getLogger(MqttService.class);
	private Set<String> setTopic=new HashSet<>();
	private MqttCallback callback;
	private MqttClient mqttClient;
	
	private String userName="dongyunsmart";
	private String password="smart@512809";
	
	public MqttService() {}
	
	public MqttService(String host, int port, String userName, String password) {
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}
	
	
	public interface MqttCallback {
		void call(String topic, String msg);
	}
	public void startMqttService() {
		mqttClient = createLongConnectMqtt();
	}
	public MqttClient createLongConnectMqtt() {
		try {
			String deviceMac = NetUtils.getMacAddress().split(",")[0];
			MqttClient client = new MqttClient("tcp://" + host + ":"+port, "DLCOD-"+deviceMac,new MemoryPersistence());
			// 创建MqttClient
			client.setCallback(new MqttCallbackExtended() {
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String msg = new String(message.getPayload(), "UTF-8");
					LOGGER.info("接收到主题：{} 消息：{}", topic, msg);
					try {
						callback.call(topic, msg);
					} catch (Exception e) {
						LOGGER.error("回调数据时发生异常", e);
					}
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken token) {

				}

				@Override
				public void connectionLost(Throwable cause) {
					LOGGER.info("mqtt连接断开:{}", cause);
				}

				@Override
				public void connectComplete(boolean reconnect, String serverURI) {
					if (setTopic.size()>0) {
						try {
							int[] qos = new int[setTopic.size()];
							for (int i = 0; i < qos.length; i++) {
								qos[i] = 1;
							}
							client.subscribe(setTopic.toArray(new String[setTopic.size()]), qos);
						} catch (MqttException e) {
							e.printStackTrace();
						} 
					}
				}
			});
			MqttConnectOptions conOptions = new MqttConnectOptions();
			conOptions.setUserName(System.getProperty("mqttLoginUser", "admin"));
			conOptions.setPassword(System.getProperty("mqttLoginPassword", "dongyun!@512809").toCharArray());
			conOptions.setCleanSession(true);
			conOptions.setAutomaticReconnect(true);
			client.connect(conOptions);
			boolean isSuccess = client.isConnected();
			LOGGER.info("连接mqtt：{}:{} 状态：{}", host, port, isSuccess);
			return client;
		} catch (Exception e) {
			throw new RuntimeException("连接mqtt失败:" + e.getMessage(), e);
		}
	}
	
	public boolean sendMqtt(String topic, String string) {
		try {
			mqttClient.publish(topic, new MqttMessage(string.getBytes("UTF-8")));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void main(String[] args) {
		MqttService mqttService = new MqttService();
		mqttService.startMqttService();
		mqttService.sendMqtt("lightcar/ipms/7e257819d2764bb6aa5c1fd43baf2f71", "{\"type\":\"adaad\"}");
	}
}
