package com.donglu.carpark.ui.view.user;

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
	private SingleCarparkMonthlyCharge monthlyCharge;
	private String address;

	
	private void expirationReminder() {
		SingleCarparkSystemSetting findSystemSettingByKey = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.固定车到期提醒.name());
		if (findSystemSettingByKey==null||findSystemSettingByKey.getSettingValue().equals("false")) {
			return;
		}
		ExecutorService userRemindThreadPool = Executors.newSingleThreadExecutor(ThreadUtil.createThreadFactory("固定车到期提醒线程池"));
		ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("定时检测固定用户是否到期"));
		newSingleThreadScheduledExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				final Date date = new Date();
				log.debug("{}进行固定车到期提醒操作",date);
				final SingleCarparkSystemSetting findSystemSettingByKey = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.固定车提醒时间.name());
				if (findSystemSettingByKey != null && findSystemSettingByKey.getSettingValue().equals(StrUtil.formatDate(date))) {
					log.info("今天已经提醒过了，不在提醒");
					return;
				}
				List<UserRemindMessageBox> listMessageBox=new ArrayList<>();
				List<SingleCarparkUser> list = view.getModel().getList();
				for (SingleCarparkUser user : list) {
					Date validTo = user.getValidTo();
					log.debug("用户：{}的过期时间为：{}",user,validTo);
					if (validTo==null) {
						continue;
					}
					Date remind =new DateTime(validTo).minusDays(user.getRemindDays() == null?5:user.getRemindDays()).toDate();
					if (remind.after(date)) {
						continue;
					}
					Runnable runnable = new Runnable() {
						public void run() {
							Display.getDefault().asyncExec(new Runnable() {
								@Override
								public void run() {
									if (findSystemSettingByKey != null && findSystemSettingByKey.getSettingValue().equals(StrUtil.formatDate(date))) {
										return;
									}
									log.debug("{}即将过期,过期时间：{}", user, user.getValitoLabel());
									UserRemindMessageBox window = new UserRemindMessageBox("用户"+user+"即将过期");
									listMessageBox.add(window);
									int open = window.open();
									listMessageBox.remove(window);
									if (open==0) {
										userRemindThreadPool.shutdownNow();
										for (UserRemindMessageBox userRemindMessageBox : listMessageBox) {
											userRemindMessageBox.close();
										}
										return;
									}
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
			addUserModel.setCarparkNo("1");
			addUserModel.setModel(model);
			addUserModel.setTotalSlot(sp.getCarparkInOutService().findFixSlotIsNow(list.get(0)));
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
			SingleCarparkMonthlyUserPayHistory singleCarparkMonthlyUserPayHistory = mm.getSingleCarparkMonthlyUserPayHistory();
			singleCarparkMonthlyUserPayHistory.setParkingSpace(user.getParkingSpace());
			singleCarparkMonthlyUserPayHistory.setUserAddress(user.getAddress());
			if (user.getType().equals("普通")) {
				SingleCarparkMonthlyCharge selectMonth = mm.getSelectMonth();
				if (!StrUtil.isEmpty(selectMonth)) {
					user.setDelayDays(selectMonth.getDelayDays());
					user.setRemindDays(selectMonth.getExpiringDays());
					user.setMonthChargeId(selectMonth.getId());
					user.setMonthChargeCode(selectMonth.getChargeCode());
					user.setMonthChargeName(selectMonth.getChargeName());
					carparkService.saveMonthlyUserPayHistory(singleCarparkMonthlyUserPayHistory);
					sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "充值了普通用户:"+user.getName(),operaName);
				}
			}else if(user.getType().equals("免费")){
				if (mm.getOverdueTime()!=null) {
					carparkService.saveMonthlyUserPayHistory(singleCarparkMonthlyUserPayHistory);
					sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "充值了免费用户:"+user.getName(),operaName);
				}
			}else if(user.getType().equals("储值")){
				if (mm.getChargesMoney()!=null&&mm.getChargesMoney()>0) {
					carparkService.saveMonthlyUserPayHistory(singleCarparkMonthlyUserPayHistory);
					sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "充值了储值用户:"+user.getName(),operaName);
					user.setLeftMoney(mm.getChargesMoney());
				}
				user.setValidTo(null);
			}else if(user.getType().equals("永久")){
				user.setValidTo(new DateTime(3000,1,1,1,1,1).toDate());
			}
			
			CarparkUserService carparkUserService = sp.getCarparkUserService();
			carparkUserService.saveUser(user);
			commonui.info("操作成功", "保存成功!");
			refresh();
		} catch (Exception e) {
			commonui.error("操作失败", "保存用户失败!"+e,e);
		}

	}

	@Override
	public void delete(List<SingleCarparkUser> list) {
		try {
			boolean confirm = commonui.confirm("删除提示", "确定删除选中的"+list.size()+"条记录");
			if (!confirm) {
				return;
			}
			CarparkUserService carparkUserService = sp.getCarparkUserService();
			for (SingleCarparkUser singleCarparkUser : list) {
				carparkUserService.deleteUser(singleCarparkUser);
			}
			commonui.info("成功", "删除用户成功");
			refresh();
		} catch (Exception e) {
			commonui.error("失败", "删除用户失败"+e.getMessage(),e);
		}
	}

	@Override
	protected List<SingleCarparkUser> findListInput() {
		return sp.getCarparkUserService().findByNameOrPlateNo(current,pageSize,userName, plateNo, address, monthlyCharge, will, ed);
	}
	@Override
	protected int getTotalSize() {
		return sp.getCarparkUserService().countByNameOrPlateNo(userName, plateNo, address, monthlyCharge, will, ed).intValue();
	}

	public void search(String userName, String plateNo, String address, SingleCarparkMonthlyCharge monthlyCharge, int will, String ed) {
		this.userName=userName;
		this.plateNo=plateNo;
		this.address = address;
		this.monthlyCharge = monthlyCharge;
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
			if (singleCarparkUser.getType().equals("普通")) {
				if (ConstUtil.getLevel()<99) {
					model.setFree(false);
					model.setSelectedSize(true);
					model.setPayDate(false);
					model.setPayMoney(false);
				}else{
					model.setFree(true);
					model.setSelectedSize(true);
					model.setPayDate(true);
					model.setPayMoney(true);
				}
			}
			model.setUserName(singleCarparkUser.getName());
			model.setCreateTime(singleCarparkUser.getCreateDate());
			model.setPlateNO(singleCarparkUser.getPlateNo());
			model.setAllmonth(sp.getCarparkService().findMonthlyChargeByCarpark(singleCarparkUser.getCarpark()));
			model.setCarparkSlot(singleCarparkUser.getCarparkSlot());
			model.setOldOverDueTime(singleCarparkUser.getValidTo()==null?singleCarparkUser.getCreateDate():singleCarparkUser.getValidTo());
			model.setUserId(singleCarparkUser.getId());
			model.setUserAddress(singleCarparkUser.getAddress());
			if (singleCarparkUser.getMonthChargeId()!=null) {
				SingleCarparkMonthlyCharge findMonthlyChargeById = sp.getCarparkService().findMonthlyChargeById(singleCarparkUser.getMonthChargeId());
				model.setSelectMonth(findMonthlyChargeById);
				if (findMonthlyChargeById==null) {
					model.setFree(true);
				}
			}else{
				if (!singleCarparkUser.getType().equals("免费"))
				model.setFree(true);
			}
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
				singleCarparkUser.setMonthChargeCode(m.getSelectMonth().getChargeCode());
				singleCarparkUser.setMonthChargeName(m.getSelectMonth().getChargeName());
//				singleCarparkUser.setCarpark(m.getSelectMonth().getCarpark());
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
			singleCarparkUser.setLastEditDate(new Date());
			sp.getCarparkUserService().saveUser(singleCarparkUser);
			SingleCarparkMonthlyUserPayHistory singleCarparkMonthlyUserPayHistory = m.getSingleCarparkMonthlyUserPayHistory();
			singleCarparkMonthlyUserPayHistory.setParkingSpace(singleCarparkUser.getParkingSpace());
			sp.getCarparkService().saveMonthlyUserPayHistory(singleCarparkMonthlyUserPayHistory);
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "充值了用户:"+singleCarparkUser.getName()+"-"+singleCarparkUser.getValitoLabel(),System.getProperty("userName"));
			commonui.info("操作成功", "充值成功");
			refresh();
		} catch (Exception e) {
			commonui.error("失败", "充值失败"+e.getMessage(),e);
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
			commonui.error("导入提示", "导入失败",e);
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
			commonui.error("导出提示", "导出时发生错误！"+e.getMessage(),e);
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
			user.setLastEditDate(new Date());
			carparkUserService.saveUser(user);
			commonui.info("操作成功", "修改成功!");
			refresh();
		} catch (Throwable e) {
			commonui.error("操作失败", "修改失败!",e);
		}
	
	}


	@Override
	protected View createView(Composite c) {
		view=new UserListView(c,c.getStyle());
		view.setTableTitle("固定用户列表");
		view.setShowMoreBtn(true);
		return view;
	}


	@Override
	protected void continue_go() {
		refresh();
		expirationReminder();
	}
	
}
