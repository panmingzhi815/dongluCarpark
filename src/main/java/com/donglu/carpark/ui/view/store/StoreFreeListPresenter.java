package com.donglu.carpark.ui.view.store;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkService;
import com.donglu.carpark.service.CarparkUserService;
import com.donglu.carpark.service.StoreServiceI;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.view.user.wizard.AddUserModel;
import com.donglu.carpark.ui.view.user.wizard.AddUserWizard;
import com.donglu.carpark.ui.view.user.wizard.MonthlyUserPayModel;
import com.donglu.carpark.ui.view.user.wizard.MonthlyUserPayWizard;
import com.donglu.carpark.ui.wizard.AddBlackUserWizard;
import com.donglu.carpark.ui.wizard.store.AddStoreWizard;
import com.donglu.carpark.ui.wizard.store.ChargeStoreModel;
import com.donglu.carpark.ui.wizard.store.ChargeStoreWizard;
import com.donglu.carpark.util.ExcelImportExport;
import com.donglu.carpark.util.ExcelImportExportImpl;
import com.donglu.carpark.util.SystemLog;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStore;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStoreChargeHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStoreFreeHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class StoreFreeListPresenter extends AbstractListPresenter<SingleCarparkStoreFreeHistory> {
	StoreFreeListView view;

	String storeName;

	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkDatabaseServiceProvider sp;


	private Date start;

	private Date end;

	private String plateNO;

	private String used;

	@Override
	public void go(Composite c) {
		view = new StoreFreeListView(c, c.getStyle());
		view.setPresenter(this);
		view.setTableTitle("商铺充值记录表");
		view.setShowMoreBtn(true);
	}
	
	@Override
	public void refresh() {
		List<SingleCarparkStoreFreeHistory> findByNameOrPlateNo = sp.getStoreService().findByPlateNO(0, 50,storeName, plateNO, used, start, end);
		view.getModel().setList(findByNameOrPlateNo);
		view.getModel().setCountSearch(findByNameOrPlateNo.size());
		view.getModel().setCountSearchAll(sp.getStoreService().countByPlateNO(storeName, plateNO,used, start, end).intValue());
	}

	public void search(String storeName, String plateNO,String used, Date start, Date end) {
		this.storeName = storeName;
		this.plateNO=plateNO;
		this.used=used;
		this.start=start;
		this.end=end;
		refresh();
	}

	public void exportAll() {

		String selectToSave = commonui.selectToSave();
		if (StrUtil.isEmpty(selectToSave)) {
			return;
		}
		String path = StrUtil.checkPath(selectToSave, new String[] { ".xls", ".xlsx" }, ".xls");
		ExcelImportExport export = new ExcelImportExportImpl();
		List<SingleCarparkStoreFreeHistory> list = view.getModel().getList();
		try {
			export.export(path, view.getNameProperties(), view.getColumnProperties(), list);
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.商铺, "导出了" + list.size() + "条商铺免费记录");
			commonui.info("导出提示", "导出成功！");
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("导出提示", "导出时发生错误！" + e.getMessage());
		}

	}
}
