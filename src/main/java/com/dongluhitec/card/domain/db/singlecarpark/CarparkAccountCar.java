package com.dongluhitec.card.domain.db.singlecarpark;

import javax.persistence.Entity;

import com.dongluhitec.card.domain.db.DomainObject;

@Entity
public class CarparkAccountCar extends DomainObject {
	
	public enum Preperty{
		plateNo,name
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4178933277926388171L;
	private String plateNo;
	private String name;
	
	public String getPlateNo() {
		return plateNo;
	}
	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
		firePropertyChange("plateNo", null, null);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		firePropertyChange("name", null, null);
	}
	@Override
	public String toString() {
		return "车牌:"+plateNo+",名称:"+name;
	}
}
