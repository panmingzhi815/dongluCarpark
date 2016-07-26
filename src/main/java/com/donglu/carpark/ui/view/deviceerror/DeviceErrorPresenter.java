package com.donglu.carpark.ui.view.deviceerror;

import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.Presenter;
import com.google.inject.Inject;

public class DeviceErrorPresenter  implements Presenter{
	private DeviceErrorView view;
	@Inject
	private DeviceErrorListPresenter listPresenter;
	
	@Override
	public void go(Composite c) {
		view=new DeviceErrorView(c, c.getStyle());
		view.setPresenter(this);
		listPresenter.go(view.getListComposite());
	}
	public DeviceErrorListPresenter getListPresenter() {
		return listPresenter;
	}
	public void exportSearch() {
		listPresenter.exportSearch();	
	}
	public void search(String deviceName, Date start, Date end) {
		listPresenter.search(deviceName,  start, end);
	}
}
