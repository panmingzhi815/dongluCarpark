package com.donglu.carpark.ui.view.user;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import com.donglu.carpark.ui.common.AbstractListView;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

public class CarparkPayHistoryListView extends AbstractListView<SingleCarparkMonthlyUserPayHistory> {
	
	private Label label_total;

	public CarparkPayHistoryListView(Composite parent, int style) {
		super(parent, style, SingleCarparkMonthlyUserPayHistory.class,
				new String[]{SingleCarparkMonthlyUserPayHistory.Property.userName.name(),
						SingleCarparkMonthlyUserPayHistory.Property.plateNO.name(),
						SingleCarparkMonthlyUserPayHistory.Property.userAddress.name(),
						SingleCarparkMonthlyUserPayHistory.Property.parkingSpace.name(),
						SingleCarparkMonthlyUserPayHistory.Property.userType.name(),
						SingleCarparkMonthlyUserPayHistory.Property.monthChargeName.name(),
						SingleCarparkMonthlyUserPayHistory.Property.chargesMoney.name(),
						SingleCarparkMonthlyUserPayHistory.Label.createTimeLabel.name(),
						SingleCarparkMonthlyUserPayHistory.Property.operaName.name(),
						SingleCarparkMonthlyUserPayHistory.Label.startTimeLabel.name(),
						SingleCarparkMonthlyUserPayHistory.Label.overdueTimeLabel.name(),"remark"},
				new String[]{"用户名","车牌号","住址","车位编号","用户类型","收费标准","充值金额","充值时间","操作人","起始时间","过期时间","备注"},
				new int[]{100,100,100,100,100,100,100,200,100,200,200,100}, new int[]{0,0,0,0,0,0,SWT.RIGHT,0,0,0,0,0});
		this.setTableTitle("充值记录表");
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
	public CarparkPayHistoryListPresenter getPresenter() {
		return (CarparkPayHistoryListPresenter) super.getPresenter();
	}
	@Override
	protected void createBottomComposite(Composite parent) {
		Composite composite_4 = new Composite(parent, SWT.NONE);
		composite_4.setLayout(new GridLayout(3, false));
		
		Label label_2 = new Label(composite_4, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		Label label = new Label(composite_4, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		label.setText("总金额:");
		
		label_total = new Label(composite_4, SWT.NONE);
		GridData gd_label_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label_1.widthHint = 105;
		label_total.setLayoutData(gd_label_1);
		label_total.setText("0");
	}

	public void setTotalMoney(float i) {
		label_total.setText(i+"");
	}
}
