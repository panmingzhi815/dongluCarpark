package com.donglu.carpark.ui.view.visitor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkVisitor;

public class VisitorListView extends AbstractListView<SingleCarparkVisitor>implements View {
	public VisitorListView(Composite parent, int style) {
		super(parent, style, SingleCarparkVisitor.class,
				new String[] { SingleCarparkVisitor.Property.plateNO.name(),
						SingleCarparkVisitor.Property.name.name(),
						SingleCarparkVisitor.Property.telephone.name(),
						SingleCarparkVisitor.Label.validToLabel.name(), 
						SingleCarparkVisitor.Property.allIn.name(), 
						SingleCarparkVisitor.Property.inCount.name(),
						SingleCarparkVisitor.Property.outCount.name(),
						SingleCarparkVisitor.Property.status.name(),
						SingleCarparkVisitor.Property.carpark.name(),
						SingleCarparkVisitor.Property.remark.name(),SingleCarparkVisitor.Property.resean.name(), },
				new String[] { "车牌号", "姓名", "电话", "到期时间", "次数限制", "进场次数","出场次数","状态", "停车场","原因", "备注" }, new int[] { 100, 100, 100, 200, 100, 100, 100, 100, 100, 100, 200, 100 }, null);
	}

	@Override
	public VisitorListPresenter getPresenter() {
		return (VisitorListPresenter) presenter;
	}

	@Override
	protected void searchMore() {
		getPresenter().searchMore();
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
		
		ToolItem toolItem = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().printDispatchNote();
			}
		});
		toolItem.setText("打印");
	}

}
