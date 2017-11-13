package com.donglu.carpark.ui.view.inouthistory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;

public class InOutHistoryListView extends AbstractListView<SingleCarparkInOutHistory> implements View{
	
	private Label lbl_shouldMoney;
	private Label lbl_factMoney;
	private Label lbl_freeMoney;
	public InOutHistoryListView(Composite parent, int style) {
		super(parent, style, SingleCarparkInOutHistory.class,
				new String[]{SingleCarparkInOutHistory.Property.plateNo.name(),
						SingleCarparkInOutHistory.Property.carType.name(),
						SingleCarparkInOutHistory.Property.userName.name(),
						SingleCarparkInOutHistory.Property.inDevice.name(),
						SingleCarparkInOutHistory.Label.inTimeLabel.name(),
						SingleCarparkInOutHistory.Property.outDevice.name(),
						SingleCarparkInOutHistory.Label.outTimeLabel.name(),
						SingleCarparkInOutHistory.Property.operaName.name(),
						SingleCarparkInOutHistory.Property.shouldMoney.name(),
						SingleCarparkInOutHistory.Property.factMoney.name(),
						SingleCarparkInOutHistory.Property.freeMoney.name(),
						SingleCarparkInOutHistory.Property.freeReason.name(),
						SingleCarparkInOutHistory.Property.returnAccount.name(),
						SingleCarparkInOutHistory.Label.remarkString.name(),
						SingleCarparkInOutHistory.Property.inPlateNO.name(),
						SingleCarparkInOutHistory.Property.outPlateNO.name(),},
				new String[]{"车牌号","车辆类型","用户名","进场设备","进场时间","出场设备","出场时间","操作员","应收金额","实收金额","免费金额","免费原因","归账编号","备注","进场车牌","出场场车牌"},
				new int[]{100,100,100,100,200,100,200,100,90,90,90,90,90,90,0,0},new int[]{0,0,0,0,0,0,0,0,SWT.RIGHT,SWT.RIGHT,SWT.RIGHT,0,0,0,0,0});
		this.setTableTitle("进出记录表");
		
	}

	@Override
	protected void searchMore() {
		getPresenter().searchMore();
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
		ToolItem countItem = new ToolItem(mainToolbar, SWT.NONE);
		countItem.setText("统计");
		countItem.setData("type", "count");
		countItem.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		    	getPresenter().flowStatistics();
		    }
		});
		countItem.setToolTipText("流量统计");
	}

	@Override
	protected void createBottomComposite(Composite parent) {
		Composite composite_4 = new Composite(parent, SWT.NONE);
		GridLayout gl_composite_4 = new GridLayout(1, true);
		gl_composite_4.verticalSpacing = 0;
		gl_composite_4.marginWidth = 0;
		gl_composite_4.marginHeight = 0;
		gl_composite_4.horizontalSpacing = 0;
		composite_4.setLayout(gl_composite_4);
		
		Composite composite = new Composite(composite_4, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new GridLayout(6, false));
		
		Label label = new Label(composite, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("应收:");
		
		lbl_shouldMoney = new Label(composite, SWT.NONE);
		lbl_shouldMoney.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_lbl_shouldMoney = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_lbl_shouldMoney.widthHint = 70;
		lbl_shouldMoney.setLayoutData(gd_lbl_shouldMoney);
		
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("实收:");
		
		lbl_factMoney = new Label(composite, SWT.NONE);
		lbl_factMoney.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_lbl_factMoney = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_factMoney.widthHint = 70;
		lbl_factMoney.setLayoutData(gd_lbl_factMoney);
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("免费:");
		
		lbl_freeMoney = new Label(composite, SWT.NONE);
		lbl_freeMoney.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_lbl_freeMoney = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_freeMoney.widthHint = 70;
		lbl_freeMoney.setLayoutData(gd_lbl_freeMoney);
	}
	public void setMoney(String shouldMoney,String factMoney,String freeMoney){
		lbl_shouldMoney.setText(shouldMoney);
		lbl_factMoney.setText(factMoney);
		lbl_freeMoney.setText(freeMoney);
	}
}
