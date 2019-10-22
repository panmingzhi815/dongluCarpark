package com.donglu.carpark.ui.view.device;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractListView;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;

public class DeviceListView extends AbstractListView<SingleCarparkDevice> {

	public DeviceListView(Composite parent) {
		super(parent, parent.getStyle(), SingleCarparkDevice.class, new String[]{SingleCarparkDevice.Property.identifire.name(),
				SingleCarparkDevice.Property.name.name(),
				SingleCarparkDevice.Property.ip.name(),
				SingleCarparkDevice.Property.cameraType.name(),
				SingleCarparkDevice.Property.linkAddress.name(),
				SingleCarparkDevice.Property.screenType.name(),
				SingleCarparkDevice.Property.inOrOut.name(),
				SingleCarparkDevice.Property.carpark.name(),
				SingleCarparkDevice.Property.host.name(),
				}, new String[]{"编号","名称","摄像机","摄像机类型","控制器","屏幕类型","进出类型","停车场","岗亭IP"}, 
				new int[]{100,100,150,100,150,100,100,100,100});
	}

}
