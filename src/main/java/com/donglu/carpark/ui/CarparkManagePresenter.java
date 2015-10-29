package com.donglu.carpark.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import com.donglu.carpark.info.CarparkChargeInfo;
import com.donglu.carpark.model.CarparkModel;
import com.donglu.carpark.model.InOutHistoryModel;
import com.donglu.carpark.model.SystemUserModel;
import com.donglu.carpark.model.UserModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkInOutServiceI;
import com.donglu.carpark.service.CarparkService;
import com.donglu.carpark.service.CarparkUserService;
import com.donglu.carpark.service.CountTempCarChargeI;
import com.donglu.carpark.service.SystemUserServiceI;
import com.donglu.carpark.service.impl.CountTempCarChargeImpl;
import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.list.BlackUserListPresenter;
import com.donglu.carpark.ui.list.CarparkPayHistoryListView;
import com.donglu.carpark.ui.list.TestPresenter;
import com.donglu.carpark.ui.view.CarparkPayHistoryPresenter;
import com.donglu.carpark.ui.view.InOutHistoryPresenter;
import com.donglu.carpark.ui.view.ReturnAccountPresenter;
import com.donglu.carpark.ui.view.UserPresenter;
import com.donglu.carpark.ui.wizard.AddCarparkChildWizard;
import com.donglu.carpark.ui.wizard.AddCarparkWizard;
import com.donglu.carpark.ui.wizard.AddMonthChargeWizard;
import com.donglu.carpark.ui.wizard.AddSystemUserWizard;
import com.donglu.carpark.ui.wizard.AddUserModel;
import com.donglu.carpark.ui.wizard.AddUserWizard;
import com.donglu.carpark.ui.wizard.EditSystemUserWizard;
import com.donglu.carpark.ui.wizard.charge.NewCommonChargeModel;
import com.donglu.carpark.ui.wizard.charge.NewCommonChargeWizard;
import com.donglu.carpark.ui.wizard.holiday.AddYearHolidayModel;
import com.donglu.carpark.ui.wizard.holiday.AddYearHolidayWizard;
import com.donglu.carpark.ui.wizard.model.AddMonthChargeModel;
import com.donglu.carpark.ui.wizard.monthcharge.MonthlyUserPayModel;
import com.donglu.carpark.ui.wizard.monthcharge.MonthlyUserPayWizard;
import com.donglu.carpark.util.ExcelImportExport;
import com.donglu.carpark.util.ExcelImportExportImpl;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkAcrossDayTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeStandard;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkDurationPrice;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkDurationStandard;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkDurationTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkHolidayTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.Holiday;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.mapper.BeanUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class CarparkManagePresenter {
	// private Logger LOGGER = LoggerFactory.getLogger(CarparkManagePresenter.class);
	// 停车场管理界面
	private CarparkManageApp view;

	private CarparkModel carparkModel;// 停车场列表信息

	private UserModel userModel;// 固定用户信息

	private SystemUserModel systemUserModel;
	private InOutHistoryModel inOutHistoryModel;
	@Inject
	private CarparkDatabaseServiceProvider sp;

	@Inject
	private CommonUIFacility commonui;

	@Inject
	private CarparkPayHistoryPresenter carparkPayHistoryPresenter;
	@Inject
	private ReturnAccountPresenter returnAccountPresenter;
	@Inject
	private InOutHistoryPresenter inOutHostoryPresenter;
	@Inject
	private BlackUserListPresenter blackUserListPresenter;
	@Inject
	private UserPresenter userPresenter;

	@Inject
	private TestPresenter p;

	/**
	 * 删除停车场
	 */
	public void deleteCarpark() {
		try {
			CarparkService carparkService = sp.getCarparkService();
			SingleCarparkCarpark carpark = carparkModel.getCarpark();
			if (StrUtil.isEmpty(carpark)) {
				return;
			}
			boolean confirm = commonui.confirm("删除提示", "是否删除选中停车场");
			if (!confirm) {
				return;
			}
			carparkService.deleteCarpark(carpark);
			commonui.info("提示", "删除成功！");
			refreshCarpark();
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("提示", "删除失败！" + e.getMessage());
		}
	}

	/**
	 * 添加停车场
	 */
	public void addCarpark() {
		SingleCarparkCarpark model = new SingleCarparkCarpark();
		addAndEditCarpark(model);
	}

	/**
	 * @param model
	 */
	private void addAndEditCarpark(SingleCarparkCarpark model) {
		try {
			CarparkService carparkService = sp.getCarparkService();
			AddCarparkWizard w = new AddCarparkWizard(model,sp);
			SingleCarparkCarpark showWizard = (SingleCarparkCarpark) commonui.showWizard(w);
			if (StrUtil.isEmpty(showWizard)) {
				return;
			}
			showWizard.setTempNumberOfSlot(showWizard.getTotalNumberOfSlot() - showWizard.getFixNumberOfSlot());
			showWizard.setLeftNumberOfSlot(showWizard.getTotalNumberOfSlot());
			carparkService.saveCarpark(showWizard);
			refreshCarpark();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 添加子停车场
	 */
	public void addChildCapark() {
		SingleCarparkCarpark model = new SingleCarparkCarpark();
		addAndEditChildCarpark(model);
	}

	/**
	 * @param model
	 */
	private void addAndEditChildCarpark(SingleCarparkCarpark model) {
		try {
			SingleCarparkCarpark carpark = carparkModel.getCarpark();
			if (carpark == null) {
				commonui.info("提示", "请先选择一个停车场");
				return;
			}
			CarparkService carparkService = sp.getCarparkService();
			AddCarparkChildWizard w = new AddCarparkChildWizard(model,sp);
			SingleCarparkCarpark showWizard = (SingleCarparkCarpark) commonui.showWizard(w);
			if (StrUtil.isEmpty(showWizard)) {
				return;
			}
			
			showWizard.setParent(carpark);
			showWizard.setTempNumberOfSlot(0);
			showWizard.setLeftNumberOfSlot(0);
			showWizard.setTotalNumberOfSlot(0);
			carparkService.saveCarpark(showWizard);
			refreshCarpark();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 编辑停车场
	 */
	public void editCarpark() {
		try {
			SingleCarparkCarpark carpark = carparkModel.getCarpark();
			if (StrUtil.isEmpty(carpark.getParent())) {
				addAndEditCarpark(carpark);
			}else{
				addAndEditChildCarpark(carpark);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 固定用户充值
	 */
	public void monthUserPay() {
		try {
			MonthlyUserPayModel model = new MonthlyUserPayModel();
			List<SingleCarparkUser> selectList = userModel.getSelectList();
			if (StrUtil.isEmpty(selectList)) {
				return;
			}

			SingleCarparkUser singleCarparkUser = selectList.get(0);
			model.setUserName(singleCarparkUser.getName());
			model.setCreateTime(singleCarparkUser.getCreateDate());
			model.setPlateNO(singleCarparkUser.getPlateNo());
			model.setAllmonth(sp.getCarparkService().findAllMonthlyCharge());
			model.setOverdueTime(singleCarparkUser.getValidTo());
			MonthlyUserPayWizard wizard = new MonthlyUserPayWizard(model);
			MonthlyUserPayModel m = (MonthlyUserPayModel) commonui.showWizard(wizard);
			if (StrUtil.isEmpty(m)) {
				return;
			}
			if (StrUtil.isEmpty(m.getSelectMonth())) {
				return;
			}
			singleCarparkUser.setValidTo(m.getOverdueTime());
			singleCarparkUser.setDelayDays(m.getSelectMonth().getDelayDays());
			singleCarparkUser.setRemindDays(m.getSelectMonth().getExpiringDays());
			singleCarparkUser.setMonthChargeId(m.getSelectMonth().getId());
			m.setOperaName(System.getProperty("userName"));
			sp.getCarparkUserService().saveUser(singleCarparkUser);
			sp.getCarparkService().saveMonthlyUserPayHistory(m.getSingleCarparkMonthlyUserPayHistory());
			commonui.info("操作成功", "充值成功");
			refreshUser();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 添加停车场用户
	 */
	public void addCarparkUser() {
		try {
			CarparkService carparkService = sp.getCarparkService();
			List<SingleCarparkCarpark> list = carparkService.findAllCarpark();
			if (StrUtil.isEmpty(list)) {
				commonui.error("错误", "请先创建停车场！！");
				return;
			}
			MonthlyUserPayModel model = new MonthlyUserPayModel();
			model.setAllmonth(sp.getCarparkService().findAllMonthlyCharge());
			AddUserModel addUserModel = new AddUserModel();
			addUserModel.setAllList(list);
			addUserModel.setModel(model);
			AddUserWizard addUserWizard = new AddUserWizard(addUserModel);
			AddUserModel m = (AddUserModel) commonui.showWizard(addUserWizard);
			if (m == null) {
				return;
			}

			SingleCarparkUser user = m.getSingleCarparkUser();
			Date createDate = m.getCreateDate() == null ? new Date() : m.getCreateDate();
			user.setCreateDate(createDate);
			user.setValidTo(m.getModel().getOverdueTime());
			CarparkUserService carparkUserService = sp.getCarparkUserService();

			MonthlyUserPayModel mm = m.getModel();
			mm.setOperaName(System.getProperty("userName"));
			SingleCarparkMonthlyCharge selectMonth = mm.getSelectMonth();
			if (!StrUtil.isEmpty(selectMonth)) {
				user.setDelayDays(selectMonth.getDelayDays());
				user.setRemindDays(selectMonth.getExpiringDays());
				user.setMonthChargeId(selectMonth.getId());
			}
			carparkUserService.saveUser(user);
			carparkService.saveMonthlyUserPayHistory(mm.getSingleCarparkMonthlyUserPayHistory());
			// 更新停车场信息
//			int userSize = sp.getCarparkService().countMonthUserByHaveCarSite();
//			SingleCarparkCarpark carpark = m.getCarpark();
//			carpark.setLeftNumberOfSlot(carpark.getTotalNumberOfSlot() - userSize);
//			carparkService.saveCarpark(carpark);
			//
			commonui.info("操作成功", "保存成功!");
			refreshUser();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 删除停车场用户
	 */
	public void delCarparkUser() {
		List<SingleCarparkUser> selectList = userModel.getSelectList();
		if (StrUtil.isEmpty(selectList)) {
			return;
		}
		boolean confirm = commonui.confirm("删除提示", "是否确认删除选中的" + selectList.size() + "个固定用户");
		if (!confirm) {
			return;
		}
		CarparkUserService carparkUserService = sp.getCarparkUserService();
		for (SingleCarparkUser singleCarparkUser : selectList) {
			try {
				carparkUserService.deleteUser(singleCarparkUser);
			} catch (Exception e) {
				e.printStackTrace();
				commonui.error("操作终止", "删除用户" + singleCarparkUser.getName() + "的车牌" + singleCarparkUser.getPlateNo() + "失败");
				return;
			}
		}
		refreshUser();
	}

	/**
	 * 编辑停车场用户
	 */
	public void editCarparkUser() {
		List<SingleCarparkUser> selectList = userModel.getSelectList();
		if (StrUtil.isEmpty(selectList)) {
			return;
		}
		try {
			CarparkService carparkService = sp.getCarparkService();
			List<SingleCarparkCarpark> list = carparkService.findAllCarpark();
			SingleCarparkUser singleCarparkUser = selectList.get(0);
			AddUserModel addUserModel = new AddUserModel();
			addUserModel.setAllList(list);
			addUserModel.setSingleCarparkUser(singleCarparkUser);
			AddUserWizard addUserWizard = new AddUserWizard(addUserModel);
			AddUserModel m = (AddUserModel) commonui.showWizard(addUserWizard);
			if (m == null) {
				return;
			}

			SingleCarparkUser user = m.getSingleCarparkUser();
			CarparkUserService carparkUserService = sp.getCarparkUserService();
			carparkUserService.saveUser(user);
			commonui.info("操作成功", "修改成功!");
			refreshUser();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CarparkManageApp getView() {
		return view;
	}

	public void setView(CarparkManageApp view) {
		this.view = view;
	}

	public CarparkModel getCarparkModel() {
		return carparkModel;
	}

	public void setCarparkModel(CarparkModel carparkModel) {
		this.carparkModel = carparkModel;
	}

	/**
	 * 初始化
	 */
	public void init() {
		refreshCarpark();
		refreshUser();
		refreshSystemUser();
		refreshSystemSetting();
		refreshCarparkCharge();
		// testDatabase();
	}

	public void testDatabase() {

		// String property = System.getProperty("testDatabase");
		// if(Strings.isNullOrEmpty(property)){}
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
			try {
				SingleCarparkInOutHistory singleCarparkInOutHistory = new SingleCarparkInOutHistory();
				// singleCarparkInOutHistory.setBigImg("2015\10\09\16\20151009162220491_粤BD021W_big.jpg");
				// singleCarparkInOutHistory.setSmallImg("2015\10\09\16\20151009162220491_粤BD021W_big.jpg");
				singleCarparkInOutHistory.setCarType("临时车");
				singleCarparkInOutHistory.setFactMoney(15F);
				singleCarparkInOutHistory.setFreeMoney(5F);
				singleCarparkInOutHistory.setShouldMoney(20F);
				singleCarparkInOutHistory.setInTime(new Date());
				singleCarparkInOutHistory.setOutTime(new Date());
				singleCarparkInOutHistory.setInDevice("进口");
				singleCarparkInOutHistory.setOutDevice("出口");
				singleCarparkInOutHistory.setReturnAccount(456789L);
				singleCarparkInOutHistory.setPlateNo("京A78945");
				singleCarparkInOutHistory.setUserId(1L);
				singleCarparkInOutHistory.setUserName("xiaobai");
				sp.getCarparkInOutService().saveInOutHistory(singleCarparkInOutHistory);
				// LOGGER.info("保存成功");
			} catch (Exception e) {
				// LOGGER.error("保存错误",e);
			}

		} , 5000, 10, TimeUnit.MILLISECONDS);
	}

	/**
	 * 刷新系统设置
	 */
	private void refreshSystemSetting() {
		Map<SystemSettingTypeEnum, String> mapSystemSetting = view.getMapSystemSetting();
		List<SingleCarparkSystemSetting> findAllSystemSetting = sp.getCarparkService().findAllSystemSetting();
		for (SingleCarparkSystemSetting singleCarparkSystemSetting : findAllSystemSetting) {
			if (!StrUtil.isEmpty(singleCarparkSystemSetting.getSettingValue())) {
				mapSystemSetting.put(SystemSettingTypeEnum.valueOf(singleCarparkSystemSetting.getSettingKey()), singleCarparkSystemSetting.getSettingValue());
			}
		}
	}

	/**
	 * 刷新固定用户
	 */
	private void refreshUser() {
		CarparkUserService carparkUserService = sp.getCarparkUserService();
		List<SingleCarparkUser> findAll = carparkUserService.findAll();
		userModel.setAllList(findAll);
	}

	/**
	 * 刷新停车场
	 */
	public void refreshCarpark() {
		carparkModel.setListCarpark(Collections.emptyList());
		CarparkService carparkService = sp.getCarparkService();
		List<SingleCarparkCarpark> list = carparkService.findCarparkToLevel();
		if (!StrUtil.isEmpty(list)) {
			carparkModel.setCarpark(list.get(0));
		}
		carparkModel.setListCarpark(list);
		view.expandAllCarpark();
	}

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

	/**
	 * 添加系统用户
	 */
	public void addSystemUser() {
		try {
			SystemUserModel s = new SystemUserModel();
			AddSystemUserWizard wizard = new AddSystemUserWizard(s);
			SystemUserModel m = (SystemUserModel) commonui.showWizard(wizard);
			if (m == null) {
				return;
			}
			if (!check(System.getProperty("userType"), m.getType())) {
				commonui.error("操作终止", "您没有权限添加" + m.getType() + "账号");
				return;
			}
			SingleCarparkSystemUser systemUser = new SingleCarparkSystemUser();
			systemUser.setCreateDate(new Date());
			systemUser.setPassword(m.getPwd());
			systemUser.setRemark(m.getRemark());
			systemUser.setType(m.getType());
			systemUser.setUserName(m.getUserName());
			SystemUserServiceI systemUserService = sp.getSystemUserService();
			systemUserService.saveSystemUser(systemUser);
			commonui.info("操作成功", "添加用户成功！");
		} catch (Exception e) {
			e.printStackTrace();
			commonui.info("操作失败", "添加用户失败！");
		} finally {
			refreshSystemUser();
		}
	}

	/**
	 * 删除系统用户
	 */
	public void deleteSystemUser() {
		List<SingleCarparkSystemUser> selectList = systemUserModel.getSelectList();
		if (StrUtil.isEmpty(selectList)) {
			return;
		}
		boolean confirm = commonui.confirm("删除提示", "是否删除选中的" + selectList.size() + "个系统用户");
		if (!confirm) {
			return;
		}
		String property = System.getProperty("userType");
		SystemUserServiceI systemUserService = sp.getSystemUserService();
		for (SingleCarparkSystemUser singleCarparkSystemUser : selectList) {
			boolean check = check(property, singleCarparkSystemUser.getType());
			if (!check) {
				commonui.error("操作终止", "您没有权限去删除系统用户：" + singleCarparkSystemUser.getUserName());
				break;
			}
			try {
				systemUserService.removeSystemUser(singleCarparkSystemUser);
				commonui.info("操作成功", "删除系统用户成功");
			} catch (Exception e) {
				e.printStackTrace();
				commonui.error("操作失败", "删除系统用户" + singleCarparkSystemUser.getUserName() + "失败");
				break;
			}
		}
		refreshSystemUser();
	}

	/**
	 * 修改系统用户名密码
	 */
	public void editSystemUser() {
		List<SingleCarparkSystemUser> selectList = systemUserModel.getSelectList();
		if (StrUtil.isEmpty(selectList)) {
			return;
		}
		SingleCarparkSystemUser singleCarparkSystemUser = selectList.get(0);
		String property = System.getProperty("userType");
		boolean result = check(property, singleCarparkSystemUser.getType());
		if (!result) {
			commonui.error("操作终止", "您没有权限修改系统用户：" + singleCarparkSystemUser.getUserName());
			return;
		}
		SystemUserModel model = new SystemUserModel();
		model.setUserName(singleCarparkSystemUser.getUserName());
		model.setRemark(singleCarparkSystemUser.getRemark());
		EditSystemUserWizard wizard = new EditSystemUserWizard(model);
		SystemUserModel m = (SystemUserModel) commonui.showWizard(wizard);
		if (m == null) {
			return;
		}
		singleCarparkSystemUser.setPassword(m.getPwd());
		singleCarparkSystemUser.setLastEditDate(new Date());
		singleCarparkSystemUser.setLastEditUser(System.getProperty("userName"));
		singleCarparkSystemUser.setRemark(m.getRemark());
		try {
			sp.getSystemUserService().saveSystemUser(singleCarparkSystemUser);
			commonui.info("提示", "修改成功！");
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("提示", "修改失败！");
		}

	}

	/**
	 * 添加临时收费
	 */
	public void addTempCharge(CarparkChargeStandard carparkCharge) {
		SingleCarparkCarpark current = carparkModel.getCarpark();
		if (current == null) {
			commonui.error("错误", "请先选择一个停车场");
			return;
		}

		final CarparkService carparkService = sp.getCarparkService();
		final CarparkChargeStandard carparkChargeStandard = new CarparkChargeStandard();

		NewCommonChargeModel model = new NewCommonChargeModel();
		if (carparkCharge != null) {
			BeanUtil.copyProperties(carparkCharge, model, CarparkChargeStandard.Property.values());
			model.setFreeTimeEnable(model.getAcrossdayChargeEnable() == 1 ? "是" : "否");
		} else {
			model.setFreeTime(0);
			model.setOnedayMaxCharge(0F);
			model.setStartStepPrice(0F);
			model.setStartStepTime(0);
		}
		model.setCarparkCarTypeList(carparkService.getCarparkCarTypeList());
		NewCommonChargeWizard wizard = new NewCommonChargeWizard(model, sp, commonui);

		// NewCommonChargeWizard newCommonChargeWizard = wizardFactory.createNewCommonChargeWizard(model);
		// NewCommonChargeModel resultModel = (NewCommonChargeModel)commonui.showWizard(newCommonChargeWizard);
		NewCommonChargeModel resultModel = (NewCommonChargeModel) commonui.showWizard(wizard);
		if (resultModel == null)
			return;
		BeanUtil.copyProperties(resultModel, carparkChargeStandard, CarparkChargeStandard.Property.values());
		try {
			carparkChargeStandard.setCarpark(current);
			carparkService.saveCarparkChargeStandard(carparkChargeStandard);
			refreshCarparkCharge();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// NewCommonChargeWizard wizard =new NewCommonChargeWizard(new NewCommonChargeModel(), sp, commonui);
		// commonui.showWizard(wizard);

	}

	/**
	 * 添加月租收费设置
	 */
	public void addMonthCharge() {
		SingleCarparkCarpark carpark = carparkModel.getCarpark();
		if (carpark == null) {
			commonui.error("提示", "请先选择一个停车场");
			return;
		}
		AddMonthChargeModel init = AddMonthChargeModel.init();
		init.setCarpark(carpark);
		addAndEditMonthCharge(init);
	}

	private void addAndEditMonthCharge(AddMonthChargeModel init) {
		try {
			AddMonthChargeWizard w = new AddMonthChargeWizard(init,sp);
			AddMonthChargeModel m = (AddMonthChargeModel) commonui.showWizard(w);
			if (m == null) {
				return;
			}
			SingleCarparkMonthlyCharge monthlyCharge=m.getSingleCarparkMonthlyCharge();
			
			if (!StrUtil.isEmpty(monthlyCharge.getId())) {
				List<SingleCarparkUser> list=sp.getCarparkUserService().findUserByMonthChargeId(monthlyCharge.getId());
				for (SingleCarparkUser singleCarparkUser : list) {
					singleCarparkUser.setRemindDays(monthlyCharge.getExpiringDays());
					singleCarparkUser.setDelayDays(monthlyCharge.getDelayDays());
				}
				sp.getCarparkUserService().saveUserByMany(list);
			}
			sp.getCarparkService().saveMonthlyCharge(monthlyCharge);
			refreshCarparkCharge();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检测登录用户权限
	 * 
	 * @param loginType
	 * @param type
	 * @return
	 */
	private boolean check(String loginType, String type) {
		if (loginType.equals("系统管理员")) {
			return true;
		}
		if (loginType.equals("普通管理员")) {
			if (type.equals("操作员")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 刷新系统用户
	 */
	public void refreshSystemUser() {
		SystemUserServiceI systemUserService = sp.getSystemUserService();
		List<SingleCarparkSystemUser> findAll = systemUserService.findAll();
		systemUserModel.setList(findAll);
	}

	/**
	 * 刷新停车场收费设置
	 */
	public void refreshCarparkCharge() {
		SingleCarparkCarpark carpark = carparkModel.getCarpark();
		if (StrUtil.isEmpty(carpark)) {
			return;
		}
		List<SingleCarparkMonthlyCharge> listCharge = sp.getCarparkService().findMonthlyChargeByCarpark(carpark);
		List<CarparkChargeInfo> list = new ArrayList<>();
		for (SingleCarparkMonthlyCharge singleCarparkMonthlyCharge : listCharge) {
			CarparkChargeInfo cci = new CarparkChargeInfo();
			cci.setCode(singleCarparkMonthlyCharge.getChargeCode());
			cci.setName(singleCarparkMonthlyCharge.getChargeName());
			cci.setId(singleCarparkMonthlyCharge.getId());
			cci.setType("固定月租收费");
			list.add(cci);
		}

		List<CarparkChargeStandard> listTemp = sp.getCarparkService().findTempChargeByCarpark(carpark);
		for (CarparkChargeStandard t : listTemp) {
			CarparkChargeInfo cci = new CarparkChargeInfo();
			cci.setCode(t.getCode());
			cci.setName(t.getName());
			cci.setId(t.getId());
			if (t.getUsing()==null||!t.getUsing()) {
				cci.setUseType("未启用");
			}else{
				cci.setUseType("已启用");
			}
			cci.setCarType(t.getCarparkCarType().getName());
			cci.setHolidayType(t.getCarparkHolidayTypeEnum().name());
			cci.setType("临时收费");
			list.add(cci);
		}
		carparkModel.setListCarparkCharge(list);
	}

	public SystemUserModel getSystemUserModel() {
		return systemUserModel;
	}

	public void setSystemUserModel(SystemUserModel systemUserModel) {
		this.systemUserModel = systemUserModel;
	}

	/**
	 * 删除收费设置
	 */
	public void deleteCarparkCharge() {
		CarparkChargeInfo carparkChargeInfo = carparkModel.getCarparkChargeInfo();
		if (StrUtil.isEmpty(carparkChargeInfo)) {
			commonui.info("", "请先选择一个收费设置");
			return;
		}
		boolean confirm = commonui.confirm("删除确认", "是否删除编号为[" + carparkChargeInfo.getCode() + "]名称为[" + carparkChargeInfo.getName() + "]的收费设置");
		if (!confirm) {
			return;
		}
		if (carparkChargeInfo.getType().equals(CarparkChargeTypeEnum.固定月租收费.name())) {
			sp.getCarparkService().deleteMonthlyCharge(carparkChargeInfo.getId());
		}
		if (carparkChargeInfo.getType().equals(CarparkChargeTypeEnum.临时收费.name())) {
			sp.getCarparkService().deleteTempCharge(carparkChargeInfo.getId());
		}
		refreshCarparkCharge();
	}

	public InOutHistoryModel getInOutHistoryModel() {
		return inOutHistoryModel;
	}

	public void setInOutHistoryModel(InOutHistoryModel inOutHistoryModel) {
		this.inOutHistoryModel = inOutHistoryModel;
	}

	/**
	 * 查询进出场记录
	 * 
	 * @param plateNo
	 * @param userName
	 * @param start
	 * @param end
	 * @param operaName
	 * @param carType
	 * @param inout
	 */
	public void search(String plateNo, String userName, Date start, Date end, String operaName, String carType, String inout) {
		CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
		List<SingleCarparkInOutHistory> findByCondition = carparkInOutService.findByCondition(inOutHistoryModel.getListSearch().size(), 200, plateNo, userName, carType, inout, start, end, operaName,
				inout, inout, null);
		Long countByCondition = carparkInOutService.countByCondition(plateNo, userName, carType, inout, start, end, operaName, inout, inout, null);
		inOutHistoryModel.addListSearch(findByCondition);
		inOutHistoryModel.setCountSearch(inOutHistoryModel.getListSearch().size());
		inOutHistoryModel.setCountSearchAll(countByCondition.intValue());

	}

	/**
	 * 刷新进出场记录
	 */
	public void refreshSearchInOut() {
		inOutHistoryModel.setListSearch(new ArrayList<>());
		search(null, null, null, null, null, null, null);
	}

	// 数据库备份
	public void backup(String text) {

	}

	/**
	 * 保存设置信息
	 */
	public void saveAllSystemSetting() {
		Map<SystemSettingTypeEnum, String> mapSystemSetting = view.getMapSystemSetting();
		CarparkService carparkService = sp.getCarparkService();
		for (SystemSettingTypeEnum t : mapSystemSetting.keySet()) {
			SingleCarparkSystemSetting h = new SingleCarparkSystemSetting();
			h.setSettingKey(t.name());
			h.setSettingValue(mapSystemSetting.get(t) == null ? t.getDefaultValue() : mapSystemSetting.get(t));
			carparkService.saveSystemSetting(h);
		}
	}

	public void searchCharge(CarparkPayHistoryListView carparkPayHistoryListView, String userName, String operaName, Date start, Date end) {
		AbstractListView<SingleCarparkMonthlyUserPayHistory>.Model model = carparkPayHistoryListView.getModel();
		List<SingleCarparkMonthlyUserPayHistory> list = sp.getCarparkService().findMonthlyUserPayHistoryByCondition(0, 50, userName, operaName, start, end);
		int countSearchAll = sp.getCarparkService().countMonthlyUserPayHistoryByCondition(userName, operaName, start, end);
		model.setList(list);
		model.setCountSearch(list.size());
		model.setCountSearchAll(countSearchAll);

	}

	public CarparkPayHistoryPresenter getCarparkPayHistoryPresenter() {
		return carparkPayHistoryPresenter;
	}

	public ReturnAccountPresenter getReturnAccountPresenter() {
		return returnAccountPresenter;
	}

	public TestPresenter getP() {
		return p;
	}

	public InOutHistoryPresenter getInOutHostoryPresenter() {
		return inOutHostoryPresenter;
	}

	public void editCarparkChargeSetting() {
		try {
			CarparkChargeInfo carparkChargeInfo = carparkModel.getCarparkChargeInfo();
			if (StrUtil.isEmpty(carparkChargeInfo)) {
				return;
			}
			CarparkService carparkService = sp.getCarparkService();
			if (carparkChargeInfo.getType().equals(CarparkChargeTypeEnum.固定月租收费.name())) {
				SingleCarparkMonthlyCharge monthlyCharge = carparkService.findMonthlyChargeById(carparkChargeInfo.getId());

				AddMonthChargeModel init = new AddMonthChargeModel(monthlyCharge);
				addAndEditMonthCharge(init);
			}
			if (carparkChargeInfo.getType().equals(CarparkChargeTypeEnum.临时收费.name())) {
				CarparkChargeStandard findCarparkChargeStandardByCode = carparkService.findCarparkChargeStandardByCode(carparkChargeInfo.getCode());
				addTempCharge(findCarparkChargeStandardByCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public BlackUserListPresenter getBlackUserListPresenter() {
		return blackUserListPresenter;
	}

	/**
	 * 节假日设置
	 */
	public void addHoliday() {
		try {
			CarparkService carparkService = sp.getCarparkService();
			int year = Calendar.getInstance().get(Calendar.YEAR);
			List<Holiday> findHolidayByYear = carparkService.findHolidayByYear(year);
			List<Date> listDate = new ArrayList<>();
			for (Holiday h : findHolidayByYear) {

				listDate.add(h.getStart());
			}
			AddYearHolidayModel model = new AddYearHolidayModel();
			model.setSelect(listDate);
			model.setYear(year);
			AddYearHolidayWizard wizard = new AddYearHolidayWizard(model, sp, commonui);
			AddYearHolidayModel m = (AddYearHolidayModel) commonui.showWizard(wizard);
			if (StrUtil.isEmpty(m)) {
				return;
			}
			carparkService.deleteHoliday(findHolidayByYear);
			List<Date> select = m.getSelect();
			List<Holiday> list = new ArrayList<>();
			for (Date date : select) {
				System.out.println(StrUtil.formatDate(date, "yyyy-MM-dd"));
				Holiday holiday = new Holiday();
				holiday.setStart(date);
				holiday.setLength(1);
				list.add(holiday);
			}
			carparkService.saveHoliday(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void searchUser(String name, String plateNo, int willOverdue, String overdue) {
		List<SingleCarparkUser> findByNameOrPlateNo = sp.getCarparkUserService().findByNameOrPlateNo(name, plateNo,willOverdue,overdue);
		userModel.setAllList(findByNameOrPlateNo);
	}

	public UserPresenter getUserPresenter() {
		return userPresenter;
	}

	public float test1() {
		float money = 0;
		CountTempCarChargeI c=new CountTempCarChargeImpl();
		DateTime start = new DateTime(2015, 10, 22, 00, 00, 00);
		DateTime end = new DateTime(2015, 10, 29, 23, 59, 59);
		money=c.charge(2L, start.toDate(), end.toDate(), sp);
		return money;
	}

	public static void main(String[] args) {
		Date start = new DateTime(2015, 10, 22, 8, 30).toDate();
		Date end = new DateTime(2015, 10, 22, 17, 35).toDate();
		int minusMinute = StrUtil.MinusMinute(start, end);
		System.out.println(minusMinute + "===" + minusMinute / 60 + "===" + minusMinute % 60);
		List<Date> cutDaysByDay = CarparkUtils.cutDaysByDay(start, end);
		for (Date date : cutDaysByDay) {
			System.out.println(StrUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss"));
		}
	}
	/**
	 * 启用临时收费设置
	 */
	public void startUseTempCharge() {
		try {
			CarparkChargeInfo carparkChargeInfo = carparkModel.getCarparkChargeInfo();
			if (carparkChargeInfo.getUseType().equals(CarparkChargeTypeEnum.固定月租收费)||carparkChargeInfo.getUseType().equals("已启用")) {
				return;
			}
			List<CarparkChargeInfo> listCarparkCharge = carparkModel.getListCarparkCharge();
			for (CarparkChargeInfo carparkChargeInfo2 : listCarparkCharge) {
				if(!carparkChargeInfo.getUseType().equals(CarparkChargeTypeEnum.固定月租收费)){
					if (carparkChargeInfo.getCarType().equals(carparkChargeInfo2.getCarType())) {
						if (carparkChargeInfo.getHolidayType().equals(carparkChargeInfo2.getHolidayType())) {
							if (carparkChargeInfo2.getUseType().equals("已启用")) {
								commonui.error("启用失败", "已有车辆类型["+carparkChargeInfo2.getCarType()+"]节假日类型["+carparkChargeInfo2.getHolidayType()+"]的临时收费设置已被启用，请先禁止！");
								return;
							}
						}
					}
				}
			}
			CarparkChargeStandard findCarparkChargeStandardByCode = sp.getCarparkService().findCarparkChargeStandardByCode(carparkChargeInfo.getCode());
			findCarparkChargeStandardByCode.setUsing(true);
			sp.getCarparkService().saveCarparkChargeStandard(findCarparkChargeStandardByCode);
			refreshCarparkCharge();
		} catch (Exception e) {
			commonui.error("启用失败", "未知错误"+e.getMessage());
			e.printStackTrace();
		}
	}
	/**
	 * 停用临时收费设置
	 */
	public void stopUseTempCharge() {
		try {
			CarparkChargeInfo carparkChargeInfo = carparkModel.getCarparkChargeInfo();
			if (carparkChargeInfo.getUseType().equals(CarparkChargeTypeEnum.固定月租收费)||carparkChargeInfo.getUseType().equals("未启用")) {
				return;
			}
			CarparkChargeStandard findCarparkChargeStandardByCode = sp.getCarparkService().findCarparkChargeStandardByCode(carparkChargeInfo.getCode());
			findCarparkChargeStandardByCode.setUsing(false);
			sp.getCarparkService().saveCarparkChargeStandard(findCarparkChargeStandardByCode);
			refreshCarparkCharge();
		} catch (Exception e) {
			commonui.error("启用失败", "未知错误"+e.getMessage());
			e.printStackTrace();
		}
	}

	public void importUser() {
		try {
			String path = commonui.selectToSave();
			ExcelImportExport export=new ExcelImportExportImpl();
			int excelRowNum = export.getExcelRowNum(path);
			if (excelRowNum<3) {
				return;
			}
			int importUser = export.importUser(path, sp);
			if (importUser>0) {
				commonui.info("导入提示", "导入完成。有"+importUser+"条数据导入失败");
			}else{
				commonui.info("导入提示", "导入成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			refreshUser();
		}
	}
	/**
	 * 导出固定用户信息
	 */
	public void exportUser() {
		String selectToSave = commonui.selectToSave();
		String path = StrUtil.checkPath(selectToSave,  new String[] { ".xls", ".xlsx" }, ".xls");
		ExcelImportExport export=new ExcelImportExportImpl();
		List<SingleCarparkUser> allList = userModel.getAllList();
		try {
			export.exportUser(path, allList);
			commonui.info("导出提示", "导出成功！");
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("导出提示", "导出失败！"+e.getMessage());
		}
	}

	
}
