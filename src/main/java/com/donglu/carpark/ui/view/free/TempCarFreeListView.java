package com.donglu.carpark.ui.view.free;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkFreeTempCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkVisitor;

public class TempCarFreeListView extends AbstractListView<SingleCarparkFreeTempCar>implements View {
	public TempCarFreeListView(Composite parent, int style) {
		super(parent, style, SingleCarparkFreeTempCar.class,
				new String[] { SingleCarparkFreeTempCar.Property.plateNo.name(),
						SingleCarparkFreeTempCar.Property.freeMoney.name(),
						SingleCarparkFreeTempCar.Property.freeMinute.name(),
						SingleCarparkFreeTempCar.Label.statusLabel.name()
				},
				new String[] { "车牌号", "免费金额", "免费时间","状态" }, new int[] { 100, 100, 100,100}, null);
	}

	@Override
	public TempCarFreeListPresenter getPresenter() {
		return (TempCarFreeListPresenter) presenter;
	}

	@Override
	protected void searchMore() {
	}

	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
		super.createMenuBarToolItem(toolBar_menu);
		ToolItem toolItem_edit = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_edit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().edit();
			}
		});
		toolItem_edit.setText("修改");
		
		ToolItem toolItem_importByUse = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_importByUse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().importByUser();
			}
		});
		toolItem_importByUse.setText("从用户模板中导入");
	}

}
