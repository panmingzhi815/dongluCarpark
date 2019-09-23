package com.donglu.carpark.ui.view.inouthistory;

import java.util.Comparator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.TableSort;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SystemUserTypeEnum;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.FillLayout;

public class InOutHistoryListView extends AbstractListView<SingleCarparkInOutHistory> implements View{
	
	private Label lbl_shouldMoney;
	private Label lbl_factMoney;
	private Label lbl_freeMoney;
	private Label lbl_onlineMoney;
	private Label lbl_freeCarSize;
	public InOutHistoryListView(Composite parent, int style) {
		super(parent, style, SingleCarparkInOutHistory.class,
				new String[]{SingleCarparkInOutHistory.Property.plateNo.name(),
						SingleCarparkInOutHistory.Property.carType.name(),
						SingleCarparkInOutHistory.Property.userName.name(),
						SingleCarparkInOutHistory.Property.inDevice.name(),
						SingleCarparkInOutHistory.Label.inTimeLabel.name(),
						SingleCarparkInOutHistory.Property.outDevice.name(),
						SingleCarparkInOutHistory.Label.outTimeLabel.name(),
						SingleCarparkInOutHistory.Label.stillTimeLabel.name(),
						SingleCarparkInOutHistory.Property.operaName.name(),
						SingleCarparkInOutHistory.Property.shouldMoney.name(),
						SingleCarparkInOutHistory.Property.factMoney.name(),
						SingleCarparkInOutHistory.Property.onlineMoney.name(),
						SingleCarparkInOutHistory.Property.freeMoney.name(),
						SingleCarparkInOutHistory.Property.freeReason.name(),
						SingleCarparkInOutHistory.Property.returnAccount.name(),
						SingleCarparkInOutHistory.Label.remarkString.name(),
						SingleCarparkInOutHistory.Property.inPlateNO.name(),
						SingleCarparkInOutHistory.Property.outPlateNO.name(),},
				new String[]{"车牌号","车辆类型","用户名","进场设备","进场时间","出场设备","出场时间","停留时间","操作员","应收金额","实收金额","网上支付","免费金额","免费原因","归账编号","备注","进场车牌","出场场车牌"},
				new int[]{100,100,100,100,200,100,200,130,100,90,90,90,90,90,90,90,0,0},new int[]{0,0,0,0,0,0,0,0,0,SWT.RIGHT,SWT.RIGHT,SWT.RIGHT,SWT.RIGHT,0,0,0,0,0});
		this.setTableTitle("进出记录表");
		TableSort.mapComparator.put(SingleCarparkInOutHistory.Label.stillTimeLabel.name(), new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				SingleCarparkInOutHistory cch1=(SingleCarparkInOutHistory) o1;
				SingleCarparkInOutHistory cch2=(SingleCarparkInOutHistory) o2;
				Long l=cch1.getStillTimeCount();
				return l.compareTo(cch2.getStillTimeCount());
			}
		});
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
		if (ConstUtil.checkPrivilege(SystemUserTypeEnum.超级管理员)) {
			ToolItem handOut = new ToolItem(mainToolbar, SWT.NONE);
			handOut.setText("手动出场");
			handOut.setData("type", "handout");
			handOut.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					getPresenter().handOut();
				}
			});
			handOut.setToolTipText("手动让未出场的车辆出场");
		}
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
		countItem.setText("流量统计");
		countItem.setData("type", "count");
		countItem.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		    	getPresenter().flowStatistics();
		    }
		});
		countItem.setToolTipText("流量统计");
		ToolItem feeCountItem = new ToolItem(mainToolbar, SWT.NONE);
		feeCountItem.setText("收费统计");
		feeCountItem.setData("type", "count");
		feeCountItem.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		    	getPresenter().feeCount();
		    }
		});
		feeCountItem.setToolTipText("收费统计");
		ToolItem setTypeItem = new ToolItem(mainToolbar, SWT.NONE);
		setTypeItem.setText("设置车类型");
		setTypeItem.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		    	getPresenter().setCarType();
		    }
		});
		setTypeItem.setToolTipText("设置车类型");
		ToolItem printItem = new ToolItem(mainToolbar, SWT.NONE);
		printItem.setText("打印小票");
		printItem.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		    	getPresenter().print();
		    }
		});
		printItem.setToolTipText("打印小票");
	}

	@Override
	protected void createBottomComposite(Composite parent) {
		Composite composite_4 = new Composite(parent, SWT.NONE);
		GridLayout gl_composite_4 = new GridLayout(1, true);
		gl_composite_4.marginHeight = 0;
		gl_composite_4.marginWidth = 0;
		gl_composite_4.horizontalSpacing = 0;
		composite_4.setLayout(gl_composite_4);
		
		Composite composite = new Composite(composite_4, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new GridLayout(8, false));
		
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
		label_2.setText("现金:");
		
		lbl_factMoney = new Label(composite, SWT.NONE);
		lbl_factMoney.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_lbl_factMoney = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_factMoney.widthHint = 70;
		lbl_factMoney.setLayoutData(gd_lbl_factMoney);
		
		Label label_3 = new Label(composite, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_3.setText("网上:");
		
		lbl_onlineMoney = new Label(composite, SWT.NONE);
		lbl_onlineMoney.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		GridData gd_lbl_onlineMoney = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_onlineMoney.widthHint = 70;
		lbl_onlineMoney.setLayoutData(gd_lbl_onlineMoney);
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("免费:");
		
		lbl_freeMoney = new Label(composite, SWT.NONE);
		lbl_freeMoney.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_lbl_freeMoney = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_freeMoney.widthHint = 70;
		lbl_freeMoney.setLayoutData(gd_lbl_freeMoney);
		
		Composite composite_1 = new Composite(composite_4, SWT.NONE);
		composite_1.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		lbl_freeCarSize = new Label(composite_1, SWT.NONE);
		lbl_freeCarSize.setAlignment(SWT.RIGHT);
		lbl_freeCarSize.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
	}
	public void setMoney(String shouldMoney,String factMoney,String freeMoney,String onlineMoney){
		lbl_shouldMoney.setText(shouldMoney);
		lbl_factMoney.setText(factMoney);
		lbl_freeMoney.setText(freeMoney);
		lbl_onlineMoney.setText(onlineMoney);
	}
	
	public void setFreeSizeLabel(String s) {
		lbl_freeCarSize.setText(s);
	}
	
}
