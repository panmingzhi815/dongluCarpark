package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.dongluhitec.card.domain.db.DomainObject;

@Entity
public class YellowUser extends DomainObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2333339737278692405L;
	
	public enum Property{
		plateNo,createTime
	}
	
	@Column(length=30)
	private String plateNo;
	private String reason;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;
	
	@PrePersist
	public void updateCreateTime(){
		createTime=new Date();
	}
	
	public String getPlateNo() {
		return plateNo;
	}
	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
		firePropertyChange("plateNo", null, null);
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
		firePropertyChange("reason", null, null);
	}
	
}
