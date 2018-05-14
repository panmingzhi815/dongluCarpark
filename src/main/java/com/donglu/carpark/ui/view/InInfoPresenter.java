package com.donglu.carpark.ui.view;



import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.ShowDialog;
import com.donglu.carpark.ui.task.CarInTask;
import com.donglu.carpark.ui.view.inouthistory.InCheckDetailPresenter;
import com.dongluhitec.card.domain.util.StrUtil;


public class InInfoPresenter  implements Presenter{
	private static final Logger LOGGER = LoggerFactory.getLogger(InInfoPresenter.class);
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
		LOGGER.info("车辆：{} 入场确认",editPlateNO);
		if (StrUtil.isEmpty(model.getMapInCheck().keySet())||(editPlateNO.length()<2)) {
			return;
		}
		if (!StrUtil.isEmpty(editPlateNO)&&model.getMapInCheck().keySet().size()==1) {
			for (String plateNO : model.getMapInCheck().keySet()) {
				CarInTask in=model.getMapInCheck().get(plateNO);
				in.setEditPlateNo(editPlateNO.split("-")[0]);
				in.refreshUserAndHistory();
				try {
					in.checkUser(false);
					model.getMapInCheck().clear();
					model.setInCheckClick(false);
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.error("{}入场确认时发生错误{}",editPlateNO,e);
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
