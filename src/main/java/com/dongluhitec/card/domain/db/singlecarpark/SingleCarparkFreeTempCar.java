package com.dongluhitec.card.domain.db.singlecarpark;

import javax.persistence.Entity;

import com.dongluhitec.card.domain.db.DomainObject;
@Entity
public class SingleCarparkFreeTempCar extends DomainObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6129386181348414448L;
	
	public enum Property{
		plateNo,freeMinute,freeMoney,status
	}
	public enum Label{
		statusLabel
	}
	private String plateNo;
	private int freeMinute=0;
	private int freeMoney=0;
	private Boolean status=true;
	public String getPlateNo() {
		return plateNo;
	}
	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
		firePropertyChange("plateNo", null, null);
	}
	public int getFreeMinute() {
		return freeMinute;
	}
	public void setFreeMinute(int freeMinute) {
		this.freeMinute = freeMinute;
		firePropertyChange("freeMinute", null, null);
	}
	public int getFreeMoney() {
		return freeMoney;
	}
	public void setFreeMoney(int freeMoney) {
		this.freeMoney = freeMoney;
		firePropertyChange("freeMoney", null, null);
	}
	public Boolean getStatus() {
		if (status==null) {
			return true;
		}
		return status;
	}
	public String getStatusLabel(){
		if (status==null||status) {
			return "启用";
		}else{
			return "停用";
		}
	}
	public void setStatus(Boolean status) {
		this.status = status;
		firePropertyChange("status", null, null);
	}
} 
