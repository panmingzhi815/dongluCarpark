package com.donglu.carpark.service.background.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.background.AbstractCarparkBackgroundService;
import com.donglu.carpark.service.background.ShanghaidibiaoSynchroServiceI;
import com.donglu.carpark.util.HttpRequestUtil;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkEvent;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemOperaLog;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.UploadHistory;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.CarparkRecordHistory;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.ProcessEnum;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.UpdateEnum;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.UserHistory;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;

public class ShanghaidibiaoSynchroServiceImpl extends AbstractCarparkBackgroundService implements ShanghaidibiaoSynchroServiceI {
	private static final String BOUNDARY = "-------45962402127348";
	private static final String FILE_ENCTYPE = "multipart/form-data";
	static Logger LOGGER = LoggerFactory.getLogger(ShanghaidibiaoSynchroServiceImpl.class);
	private CarparkDatabaseServiceProvider sp;

	public static String serverUrl = "http://cctvyz.gicp.net:8081/agbox/";
	public static String key = "570670eb-9916-43a6-8a63-fa9e49ce1ef6";
	boolean isStart = false;
	long runSize = 0;
	Cache<String, Object> cache = CacheBuilder.newBuilder().expireAfterWrite(10000, TimeUnit.SECONDS).build();
	private static MqttClient client;
	private String mqttUrl;
	private String mqttUser;
	private String mqttPwd;
	public static Set<String> mqttTopicSet = new HashSet<>();
	private String villageCode;
	private String buildingCode;
	private String houseCode;
	private int credentialType=Integer.valueOf(System.getProperty("credentialType", "1"));
	public static final Map<String,Integer> mapCarType=new HashMap<>();
	public static final Map<Integer,String> mapCarTypeToName=new HashMap<>();
	public static final List<String> listCarTypeNames=new ArrayList<>();
	public static final Map<String,Integer> mapEventCode=new HashMap<>();
	public static final Map<Integer,String> mapEventCodeToName=new HashMap<>();
	public static final Map<String,Integer> mapPlateType=new HashMap<>();
	public static final Map<Integer,String> mapPlateTypeToName=new HashMap<>();
	public static final List<String> listPlateTypeNames=new ArrayList<>();

	@Inject
	public ShanghaidibiaoSynchroServiceImpl(CarparkDatabaseServiceProvider sp) {
		super(Scheduler.newFixedDelaySchedule(5, 5, TimeUnit.SECONDS), "上海地标数据同步服务");
		this.sp = sp;
	}

	@Override
	protected void run() {
		if (runSize % 60 == 0) {
			SingleCarparkSystemSetting agboxUrl = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.agboxUrl.name());
			SingleCarparkSystemSetting agboxEnable = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.agboxEnable.name());
			SingleCarparkSystemSetting agboxKey = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.agboxKey.name());
			SingleCarparkSystemSetting agboxMqttUrl = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.agboxMqttUrl.name());
			SingleCarparkSystemSetting agboxMqttUser = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.agboxMqttUser.name());
			SingleCarparkSystemSetting agboxMqttPwd = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.agboxMqttPwd.name());
			serverUrl = agboxUrl.getSettingValue();
			key = agboxKey.getSettingValue();
			isStart = agboxEnable.getBooleanValue();
			LOGGER.info("上海数据上传服务：{}", isStart);
			mqttUrl = agboxMqttUrl.getSettingValue();
			mqttUser = agboxMqttUser.getSettingValue();
			mqttPwd = agboxMqttPwd.getSettingValue();
			if (client == null && !StrUtil.isEmpty(agboxMqttUrl.getSettingValue())) {
				startMqtt();
			}
			if (isStart&&!StrUtil.isEmpty(serverUrl)) {
				JSONArray credentialTypeCodes = getCredentialTypeCode();
				for (int i = 0; i < credentialTypeCodes.size(); i++) {
					JSONObject object = credentialTypeCodes.getJSONObject(i);
					if ("身份证".equals(object.getString("name"))) {
						credentialType = object.getIntValue("code");
					}
				}
				JSONArray carTypeCode = getCarTypeCode();
				System.out.println(carTypeCode);
				if (!StrUtil.isEmpty(carTypeCode)) {
					listCarTypeNames.clear();
					mapCarType.clear();
					for (int i = 0; i < carTypeCode.size(); i++) {
						JSONObject object = carTypeCode.getJSONObject(i);
						System.out.println(object);
						mapCarType.put(object.getString("name"), object.getIntValue("code"));
						mapCarTypeToName.put(object.getIntValue("code"), object.getString("name"));
						listCarTypeNames.add(object.getString("name"));
					}
				}
				JSONArray plateTypeCode = getPlateTypeCode();
				System.out.println(plateTypeCode);
				if (!StrUtil.isEmpty(plateTypeCode)) {
					mapPlateType.clear();
					listPlateTypeNames.clear();
					for (int i = 0; i < plateTypeCode.size(); i++) {
						JSONObject object = plateTypeCode.getJSONObject(i);
						System.out.println(object);
						mapPlateType.put(object.getString("name"), object.getIntValue("code"));
						mapPlateTypeToName.put(object.getIntValue("code"), object.getString("name"));
						listPlateTypeNames.add(object.getString("name"));
					}
				}
				JSONArray eventCode = getEventCode();
				System.out.println(eventCode);
				if (!StrUtil.isEmpty(eventCode)) {
					for (int i = 0; i < eventCode.size(); i++) {
						JSONObject jo = eventCode.getJSONObject(i);
						mapEventCode.put(jo.getString("name"), jo.getIntValue("code"));
						mapEventCodeToName.put(jo.getIntValue("code"), jo.getString("name"));
					}
				}
			}
		}
		runSize++;
		if (!isStart || StrUtil.isEmpty(serverUrl) || StrUtil.isEmpty(key)) {
			return;
		}
		List<UploadHistory> history = sp.getSettingService().findUploadHistory(0, 10, "agbox", 0);
		for (UploadHistory uploadHistory : history) {
			String url = uploadHistory.getUrl().startsWith("http") ? uploadHistory.getUrl() : serverUrl + uploadHistory.getUrl();
			try {
				Map<String, String> map = new HashMap<>();
				map.put("key", key);
				map.put("json", new String(uploadHistory.getData(), "UTF-8"));
				String post = post(url, map, null);
				// String post = HttpRequestUtil.httpPostMssage(url+uploadHistory.getUrl(),
				// "key="+key+"&json="+new String(uploadHistory.getData(),"UTF-8"), 5000);
				LOGGER.info("返回消息：{}", post);
				if (post != null) {
					sp.getSettingService().updateUploadHistory(uploadHistory.getId(), 1);
				}
			} catch (Exception e) {
				LOGGER.error("推送UploadHistory数据：" + uploadHistory.getId() + "到上海agbox时发生错误", e);
			}
		}
		List<CarparkRecordHistory> list = sp.getCarparkInOutService().findHaiYuRecordHistory(0, 10, new UpdateEnum[] { UpdateEnum.新添加, UpdateEnum.被修改 }, new ProcessEnum[] { ProcessEnum.未处理 });
		for (CarparkRecordHistory carparkRecordHistory : list) {
			try {
				boolean addEvent = addEvent(carparkRecordHistory);
				if (addEvent) {
					sp.getCarparkInOutService().updateHaiYuRecordHistory(Arrays.asList(carparkRecordHistory.getId()), ProcessEnum.己处理);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		List<UserHistory> findUserHistory = sp.getCarparkUserService().findUserHistory(UpdateEnum.values(), new ProcessEnum[] { ProcessEnum.未处理 });
		for (UserHistory userHistory : findUserHistory) {
			boolean updatePlate = updatePlate(userHistory);
			if (updatePlate) {
				sp.getCarparkUserService().updateUserHistory(userHistory, ProcessEnum.己处理);
			}
		}
		
		if ((runSize-1)%120==0) {
			int i=1;
			while(true) {
				JSONArray plateList = getPlateList(i,10);
				if (plateList==null||plateList.isEmpty()) {
					return;
				}
				SingleCarparkSystemOperaLog log = new SingleCarparkSystemOperaLog();
				log.setType(SystemOperaLogTypeEnum.数据上传);
				log.setContent("获取到"+plateList.size()+"车辆信息");
				log.setRemarkString("获取到"+plateList.size()+"车辆信息："+plateList);
				log.setOperaDate(new Date());
				log.setOperaName("服务器更新");
				sp.getSystemOperaLogService().saveOperaLog(log);
				for (Object object : plateList) {
					try {
						JSONObject plateInfo = JSONObject.class.cast(object);
						String plateNo = plateInfo.getString("plateNo");
						boolean enabled = plateInfo.getBooleanValue("enabled");
						List<SingleCarparkUser> list2 = sp.getCarparkUserService().findByNameOrPlateNo(null, plateNo, null, null, 0, null);
						SingleCarparkUser user=null;
						if (enabled) {
							if(StrUtil.isEmpty(list2)) {
								user = new SingleCarparkUser();
								user.setPlateNo(plateNo);
								List<SingleCarparkCarpark> list3 = sp.getCarparkService().findCarparkToLevel();
								user.setCarpark(list3.get(0));
							}else {
								user=list2.get(0);
							}
							JSONObject info = getUserInfo(plateInfo.getIntValue("credentialType"),plateInfo.getString("credentialNo"));
							if (info!=null) {
								user.setName(info.getString("peopleName"));
								try {
									JSONArray phone = info.getJSONArray("phone");
									user.setTelephone(phone.size() > 0 ? phone.getString(0) : "");
								} catch (Exception e) {
									e.printStackTrace();
								}
							}else {
								user.setName("agbox数据");
							}
							user.setValidTo(new Date(System.currentTimeMillis()+30l*24*60*60*1000));
							user.setCreateHistory(false);
							user.setIdCard(plateInfo.getString("credentialNo"));
							user.setPlateType(mapPlateTypeToName.get(plateInfo.getIntValue("plateTypeCode")));
							user.setCarTypeShanghai(mapCarTypeToName.get(plateInfo.getIntValue("carTypeCode")));
							sp.getCarparkUserService().saveUser(user);
						}
						
						if (!StrUtil.isEmpty(list2)) {
							if (!enabled) {
								for (SingleCarparkUser singleCarparkUser : list2) {
									singleCarparkUser.setValidTo(new Date(System.currentTimeMillis()-24*60*60*1000));
									singleCarparkUser.setCreateHistory(false);
									sp.getCarparkUserService().saveUser(singleCarparkUser);
								}
							}
						}
					} catch (Exception e) {
						LOGGER.error("处理车牌数据："+object+"时发生错误",e);
					}
				}
				if (plateList.size()<10) {
					return;
				}else {
					i++;
				}
			}
		}
	}
	
	public JSONArray getPlateTypeCode() {
		JSONObject jo = new JSONObject();
		jo.put("jsonrpc", "2.0");
		jo.put("method", "getPlateTypeCode");
		jo.put("id", "" + System.currentTimeMillis());
		try {
			Map<String, String> map = new HashMap<>();
			map.put("key", key);
			map.put("json", jo.toJSONString());
			String post = post(serverUrl + "share/car", map, null);
			System.out.println(post);
			LOGGER.info("获取车辆类型编码列表结果：{}", post);
			if (post != null) {
				JSONObject d = JSON.parseObject(post);
				JSONObject result = d.getJSONObject("result");
				if (result!=null) {
					return result.getJSONArray("plateType");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONArray();
	}
	
	public JSONArray getCarTypeCode() {
		JSONObject jo = new JSONObject();
		jo.put("jsonrpc", "2.0");
		jo.put("method", "getCarTypeCode");
		jo.put("id", "" + System.currentTimeMillis());
		try {
			Map<String, String> map = new HashMap<>();
			map.put("key", key);
			map.put("json", jo.toJSONString());
			String post = post(serverUrl + "share/car", map, null);
			System.out.println(post);
			LOGGER.info("获取车辆类型编码列表结果：{}", post);
			if (post != null) {
				JSONObject d = JSON.parseObject(post);
				JSONObject result = d.getJSONObject("result");
				if (result!=null) {
					return result.getJSONArray("carType");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONArray();
	}
	
	public JSONArray getEventCode() {
		JSONObject jo = new JSONObject();
		jo.put("jsonrpc", "2.0");
		jo.put("method", "getEventCode");
		jo.put("id", "" + System.currentTimeMillis());
		try {
			Map<String, String> map = new HashMap<>();
			map.put("key", key);
			map.put("json", jo.toJSONString());
			String post = post(serverUrl + "device/parking", map, null);
			System.out.println(post);
			LOGGER.info("获取事件编码列表结果：{}", post);
			if (post != null) {
				JSONObject d = JSON.parseObject(post);
				JSONObject result = d.getJSONObject("result");
				if (result!=null) {
					return result.getJSONArray("eventCode");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONArray();
	}
	
	private JSONArray getCredentialTypeCode() {
		JSONObject jo = new JSONObject();
		jo.put("jsonrpc", "2.0");
		jo.put("method", "getCredentialTypeCode");
		jo.put("id", "" + System.currentTimeMillis());
		try {
			Map<String, String> map = new HashMap<>();
			map.put("key", key);
			map.put("json", jo.toJSONString());
			String post = post(serverUrl + "person", map, null);
			System.out.println(post);
			LOGGER.info("获取获取证件类型编码列表结果：{}", post);
			if (post != null) {
				JSONObject d = JSON.parseObject(post);
				JSONObject result = d.getJSONObject("result");
				if (result!=null) {
					return result.getJSONArray("credentialType");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONArray();
	}

	private JSONArray getPlateList(int page,int size) {
		JSONObject jo = new JSONObject();
		jo.put("jsonrpc", "2.0");
		jo.put("method", "getPlate");
		JSONObject params=new JSONObject();
		params.put("pageSize", size);
		params.put("page", page);
		jo.put("params", params);
		jo.put("id", "" + System.currentTimeMillis());
		try {
			Map<String, String> map = new HashMap<>();
			map.put("key", key);
			map.put("json", jo.toJSONString());
			String post = post(serverUrl + "share/car", map, null);
			System.out.println(post);
			LOGGER.info("获取车牌列表结果：{}", post);
			if (post != null) {
				JSONObject d = JSON.parseObject(post);
				JSONObject result = d.getJSONObject("result");
				if (result!=null) {
					return result.getJSONArray("plateList");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONArray();
	}

	@Override
	protected void startUp() throws Exception {

	}

	public static void addTopic(String deviceId) {
		String topicFilter = "event/parking/"+deviceId;
		mqttTopicSet.add(topicFilter);
		if (client!=null&&mqttTopicSet.contains(topicFilter)) {
			try {
				client.subscribe(topicFilter);
			} catch (MqttException e) {
				LOGGER.error("订阅设备：{}主题时发生错误：{}",deviceId,e);
			}
		}
	}

	/**
	 * 
	 */
	public void startMqtt() {
		try {
			client = new MqttClient(mqttUrl, "donglucarpark-" + System.currentTimeMillis(), new MemoryPersistence());
			MqttConnectOptions conOptions = new MqttConnectOptions();
			conOptions.setUserName(System.getProperty("mqttLoginUser", mqttUser));
			conOptions.setPassword(System.getProperty("mqttLoginPassword", mqttPwd).toCharArray());
			conOptions.setCleanSession(true);
			conOptions.setAutomaticReconnect(true);
			client.setCallback(new MqttCallbackExtended() {
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String msg = new String(message.getPayload(), "UTF-8");
					LOGGER.info("接收到主题：{} 消息：{}", topic, msg);
					try {
						SingleCarparkSystemOperaLog log = new SingleCarparkSystemOperaLog();
						log.setType(SystemOperaLogTypeEnum.数据上传);
						log.setContent("接收到主题:"+topic+"消息:"+msg);
						log.setOperaDate(new Date());
						log.setOperaName("服务器MQTT");
						log.setRemarkString("接收到主题:"+topic+"消息:"+msg);
						JSONObject jo = JSON.parseObject(msg);
						if ("event".equals(jo.getString("method"))) {
							JSONObject e = getEvent(jo.getString("id"));
							System.out.println(e);
							log.setRemarkString("接收到主题:"+topic+"消息:"+msg+" 内容："+e);
							String eventName = mapEventCodeToName.get(e.getIntValue("eventCode"));
//							SingleCarparkInOutHistory ioh = new SingleCarparkInOutHistory();
//							ioh.setPlateNo(e.getString("plateNo"));
//							ioh.setEntranceCode(e.getString("entranceCode"));
//							ioh.setPlateColor(e.getString("plateColor"));
//							ioh.setEventName(eventName);
//							ioh.setPlateType(mapPlateTypeToName.getOrDefault(e.getIntValue("plateCode"), "普通蓝牌"));
//							ioh.setRemarkString(ioh.getEventName()+","+ioh.getPlateType()+",mqtt消息时间");
//							ioh.setOutTime(StrUtil.parseDateTime(e.getString("triggerTime")));
//							ioh.setInTime(StrUtil.parseDateTime(e.getString("triggerTime")));
//							ioh.setCarType("");
//							ioh.setSaveHistory(false);
//							ioh.setShouldMoney(0);
//							ioh.setFactMoney(0);
//							ioh.setCarTypeShanghai(eventName.substring(0, eventName.length()-1));
//							sp.getCarparkInOutService().saveInOutHistory(ioh);
							CarparkEvent ee = new CarparkEvent();
							ee.setPlate(e.getString("plateNo"));
							ee.setCarType(eventName.substring(0, eventName.length()-1));
							ee.setPlateType(mapPlateTypeToName.getOrDefault(e.getIntValue("plateCode"), "普通蓝牌"));
							ee.setChannel(e.getString("channel"));
							ee.setDeviceId(e.getString("deviceId"));
							ee.setEntranceCode(e.getString("entranceCode"));
							ee.setEventName(eventName);
							ee.setEventTime(StrUtil.parseDateTime(e.getString("triggerTime")));
							ee.setPlateColor(e.getString("plateColor"));
							sp.getCarparkInOutService().saveCarparkEvent(ee);
						}
						sp.getSystemOperaLogService().saveOperaLog(log);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken token) {

				}

				@Override
				public void connectionLost(Throwable cause) {

				}

				@Override
				public void connectComplete(boolean reconnect, String serverURI) {
					try {
						if (mqttTopicSet.size()>0) {
							client.subscribe(mqttTopicSet.toArray(new String[mqttTopicSet.size()]));
							LOGGER.info("订阅主题：{}成功",mqttTopicSet);
						}
					} catch (MqttException e) {
						LOGGER.info("订阅主题失败",e);
					}
				}
			});
			client.connect(conOptions);
			LOGGER.info("连接上海停车场mqtt成功");
		} catch (MqttException e) {
			LOGGER.error("连接mqtt：{}失败",mqttUrl,e);
		}
	}

	protected JSONArray getPlate(String certifiedNo) {
		JSONObject jo = new JSONObject();
		jo.put("jsonrpc", "2.0");
		jo.put("method", "getEvent");
		JSONObject params = new JSONObject();
		// eventId Number Required 事件id，来自于通知订阅
		params.put("credentialNo", certifiedNo);
		jo.put("params", params);
		jo.put("id", "" + System.currentTimeMillis());
		try {
			Map<String, String> map = new HashMap<>();
			map.put("key", "570670eb-9916-43a6-8a63-fa9e49ce1ef6");
			map.put("json", jo.toJSONString());
			String post = post(serverUrl+"device/parking", map, null);
			LOGGER.info("获取车牌信息结果：{}", post);
			JSONObject result = JSON.parseObject(post);
			int count = result.getIntValue("count");
			if (count > 0) {
				return result.getJSONArray("plateList");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONArray();
	}
	
	public JSONObject getUserInfo(int credentialType, String certifiedNo) {
		JSONObject jo = new JSONObject();
		jo.put("jsonrpc", "2.0");
		jo.put("method", "getInfo");
		JSONObject params = new JSONObject();
		// eventId Number Required 事件id，来自于通知订阅
		params.put("credentialType", credentialType);
		params.put("credentialNo", certifiedNo);
		params.put("range", "basic");
		jo.put("params", params);
		jo.put("id", "" + System.currentTimeMillis());
		try {
			Map<String, String> map = new HashMap<>();
			map.put("key", key);
			map.put("json", jo.toJSONString());
			String post = post(serverUrl+"person", map, null);
			LOGGER.info("获取用户信息结果：{}", post);
			JSONObject result = JSON.parseObject(post);
			return result.getJSONObject("result").getJSONObject("personInfo");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected JSONObject getEvent(String id) {
		JSONObject jo = new JSONObject();
		jo.put("jsonrpc", "2.0");
		jo.put("method", "getEvent");
		JSONObject params = new JSONObject();
		// eventId Number Required 事件id，来自于通知订阅
		params.put("eventId", id);
		jo.put("params", params);
		jo.put("id", "" + System.currentTimeMillis());
		try {
			Map<String, String> map = new HashMap<>();
			map.put("key", key);
			map.put("json", jo.toJSONString());
			String post = post(serverUrl+"device/parking", map, null);
			LOGGER.info("获取事件信息结果：{}", post);
			if (post != null) {
				return JSON.parseObject(post).getJSONObject("result").getJSONObject("eventInfo");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean updatePlate(UserHistory userHistory) {
		updateUser(userHistory);
		bindingHouse(userHistory);
		JSONObject jo = new JSONObject();
		jo.put("jsonrpc", "2.0");
		jo.put("method", "updatePlate");
		JSONObject params = new JSONObject();
		// plateNo String Required 车牌号码 MSTL
		// plateTypeCode Number Required 车牌类型编码
		// carTypeCode Number Optional 车辆类型编码
		// credentialType Number Optional 证件类型编码
		// credentialNo String Optional 证件号码
		// enabled Boolean Optional 启用/弃用
		params.put("plateNo", userHistory.getPlateNo());
		params.put("plateTypeCode", mapPlateType.getOrDefault(userHistory.getPlateType(), 1));
		params.put("carTypeCode", mapCarType.getOrDefault(userHistory.getCarTypeShanghai(), 1));
		params.put("credentialType", credentialType);
		params.put("credentialNo", userHistory.getIdCard());
		params.put("enabled", userHistory.getHistoryDetail().getUpdateState() == UpdateEnum.被删除 ? "false" : "true");
		jo.put("params", params);
		jo.put("id", "" + System.currentTimeMillis());
		try {
			Map<String, String> map = new HashMap<>();
			map.put("key", key);
			map.put("json", jo.toJSONString());
			String post = post(serverUrl + "share/car", map, null);
			System.out.println(post);
			LOGGER.info("更新车牌信息结果：{}", post);
			if (post != null) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	public boolean updateUser(UserHistory userHistory) {
//		peopleName String Optional 姓名 
//		credentialType Number Required 证件类型 
//		credentialNo String Required 证件号码 
//		source Number Required 来源 
//		
//		phone1 Object Optional 电话1 
//		no String Optional 电话号码 
//		name String Optional 归属人姓名 
//		credentialType Number Optional 归属人证件类型 
//		credentialNo String Optional 归属人身份证号码 
//		phone2 Object Optional 电话2 
//		phone3 Object Optional 电话3 
//		entranceTypeCode Number Required 出入类型
		JSONObject jo = new JSONObject();
		jo.put("jsonrpc", "2.0");
		jo.put("method", "update");
		JSONObject params = new JSONObject();
		
		params.put("peopleName", userHistory.getName());
		params.put("source", 4);
		params.put("typeCode", 1);
		params.put("credentialType", credentialType);
		params.put("credentialNo", userHistory.getIdCard());
		
//		JSONObject domicile = new JSONObject();
//		domicile.put("provinceCode","310000000000");
//		domicile.put("cityCode","310100000000");
//		domicile.put("districtCode","310109000000");
//		domicile.put("streetCode","310109010000");
//		params.put("domicile", domicile);
//		
//		JSONObject residence = new JSONObject();
//		residence.put("provinceCode","310000000000");
//		residence.put("cityCode","310100000000");
//		residence.put("districtCode","310109000000");
//		residence.put("streetCode","310109010000");
//		params.put("residence", residence);
		
		JSONObject phone1 = new JSONObject();
		phone1.put("no", userHistory.getTelephone());
		phone1.put("name", userHistory.getName());
		phone1.put("credentialType", credentialType);
		phone1.put("credentialNo", userHistory.getIdCard());
		params.put("phone1",phone1);
		params.put("phone2", new JSONObject());
		params.put("phone3", new JSONObject());
		
		jo.put("params", params);
		jo.put("id", "" + System.currentTimeMillis());
		try {
			Map<String, String> map = new HashMap<>();
			map.put("key", key);
			map.put("json", jo.toJSONString());
			String post = post(serverUrl + "person", map, null);
			System.out.println(post);
			LOGGER.info("更新用户信息结果：{}", post);
			if (post != null) {
				return true;
			}
		} catch (Exception e) {
			LOGGER.info("更新用户信息发生错误", e);
		}
		return false;
	}
	
	public boolean bindingHouse(UserHistory userHistory) {
		if (villageCode==null) {
			JSONArray village = getVillage();
			if (StrUtil.isEmpty(village)) {
				return false;
			}
			villageCode = village.getJSONObject(0).getString("villageCode");
		}
		if (buildingCode==null) {
			JSONArray buildings = getBuilding(villageCode);
			if (StrUtil.isEmpty(buildings)) {
				return false;
			}
			buildingCode = buildings.getJSONObject(0).getString("buildingCode");
		}
		if (houseCode==null) {
			JSONArray houses = getHouse(buildingCode);
			if (StrUtil.isEmpty(houses)) {
				return false;
			}
			houseCode = houses.getJSONObject(0).getString("houseCode");
		}
		LOGGER.info("更新人屋信息：证件号码：{}：区域编号：{} 楼栋编号：{},房屋编号：{}",userHistory.getIdCard(),villageCode,buildingCode,houseCode);
		JSONObject jo = new JSONObject();
		jo.put("jsonrpc", "2.0");
		jo.put("method", "bindingHouse");
		JSONObject params = new JSONObject();
		
		params.put("credentialType", credentialType);
		params.put("credentialNo", userHistory.getIdCard());
		JSONObject house = new JSONObject();
		house.put("villageCode", villageCode);
		house.put("buildingCode",buildingCode);
		house.put("houseCode", houseCode);
		house.put("HouseRelCode", 1);
		params.put("house",house);
		jo.put("params", params);
		jo.put("id", "" + System.currentTimeMillis());
		try {
			Map<String, String> map = new HashMap<>();
			map.put("key", key);
			map.put("json", jo.toJSONString());
			String post = post(serverUrl + "person", map, null);
			System.out.println(post);
			LOGGER.info("更新用户人屋信息结果：{}", post);
			if (post != null) {
				return true;
			}
		} catch (Exception e) {
			LOGGER.info("更新用户信息发生错误", e);
		}
		return false;
		
	}
	
	public JSONArray getVillage() {
		JSONObject jo = new JSONObject();
		jo.put("jsonrpc", "2.0");
		jo.put("method", "getList");
		JSONObject params=new JSONObject();
		params.put("pageSize", 2);
		params.put("page", 1);
		jo.put("params", params);
		jo.put("id", "" + System.currentTimeMillis());
		try {
			Map<String, String> map = new HashMap<>();
			map.put("key", key);
			map.put("json", jo.toJSONString());
			String post = post(serverUrl + "village", map, null);
			System.out.println(post);
			LOGGER.info("获取小区列表结果：{}", post);
			if (post != null) {
				JSONObject d = JSON.parseObject(post);
				JSONObject result = d.getJSONObject("result");
				if (result!=null) {
					return result.getJSONArray("villageList");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONArray();
	}
	public JSONArray getBuilding(String villageCode) {
		JSONObject jo = new JSONObject();
		jo.put("jsonrpc", "2.0");
		jo.put("method", "getBuilding");
		JSONObject params=new JSONObject();
		params.put("pageSize", 2);
		params.put("page", 1);
		params.put("villageCode", villageCode);
		jo.put("params", params);
		jo.put("id", "" + System.currentTimeMillis());
		try {
			Map<String, String> map = new HashMap<>();
			map.put("key", key);
			map.put("json", jo.toJSONString());
			String post = post(serverUrl + "village", map, null);
			System.out.println(post);
			LOGGER.info("获取小区楼栋列表结果：{}", post);
			if (post != null) {
				JSONObject d = JSON.parseObject(post);
				JSONObject result = d.getJSONObject("result");
				if (result!=null) {
					return result.getJSONArray("buildingList");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONArray();
	}
	
	public JSONArray getHouse(String buildingCode) {
		JSONObject jo = new JSONObject();
		jo.put("jsonrpc", "2.0");
		jo.put("method", "getHouse");
		JSONObject params=new JSONObject();
		params.put("pageSize", 2);
		params.put("page", 1);
		params.put("buildingCode", buildingCode);
		jo.put("params", params);
		jo.put("id", "" + System.currentTimeMillis());
		try {
			Map<String, String> map = new HashMap<>();
			map.put("key", key);
			map.put("json", jo.toJSONString());
			String post = post(serverUrl + "village", map, null);
			System.out.println(post);
			LOGGER.info("获取小区楼栋列表结果：{}", post);
			if (post != null) {
				JSONObject d = JSON.parseObject(post);
				JSONObject result = d.getJSONObject("result");
				if (result!=null) {
					return result.getJSONArray("houseList");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONArray();
	}
	

	public boolean addEvent(CarparkRecordHistory history) {
		JSONObject jo = new JSONObject();
		jo.put("jsonrpc", "2.0");
		jo.put("method", "addEvent");
		JSONObject params = new JSONObject();
		// deviceId String Required 设备编号
		// channel Number Required 通道号
		// entranceCode String Required 出入口编号
		// plateNo String Optional 车牌号码
		// plateCode Number Optional 车牌类型
		// plateColor String Optional 车牌颜色
		// carType Number Optional 车辆类型
		// triggerTime String Required 触发时间
		// eventCode Number Required 事件类型编码
		// similarity Number Optional 识别可信度
		// note String Optional 备注信息
		params.put("deviceId", StrUtil.isEmpty(history.getOutTime()) ? history.getInDeviceId() : history.getOutDeviceId());
		params.put("channel", 1);
		params.put("entranceCode", history.getEntranceCode());
		params.put("plateNo", history.getPlateNO());
		params.put("plateCode", mapPlateType.getOrDefault(history.getPlateType(), 1));
		params.put("plateColor", history.getPlateColor());
		
		String triggerTime = StrUtil.isEmpty(history.getOutTime()) ? history.getInTime() : history.getOutTime();
		params.put("triggerTime", triggerTime);
		String eventName = history.getEventName();
		if (eventName==null) {
			eventName=StrUtil.isEmpty(history.getOutTime()) ? "其他车辆进" : "其他车辆出";
		}
		int eventCode = mapEventCode.getOrDefault(eventName, StrUtil.isEmpty(history.getOutTime()) ? 25 : 26);
		int carType=mapCarType.getOrDefault(eventName.substring(0, eventName.length()-1), 12);
		if (!StrUtil.isEmpty(history.getUserName())) {
			eventCode = mapEventCode.getOrDefault(eventName, StrUtil.isEmpty(history.getOutTime()) ? 1: 2);
			carType=mapCarType.getOrDefault(eventName.substring(0, eventName.length()-1), 1);
		}
		
		params.put("carType", carType);
		params.put("accessType", StrUtil.isEmpty(history.getOutTime()) ? 1 : 2);
		params.put("carTypeCode", 10);
		params.put("eventCode", eventCode);
		params.put("similarity", 28);
		params.put("note", "备注");
		String image = history.getInImage();
		String plateImage = history.getInPlateImage();
		if (!StrUtil.isEmpty(history.getOutTime())) {
			image = history.getOutImage();
			plateImage = history.getOutPlateImage();
		}
		try {
			Map<String, String> map = new HashMap<>();
			Map<String, byte[]> images = new HashMap<>();
			if (image != null) {
				byte[] image2 = sp.getImageService().getImage(image.substring(image.lastIndexOf("/") + 1));
				if (!StrUtil.isEmpty(image2)) {
					String eventPic = StrUtil.formatDate(StrUtil.parseDateTime(triggerTime), "yyyyMMddHHmmss") + "big.jpg";
					params.put("eventPic", eventPic);
					images.put(eventPic, image2);
				}
			}
			if (plateImage != null) {
				byte[] image3 = sp.getImageService().getImage(plateImage.substring(plateImage.lastIndexOf("/") + 1));
				if (!StrUtil.isEmpty(image3)) {
					String platePic = StrUtil.formatDate(StrUtil.parseDateTime(triggerTime), "yyyyMMddHHmmss") + "plate.jpg";
					params.put("platePic", platePic);
					images.put(platePic, image3);
				}
			}
			jo.put("params", params);
			jo.put("id", "" + System.currentTimeMillis());
			map.put("key", key);
			map.put("json", jo.toJSONString());

			String post = post(serverUrl + "device/parking", map, images);
			LOGGER.info("推送进出记录返回：{}", post);

			if (post != null) {
				return true;
			}
		} catch (Exception e) {
			LOGGER.info("推送进出记录到上海agbox时发生错误", e);
		}
		return false;
	}

	public static String post(String urlStr, Map<String, String> params, Map<String, byte[]> images) {
		LOGGER.info("向地址：[{}] 发送消息：[{}] 文件：[{}]", urlStr, params, images);
		HttpURLConnection con = null;
		try {
			URL url = new URL(urlStr);
			con = (HttpURLConnection) url.openConnection();

			con.setConnectTimeout(5000);
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type", FILE_ENCTYPE + "; boundary=" + BOUNDARY);

			StringBuilder sb = null;
			DataOutputStream dos = new DataOutputStream(con.getOutputStream());
			;
			if (params != null) {
				sb = new StringBuilder();
				for (String s : params.keySet()) {
					sb.append("--");
					sb.append(BOUNDARY);
					sb.append("\r\n");
					sb.append("Content-Disposition: form-data; name=\"");
					sb.append(s);
					sb.append("\"\r\n\r\n");
					sb.append(params.get(s));
					sb.append("\r\n");
				}

				dos.write(sb.toString().getBytes("UTF-8"));
			}

			if (images != null) {
				for (String s : images.keySet()) {
					byte[] f = images.get(s);
					sb = new StringBuilder();
					sb.append("--");
					sb.append(BOUNDARY);
					sb.append("\r\n");
					sb.append("Content-Disposition: form-data; name=\"");
					sb.append(s);
					sb.append("\"; filename=\"");
					sb.append(s);
					sb.append("\"\r\n");
					sb.append("Content-Type: application/octet-stream");// 这里注意！如果上传的不是图片，要在这里改文件格式，比如txt文件，这里应该是text/plain
					sb.append("\r\n\r\n");
					dos.write(sb.toString().getBytes("UTF-8"));
					dos.write(f);
					dos.write("\r\n".getBytes("UTF-8"));
				}
				sb = new StringBuilder();
				sb.append("--");
				sb.append(BOUNDARY);
				sb.append("--\r\n");
				dos.write(sb.toString().getBytes("UTF-8"));
			}
			dos.flush();
			LOGGER.debug("通讯状态:{}",con.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			String readLine = br.readLine();
			dos.close();
			return readLine;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		// String url="http://cctvyz.gicp.net:8081/agbox/device/parking";
		// JSONObject jo=new JSONObject();
		// jo.put("jsonrpc", "2.0");
		// jo.put("method", "addEvent");
		// JSONObject params=new JSONObject();
		//// deviceId String Required 设备编号
		//// channel Number Required 通道号
		//// entranceCode String Required 出入口编号
		//// plateNo String Optional 车牌号码
		//// plateCode Number Optional 车牌类型
		//// plateColor String Optional 车牌颜色
		//// carType Number Optional 车辆类型
		//// triggerTime String Required 触发时间
		//// eventCode Number Required 事件类型编码
		//// similarity Number Optional 识别可信度
		//// note String Optional 备注信息
		// params.put("deviceId", "SubinTest");
		// params.put("channel", "1");
		// params.put("entranceCode", "9d30bf75-a6d8-462c-80b6-45e47790ae92");
		// params.put("plateNo", "粤BD021W");
		// params.put("plateCode", "1");
		// params.put("plateColor", "2");
		// params.put("carType", "10");
		// params.put("triggerTime", "2019-07-23 13:34:15");
		// params.put("eventCode", "7");
		// params.put("similarity", "28");
		// params.put("note", "备注");
		// String fileName = System.currentTimeMillis()+".jpg";
		// params.put("eventPic", fileName);
		// jo.put("params", params);
		// jo.put("id", ""+System.currentTimeMillis());
		//
		// Map<String, String> map=new HashMap<>();
		// map.put("key", key);
		// map.put("json", jo.toJSONString());
		// Map<String, byte[]> images=new HashMap<>();
		// images.put(fileName,
		// Files.readAllBytes(Paths.get("D:\\img\\20161122111651128_粤BD021W_big.jpg")));
		// String post = post(url, map, images);
		// System.out.println(post);

		// String url="http://cctvyz.gicp.net:8081/agbox/share/car";
		// JSONObject jo=new JSONObject();
		// jo.put("jsonrpc", "2.0");
		// jo.put("method", "updatePlate");
		// JSONObject params=new JSONObject();
		// params.put("plateNo", "粤BD021W");
		// params.put("plateTypeCode", "1");
		// params.put("carTypeCode", "1");
		// params.put("credentialType", "1");
		// params.put("credentialNo", "111");
		// params.put("enabled", "true");
		// params.put("set", "{}");
		// jo.put("params", params);
		// jo.put("id", ""+System.currentTimeMillis());
		//
		// Map<String, String> map=new HashMap<>();
		// map.put("key", key);
		// map.put("json", jo.toJSONString());
		// String post = post(url, map, null);
		// System.out.println(post);

		// String p="key=570670eb-9916-43a6-8a63-fa9e49ce1ef6&json="+jo.toJSONString();
		// System.out.println(p);
		// String httpPostMssage = HttpRequestUtil.httpPostMssage(url, p, 5000);
		// System.out.println(httpPostMssage);

		// getCarTypeCode();

		addUserInfo();
	}

	private static void addUserInfo() {
		String url = "http://cctvyz.gicp.net:8081/agbox/person";
		JSONObject jo = new JSONObject();
		jo.put("jsonrpc", "2.0");
		jo.put("method", "update");
		JSONObject params = new JSONObject();
		// peopleName String Optional 姓名
		// credentialType Number Required 证件类型
		// credentialNo String Required 证件号码
		// source Number Required 来源
		params.put("peopleName", "秦莞尔");
		params.put("credentialType", "1");
		params.put("credentialNo", "431022199111112215");
		params.put("source", "4");
		// params.put("entranceTypeCode", 1);
		jo.put("params", params);
		jo.put("id", "" + System.currentTimeMillis());

		Map<String, String> map = new HashMap<>();
		map.put("key", key);
		map.put("json", jo.toJSONString());
		String post = post(url, map, null);
		System.out.println(post);
	}

}
