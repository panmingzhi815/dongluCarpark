package com.donglu.carpark.ui.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.list.OpenDoorListPresenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.google.inject.Inject;

public class OpenDoorLogPresenter  implements Presenter{
	private OpenDoorLogView view;
	@Inject
	private OpenDoorListPresenter listPresenter;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Override
	public void go(Composite c) {
		view=new OpenDoorLogView(c, c.getStyle());
		view.setPresenter(this);
		listPresenter.go(view.getListComposite());
		
		List<SingleCarparkSystemUser> findAll = new ArrayList<>();
		SingleCarparkSystemUser e = new SingleCarparkSystemUser();
		e.setUserName("全部");
		findAll.add(e);
		findAll.addAll(sp.getSystemUserService().findAllSystemUser());
		view.setComboValue(findAll);
	}
	public OpenDoorListPresenter getListPresenter() {
		return listPresenter;
	}
	
	public void search(String operaName, Date start, Date end, String deviceName) {
		if (deviceName.equals(SystemOperaLogTypeEnum.全部)) {
			deviceName=null;
		}
		listPresenter.search(operaName, start, end, deviceName);
	}
	
}
