package com.donglu.carpark.model;

import java.util.Date;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;

public class ConcentrateModel extends DomainObject {
	String userName;
	String workTime;
	Float totalFact;
	Float totalFree;
	
	String plateNO;
	private String inTime;
	String stillTime;
	Float shouldMoney;
	Float factMoney;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
		if (pcs != null)
			pcs.firePropertyChange("userName", null, null);
	}
	public String getWorkTime() {
		return workTime;
	}
	public void setWorkTime(String workTime) {
		this.workTime = workTime;
		if (pcs != null)
			pcs.firePropertyChange("workTime", null, null);
	}
	public Float getTotalFact() {
		return totalFact;
	}
	public void setTotalFact(Float totalFact) {
		this.totalFact = totalFact;
		if (pcs != null)
			pcs.firePropertyChange("totalFact", null, null);
	}
	public Float getTotalFree() {
		return totalFree;
	}
	public void setTotalFree(Float totalFree) {
		this.totalFree = totalFree;
		if (pcs != null)
			pcs.firePropertyChange("totalFree", null, null);
	}
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
	public void setInTime(Date inTime) {
		this.inTime = StrUtil.formatDate(inTime, StrUtil.DATETIME_PATTERN);
		if (pcs != null)
			pcs.firePropertyChange("inTime", null, null);
	}
	public String getStillTime() {
		return stillTime;
	}
	public void setStillTime(String stillTime) {
		this.stillTime = stillTime;
		if (pcs != null)
			pcs.firePropertyChange("stillTime", null, null);
	}
	public Float getShouldMoney() {
		return shouldMoney;
	}
	public void setShouldMoney(Float shouldMoney) {
		this.shouldMoney = shouldMoney;
		if (pcs != null)
			pcs.firePropertyChange("shouldMoney", null, null);
	}
	public Float getFactMoney() {
		return factMoney;
	}
	public void setFactMoney(Float factMoney) {
		this.factMoney = factMoney;
		if (pcs != null)
			pcs.firePropertyChange("factMoney", null, null);
	}
	
}
