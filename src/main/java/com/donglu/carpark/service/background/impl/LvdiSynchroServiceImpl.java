package com.donglu.carpark.service.background.impl;


import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CountTempCarChargeI;
import com.donglu.carpark.service.IpmsServiceI;
import com.donglu.carpark.service.background.AbstractCarparkBackgroundService;
import com.donglu.carpark.service.background.LvdiSynchroServiceI;
import com.donglu.carpark.service.impl.CountTempCarChargeImpl;
import com.donglu.carpark.ui.servlet.WebSocketClient;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkCarType;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.CarparkRecordHistory;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.ProcessEnum;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.UpdateEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;

public class LvdiSynchroServiceImpl extends AbstractCarparkBackgroundService implements LvdiSynchroServiceI {
	Logger LOGGER=LoggerFactory.getLogger(LvdiSynchroServiceImpl.class);
	private static final String IMPS_USER_SAVE_HISTORY = "impsUserSaveHistory";
	private CarparkDatabaseServiceProvider sp;
	private IpmsServiceI ipmsService;
	private boolean isRunService=false;
	private WebSocketClient client;
	Cache<String, CarparkRecordHistory> cacheHistory = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();

	@Inject
	public LvdiSynchroServiceImpl(IpmsServiceI ipmsService,CarparkDatabaseServiceProvider sp) {
		super(Scheduler.newFixedDelaySchedule(3, 3, TimeUnit.SECONDS), "绿地信息同步服务");
		this.ipmsService = ipmsService;
		this.sp = sp;
	}


	@Override
	protected void run() {
		try {
			if (client==null) {
				LOGGER.info("准备连接绿地服务器");
				client = new WebSocketClient("ws://103.21.117.90:4003/v1/carpark/poll?station_id=1&timestamp=1512032803&sign=86db9ee34187953d8e0e8adf43c6796a") {
					@Override
					public void onClose(int code, String reason, boolean remote) {
						LOGGER.error("连接断开：{}-{}",code,reason);
						client=null;
					}
					@Override
					public void onMessage(String message) {
						LOGGER.info("收到绿地返回消息:{}" ,message);
						JSONObject jo = JSON.parseObject(message);
						int command = jo.getIntValue("command");
						switch (command) {
						case 2:
							
							break;
						case 3:
							
							break;
						case 5:
							JSONObject data = jo.getJSONObject("data");
							String car = data.getString("car");
							List<SingleCarparkInOutHistory> list = sp.getCarparkInOutService().findByNoOut(car, null);
							if (StrUtil.isEmpty(list)) {
								data.put("code", -1);
								data.put("msg", "进场记录不存在");
								jo.put("data", data);
								client.send(jo.toJSONString());
								return;
							}
							SingleCarparkInOutHistory history = list.get(0);
							
							CountTempCarChargeI c=new CountTempCarChargeImpl();
							List<CarparkCarType> carparkCarTypeList = sp.getCarparkService().getCarparkCarTypeList();
							Long carTypeId=1l;
							for (CarparkCarType carparkCarType : carparkCarTypeList) {
								if (history.getUserType().equals(carparkCarType.getName())) {
									carTypeId=carparkCarType.getId();
								}
							}
							float charge = c.charge(history.getCarparkId(), carTypeId, history.getInTime(), new Date(), sp, history.getPlateNo(), true);
							data.put("enter_time", history.getInTime().getTime());
							data.put("fee_value", (int)(charge*100));
							data.put("discount", 0);
							data.put("total_fee", (int)(charge*100));
							data.put("code", 0);
							data.put("msg", "操作成功");
							jo.put("data", data);
							client.send(jo.toJSONString());
							break;
						default:
							break;
						}
					}
					@Override
					public void onError(Exception ex) {
						LOGGER.error("发生错误",ex);
						
					}
				};
				client.setConnectionLostTimeout(20);
				boolean connectBlocking = client.connectBlocking();
				if (connectBlocking) {
					LOGGER.info("连接服务器成功");
				}else {
					LOGGER.info("连接服务器失败");
					client=null;
					return;
				}
			}
			List<CarparkRecordHistory> list = sp.getCarparkInOutService().findHaiYuRecordHistory(0, 10, new UpdateEnum[] {UpdateEnum.新添加,UpdateEnum.被修改}, new ProcessEnum[] {ProcessEnum.未处理});
			for (CarparkRecordHistory history : list) {
				if (StrUtil.isEmpty(history.getOutTime())) {
					carIn(history);
				}else {
					carOut(history);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof WebsocketNotConnectedException) {
				client.close();
				client=null;
			}
		}
	}

	private void carOut(CarparkRecordHistory history) {
		JSONObject jo=new JSONObject();
		jo.put("command", 3);
		long currentTimeMillis = System.currentTimeMillis();
		jo.put("requestid", currentTimeMillis+"");
		jo.put("version", "1.0");
		jo.put("timestamp", currentTimeMillis);
		JSONObject data = new JSONObject();
		data.put("car", history.getPlateNO());
		data.put("image", "");
		data.put("pay_money", (int)(history.getFactMoney()*100));
		data.put("pay_type", history.getChargedType()==0?4:1);
		data.put("leave_time", StrUtil.parseDateTime(history.getOutTime()).getTime());
		jo.put("data", data);
		LOGGER.info("车辆出场数据：{}",jo);
		client.send(jo.toJSONString());
		LOGGER.info("发送成功");
	}


	private void carIn(CarparkRecordHistory history) {
//		LOGGER.info("车辆进场：{}-{}",history.getPlateNO(),history.getInTime());
		JSONObject jo=new JSONObject();
		jo.put("command", 2);
		long currentTimeMillis = System.currentTimeMillis();
		jo.put("requestid", currentTimeMillis+"");
		jo.put("version", "1.0");
		jo.put("timestamp", currentTimeMillis);
		JSONObject data = new JSONObject();
		data.put("car", history.getPlateNO());
		data.put("image", "");
		data.put("enter_time", StrUtil.parseDateTime(history.getInTime()).getTime());
		jo.put("data", data);
		LOGGER.info("车辆进场数据：{}",jo);
		client.send(jo.toJSONString());
		LOGGER.info("发送成功");
		cacheHistory.put(currentTimeMillis+"", history);
	}

	/**
	 * 
	 */
	public void checkSetting() {
		try {
			SingleCarparkSystemSetting findSystemSettingByKey = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.启用CJLAPP支付.name());
			if (findSystemSettingByKey != null && findSystemSettingByKey.getBooleanValue()) {
				isRunService=true;
			}else {
				isRunService=false;
			}
		} catch (Exception e) {
			log.error("检测设置时发生错误!",e);
		}
	}
	@Override
	protected void shutDown() throws Exception {
		super.shutDown();
	}
	@Override
	protected void startUp() throws Exception {
		super.startUp();
		checkSetting();
	}


}
