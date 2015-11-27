package com.donglu.carpark.ui.list.store;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStoreChargeHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStoreFreeHistory;

public class StoreFreeListView extends AbstractListView<SingleCarparkStoreFreeHistory> implements View {
	public StoreFreeListView(Composite parent, int style) {
		super(parent, style,SingleCarparkStoreFreeHistory.class,new String[]{SingleCarparkStoreFreeHistory.Property.storeName.name(),
				SingleCarparkStoreFreeHistory.Property.freePlateNo.name(),
				SingleCarparkStoreFreeHistory.Property.freeMoney.name(),
				SingleCarparkStoreFreeHistory.Property.freeHour.name(),
				SingleCarparkStoreFreeHistory.Property.coupon.name(),
				SingleCarparkStoreFreeHistory.Property.used.name(),
				SingleCarparkStoreFreeHistory.Property.createTimeLabel.name(),
				
				}, new String[]{"商铺名称","优惠车牌","优惠金额","优惠时间","优惠卷","是否使用","优惠时间"},
				new int[]{100,100,100,100,100,100,200}, null);
	}

	@Override
	public StoreFreeListPresenter getPresenter() {
		return (StoreFreeListPresenter) presenter;
	}

	@Override
	protected void searchMore() {
	}

	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
		
	}
	
}
