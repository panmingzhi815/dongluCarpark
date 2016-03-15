package com.donglu.carpark.ui.view.inouthistory;



import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.task.CarInTask;

public class InCheckDetailPresenter implements Presenter{
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
		if (check) {
			model.setInCheckIsClick(false);
			carInTask.refreshUserAndHistory();
		}
		try {
			carInTask.checkUser(false);
			model.getMapInCheck().remove(plateNO);
			if (model.getMapInCheck().keySet().size()<1) {
				view.getShell().dispose();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
