package com.donglu.carpark.ui.view.store;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStoreChargeHistory;

public class StoreChargeListView extends AbstractListView<SingleCarparkStoreChargeHistory> implements View {
	public StoreChargeListView(Composite parent, int style) {
		super(parent, style,SingleCarparkStoreChargeHistory.class,new String[]{SingleCarparkStoreChargeHistory.Property.storeName.name(),
				SingleCarparkStoreChargeHistory.Property.loginName.name(),
				SingleCarparkStoreChargeHistory.Property.payMoney.name(),
				SingleCarparkStoreChargeHistory.Property.payType.name(),
				SingleCarparkStoreChargeHistory.Property.freeMoney.name(),
				SingleCarparkStoreChargeHistory.Property.freeHours.name(),
				SingleCarparkStoreChargeHistory.Property.operaName.name(),
				SingleCarparkStoreChargeHistory.Property.createTimeLabel.name(),
				SingleCarparkStoreChargeHistory.Property.remark.name(),
				}, new String[]{"商铺名称","用户账号","充值金额","充值类型","账号增加金额","账号增加时间","操作员","充值时间","备注"},
				new int[]{100,100,100,100,120,120,100,200,200}, null);
	}

	@Override
	public StoreChargeListPresenter getPresenter() {
		return (StoreChargeListPresenter) presenter;
	}

	@Override
	protected void searchMore() {
	}

	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
		
	}
	
}
