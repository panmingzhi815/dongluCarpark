package com.donglu.carpark.ui.view.inouthistory;


import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.Presenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;

public class FreeReasonPresenter implements Presenter{
	private FreeReasonView view;
	private SingleCarparkInOutHistory model;
	private String reasons;
	@Override
	public void go(Composite c) {
		view=new FreeReasonView(c, c.getStyle(),model,reasons);
		view.setPresenter(this);
	}
	@Override
	public Object getModel() {
		return model;
	}
	public void setModel(SingleCarparkInOutHistory model) {
		this.model = model;
	}
	public void setReasons(String reasons) {
		this.reasons = reasons;
	}
	@Override
	public Composite getViewComposite() {
		return view;
	}
	
}
