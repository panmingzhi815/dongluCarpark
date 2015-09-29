package com.dongluhitec.card.domain.db.singlecarpark;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import com.dongluhitec.card.domain.db.DomainObject;
@Entity
public class SingleCarparkDevice extends DomainObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6409867850987940644L;
	
	private String identifire;
	private String name;
	private String ip;
	private String inType;
	private String type;
	private String linkAddress;
	private String address;
	private String roadType;
	@OneToOne
	private SingleCarparkCarpark carpark;
	
	public String getIdentifire() {
		return identifire;
	}
	public void setIdentifire(String identifire) {
		this.identifire = identifire;
		if (pcs != null)
			pcs.firePropertyChange("identifire", null, null);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		if (pcs != null)
			pcs.firePropertyChange("name", null, null);
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
		if (pcs != null)
			pcs.firePropertyChange("ip", null, null);
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
	public String getRoadType() {
		return roadType;
	}
	public void setRoadType(String roadType) {
		this.roadType = roadType;
		if (pcs != null)
			pcs.firePropertyChange("roadType", null, null);
	}
	public SingleCarparkCarpark getCarpark() {
		return carpark;
	}
	public void setCarpark(SingleCarparkCarpark carpark) {
		this.carpark = carpark;
		if (pcs != null)
			pcs.firePropertyChange("carpark", null, null);
	}
	public String getLinkAddress() {
		return linkAddress;
	}
	public void setLinkAddress(String linkAddress) {
		this.linkAddress = linkAddress;
		if (pcs != null)
			pcs.firePropertyChange("linkAddress", null, null);
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getInType() {
		return inType;
	}
	public void setInType(String inType) {
		this.inType = inType;
		if (pcs != null)
			pcs.firePropertyChange("inType", null, null);
	}
	
	
	
}
