package com.donglu.carpark.ui.list;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;

public class ReturnAccountListView extends AbstractListView<SingleCarparkReturnAccount> implements View{
	public ReturnAccountListView(Composite parent, int style) {
		super(parent, style, SingleCarparkReturnAccount.class,
				new String[]{SingleCarparkReturnAccount.Property.id.name(),
						SingleCarparkReturnAccount.Property.returnUser.name(),
						SingleCarparkReturnAccount.Property.operaName.name(),
						SingleCarparkReturnAccount.Property.shouldReturn.name(),
						SingleCarparkReturnAccount.Property.factReturn.name(),
						SingleCarparkReturnAccount.Property.returnTimeLabel.name()},
				new String[]{"编号","归账人","操作员","归账金额","免费金额","归账时间"},
				new int[]{100,100,100,100,100,200}, null);
		this.setTableTitle("归账记录表");
	}

	@Override
	protected void searchMore() {
		getPresenter().searchMore();
	}
	@Override
	public ReturnAccountListPresenter getPresenter() {
		return (ReturnAccountListPresenter) presenter;
	}

	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
		
	}

}
