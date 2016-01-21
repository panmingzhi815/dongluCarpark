package com.donglu.carpark.ui.view.inouthistory;


import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.Presenter;
import com.google.inject.Inject;

public class CarInPresenter implements Presenter{
	private CarInView view;
	@Inject
	private CarInListPresenter listPresenter;
	@Override
	public void go(Composite c) {
		view=new CarInView(c, c.getStyle());
		view.setPresenter(this);
		listPresenter.go(view.getListComposite());
	}
	public CarInListPresenter getListPresenter() {
		return listPresenter;
	}
	
	public void search(String plateNO) {
		
		listPresenter.search(plateNO);
	}
	
}
