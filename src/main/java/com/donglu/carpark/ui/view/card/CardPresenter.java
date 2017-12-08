package com.donglu.carpark.ui.view.card;


import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractPresenter;
import com.google.inject.Inject;

public class CardPresenter  extends AbstractPresenter{
	@Inject
	private CardListPresenter listPresenter;
	public CardListPresenter getListPresenter() {
		return listPresenter;
	}
	
	public void search(String userName, String plateNo) {
		listPresenter.search(userName,plateNo);
	}
	@Override
	protected CardView createView(Composite c) {
		return new CardView(c, c.getStyle());
	}

	@Override
	protected void continue_go() {
		listPresenter.go(getView().getListComposite());
	}

	@Override
	public CardView getView() {
		return (CardView) super.getView();
	}
	
}
