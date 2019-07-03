package com.donglu.carpark.ui.view.user.wizard;

import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.google.common.collect.Lists;

public class AddUserModel extends SingleCarparkUser {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6263970320603664640L;

	private List<SingleCarparkCarpark> allList=Lists.newArrayList();
	
	private MonthlyUserPayModel model;
	private int totalSlot=0;
	private boolean free;
	private UserType userType=UserType.普通;

	public List<SingleCarparkCarpark> getAllList() {
		return allList;
	}

	public void setAllList(List<SingleCarparkCarpark> allList) {
		this.allList = allList;
		if (pcs != null)
			pcs.firePropertyChange("allList", null, null);
	}


	public SingleCarparkUser getSingleCarparkUser() {
		SingleCarparkUser user = new SingleCarparkUser();
		user.setAddress(getAddress());
		user.setCarpark(getCarpark());
		user.setCarparkNo(getCarparkNo());
		user.setName(getName());
		user.setPlateNo(getPlateNo());
		user.setType(userType.toString());
		user.setCreateDate(getCreateDate());
		user.setId(getId());
		user.setValidTo(getValidTo());
		user.setRemark(getRemark());
		user.setDelayDays(getDelayDays());
		user.setRemindDays(getRemindDays());
		user.setLeftMoney(getLeftMoney());
		user.setCarType(getCarType());
		user.setTelephone(getTelephone());
		user.setParkingSpace(getParkingSpace());
		user.setMonthChargeId(getMonthChargeId());
		user.setMonthChargeCode(getMonthChargeCode());
		user.setMonthChargeName(getMonthChargeName());
		user.setCarparkSlotType(getCarparkSlotType());
		user.setIdCard(getIdCard());
		return user;
	}
	public void setSingleCarparkUser(SingleCarparkUser user){
		setAddress(user.getAddress());
		setCarpark(user.getCarpark());
		setCarparkNo(user.getCarparkNo());
		setName(user.getName());
		setPlateNo(user.getPlateNo());
		setUserType(UserType.valueOf(user.getType()));
		setType(user.getType());
		setCreateDate(user.getCreateDate());
		setId(user.getId());
		setValidTo(user.getValidTo());
		setRemark(user.getRemark());
		setLeftMoney(user.getLeftMoney());
		setCarType(user.getCarType());
		setTelephone(user.getTelephone());
		setParkingSpace(user.getParkingSpace());
		setMonthChargeId(user.getMonthChargeId());
		setMonthChargeCode(user.getMonthChargeCode());
		setMonthChargeName(user.getMonthChargeName());
		setCarparkSlotType(user.getCarparkSlotType());
		setIdCard(user.getIdCard());
	}

	public MonthlyUserPayModel getModel() {
		return model;
	}

	public void setModel(MonthlyUserPayModel model) {
		this.model = model;
		if (pcs != null)
			pcs.firePropertyChange("model", null, null);
	}

	public int getTotalSlot() {
		return totalSlot;
	}

	public void setTotalSlot(int totalSlot) {
		this.totalSlot = totalSlot;
		if (pcs != null)
			pcs.firePropertyChange("totalSlot", null, null);
	}

	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
		if (pcs != null)
			pcs.firePropertyChange("free", null, null);
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType=userType;
		firePropertyChange("userType", null, null);
	}
}
