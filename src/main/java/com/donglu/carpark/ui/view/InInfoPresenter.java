package com.donglu.carpark.ui.view;



import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.ShowDialog;
import com.donglu.carpark.ui.view.inouthistory.InCheckDetailPresenter;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;


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

	public void check() {
		if (StrUtil.isEmpty(model.getMapInCheck().keySet())) {
			return;
		}
		if (model.getMapInCheck().keySet().size()==1) {
			
		}else{
    		InCheckDetailPresenter p=new InCheckDetailPresenter();
    		p.setModel(model);
    		ShowDialog s=new ShowDialog("待确认车辆");
    		s.setPresenter(p);
    		s.setHaveButon(false);
    		s.setSize(500, 500);
    		s.open();
		}
	}
}
