package com.donglu.carpark.ui.view;



import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.ui.CarparkMainPresenter;
import com.donglu.carpark.ui.common.Presenter;
import com.dongluhitec.card.common.ui.CommonUIFacility;


public class OutInfoPresenter  implements Presenter{
	
	private OutInfoView view;
	private CarparkMainModel model;
	private CarparkMainPresenter presenter;
	private CommonUIFacility commonui;
	@Override
	public void go(Composite c) {
		view=new OutInfoView(c, c.getStyle(),model,commonui);
		view.setPresenter(this);
	}

	public void setModel(CarparkMainModel model) {
		this.model = model;
	}

	public CLabel getOutBigImgLabel() {
		return view.getLbl_bigImg();
	}

	public CLabel getOutSmallImgLabel() {
		return view.getLbl_smallImg();
	}

	public void showManualSearch(String data, String bigImg, String smallImg) {
		presenter.showManualSearch(data, bigImg, smallImg);
	}

	public void setPresenter(CarparkMainPresenter presenter) {
		this.presenter = presenter;
	}

	public void setCommonui(CommonUIFacility commonui) {
		this.commonui = commonui;
	}
}
