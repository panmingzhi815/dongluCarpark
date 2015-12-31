package com.donglu.carpark.ui.list.store;

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
import com.donglu.carpark.ui.wizard.AddBlackUserWizard;
import com.donglu.carpark.ui.wizard.AddUserModel;
import com.donglu.carpark.ui.wizard.AddUserWizard;
import com.donglu.carpark.ui.wizard.monthcharge.MonthlyUserPayModel;
import com.donglu.carpark.ui.wizard.monthcharge.MonthlyUserPayWizard;
import com.donglu.carpark.ui.wizard.store.AddStoreModel;
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
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class StoreListPresenter extends AbstractListPresenter<SingleCarparkStore> {
	StoreListView view;

	String storeName;

	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkDatabaseServiceProvider sp;

	@Override
	public void go(Composite c) {
		view = new StoreListView(c, c.getStyle());
		view.setPresenter(this);
		view.setTableTitle("固定用户列表");
		view.setShowMoreBtn(false);
	}

	public void add() {
		try {
			StoreServiceI storeService = sp.getStoreService();
			List<SingleCarparkCarpark> findAllCarpark = sp.getCarparkService().findAllCarpark();
			if (StrUtil.isEmpty(findAllCarpark)) {
				commonui.info("提示", "请先添加停车场");
				return;
			}
			AddStoreModel model=new AddStoreModel();
			model.setListCarpark(findAllCarpark);
			model.setCarpark(findAllCarpark.get(0));
			AddStoreWizard w = new AddStoreWizard(model);
			AddStoreModel m = (AddStoreModel) commonui.showWizard(w);
			if (StrUtil.isEmpty(m)) {
				return;
			}
			m.setCreateTime(new Date());
			m.setLeftFreeHour(0F);
			m.setLeftFreeMoney(0F);
			storeService.saveStore(m.getStore());
			commonui.info("操作成功", "保存成功!");
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
			commonui.info("操作失败", "保存失败!");
		}

	}

	@Override
	public void delete(List<SingleCarparkStore> list) {
		try {
			boolean confirm = commonui.confirm("删除提示", "确定删除选中的" + list.size() + "条记录");
			if (!confirm) {
				return;
			}
			String userName = "";
			CarparkUserService carparkUserService = sp.getCarparkUserService();
			for (SingleCarparkStore s : list) {
				sp.getStoreService().deleteStore(s);
			}
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.商铺, "删除了商铺:" + userName);
			commonui.info("成功", "删除商铺成功");
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("失败", "删除商铺失败" + e.getMessage());
		}
	}

	@Override
	public void refresh() {
		List<SingleCarparkStore> findByNameOrPlateNo = sp.getStoreService().findStoreByCondition(0, 50, storeName);
		view.getModel().setList(findByNameOrPlateNo);
		view.getModel().setCountSearch(findByNameOrPlateNo.size());
		view.getModel().setCountSearchAll(sp.getStoreService().countStoreByCondition(storeName).intValue());
	}

	public void search(String storeName) {
		this.storeName = storeName;
		refresh();
	}

	public void pay() {
		try {
			List<SingleCarparkStore> selected = view.getModel().getSelected();
			if (StrUtil.isEmpty(selected)) {
				return;
			}
			SingleCarparkStore store = selected.get(0);
			ChargeStoreModel model = new ChargeStoreModel();
			model.setStoryId(store.getId());
			model.setStoreName(store.getStoreName());
			model.setOperaName(System.getProperty("userName"));
			model.setLoginName(store.getLoginName());
			ChargeStoreWizard w = new ChargeStoreWizard(model);
			ChargeStoreModel m = (ChargeStoreModel) commonui.showWizard(w);
			if (StrUtil.isEmpty(m)) {
				return;
			}
			m.setCreateTime(new Date());
			if (m.getPayType().equals("金额")) {
				m.setFreeMoney(m.getNum());
				store.setLeftFreeMoney(store.getLeftFreeMoney()+m.getFreeMoney());
			} else if (m.getPayType().equals("时间")) {
				m.setFreeHours(m.getNum());
				store.setLeftFreeHour(store.getLeftFreeHour()+m.getFreeHours());
			} else if (m.getPayType().equals("优惠券")) {
				m.setCouponNum(m.getNum().intValue());
			}
			sp.getStoreService().saveStore(store);
			SingleCarparkStoreChargeHistory sc = m.getStoreCharge();
			sp.getStoreService().saveStorePay(sc);
			commonui.info("提示", "操作成功");
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("错误", "操作时发生错误");
		}
	}

	public void importAll() {
		try {
			String path = commonui.selectToSave();
			if (StrUtil.isEmpty(path)) {
				return;
			}
			ExcelImportExport export = new ExcelImportExportImpl();
			int excelRowNum = export.getExcelRowNum(path);
			if (excelRowNum < 3) {
				return;
			}
			int importUser = export.importUser(path, sp);
			if (importUser > 0) {
				commonui.info("导入提示", "导入完成。有" + importUser + "条数据导入失败");
			} else {
				commonui.info("导入提示", "导入成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("导入提示", "导入失败");
		} finally {
			refresh();
		}

	}

	public void exportAll() {
		/*
		 * String selectToSave = commonui.selectToSave(); if (StrUtil.isEmpty(selectToSave)) { return; } String path = StrUtil.checkPath(selectToSave, new String[] { ".xls", ".xlsx" }, ".xls");
		 * ExcelImportExport export=new ExcelImportExportImpl(); List<SingleCarparkUser> allList = view.getModel().getList(); try { export.exportUser(path, allList);
		 * sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "导出了"+allList.size()+"条记录"); commonui.info("导出提示", "导出成功！"); } catch (Exception e) { e.printStackTrace();
		 * commonui.error("导出提示", "导出时发生错误！"+e.getMessage()); }
		 * 
		 */}

	public void edit() {
		try {
			List<SingleCarparkStore> selected = view.getModel().getSelected();
			if (StrUtil.isEmpty(selected)) {
				return;
			}
			SingleCarparkStore s = selected.get(0);
			AddStoreModel model=new AddStoreModel();
			model.setInfo(s);
			model.setRePawword(s.getLoginPawword());
			model.setListCarpark(sp.getCarparkService().findAllCarpark());
			AddStoreWizard w = new AddStoreWizard(model);
			SingleCarparkStore m = (SingleCarparkStore) commonui.showWizard(w);
			if (StrUtil.isEmpty(m)) {
				return;
			}
			m.setLoginPawword(m.getLoginName());
			m.setLeftFreeHour(0F);
			m.setLeftFreeMoney(0F);
			sp.getStoreService().saveStore(m);
			commonui.info("操作成功", "保存成功!");
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
			commonui.info("操作失败", "保存失败!");
		}

	}

}
