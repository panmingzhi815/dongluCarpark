package com.donglu.carpark.ui.view.offline;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkOffLineHistory;

public class CarparkOffLineHistoryListView extends AbstractListView<CarparkOffLineHistory> implements View{
	
	public CarparkOffLineHistoryListView(Composite parent, int style) {
		super(parent, style, CarparkOffLineHistory.class,
				new String[]{CarparkOffLineHistory.Property.plateNO.name(),
						CarparkOffLineHistory.Label.inTimeLabel.name(),
						CarparkOffLineHistory.Property.deviceName.name(),
						CarparkOffLineHistory.Property.deviceIp.name(),
						},
				new String[]{"车牌号","进场时间","设备名称","设备地址"},
				new int[]{100,200,100,200,200,300},null);
		this.setTableTitle("离线记录表");
	}
	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
	}
	@Override
	protected void searchMore() {
		getPresenter().searchMore();
	}


	@Override
	public CarparkOffLineHistoryListPresenter getPresenter() {
		return (CarparkOffLineHistoryListPresenter) presenter;
	}
}
