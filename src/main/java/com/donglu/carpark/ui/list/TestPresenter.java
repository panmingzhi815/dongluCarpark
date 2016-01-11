package com.donglu.carpark.ui.list;


import com.dongluhitec.card.common.ui.Viewer;
import com.dongluhitec.card.common.ui.WidgetContainer;
import com.dongluhitec.card.common.ui.control.AbstractPresenter;
import com.google.inject.Inject;

public class TestPresenter extends AbstractPresenter{
	@Inject
	TestListPresenter listPresenter;
	
	@Override
	protected Viewer createViewer(WidgetContainer container) {
		return new TestView(container);
	}
	public TestListPresenter getListPresenter() {
		return listPresenter;
	}
	@Override
	public TestView getViewer() {
		return (TestView) super.getViewer();
	}
	@Override
	protected void continue_go() {
		listPresenter.go(getViewer().getListContainer());
		super.continue_go();
	}
}
