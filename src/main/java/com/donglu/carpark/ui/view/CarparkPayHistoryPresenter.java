package com.donglu.carpark.ui.view;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.list.CarparkPayHistoryListView;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.google.inject.Inject;

public class CarparkPayHistoryPresenter {
	private CarparkPayHistoryView view;
	
	@Inject
	private CarparkDatabaseServiceProvider sp;
	
	public Composite getView(Composite parent, int style){
		view=new CarparkPayHistoryView(parent, style);
		view.setCarparkPayHistoryPresenter(this);
		return view;
	}

	public void searchCharge(CarparkPayHistoryListView carparkPayHistoryListView, String userName, String operaName, Date start, Date end) {
		AbstractListView<SingleCarparkMonthlyUserPayHistory>.Model model = carparkPayHistoryListView.getModel();
		List<SingleCarparkMonthlyUserPayHistory> list =sp.getCarparkService().findMonthlyUserPayHistoryByCondition(0,50,userName,operaName,start,end);
		int countSearchAll=sp.getCarparkService().countMonthlyUserPayHistoryByCondition(userName,operaName,start,end);
		model.setList(list);
		model.setCountSearch(list.size());
		model.setCountSearchAll(countSearchAll);
	}
}
