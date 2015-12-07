package com.donglu.carpark.ui.view;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.model.SearchErrorCarModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkInOutServiceI;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
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
		
		CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
		List<SingleCarparkInOutHistory> list = new ArrayList<>();
//		list = carparkInOutService.searchHistoryByLikePlateNO(model.getPlateNo(),order,model.getCarpark());
		for (int i = model.getPlateNo().length(); i >1; i--) {
			List<String> plateNOs=CarparkUtils.splitString(model.getPlateNo(), i);
			List<SingleCarparkInOutHistory> searchHistoryByLikePlateNO = carparkInOutService.searchHistoryByLikePlateNO(plateNOs, order, model.getCarpark());
			for (SingleCarparkInOutHistory singleCarparkInOutHistory : searchHistoryByLikePlateNO) {
				if (!list.contains(singleCarparkInOutHistory)) {
					list.add(singleCarparkInOutHistory);
				}
			}
		}
		model.setHavePlateNoList(list);
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
