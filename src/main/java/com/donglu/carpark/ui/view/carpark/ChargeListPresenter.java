package com.donglu.carpark.ui.view.carpark;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkService;
import com.donglu.carpark.ui.CarparkManagePresenter;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.view.carpark.wizard.AddMonthChargeModel;
import com.donglu.carpark.ui.view.carpark.wizard.AddMonthChargeWizard;
import com.donglu.carpark.ui.view.carpark.wizard.NewCommonChargeModel;
import com.donglu.carpark.ui.view.carpark.wizard.NewCommonChargeWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeStandard;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkHolidayTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.mapper.BeanUtil;
import com.google.inject.Inject;

public class ChargeListPresenter extends AbstractListPresenter<CarparkChargeInfo>{
	private final static Logger LOGGER = LoggerFactory.getLogger(CarparkManagePresenter.class);
	ChargeListView view;
	
	String userName; 
	String plateNo;
	int will=0; 
	String ed;
	
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkDatabaseServiceProvider sp;

	private SingleCarparkCarpark carpark;
	@Override
	public void go(Composite c) {
		view=new ChargeListView(c,c.getStyle());
		view.setPresenter(this);
		view.setTableTitle("收费设置");
		view.setShowMoreBtn(false);
	}

	
	/**
	 * 添加临时收费
	 */
	public void addTempCharge(CarparkChargeStandard carparkCharge) {
		if (carpark == null) {
			commonui.error("错误", "请先选择一个停车场");
			return;
		}

		final CarparkService carparkService = sp.getCarparkService();
		final CarparkChargeStandard carparkChargeStandard = new CarparkChargeStandard();

		NewCommonChargeModel model = new NewCommonChargeModel();
		model.setCarpark(carpark);
		if (carparkCharge != null) {
			BeanUtil.copyProperties(carparkCharge, model, CarparkChargeStandard.Property.values());
			model.setFreeTimeEnable(model.getAcrossdayChargeEnable() == 1 ? "是" : "否");
			
			if (model.getCarparkHolidayTypeEnum().equals(CarparkHolidayTypeEnum.非工作日)) {
				
			}
			if (model.getFreeTime()>0) {
				
			}
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
			carparkChargeStandard.setCarpark(carpark);
			carparkService.saveCarparkChargeStandard(carparkChargeStandard);
			if (StrUtil.isEmpty(carparkCharge)) {
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.临时收费设置, "添加临时收费:" + carparkChargeStandard.getCode());
			} else {
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.临时收费设置, "修改临时收费:" + carparkChargeStandard.getCode());
			}
			refresh();
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
			AddMonthChargeWizard w = new AddMonthChargeWizard(init, sp);
			AddMonthChargeModel m = (AddMonthChargeModel) commonui.showWizard(w);
			if (m == null) {
				return;
			}
			SingleCarparkMonthlyCharge monthlyCharge = m.getSingleCarparkMonthlyCharge();

			if (!StrUtil.isEmpty(monthlyCharge.getId())) {
				List<SingleCarparkUser> list = sp.getCarparkUserService().findUserByMonthChargeId(monthlyCharge.getId());
				for (SingleCarparkUser singleCarparkUser : list) {
					singleCarparkUser.setRemindDays(monthlyCharge.getExpiringDays());
					singleCarparkUser.setDelayDays(monthlyCharge.getDelayDays());
				}
				sp.getCarparkUserService().saveUserByMany(list);
			}
			sp.getCarparkService().saveMonthlyCharge(monthlyCharge);
			if (StrUtil.isEmpty(init.getChargeCode())) {
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定收费设置, "添加固定收费:" + monthlyCharge.getChargeCode());
			} else {
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定收费设置, "修改固定收费:" + monthlyCharge.getChargeCode());
			}
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void refresh() {
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
			if (t.getUsing() == null || !t.getUsing()) {
				cci.setUseType("未启用");
			} else {
				cci.setUseType("已启用");
			}
			cci.setCarType(t.getCarparkCarType().getName());
			cci.setHolidayType(t.getCarparkHolidayTypeEnum().name());
			cci.setType("临时收费");
			list.add(cci);
		}
		view.getModel().setList(list);
	}

	public void search(String userName, String plateNo, int will, String ed) {
		this.userName=userName;
		this.plateNo=plateNo;
		this.will=will;
		this.ed=ed;
		refresh();
	}
	/**
	 * 编辑收费设置
	 */
	public void editCarparkChargeSetting() {
		try {
			List<CarparkChargeInfo> selected = view.getModel().getSelected();
			if (StrUtil.isEmpty(selected)) {
				return;
			}
			CarparkChargeInfo carparkChargeInfo = selected.get(0);
			CarparkService carparkService = sp.getCarparkService();
			if (carparkChargeInfo.getType().equals(CarparkChargeTypeEnum.固定月租收费.name())) {
				SingleCarparkMonthlyCharge monthlyCharge = carparkService.findMonthlyChargeById(carparkChargeInfo.getId());

				AddMonthChargeModel init = new AddMonthChargeModel(monthlyCharge);
				addAndEditMonthCharge(init);
			}
			if (carparkChargeInfo.getType().equals(CarparkChargeTypeEnum.临时收费.name())) {
				CarparkChargeStandard findCarparkChargeStandardByCode = carparkService.findCarparkChargeStandardByCode(carparkChargeInfo.getCode(),carpark);
				addTempCharge(findCarparkChargeStandardByCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 删除收费设置
	 */
	public void deleteCarparkCharge() {
		List<CarparkChargeInfo> selected = view.getModel().getSelected();
		if (StrUtil.isEmpty(selected)) {
			commonui.info("", "请先选择一个收费设置");
			return;
		}
		for (CarparkChargeInfo carparkChargeInfo : selected) {
			boolean confirm = commonui.confirm("删除确认", "是否删除编号为[" + carparkChargeInfo.getCode() + "]名称为[" + carparkChargeInfo.getName() + "]的收费设置");
			if (!confirm) {
				return;
			}
			if (carparkChargeInfo.getType().equals(CarparkChargeTypeEnum.固定月租收费.name())) {
				sp.getCarparkService().deleteMonthlyCharge(carparkChargeInfo.getId());
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定收费设置, "删除固定收费:" + carparkChargeInfo.getCode());
			}
			if (carparkChargeInfo.getType().equals(CarparkChargeTypeEnum.临时收费.name())) {
				sp.getCarparkService().deleteTempCharge(carparkChargeInfo.getId());
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.临时收费设置, "删除临时收费:" + carparkChargeInfo.getCode());
			}
		}

		refresh();
	}
	/**
	 * 启用临时收费设置
	 */
	public void startUseTempCharge() {
		try {
			List<CarparkChargeInfo> selected = view.getModel().getSelected();
			if (StrUtil.isEmpty(selected)) {
				commonui.info("", "请先选择一个收费设置");
				return;
			}
			CarparkChargeInfo carparkChargeInfo = selected.get(0);
			if (carparkChargeInfo.getType().equals(CarparkChargeTypeEnum.固定月租收费.name()) || carparkChargeInfo.getUseType().equals("已启用")) {
				return;
			}
			List<CarparkChargeInfo> listCarparkCharge = view.getModel().getList();
			for (CarparkChargeInfo carparkChargeInfo2 : listCarparkCharge) {
				if (!carparkChargeInfo.getUseType().equals(CarparkChargeTypeEnum.固定月租收费)) {
					if (carparkChargeInfo.getCarType().equals(carparkChargeInfo2.getCarType())) {
						if (carparkChargeInfo.getHolidayType().equals(carparkChargeInfo2.getHolidayType())) {
							if (carparkChargeInfo2.getUseType().equals("已启用")) {
								commonui.error("启用失败", "已有车辆类型[" + carparkChargeInfo2.getCarType() + "]节假日类型[" + carparkChargeInfo2.getHolidayType() + "]的临时收费设置已被启用，请先禁止！");
								return;
							}
						}
					}
				}
			}
			sp.getCarparkService().changeChargeStandardState(carparkChargeInfo.getId(), true);
			refresh();
		} catch (Exception e) {
			LOGGER.error("启用收费设置异常", e);
			e.printStackTrace();
			commonui.error("启用失败", "启用收费设置异常");
		}
	}

	/**
	 * 停用临时收费设置
	 */
	public void stopUseTempCharge() {
		try {
			List<CarparkChargeInfo> selected = view.getModel().getSelected();
			if (StrUtil.isEmpty(selected)) {
				commonui.info("", "请先选择一个收费设置");
				return;
			}
			CarparkChargeInfo carparkChargeInfo = selected.get(0);
			if (carparkChargeInfo.getType().equals(CarparkChargeTypeEnum.固定月租收费.name()) || carparkChargeInfo.getUseType().equals("未启用")) {
				return;
			}
			sp.getCarparkService().changeChargeStandardState(carparkChargeInfo.getId(), false);
			refresh();
		} catch (Exception e) {
			LOGGER.error("禁用临时收费设置失败", e);
			commonui.error("禁用失败", "禁用临时收费设置失败" + e);
			e.printStackTrace();
		}
	}


	public void setCarpark(SingleCarparkCarpark carpark) {
		this.carpark = carpark;
		refresh();
	}
	
}
