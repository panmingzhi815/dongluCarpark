package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import com.dongluhitec.card.domain.db.DomainObject;

public class SingleCarparkReturnAccount extends DomainObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3639922435037701579L;
	
	private String returnUser;
	private float factReturn;
	private float shouldReturn;
	private Date returnTime;
	private String operaName;
	public String getReturnUser() {
		return returnUser;
	}
	public void setReturnUser(String returnUser) {
		this.returnUser = returnUser;
		if (pcs != null)
			pcs.firePropertyChange("returnUser", null, null);
	}
	public float getFactReturn() {
		return factReturn;
	}
	public void setFactReturn(float factReturn) {
		this.factReturn = factReturn;
		if (pcs != null)
			pcs.firePropertyChange("factReturn", null, null);
	}
	public float getShouldReturn() {
		return shouldReturn;
	}
	public void setShouldReturn(float shouldReturn) {
		this.shouldReturn = shouldReturn;
		if (pcs != null)
			pcs.firePropertyChange("shouldReturn", null, null);
	}
	public Date getReturnTime() {
		return returnTime;
	}
	public void setReturnTime(Date returnTime) {
		this.returnTime = returnTime;
		if (pcs != null)
			pcs.firePropertyChange("returnTime", null, null);
	}
	public String getOperaName() {
		return operaName;
	}
	public void setOperaName(String operaName) {
		this.operaName = operaName;
		if (pcs != null)
			pcs.firePropertyChange("operaName", null, null);
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
