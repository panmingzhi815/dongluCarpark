package com.donglu.carpark.ui.view.deviceerror;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceErrorMessage;

public class DeviceErrorListView extends AbstractListView<DeviceErrorMessage> implements View{
	
	public DeviceErrorListView(Composite parent, int style) {
		super(parent, style, DeviceErrorMessage.class,
				new String[]{DeviceErrorMessage.Property.deviceName.name(),
						DeviceErrorMessage.Property.ip.name(),
						DeviceErrorMessage.Property.controlIp.name(),
						DeviceErrorMessage.Label.checkDateLabel.name(),
						DeviceErrorMessage.Label.nomalTimeLabel.name(),
						DeviceErrorMessage.Property.errorMsg.name()},
				new String[]{"设备名称","摄像机ip","控制器ip","故障时间","故障解除时间","故障信息"},
				new int[]{100,100,100,200,200,300},null);
		this.setTableTitle("故障记录表");
	}
	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
	}
	@Override
	protected void searchMore() {
		getPresenter().searchMore();
	}


	@Override
	public DeviceErrorListPresenter getPresenter() {
		return (DeviceErrorListPresenter) presenter;
	}
}
