package com.donglu.carpark;

import java.util.Date;
import java.util.List;

import com.donglu.carpark.model.UserModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkService;
import com.donglu.carpark.service.CarparkUserService;
import com.donglu.carpark.wizard.AddCarparkWizard;
import com.donglu.carpark.wizard.AddDeviceModel;
import com.donglu.carpark.wizard.AddUserModel;
import com.donglu.carpark.wizard.AddUserWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class CarparkManagePresenter {
	//停车场管理界面
	private CarparkManageApp view;
	
	private CarparkModel carparkModel;//停车场列表信息
	
	private UserModel userModel;//固定用户信息
	@Inject
	private CarparkDatabaseServiceProvider sp;
	
	@Inject
	CommonUIFacility commonui;
	
	/**
	 * 删除停车场
	 */
	public void deleteCarpark(){
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
			commonui.error("提示", "删除失败！"+e.getMessage());
		}
	}
	/**
	 * 添加停车场
	 */
	public void addCarpark(){
		try {
			CarparkService carparkService = sp.getCarparkService();
			AddCarparkWizard w=new AddCarparkWizard(new SingleCarparkCarpark());
			SingleCarparkCarpark showWizard = (SingleCarparkCarpark) commonui.showWizard(w);
			if (!StrUtil.isEmpty(showWizard)) {
				SingleCarparkCarpark carpark = carparkModel.getCarpark();
				if (carpark!=null) {
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
	
	public void addCarparkUser(){
		
		try {
			CarparkService carparkService = sp.getCarparkService();
			List<SingleCarparkCarpark> list = carparkService.findAll();
			if (StrUtil.isEmpty(list)) {
				commonui.error("错误", "请先创建停车场！！");
				return;
			}
			AddUserModel addUserModel = new AddUserModel();
			addUserModel.setAllList(list);
			AddUserWizard addUserWizard=new AddUserWizard(addUserModel);
			AddUserModel m = (AddUserModel) commonui.showWizard(addUserWizard);
			if (m==null) {
				return;
			}
			SingleCarparkUser user=new SingleCarparkUser();
			user.setAddress(m.getAddress());
			user.setCarpark(m.getCarpark());
			user.setCarparkNo(m.getCarparkNo());
			user.setName(m.getName());
			user.setPlateNo(m.getPlateNo());
			user.setType(m.getType());
			user.setCreateDate(new Date());
			CarparkUserService carparkUserService = sp.getCarparkUserService();
			carparkUserService.saveUser(user);
			commonui.info("操作成功", "保存成功!");
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
	
}
