package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Entity;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;
@Entity
public class CarparkOffLineHistory extends DomainObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4256889538356737554L;
	public enum Property{
		plateNO,inTime,deviceName,deviceIp
	}
	public enum Label{
		inTimeLabel
	}
	private String plateNO;
	private Date inTime;
	private String deviceName;
	private String deviceIp;
	private String bigImage;
	private String smallImage;
	
	public String getInTimeLabel(){
		return StrUtil.formatDateTime(inTime);
	}
	public String getPlateNO() {
		return plateNO;
	}
	public void setPlateNO(String plateNO) {
		this.plateNO = plateNO;
		firePropertyChange("plateNO", null, null);
	}
	public Date getInTime() {
		return inTime;
	}
	public void setInTime(Date inTime) {
		this.inTime = inTime;
		firePropertyChange("inTime", null, null);
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
		firePropertyChange("deviceName", null, null);
	}
	public String getDeviceIp() {
		return deviceIp;
	}
	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
		firePropertyChange("deviceIp", null, null);
	}
	public String getBigImage() {
		return bigImage;
	}
	public void setBigImage(String bigImage) {
		this.bigImage = bigImage;
		firePropertyChange("bigImage", null, null);
	}
	public String getSmallImage() {
		return smallImage;
	}
	public void setSmallImage(String smallImage) {
		this.smallImage = smallImage;
		firePropertyChange("smallImage", null, null);
	}
}
