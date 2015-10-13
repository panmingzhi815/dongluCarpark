package com.donglu.carpark.ui.list;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.ui.view.CarparkPayHistoryPresenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;

public class ReturnAccountListView extends AbstractListView<SingleCarparkReturnAccount> implements View{
	private Presenter presenter;
	public ReturnAccountListView(Composite parent, int style) {
		super(parent, style, SingleCarparkReturnAccount.class,
				new String[]{SingleCarparkReturnAccount.Property.id.name(),
						SingleCarparkReturnAccount.Property.returnUser.name(),
						SingleCarparkReturnAccount.Property.operaName.name(),
						SingleCarparkReturnAccount.Property.shouldReturn.name(),
						SingleCarparkReturnAccount.Property.factReturn.name(),
						SingleCarparkReturnAccount.Property.returnTime.name(),},
				new String[]{"编号","归账人","操作员","应归账金额","实归账金额","归账实际时间"},
				new int[]{100,100,100,100,100,200});
		this.setTableTitle("归账记录表");
	}

	@Override
	protected void searchMore() {
		getPresenter().searchMore();
	}

	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public ReturnAccountListPresenter getPresenter() {
		return (ReturnAccountListPresenter) presenter;
	}

}
