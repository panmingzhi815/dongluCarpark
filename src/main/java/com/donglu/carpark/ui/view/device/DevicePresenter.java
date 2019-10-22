package com.donglu.carpark.ui.view.device;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractPresenter;
import com.donglu.carpark.ui.common.View;
import com.google.inject.Inject;

public class DevicePresenter extends AbstractPresenter {
	
	@Inject
	private DeviceListPresenter deviceListPresenter;
	private DeviceView deviceView;

	@Override
	protected View createView(Composite c) {
		deviceView = new DeviceView(c);
		return deviceView;
	}
	
	@Override
	protected void continue_go() {
		deviceListPresenter.go(deviceView.getComposite());
	}

}
