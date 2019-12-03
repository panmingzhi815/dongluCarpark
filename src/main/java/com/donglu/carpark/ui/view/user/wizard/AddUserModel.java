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
		copy(user);
		return user;
	}
	public void setSingleCarparkUser(SingleCarparkUser user){
		user.copy(this);
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
