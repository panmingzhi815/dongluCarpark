package com.donglu.carpark.ui.view;

import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.list.InOutHistoryListPresenter;
import com.donglu.carpark.ui.list.OpenDoorListPresenter;
import com.donglu.carpark.ui.list.ReturnAccountListPresenter;
import com.donglu.carpark.ui.list.SystemLogListPresenter;
import com.donglu.carpark.ui.list.UserListPresenter;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.google.inject.Inject;

public class OpenDoorLogPresenter  extends AbstractListPresenter{
	private OpenDoorLogView view;
	@Inject
	private OpenDoorListPresenter listPresenter;
	@Override
	public void go(Composite c) {
		view=new OpenDoorLogView(c, c.getStyle());
		view.setPresenter(this);
		listPresenter.go(view.getListComposite());
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
