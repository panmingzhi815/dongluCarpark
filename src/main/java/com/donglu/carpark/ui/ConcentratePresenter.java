package com.donglu.carpark.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.donglu.carpark.model.ConcentrateModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.view.SearchErrorCarPresenter;
import com.donglu.carpark.ui.wizard.SearchHistoryByHandWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeStandard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
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
			if (factMoney<0) {
				commonui.info("提示", "收费金额不能小于0");
				return;
			}
			Float paidMoney = model.getPaidMoney();
			if ((paidMoney+factMoney)>model.getShouldMoney()) {
				commonui.info("提示", "收费总金额不能大于应收金额");
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
}
