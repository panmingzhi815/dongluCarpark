package com.donglu.carpark.ui.list;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkService;
import com.donglu.carpark.service.CarparkUserService;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.wizard.AddBlackUserWizard;
import com.donglu.carpark.ui.wizard.AddUserModel;
import com.donglu.carpark.ui.wizard.AddUserWizard;
import com.donglu.carpark.ui.wizard.OpenDoorDetailWizard;
import com.donglu.carpark.ui.wizard.monthcharge.MonthlyUserPayModel;
import com.donglu.carpark.ui.wizard.monthcharge.MonthlyUserPayWizard;
import com.donglu.carpark.util.ExcelImportExport;
import com.donglu.carpark.util.ExcelImportExportImpl;
import com.donglu.carpark.util.SystemLog;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkOpenDoorLog;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemOperaLog;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class OpenDoorListPresenter extends AbstractListPresenter<SingleCarparkOpenDoorLog>{
	private OpenDoorLogListView view;
	private String operaName;
	private Date start;
	private Date end;
	private String deviceName;
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Override
	public void go(Composite c) {
		view=new OpenDoorLogListView(c,c.getStyle());
		view.setPresenter(this);
		view.setTableTitle("手动抬杆记录");
//		view.setShowMoreBtn(true);
	}
	@Override
	public void refresh() {
		
		List<SingleCarparkOpenDoorLog> findByNameOrPlateNo = sp.getCarparkInOutService().findOpenDoorLogBySearch(operaName,start,end,deviceName);
		view.getModel().setList(findByNameOrPlateNo);
		view.getModel().setCountSearch(findByNameOrPlateNo.size());
		view.getModel().setCountSearchAll(findByNameOrPlateNo.size());
	}

	
	public void search(String operaName, Date start, Date end, String deviceName) {
		this.operaName=operaName;
		this.start=start;
		this.end=end;
		this.deviceName=deviceName;
		refresh();
	}
	@Override
	public void mouseDoubleClick(List<SingleCarparkOpenDoorLog> list) {
		SingleCarparkOpenDoorLog singleCarparkOpenDoorLog = list.get(0);
		OpenDoorDetailWizard wizard=new OpenDoorDetailWizard(singleCarparkOpenDoorLog);
		commonui.showWizard(wizard);
	}
}
