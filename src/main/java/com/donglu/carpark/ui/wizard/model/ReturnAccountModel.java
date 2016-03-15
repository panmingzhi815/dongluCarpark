package com.donglu.carpark.ui.wizard.model;

import java.util.ArrayList;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;

public class ReturnAccountModel extends SingleCarparkReturnAccount {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3230318779790754123L;
	private String pwd;
	private boolean free=false;
	private List<SingleCarparkSystemUser> listSystemUser=new ArrayList<>();
	private SingleCarparkSystemUser operaUser;
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

	public List<SingleCarparkSystemUser> getListSystemUser() {
		return listSystemUser;
	}

	public void setListSystemUser(List<SingleCarparkSystemUser> listSystemUser) {
		this.listSystemUser = listSystemUser;
		firePropertyChange("listSystemUser", null, null);
	}

	public SingleCarparkSystemUser getOperaUser() {
		return operaUser;
	}

	public void setOperaUser(SingleCarparkSystemUser operaUser) {
		this.operaUser = operaUser;
		firePropertyChange("operaUser", null, null);
	}
}
