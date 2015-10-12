package com.donglu.carpark.ui.view;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.list.ReturnAccountListPresenter;
import com.google.inject.Inject;

public class ReturnAccountPresenter implements Presenter{
	private ReturnAccountView view;
	@Inject
	private ReturnAccountListPresenter returnAccountListPresenter;
	@Override
	public void go(Composite c) {
		view=new ReturnAccountView(c, c.getStyle());
		view.setPresenter(this);
		returnAccountListPresenter.go(view.getListComposite());
	}
	
}
