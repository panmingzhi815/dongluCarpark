package com.donglu.carpark.ui.wizard;

import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.google.common.collect.Lists;

public class AddUserModel extends SingleCarparkUser {
	
	private List<SingleCarparkCarpark> allList=Lists.newArrayList();

	public List<SingleCarparkCarpark> getAllList() {
		return allList;
	}

	public void setAllList(List<SingleCarparkCarpark> allList) {
		this.allList = allList;
		if (pcs != null)
			pcs.firePropertyChange("allList", null, null);
	}

	public void copyToUser(SingleCarparkUser user) {
		user.setCarpark(getCarpark());
		user.setName(getName());
		user.setAddress(getAddress());
		user.setCarparkNo(getCarparkNo());
		user.setPlateNo(getPlateNo());
		user.setType(getType());
	}

	public SingleCarparkUser getSingleCarparkUser() {
		SingleCarparkUser user = new SingleCarparkUser();
		user.setAddress(getAddress());
		user.setCarpark(getCarpark());
		user.setCarparkNo(getCarparkNo());
		user.setName(getName());
		user.setPlateNo(getPlateNo());
		user.setType(getType());
		user.setCreateDate(getCreateDate());
		user.setId(getId());
		user.setValidTo(getValidTo());
		user.setRemark(getRemark());
		return user;
	}
	public void setSingleCarparkUser(SingleCarparkUser user){
		setAddress(user.getAddress());
		setCarpark(user.getCarpark());
		setCarparkNo(user.getCarparkNo());
		setName(user.getName());
		setPlateNo(user.getPlateNo());
		setType(user.getType());
		setCreateDate(user.getCreateDate());
		setId(user.getId());
		setValidTo(user.getValidTo());
		setRemark(user.getRemark());
	}
}
