package com.donglu.carpark.ui.view.img;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkOffLineHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkImageHistory;

public class ImageHistoryListView extends AbstractListView<SingleCarparkImageHistory> implements View{
	
	public ImageHistoryListView(Composite parent, int style) {
		super(parent, style, SingleCarparkImageHistory.class,
				new String[]{SingleCarparkImageHistory.Property.plateNO.name(),
						SingleCarparkImageHistory.Property.type.name(),
						SingleCarparkImageHistory.Label.timeLabel.name()
						},
				new String[]{"车牌号","类型","时间"},
				new int[]{100,100,200,200,200,300},null);
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
	public ImageHistoryListPresenter getPresenter() {
		return (ImageHistoryListPresenter) presenter;
	}
}
