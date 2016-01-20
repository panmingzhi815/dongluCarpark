package com.donglu.carpark.ui.view.lockcar;


import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.Presenter;
import com.google.inject.Inject;

public class LockCarPresenter  implements Presenter{
	private LockCarView view;
	@Inject
	private LockCarListPresenter listPresenter;
	@Override
	public void go(Composite c) {
		view=new LockCarView(c, c.getStyle());
		view.setPresenter(this);
		listPresenter.go(view.getListComposite());
	}
	public LockCarListPresenter getListPresenter() {
		return listPresenter;
	}
	public void search(String plateNo, String operaName, String status, Date start, Date end) {
		listPresenter.search(plateNo, operaName, status, start, end);
	}
}
