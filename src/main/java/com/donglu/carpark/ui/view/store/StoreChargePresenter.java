package com.donglu.carpark.ui.view.store;


import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractPresenter;
import com.donglu.carpark.ui.common.View;
import com.google.inject.Inject;

public class StoreChargePresenter  extends AbstractPresenter{
	private StoreChargeView view;
	@Inject
	private StoreChargeListPresenter listPresenter;
	public StoreChargeListPresenter getListPresenter() {
		return listPresenter;
	}
	public void search(String storeName, String operaName, Date start, Date end) {
		listPresenter.search(storeName,operaName,start,end);
	}
	public void export() {
		listPresenter.exportAll();
	}
	@Override
	protected View createView(Composite c) {
		view=new StoreChargeView(c, c.getStyle());
		return view;
	}
	@Override
	protected void continue_go() {
		listPresenter.go(view.getListComposite());
	}
}
