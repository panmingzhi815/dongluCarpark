package com.donglu.carpark.ui.wizard.model;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;

public class ReturnAccountModel extends SingleCarparkReturnAccount {
	private String pwd;
	private boolean free=false;
	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
		if (pcs != null)
			pcs.firePropertyChange("pwd", null, null);
	}

	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
		if (pcs != null)
			pcs.firePropertyChange("free", null, null);
	}
}
