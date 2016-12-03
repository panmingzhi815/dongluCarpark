package com.donglu.carpark.ui.view.card;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.Presenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCard;

public class CardListView extends AbstractListView<SingleCarparkCard>{

	public CardListView(Composite parent, int style) {
		super(parent, style, SingleCarparkCard.class, new String[]{"serialNumber","user"}, new String[]{"内码","用户"}, new int[]{200,300},  new int[]{0,0});
	}
	
	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
		super.createMenuBarToolItem(toolBar_menu);
		ToolItem toolItem_delete = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_delete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<SingleCarparkCard> selected = getModel().getSelected();
				if (selected.size()<=0) {
					return;
				}
				getPresenter().edit(selected.get(0));
			}
		});
		toolItem_delete.setText("修改");
	}
	@Override
	public CardListPresenter getPresenter() {
		return (CardListPresenter) super.getPresenter();
	}
}
