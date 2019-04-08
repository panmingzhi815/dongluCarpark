package com.donglu.carpark.ui.view.account;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractPresenter;
import com.donglu.carpark.ui.common.View;
import com.google.inject.Inject;

public class AccountCarPresenter extends AbstractPresenter {
	private AccountCarListPresenter accountCarListPresenter;
	@Inject
	public AccountCarPresenter(AccountCarListPresenter accountCarListPresenter) {
		this.accountCarListPresenter = accountCarListPresenter;
	}
	@Override
	protected View createView(Composite c) {
		return new AccountCarView(c);
	}
	@Override
	protected void continue_go() {
		accountCarListPresenter.go(getView().getComposite_list());
	}
	@Override
	public AccountCarView getView() {
		return (AccountCarView) super.getView();
	}
	public void search(String plateNo, String name) {
		accountCarListPresenter.search(plateNo,name);
	}
	
	@Override
	public String getTitle() {
		return "记账车查询";
	}
}
