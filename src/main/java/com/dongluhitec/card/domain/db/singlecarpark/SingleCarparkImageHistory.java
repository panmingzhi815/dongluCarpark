package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Entity;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;

@Entity
public class SingleCarparkImageHistory extends DomainObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6818506711499205078L;
	public enum Property{
		plateNO,factPlateNO,bigImage,type,time,deviceName,deviceIp
	}
	public enum Label{
		timeLabel
	}
	
	private String plateNO;
	private String factPlateNO;
	private String bigImage;
	private String type;
	private Date time;
	private String smallImage;
	private String deviceIp;
	private String deviceName;
	
	public String getTimeLabel(){
		return StrUtil.formatDateTime(time);
	}
	
	public String getPlateNO() {
		return plateNO;
	}
	public void setPlateNO(String plateNO) {
		this.plateNO = plateNO;
		firePropertyChange("plateNO", null, null);
	}
	public String getBigImage() {
		return bigImage;
	}
	public void setBigImage(String bigImage) {
		this.bigImage = bigImage;
		firePropertyChange("bigImage", null, null);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
		firePropertyChange("type", null, null);
	}
	public String getFactPlateNO() {
		return factPlateNO;
	}
	public void setFactPlateNO(String factPlateNO) {
		this.factPlateNO = factPlateNO;
		firePropertyChange("factPlateNO", null, null);
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time=time;
		firePropertyChange("time", null, null);
	}

	public void setSmallImage(String smallImage) {
		this.smallImage=smallImage;
		firePropertyChange("smallImage", null, null);
	}

	public String getSmallImage() {
		return smallImage;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj!=null) {
			if (!obj.getClass().equals(getClass())) {
				return false;
			}
			SingleCarparkImageHistory cast = getClass().cast(obj);
			if (cast.getId()!=null&&getId()!=null) {
				return getId().equals(cast.getId());
			}
		}
		return super.equals(obj);
	}

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
		firePropertyChange("deviceIp", null, null);
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
		firePropertyChange("deviceName", null, null);
	}
}
