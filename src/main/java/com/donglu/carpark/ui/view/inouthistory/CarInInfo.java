package com.donglu.carpark.ui.view.inouthistory;

import com.dongluhitec.card.domain.db.DomainObject;

public class CarInInfo extends DomainObject{
	public enum Property{
		plateNO,inTime,userName,userType,status
	}
	private String plateNO;
	private String inTime;
	private String userName;
	private String userType;
	private String status;
	
	public String getPlateNO() {
		return plateNO;
	}
	public void setPlateNO(String plateNO) {
		this.plateNO = plateNO;
		if (pcs != null)
			pcs.firePropertyChange("plateNO", null, null);
	}
	public String getInTime() {
		return inTime;
	}
	public void setInTime(String inTime) {
		this.inTime = inTime;
		if (pcs != null)
			pcs.firePropertyChange("inTime", null, null);
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
		if (pcs != null)
			pcs.firePropertyChange("userName", null, null);
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
		if (pcs != null)
			pcs.firePropertyChange("userType", null, null);
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
		if (pcs != null)
			pcs.firePropertyChange("status", null, null);
	}
	
}
