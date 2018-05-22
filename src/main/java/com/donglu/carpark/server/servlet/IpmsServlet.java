package com.donglu.carpark.server.servlet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.server.HessianServlet;
import com.donglu.carpark.model.Result;
import com.donglu.carpark.service.CarparkQrCodeInOutService;
import com.donglu.carpark.service.IpmsServiceI;
import com.donglu.carpark.service.impl.CarparkQrCodeInOutServiceImpl;
import com.donglu.carpark.util.ShortURLUtils;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
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
	
	static Map<String, String> mapQrInOutInfos=new HashMap<>();
	static Map<String, CarparkQrCodeInOutService> mapStartedServices=new HashMap<>();
	@Inject
	private IpmsServiceI ipmsService;
	@Override
	public void init() throws ServletException {
	}
	
	@Override
	public boolean startQrCodeInOutService(String buildId){
		try {
			LOGGER.debug("检测是否启动了进出场服务，建筑：{}",buildId);
			if (mapStartedServices.get(buildId)!=null) {
				return true;
			}
			LOGGER.info("启动二维码进出场服务 建筑:{}",buildId);
			CarparkQrCodeInOutService carparkQrCodeInOutService=new CarparkQrCodeInOutServiceImpl();
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
						LOGGER.info("云平台推送消息：[{}]",info);
						JSONObject jsonObject2 = jsonObject.getJSONObject("data");
						synchronized (mapQrInOutInfos) {
							String deviceId = jsonObject2.getString("deviceId");
							if (!StrUtil.isEmpty(deviceId)) {
								mapQrInOutInfos.put(deviceId, info);
							}else{
								mapQrInOutInfos.put(jsonObject2.getString("carNum"), info);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
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
	
	@Override
	public String getQrCodeInOutInfo(Collection<String> deviceIps,Collection<String> waitPlates){
		for (String ip : deviceIps) {
			synchronized (mapQrInOutInfos) {
				String info = mapQrInOutInfos.remove(ip);
				if (info!=null) {
					return info;
				}
			}
		}
		for (String ip : waitPlates) {
			synchronized (mapQrInOutInfos) {
				String info = mapQrInOutInfos.remove(ip);
				if (info!=null) {
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

}
