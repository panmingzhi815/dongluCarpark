package com.donglu.carpark.ui.view.inouthistory;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.CarPayHistory;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;

public class CarPayListView extends AbstractListView<CarPayHistory>implements View {
	private Label lbl_total;
	private Label lbl_fact;
	private Label lbl_online;
	private Label lbl_free;
	public CarPayListView(Composite parent, int style) {
		super(parent, style, CarPayHistory.class,
				new String[] { CarPayHistory.Label.plateNO.name(),
						 CarPayHistory.Label.payedMoney.name(),
						 CarPayHistory.Label.cashCost.name(),
						 CarPayHistory.Label.onlineCost.name(),
						 CarPayHistory.Label.couponValue.name(),
						 CarPayHistory.Label.inTimeLabel.name(),
						 CarPayHistory.Label.payTimeLabel.name(),
						 CarPayHistory.Label.payType.name(),
						 CarPayHistory.Label.createDateLabel.name(),
						 CarPayHistory.Label.operaName.name(),
						 CarPayHistory.Label.remark.name(),
				},
				new String[] { "车牌号", "应收金额","现金","网上","免费", "进场时间","缴费时间","缴费方式","保存时间","操作员","备注" }, new int[] { 100, 100,80,80,80,200,200,100,200,100,200}, null);
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
	@Override
	protected void createBottomComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		composite_1.setLayout(new GridLayout(8, false));
		
		Label label = new Label(composite_1, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label.setText("应收:");
		
		lbl_total = new Label(composite_1, SWT.NONE);
		GridData gd_label_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label_1.widthHint = 80;
		lbl_total.setLayoutData(gd_label_1);
		lbl_total.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		lbl_total.setText("0");
		
		Label label_2 = new Label(composite_1, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_2.setText("现金:");
		
		lbl_fact = new Label(composite_1, SWT.NONE);
		GridData gd_label_3 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label_3.widthHint = 80;
		lbl_fact.setLayoutData(gd_label_3);
		lbl_fact.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		lbl_fact.setText("0");
		
		Label label_4 = new Label(composite_1, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_4.setText("网上:");
		
		lbl_online = new Label(composite_1, SWT.NONE);
		GridData gd_label_5 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label_5.widthHint = 80;
		lbl_online.setLayoutData(gd_label_5);
		lbl_online.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		lbl_online.setText("0");
		
		Label label_6 = new Label(composite_1, SWT.NONE);
		label_6.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_6.setText("免费");
		
		lbl_free = new Label(composite_1, SWT.NONE);
		lbl_free.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		GridData gd_label_7 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label_7.widthHint = 80;
		lbl_free.setLayoutData(gd_label_7);
		lbl_free.setText("0");
	}
	
	public void setLabelText(List<Double> list){
		if (list==null||list.size()<4) {
			return;
		}
		lbl_total.setText(list.get(0)+"");
		lbl_fact.setText(list.get(1)+"");
		lbl_online.setText(list.get(2)+"");
		lbl_free.setText(list.get(3)+"");
	}
}
