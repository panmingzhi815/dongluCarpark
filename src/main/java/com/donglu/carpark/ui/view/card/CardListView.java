package com.donglu.carpark.ui.view.card;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCard;

public class CardListView extends AbstractListView<SingleCarparkCard>implements View {
	public CardListView(Composite parent, int style) {
		super(parent, style, SingleCarparkCard.class,
				new String[] { SingleCarparkCard.Property.identifier.name(),
						SingleCarparkCard.Property.serialNumber.name(),
						},
				new String[] { "卡片编号", "卡片内码", }, new int[] { 120, 180 }, null);
	}

	@Override
	public CardListPresenter getPresenter() {
		return (CardListPresenter) presenter;
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
	}

}
