package com.donglu.carpark.ui.list;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;

public class BlackUserListView extends AbstractListView<SingleCarparkBlackUser> implements View {
	public BlackUserListView(Composite parent, int style) {
		super(parent, style,SingleCarparkBlackUser.class,new String[]{SingleCarparkBlackUser.Property.plateNO.name(),SingleCarparkBlackUser.Property.timeLabel.name(),
				SingleCarparkBlackUser.Property.validLabel.name(),SingleCarparkBlackUser.Property.remark.name()}, new String[]{"车牌号","限制时间段","有效期","备注"},
				new int[]{100,120,120,200}, null);
	}

	@Override
	public BlackUserListPresenter getPresenter() {
		return (BlackUserListPresenter) presenter;
	}
	
	
	
	@Override
	protected void searchMore() {
	}

	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
		super.createMenuBarToolItem(toolBar_menu);
		ToolItem toolItem_add = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().edit();
			}
		});
		toolItem_add.setText("修改");
	}
}
