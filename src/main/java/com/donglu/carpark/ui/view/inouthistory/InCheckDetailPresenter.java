package com.donglu.carpark.ui.view.inouthistory;


import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.task.CarInTask;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.util.StrUtil;

public class InCheckDetailPresenter implements Presenter{
	private InCheckDetailView view;
	private CarparkMainModel model;
	private CarparkDatabaseServiceProvider sp;
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
			sp=carInTask.getSp();
			model.setInCheckIsClick(false);
			carInTask.setPlateNO(plateNO);
			carInTask.showPlateToDevice();
			List<SingleCarparkInOutHistory> findByNoOut = sp.getCarparkInOutService().findByNoOut(plateNO, carInTask.getDevice().getCarpark());
			SingleCarparkInOutHistory cch = StrUtil.isEmpty(findByNoOut)?null:findByNoOut.get(0);
			if (StrUtil.isEmpty(cch)) {
				cch = new SingleCarparkInOutHistory();
			}
			carInTask.setCch(cch);
			SingleCarparkUser findUserByPlateNo = sp.getCarparkUserService().findUserByPlateNo(plateNO, carInTask.getDevice().getCarpark().getId());
			carInTask.setUser(findUserByPlateNo);
		}
		try {
			carInTask.checkUser(false);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	public void setSp(CarparkDatabaseServiceProvider sp) {
		this.sp = sp;
	}
	
}
