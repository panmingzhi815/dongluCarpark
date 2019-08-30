package com.donglu.carpark.ui.view.speed;

import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.Presenter;
import com.google.inject.Inject;

public class OverSpeedCarPresenter  implements Presenter{
	private OverSpeedCarView view;
	@Inject
	private OverSpeedCarListPresenter listPresenter;
	@Override
	public void go(Composite c) {
		view=new OverSpeedCarView(c, c.getStyle());
		view.setPresenter(this);
		listPresenter.go(view.getListComposite());
		
	}
	public OverSpeedCarListPresenter getListPresenter() {
		return listPresenter;
	}
	
	public void search( Date start, Date end, String plateNo) {
		listPresenter.search(start, end, plateNo);
	}
	
}
