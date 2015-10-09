package com.dongluhitec.card.domain.db.singlecarpark;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.dongluhitec.card.domain.db.DomainObject;
@Entity
public class SingleCarparkDevice extends DomainObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6409867850987940644L;
	
	private String identifire;//编号
	private String name;//名字
	private String ip="192.168.1.1";//车牌识别ip
	private String inType;//出入口类型
	private String type;//设备类型  tcp or 485
	private String linkAddress;//连接地址
	private String address;//设备地址
	private String roadType;//通道类型
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "carparkId", nullable = true, insertable = false, updatable = false)
	private SingleCarparkCarpark carpark; //所属停车场
	
	@Column(name = "carparkId", nullable = true)
	private Long carparkId;
	
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
	public Long getCarparkId() {
		return carparkId;
	}
	public void setCarparkId(Long carparkId) {
		this.carparkId = carparkId;
		if (pcs != null)
			pcs.firePropertyChange("carparkId", null, null);
	}
	
	
	
}
