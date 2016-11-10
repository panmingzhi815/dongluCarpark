package com.donglu.carpark.service.background.impl;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.background.AbstractCarparkBackgroundService;
import com.donglu.carpark.service.background.ClientCheckSoftDogServiceI;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.setting.SNSettingType;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class ClientCheckSoftDogServiceImpl extends AbstractCarparkBackgroundService implements ClientCheckSoftDogServiceI {
	
	public ClientCheckSoftDogServiceImpl() {
		super(Scheduler.newFixedDelaySchedule(5, 30, TimeUnit.MINUTES), "客户端检查加密狗");
	}

	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CommonUIFacility commonui;
	
	private boolean isBetaVersion=false;
	private Date remindTime=null;
	@Override
	protected void run() {
		String defaultValue = SystemSettingTypeEnum.软件版本.getDefaultValue();
		if (defaultValue.indexOf("beta") > -1) {
			if (!isBetaVersion) {
				isBetaVersion = true;
				log.info("测试版本，3小时后关闭");
			} else {
				System.exit(0);
			}
			return;
		}
		try {
			log.info("客户端从数据库获取注册信息");
			Map<SNSettingType, SingleCarparkSystemSetting> findAllSN = sp.getCarparkService().findAllSN();
			String sn = findAllSN.get(SNSettingType.sn).getSettingValue();
			String validTo = findAllSN.get(SNSettingType.validTo).getSettingValue();

			if (StrUtil.isEmpty(sn) || StrUtil.isEmpty(validTo)) {
				log.info("没有检测到注册码，请检测服务器加密狗");
				commonui.error("检查失败", "没有检测到注册码，请检测服务器加密狗");
				System.exit(0);
				return;
			}
			if (new DateTime(validTo).minusDays(30).isBeforeNow()&&(remindTime==null||System.currentTimeMillis()-remindTime.getTime()>3600000)) {
				log.info("检查注册码成功,有效期至{},即将到期", validTo);
				commonui.info("注册码", "注册码即将到期，请及时注册！！！");
			}
			log.info("检查注册码成功,有效期至{}", validTo);
		} catch (Exception e) {
			commonui.error("检查失败", "没有检测到注册码，请检测服务器加密狗");
			System.exit(0);
		}
	}
}
