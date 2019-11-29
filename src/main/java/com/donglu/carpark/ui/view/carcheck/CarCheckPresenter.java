package com.donglu.carpark.ui.view.carcheck;


import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractPresenter;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class CarCheckPresenter  extends AbstractPresenter{
	@Inject
	private CarCheckListPresenter listPresenter;
	public CarCheckListPresenter getListPresenter() {
		return listPresenter;
	}
	
	public void search(String plateNo, Date start, Date end, String type, String status, String edit, String editPlateSize) {
		if ("全部".equals(type)) {
			type=null;
		}
		if ("全部".equals(status)) {
			status=null;
		}
		if ("全部".equals(edit)) {
			edit=null;
		}
		listPresenter.search(plateNo,start,end,type,status,edit==null?null:"已修改".equals(edit),StrUtil.isEmpty(editPlateSize)?null:Integer.valueOf(editPlateSize));
	}
	@Override
	protected CarCheckView createView(Composite c) {
		return new CarCheckView(c, c.getStyle());
	}

	@Override
	protected void continue_go() {
		listPresenter.go(getView().getListComposite());
	}

	@Override
	public CarCheckView getView() {
		return (CarCheckView) super.getView();
	}
	
}
