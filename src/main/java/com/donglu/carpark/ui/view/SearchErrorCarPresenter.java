package com.donglu.carpark.ui.view;


import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.model.SearchErrorCarModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.Presenter;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.google.inject.Inject;

public class SearchErrorCarPresenter implements Presenter{
	
	private SearchErrorCarView view;
	private SearchErrorCarModel model=new SearchErrorCarModel();
	Map<SystemSettingTypeEnum, String> mapSystemSetting;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Override
	public void go(Composite c) {
		view=new SearchErrorCarView(c, c.getStyle(),model);
		view.setPresenter(this);
		view.setSystemSetting(mapSystemSetting);
		refresh();
	}
	
	public void search(boolean order){
		model.setHavePlateNoList(sp.getCarparkInOutService().searchHistoryByLikePlateNO(model.getPlateNo(),order,model.getCarpark()));
	}
	private void refresh() {
		search(false);
	}

	public SearchErrorCarModel getModel() {
		return model;
	}

	public void setSystemSetting(Map<SystemSettingTypeEnum, String> mapSystemSetting) {
		this.mapSystemSetting=mapSystemSetting;
	}
}
