package com.donglu.carpark.ui.list;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;

public class BlackUserListView extends AbstractListView<SingleCarparkBlackUser> implements View {
	public BlackUserListView(Composite parent, int style) {
		super(parent, style,SingleCarparkBlackUser.class,new String[]{SingleCarparkBlackUser.Property.plateNO.name(),
				SingleCarparkBlackUser.Property.remark.name()}, new String[]{"车牌号","备注"},
				new int[]{100,200});
	}

	@Override
	public BlackUserListPresenter getPresenter() {
		return (BlackUserListPresenter) presenter;
	}

	@Override
	protected void searchMore() {
	}
}
