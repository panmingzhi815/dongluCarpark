package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.PrePersist;

import org.hibernate.annotations.Index;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;

@Entity
public class OverSpeedCar extends DomainObject {
	
	public enum Property{
		plate,time,currentSpeed,rateLimiting,place,camId,status,carType,processState
	}
	public enum Label{
		plate,timeLabel,currentSpeed,rateLimiting,place,camId,statusLabel,createTimeLabel
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Index(name = "OverSpeedCar_plate")
	private String plate;
	@Index(name = "OverSpeedCar_time")
	private Date time;
	private int currentSpeed;
	private int rateLimiting;
	private String image;
	private String image2;
	private int status;
	private String place;
	private String camId;
	@Index(name = "OverSpeedCar_carType")
	private String carType;
	private Date createTime;
	private String remark;
	
	private Integer processState=0;
	
	@PrePersist
	public void init() {
		createTime=new Date();
	}
	
	public String getTimeLabel() {
		return StrUtil.formatDateTime(time);
	}
	public String getCreateTimeLabel() {
		return StrUtil.formatDateTime(createTime);
	}
	public String getStatusLabel() {
		switch (status) {
		case 1:
			return "超速";
		case 2:
			return "严重超速";
		default:
			break;
		}
		return "正常";
	}
	
	public String getPlate() {
		return plate;
	}
	public void setPlate(String plate) {
		this.plate = plate;
		firePropertyChange("plate", null, null);
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
		firePropertyChange("image", null, null);
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
		firePropertyChange("time", null, null);
	}
	public String getImage2() {
		return image2;
	}
	public void setImage2(String image2) {
		this.image2 = image2;
		firePropertyChange("image2", null, null);
	}
	public int getCurrentSpeed() {
		return currentSpeed;
	}
	public void setCurrentSpeed(int currentSpeed) {
		this.currentSpeed = currentSpeed;
		firePropertyChange("currentSpeed", null, null);
	}
	public int getRateLimiting() {
		return rateLimiting;
	}
	public void setRateLimiting(int rateLimiting) {
		this.rateLimiting = rateLimiting;
		firePropertyChange("rateLimiting", null, null);
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
		firePropertyChange("status", null, null);
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
		firePropertyChange("place", null, null);
	}
	public String getCamId() {
		return camId;
	}
	public void setCamId(String camId) {
		this.camId = camId;
		firePropertyChange("camId", null, null);
	}
	public String getCarType() {
		return carType==null?"临时车":carType;
	}
	public void setCarType(String carType) {
		this.carType = carType;
		firePropertyChange("carType", null, null);
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
		firePropertyChange("remark", null, null);
	}

	public Integer getProcessState() {
		return processState;
	}

	public void setProcessState(Integer processState) {
		this.processState = processState;
		firePropertyChange("processState", null, null);
	}
}