package com.donglu.carpark.ui.list;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemOperaLog;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.google.inject.Inject;

public class SystemLogListPresenter extends AbstractListPresenter<SingleCarparkSystemOperaLog>{
	SystemLogListView view;
	String operaName;
	Date start;
	Date end;
	SystemOperaLogTypeEnum type;
	
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Override
	public void go(Composite c) {
		view=new SystemLogListView(c,c.getStyle());
		view.setPresenter(this);
		view.setTableTitle("操作员日志");
	}
	@Override
	public void refresh() {
		List<SingleCarparkSystemOperaLog> findByNameOrPlateNo = sp.getSystemOperaLogService().findBySearch(operaName,start,end,type);
		view.getModel().setList(findByNameOrPlateNo);
		view.getModel().setCountSearch(findByNameOrPlateNo.size());
		view.getModel().setCountSearchAll(findByNameOrPlateNo.size());
	}

	
	public void search(String operaName, Date start, Date end, SystemOperaLogTypeEnum type) {
		this.operaName=operaName;
		this.start=start;
		this.end=end;
		this.type=type;
		refresh();
	}
}
