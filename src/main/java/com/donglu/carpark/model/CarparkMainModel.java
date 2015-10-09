package com.donglu.carpark.model;

import java.util.Date;

import com.dongluhitec.card.domain.db.DomainObject;

public class CarparkMainModel extends DomainObject{
	String userName;
	Date workTime;
	int totalSlot;
	int hoursSlot;
	int monthSlot;
	float totalCharge;
	float totalFree;
	
	
	String plateNo;
	String carUser;
	String carType;
	Date inTime;
	Date outTime;
	String totalTime;
	float shouldMony;
	float real;
	
	boolean btnClick=false;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
		if (pcs != null)
			pcs.firePropertyChange("userName", null, null);
	}
	public Date getWorkTime() {
		return workTime;
	}
	public void setWorkTime(Date workTime) {
		this.workTime = workTime;
		if (pcs != null)
			pcs.firePropertyChange("workTime", null, null);
	}
	public String getPlateNo() {
		return plateNo;
	}
	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
		if (pcs != null)
			pcs.firePropertyChange("plateNo", null, null);
	}
	public String getCarUser() {
		return carUser;
	}
	public void setCarUser(String carUser) {
		this.carUser = carUser;
		if (pcs != null)
			pcs.firePropertyChange("carUser", null, null);
	}
	public String getCarType() {
		return carType;
	}
	public void setCarType(String carType) {
		this.carType = carType;
		if (pcs != null)
			pcs.firePropertyChange("carType", null, null);
	}
	public Date getInTime() {
		return inTime;
	}
	public void setInTime(Date inTime) {
		this.inTime = inTime;
		if (pcs != null)
			pcs.firePropertyChange("inTime", null, null);
	}
	public Date getOutTime() {
		return outTime;
	}
	public void setOutTime(Date outTime) {
		this.outTime = outTime;
		if (pcs != null)
			pcs.firePropertyChange("outTime", null, null);
	}
	public String getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
		if (pcs != null)
			pcs.firePropertyChange("totalTime", null, null);
	}
	public float getShouldMony() {
		return shouldMony;
	}
	public void setShouldMony(float shouldMony) {
		this.shouldMony = shouldMony;
		if (pcs != null)
			pcs.firePropertyChange("shouldMony", null, null);
	}
	public float getReal() {
		return real;
	}
	public void setReal(float real) {
		this.real = real;
		if (pcs != null)
			pcs.firePropertyChange("real", null, null);
	}
	public int getTotalSlot() {
		return totalSlot;
	}
	public void setTotalSlot(int totalSlot) {
		this.totalSlot = totalSlot;
		if (pcs != null)
			pcs.firePropertyChange("totalSlot", null, null);
	}
	public int getHoursSlot() {
		return hoursSlot;
	}
	public void setHoursSlot(int hoursSlot) {
		this.hoursSlot = hoursSlot;
		if (pcs != null)
			pcs.firePropertyChange("hoursSlot", null, null);
	}
	public int getMonthSlot() {
		return monthSlot;
	}
	public void setMonthSlot(int monthSlot) {
		this.monthSlot = monthSlot;
		if (pcs != null)
			pcs.firePropertyChange("monthSlot", null, null);
	}
	public float getTotalCharge() {
		return totalCharge;
	}
	public void setTotalCharge(float totalCharge) {
		this.totalCharge = totalCharge;
		if (pcs != null)
			pcs.firePropertyChange("totalCharge", null, null);
	}
	public float getTotalFree() {
		return totalFree;
	}
	public void setTotalFree(float totalFree) {
		this.totalFree = totalFree;
		if (pcs != null)
			pcs.firePropertyChange("totalFree", null, null);
	}
	public boolean isBtnClick() {
		return btnClick;
	}
	public void setBtnClick(boolean btnClick) {
		this.btnClick = btnClick;
		if (pcs != null)
			pcs.firePropertyChange("btnClick", null, null);
	}
	
}
