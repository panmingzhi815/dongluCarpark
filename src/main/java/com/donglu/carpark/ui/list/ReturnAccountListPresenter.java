package com.donglu.carpark.ui.list;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.donglu.carpark.ui.common.Presenter;

public class ReturnAccountListPresenter implements Presenter{
	ReturnAccountListView v;
	@Override
	public void go(Composite c) {
		v=new ReturnAccountListView( c, c.getStyle());
		v.setPresenter(this);
	}
	
}
