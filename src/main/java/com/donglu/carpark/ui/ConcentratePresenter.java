package com.donglu.carpark.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.donglu.carpark.model.ConcentrateModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.view.SearchErrorCarPresenter;
import com.donglu.carpark.ui.wizard.SearchHistoryByHandWizard;
import com.donglu.carpark.ui.wizard.monthcharge.MonthlyUserPayModel;
import com.donglu.carpark.ui.wizard.monthcharge.MonthlyUserPayWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeStandard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class ConcentratePresenter {
	ConcentrateApp view;
	ConcentrateModel model;
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private SearchErrorCarPresenter searchErrorCarPresenter;

	public void setView(ConcentrateApp view) {
		this.view = view;
	}
	/**
	 * 查询计算
	 */
	public void searchAndCount() {
		Date date = new Date();
		String plateNO = model.getPlateNO();
		SingleCarparkInOutHistory in = sp.getCarparkInOutService().findInOutHistoryByPlateNO(plateNO);
		if (StrUtil.isEmpty(in)) {
			boolean confirm = commonui.confirm("提示", "没有找到进场记录是否进人工查找？");
			if (!confirm) {
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
			if (StrUtil.isEmpty(havePlateNoSelect)) {
				in = searchErrorCarPresenter.getModel().getNoPlateNoSelect();
			} else {
				in = havePlateNoSelect;
			}
			if (!searchErrorCarPresenter.getModel().isInOrOut()) {
				in.setPlateNo(model.getPlateNO());
			}else{
				model.setPlateNO(in.getPlateNo());
			}
		}
		model.setInTime(in.getInTime());
		model.setStillTime(StrUtil.MinusTime2(in.getInTime(), date));
		float calculateTempCharge = sp.getCarparkService().calculateTempCharge(model.getCarType().index(), model.getCarpark().getId(), in.getInTime(), date);
		model.setShouldMoney(calculateTempCharge);
		float paidMoney = in.getFactMoney() == null ? 0 : in.getFactMoney();
		model.setPaidMoney(paidMoney);
		float factMoney = calculateTempCharge - paidMoney < 0 ? 0 : calculateTempCharge - paidMoney;
		model.setFactMoney(factMoney);
		in.setFactMoney(paidMoney + factMoney);
		in.setShouldMoney(calculateTempCharge);
		in.setChargeTime(date);
		in.setOperaName(System.getProperty("userName"));
		view.setInImage(in.getBigImg());
		model.setIn(in);

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
			if ((paidMoney+factMoney)>model.getShouldMoney()) {
				commonui.info("提示", "收费总金额不能大于应收金额");
				return;
			}
			boolean confirm = commonui.confirm("提示", "车牌"+model.getPlateNO()+":应收费"+model.getShouldMoney()+"元，已缴费"+model.getPaidMoney()+"元。收费"+model.getFactMoney()+"元");
			if (!confirm) {
				return;
			}
			sp.getCarparkInOutService().saveInOutHistory(in);
			commonui.info("提示", "收费成功");
		} catch (Exception e) {
			commonui.error("提示", "收费时发生错误，"+e);
		}
	}
	
	public void setModel(ConcentrateModel model) {
		this.model = model;
	}
	public void init() {
		List<SingleCarparkCarpark> findAllCarpark = sp.getCarparkService().findAllCarpark();
		if (StrUtil.isEmpty(findAllCarpark)) {
			return;
		}
		model.setCarpark(findAllCarpark.get(0));
		model.setListCarpark(findAllCarpark);
		String userName = System.getProperty("userName");
		model.setUserName(userName);
		model.setWorkTime(new Date());
		model.setTotalFact(sp.getCarparkInOutService().findFactMoneyByName(userName));
		model.setTotalFree(sp.getCarparkInOutService().findFreeMoneyByName(userName));
		getListCarTypeAndSelect();
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
			m.setOperaName(System.getProperty("userName"));
			sp.getCarparkUserService().saveUser(user);
			sp.getCarparkService().saveMonthlyUserPayHistory(m.getSingleCarparkMonthlyUserPayHistory());
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "充值了用户:"+user.getName());
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
		
		
	}
}
