package com.donglu.carpark;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bridj.cpp.std.list;

import com.donglu.carpark.info.CarparkChargeInfo;
import com.donglu.carpark.model.CarparkModel;
import com.donglu.carpark.model.InOutHistoryModel;
import com.donglu.carpark.model.SystemUserModel;
import com.donglu.carpark.model.UserModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkInOutServiceI;
import com.donglu.carpark.service.CarparkService;
import com.donglu.carpark.service.CarparkUserService;
import com.donglu.carpark.service.SystemUserServiceI;
import com.donglu.carpark.wizard.AddCarparkWizard;
import com.donglu.carpark.wizard.AddDeviceModel;
import com.donglu.carpark.wizard.AddMonthChargeWizard;
import com.donglu.carpark.wizard.AddSystemUserWizard;
import com.donglu.carpark.wizard.AddUserModel;
import com.donglu.carpark.wizard.AddUserWizard;
import com.donglu.carpark.wizard.EditSystemUserWizard;
import com.donglu.carpark.wizard.model.AddMonthChargeModel;
import com.donglu.carpark.wizard.monthcharge.MonthlyUserPayModel;
import com.donglu.carpark.wizard.monthcharge.MonthlyUserPayWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class CarparkManagePresenter {
	// 停车场管理界面
	private CarparkManageApp view;

	private CarparkModel carparkModel;// 停车场列表信息

	private UserModel userModel;// 固定用户信息
	
	private SystemUserModel systemUserModel;
	private InOutHistoryModel inOutHistoryModel;
	@Inject
	private CarparkDatabaseServiceProvider sp;

	@Inject
	CommonUIFacility commonui;

	/**
	 * 删除停车场
	 */
	public void deleteCarpark() {
		try {
			CarparkService carparkService = sp.getCarparkService();
			SingleCarparkCarpark carpark = carparkModel.getCarpark();
			if (StrUtil.isEmpty(carpark)) {
				return;
			}
			boolean confirm = commonui.confirm("删除提示", "是否删除选中停车场");
			if (!confirm) {
				return;
			}
			carparkService.deleteCarpark(carpark);
			commonui.info("提示", "删除成功！");
			refreshCarpark();
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("提示", "删除失败！" + e.getMessage());
		}
	}

	/**
	 * 添加停车场
	 */
	public void addCarpark() {
		try {
			CarparkService carparkService = sp.getCarparkService();
			AddCarparkWizard w = new AddCarparkWizard(new SingleCarparkCarpark());
			SingleCarparkCarpark showWizard = (SingleCarparkCarpark) commonui.showWizard(w);
			if (!StrUtil.isEmpty(showWizard)) {
				SingleCarparkCarpark carpark = carparkModel.getCarpark();
				if (carpark != null) {
					showWizard.setParent(carpark);
				}
				carparkService.saveCarpark(showWizard);
			}
			List<SingleCarparkCarpark> findCarparkToLevel = carparkService.findCarparkToLevel();
			carparkModel.setListCarpark(findCarparkToLevel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addCarparkUser() {

		try {
			CarparkService carparkService = sp.getCarparkService();
			List<SingleCarparkCarpark> list = carparkService.findAllCarpark();
			if (StrUtil.isEmpty(list)) {
				commonui.error("错误", "请先创建停车场！！");
				return;
			}
			AddUserModel addUserModel = new AddUserModel();
			addUserModel.setAllList(list);
			AddUserWizard addUserWizard = new AddUserWizard(addUserModel);
			AddUserModel m = (AddUserModel) commonui.showWizard(addUserWizard);
			if (m == null) {
				return;
			}
			
			SingleCarparkUser user = m.getSingleCarparkUser();
			user.setCreateDate(new Date());
			CarparkUserService carparkUserService = sp.getCarparkUserService();
			carparkUserService.saveUser(user);
			commonui.info("操作成功", "保存成功!");
			refreshUser();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void delCarparkUser() {
		List<SingleCarparkUser> selectList = userModel.getSelectList();
		if (StrUtil.isEmpty(selectList)) {
			return;
		}
		boolean confirm = commonui.confirm("删除提示", "是否确认删除选中的"+selectList.size()+"个固定用户");
		if (!confirm) {
			return;
		}
		CarparkUserService carparkUserService = sp.getCarparkUserService();
		for (SingleCarparkUser singleCarparkUser : selectList) {
			try {
				carparkUserService.deleteUser(singleCarparkUser);
			} catch (Exception e) {
				e.printStackTrace();
				commonui.error("操作终止", "删除用户" + singleCarparkUser.getName() + "的车牌" + singleCarparkUser.getPlateNo() + "失败");
				return;
			}
		}
		refreshUser();
	}
	
	public void editCarparkUser(){
		List<SingleCarparkUser> selectList = userModel.getSelectList();
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
			AddUserWizard addUserWizard = new AddUserWizard(addUserModel);
			AddUserModel m = (AddUserModel) commonui.showWizard(addUserWizard);
			if (m == null) {
				return;
			}
			
			SingleCarparkUser user = m.getSingleCarparkUser();
			user.setCreateDate(new Date());
			CarparkUserService carparkUserService = sp.getCarparkUserService();
			carparkUserService.saveUser(user);
			commonui.info("操作成功", "修改成功!");
			refreshUser();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public CarparkManageApp getView() {
		return view;
	}

	public void setView(CarparkManageApp view) {
		this.view = view;
	}

	public CarparkModel getCarparkModel() {
		return carparkModel;
	}

	public void setCarparkModel(CarparkModel carparkModel) {
		this.carparkModel = carparkModel;
	}

	public void init() {
		refreshCarpark();
		refreshUser();
		refreshSystemUser();
		refreshSearchInOut();
	}

	private void refreshUser() {
		CarparkUserService carparkUserService = sp.getCarparkUserService();
		List<SingleCarparkUser> findAll = carparkUserService.findAll();
		userModel.setAllList(findAll);
	}

	private void refreshCarpark() {
		CarparkService carparkService = sp.getCarparkService();
		List<SingleCarparkCarpark> list = carparkService.findCarparkToLevel();
		carparkModel.setListCarpark(list);
	}

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}
	/**
	 * 添加系统用户
	 */
	public void addSystemUser() {
		try {
			SystemUserModel s=new SystemUserModel();
			AddSystemUserWizard wizard=new AddSystemUserWizard(s);
			SystemUserModel m = (SystemUserModel) commonui.showWizard(wizard);
			if (m==null) {
				return;
			}
			if (!check(System.getProperty("userType"), m.getType())) {
				commonui.error("操作终止", "您没有权限添加"+m.getType()+"账号");
				return;
			}
			SingleCarparkSystemUser systemUser=new SingleCarparkSystemUser();
			systemUser.setCreateDate(new Date());
			systemUser.setPassword(m.getPwd());
			systemUser.setRemark(m.getRemark());
			systemUser.setType(m.getType());
			systemUser.setUserName(m.getUserName());
			SystemUserServiceI systemUserService = sp.getSystemUserService();
			systemUserService.saveSystemUser(systemUser);
			commonui.info("操作成功", "添加用户成功！");
		} catch (Exception e) {
			e.printStackTrace();
			commonui.info("操作失败", "添加用户失败！");
		}finally{
			refreshSystemUser();
		}
	}
	/**
	 * 删除系统用户
	 */
	public void deleteSystemUser(){
		List<SingleCarparkSystemUser> selectList = systemUserModel.getSelectList();
		if (StrUtil.isEmpty(selectList)) {
			return;
		}
		boolean confirm = commonui.confirm("删除提示", "是否删除选中的"+selectList.size()+"个系统用户");
		if (!confirm) {
			return;
		}
		String property = System.getProperty("userType");
		SystemUserServiceI systemUserService = sp.getSystemUserService();
		for (SingleCarparkSystemUser singleCarparkSystemUser : selectList) {
			boolean check = check(property, singleCarparkSystemUser.getType());
			if (!check) {
				commonui.error("操作终止", "您没有权限去删除系统用户："+singleCarparkSystemUser.getUserName());
				break;
			}
			try {
				systemUserService.removeSystemUser(singleCarparkSystemUser);
				commonui.info("操作成功", "删除系统用户成功");
			} catch (Exception e) {
				e.printStackTrace();
				commonui.error("操作失败", "删除系统用户"+singleCarparkSystemUser.getUserName()+"失败");
				break;
			}
		}
		refreshSystemUser();
	}
	/**
	 * 修改系统用户名密码
	 */
	public void editSystemUser(){
		List<SingleCarparkSystemUser> selectList = systemUserModel.getSelectList();
		if (StrUtil.isEmpty(selectList)) {
			return;
		}
		SingleCarparkSystemUser singleCarparkSystemUser = selectList.get(0);
		String property = System.getProperty("userType");
		boolean result= check(property,singleCarparkSystemUser.getType());
		if (!result) {
			commonui.error("操作终止", "您没有权限修改系统用户："+singleCarparkSystemUser.getUserName());
			return;
		}
		SystemUserModel model=new SystemUserModel();
		model.setUserName(singleCarparkSystemUser.getUserName());
		model.setRemark(singleCarparkSystemUser.getRemark());
		EditSystemUserWizard wizard=new EditSystemUserWizard(model);
		SystemUserModel m = (SystemUserModel) commonui.showWizard(wizard);
		if (m==null) {
			return;
		}
		singleCarparkSystemUser.setPassword(m.getPwd());
		singleCarparkSystemUser.setLastEditDate(new Date());
		singleCarparkSystemUser.setLastEditUser(System.getProperty("userName"));
		singleCarparkSystemUser.setRemark(m.getRemark());
		try {
			sp.getSystemUserService().saveSystemUser(singleCarparkSystemUser);
			commonui.info("提示", "修改成功！");
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("提示", "修改失败！");
		}
		
	}
	
	public void addMonthCharge(){
		try {
			SingleCarparkCarpark carpark = carparkModel.getCarpark();
			if (carpark==null) {
				commonui.error("提示", "请先选择一个停车场");
				return;
			}
			AddMonthChargeWizard w=new AddMonthChargeWizard(AddMonthChargeModel.init());
			AddMonthChargeModel m = (AddMonthChargeModel) commonui.showWizard(w);
			if (m==null) {
				return;
			}
			SingleCarparkMonthlyCharge monthlyCharge=new SingleCarparkMonthlyCharge();
			monthlyCharge.setCarpark(carpark);
			monthlyCharge.setCarType(m.getCarType());
			monthlyCharge.setChargeCode(m.getChargeCode());
			monthlyCharge.setChargeName(m.getChargeName());
			monthlyCharge.setDelayDays(m.getDelayDays());
			monthlyCharge.setExpiringDays(m.getExpiringDays());
			monthlyCharge.setNote(m.getNote());
			monthlyCharge.setParkType(m.getParkType());
			monthlyCharge.setPrice(m.getPrice());
			monthlyCharge.setRentingDays(m.getRentingDays());
			sp.getCarparkService().saveMonthlyCharge(monthlyCharge);
			refreshCarparkCharge();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private boolean check(String loginType, String type) {
		if (loginType.equals("系统管理员")) {
			return true;
		}
		if (loginType.equals("普通管理员")) {
			if (type.equals("操作员")) {
				return true;
			}
		}
		return false;
	}

	public void refreshSystemUser(){
		SystemUserServiceI systemUserService = sp.getSystemUserService();
		List<SingleCarparkSystemUser> findAll = systemUserService.findAll();
		systemUserModel.setList(findAll);
	}
	public void refreshCarparkCharge(){
		SingleCarparkCarpark carpark = carparkModel.getCarpark();
		if (StrUtil.isEmpty(carpark)) {
			return;
		}
		List<SingleCarparkMonthlyCharge> listCharge=sp.getCarparkService().findMonthlyChargeByCarpark(carpark);
		List<CarparkChargeInfo> list=new ArrayList<>();
		for (SingleCarparkMonthlyCharge singleCarparkMonthlyCharge : listCharge) {
			CarparkChargeInfo cci=new CarparkChargeInfo();
			cci.setCode(singleCarparkMonthlyCharge.getChargeCode());
			cci.setName(singleCarparkMonthlyCharge.getChargeName());
			cci.setId(singleCarparkMonthlyCharge.getId());
			cci.setType("固定月租收费");
			list.add(cci);
		}
		carparkModel.setListCarparkCharge(list);
	}
	public SystemUserModel getSystemUserModel() {
		return systemUserModel;
	}

	public void setSystemUserModel(SystemUserModel systemUserModel) {
		this.systemUserModel = systemUserModel;
	}
	/**
	 * 固定用户充值
	 */
	public void monthUserPay() {
		try {
			MonthlyUserPayModel model = new MonthlyUserPayModel();
			SingleCarparkUser singleCarparkUser = userModel.getSelectList().get(0);
			model.setUserName(singleCarparkUser.getName());
			model.setCreateTime(singleCarparkUser.getCreateDate());
			model.setPlateNO(singleCarparkUser.getPlateNo());
			model.setAllmonth(sp.getCarparkService().findAllMonthlyCharge());
			model.setOverdueTime(singleCarparkUser.getValidTo());
			MonthlyUserPayWizard wizard=new MonthlyUserPayWizard(model);
			MonthlyUserPayModel m = (MonthlyUserPayModel) commonui.showWizard(wizard);
			if (StrUtil.isEmpty(m)) {
				return;
			}
			singleCarparkUser.setValidTo(m.getOverdueTime());
			SingleCarparkMonthlyUserPayHistory singleCarparkMonthlyUserPayHistory = m.getSingleCarparkMonthlyUserPayHistory();
			sp.getCarparkUserService().saveUser(singleCarparkUser);
			sp.getCarparkService().saveMonthlyUserPayHistory(m.getSingleCarparkMonthlyUserPayHistory());
			refreshUser();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 删除收费设置
	 */
	public void deleteCarparkCharge() {
		CarparkChargeInfo carparkChargeInfo = carparkModel.getCarparkChargeInfo();
		if (StrUtil.isEmpty(carparkChargeInfo)) {
			commonui.info("", "请先选择一个收费设置");
			return;
		}
		boolean confirm = commonui.confirm("删除确认", "是否删除编号为["+carparkChargeInfo.getCode()+"]名称为["+carparkChargeInfo.getName()+"]的收费设置");
		if (!confirm) {
			return;
		}
		if (carparkChargeInfo.getType().equals("固定月租收费")) {
			sp.getCarparkService().deleteMonthlyCharge(carparkChargeInfo.getId());
		}
		refreshCarparkCharge();
	}

	public InOutHistoryModel getInOutHistoryModel() {
		return inOutHistoryModel;
	}

	public void setInOutHistoryModel(InOutHistoryModel inOutHistoryModel) {
		this.inOutHistoryModel = inOutHistoryModel;
	}

	public void search(String plateNo, String userName, Date start, Date end, String operaName, String carType, String inout) {
		CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
		List<SingleCarparkInOutHistory> findByCondition = carparkInOutService.findByCondition(inOutHistoryModel.getListSearch().size(), 50, plateNo, userName, carType, inout, start, end, operaName);
		Long countByCondition = carparkInOutService.countByCondition(plateNo, userName, carType, inout, start, end, operaName);
		inOutHistoryModel.addListSearch(findByCondition);
		inOutHistoryModel.setCountSearch(inOutHistoryModel.getListSearch().size());
		inOutHistoryModel.setCountSearchAll(countByCondition.intValue());
		
	}
	public void refreshSearchInOut(){
		inOutHistoryModel.setListSearch(new ArrayList<>());
		search(null, null, null, null, null, null, null);
	}
}
