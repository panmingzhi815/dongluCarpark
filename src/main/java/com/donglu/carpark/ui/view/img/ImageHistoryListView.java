package com.donglu.carpark.ui.view.img;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkOffLineHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkImageHistory;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class ImageHistoryListView extends AbstractListView<SingleCarparkImageHistory> implements View{
	
	private Label lbl_total;
	private Label lbl_true;
	private Label lbl_false;
	private Label lbl_trueToTotal;
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
		ToolItem toolItem_true = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_true.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().setTrue();
			}
		});
		toolItem_true.setText("正确");
		
		ToolItem toolItem_false = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_false.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().setFalse();
			}
		});
		toolItem_false.setText("错误");
		
		ToolItem toolItem_delete = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_delete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().delete(getModel().getSelected());
			}
		});
		toolItem_delete.setText("删除");
	}
	@Override
	protected void searchMore() {
		getPresenter().searchMore();
	}


	@Override
	public ImageHistoryListPresenter getPresenter() {
		return (ImageHistoryListPresenter) presenter;
	}
	@Override
	protected void createBottomComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(8, false));
		composite_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		
		Label label = new Label(composite_1, SWT.NONE);
		label.setText("原始:");
		
		lbl_total = new Label(composite_1, SWT.NONE);
		GridData gd_label_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label_1.widthHint = 50;
		lbl_total.setLayoutData(gd_label_1);
		lbl_total.setText("0");
		
		Label label_2 = new Label(composite_1, SWT.NONE);
		label_2.setText("正确:");
		
		lbl_true = new Label(composite_1, SWT.NONE);
		GridData gd_label_3 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label_3.widthHint = 50;
		lbl_true.setLayoutData(gd_label_3);
		lbl_true.setText("0");
		
		Label label_4 = new Label(composite_1, SWT.NONE);
		label_4.setText("错误:");
		
		lbl_false = new Label(composite_1, SWT.NONE);
		GridData gd_label_5 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label_5.widthHint = 50;
		lbl_false.setLayoutData(gd_label_5);
		lbl_false.setText("0");
		
		Label label_6 = new Label(composite_1, SWT.NONE);
		label_6.setText("正确率:");
		
		lbl_trueToTotal = new Label(composite_1, SWT.NONE);
		GridData gd_label_7 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label_7.widthHint = 35;
		lbl_trueToTotal.setLayoutData(gd_label_7);
		lbl_trueToTotal.setText("0");
	}
	public void setInfo(int size,int tSize,int fSize){
		lbl_total.setText(""+size);
		lbl_true.setText(""+tSize);
		lbl_false.setText(""+fSize);
		double d=0;
		if (tSize+fSize>0) {
			d=tSize*1d/(tSize+fSize)*1d;
		}
		lbl_trueToTotal.setText(""+d);
	}
}
