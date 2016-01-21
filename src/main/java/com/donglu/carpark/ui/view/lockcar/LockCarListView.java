package com.donglu.carpark.ui.view.lockcar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkLockCar;

public class LockCarListView extends AbstractListView<SingleCarparkLockCar> implements View {
	public LockCarListView(Composite parent, int style) {
		super(parent, style,SingleCarparkLockCar.class,new String[]{
				SingleCarparkLockCar.Property.plateNO.name(),
				SingleCarparkLockCar.Property.status.name(),
				SingleCarparkLockCar.Property.operaName.name(),
				SingleCarparkLockCar.Property.createTimeLabel.name(),
				}, new String[]{"车牌号","状态","操作员","创建时间"},
				new int[]{100,100,100,200}, null);
	}

	@Override
	public LockCarListPresenter getPresenter() {
		return (LockCarListPresenter) presenter;
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
				getPresenter().lockCar();
			}
		});
		toolItem_pay.setText("锁车");
		
		ToolItem toolItem_edit = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_edit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().unlockCar();
			}
		});
		toolItem_edit.setText("解锁");
	}
	
}
