package com.donglu.carpark.ui.list;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkService;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.Presenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;
import com.google.inject.Inject;

public class ReturnAccountListPresenter  extends AbstractListPresenter{
	private ReturnAccountListView v;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	private String operaName;
	private String userName;
	private Date start;
	private Date end;
	@Override
	public void go(Composite c) {
		v=new ReturnAccountListView( c, c.getStyle());
		v.setPresenter(this);
	}
	public void search(String operaName, String userName, Date start, Date end) {
		AbstractListView<SingleCarparkReturnAccount>.Model model = v.getModel();
		model.setList(new ArrayList<>());
		CarparkService carparkService = sp.getCarparkService();
		List<SingleCarparkReturnAccount> findMonthlyUserPayHistoryByCondition = carparkService.findReturnAccountByCondition(model.getList().size(), 50, userName, operaName, start, end);
		int countMonthlyUserPayHistoryByCondition = carparkService.countReturnAccountByCondition(userName, operaName, start, end);
		model.setCountSearchAll(countMonthlyUserPayHistoryByCondition);
		model.AddList(findMonthlyUserPayHistoryByCondition);
		model.setCountSearch(model.getList().size());
		
	}
	public void searchMore() {
		AbstractListView<SingleCarparkReturnAccount>.Model model = v.getModel();
		if (model.getCountSearchAll()<=model.getCountSearch()) {
			return;
		}
		CarparkService carparkService = sp.getCarparkService();
		List<SingleCarparkReturnAccount> findMonthlyUserPayHistoryByCondition = carparkService.findReturnAccountByCondition(model.getList().size(), 50, userName, operaName, start, end);
		int countMonthlyUserPayHistoryByCondition = carparkService.countReturnAccountByCondition(userName, operaName, start, end);
		model.setCountSearchAll(countMonthlyUserPayHistoryByCondition);
		model.AddList(findMonthlyUserPayHistoryByCondition);
		model.setCountSearch(model.getList().size());
	}
	
}
