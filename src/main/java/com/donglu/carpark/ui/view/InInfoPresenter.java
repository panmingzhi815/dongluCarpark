package com.donglu.carpark.ui.view;



import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.ui.common.Presenter;


public class InInfoPresenter  implements Presenter{
	
	private InInfoView view;
	private CarparkMainModel model;
	
	@Override
	public void go(Composite c) {
		view=new InInfoView(c, c.getStyle(),model);
		view.setPresenter(this);
	}

	public void setModel(CarparkMainModel model) {
		this.model = model;
	}

	public CLabel getInBigImgLabel() {
		return view.getLbl_bigImg();
	}

	public CLabel getInSmallImgLabel() {
		return view.getLbl_smallImg();
	}
}
