package com.donglu.carpark.ui.view.carpark;


import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkService;
import com.donglu.carpark.ui.common.AbstractPresenter;
import com.donglu.carpark.ui.wizard.AddCarparkChildWizard;
import com.donglu.carpark.ui.wizard.AddCarparkWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class CarparkPresenter  extends AbstractPresenter{
	private static final String OPERANAME = System.getProperty("userName");
	private CarparkView view;
	private CarparkModel model=new CarparkModel();
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private ChargeListPresenter listPresenter;
	/**
	 * 删除停车场
	 */
	public void deleteCarpark() {
		try {
			CarparkService carparkService = sp.getCarparkService();
			SingleCarparkCarpark carpark = model.getCarpark();
			if (StrUtil.isEmpty(carpark)) {
				return;
			}
			boolean confirm = commonui.confirm("删除提示", "是否删除选中停车场");
			if (!confirm) {
				return;
			}
			carparkService.deleteCarpark(carpark);
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.停车场, "删除了停车场:" + carpark.getCode(),OPERANAME);
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
			Long id = model.getId();
			Integer totalNumberOfSlot = model.getTotalNumberOfSlot();
			Integer fixNumberOfSlot = model.getFixNumberOfSlot();
			Integer tempNumberOfSlot = model.getTempNumberOfSlot();
			CarparkService carparkService = sp.getCarparkService();
			AddCarparkWizard w = new AddCarparkWizard(model, sp);
			SingleCarparkCarpark carpark = (SingleCarparkCarpark) commonui.showWizard(w);
			if (StrUtil.isEmpty(carpark)) {
				return;
			}
			carpark.setTempNumberOfSlot(carpark.getTotalNumberOfSlot() - carpark.getFixNumberOfSlot());
			carpark.setLeftNumberOfSlot(carpark.getTotalNumberOfSlot());
			Long saveCarpark = carparkService.saveCarpark(carpark);
			if (totalNumberOfSlot!=carpark.getTotalNumberOfSlot()||model.getFixNumberOfSlot()!=fixNumberOfSlot) {
				carpark.setId(saveCarpark);
				sp.getPositionUpdateService().updatePosion(carpark, true, carpark.getFixNumberOfSlot()-fixNumberOfSlot);
				sp.getPositionUpdateService().updatePosion(carpark, false, carpark.getTempNumberOfSlot()-tempNumberOfSlot);
			}
			if (StrUtil.isEmpty(id)) {
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.停车场, "添加了停车场:" + carpark.getCode(),OPERANAME);
				commonui.info("提示", "添加停车场成功");
			} else {
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.停车场, "修改了停车场:" + carpark.getCode(),OPERANAME);
				commonui.info("提示", "修改停车场成功");
			}
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
		SingleCarparkCarpark carpark = this.model.getCarpark();
		if (carpark == null) {
			commonui.info("提示", "请先选择一个停车场");
			return;
		}
		model.setParent(carpark);
		addAndEditChildCarpark(model);
	}

	/**
	 * @param model
	 */
	private void addAndEditChildCarpark(SingleCarparkCarpark model) {
		try {

			CarparkService carparkService = sp.getCarparkService();
			AddCarparkChildWizard w = new AddCarparkChildWizard(model, sp);
			SingleCarparkCarpark showWizard = (SingleCarparkCarpark) commonui.showWizard(w);
			if (StrUtil.isEmpty(showWizard)) {
				return;
			}
			showWizard.setTempNumberOfSlot(0);
			showWizard.setLeftNumberOfSlot(0);
			showWizard.setTotalNumberOfSlot(0);
			carparkService.saveCarpark(showWizard);
			if (StrUtil.isEmpty(model.getCode())) {
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.停车场, "添加了子停车场:" + showWizard.getCode(),OPERANAME);
				commonui.info("提示", "添加子停车场成功");
			} else {
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.停车场, "修改了子停车场:" + showWizard.getCode(),OPERANAME);
				commonui.info("提示", "修改子停车场成功");
			}
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

			SingleCarparkCarpark carpark = model.getCarpark();
			if (StrUtil.isEmpty(carpark.getParent())) {
				addAndEditCarpark(carpark);
			} else {
				addAndEditChildCarpark(carpark);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 刷新停车场
	 */
	public void refreshCarpark() {
		model.setListCarpark(Collections.emptyList());
		CarparkService carparkService = sp.getCarparkService();
		List<SingleCarparkCarpark> list = carparkService.findCarparkToLevel();
		if (!StrUtil.isEmpty(list)) {
			model.setCarpark(list.get(0));
		}
		model.setListCarpark(list);
		view.expandAllCarpark();
	}
	public void refreshCharges() {
		listPresenter.setCarpark(model.getCarpark());
	}
	
	@Override
	protected void continue_go() {
		listPresenter.go(view.getListComsite());
		refreshCarpark();
		refreshCharges();		
	}
	@Override
	protected CarparkView createView(Composite c) {
		if (view!=null) {
			return view;
		}
		view=new CarparkView(c, c.getStyle(), model);
		return view;
	}
}
