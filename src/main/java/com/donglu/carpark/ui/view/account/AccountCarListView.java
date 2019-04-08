package com.donglu.carpark.ui.view.account;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkAccountCar;
import com.dongluhitec.card.domain.db.singlecarpark.SystemUserTypeEnum;

public class AccountCarListView extends AbstractListView<CarparkAccountCar> {

	public AccountCarListView(Composite parent) {
		super(parent, parent.getStyle(), CarparkAccountCar.class, new String[]{"plateNo","name"}, new String[]{"车牌","名称"}, new int[]{150,200});
	}
	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
		if(!ConstUtil.checkPrivilege(SystemUserTypeEnum.普通管理员)) {
			return;
		}
		ToolItem toolItem = new ToolItem(toolBar_menu, SWT.NULL);
		toolItem.setText("导入");
		toolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().importPlate();
			}
		});
		toolItem.setToolTipText("从固定车模板中导入数据");
		ToolItem toolItem_export = new ToolItem(toolBar_menu, SWT.NULL);
		toolItem_export.setText("导出");
		toolItem_export.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().exportPlate();
			}
		});
		super.createMenuBarToolItem(toolBar_menu);
		ToolItem toolItem_edit = new ToolItem(toolBar_menu, SWT.NULL);
		toolItem_edit.setText("修改");
		toolItem_edit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().edit();
			}
		});
	}
	
	@Override
	public AccountCarListPresenter getPresenter() {
		return (AccountCarListPresenter) super.getPresenter();
	}
}
