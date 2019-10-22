package com.donglu.carpark.ui.view.device;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.google.inject.Inject;

public class DeviceListPresenter extends AbstractListPresenter<SingleCarparkDevice> {
	@Inject
	private CarparkDatabaseServiceProvider sp;
	private String host;
	@Override
	protected View createView(Composite c) {
		return new DeviceListView(c);
	}
	@Override
	protected List<SingleCarparkDevice> findListInput() {
		return sp.getCarparkDeviceService().findAllDevice(host,null);
	}
}
