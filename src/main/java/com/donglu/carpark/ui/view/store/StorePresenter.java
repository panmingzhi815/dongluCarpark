package com.donglu.carpark.ui.view.store;


import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractPresenter;
import com.donglu.carpark.ui.common.View;
import com.google.inject.Inject;

public class StorePresenter  extends AbstractPresenter{
	private StoreView view;
	@Inject
	private StoreListPresenter listPresenter;
	
	public StoreListPresenter getListPresenter() {
		return listPresenter;
	}
	public void search(String userName) {
		listPresenter.search(userName);
	}
	@Override
	protected View createView(Composite c) {
		view=new StoreView(c, c.getStyle());
		view.setPresenter(this);
		listPresenter.go(view.getListComposite());
		return view;
	}
	
}
