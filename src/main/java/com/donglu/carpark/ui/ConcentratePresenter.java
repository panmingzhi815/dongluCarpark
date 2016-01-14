package com.donglu.carpark.ui;

import java.util.List;

import com.donglu.carpark.model.ConcentrateModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.view.SearchErrorCarPresenter;
import com.donglu.carpark.ui.wizard.SearchHistoryByHandWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
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
		String plateNO = model.getPlateNO();
		SingleCarparkInOutHistory findByNoOut = sp.getCarparkInOutService().findInOutHistoryByPlateNO(plateNO);
		if (StrUtil.isEmpty(findByNoOut)) {
			boolean confirm = commonui.confirm("", "");
			if (!confirm) {
				return;
			}
			searchErrorCarPresenter.getModel().setPlateNo(plateNO);
			searchErrorCarPresenter.getModel().setHavePlateNoSelect(null);
			searchErrorCarPresenter.getModel().setNoPlateNoSelect(null);
			SearchHistoryByHandWizard wizard = new SearchHistoryByHandWizard(searchErrorCarPresenter);
			Object showWizard = commonui.showWizard(wizard);
		}else{
			
		}
	}
	public void setModel(ConcentrateModel model) {
		this.model = model;
	}
}
