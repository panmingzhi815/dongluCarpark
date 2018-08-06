package com.donglu.carpark.ui;

import java.util.List;
import java.util.Map;


import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.view.main.MainPresenter;
import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CarparkManagePresenter {
	// 停车场管理界面
	private CarparkManageApp view;

	@Inject
	private CarparkDatabaseServiceProvider sp;
	

	private MainPresenter mainPresenter;

	public CarparkManageApp getView() {
		return view;
	}

	public void setView(CarparkManageApp view) {
		this.view = view;
	}

	/**
	 * 初始化
	 */
	public void init() {
		mainPresenter = Login.injector.getInstance(MainPresenter.class);
		sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.登录登出, "登录了管理界面", System.getProperty(ConstUtil.USER_NAME));
		refreshSystemSetting();
	}
	/**
	 * 刷新系统设置
	 */
	private void refreshSystemSetting() {
		Map<SystemSettingTypeEnum, String> mapSystemSetting = view.getMapSystemSetting();
		for (SystemSettingTypeEnum type : SystemSettingTypeEnum.values()) {
			mapSystemSetting.put(type, type.getDefaultValue());
		}
		List<SingleCarparkSystemSetting> findAllSystemSetting = sp.getCarparkService().findAllSystemSetting();
		for (SingleCarparkSystemSetting singleCarparkSystemSetting : findAllSystemSetting) {
			if (!StrUtil.isEmpty(singleCarparkSystemSetting.getSettingValue())) {
				SystemSettingTypeEnum valueOf;
				try {
					valueOf = SystemSettingTypeEnum.valueOf(singleCarparkSystemSetting.getSettingKey());
				} catch (Exception e) {
					continue;
				}
				mapSystemSetting.put(valueOf, singleCarparkSystemSetting.getSettingValue());
			}
		}
	}

	public void systemExit() {
		sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.登录登出, "用户退出了管理界面", System.getProperty(ConstUtil.USER_NAME));
		System.exit(0);
	}

	public MainPresenter getMainPresenter() {
		return mainPresenter;
	}

}
