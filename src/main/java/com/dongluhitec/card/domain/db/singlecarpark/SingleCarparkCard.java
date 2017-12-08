package com.dongluhitec.card.domain.db.singlecarpark;

import javax.persistence.Entity;

import com.dongluhitec.card.domain.db.DomainObject;

@Entity
public class SingleCarparkCard extends DomainObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2463407478078817514L;
	
	public enum Property{
		identifier,serialNumber
	}
	
	private String identifier="卡片00001";
	private String serialNumber;
	
	public SingleCarparkCard(){}
	
	public SingleCarparkCard(String identifier, String serialNumber) {
		this.identifier = identifier;
		this.serialNumber = serialNumber;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
		firePropertyChange("identifier", null, null);
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
		firePropertyChange("serialNumber", null, null);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj!=null&&obj.getClass().equals(getClass())){
			SingleCarparkCard cast = getClass().cast(obj);
			return cast.getIdentifier().equals(identifier)||cast.getSerialNumber().equals(serialNumber);
		}
		return super.equals(obj);
	}
	
	@Override
	public SingleCarparkCard clone() throws CloneNotSupportedException {
		SingleCarparkCard card = new SingleCarparkCard(identifier, serialNumber);
		card.setId(id);
		return card;
	}
}
