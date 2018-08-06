package com.donglu.carpark.ui.view.user;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import com.donglu.carpark.ui.common.AbstractListView;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkPrepaidUserPayHistory;

public class PrepaidUserPayHistoryListView extends AbstractListView<SingleCarparkPrepaidUserPayHistory> {
	
	public PrepaidUserPayHistoryListView(Composite parent, int style) {
		super(parent, style, SingleCarparkPrepaidUserPayHistory.class,
				new String[]{SingleCarparkPrepaidUserPayHistory.Property.userName.name(),
						SingleCarparkPrepaidUserPayHistory.Property.plateNO.name(),
						SingleCarparkPrepaidUserPayHistory.Label.inTimeLabel.name(),
						SingleCarparkPrepaidUserPayHistory.Label.outTimeLabel.name(),
						SingleCarparkPrepaidUserPayHistory.Property.payMoney.name(),
						SingleCarparkPrepaidUserPayHistory.Property.operaName.name(),
						},
				new String[]{"用户名","车牌号","进场时间","出场时间","缴费金额","操作人"},
				new int[]{100,100,200,200,100,100}, new int[]{0,0,0,0,SWT.RIGHT,0});
		this.setTableTitle("储值车消费记录表");
	}

	@Override
	protected void searchMore() {
		getPresenter().searMore();
	}
	
	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
	}

	@Override
	protected void createRefreshBarToolItem(ToolBar toolBar_refresh) {
	}

	@Override
	public PripaidUserPayHistoryListPresenter getPresenter() {
		return (PripaidUserPayHistoryListPresenter) super.getPresenter();
	}
	
}
