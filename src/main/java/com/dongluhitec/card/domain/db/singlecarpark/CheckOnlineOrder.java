package com.dongluhitec.card.domain.db.singlecarpark;

import javax.persistence.Entity;

import com.dongluhitec.card.domain.db.DomainObject;

@Entity
public class CheckOnlineOrder extends DomainObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String time;
	private int status;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
