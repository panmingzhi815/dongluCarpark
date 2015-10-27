package com.donglu.carpark.ui.list;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;

public class UserListView extends AbstractListView<SingleCarparkUser> implements View {
	public UserListView(Composite parent, int style) {
		super(parent, style,SingleCarparkUser.class,new String[]{SingleCarparkUser.Property.id.name(),
				SingleCarparkUser.Property.plateNo.name(),
				SingleCarparkUser.Property.name.name(),
				SingleCarparkUser.Property.address.name(),
				SingleCarparkUser.Property.type.name(),
				SingleCarparkUser.Property.validTo.name(),
				SingleCarparkUser.Property.carparkNo.name(),
				SingleCarparkUser.Property.remark.name()}, new String[]{"编号","车牌号","姓名","住址","用户类型","有效期","车位","备注"},
				new int[]{60,100,100,100,100,200,100,100});
	}

	@Override
	public UserListPresenter getPresenter() {
		return (UserListPresenter) presenter;
	}

	@Override
	protected void searchMore() {
	}
}
