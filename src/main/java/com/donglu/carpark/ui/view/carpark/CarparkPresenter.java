package com.donglu.carpark.ui.view.carpark;


import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkService;
import com.donglu.carpark.ui.CarparkMainApp;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.wizard.AddCarparkChildWizard;
import com.donglu.carpark.ui.wizard.AddCarparkWizard;
import com.donglu.carpark.util.CarparkFileUtils;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class CarparkPresenter  implements Presenter{
	private CarparkView view;
	private CarparkModel model=new CarparkModel();
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private ChargeListPresenter listPresenter;
	@Override
	public void go(Composite c) {
		view=new CarparkView(c, c.getStyle(), model);
		view.setPresenter(this);
		listPresenter.go(view.getListComsite());
		refreshCarpark();
		refreshCharges();
	}
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
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.停车场, "删除了停车场:" + carpark.getCode());
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
			AddCarparkWizard w = new AddCarparkWizard(model, sp);
			SingleCarparkCarpark showWizard = (SingleCarparkCarpark) commonui.showWizard(w);
			if (StrUtil.isEmpty(showWizard)) {
				return;
			}
			showWizard.setTempNumberOfSlot(showWizard.getTotalNumberOfSlot() - showWizard.getFixNumberOfSlot());
			showWizard.setLeftNumberOfSlot(showWizard.getTotalNumberOfSlot());
			carparkService.saveCarpark(showWizard);
			if (StrUtil.isEmpty(model.getCode())) {
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.停车场, "添加了停车场:" + showWizard.getCode());
				commonui.info("提示", "添加停车场成功");
			} else {
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.停车场, "修改了停车场:" + showWizard.getCode());
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
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.停车场, "添加了子停车场:" + showWizard.getCode());
				commonui.info("提示", "添加子停车场成功");
			} else {
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.停车场, "修改了子停车场:" + showWizard.getCode());
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
			for (String ip : CarparkMainApp.mapIpToDevice.keySet()) {
				SingleCarparkDevice singleCarparkDevice = CarparkMainApp.mapIpToDevice.get(ip);
				SingleCarparkCarpark findCarparkById = sp.getCarparkService().findCarparkById(singleCarparkDevice.getCarpark().getId());
				singleCarparkDevice.setCarpark(findCarparkById);
				CarparkMainApp.mapIpToDevice.put(ip, singleCarparkDevice);
			}
			CarparkFileUtils.writeObject(CarparkMainApp.MAP_IP_TO_DEVICE, CarparkMainApp.mapIpToDevice);

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
}
