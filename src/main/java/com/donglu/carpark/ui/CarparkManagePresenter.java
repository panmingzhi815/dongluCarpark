package com.donglu.carpark.ui;

import java.util.List;
import java.util.Map;


import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.list.BlackUserListPresenter;
import com.donglu.carpark.ui.view.OpenDoorLogPresenter;
import com.donglu.carpark.ui.view.ReturnAccountPresenter;
import com.donglu.carpark.ui.view.SystemLogPresenter;
import com.donglu.carpark.ui.view.carpark.CarparkPresenter;
import com.donglu.carpark.ui.view.inouthistory.InOutHistoryPresenter;
import com.donglu.carpark.ui.view.lockcar.LockCarPresenter;
import com.donglu.carpark.ui.view.setting.SettingPresenter;
import com.donglu.carpark.ui.view.store.StoreChargePresenter;
import com.donglu.carpark.ui.view.store.StoreFreePresenter;
import com.donglu.carpark.ui.view.store.StorePresenter;
import com.donglu.carpark.ui.view.systemuser.SystemUserListPresenter;
import com.donglu.carpark.ui.view.user.CarparkPayHistoryPresenter;
import com.donglu.carpark.ui.view.user.PrepaidUserPayHistoryPresenter;
import com.donglu.carpark.ui.view.user.UserPresenter;
import com.donglu.carpark.ui.view.visitor.VisitorPresenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkModuleEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
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
	
	
	private CarparkPayHistoryPresenter carparkPayHistoryPresenter;
	private ReturnAccountPresenter returnAccountPresenter;
	private InOutHistoryPresenter inOutHostoryPresenter;
	private BlackUserListPresenter blackUserListPresenter;
	private UserPresenter userPresenter;
	private SystemLogPresenter systemLogPresenter;
	private OpenDoorLogPresenter openDoorLogPresenter;
	private StorePresenter storePresenter;
	private StoreChargePresenter storeChargePresenter;
	private StoreFreePresenter storeFreePresenter;
	private SettingPresenter settingPresenter;
	private PrepaidUserPayHistoryPresenter prepaidUserPayHistoryPresenter;
	private LockCarPresenter lockCarPresenter;
	private CarparkPresenter carparkPresenter;
	private SystemUserListPresenter systemUserListPresenter;
	private VisitorPresenter visitorPresenter;

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
		carparkPayHistoryPresenter = Login.injector.getInstance(CarparkPayHistoryPresenter.class);
		returnAccountPresenter = Login.injector.getInstance(ReturnAccountPresenter.class);
		inOutHostoryPresenter = Login.injector.getInstance(InOutHistoryPresenter.class);
		blackUserListPresenter = Login.injector.getInstance(BlackUserListPresenter.class);
		userPresenter = Login.injector.getInstance(UserPresenter.class);
		systemLogPresenter = Login.injector.getInstance(SystemLogPresenter.class);
		openDoorLogPresenter = Login.injector.getInstance(OpenDoorLogPresenter.class);
		storePresenter = Login.injector.getInstance(StorePresenter.class);
		storeChargePresenter = Login.injector.getInstance(StoreChargePresenter.class);
		storeFreePresenter = Login.injector.getInstance(StoreFreePresenter.class);
		settingPresenter = Login.injector.getInstance(SettingPresenter.class);
		prepaidUserPayHistoryPresenter = Login.injector.getInstance(PrepaidUserPayHistoryPresenter.class);
		lockCarPresenter = Login.injector.getInstance(LockCarPresenter.class);
		carparkPresenter = Login.injector.getInstance(CarparkPresenter.class);
		systemUserListPresenter = Login.injector.getInstance(SystemUserListPresenter.class);
		visitorPresenter=Login.injector.getInstance(VisitorPresenter.class);
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



	

	public CarparkPayHistoryPresenter getCarparkPayHistoryPresenter() {
		return carparkPayHistoryPresenter;
	}

	public ReturnAccountPresenter getReturnAccountPresenter() {
		return returnAccountPresenter;
	}

	public InOutHistoryPresenter getInOutHostoryPresenter() {
		return inOutHostoryPresenter;
	}

	public BlackUserListPresenter getBlackUserListPresenter() {
		return blackUserListPresenter;
	}

	public UserPresenter getUserPresenter() {
		return userPresenter;
	}

	public SystemLogPresenter getSystemLogPresenter() {
		return systemLogPresenter;
	}

	public OpenDoorLogPresenter getOpenDoorLogPresenter() {
		return openDoorLogPresenter;
	}

	public StorePresenter getStorePresenter() {
		return storePresenter;
	}

	public StoreChargePresenter getStoreChargePresenter() {
		return storeChargePresenter;
	}

	public StoreFreePresenter getStoreFreePresenter() {
		return storeFreePresenter;
	}

	public SettingPresenter getSettingPresenter() {
		return settingPresenter;
	}

	public PrepaidUserPayHistoryPresenter getPrepaidUserPayHistoryPresenter() {
		return prepaidUserPayHistoryPresenter;
	}

	public LockCarPresenter getLockCarPresenter() {
		return lockCarPresenter;
	}

	public CarparkPresenter getCarparkPresenter() {
		return carparkPresenter;
	}

	public SystemUserListPresenter getSystemUserListPresenter() {
		return systemUserListPresenter;
	}

	public void setSelete(SingleCarparkModuleEnum module) {
		getView().select(module);
	}

	public VisitorPresenter getVisitorPresenter() {
		return visitorPresenter;	
	}

}
