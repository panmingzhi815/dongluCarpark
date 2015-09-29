package com.donglu.carpark;

import java.util.List;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkService;
import com.donglu.carpark.wizard.AddCarparkWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class CarparkManagePresenter {
	CarparkManageApp view;
	CarparkModel carparkModel;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	
	@Inject
	CommonUIFacility commonui;
	
	public void deleteCarpark(){
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
			init();
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("提示", "删除失败！"+e.getMessage());
		}
	}
	public void addCarpark(){
		SingleCarparkCarpark carpark = carparkModel.getCarpark();
		CarparkService carparkService = sp.getCarparkService();
		AddCarparkWizard w=new AddCarparkWizard(new SingleCarparkCarpark());
		SingleCarparkCarpark showWizard = (SingleCarparkCarpark) commonui.showWizard(w);
		if (!StrUtil.isEmpty(showWizard)) {
			if (carpark!=null) {
				showWizard.setParent(carpark);
			}
			carparkService.saveCarpark(showWizard);
		}
		List<SingleCarparkCarpark> findCarparkToLevel = carparkService.findCarparkToLevel();
		carparkModel.setListCarpark(findCarparkToLevel);
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
	
	public void init() {
		CarparkService carparkService = sp.getCarparkService();
		List<SingleCarparkCarpark> list = carparkService.findCarparkToLevel();
		carparkModel.setListCarpark(list);
	}
}
