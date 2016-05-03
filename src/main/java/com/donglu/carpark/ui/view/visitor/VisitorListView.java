package com.donglu.carpark.ui.view.visitor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkVisitor;

public class VisitorListView extends AbstractListView<SingleCarparkVisitor>implements View {
	public VisitorListView(Composite parent, int style) {
		super(parent, style, SingleCarparkVisitor.class,
				new String[] { SingleCarparkVisitor.Property.plateNO.name(),
						SingleCarparkVisitor.Property.name.name(),
						SingleCarparkVisitor.Property.telephone.name(),
						SingleCarparkVisitor.Property.validTo.name(), 
						SingleCarparkVisitor.Property.allIn.name(), 
						SingleCarparkVisitor.Property.inCount.name(),
						SingleCarparkVisitor.Property.remark.name() },
				new String[] { "车牌号", "姓名", "电话", "到期时间", "次数限制", "进场次数", "停车场", "备注" }, new int[] { 100, 100, 100, 100, 100, 100, 100, 200, 100, 100, 100, 100 }, null);
	}

	@Override
	public VisitorListPresenter getPresenter() {
		return (VisitorListPresenter) presenter;
	}

	@Override
	protected void searchMore() {
	}

	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
		super.createMenuBarToolItem(toolBar_menu);
		ToolItem toolItem_impot = new ToolItem(toolBar_menu, SWT.NONE);
//		toolItem_impot.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				getPresenter().importAll();
//			}
//		});
//		toolItem_impot.setText("导入");
//		ToolItem toolItem_export = new ToolItem(toolBar_menu, SWT.NONE);
//		toolItem_export.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				getPresenter().exportAll();
//			}
//		});
//		toolItem_export.setText("导出");

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
