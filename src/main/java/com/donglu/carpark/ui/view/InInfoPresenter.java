package com.donglu.carpark.ui.view;



import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.ShowDialog;
import com.donglu.carpark.ui.task.CarInTask;
import com.donglu.carpark.ui.view.inouthistory.InCheckDetailPresenter;
import com.dongluhitec.card.domain.util.StrUtil;


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

	public void check(String editPlateNO) {
		if (StrUtil.isEmpty(model.getMapInCheck().keySet())||(editPlateNO.length()<2||editPlateNO.length()>10)) {
			return;
		}
		if (!StrUtil.isEmpty(editPlateNO)&&model.getMapInCheck().keySet().size()==1) {
			for (String plateNO : model.getMapInCheck().keySet()) {
				
				CarInTask in=model.getMapInCheck().get(plateNO);
				in.setEditPlateNo(editPlateNO);
				in.refreshUserAndHistory();
				try {
					in.checkUser(false);
					model.getMapInCheck().clear();
					model.setInCheckClick(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else{
    		InCheckDetailPresenter p=new InCheckDetailPresenter();
    		p.setModel(model);
    		ShowDialog s=new ShowDialog("待确认车辆");
    		s.setPresenter(p);
    		s.setHaveButon(false);
    		s.setSize(500, 500);
    		s.open();
    		model.getMapInCheck().clear();
			model.setInCheckClick(false);
		}
	}
}
