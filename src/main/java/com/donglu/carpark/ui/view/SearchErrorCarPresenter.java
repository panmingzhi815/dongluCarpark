package com.donglu.carpark.ui.view;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.model.SearchErrorCarModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkInOutServiceI;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
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
		refresh();
	}
	
	public void search(boolean order) {
		long currentTimeMillis = System.currentTimeMillis();
		CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
		List<SingleCarparkInOutHistory> list = new ArrayList<>();
		// list = carparkInOutService.searchHistoryByLikePlateNO(model.getPlateNo(),order,model.getCarpark());
		// for (int i = model.getPlateNo().length(); i >1; i--) {
		// List<String> plateNOs=CarparkUtils.splitString(model.getPlateNo(), i);
		// List<SingleCarparkInOutHistory> searchHistoryByLikePlateNO = carparkInOutService.searchHistoryByLikePlateNO(plateNOs, order, model.getCarpark());
		// for (SingleCarparkInOutHistory singleCarparkInOutHistory : searchHistoryByLikePlateNO) {
		// if (!list.contains(singleCarparkInOutHistory)) {
		// list.add(singleCarparkInOutHistory);
		// }
		// }
		// }
		System.out.println("SearchErrorCarPresenter===================" + (System.currentTimeMillis() - currentTimeMillis));
		List<SingleCarparkInOutHistory> findByNoOut = carparkInOutService.searchNotOutHistory(0, 10000, null,model.getCarpark());
		if (StrUtil.isEmpty(findByNoOut)) {
			return;
		}
		findByNoOut.sort(new Comparator<SingleCarparkInOutHistory>() {

			@Override
			public int compare(SingleCarparkInOutHistory o1, SingleCarparkInOutHistory o2) {
				int compareTo = 0;
				if (order) {
					compareTo = o1.getInTime().compareTo(o2.getInTime());
				} else {
					compareTo = o2.getInTime().compareTo(o1.getInTime());
				}
				return compareTo;
			}
		});

		List<String> listPs = new ArrayList<>();
		for (int i = model.getPlateNo().length(); i > 1; i--) {
			List<String> plateNOs = CarparkUtils.splitString(model.getPlateNo(), i);
			listPs.addAll(plateNOs);
		}
		List<SingleCarparkInOutHistory> listNoPlate = new ArrayList<>();
		for (SingleCarparkInOutHistory inout : findByNoOut) {
			String s = inout.getPlateNo();
			if (StrUtil.isEmpty(s)||s.startsWith("WPC")) {
				listNoPlate.add(inout);
			}
		}
		for (String string : listPs) {
			for (SingleCarparkInOutHistory inout : findByNoOut) {
				String s = inout.getPlateNo();
				if (s.contains(string)) {
					if (!list.contains(inout)) {
						list.add(inout);
					}
				}
			}
		}
		model.setHavePlateNoList(list);
		System.out.println(findByNoOut.size());
		System.out.println("SearchErrorCarPresenter===================" + (System.currentTimeMillis() - currentTimeMillis));
		model.setNoPlateNoList(listNoPlate);
	}
	private void refresh() {
		new Thread(new Runnable() {
			public void run() {
				search(false);
			}
		}).start();
	}

	public SearchErrorCarModel getModel() {
		return model;
	}

	public void setSystemSetting(Map<SystemSettingTypeEnum, String> mapSystemSetting) {
		this.mapSystemSetting=mapSystemSetting;
	}

	public void Order(boolean order) {
		List<SingleCarparkInOutHistory> havePlateNoList = model.getHavePlateNoList();
		List<SingleCarparkInOutHistory> sortObjectPropety = CarparkUtils.sortObjectPropety(havePlateNoList, "inTimeLabel", order);
		model.setHavePlateNoList(sortObjectPropety);
	}
}
