package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.dongluhitec.card.domain.db.DomainObject;
@Entity
public class CarparkStillTime extends DomainObject {
	
	public enum Property{
		carparkName,plateNO,inBigImg,outBigImg,carparkId,inTime,inDevice,outDevice,outTime,stillSecond;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6819201932242701655L;
	@Column(length=40)
	private String carparkName;
	@Column(length=20)
	private String plateNO;
	@Column(length=120)
	private String inBigImg;
	@Column(length=120)
	private String outBigImg;
	private Long carparkId;
	private Date inTime;
	@Column(length=40)
	private String inDevice;
	@Column(length=40)
	private String outDevice;
	private Date outTime;
	private int stillSecond;
	
	public String getCarparkName() {
		return carparkName;
	}
	public void setCarparkName(String carparkName) {
		this.carparkName = carparkName;
		if (pcs != null)
			pcs.firePropertyChange("carparkName", null, null);
	}
	public Long getCarparkId() {
		return carparkId;
	}
	public void setCarparkId(Long carparkId) {
		this.carparkId = carparkId;
		if (pcs != null)
			pcs.firePropertyChange("carparkId", null, null);
	}
	public Date getInTime() {
		return inTime;
	}
	public void setInTime(Date inTime) {
		this.inTime = inTime;
		if (pcs != null)
			pcs.firePropertyChange("inTime", null, null);
	}
	public String getInDevice() {
		return inDevice;
	}
	public void setInDevice(String inDevice) {
		this.inDevice = inDevice;
		if (pcs != null)
			pcs.firePropertyChange("inDevice", null, null);
	}
	public String getOutDevice() {
		return outDevice;
	}
	public void setOutDevice(String outDevice) {
		this.outDevice = outDevice;
		if (pcs != null)
			pcs.firePropertyChange("outDevice", null, null);
	}
	public Date getOutTime() {
		return outTime;
	}
	public void setOutTime(Date outTime) {
		this.outTime = outTime;
		if (pcs != null)
			pcs.firePropertyChange("outTime", null, null);
	}
	public int getStillSecond() {
		return stillSecond;
	}
	public void setStillSecond(int stillSecond) {
		this.stillSecond = stillSecond;
		if (pcs != null)
			pcs.firePropertyChange("stillSecond", null, null);
	}
	public String getPlateNO() {
		return plateNO;
	}
	public void setPlateNO(String plateNO) {
		this.plateNO = plateNO;
		if (pcs != null)
			pcs.firePropertyChange("plateNO", null, null);
	}
	public String getInBigImg() {
		return inBigImg;
	}
	public void setInBigImg(String inBigImg) {
		this.inBigImg = inBigImg;
		if (pcs != null)
			pcs.firePropertyChange("inBigImg", null, null);
	}
	public String getOutBigImg() {
		return outBigImg;
	}
	public void setOutBigImg(String outBigImg) {
		this.outBigImg = outBigImg;
		if (pcs != null)
			pcs.firePropertyChange("outBigImg", null, null);
	}

}
