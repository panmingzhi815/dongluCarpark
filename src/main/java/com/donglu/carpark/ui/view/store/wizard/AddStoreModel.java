package com.donglu.carpark.ui.view.store.wizard;

import java.util.ArrayList;
import java.util.List;



import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStore;

public class AddStoreModel extends SingleCarparkStore {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2297903546044351259L;
	private String rePawword;
	private List<SingleCarparkCarpark> listCarpark=new ArrayList<>();

	public SingleCarparkStore getStore() {
		SingleCarparkStore sc=new SingleCarparkStore();
		sc.setId(getId());
		sc.setAddress(getAddress());
		sc.setCanAllFree(getCanAllFree());
		sc.setLeftFreeHour(getLeftFreeHour());
		sc.setLeftFreeMoney(getLeftFreeMoney());
		sc.setLoginName(getLoginName());
		sc.setLoginPawword(getLoginPawword());
		sc.setStoreName(getStoreName());
		sc.setUserName(getUserName());
		sc.setCarpark(getCarpark());
		sc.setCreateTime(getCreateTime());
		return sc;
	}
	public void setInfo(SingleCarparkStore sc){
		setId(sc.getId());
		setAddress(sc.getAddress());
		setCanAllFree(sc.getCanAllFree());
		setLeftFreeHour(sc.getLeftFreeHour());
		setLeftFreeMoney(sc.getLeftFreeMoney());
		setLoginName(sc.getLoginName());
		setLoginPawword(sc.getLoginPawword());
		setStoreName(sc.getStoreName());
		setUserName(sc.getUserName());
		setCarpark(sc.getCarpark());
		setCreateTime(sc.getCreateTime());
	}
	public String getRePawword() {
		return rePawword;
	}
	public void setRePawword(String rePawword) {
		this.rePawword = rePawword;
		if (pcs != null)
			pcs.firePropertyChange("rePawword", null, null);
	}
	public List<SingleCarparkCarpark> getListCarpark() {
		return listCarpark;
	}
	public void setListCarpark(List<SingleCarparkCarpark> listCarpark) {
		this.listCarpark = listCarpark;
		if (pcs != null)
			pcs.firePropertyChange("listCarpark", null, null);
	}
}
