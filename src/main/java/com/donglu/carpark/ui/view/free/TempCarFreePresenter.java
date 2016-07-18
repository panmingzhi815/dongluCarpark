package com.donglu.carpark.ui.view.free;


import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractPresenter;
import com.google.inject.Inject;

public class TempCarFreePresenter  extends AbstractPresenter{
	@Inject
	private TempCarFreeListPresenter listPresenter;
	public TempCarFreeListPresenter getListPresenter() {
		return listPresenter;
	}
	
	public void search(String plateNo) {
		listPresenter.search(plateNo);
	}
	@Override
	protected TempCarFreeView createView(Composite c) {
		return new TempCarFreeView(c, c.getStyle());
	}

	@Override
	protected void continue_go() {
		listPresenter.go(getView().getListComposite());
	}

	@Override
	public TempCarFreeView getView() {
		return (TempCarFreeView) super.getView();
	}
	
}
