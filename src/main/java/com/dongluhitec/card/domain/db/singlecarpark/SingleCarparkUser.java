package com.dongluhitec.card.domain.db.singlecarpark;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import com.dongluhitec.card.domain.db.DomainObject;

@Entity
public class SingleCarparkUser extends DomainObject {
	private String name;
	private String plateNo;
	private String type;
	private String address;
	private String carparkNo;
	@OneToOne
	private SingleCarparkCarpark carpark;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		if (pcs != null)
			pcs.firePropertyChange("name", null, null);
	}
	public String getPlateNo() {
		return plateNo;
	}
	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
		if (pcs != null)
			pcs.firePropertyChange("plateNo", null, null);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
		if (pcs != null)
			pcs.firePropertyChange("type", null, null);
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
		if (pcs != null)
			pcs.firePropertyChange("address", null, null);
	}
	public String getCarparkNo() {
		return carparkNo;
	}
	public void setCarparkNo(String carparkNo) {
		this.carparkNo = carparkNo;
		if (pcs != null)
			pcs.firePropertyChange("carparkNo", null, null);
	}
	public SingleCarparkCarpark getCarpark() {
		return carpark;
	}
	public void setCarpark(SingleCarparkCarpark carpark) {
		this.carpark = carpark;
		if (pcs != null)
			pcs.firePropertyChange("carpark", null, null);
	}
}
