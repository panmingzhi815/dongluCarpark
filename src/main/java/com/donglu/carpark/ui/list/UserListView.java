package com.donglu.carpark.ui.list;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;

public class UserListView extends AbstractListView<SingleCarparkUser> implements View {
	//SingleCarparkUser.Property.id.name(),"编号",60,
	public UserListView(Composite parent, int style) {
		super(parent, style,SingleCarparkUser.class,new String[]{
				SingleCarparkUser.Property.plateNo.name(),
				SingleCarparkUser.Property.name.name(),
				SingleCarparkUser.Property.address.name(),
				SingleCarparkUser.Property.type.name(),
				SingleCarparkUser.Property.valitoLabel.name(),
				SingleCarparkUser.Property.carparkNo.name(),
				SingleCarparkUser.Property.remark.name()}, new String[]{"车牌号","姓名","住址","用户类型","有效期","车位","备注"},
				new int[]{100,100,100,100,200,100,100}, null);
	}

	@Override
	public UserListPresenter getPresenter() {
		return (UserListPresenter) presenter;
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
		ToolItem toolItem_impot = new ToolItem(toolBar_menu, SWT.NONE);
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
		toolItem_export.setText("导出");
		
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
