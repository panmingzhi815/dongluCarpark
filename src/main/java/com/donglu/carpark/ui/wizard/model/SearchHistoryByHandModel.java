package com.donglu.carpark.ui.wizard.model;

import com.dongluhitec.card.domain.db.DomainObject;

public class SearchHistoryByHandModel extends DomainObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6371093846867208227L;
	private String pwd;

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
		if (pcs != null)
			pcs.firePropertyChange("pwd", null, null);
	}
}
