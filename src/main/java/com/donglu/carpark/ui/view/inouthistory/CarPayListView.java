package com.donglu.carpark.ui.view.inouthistory;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.CarPayHistory;

public class CarPayListView extends AbstractListView<CarPayHistory>implements View {
	public CarPayListView(Composite parent, int style) {
		super(parent, style, CarPayHistory.class,
				new String[] { CarPayHistory.Label.plateNO.name(),
						 CarPayHistory.Label.payedMoney.name(),
						 CarPayHistory.Label.payTimeLabel.name(),
						 CarPayHistory.Label.payType.name(),
						 CarPayHistory.Label.createDateLabel.name(),
						 CarPayHistory.Label.operaName.name(),
						 CarPayHistory.Label.remark.name(),
				},
				new String[] { "车牌号", "缴费金额", "缴费时间","缴费方式","创建时间","操作员","备注" }, new int[] { 100, 100, 200,100,200,100,200}, null);
	}

	@Override
	public CarPayListPresenter getPresenter() {
		return (CarPayListPresenter) presenter;
	}

	@Override
	protected void searchMore() {
		getPresenter().loadMore();
	}

	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
		
	}

}
