package com.donglu.carpark.ui.view.user;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkService;
import com.donglu.carpark.service.CarparkUserService;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.view.user.wizard.AddUserModel;
import com.donglu.carpark.ui.view.user.wizard.AddUserWizard;
import com.donglu.carpark.ui.view.user.wizard.MonthlyUserPayModel;
import com.donglu.carpark.ui.view.user.wizard.MonthlyUserPayWizard;
import com.donglu.carpark.util.ExcelImportExport;
import com.donglu.carpark.util.ExcelImportExportImpl;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.util.ThreadUtil;
import com.google.inject.Inject;

public class UserListPresenter extends AbstractListPresenter<SingleCarparkUser>{
	private static final Logger log = LoggerFactory.getLogger(UserListPresenter.class);
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
		refresh();
		expirationReminder();
	}

	
	private void expirationReminder() {
		ExecutorService userRemindThreadPool = Executors.newSingleThreadExecutor(ThreadUtil.createThreadFactory("固定车到期提醒线程池"));
		ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("定时检测固定用户是否到期"));
		newSingleThreadScheduledExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				final Date date = new Date();
				log.info("{}进行固定车到期提醒操作",date);
				final SingleCarparkSystemSetting findSystemSettingByKey = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.固定车提醒时间.name());
				if (findSystemSettingByKey != null && findSystemSettingByKey.getSettingValue().equals(StrUtil.formatDate(date))) {
					log.info("今天已经提醒过了，不在提醒");
					return;
				}
				List<SingleCarparkUser> list = view.getModel().getList();
				for (final SingleCarparkUser user : list) {
					if ((user.getRemindDays() == null || user.getRemindDays() == 0) && user.getValidTo().after(date)) {
						continue;
					}
					Runnable runnable = new Runnable() {
						public void run() {
							Display.getDefault().asyncExec(new Runnable() {
								@Override
								public void run() {
									// if (user.getValidTo().before(date)) {
									// continue;
									// }
									log.info("{}即将过期,过期时间：{}", user, user.getValidTo());
									UserRemindMessageBox window = new UserRemindMessageBox(user);
									int open = window.open();
									if (open == 1) {
										view.getModel().setSelected(Arrays.asList(user));
									} else if (open == 3) {
										SingleCarparkSystemSetting ss = findSystemSettingByKey;
										if (findSystemSettingByKey == null) {
											ss = new SingleCarparkSystemSetting();
											ss.setSettingKey(SystemSettingTypeEnum.固定车提醒时间.name());
										}
										ss.setSettingValue(StrUtil.formatDate(date));
										sp.getCarparkService().saveSystemSetting(ss);
									}
								}
							});
						}
					};
					userRemindThreadPool.submit(runnable);
				}
			}
		}, 10, 60*60, TimeUnit.SECONDS);
		
	}


	@Override
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
			addUserModel.setCarparkNo("0");
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
			MonthlyUserPayModel mm = m.getModel();
			user.setValidTo(mm.getOverdueTime()==null?new Date():mm.getOverdueTime());
			user.setDelayDays(5);
			user.setRemindDays(5);
			mm.setUserType(user.getType());
			String operaName = System.getProperty("userName");
			mm.setOperaName(operaName);
			if (user.getType().equals("普通")) {
				SingleCarparkMonthlyCharge selectMonth = mm.getSelectMonth();
				if (!StrUtil.isEmpty(selectMonth)) {
					user.setDelayDays(selectMonth.getDelayDays());
					user.setRemindDays(selectMonth.getExpiringDays());
					user.setMonthChargeId(selectMonth.getId());
					carparkService.saveMonthlyUserPayHistory(mm.getSingleCarparkMonthlyUserPayHistory());
					sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "充值了普通用户:"+user.getName(),operaName);
				}
			}else if(user.getType().equals("免费")){
				if (mm.getOverdueTime()!=null) {
					carparkService.saveMonthlyUserPayHistory(mm.getSingleCarparkMonthlyUserPayHistory());
					sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "充值了免费用户:"+user.getName(),operaName);
				}
			}else if(user.getType().equals("储值")){
				if (mm.getChargesMoney()!=null&&mm.getChargesMoney()>0) {
					carparkService.saveMonthlyUserPayHistory(mm.getSingleCarparkMonthlyUserPayHistory());
					sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "充值了储值用户:"+user.getName(),operaName);
					user.setLeftMoney(mm.getChargesMoney());
				}
				user.setValidTo(null);
			}
			
			CarparkUserService carparkUserService = sp.getCarparkUserService();
			carparkUserService.saveUser(user);
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "添加了用户:"+user.getName(),System.getProperty("userName"));
			commonui.info("操作成功", "保存成功!");
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
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
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "删除了用户:"+userName,System.getProperty("userName"));
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
				model.setPayMoney(false);
			}
			if (singleCarparkUser.getType().equals("储值")) {
				model.setFree(false);
				model.setPayDate(false);
				model.setPayMoney(true);
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
			singleCarparkUser.setValidTo(m.getOverdueTime());
			if (singleCarparkUser.getType().equals("普通")) {
				if (StrUtil.isEmpty(m.getSelectMonth())) {
					return;
				}
				singleCarparkUser.setDelayDays(m.getSelectMonth().getDelayDays());
				singleCarparkUser.setRemindDays(m.getSelectMonth().getExpiringDays());
				singleCarparkUser.setMonthChargeId(m.getSelectMonth().getId());
				singleCarparkUser.setCarpark(m.getSelectMonth().getCarpark());
			}
			if (singleCarparkUser.getType().equals("储值")) {
				Float chargesMoney = m.getChargesMoney();
				if (chargesMoney>0) {
					singleCarparkUser.setLeftMoney(singleCarparkUser.getLeftMoney()+chargesMoney);
				}else{
					commonui.info("提示", "充值金额不能小于0");
					return;
				}
			}
			m.setOperaName(System.getProperty("userName"));
			m.setUserType(singleCarparkUser.getType());
			sp.getCarparkUserService().saveUser(singleCarparkUser);
			sp.getCarparkService().saveMonthlyUserPayHistory(m.getSingleCarparkMonthlyUserPayHistory());
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "充值了用户:"+singleCarparkUser.getName(),System.getProperty("userName"));
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
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "导出了"+allList.size()+"条记录",System.getProperty("userName"));
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
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "修改了用户:"+singleCarparkUser.getName(),System.getProperty("userName"));
			commonui.info("操作成功", "修改成功!");
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("操作失败", "修改失败!");
		}
	
	}
	
}
