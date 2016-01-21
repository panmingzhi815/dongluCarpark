package com.donglu.carpark.ui.view.systemuser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;

public class SystemUserListView extends AbstractListView<SingleCarparkSystemUser> implements View {
	public SystemUserListView(Composite parent, int style) {
		super(parent, style,SingleCarparkSystemUser.class,new String[]{SingleCarparkSystemUser.Property.userName.name(),
				SingleCarparkSystemUser.Property.type.name(),
				SingleCarparkSystemUser.Property.createDateLabel.name(),
				SingleCarparkSystemUser.Property.lastEditDateLabel.name(),
				SingleCarparkSystemUser.Property.lastEditUser.name(),
				SingleCarparkSystemUser.Property.remark.name()},
				new String[]{"用户名称","用户类型","创建时间","最后修改时间","最后修改人","备注"},
				new int[]{100,110,200,200,100,200}, null);
	}

	@Override
	public SystemUserListPresenter getPresenter() {
		return (SystemUserListPresenter) presenter;
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
