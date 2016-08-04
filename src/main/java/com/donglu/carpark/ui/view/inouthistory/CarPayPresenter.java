package com.donglu.carpark.ui.view.inouthistory;


import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractPresenter;
import com.google.inject.Inject;

public class CarPayPresenter  extends AbstractPresenter{
	@Inject
	private CarPayListPresenter listPresenter;
	public CarPayListPresenter getListPresenter() {
		return listPresenter;
	}
	
	public void search(String plateNo, Date start, Date end) {
		listPresenter.search(plateNo,start,end);
	}
	@Override
	protected CarPayView createView(Composite c) {
		return new CarPayView(c, c.getStyle());
	}

	@Override
	protected void continue_go() {
		listPresenter.go(getView().getListComposite());
	}

	@Override
	public CarPayView getView() {
		return (CarPayView) super.getView();
	}
	
}
