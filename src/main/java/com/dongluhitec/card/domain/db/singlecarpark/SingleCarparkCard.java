package com.dongluhitec.card.domain.db.singlecarpark;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.dongluhitec.card.domain.db.DomainObject;

@Entity
public class SingleCarparkCard extends DomainObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2463407478078817514L;
	public enum Property{
		serialNumber,user
	}
	@Column(unique=true)
	private String serialNumber;
	@ManyToOne
	private SingleCarparkUser user;
	
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
		firePropertyChange("serialNumber", null, null);
	}
	public SingleCarparkUser getUser() {
		return user;
	}
	public void setUser(SingleCarparkUser user) {
		this.user = user;
		firePropertyChange("user", null, null);
	}
	
	
}
