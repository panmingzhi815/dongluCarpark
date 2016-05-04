package com.donglu.carpark.ui.view.visitor;


import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractPresenter;
import com.google.inject.Inject;

public class VisitorPresenter  extends AbstractPresenter{
	@Inject
	private VisitorListPresenter listPresenter;
	public VisitorListPresenter getListPresenter() {
		return listPresenter;
	}
	
	public void search(String userName, String plateNo) {
		listPresenter.search(userName,plateNo);
	}
	@Override
	protected VisitorView createView(Composite c) {
		return new VisitorView(c, c.getStyle());
	}

	@Override
	protected void continue_go() {
		listPresenter.go(getView().getListComposite());
	}

	@Override
	public VisitorView getView() {
		return (VisitorView) super.getView();
	}
	
}
