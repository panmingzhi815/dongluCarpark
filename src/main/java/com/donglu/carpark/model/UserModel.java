package com.donglu.carpark.model;

import java.util.ArrayList;
import java.util.List;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;

public class UserModel extends DomainObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1363726723176262492L;
	List<SingleCarparkUser> allList=new ArrayList<>();
	List<SingleCarparkUser> selectList=new ArrayList<>();
	
	public List<SingleCarparkUser> getAllList() {
		return allList;
	}
	public void setAllList(List<SingleCarparkUser> allList) {
		this.allList = allList;
		if (pcs != null)
			pcs.firePropertyChange("allList", null, null);
	}
	public List<SingleCarparkUser> getSelectList() {
		return selectList;
	}
	public void setSelectList(List<SingleCarparkUser> selectList) {
		this.selectList = selectList;
		if (pcs != null)
			pcs.firePropertyChange("selectList", null, null);
	}
	
}
