package com.donglu.carpark.ui.view;

import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.list.ReturnAccountListPresenter;
import com.google.inject.Inject;

public class ReturnAccountPresenter  extends AbstractListPresenter{
	private ReturnAccountView view;
	@Inject
	private ReturnAccountListPresenter listPresenter;
	@Override
	public void go(Composite c) {
		view=new ReturnAccountView(c, c.getStyle());
		view.setPresenter(this);
		listPresenter.go(view.getListComposite());
	}
	public void search(String operaName, String returnUser, Date start, Date end) {
		listPresenter.search(operaName,returnUser,start,end);
	}
	
}
