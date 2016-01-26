package com.donglu.carpark.ui.view.store;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStore;

public class StoreListView extends AbstractListView<SingleCarparkStore> implements View {
	public StoreListView(Composite parent, int style) {
		super(parent, style,SingleCarparkStore.class,new String[]{SingleCarparkStore.Property.storeName.name(),
				SingleCarparkStore.Property.address.name(),
				SingleCarparkStore.Property.userName.name(),
				SingleCarparkStore.Property.leftFreeMoney.name(),
				SingleCarparkStore.Property.leftFreeHour.name(),
				SingleCarparkStore.Property.createTimeLabel.name()}, new String[]{"商铺名称","地址","商铺主人","可用金额","可用时间","创建时间"},
				new int[]{100,100,100,100,100,200}, null);
	}

	@Override
	public StoreListPresenter getPresenter() {
		return (StoreListPresenter) presenter;
	}

	@Override
	protected void searchMore() {
	}

	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
		ToolItem toolItem_pay = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_pay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().pay();
			}
		});
		toolItem_pay.setText("续费");
		super.createMenuBarToolItem(toolBar_menu);
		/*ToolItem toolItem_impot = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_impot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().importAll();
			}
		});
		toolItem_impot.setText("导入");
		ToolItem toolItem_export = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_export.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().exportAll();
			}
		});
		toolItem_export.setText("导出");*/
		
		ToolItem toolItem_edit = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_edit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().edit();
			}
		});
		toolItem_edit.setText("修改");
	}
	
}
