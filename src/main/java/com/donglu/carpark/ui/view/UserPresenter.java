package com.donglu.carpark.ui.view;


import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.list.UserListPresenter;
import com.google.inject.Inject;

public class UserPresenter  implements Presenter{
	private UserView view;
	@Inject
	private UserListPresenter listPresenter;
	@Override
	public void go(Composite c) {
		view=new UserView(c, c.getStyle());
		view.setPresenter(this);
		listPresenter.go(view.getListComposite());
	}
	public UserListPresenter getListPresenter() {
		return listPresenter;
	}
	public void search(String userName, String plateNo, int parseInt, String text2) {
		listPresenter.search(userName,plateNo,parseInt,text2);
	}
	
}
