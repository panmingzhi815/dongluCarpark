package com.donglu.carpark.ui.view.offline;

import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.Presenter;
import com.google.inject.Inject;

public class CarparkOffLineHistoryPresenter  implements Presenter{
	private CarparkOffLineHistoryView view;
	@Inject
	private CarparkOffLineHistoryListPresenter listPresenter;
	
	@Override
	public void go(Composite c) {
		view=new CarparkOffLineHistoryView(c, c.getStyle());
		view.setPresenter(this);
		listPresenter.go(view.getListComposite());
	}
	public CarparkOffLineHistoryListPresenter getListPresenter() {
		return listPresenter;
	}
	public void exportSearch() {
		listPresenter.exportSearch();	
	}
	public void search(String deviceName, Date start, Date end) {
		listPresenter.search(deviceName,  start, end);
	}
}
