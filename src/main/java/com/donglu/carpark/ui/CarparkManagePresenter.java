package com.donglu.carpark.ui;

import java.util.List;
import java.util.Map;


import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.list.BlackUserListPresenter;
import com.donglu.carpark.ui.view.OpenDoorLogPresenter;
import com.donglu.carpark.ui.view.ReturnAccountPresenter;
import com.donglu.carpark.ui.view.SystemLogPresenter;
import com.donglu.carpark.ui.view.carpark.CarparkPresenter;
import com.donglu.carpark.ui.view.deviceerror.DeviceErrorPresenter;
import com.donglu.carpark.ui.view.free.TempCarFreePresenter;
import com.donglu.carpark.ui.view.img.ImageHistoryPresenter;
import com.donglu.carpark.ui.view.inouthistory.CarPayPresenter;
import com.donglu.carpark.ui.view.inouthistory.InOutHistoryPresenter;
import com.donglu.carpark.ui.view.lockcar.LockCarPresenter;
import com.donglu.carpark.ui.view.offline.CarparkOffLineHistoryPresenter;
import com.donglu.carpark.ui.view.setting.SettingPresenter;
import com.donglu.carpark.ui.view.store.StoreChargePresenter;
import com.donglu.carpark.ui.view.store.StoreFreePresenter;
import com.donglu.carpark.ui.view.store.StorePresenter;
import com.donglu.carpark.ui.view.systemuser.SystemUserListPresenter;
import com.donglu.carpark.ui.view.user.CarparkPayHistoryPresenter;
import com.donglu.carpark.ui.view.user.PrepaidUserPayHistoryPresenter;
import com.donglu.carpark.ui.view.user.UserPresenter;
import com.donglu.carpark.ui.view.visitor.VisitorPresenter;
import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkModuleEnum;
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
	private DeviceErrorPresenter deviceErrorPresenter;
	private CarparkOffLineHistoryPresenter carparkOffLineHistoryPresenter;
	private TempCarFreePresenter tempCarFreePresenter;
	private CarPayPresenter carPayPresenter;
	private ImageHistoryPresenter imageHistoryPresenter;

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
		deviceErrorPresenter=Login.injector.getInstance(DeviceErrorPresenter.class);
		carparkOffLineHistoryPresenter=Login.injector.getInstance(CarparkOffLineHistoryPresenter.class);
		tempCarFreePresenter=Login.injector.getInstance(TempCarFreePresenter.class);
		carPayPresenter=Login.injector.getInstance(CarPayPresenter.class);
		imageHistoryPresenter=Login.injector.getInstance(ImageHistoryPresenter.class);
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
		if (getView()!=null) {
			getView().select(module);
		}
	}

	public VisitorPresenter getVisitorPresenter() {
		return visitorPresenter;	
	}

	public void systemExit() {
		sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.登录登出, "用户退出了管理界面", System.getProperty(ConstUtil.USER_NAME));
		System.exit(0);
	}

	public DeviceErrorPresenter getDeviceErrorPresenter() {
		return deviceErrorPresenter;
	}

	public CarparkOffLineHistoryPresenter getCarparkOffLineHistoryPresenter() {
		return carparkOffLineHistoryPresenter;
	}
	public TempCarFreePresenter getTempCarFreePresenter() {
		return tempCarFreePresenter;
	}

	public CarPayPresenter getCarPayPresenter() {
		return carPayPresenter;
	}

	public ImageHistoryPresenter getImageHistoryPresenter() {
		return imageHistoryPresenter;
	}

}
