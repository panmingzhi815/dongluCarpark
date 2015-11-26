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
import com.donglu.carpark.ui.wizard.monthcharge.MonthlyUserPayModel;
import com.donglu.carpark.ui.wizard.monthcharge.MonthlyUserPayWizard;
import com.donglu.carpark.util.ExcelImportExport;
import com.donglu.carpark.util.ExcelImportExportImpl;
import com.donglu.carpark.util.SystemLog;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class UserListPresenter extends AbstractListPresenter<SingleCarparkUser>{
	UserListView view;
	
	String userName; 
	String plateNo;
	int will=0; 
	String ed;
	
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Override
	public void go(Composite c) {
		view=new UserListView(c,c.getStyle());
		view.setPresenter(this);
		view.setTableTitle("固定用户列表");
		view.setShowMoreBtn(false);
	}

	
	public void add() {
		try {
			CarparkService carparkService = sp.getCarparkService();
			List<SingleCarparkCarpark> list = carparkService.findAllCarpark();
			if (StrUtil.isEmpty(list)) {
				commonui.error("错误", "请先创建停车场！！");
				return;
			}
			MonthlyUserPayModel model = new MonthlyUserPayModel();
			model.setAllmonth(sp.getCarparkService().findAllMonthlyCharge());
			AddUserModel addUserModel = new AddUserModel();
			addUserModel.setAllList(list);
			addUserModel.setCarpark(list.get(0));
			addUserModel.setType("普通");
			addUserModel.setCarparkNo("1");
			addUserModel.setModel(model);
			addUserModel.setTotalSlot(sp.getCarparkInOutService().findFixSlotIsNow(list.get(0)));
			System.out.println(addUserModel.getTotalSlot());
			AddUserWizard addUserWizard = new AddUserWizard(addUserModel,sp);
			AddUserModel m = (AddUserModel) commonui.showWizard(addUserWizard);
			if (m == null) {
				return;
			}

			SingleCarparkUser user = m.getSingleCarparkUser();
			Date createDate = m.getCreateDate() == null ? new Date() : m.getCreateDate();
			user.setCreateDate(createDate);
			user.setValidTo(m.getModel().getOverdueTime());
			CarparkUserService carparkUserService = sp.getCarparkUserService();
			carparkUserService.saveUser(user);
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "添加了用户:"+user.getName());
			MonthlyUserPayModel mm = m.getModel();
			if (!StrUtil.isEmpty(mm.getSelectMonth())) {
				mm.setOperaName(System.getProperty("userName"));
				SingleCarparkMonthlyCharge selectMonth = mm.getSelectMonth();
				if (!StrUtil.isEmpty(selectMonth)) {
					user.setDelayDays(selectMonth.getDelayDays());
					user.setRemindDays(selectMonth.getExpiringDays());
					user.setMonthChargeId(selectMonth.getId());
				}
				carparkService.saveMonthlyUserPayHistory(mm.getSingleCarparkMonthlyUserPayHistory());
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "充值了用户:"+user.getName());
			}
			
			commonui.info("操作成功", "保存成功!");
			refresh();
		} catch (Exception e) {
			commonui.info("操作失败", "保存用户失败!");
		}

	}

	@Override
	public void delete(List<SingleCarparkUser> list) {
		try {
			boolean confirm = commonui.confirm("删除提示", "确定删除选中的"+list.size()+"条记录");
			if (!confirm) {
				return;
			}
			String userName="";
			CarparkUserService carparkUserService = sp.getCarparkUserService();
			for (SingleCarparkUser singleCarparkUser : list) {
				carparkUserService.deleteUser(singleCarparkUser);
				userName+="["+singleCarparkUser.getName()+"]";
			}
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "删除了用户:"+userName);
			commonui.info("成功", "删除用户成功");
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("失败", "删除用户失败"+e.getMessage());
		}
	}

	@Override
	public void refresh() {
		List<SingleCarparkUser> findByNameOrPlateNo = sp.getCarparkUserService().findByNameOrPlateNo(userName, plateNo, will, ed);
		view.getModel().setList(findByNameOrPlateNo);
		view.getModel().setCountSearch(findByNameOrPlateNo.size());
		view.getModel().setCountSearchAll(findByNameOrPlateNo.size());
	}

	public void search(String userName, String plateNo, int will, String ed) {
		this.userName=userName;
		this.plateNo=plateNo;
		this.will=will;
		this.ed=ed;
		refresh();
	}

	public void pay() {
		try {
			MonthlyUserPayModel model = new MonthlyUserPayModel();
			List<SingleCarparkUser> selectList = view.getModel().getSelected();
			if (StrUtil.isEmpty(selectList)) {
				return;
			}

			SingleCarparkUser singleCarparkUser = selectList.get(0);
			if (singleCarparkUser.getType().equals("免费")) {
				model.setFree(false);
			}
			model.setUserName(singleCarparkUser.getName());
			model.setCreateTime(singleCarparkUser.getCreateDate());
			model.setPlateNO(singleCarparkUser.getPlateNo());
			model.setAllmonth(sp.getCarparkService().findMonthlyChargeByCarpark(singleCarparkUser.getCarpark()));
			model.setOverdueTime(singleCarparkUser.getValidTo());
			MonthlyUserPayWizard wizard = new MonthlyUserPayWizard(model);
			MonthlyUserPayModel m = (MonthlyUserPayModel) commonui.showWizard(wizard);
			if (StrUtil.isEmpty(m)) {
				return;
			}
			if (StrUtil.isEmpty(m.getSelectMonth())) {
				return;
			}
			singleCarparkUser.setValidTo(m.getOverdueTime());
			singleCarparkUser.setDelayDays(m.getSelectMonth().getDelayDays());
			singleCarparkUser.setRemindDays(m.getSelectMonth().getExpiringDays());
			singleCarparkUser.setMonthChargeId(m.getSelectMonth().getId());
			singleCarparkUser.setCarpark(m.getSelectMonth().getCarpark());
			m.setOperaName(System.getProperty("userName"));
			sp.getCarparkUserService().saveUser(singleCarparkUser);
			sp.getCarparkService().saveMonthlyUserPayHistory(m.getSingleCarparkMonthlyUserPayHistory());
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "充值了用户:"+singleCarparkUser.getName());
			commonui.info("操作成功", "充值成功");
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("失败", "充值失败"+e.getMessage());
		}
		
	}

	public void importAll() {
		try {
			String path = commonui.selectToSave();
			if (StrUtil.isEmpty(path)) {
				return;
			}
			ExcelImportExport export=new ExcelImportExportImpl();
			int excelRowNum = export.getExcelRowNum(path);
			if (excelRowNum<3) {
				return;
			}
			int importUser = export.importUser(path, sp);
			if (importUser>0) {
				commonui.info("导入提示", "导入完成。有"+importUser+"条数据导入失败");
			}else{
				commonui.info("导入提示", "导入成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("导入提示", "导入失败");
		}finally{
			refresh();
		}
		
	}

	public void exportAll() {
		String selectToSave = commonui.selectToSave();
		if (StrUtil.isEmpty(selectToSave)) {
			return;
		}
		String path = StrUtil.checkPath(selectToSave,  new String[] { ".xls", ".xlsx" }, ".xls");
		ExcelImportExport export=new ExcelImportExportImpl();
		List<SingleCarparkUser> allList = view.getModel().getList();
		try {
			export.exportUser(path, allList);
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "导出了"+allList.size()+"条记录");
			commonui.info("导出提示", "导出成功！");
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("导出提示", "导出时发生错误！"+e.getMessage());
		}
		
	}

	public void edit() {

		List<SingleCarparkUser> selectList = view.getModel().getSelected();
		if (StrUtil.isEmpty(selectList)) {
			return;
		}
		try {
			CarparkService carparkService = sp.getCarparkService();
			List<SingleCarparkCarpark> list = carparkService.findAllCarpark();
			SingleCarparkUser singleCarparkUser = selectList.get(0);
			AddUserModel addUserModel = new AddUserModel();
			addUserModel.setAllList(list);
			addUserModel.setSingleCarparkUser(singleCarparkUser);
			addUserModel.setTotalSlot(sp.getCarparkInOutService().findFixSlotIsNow(singleCarparkUser.getCarpark()));
			AddUserWizard addUserWizard = new AddUserWizard(addUserModel,sp);
			AddUserModel m = (AddUserModel) commonui.showWizard(addUserWizard);
			if (m == null) {
				return;
			}

			SingleCarparkUser user = m.getSingleCarparkUser();
			CarparkUserService carparkUserService = sp.getCarparkUserService();
			carparkUserService.saveUser(user);
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "充值了用户:"+singleCarparkUser.getName());
			commonui.info("操作成功", "修改成功!");
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("操作失败", "修改失败!");
		}
	
	}
	
}
