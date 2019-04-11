package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Entity;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;
@Entity
public class SingleCarparkOpenDoorLog extends DomainObject{
	
	public enum Property{
		operaName,operaDate,deviceName,image, operaDateLabel
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -3207836534894290785L;
	private String operaName;
	private Date operaDate;
	private String deviceName;
	private String deviceIdentifier;
	private String image;
	private String type="远程开闸";
	
	public String getOperaName() {
		return operaName;
	}
	public void setOperaName(String operaName) {
		this.operaName = operaName;
		if (pcs != null)
			pcs.firePropertyChange("operaName", null, null);
	}
	
	public String getOperaDateLabel() {
		return StrUtil.formatDate(operaDate, "yyyy-MM-dd HH:mm:ss");
	}
	public Date getOperaDate() {
		return operaDate;
	}
	public void setOperaDate(Date operaDate) {
		this.operaDate = operaDate;
		if (pcs != null)
			pcs.firePropertyChange("operaDate", null, null);
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
		if (pcs != null)
			pcs.firePropertyChange("deviceName", null, null);
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
		if (pcs != null)
			pcs.firePropertyChange("image", null, null);
	}
	public String getDeviceIdentifier() {
		return deviceIdentifier;
	}
	public void setDeviceIdentifier(String deviceIdentifier) {
		this.deviceIdentifier = deviceIdentifier;
		firePropertyChange("deviceIdentifier", null, null);
	}
	public String getType() {
		return type==null?"远程开闸":type;
	}
	public void setType(String type) {
		this.type = type;
		firePropertyChange("type", null, null);
	}
}
