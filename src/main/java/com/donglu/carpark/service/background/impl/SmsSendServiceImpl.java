package com.donglu.carpark.service.background.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.background.AbstractCarparkBackgroundService;
import com.donglu.carpark.service.background.SmsSendServiceI;
import com.donglu.carpark.util.aliyun.AliyunSmsUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SmsInfo;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class SmsSendServiceImpl extends AbstractCarparkBackgroundService implements SmsSendServiceI {
	private static final Logger LOGGER=LoggerFactory.getLogger(SmsSendServiceImpl.class);
	private CarparkDatabaseServiceProvider sp;
	private String appid;
	private String appisecret;
	private String appsign;
	private String apptemp;
	
	@Inject
	public SmsSendServiceImpl(CarparkDatabaseServiceProvider sp) {
		super(Scheduler.newFixedDelaySchedule(10, 3, TimeUnit.SECONDS), "短信发送服务");
		this.sp = sp;
	}

	@Override
	protected void run() {
		try {
			List<SmsInfo> list = sp.getCarparkInOutService().findSmsInfoByStatus(10,new int[] { 1, 2 });
			if(list.isEmpty()) {
				list = sp.getCarparkInOutService().findSmsInfoByStatus(10,new int[] { 3 });
			}
			for (SmsInfo smsInfo : list) {
				if (smsInfo.getTemplateCode()==null||StrUtil.isEmpty(smsInfo.getTel())||smsInfo.getTel().length()<11) {
					smsInfo.setStatus(4);
					smsInfo.setRemark("格式不正确");
					sp.getCarparkInOutService().saveSmsInfo(smsInfo);
				}
				LOGGER.info("发送短信：{}-{}",smsInfo.getTel(),smsInfo.getData());
			    try {
			    	String sendSms = AliyunSmsUtil.sendSms(appid, appisecret, appsign, smsInfo.getTemplateCode(), smsInfo.getTel(), smsInfo.getData());
			    	LOGGER.info("发送短信返回：{}",sendSms);
			    	JSONObject result = JSON.parseObject(sendSms);
					if("OK".equals(result.getString("Code"))) {
						smsInfo.setStatus(0);
						smsInfo.setSendTime(new Date());
						smsInfo.setRemark("发送成功");
						sp.getCarparkInOutService().saveSmsInfo(smsInfo);
					    continue;
					}
					smsInfo.setStatus(4);
					smsInfo.setRemark(result.getString("Message"));
				} catch (Exception e) {
					smsInfo.setStatus(2);
					smsInfo.setRemark("发送失败,"+e);
				}
			    smsInfo.setSendTime(new Date());
			    sp.getCarparkInOutService().saveSmsInfo(smsInfo);
			}
		} catch (Exception e) {
			LOGGER.error("发送短信时发生错误"+e);
		}
	}

	@Override
	protected void startUp() throws Exception {
		LOGGER.info("启动短信发送服务");
		List<SingleCarparkSystemSetting> list = sp.getCarparkService().findAllSystemSetting(SystemSettingTypeEnum.启动短信发送服务, SystemSettingTypeEnum.短信服务appid, SystemSettingTypeEnum.短信服务appsecret,
				SystemSettingTypeEnum.短信签名, SystemSettingTypeEnum.短信模板);
		Map<SystemSettingTypeEnum, String> map = list.stream().collect(Collectors.toMap(t -> SystemSettingTypeEnum.valueOf(t.getSettingKey()), t -> t.getSettingValue()));
		boolean isStartService = Boolean.valueOf(map.get(SystemSettingTypeEnum.启动短信发送服务));
		if (!isStartService) {
			stopAsync();
			return;
		}
		appid = map.get(SystemSettingTypeEnum.短信服务appid);
		appisecret = map.get(SystemSettingTypeEnum.短信服务appsecret);
		appsign = map.get(SystemSettingTypeEnum.短信签名);
		apptemp = map.get(SystemSettingTypeEnum.短信模板);
	}
}
