package com.donglu.carpark.ui.view.account;

import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractPresenter;
import com.donglu.carpark.ui.common.View;
import com.google.inject.Inject;

public class AccountCarInOutPresenter extends AbstractPresenter {
	private AccountCarInOutListPresenter accountCarInOutListPresenter;
	@Inject
	public AccountCarInOutPresenter(AccountCarInOutListPresenter accountCarInOutListPresenter) {
		this.accountCarInOutListPresenter = accountCarInOutListPresenter;
	}

	@Override
	protected View createView(Composite c) {
		return new AccountCarInOutView(c);
	}
	@Override
	protected void continue_go() {
		accountCarInOutListPresenter.go(getView().getComposite_list());
	}
	@Override
	public AccountCarInOutView getView() {
		return (AccountCarInOutView) super.getView();
	}

	public void search(String plateNo, String name, Date start, Date end) {
		accountCarInOutListPresenter.search(plateNo, name, start, end);
	}
}
