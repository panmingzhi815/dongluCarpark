package com.dongluhitec.card.domain.db.singlecarpark;

import javax.persistence.Entity;

import com.dongluhitec.card.domain.db.DomainObject;

@Entity
public class CarparkPlateCarType extends DomainObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -904849382534461516L;
	
	public enum Property{
		plate,carType
	}
	
	private String plate;
	private String carType;
	private Long tid;
	
	public String getPlate() {
		return plate;
	}
	public void setPlate(String plate) {
		this.plate = plate;
		//firePropertyChange("plate", null, null);
	}
	public String getCarType() {
		return carType;
	}
	public void setCarType(String carType) {
		this.carType = carType;
		//firePropertyChange("carType", null, null);
	}
	public Long getTid() {
		return tid;
	}
	public void setTid(Long tid) {
		this.tid = tid;
		//firePropertyChange("tid", null, null);
	}
}
