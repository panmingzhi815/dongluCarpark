package com.donglu.carpark.ui.view.store;


import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractPresenter;
import com.donglu.carpark.ui.common.View;
import com.google.inject.Inject;

public class StoreFreePresenter  extends AbstractPresenter{
	private StoreFreeView view;
	@Inject
	private StoreFreeListPresenter listPresenter;
	
	public StoreFreeListPresenter getListPresenter() {
		return listPresenter;
	}
	public void search(String storeName, String operaName, String used, Date start, Date end) {
		listPresenter.search(storeName,operaName,used,start,end);
	}
	public void export() {
		listPresenter.exportAll();
		
	}
	@Override
	protected View createView(Composite c) {
		view=new StoreFreeView(c, c.getStyle());
		return view;
	}
	@Override
	protected void continue_go() {
		listPresenter.go(view.getListComposite());
	}
	
}
