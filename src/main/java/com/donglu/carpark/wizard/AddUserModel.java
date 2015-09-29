package com.donglu.carpark.wizard;

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
}
