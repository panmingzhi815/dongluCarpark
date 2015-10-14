package com.donglu.carpark.ui.list;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.ui.view.CarparkPayHistoryPresenter;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;

public class InOutHistoryListView extends AbstractListView<SingleCarparkInOutHistory> implements View{
	private Presenter presenter;
	public InOutHistoryListView(Composite parent, int style) {
		super(parent, style, SingleCarparkInOutHistory.class,
				new String[]{SingleCarparkInOutHistory.Property.plateNo.name(),
						SingleCarparkInOutHistory.Property.carType.name(),
						SingleCarparkInOutHistory.Property.userName.name(),
						SingleCarparkInOutHistory.Property.inDevice.name(),
						SingleCarparkInOutHistory.Property.inTime.name(),
						SingleCarparkInOutHistory.Property.outDevice.name(),
						SingleCarparkInOutHistory.Property.outTime.name(),
						SingleCarparkInOutHistory.Property.operaName.name(),
						SingleCarparkInOutHistory.Property.shouldMoney.name(),
						SingleCarparkInOutHistory.Property.factMoney.name(),
						SingleCarparkInOutHistory.Property.freeMoney.name(),
						SingleCarparkInOutHistory.Property.returnAccount.name(),},
				new String[]{"车牌号","车辆类型","用户名","进场设备","进场时间","出场设备","出场时间","操作员","应收金额","实收金额","免费金额","归账编号"},
				new int[]{100,100,100,100,200,100,200,100,90,90,90,90});
		this.setTableTitle("归账记录表");
	}

	@Override
	protected void searchMore() {
		getPresenter().searchMore();
	}

	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public InOutHistoryListPresenter getPresenter() {
		return (InOutHistoryListPresenter) presenter;
	}

	@Override
	protected void createMenuBarToolItem(ToolBar mainToolbar) {
		ToolItem addItem = new ToolItem(mainToolbar, SWT.NONE);
		addItem.setText("查看");
		addItem.setData("type", "look");
		addItem.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		    	getPresenter().lookDetail();
		    }
		});
		addItem.setToolTipText("查看图片");
	}

}
