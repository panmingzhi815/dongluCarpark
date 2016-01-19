package com.donglu.carpark.ui.view.user;

import java.util.Date;


import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.Presenter;
import com.google.inject.Inject;

public class PrepaidUserPayHistoryPresenter  implements Presenter{
	private PrepaidUserPayHistoryView view;
	int max=0;
	int size=50;
	String userName, operaName;
	Date start,  end;
	CarparkPayHistoryListView carparkPayHistoryListView;
	
	@Inject
	private PripaidUserPayHistoryListPresenter carparkPayHistoryListPresenter;
	
	public void go(Composite parent){
		view=new PrepaidUserPayHistoryView(parent, parent.getStyle());
		view.setCarparkPayHistoryPresenter(this);
		carparkPayHistoryListPresenter.go(view.getListComposite());
	}
	
	public void searchCharge(String userName, String operaName, Date start, Date end) {
		this.userName=userName;
		this.operaName=operaName;
		this.start=start;
		this.end=end;
		carparkPayHistoryListPresenter.searchCharge(userName, operaName, start, end);
	}

	public void export() {
		carparkPayHistoryListPresenter.export();
	}

}
