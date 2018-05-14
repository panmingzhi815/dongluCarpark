package com.donglu.carpark.ui.view.inouthistory;



import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.task.CarInTask;

public class InCheckDetailPresenter implements Presenter{
	Logger LOGGER = LoggerFactory.getLogger(InCheckDetailPresenter.class);
	private InCheckDetailView view;
	private CarparkMainModel model;
	@Override
	public void go(Composite c) {
		view=new InCheckDetailView(c, c.getStyle(),model);
		view.setPresenter(this);
	}
	@Override
	public Object getModel() {
		return model;
	}
	public void setModel(CarparkMainModel model) {
		this.model = model;
	}
	@Override
	public Composite getViewComposite() {
		return view;
	}
	public boolean carIn(CarInTask carInTask, boolean check,String plateNO) {
		LOGGER.info("车辆：{}入场确认",plateNO);
		if (check) {
			model.setInCheckIsClick(false);
			carInTask.setEditPlateNo(plateNO);
			model.setInShowPlateNO(plateNO);
			carInTask.refreshUserAndHistory();
		}
		try {
			carInTask.checkUser(false);
			model.getMapInCheck().remove(plateNO);
			model.setInShowPlateNO(plateNO);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
