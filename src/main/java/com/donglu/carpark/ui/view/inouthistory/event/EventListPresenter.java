package com.donglu.carpark.ui.view.inouthistory.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkService;
import com.donglu.carpark.service.CarparkUserService;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.ui.view.user.wizard.AddUserModel;
import com.donglu.carpark.ui.view.user.wizard.AddUserWizard;
import com.donglu.carpark.ui.view.user.wizard.MonthlyUserPayModel;
import com.donglu.carpark.ui.view.user.wizard.MonthlyUserPayWizard;
import com.donglu.carpark.util.ConstUtil;
import com.donglu.carpark.util.ExcelImportExport;
import com.donglu.carpark.util.ExcelImportExportImpl;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkEvent;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.util.ThreadUtil;
import com.google.inject.Inject;

public class EventListPresenter extends AbstractListPresenter<CarparkEvent>{
	private static final Logger log = LoggerFactory.getLogger(EventListPresenter.class);
	EventListView view;
	String userName; 
	String plateNo;
	String ed;
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	private Date start;
	private Date end;

	

	@Override
	protected List<CarparkEvent> findListInput() {
		return sp.getCarparkInOutService().findByCarparkEvent(current,pageSize,plateNo, start, end);
	}
	@Override
	protected int getTotalSize() {
		return sp.getCarparkInOutService().countByCarparkEvent(plateNo, start, end).intValue();
	}


	

	public void importAll() {
		try {
			String path = commonui.selectToSave();
			if (StrUtil.isEmpty(path)) {
				return;
			}
			ExcelImportExport export=new ExcelImportExportImpl();
			int excelRowNum = export.getExcelRowNum(path);
			if (excelRowNum<2) {
				return;
			}
			int importUser = export.importUser(path, sp);
			if (importUser>0) {
				commonui.info("导入提示", "导入完成。有"+importUser+"条数据导入失败");
			}else{
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "导入了"+(excelRowNum-3)+"条记录",System.getProperty("userName"));
				commonui.info("导入提示", "导入成功");
			}
		} catch (Exception e) {
			commonui.error("导入提示", "导入失败",e);
		}finally{
			refresh();
		}
		
	}

	public void exportAll() {
		
		
	}



	@Override
	protected View createView(Composite c) {
		view=new EventListView(c,c.getStyle());
		view.setTableTitle("事件列表");
		view.setShowMoreBtn(true);
		return view;
	}


	@Override
	protected void continue_go() {
		refresh();
	}
	public void search(String text, Date selection, Date selection2) {
		plateNo = text;
		start = selection;
		end = selection2;
		refresh();
	}
	
}
