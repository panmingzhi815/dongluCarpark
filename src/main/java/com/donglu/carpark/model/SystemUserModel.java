package com.donglu.carpark.model;

import java.util.ArrayList;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;

public class SystemUserModel extends SingleCarparkSystemUser {
	/**
	 * 
	 */
	private static final long serialVersionUID = 910508348133776532L;
	private String oldPwd;
	private String pwd;
	private String rePwd;
	
	private List<SingleCarparkSystemUser> list=new ArrayList<>();
	private List<SingleCarparkSystemUser> selectList=new ArrayList<>();
	
	
	public List<SingleCarparkSystemUser> getList() {
		return list;
	}

	public void setList(List<SingleCarparkSystemUser> list) {
		this.list = list;
		if (pcs != null)
			pcs.firePropertyChange("list", null, null);
	}

	public List<SingleCarparkSystemUser> getSelectList() {
		return selectList;
	}

	public void setSelectList(List<SingleCarparkSystemUser> selectList) {
		this.selectList = selectList;
		if (pcs != null)
			pcs.firePropertyChange("selectList", null, null);
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
		if (pcs != null)
			pcs.firePropertyChange("pwd", null, null);
	}

	public String getRePwd() {
		return rePwd;
	}

	public void setRePwd(String rePwd) {
		this.rePwd = rePwd;
		if (pcs != null)
			pcs.firePropertyChange("rePwd", null, null);
	}

	public String getOldPwd() {
		return oldPwd;
	}

	public void setOldPwd(String oldPwd) {
		this.oldPwd = oldPwd;
		firePropertyChange("oldPwd", null, null);
	}
	
}
