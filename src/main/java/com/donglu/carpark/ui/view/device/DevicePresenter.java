package com.donglu.carpark.ui.view.device;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractPresenter;
import com.donglu.carpark.ui.common.View;

public class DevicePresenter extends AbstractPresenter {

	@Override
	protected View createView(Composite c) {
		return new DeviceView(c);
	}

}
