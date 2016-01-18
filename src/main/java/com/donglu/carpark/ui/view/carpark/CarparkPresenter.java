package com.donglu.carpark.ui.view.carpark;


import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.list.UserListPresenter;
import com.google.inject.Inject;

public class CarparkPresenter  implements Presenter{
	private CarparkView view;
	@Override
	public void go(Composite c) {
		view=new CarparkView(c, c.getStyle());
		view.setPresenter(this);
	}
}
