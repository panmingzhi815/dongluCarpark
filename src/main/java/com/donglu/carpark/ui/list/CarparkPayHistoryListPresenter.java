package com.donglu.carpark.ui.list;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.AbstractListView;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.google.inject.Inject;

public class CarparkPayHistoryListPresenter  extends AbstractListPresenter{
	
	CarparkPayHistoryListView view;
	
	int max=0;
	int size=50;
	String userName, operaName;
	Date start,  end;
	
	@Inject
	private CarparkDatabaseServiceProvider sp;
	
	public Composite getView(Composite parent, int style){
		view=new CarparkPayHistoryListView(parent, style);
		view.setPresenter(this);
		return view;
	}
	
	public void refresh(){
		AbstractListView<SingleCarparkMonthlyUserPayHistory>.Model model = view.getModel();
		List<SingleCarparkMonthlyUserPayHistory> list =sp.getCarparkService().findMonthlyUserPayHistoryByCondition(0,50,userName,operaName,start,end);
		int countSearchAll=sp.getCarparkService().countMonthlyUserPayHistoryByCondition(userName,operaName,start,end);
		model.setList(list);
		model.setCountSearch(list.size());
		model.setCountSearchAll(countSearchAll);
	}
	public void searMore(){
		AbstractListView<SingleCarparkMonthlyUserPayHistory>.Model model = view.getModel();
		if (model.getCountSearchAll()<=model.getCountSearch()) {
			return;
		}
		List<SingleCarparkMonthlyUserPayHistory> list =sp.getCarparkService().findMonthlyUserPayHistoryByCondition(model.getList().size(),50,userName,operaName,start,end);
		int countSearchAll=sp.getCarparkService().countMonthlyUserPayHistoryByCondition(userName,operaName,start,end);
		model.AddList(list);
		model.setCountSearch(model.getList().size());
		model.setCountSearchAll(countSearchAll);
	}
	public void searchCharge(String userName, String operaName, Date start, Date end) {
		this.userName=userName;
		this.operaName=operaName;
		this.start=start;
		this.end=end;
		refresh();
	}

	public void go(Composite listComposite) {
		view=new CarparkPayHistoryListView(listComposite, listComposite.getStyle());
		view.setPresenter(this);
	}
}
