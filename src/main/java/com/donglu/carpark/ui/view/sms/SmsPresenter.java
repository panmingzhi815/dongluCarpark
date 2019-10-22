package com.donglu.carpark.ui.view.sms;

import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.Presenter;
import com.google.inject.Inject;

public class SmsPresenter  implements Presenter{
	private SmsView view;
	@Inject
	private SmsListPresenter listPresenter;
	@Override
	public void go(Composite c) {
		view=new SmsView(c, c.getStyle());
		view.setPresenter(this);
		listPresenter.go(view.getListComposite());
		
	}
	public SmsListPresenter getListPresenter() {
		return listPresenter;
	}
	
	public void search( Date start, Date end, String plateNo, String name, String tel, int status) {
		listPresenter.search(start, end, plateNo,name,tel,status);
	}
	
}
