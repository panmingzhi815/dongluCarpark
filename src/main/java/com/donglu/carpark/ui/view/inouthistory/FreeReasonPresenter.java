package com.donglu.carpark.ui.view.inouthistory;


import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.Presenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;

public class FreeReasonPresenter implements Presenter{
	private FreeReasonView view;
	private SingleCarparkInOutHistory model;
	@Override
	public void go(Composite c) {
		view=new FreeReasonView(c, c.getStyle());
		view.setPresenter(this);
		view.setModel(model);
	}
	@Override
	public Object getModel() {
		return model;
	}
	public void setModel(SingleCarparkInOutHistory model) {
		this.model = model;
	}
}
