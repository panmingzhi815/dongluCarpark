package com.donglu.carpark.ui.list;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkOpenDoorLog;

public class OpenDoorLogListView extends AbstractListView<SingleCarparkOpenDoorLog> implements View {
	public OpenDoorLogListView(Composite parent, int style) {
		super(parent, style,SingleCarparkOpenDoorLog.class,new String[]{SingleCarparkOpenDoorLog.Property.operaName.name(),
				SingleCarparkOpenDoorLog.Property.operaDateLabel.name(),
				SingleCarparkOpenDoorLog.Property.deviceName.name(),"type"
				}, new String[]{"操作人","抬杆时间","抬杆设备","类型"},
				new int[]{100,200,100,120}, null);
	}

	@Override
	public OpenDoorListPresenter getPresenter() {
		return (OpenDoorListPresenter) presenter;
	}

	@Override
	protected void searchMore() {
	}

	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
		
	}
	
}
