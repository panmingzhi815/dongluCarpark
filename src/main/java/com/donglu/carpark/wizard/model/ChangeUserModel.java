package com.donglu.carpark.wizard.model;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;

public class ChangeUserModel extends DomainObject{
	
	private String userName;
	private String pwd;
	private SingleCarparkSystemUser systemUser;
	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
		if (pcs != null)
			pcs.firePropertyChange("pwd", null, null);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
		if (pcs != null)
			pcs.firePropertyChange("userName", null, null);
	}

	public SingleCarparkSystemUser getSystemUser() {
		return systemUser;
	}

	public void setSystemUser(SingleCarparkSystemUser systemUser) {
		this.systemUser = systemUser;
		if (pcs != null)
			pcs.firePropertyChange("systemUser", null, null);
	}
}
