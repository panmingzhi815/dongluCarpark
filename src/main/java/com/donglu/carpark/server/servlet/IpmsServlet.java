package com.donglu.carpark.server.servlet;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.server.HessianServlet;
import com.donglu.carpark.model.Result;
import com.donglu.carpark.server.WebSocketServer;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkQrCodeInOutService;
import com.donglu.carpark.service.IpmsServiceI;
import com.donglu.carpark.service.impl.CarparkQrCodeInOutServiceImpl;
import com.donglu.carpark.util.ShortURLUtils;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;

public class IpmsServlet extends HessianServlet implements IpmsServiceI {
	private static final Logger LOGGER=LoggerFactory.getLogger(IpmsServlet.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -5489797873783489607L;
	
	static Cache<String, String> mapQrInOutInfos=CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();
	static Map<String, CarparkQrCodeInOutService> mapStartedServices=new HashMap<>();
	@Inject
	private IpmsServiceI ipmsService;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Override
	public void init() throws ServletException {
		SingleCarparkSystemSetting key = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.启用CJLAPP支付.name());
		if (key==null||!key.getBooleanValue()) {
			return;
		}
		Map<String, SingleCarparkCarpark> mapYunIdToCarpark=new HashMap<>();
		List<SingleCarparkCarpark> list = sp.getCarparkService().findAllCarpark();
		list.forEach(e->{
			if(StrUtil.isEmpty(e.getYunIdentifier())||StrUtil.isEmpty(e.getYunBuildIdentifier())) {
				LOGGER.info("停车场：{}未设置云平台编号",e.getName());
				return;
			}
			mapYunIdToCarpark.put(e.getYunIdentifier(), e);
		});
		for (String string : mapYunIdToCarpark.keySet()) {
			try {
				startQrCodeInOutService(mapYunIdToCarpark.get(string).getYunBuildIdentifier());
			} catch (Exception e1) {
				LOGGER.info(e1.getMessage());
			}
		}
	}
	
	@Override
	public synchronized boolean startQrCodeInOutService(String buildId){
		try {
			LOGGER.debug("检测是否启动了进出场服务，建筑：{}",buildId);
			if (mapStartedServices.get(buildId)!=null) {
				return true;
			}
			LOGGER.info("启动二维码进出场服务 建筑:{}",buildId);
			CarparkQrCodeInOutServiceImpl carparkQrCodeInOutService=new CarparkQrCodeInOutServiceImpl();
			carparkQrCodeInOutService.initService(buildId,new CarparkQrCodeInOutService.CarparkQrCodeInOutCallback() {
				@Override
				public void call(String info) {
					try {
						info=info.trim();
						JSONObject jsonObject = JSONObject.parseObject(info);
						String type = jsonObject.getString("type");
						if (type.equals("PONG_MSG")) {
							return;
						}
						if(type.equals("synchData")) {
							LOGGER.info("暂时忽略synchData类型消息");
							return;
						}
						LOGGER.info("云平台推送消息：[{}]",info);
						broadcastInfo(info);
						if ("true".equals(System.getProperty("saveMqttMsg", "false"))) {
							JSONObject jsonObject2 = jsonObject.getJSONObject("data");
							synchronized (mapQrInOutInfos) {
								String deviceId = jsonObject2.getString("deviceId");
								if (!StrUtil.isEmpty(deviceId)) {
									mapQrInOutInfos.put(deviceId, info);
								} else if (!StrUtil.isEmpty(jsonObject2.getString("carNum"))) {
									mapQrInOutInfos.put(jsonObject2.getString("carNum").trim(), info);
								}
							} 
						}
					} catch (Exception e) {
						LOGGER.error("接收云平台推送消息时发生错误",e);
					}
				}
			});
			mapStartedServices.put(buildId, carparkQrCodeInOutService);
			LOGGER.info("启动二维码进出场服务成功 建筑:{}",buildId);
			return true;
		} catch (Exception e) {
			LOGGER.error("启动二维码进出场服务失败:{}",e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	protected void broadcastInfo(String info) {
		try {
			WebSocketServer.sendToAll(info);
			LOGGER.info("发送广播成功");
		} catch (Exception e) {
			LOGGER.info("发送广播失败",e);
		}
	}

	@Override
	public String getQrCodeInOutInfo(Collection<String> deviceIps,Collection<String> waitPlates){
		for (String ip : deviceIps) {
			synchronized (mapQrInOutInfos) {
				String info = mapQrInOutInfos.getIfPresent(ip);
				if (info!=null) {
					mapQrInOutInfos.invalidate(ip);
					return info;
				}
			}
		}
		for (String ip : waitPlates) {
			synchronized (mapQrInOutInfos) {
				String info = mapQrInOutInfos.getIfPresent(ip);
				if (info!=null) {
					mapQrInOutInfos.invalidate(ip);
					return info;
				}
			}
		}
		return null;
	}

	@Override
	public boolean addInOutHistory(SingleCarparkInOutHistory inOutHistory) {
		return ipmsService.addInOutHistory(inOutHistory);
	}

	@Override
	public boolean updateInOutHistory(SingleCarparkInOutHistory inOutHistory) {
		return ipmsService.updateInOutHistory(inOutHistory);
	}

	@Override
	public boolean addUser(SingleCarparkUser user) {
		return ipmsService.addUser(user);
	}

	@Override
	public boolean updateUser(SingleCarparkUser user) {
		return ipmsService.updateUser(user);
	}

	@Override
	public void updateTempCarChargeHistory() {
		ipmsService.updateTempCarChargeHistory();
	}

	@Override
	public void updateUserInfo() {
		ipmsService.updateUserInfo();
	}

	@Override
	public void updateFixCarChargeHistory() {
		ipmsService.updateFixCarChargeHistory();
	}

	@Override
	public int pay(SingleCarparkInOutHistory inout, float chargeMoney) {
		return ipmsService.pay(inout, chargeMoney);
	}

	@Override
	public boolean deleteUser(SingleCarparkUser user) {
		return ipmsService.deleteUser(user);
	}

	@Override
	public Result getPayResult(SingleCarparkInOutHistory inout) {
		Result payResult = ipmsService.getPayResult(inout);
		return payResult;
	}

	@Override
	public void updateParkSpace() {
		ipmsService.updateParkSpace();		
	}
	@Override
	public String long2ShortUrl(String qrCodeUrl) {
		String[] split = qrCodeUrl.split("\\?");
		qrCodeUrl=System.getProperty("qrUrl","http://www.dongluhitec.net/dongyun_pay/parking_detail.html");
		if (split.length>1) {
			qrCodeUrl+="?"+split[1];
		}
		String longToShort = ShortURLUtils.longToShort(qrCodeUrl);
		if (longToShort==null) {
			for (int i = 0; i < 2; i++) {
				longToShort = ShortURLUtils.longToShort(qrCodeUrl);
				if (longToShort!=null) {
					break;
				}
			}
		}
		return longToShort;
	}

	@Override
	public void synchroImage(int maxSize) {
		synchroImage(maxSize);
	}

	@Override
	public boolean pustFee(String parkingRecordId, double fee) {
		return ipmsService.pustFee(parkingRecordId, fee);
	}
	
	@Override
	public boolean sendMqtt(String id, String msg) {
		CarparkQrCodeInOutService service = mapStartedServices.get(id);
		if (service!=null) {
			return service.sendMqtt(id, msg);
		}
		return false;
	}

	@Override
	public boolean notifyDeviceCarIn(String deviceId, String plate) {
		return ipmsService.notifyDeviceCarIn(deviceId, plate);
	}
}
