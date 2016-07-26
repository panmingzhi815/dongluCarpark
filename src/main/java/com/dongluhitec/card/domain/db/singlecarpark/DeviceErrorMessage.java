package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Entity;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;

@Entity
public class DeviceErrorMessage extends DomainObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2323474368347838797L;
	public enum Property{
		deviceName,errorMsg,checkDate,ip,controlIp,nomalTime
	}
	public enum Label{
		checkDateLabel,nomalTimeLabel
	}
	private String deviceName;
	private String errorMsg;
	private String ip;
	private String controlIp;
	private Date checkDate;
	private Date nomalTime;
	
	public String getCheckDateLabel(){
		return StrUtil.formatDateTime(checkDate);
	}
	public String getNomalTimeLabel(){
		return StrUtil.formatDateTime(nomalTime);
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
		firePropertyChange("deviceName", null, null);
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
		firePropertyChange("errorMsg", null, null);
	}
	public Date getCheckDate() {
		return checkDate;
	}
	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
		firePropertyChange("checkDate", null, null);
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
		firePropertyChange("ip", null, null);
	}
	public String getControlIp() {
		return controlIp;
	}
	public void setControlIp(String controlIp) {
		this.controlIp = controlIp;
		firePropertyChange("controlIp", null, null);
	}
	public Date getNomalTime() {
		return nomalTime;
	}
	public void setNomalTime(Date nomalTime) {
		this.nomalTime = nomalTime;
		firePropertyChange("nomalTime", null, null);
	}
	
}
