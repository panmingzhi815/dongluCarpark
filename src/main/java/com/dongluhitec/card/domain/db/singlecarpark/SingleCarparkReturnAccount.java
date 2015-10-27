package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Entity;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;
@Entity
public class SingleCarparkReturnAccount extends DomainObject {
	public enum Property{
		id,returnUser,factReturn,shouldReturn,returnTime,operaName,returnTimeLabel
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 3639922435037701579L;
	
	private String returnUser;
	private Float factReturn;
	private Float shouldReturn;
	private Date returnTime;
	private String operaName;
	
	public String getReturnTimeLabel(){
		return StrUtil.formatDate(returnTime, "yyyy-MM-dd HH:mm:ss");
	}
	public String getReturnUser() {
		return returnUser;
	}
	public void setReturnUser(String returnUser) {
		this.returnUser = returnUser;
		if (pcs != null)
			pcs.firePropertyChange("returnUser", null, null);
	}
	public Float getFactReturn() {
		return factReturn;
	}
	public void setFactReturn(float factReturn) {
		this.factReturn = factReturn;
		if (pcs != null)
			pcs.firePropertyChange("factReturn", null, null);
	}
	public Float getShouldReturn() {
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
	public void setFactReturn(Float factReturn) {
		this.factReturn = factReturn;
		if (pcs != null)
			pcs.firePropertyChange("factReturn", null, null);
	}
	public void setShouldReturn(Float shouldReturn) {
		this.shouldReturn = shouldReturn;
		if (pcs != null)
			pcs.firePropertyChange("shouldReturn", null, null);
	}
}
