package com.donglu.carpark.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.donglu.carpark.model.ConcentrateModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkInOutServiceI;
import com.donglu.carpark.ui.view.SearchErrorCarPresenter;
import com.donglu.carpark.ui.view.inouthistory.CarInPresenter;
import com.donglu.carpark.ui.view.user.wizard.MonthlyUserPayModel;
import com.donglu.carpark.ui.view.user.wizard.MonthlyUserPayWizard;
import com.donglu.carpark.ui.wizard.ChangeUserWizard;
import com.donglu.carpark.ui.wizard.ReturnAccountWizard;
import com.donglu.carpark.ui.wizard.SearchHistoryByHandWizard;
import com.donglu.carpark.ui.wizard.model.ChangeUserModel;
import com.donglu.carpark.ui.wizard.model.ReturnAccountModel;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeStandard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.mapper.BeanUtil;
import com.dongluhitec.card.util.ThreadUtil;
import com.google.inject.Inject;

public class ConcentratePresenter {
	private static String OPERANAME = System.getProperty("userName");
	ConcentrateApp view;
	ConcentrateModel model;
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private SearchErrorCarPresenter searchErrorCarPresenter;
	@Inject
	private CarInPresenter carInPresenter;

	public void setView(ConcentrateApp view) {
		this.view = view;
	}
	/**
	 * 查询计算
	 */
	public void searchAndCount() {
		try {
			String plateNO = model.getPlateNO();
			if (StrUtil.isEmpty(plateNO)) {
				return;
			}
			Date date = new Date();
			SingleCarparkInOutHistory in = sp.getCarparkInOutService().findInOutHistoryByPlateNO(plateNO);
			if (StrUtil.isEmpty(in)) {
				commonui.info("提示", "没有找到车辆"+plateNO+"的进场记录");
				return;
			}
			count(date, in);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 计算收费
	 * @param date
	 * @param in
	 */
	public void count(Date date, SingleCarparkInOutHistory in) {
		model.setInTime(in.getInTime());
		model.setStillTime(StrUtil.MinusTime2(in.getInTime(), date));
		float calculateTempCharge = sp.getCarparkService().calculateTempCharge(model.getCarpark().getId(),model.getCarType().index(), in.getInTime(), date);
		model.setShouldMoney(calculateTempCharge);
		float paidMoney = in.getFactMoney() == null ? 0 : in.getFactMoney();
		model.setPaidMoney(paidMoney);
		float factMoney = calculateTempCharge - paidMoney < 0 ? 0 : calculateTempCharge - paidMoney;
		model.setFactMoney(factMoney);
		in.setFactMoney(paidMoney + factMoney);
		in.setShouldMoney(calculateTempCharge);
		in.setChargeTime(date);
		in.setOperaName(OPERANAME);
		in.setChargeOperaName(OPERANAME);
		view.setInImage(in.getBigImg());
		model.setIn(in);
	}
	/**
	 * 
	 */
	public void search() {
		try {
			String plateNO = model.getPlateNO();
			if (StrUtil.isEmpty(plateNO)||plateNO.length()<2) {
				commonui.info("提示", "车牌的长度最少是2位");
				return;
			}
			searchErrorCarPresenter.getModel().setPlateNo(plateNO);
			searchErrorCarPresenter.getModel().setHavePlateNoSelect(null);
			searchErrorCarPresenter.getModel().setNoPlateNoSelect(null);
			SearchHistoryByHandWizard wizard = new SearchHistoryByHandWizard(searchErrorCarPresenter);
			Object showWizard = commonui.showWizard(wizard);
			if (StrUtil.isEmpty(showWizard)) {
				return;
			}
			SingleCarparkInOutHistory havePlateNoSelect = searchErrorCarPresenter.getModel().getHavePlateNoSelect();
			SingleCarparkInOutHistory in;
			if (StrUtil.isEmpty(havePlateNoSelect)) {
				in = searchErrorCarPresenter.getModel().getNoPlateNoSelect();
			} else {
				in = havePlateNoSelect;
			}
			if (!searchErrorCarPresenter.getModel().isInOrOut()) {
				in.setPlateNo(plateNO);
			}else{
				model.setPlateNO(in.getPlateNo());
			}
			count(new Date(), in);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 收费
	 */
	public void charge() {
		try {
			SingleCarparkInOutHistory in = model.getIn();
			if (StrUtil.isEmpty(in)) {
				return;
			}
			Float factMoney = model.getFactMoney();
			if (factMoney<=0) {
				commonui.info("提示", "收费金额不能小于0");
				return;
			}
			Float paidMoney = model.getPaidMoney();
			float totalMoney = paidMoney+factMoney;
			if (totalMoney>model.getShouldMoney()) {
				commonui.info("提示", "收费总金额不能大于应收金额");
				return;
			}
			boolean confirm = commonui.confirm("提示", "车牌"+model.getPlateNO()+":应收费"+model.getShouldMoney()+"元，已缴费"+model.getPaidMoney()+"元。收费"+model.getFactMoney()+"元");
			if (!confirm) {
				return;
			}
			in.setFactMoney(totalMoney);
			sp.getCarparkInOutService().saveInOutHistory(in);
			commonui.info("提示", "收费成功");
			model.setIn(null);
			model.setPaidMoney(model.getPaidMoney()+model.getFactMoney());
			model.setFactMoney(0F);
		} catch (Exception e) {
			commonui.error("提示", "收费时发生错误，"+e);
		}
	}
	
	public void setModel(ConcentrateModel model) {
		this.model = model;
	}
	public void init() {
		OPERANAME = System.getProperty("userName");
		List<SingleCarparkCarpark> findAllCarpark = sp.getCarparkService().findAllCarpark();
		if (StrUtil.isEmpty(findAllCarpark)) {
			return;
		}
		model.setCarpark(findAllCarpark.get(0));
		model.setListCarpark(findAllCarpark);
		String userName = OPERANAME;
		model.setUserName(userName);
		model.setWorkTime(new Date());
		refreshMoney(userName);
		getListCarTypeAndSelect();
	}
	/**
	 * @param userName
	 */
	public void refreshMoney(String userName) {
		
		ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("集中收费界面金钱刷新"));
		newSingleThreadScheduledExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				model.setTotalFact(sp.getCarparkInOutService().findFactMoneyByName(userName));
				model.setTotalFree(sp.getCarparkInOutService().findFreeMoneyByName(userName));
			}
		}, 1, 5, TimeUnit.SECONDS);
	}
	/**
	 * 获得所有车辆类型并默认选择
	 */
	public void getListCarTypeAndSelect() {
		List<CarparkChargeStandard> findAllCarparkChargeStandard = sp.getCarparkService().findAllCarparkChargeStandard(model.getCarpark(),true);
		List<CarTypeEnum> list=new ArrayList<>();
		for (CarparkChargeStandard carparkChargeStandard : findAllCarparkChargeStandard) {
			String name = carparkChargeStandard.getCarparkCarType().getName();
			CarTypeEnum parse = CarTypeEnum.parse(name);
			if (!StrUtil.isEmpty(parse)&&!list.contains(parse)) {
				list.add(parse);
			}
		}
		model.setListCarType(list);
		if (list.size()>0) {
			model.setCarType(list.get(0));
		}
	}
	public void userRecharge() {
		try {
			SingleCarparkUser user = sp.getCarparkUserService().findUserByPlateNo(model.getPlateNO(), null);
			if (StrUtil.isEmpty(user)) {
				commonui.info("提示", "未找到车牌为["+model.getPlateNO()+"]的用户!");
				return;
			}
			MonthlyUserPayModel model = new MonthlyUserPayModel();

			
			if (user.getType().equals("免费")) {
				model.setFree(false);
				model.setPayMoney(false);
			}
			if (user.getType().equals("储值")) {
				model.setFree(false);
				model.setPayDate(false);
				model.setPayMoney(true);
			}
			model.setUserName(user.getName());
			model.setCreateTime(user.getCreateDate());
			model.setPlateNO(user.getPlateNo());
			model.setAllmonth(sp.getCarparkService().findMonthlyChargeByCarpark(user.getCarpark()));
			model.setOverdueTime(user.getValidTo());
			MonthlyUserPayWizard wizard = new MonthlyUserPayWizard(model);
			MonthlyUserPayModel m = (MonthlyUserPayModel) commonui.showWizard(wizard);
			if (StrUtil.isEmpty(m)) {
				return;
			}
			user.setValidTo(m.getOverdueTime());
			m.setOperaName(OPERANAME);
			m.setUserType(user.getType());
			if (user.getType().equals("普通")) {
				if (StrUtil.isEmpty(m.getSelectMonth())) {
					return;
				}
				user.setDelayDays(m.getSelectMonth().getDelayDays());
				user.setRemindDays(m.getSelectMonth().getExpiringDays());
				user.setMonthChargeId(m.getSelectMonth().getId());
				user.setCarpark(m.getSelectMonth().getCarpark());
			}
			if (user.getType().equals("储值")) {
				Float chargesMoney = m.getChargesMoney();
				if (chargesMoney>0) {
					user.setLeftMoney(user.getLeftMoney()+chargesMoney);
				}else{
					commonui.info("提示", "充值金额不能小于0");
					return;
				}
			}
			sp.getCarparkUserService().saveUser(user);
			sp.getCarparkService().saveMonthlyUserPayHistory(m.getSingleCarparkMonthlyUserPayHistory());
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "充值了用户:"+user.getName(),OPERANAME);
			commonui.info("操作成功", "充值成功");
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("失败", "充值失败"+e.getMessage());
		}
	}
	private void refresh() {
		
		
	}
	public void changeUser() {
		ChangeUserModel changeUserModel = new ChangeUserModel();
		List<SingleCarparkSystemUser> findAllSystemUser = sp.getSystemUserService().findAllSystemUser();
		changeUserModel.setAllSystemUserList(findAllSystemUser);
		changeUserModel.setSystemUser(findAllSystemUser.get(0));
		ChangeUserWizard wizard = new ChangeUserWizard(changeUserModel, sp);
		ChangeUserModel showWizard = (ChangeUserModel) commonui.showWizard(wizard);
		if (StrUtil.isEmpty(showWizard)) {
			return;
		}
		SingleCarparkSystemUser systemUser = showWizard.getSystemUser();
		String userName = systemUser.getUserName();
		System.setProperty("userName", userName);
		System.setProperty("userType", systemUser.getType());

		model.setUserName(userName);
		model.setWorkTime(new Date());
	}
	public void returnAccount() {
		try {
			ReturnAccountModel model = new ReturnAccountModel();
			String userName = this.model.getUserName();
			model.setReturnUser(userName);
			CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
			List<SingleCarparkInOutHistory> listFact = carparkInOutService.findHistoryFactMoneyNotReturn(userName);
			List<SingleCarparkInOutHistory> listFree = carparkInOutService.findHistoryFreeMoneyNotReturn(userName);
			float factMoney = 0;
			float freeMoney = 0;
			for (SingleCarparkInOutHistory singleCarparkInOutHistory : listFact) {
				Float factMoney2 = singleCarparkInOutHistory.getFactMoney();
				if (StrUtil.isEmpty(factMoney2)) {
					factMoney2 = 0F;
				}
				factMoney += factMoney2;
			}
			for (SingleCarparkInOutHistory singleCarparkInOutHistory : listFree) {
				Float factMoney2 = singleCarparkInOutHistory.getFreeMoney();
				if (StrUtil.isEmpty(factMoney2)) {
					factMoney2 = 0F;
				}
				freeMoney += factMoney2;
			}
			model.setShouldReturn(factMoney);
			model.setFactReturn(freeMoney);
			List<SingleCarparkSystemUser> findAllSystemUser = sp.getSystemUserService().findAllSystemUser();
			model.setListSystemUser(findAllSystemUser);
			model.setOperaUser(findAllSystemUser.get(0));
			ReturnAccountWizard wizard = new ReturnAccountWizard(model, sp);
			ReturnAccountModel m = (ReturnAccountModel) commonui.showWizard(wizard);
			if (StrUtil.isEmpty(m)) {
				return;
			}
			SingleCarparkReturnAccount a = new SingleCarparkReturnAccount();
			BeanUtil.copyProperties(m, a, "returnUser", "factReturn", "shouldReturn", "operaName");
			a.setReturnTime(new Date());
			if (model.isFree()) {
				a.setFactReturn(model.getFactReturn());
			} else {
				if (a.getShouldReturn() <= 0) {
					return;
				}
				a.setFactReturn(0);
			}

			Long saveReturnAccount = sp.getCarparkService().saveReturnAccount(a);

			Map<Long, Long> map = new HashMap<Long, Long>();
			for (SingleCarparkInOutHistory singleCarparkInOutHistory : listFact) {
				singleCarparkInOutHistory.setReturnAccount(saveReturnAccount);
				map.put(singleCarparkInOutHistory.getId(), saveReturnAccount);
			}

			carparkInOutService.saveInOutHistoryOfList(listFact);
			if (model.isFree()) {
				for (SingleCarparkInOutHistory singleCarparkInOutHistory : listFree) {
					if (!StrUtil.isEmpty(map.get(singleCarparkInOutHistory.getId()))) {
						singleCarparkInOutHistory.setReturnAccount(saveReturnAccount);
					}
					singleCarparkInOutHistory.setFreeReturnAccount(saveReturnAccount);
					// singleCarparkInOutHistory.setOperaName(model.getOperaName());
				}
				carparkInOutService.saveInOutHistoryOfList(listFree);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		
	}
	public CarInPresenter getCarInPresenter() {
		return carInPresenter;
	}
}
